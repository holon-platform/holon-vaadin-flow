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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.holonplatform.core.operation.TriConsumer;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.PropertyHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEventNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.value.HasValueChangeMode;

/**
 * Base builder to create {@link Input} components from a {@link HasValue} component.
 * 
 * @param <T> Input value type
 * @param <V> {@link HasValue} value type
 * @param <H> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface InputAdapterBuilder<T, V, H extends HasValue<?, V>, C extends Component, B extends InputAdapterBuilder<T, V, H, C, B>>
		extends InputBuilder<T, Input<T>, B> {

	/**
	 * Set the empty value supplier.
	 * @param emptyValueSupplier the empty value supplier to set
	 * @return this
	 */
	B emptyValueSupplier(Function<H, T> emptyValueSupplier);

	/**
	 * Set the <em>is empty</em> value supplier.
	 * @param isEmptySupplier the <em>is empty</em> value supplier to set
	 * @return this
	 */
	B isEmptySupplier(Function<H, Boolean> isEmptySupplier);

	/**
	 * Set the Input value supplier.
	 * @param valueSupplier the Input value supplier to set
	 * @return this
	 */
	B valueSupplier(Function<H, T> valueSupplier);

	/**
	 * Set the <code>focus</code> operation.
	 * @param focusOperation the operation to set
	 * @return this
	 */
	B focusOperation(Consumer<H> focusOperation);

	/**
	 * Set the {@link HasSize} supplier.
	 * @param hasSizeSupplier the supplier to set
	 * @return this
	 */
	B hasSizeSupplier(Function<H, HasSize> hasSizeSupplier);

	/**
	 * Set the {@link HasStyle} supplier.
	 * @param hasStyleSupplier the supplier to set
	 * @return this
	 */
	B hasStyleSupplier(Function<H, HasStyle> hasStyleSupplier);

	/**
	 * Set the {@link HasEnabled} supplier.
	 * @param hasEnabledSupplier the supplier to set
	 * @return this
	 */
	B hasEnabledSupplier(Function<H, HasEnabled> hasEnabledSupplier);

	/**
	 * Set the {@link HasValueChangeMode} supplier.
	 * @param hasValueChangeModeSupplier the supplier to set
	 * @return this
	 */
	B hasValueChangeModeSupplier(Function<H, HasValueChangeMode> hasValueChangeModeSupplier);

	/**
	 * Set the {@link HasValidation} supplier.
	 * @param hasValidationSupplier the supplier to set
	 * @return this
	 */
	B hasValidationSupplier(Function<H, HasValidation> hasValidationSupplier);

	/**
	 * Set the {@link InvalidChangeEventNotifier} supplier
	 * @param invalidChangeEventNotifierSupplier the supplier to set
	 * @return this
	 */
	B invalidChangeEventNotifierSupplier(Function<H, InvalidChangeEventNotifier> invalidChangeEventNotifierSupplier);

	/**
	 * Provide the {@link PropertyHandler} to use to get and set the <em>required</em> property value.
	 * <p>
	 * This handler will be used for the {@link Input#isRequired()} and {@link Input#setRequired(boolean)} methods
	 * implementation.
	 * </p>
	 * @param requiredPropertyHandler The {@link PropertyHandler} to use to get and set the <em>required</em> property
	 *        value
	 * @return this
	 */
	B requiredPropertyHandler(PropertyHandler<Boolean, V, H, C> requiredPropertyHandler);

	/**
	 * Set the <code>required</code> property handler using given callback functions.
	 * <p>
	 * This handler will be used for the {@link Input#isRequired()} and {@link Input#setRequired(boolean)} methods
	 * implementation.
	 * </p>
	 * @param getter The {@link Supplier} to use to get the <code>required</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>required</code> property value (not null)
	 * @return this
	 */
	default B requiredPropertyHandler(BiFunction<H, C, Boolean> getter, TriConsumer<H, C, Boolean> setter) {
		return requiredPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Provide the {@link PropertyHandler} to use to get and set the <em>label</em> property value.
	 * <p>
	 * This handler will be used to provide the {@link HasLabel} implementation returned by the {@link Input#hasLabel()}
	 * method.
	 * </p>
	 * @param labelPropertyHandler The {@link PropertyHandler} to use to get and set the <em>label</em> property value
	 * @return this
	 */
	B labelPropertyHandler(PropertyHandler<String, V, H, C> labelPropertyHandler);

	/**
	 * Set the <code>label</code> property handler using given callback functions.
	 * <p>
	 * This handler will be used to provide the {@link HasLabel} implementation returned by the {@link Input#hasLabel()}
	 * method.
	 * </p>
	 * @param getter The {@link Supplier} to use to get the <code>label</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>label</code> property value (not null)
	 * @return this
	 */
	default B labelPropertyHandler(BiFunction<H, C, String> getter, TriConsumer<H, C, String> setter) {
		return labelPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Provide the {@link PropertyHandler} to use to get and set the <em>title</em> property value.
	 * <p>
	 * This handler will be used to provide the {@link HasTitle} implementation returned by the {@link Input#hasTitle()}
	 * method.
	 * </p>
	 * @param titlePropertyHandler The {@link PropertyHandler} to use to get and set the <em>title</em> property value
	 * @return this
	 */
	B titlePropertyHandler(PropertyHandler<String, V, H, C> titlePropertyHandler);

	/**
	 * Set the <code>title</code> property handler using given callback functions.
	 * <p>
	 * This handler will be used to provide the {@link HasTitle} implementation returned by the {@link Input#hasTitle()}
	 * method.
	 * </p>
	 * @param getter The {@link Supplier} to use to get the <code>title</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>title</code> property value (not null)
	 * @return this
	 */
	default B titlePropertyHandler(BiFunction<H, C, String> getter, TriConsumer<H, C, String> setter) {
		return titlePropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Provide the {@link PropertyHandler} to use to get and set the <em>placeholder</em> property value.
	 * <p>
	 * This handler will be used to provide the {@link HasPlaceholder} implementation returned by the
	 * {@link Input#hasPlaceholder()} method.
	 * </p>
	 * @param placeholderPropertyHandler The {@link PropertyHandler} to use to get and set the <em>placeholder</em>
	 *        property value
	 * @return this
	 */
	B placeholderPropertyHandler(PropertyHandler<String, V, H, C> placeholderPropertyHandler);

	/**
	 * Set the <code>placeholder</code> property handler using given callback functions.
	 * <p>
	 * This handler will be used to provide the {@link HasPlaceholder} implementation returned by the
	 * {@link Input#hasPlaceholder()} method.
	 * </p>
	 * @param getter The {@link Supplier} to use to get the <code>placeholder</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>placeholder</code> property value (not null)
	 * @return this
	 */
	default B placeholderPropertyHandler(BiFunction<H, C, String> getter, TriConsumer<H, C, String> setter) {
		return placeholderPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Add a set of {@link ValueChangeListener}s to be notified when the input value changes.
	 * @param listeners The {@link ValueChangeListener}s to add (not null)
	 * @return this
	 */
	B withValueChangeListeners(Collection<ValueChangeListener<T>> listeners);

}
