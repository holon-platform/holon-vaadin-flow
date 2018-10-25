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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasValueInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;

/**
 * A builder to create {@link Input} components from a {@link HasValue} component.
 * 
 * @param <T> Value type
 * @param <V> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 *
 * @since 5.2.0
 */
public interface HasValueInputBuilder<T, V extends HasValue<?, T>, C extends Component>
		extends InputBuilder<T, Input<T>, HasValueInputBuilder<T, V, C>> {

	/**
	 * Set the empty value supplier.
	 * @param emptyValueSupplier the empty value supplier to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> emptyValueSupplier(Function<V, T> emptyValueSupplier);

	/**
	 * Set the <em>is empty</em> value supplier.
	 * @param isEmptySupplier the <em>is empty</em> value supplier to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> isEmptySupplier(Function<V, Boolean> isEmptySupplier);

	/**
	 * Set the Input value supplier.
	 * @param valueSupplier the Input value supplier to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> valueSupplier(Function<V, T> valueSupplier);

	/**
	 * Set the <code>focus</code> operation.
	 * @param focusOperation the operation to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> focusOperation(Consumer<V> focusOperation);

	/**
	 * Set the {@link HasSize} supplier.
	 * @param hasSizeSupplier the supplier to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> hasSizeSupplier(Function<V, HasSize> hasSizeSupplier);

	/**
	 * Set the {@link HasStyle} supplier.
	 * @param hasStyleSupplier the supplier to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> hasStyleSupplier(Function<V, HasStyle> hasStyleSupplier);

	/**
	 * Set the {@link HasEnabled} supplier.
	 * @param hasEnabledSupplier the supplier to set
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> hasEnabledSupplier(Function<V, HasEnabled> hasEnabledSupplier);

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
	HasValueInputBuilder<T, V, C> requiredPropertyHandler(PropertyHandler<Boolean, T, V, C> requiredPropertyHandler);

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
	default HasValueInputBuilder<T, V, C> requiredPropertyHandler(BiFunction<V, C, Boolean> getter,
			TriConsumer<V, C, Boolean> setter) {
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
	HasValueInputBuilder<T, V, C> labelPropertyHandler(PropertyHandler<String, T, V, C> labelPropertyHandler);

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
	default HasValueInputBuilder<T, V, C> labelPropertyHandler(BiFunction<V, C, String> getter,
			TriConsumer<V, C, String> setter) {
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
	HasValueInputBuilder<T, V, C> titlePropertyHandler(PropertyHandler<String, T, V, C> titlePropertyHandler);

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
	default HasValueInputBuilder<T, V, C> titlePropertyHandler(BiFunction<V, C, String> getter,
			TriConsumer<V, C, String> setter) {
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
	HasValueInputBuilder<T, V, C> placeholderPropertyHandler(
			PropertyHandler<String, T, V, C> placeholderPropertyHandler);

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
	default HasValueInputBuilder<T, V, C> placeholderPropertyHandler(BiFunction<V, C, String> getter,
			TriConsumer<V, C, String> setter) {
		return placeholderPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Add a set of {@link ValueChangeListener}s to be notified when the input value changes.
	 * @param listeners The {@link ValueChangeListener}s to add (not null)
	 * @return this
	 */
	HasValueInputBuilder<T, V, C> withValueChangeListeners(Collection<ValueChangeListener<T>> listeners);

	// statics

	/**
	 * Create a new {@link HasValueInputBuilder} using given {@link HasValue} {@link Component} field instance.
	 * @param <T> Value type
	 * @param <H> Actual field type
	 * @param field {@link HasValue} {@link Component} field (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, H extends Component & HasValue<?, T>> HasValueInputBuilder<T, H, H> create(H field) {
		return new DefaultHasValueInputBuilder<>(field, field);
	}

	/**
	 * Create a new {@link HasValueInputBuilder} using given {@link HasValue} and {@link Component} field instances.
	 * @param <T> Value type
	 * @param <V> {@link HasValue} type
	 * @param <C> {@link Component} type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, V extends HasValue<?, T>, C extends Component> HasValueInputBuilder<T, V, C> create(V field,
			C component) {
		return new DefaultHasValueInputBuilder<>(field, component);
	}

}
