/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.navigator.internal.listeners;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
import com.holonplatform.vaadin.flow.navigator.annotations.OnShow;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterUtils;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration.NavigationParameterDefinition;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration.ParameterContainerType;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.router.ListenerPriority;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.VaadinService;

/**
 * An {@link AfterNavigationListener} to process navigation targets, setting {@link QueryParameter}
 * values and firing {@link OnShow} annotated methods.
 * 
 * @since 5.2.0
 */
@ListenerPriority(Integer.MAX_VALUE)
public class DefaultNavigationTargetAfterNavigationListener extends AbstractNavigationTargetListener
		implements AfterNavigationListener {

	private static final long serialVersionUID = 3389997906125447359L;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.flow.router.internal.AfterNavigationHandler#afterNavigation(com.vaadin.flow.router.
	 * AfterNavigationEvent)
	 */
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		if (event.getActiveChain() != null && !event.getActiveChain().isEmpty()) {
			// current navigation target
			final HasElement navigationTarget = event.getActiveChain().get(0);
			if (navigationTarget != null) {
				LOGGER.debug(() -> "Process navigation target [" + navigationTarget.getClass().getName()
						+ "] after navigation");
				// configuration
				final NavigationTargetConfiguration configuration = getNavigationTargetConfigurationRegistry()
						.getConfiguration(navigationTarget.getClass(), VaadinService.getCurrent().getContext());
				// set query parameters
				setQueryParameterValues(navigationTarget, configuration, event.getLocation());
				// fire OnShow methods
				configuration.getOnShowMethods().forEach(method -> {
					LOGGER.debug(() -> "Invoke OnShow method [" + method.getName() + "] for navigation target ["
							+ navigationTarget.getClass().getName() + "]");
					invokeMethod(navigationTarget, method, event);
				});
			}
		}
	}

	/**
	 * Set the query parameter values for given navigation target instance.
	 * @param navigationTargetInstance the navigation target instance
	 * @param configuration the navigation target configuration
	 * @param location The current URL location
	 */
	private static void setQueryParameterValues(HasElement navigationTargetInstance,
			NavigationTargetConfiguration configuration, Location location) {
		// get and decode query paremeters
		final Map<String, List<String>> queryParameters = NavigationParameterUtils
				.decodeParameters(location.getQueryParameters().getParameters());
		configuration.getQueryParameters().forEach((name, definition) -> {
			// process parameter definition
			LOGGER.debug(() -> "Process query parameter [" + name + "] for navigation target ["
					+ navigationTargetInstance.getClass().getName() + "]");
			final List<String> queryParameterValues = queryParameters.get(name);
			final List<?> values = getParameterValue(definition,
					NavigationParameterMapper.get().deserialize(definition.getType(),
							(queryParameterValues != null) ? queryParameterValues : Collections.emptyList()));
			if (values.isEmpty()) {
				// check required
				if (definition.isRequired()) {
					throw new InvalidNavigationParameterException(
							"The value of the query parameter [" + name + "] is required");
				}
				// set as null
				setParameterValue(navigationTargetInstance, definition,
						getNullParameterValue(definition.getType(), definition.getParameterContainerType()));
			} else {
				// check container type
				switch (definition.getParameterContainerType()) {
				case OPTIONAL:
					setParameterValue(navigationTargetInstance, definition, Optional.ofNullable(values.get(0)));
					break;
				case LIST:
					setParameterValue(navigationTargetInstance, definition, values);
					break;
				case SET:
					setParameterValue(navigationTargetInstance, definition, new HashSet<>(values));
					break;
				case NONE:
				default:
					setParameterValue(navigationTargetInstance, definition, values.get(0));
					break;
				}
			}
		});
	}

	/**
	 * Get the parameter values, checking default value if given values null or is empty.
	 * @param definition The parameter definition
	 * @param values The parameter values
	 * @return the parameter values
	 */
	private static List<?> getParameterValue(NavigationParameterDefinition definition, List<?> values) {
		if (values == null || values.isEmpty()) {
			// check default
			return definition.getDefaultValue().map(defaultValue -> Collections.singletonList(defaultValue))
					.orElse(Collections.emptyList());
		}
		return values;
	}

	/**
	 * Set the parameter value which corresponds to given definition on the provided navigation target
	 * instance.
	 * @param navigationTarget The navigation target instance
	 * @param definition The parameter definition
	 * @param value The parameter value
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	private static void setParameterValue(Object navigationTarget, NavigationParameterDefinition definition,
			Object value) throws InvalidNavigationParameterException {
		if (definition.getWriteMethod().isPresent()) {
			// use write method
			try {
				definition.getWriteMethod().get().invoke(navigationTarget, new Object[] { value });
			} catch (Exception e) {
				throw new InvalidNavigationParameterException("Failed to set navigation parameter ["
						+ definition.toString() + "] value on navigation target ["
						+ navigationTarget.getClass().getName() + "] using value [" + value + "]", e);
			}
		} else {
			// use field
			try {
				FieldUtils.writeField(definition.getField(), navigationTarget, value, true);
			} catch (Exception e) {
				throw new InvalidNavigationParameterException("Failed to set navigation parameter ["
						+ definition.toString() + "] value on navigation target ["
						+ navigationTarget.getClass().getName() + "] using value [" + value + "]", e);
			}
		}
	}

	/**
	 * Get the null parameter value according to type, taking into account primitive types.
	 * @param type Parameter value type
	 * @param parameterContainerType Parameter container type
	 * @return the null parameter value
	 */
	private static Object getNullParameterValue(Class<?> type, ParameterContainerType parameterContainerType) {
		if (ParameterContainerType.OPTIONAL == parameterContainerType) {
			return Optional.empty();
		}
		if (ParameterContainerType.LIST == parameterContainerType) {
			return Collections.emptyList();
		}
		if (ParameterContainerType.SET == parameterContainerType) {
			return Collections.emptySet();
		}
		if (TypeUtils.isPrimitiveBoolean(type)) {
			return Boolean.FALSE;
		}
		if (TypeUtils.isPrimitiveInt(type)) {
			return 0;
		}
		if (TypeUtils.isPrimitiveLong(type)) {
			return 0L;
		}
		if (TypeUtils.isPrimitiveFloat(type)) {
			return 0f;
		}
		if (TypeUtils.isPrimitiveDouble(type)) {
			return 0d;
		}
		return null;
	}

}
