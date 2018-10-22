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

import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.support.PropertyHandler;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasValueInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * A builder to create {@link Input} components from a {@link HasValue} component.
 *
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public interface HasValueInputBuilder<T> extends InputBuilder<T, Input<T>, HasValueInputBuilder<T>> {

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
	HasValueInputBuilder<T> requiredPropertyHandler(PropertyHandler<Boolean> requiredPropertyHandler);

	/**
	 * Provide the {@link PropertyHandler} to use to get and set the <em>label</em> property value.
	 * <p>
	 * This handler will be used to provide the {@link HasLabel} implementation returned by the {@link Input#hasLabel()}
	 * method.
	 * </p>
	 * @param labelPropertyHandler The {@link PropertyHandler} to use to get and set the <em>label</em> property value
	 * @return this
	 */
	HasValueInputBuilder<T> labelPropertyHandler(PropertyHandler<String> labelPropertyHandler);

	/**
	 * Provide the {@link PropertyHandler} to use to get and set the <em>title</em> property value.
	 * <p>
	 * This handler will be used to provide the {@link HasTitle} implementation returned by the {@link Input#hasTitle()}
	 * method.
	 * </p>
	 * @param titlePropertyHandler The {@link PropertyHandler} to use to get and set the <em>title</em> property value
	 * @return this
	 */
	HasValueInputBuilder<T> titlePropertyHandler(PropertyHandler<String> titlePropertyHandler);

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
	HasValueInputBuilder<T> placeholderPropertyHandler(PropertyHandler<String> placeholderPropertyHandler);

	// statics

	/**
	 * Create a new {@link HasValueInputBuilder} using given {@link HasValue} {@link Component} field instance.
	 * @param <T> Value type
	 * @param <E> ValueChangeEvent type
	 * @param <H> Actual field type
	 * @param field {@link HasValue} {@link Component} field (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, E extends HasValue.ValueChangeEvent<T>, H extends Component & HasValue<E, T>> HasValueInputBuilder<T> create(
			H field) {
		return new DefaultHasValueInputBuilder<>(field);
	}

	/**
	 * Create a new {@link HasValueInputBuilder} using given {@link HasValue} and {@link Component} field instances.
	 * @param <T> Value type
	 * @param <E> ValueChangeEvent type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, E extends HasValue.ValueChangeEvent<T>> HasValueInputBuilder<T> create(HasValue<E, T> field,
			Component component) {
		return new DefaultHasValueInputBuilder<>(field, component);
	}

}
