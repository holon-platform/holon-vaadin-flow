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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.PropertyComponentSource;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator.BaseComponentConfigurator;
import com.vaadin.flow.component.Component;

/**
 * Base {@link Composable} property form component.
 * 
 * @param <C> Content component type
 * @param <PC> Property component type
 * @param <S> Components source type
 * 
 * @since 5.2.0
 */
public abstract class AbstractComposablePropertyForm<C extends Component, PC extends HasComponent & HasLabel, S extends PropertyComponentSource>
		extends AbstractComposable<C, S> {

	private static final long serialVersionUID = 7681948564727269874L;

	/**
	 * Custom property captions
	 */
	private final Map<Property<?>, Localizable> propertyCaptions = new HashMap<>(8);

	/**
	 * Hidden property captions
	 */
	private final Collection<Property<?>> hiddenPropertyCaptions = new HashSet<>(8);

	/**
	 * Component configurators
	 */
	private final Map<Property<?>, Consumer<BaseComponentConfigurator>> propertyComponentConfigurators = new HashMap<>(
			8);

	/**
	 * Constructor.
	 * @param content Form composition content (not null)
	 */
	public AbstractComposablePropertyForm(C content) {
		super(content);
	}

	/**
	 * Set the caption for the component bound to given property
	 * @param property Property
	 * @param caption Localizable caption
	 */
	protected void setPropertyCaption(Property<?> property, Localizable caption) {
		if (property != null && caption != null) {
			propertyCaptions.put(property, caption);
		}
	}

	/**
	 * Set the caption for the component bound to given property as hidden
	 * @param property Property
	 */
	protected void hidePropertyCaption(Property<?> property) {
		if (property != null) {
			hiddenPropertyCaptions.add(property);
		}
	}

	/**
	 * Set the {@link ComponentConfigurator} consumer associated to given {@link Property}.
	 * @param property Property (not null)
	 * @param configurator Component configurator (not null)
	 */
	public void setPropertyComponentConfigurator(Property<?> property,
			Consumer<BaseComponentConfigurator> configurator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(configurator, "ComponentConfigurator Consumer must be not null");
		propertyComponentConfigurators.put(property, configurator);
	}

	/**
	 * Get the {@link ComponentConfigurator} consumer associated to given {@link Property}, if available.
	 * @param property Property
	 * @return Optional component configurator
	 */
	protected Optional<Consumer<BaseComponentConfigurator>> getPropertyComponentConfigurator(Property<?> property) {
		return Optional.ofNullable(propertyComponentConfigurators.get(property));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractComposable#setupPropertyComponent(com.holonplatform.
	 * core.property.Property, com.vaadin.flow.component.Component, boolean)
	 */
	@Override
	protected void setupPropertyComponent(Property<?> property, Component component, boolean fullWidth) {
		super.setupPropertyComponent(property, component, fullWidth);
		// check configurator
		getPropertyComponentConfigurator(property).ifPresent(consumer -> {
			consumer.accept(BaseComponentConfigurator.create(component));
		});
	}

	/**
	 * Configure given component using given property.
	 * @param property Property to which the component refers
	 * @param component Component to configure
	 */
	protected void configurePropertyComponent(Property<?> property, PC component) {
		if (hiddenPropertyCaptions.contains(property)) {
			component.setLabel(null);
		} else {
			if (propertyCaptions.containsKey(property)) {
				component.setLabel(LocalizationContext.translate(propertyCaptions.get(property), true));
			} else {
				if (component.getLabel() == null) {
					// default behaviour
					if (property.getMessage() != null) {
						component.setLabel(LocalizationContext.translate(property, true));
					} else {
						component.setLabel(property.getName());
					}
				}
			}
		}
	}

}
