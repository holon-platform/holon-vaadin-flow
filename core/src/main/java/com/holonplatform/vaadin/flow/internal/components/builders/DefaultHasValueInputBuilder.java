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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.components.support.PropertyHandler;
import com.holonplatform.vaadin.flow.internal.components.HasValueInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * Default {@link HasValueInputBuilder} implementation.
 *
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultHasValueInputBuilder<T> extends AbstractComponentConfigurator<Component, HasValueInputBuilder<T>>
		implements HasValueInputBuilder<T> {

	private final HasValueInput<T> instance;

	/**
	 * Constructor using a {@link HasValue} and {@link Component} field instance.
	 * @param <E> ValueChangeEvent type
	 * @param <H> Actual HasValue component type
	 * @param field The {@link HasValue} field and component (not null)
	 */
	public <E extends HasValue.ValueChangeEvent<T>, H extends Component & HasValue<E, T>> DefaultHasValueInputBuilder(
			H field) {
		this(field, field);
	}

	/**
	 * Constructor using separate {@link HasValue} and {@link Component} field instances.
	 * @param <E> ValueChangeEvent type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 */
	public <E extends HasValue.ValueChangeEvent<T>> DefaultHasValueInputBuilder(HasValue<E, T> field,
			Component component) {
		super(component);
		this.instance = new HasValueInput<>(field, component);
	}

	/**
	 * Get the {@link HasValueInput} instance to build.
	 * @return the instance
	 */
	protected HasValueInput<T> getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected HasValueInputBuilder<T> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<T> build() {
		return getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> validatable() {
		return ValidatableInputBuilder.create(build());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public HasValueInputBuilder<T> readOnly(boolean readOnly) {
		getInstance().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public HasValueInputBuilder<T> withValue(T value) {
		getInstance().setValue(value);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public HasValueInputBuilder<T> withValueChangeListener(ValueChangeListener<T> listener) {
		getInstance().addValueChangeListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public HasValueInputBuilder<T> required(boolean required) {
		getInstance().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#requiredPropertyHandler(com.holonplatform.
	 * vaadin.flow.components.support.PropertyHandler)
	 */
	@Override
	public HasValueInputBuilder<T> requiredPropertyHandler(PropertyHandler<Boolean> requiredPropertyHandler) {
		getInstance().setRequiredPropertyHandler(requiredPropertyHandler);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#labelPropertyHandler(com.holonplatform.
	 * vaadin.flow.components.support.PropertyHandler)
	 */
	@Override
	public HasValueInputBuilder<T> labelPropertyHandler(PropertyHandler<String> labelPropertyHandler) {
		getInstance().setLabelPropertyHandler(labelPropertyHandler);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#titlePropertyHandler(com.holonplatform.
	 * vaadin.flow.components.support.PropertyHandler)
	 */
	@Override
	public HasValueInputBuilder<T> titlePropertyHandler(PropertyHandler<String> titlePropertyHandler) {
		getInstance().setTitlePropertyHandler(titlePropertyHandler);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#placeholderPropertyHandler(com.
	 * holonplatform.vaadin.flow.components.support.PropertyHandler)
	 */
	@Override
	public HasValueInputBuilder<T> placeholderPropertyHandler(PropertyHandler<String> placeholderPropertyHandler) {
		getInstance().setPlaceholderPropertyHandler(placeholderPropertyHandler);
		return getConfigurator();
	}

}
