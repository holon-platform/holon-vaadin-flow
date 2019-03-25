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

import java.util.function.Function;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.PropertySetInputGroupConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.converter.Converter;

/**
 * {@link PropertyInputGroup} configurator.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface PropertyInputGroupConfigurator<C extends PropertyInputGroupConfigurator<C>>
		extends PropertySetInputGroupConfigurator<PropertyInputGroup, C> {

	/**
	 * Set the given property as hidden. If a property is hidden, the {@link Input} bound to the property will
	 * never be generated, but its value will be written to a {@link PropertyBox} using
	 * {@link PropertyViewGroup#getValue()}.
	 * @param <T> Property type
	 * @param property Property to set as hidden (not null)
	 * @return this
	 */
	<T> C hidden(Property<T> property);

	/**
	 * Set whether the given property is read-only. If a property is read-only, the {@link Input} bound to the property
	 * will be setted as read-only, and its value cannot be changed by the user.
	 * <p>
	 * Any validator bound to the given property will be ignored.
	 * </p>
	 * @param <T> Property type
	 * @param property The property to set as read-only (not null)
	 * @param readOnly <code>true</code> to set the property as read-only, <code>false</code> otherwise
	 * @return this
	 */
	<T> C readOnly(Property<T> property, boolean readOnly);

	/**
	 * Set the given property as read-only. If a property is read-only, the {@link Input} bound to the property will be
	 * setted as read-only, and its value cannot be changed by the user.
	 * <p>
	 * Any validator bound to the given property will be ignored.
	 * </p>
	 * @param <T> Property type
	 * @param property The property to set as read-only (not null)
	 * @return this
	 */
	default <T> C readOnly(Property<T> property) {
		return readOnly(property, true);
	}

	/**
	 * Set the specific {@link PropertyRenderer} to use to render the {@link Input} to bind to given
	 * <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param renderer Property renderer (not null)
	 * @return this
	 */
	<T> C bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer);

	/**
	 * Set the function to use to render the {@link Input} bound to given <code>property</code>.
	 * @param <T> Property type
	 * @param property The property to render (not null)
	 * @param function The function to use to render the property {@link Input} (not null)
	 * @return this
	 */
	default <T> C bind(Property<T> property, Function<Property<? extends T>, Input<T>> function) {
		return bind(property, InputPropertyRenderer.create(function));
	}

	/**
	 * Bind the given <code>property</code> to given <code>input</code> instance. If the property was already bound to a
	 * {@link Input}, the old input will be replaced by the new input.
	 * <p>
	 * This method also adds property validators to given {@link Input} when applicable.
	 * </p>
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param input Input to bind (not null)
	 * @return this
	 */
	default <T> C bind(Property<T> property, Input<T> input) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		return bind(property, InputPropertyRenderer.create(p -> input));
	}

	/**
	 * Bind the given <code>property</code> to given <code>input</code> instance with different value type, using a
	 * {@link Converter} to perform value conversions. If the property was already bound to a {@link Input}, the old
	 * input will be replaced by the new input.
	 * <p>
	 * This method also adds property validators to given {@link Input} when applicable.
	 * </p>
	 * @param <T> Property type
	 * @param <V> Input value type
	 * @param property Property (not null)
	 * @param input Input to bind (not null)
	 * @param converter Value converter (not null)
	 * @return this
	 */
	default <T, V> C bind(Property<T> property, Input<V> input, Converter<V, T> converter) {
		return bind(property, Input.from(input, converter));
	}

	/**
	 * Bind the given <code>property</code> to given {@link HasValue} component. If the property was already bound to a
	 * {@link Input}, the old input will be replaced by the new input.
	 * @param <T> Property type
	 * @param <F> HasValue type
	 * @param property Property (not null)
	 * @param field HasValue component to bind (not null)
	 * @return this
	 */
	default <T, F extends Component & HasValue<?, T>> C bindField(Property<T> property, F field) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		return bind(property, Input.from(field));
	}

	/**
	 * Bind the given <code>property</code> to given {@link HasValue} component with different value type, using a
	 * {@link Converter} to perform value conversions. If the property was already bound to an {@link Input}, the old
	 * input will be replaced by the new input.
	 * <p>
	 * This method also adds property validators to given {@link Input} when applicable.
	 * </p>
	 * @param <T> Property type
	 * @param <V> Input value type
	 * @param <F> HasValue type
	 * @param property Property (not null)
	 * @param field The field to bind (not null)
	 * @param converter Value converter (not null)
	 * @return this
	 */
	default <T, V, F extends Component & HasValue<?, V>> C bindField(Property<T> property, F field,
			Converter<V, T> converter) {
		return bind(property, Input.from(field, converter));
	}

	/**
	 * Sets whether to validate the available {@link Input}s value every time the {@link Input} value changes.
	 * <p>
	 * Default is <code>false</code>.
	 * </p>
	 * @param validateOnValueChange <code>true</code> to perform value validation every time a {@link Input} value
	 *        changes, <code>false</code> if not
	 * @return this
	 */
	C validateOnValueChange(boolean validateOnValueChange);

	/**
	 * Set whether to stop inputs validation at first validation failure. If <code>true</code>, only the first
	 * {@link ValidationException} is thrown at inputs validation, otherwise a {@link ValidationException} containing
	 * all the occurred validation exception is thrown.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop inputs validation at first validation failure
	 * @return this
	 */
	C stopInputsValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

	/**
	 * Set whether to stop group validation at first validation failure. If <code>true</code>, only the first
	 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all the
	 * occurred validation exception is thrown.
	 * <p>
	 * The group validation is the one which is performed using validators added with {@link #withValidator(Validator)}
	 * method.
	 * </p>
	 * @param stopValidationAtFirstFailure <code>true</code> to stop overall validation at first validation failure
	 * @return this
	 */
	C stopGroupValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

}
