/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.flow.internal.components.events;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.ValueComponent;

/**
 * Default {@link ValidationStatusEvent} implementation.
 *
 * @param <S> Validation source
 * @param <V> Validation value type
 * @param <C> Value component to which the validation event refers
 *
 * @since 5.2.0
 */
public class DefaultValidationStatusEvent<S, V, C extends ValueComponent<V>> implements ValidationStatusEvent<S, V, C> {

	private static final long serialVersionUID = -2504760138806763843L;

	private final S source;
	private final C component;
	private final Property<V> property;
	private final Status status;
	private final List<Localizable> errors;

	public DefaultValidationStatusEvent(S source, Status status, List<Localizable> errors) {
		this(source, null, null, status, errors);
	}

	public DefaultValidationStatusEvent(S source, C component, Property<V> property, Status status,
			List<Localizable> errors) {
		super();
		ObjectUtils.argumentNotNull(source, "Source must be not null");
		ObjectUtils.argumentNotNull(status, "Status must be not null");
		this.source = source;
		this.component = component;
		this.property = property;
		this.status = status;
		this.errors = errors;

	}

	@Override
	public S getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent#getComponent()
	 */
	@Override
	public Optional<C> getComponent() {
		return Optional.ofNullable(component);
	}

	@Override
	public Optional<Property<V>> getProperty() {
		return Optional.ofNullable(property);
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public List<Localizable> getErrors() {
		return (errors != null) ? errors : Collections.emptyList();
	}

}
