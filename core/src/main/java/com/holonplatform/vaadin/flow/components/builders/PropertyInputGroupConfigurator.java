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

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup.DefaultValueProvider;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.converter.Converter;

/**
 * {@link PropertyInputGroup} configurator.
 *
 * @param <G> Actual {@link PropertyInputGroup} type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface PropertyInputGroupConfigurator<G extends PropertyInputGroup, C extends PropertyInputGroupConfigurator<G, C>>
		extends PropertyGroupConfigurator<C> {

	/**
	 * Set the given property as read-only. If a property is read-only, the {@link Input} bound to the property will be
	 * setted as read-only too, and its value cannot be changed by the user.
	 * <p>
	 * Any validator bound to the given property will be ignored.
	 * </p>
	 * @param <T> Property type
	 * @param property Property to set as read-only (not null)
	 * @return this
	 */
	<T> C readOnly(Property<T> property);

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param <T> Property type
	 * @param property Property to set as required (not null)
	 * @return this
	 */
	<T> C required(Property<T> property);

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param <T> Property type
	 * @param property Property to set as required (not null)
	 * @param message The required validation error message to use
	 * @return this
	 */
	<T> C required(Property<T> property, Localizable message);

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param <T> Property type
	 * @param property Property to set as required (not null)
	 * @param message The required validation error message to use
	 * @return this
	 */
	default <T> C required(Property<T> property, String message) {
		return required(property, Localizable.builder().message(message).build());
	}

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param <T> Property type
	 * @param property Property to set as required (not null)
	 * @param defaultMessage Default required validation error message
	 * @param messageCode Required validation error message translation key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default <T> C required(Property<T> property, String defaultMessage, String messageCode, Object... arguments) {
		return required(property, Localizable.builder().message((defaultMessage == null) ? "" : defaultMessage)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Set the default value provider for given <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param defaultValueProvider DefaultValueProvider (not null)
	 * @return this
	 */
	<T> C defaultValue(Property<T> property, DefaultValueProvider<T> defaultValueProvider);

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
	 * @param <V> Input value type type
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
	 * Add a {@link BiConsumer} to allow further {@link Input} configuration before the input is actually bound to a
	 * property.
	 * @param postProcessor the post processor to add (not null)
	 * @return this
	 */
	C withPostProcessor(BiConsumer<Property<?>, Input<?>> postProcessor);

	/**
	 * Add a {@link ValueChangeListener} to the {@link Input} bound to given <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param listener The ValueChangeListener to add (not null)
	 * @return this
	 */
	<T> C withValueChangeListener(Property<T> property, ValueChangeListener<T> listener);

	/**
	 * Adds a {@link Validator} to the {@link Input} bound to given <code>property</code>.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @param validator Validator to add (not null)
	 * @return this
	 */
	<T> C withValidator(Property<T> property, Validator<T> validator);

	/**
	 * Adds a {@link Validator} to the {@link PropertyInputGroup}, using a {@link PropertyBox} to provide the property
	 * values to validate.
	 * @param validator Validator to add (not null)
	 * @return this
	 */
	C withValidator(Validator<PropertyBox> validator);

	/**
	 * Set the {@link ValidationStatusHandler} to use to track given <code>property</code> validation status changes.
	 * @param <T> Property type
	 * @param property Property for which to set the validation status handler
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to associate to given <code>property</code>
	 *        (not null)
	 * @return this
	 */
	<T> C validationStatusHandler(Property<T> property,
			ValidationStatusHandler<PropertyInputGroup, T, Input<T>> validationStatusHandler);

	/**
	 * Set the {@link ValidationStatusHandler} to use to track overall validation status changes.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set (not null)
	 * @return this
	 */
	C validationStatusHandler(
			ValidationStatusHandler<PropertyInputGroup, PropertyBox, ValueComponent<PropertyBox>> validationStatusHandler);

	/**
	 * By default, a default {@link ValidationStatusHandler} is associated to each group property {@link Input}. This
	 * method can be used to avoid the default {@link ValidationStatusHandler} configuration.
	 * @return this
	 * @see ValidationStatusHandler#getDefault()
	 * @see #validationStatusHandler(Property, ValidationStatusHandler)
	 */
	C disableDefaultPropertyValidationStatusHandler();

	/**
	 * Use given label as status label to track overall validation status changes.
	 * @param <L> Label type
	 * @param statusLabel the status label to set (not null)
	 * @return this
	 */
	default <L extends Component & HasText> C validationStatusLabel(L statusLabel) {
		return validationStatusHandler(ValidationStatusHandler.label(statusLabel));
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
	 * Set whether to stop validation at first validation failure. If <code>true</code>, only the first
	 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all the
	 * occurred validation exception is thrown.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
	 * @return this
	 */
	C stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure);

	/**
	 * Set whether to stop overall validation at first validation failure. If <code>true</code>, only the first
	 * {@link ValidationException} is thrown at validation, otherwise a {@link ValidationException} containing all the
	 * occurred validation exception is thrown.
	 * <p>
	 * The overall validation is the one which is performed using validators added with
	 * {@link #withValidator(Validator)} method.
	 * </p>
	 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
	 *        failure
	 * @return this
	 */
	C stopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure);

	/**
	 * Set whether to enable {@link VirtualProperty} input value refresh when any group input value changes.
	 * <p>
	 * Default is <code>false</code>.
	 * </p>
	 * <p>
	 * The {@link PropertyInputGroup#refresh()} and {@link PropertyInputGroup#refresh(Property)} to explicitly trigger
	 * the {@link VirtualProperty} inputs value refresh.
	 * </p>
	 * @param enableRefreshOnValueChange Whether to enable {@link VirtualProperty} input value refresh when any group
	 *        input value changes
	 * @return this
	 */
	C enableRefreshOnValueChange(boolean enableRefreshOnValueChange);

}
