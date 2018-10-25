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

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.PropertyHandler;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.HasValueInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;

/**
 * Default {@link HasValueInputBuilder} implementation.
 *
 * @param <T> Value type
 * @param <V> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 *
 * @since 5.2.0
 */
public class DefaultHasValueInputBuilder<T, V extends HasValue<?, T>, C extends Component> extends
		AbstractComponentConfigurator<C, HasValueInputBuilder<T, V, C>> implements HasValueInputBuilder<T, V, C> {

	private final HasValueInput<T, V, C> instance;

	/**
	 * Constructor using separate {@link HasValue} and {@link Component} field instances.
	 * @param <E> ValueChangeEvent type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 */
	public DefaultHasValueInputBuilder(V field, C component) {
		super(component);
		this.instance = new HasValueInput<>(field, component);
	}

	/**
	 * Get the {@link HasValueInput} instance to build.
	 * @return the instance
	 */
	protected HasValueInput<T, V, C> getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected HasValueInputBuilder<T, V, C> getConfigurator() {
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
	public HasValueInputBuilder<T, V, C> readOnly(boolean readOnly) {
		getInstance().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> withValue(T value) {
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
	public HasValueInputBuilder<T, V, C> withValueChangeListener(ValueChangeListener<T> listener) {
		getInstance().addValueChangeListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> required(boolean required) {
		getInstance().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#emptyValueSupplier(java.util.function.
	 * Supplier)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> emptyValueSupplier(Function<V, T> emptyValueSupplier) {
		getInstance().setEmptyValueSupplier(emptyValueSupplier);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#isEmptySupplier(java.util.function.
	 * Function)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> isEmptySupplier(Function<V, Boolean> isEmptySupplier) {
		getInstance().setIsEmptySupplier(isEmptySupplier);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#valueSupplier(java.util.function.Function)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> valueSupplier(Function<V, T> valueSupplier) {
		getInstance().setValueSupplier(valueSupplier);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#focusOperation(java.util.function.
	 * Consumer)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> focusOperation(Consumer<V> focusOperation) {
		getInstance().setFocusOperation(focusOperation);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#hasSizeSupplier(java.util.function.
	 * Function)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> hasSizeSupplier(Function<V, HasSize> hasSizeSupplier) {
		getInstance().setHasSizeSupplier(hasSizeSupplier);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#hasStyleSupplier(java.util.function.
	 * Function)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> hasStyleSupplier(Function<V, HasStyle> hasStyleSupplier) {
		getInstance().setHasStyleSupplier(hasStyleSupplier);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#hasEnabledSupplier(java.util.function.
	 * Function)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> hasEnabledSupplier(Function<V, HasEnabled> hasEnabledSupplier) {
		getInstance().setHasEnabledSupplier(hasEnabledSupplier);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#requiredPropertyHandler(com.holonplatform.
	 * vaadin.flow.components.support.PropertyHandler)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> requiredPropertyHandler(
			PropertyHandler<Boolean, T, V, C> requiredPropertyHandler) {
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
	public HasValueInputBuilder<T, V, C> labelPropertyHandler(PropertyHandler<String, T, V, C> labelPropertyHandler) {
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
	public HasValueInputBuilder<T, V, C> titlePropertyHandler(PropertyHandler<String, T, V, C> titlePropertyHandler) {
		getInstance().setTitlePropertyHandler(titlePropertyHandler);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#placeholderPropertyHandler(com.
	 * holonplatform.vaadin.flow.components.support.PropertyHandler)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> placeholderPropertyHandler(
			PropertyHandler<String, T, V, C> placeholderPropertyHandler) {
		getInstance().setPlaceholderPropertyHandler(placeholderPropertyHandler);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder#withValueChangeListeners(java.util.
	 * Collection)
	 */
	@Override
	public HasValueInputBuilder<T, V, C> withValueChangeListeners(Collection<ValueChangeListener<T>> listeners) {
		if (listeners != null) {
			listeners.forEach(listener -> getInstance().addValueChangeListener(listener));
		}
		return getConfigurator();
	}

}
