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
package com.holonplatform.vaadin.flow.components;

import java.util.Optional;

import com.holonplatform.core.Registration;
import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.ValidatableInputAdapter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultValidatableInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * An {@link Input} component with validation support using {@link Validator}s.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public interface ValidatableInput<T> extends Input<T>, Validatable {

	/**
	 * Get the input value.
	 * <p>
	 * The value is validated using the registered {@link Validator}s, if any, and a {@link ValidationException} is
	 * thrown if any validator fail.
	 * </p>
	 * @return the input value
	 * @throws ValidationException If validation fails
	 */
	@Override
	T getValue();

	/**
	 * Get the input value, if the input is not empty and the value is <em>valid</em>, i.e. validation is successful for
	 * any validator bound to the input.
	 * <p>
	 * Differently from the {@link #getValue()} method, any validation exception is swallowed, returning an empty
	 * Optional instead.
	 * </p>
	 * @return the input value, or an empty Optional if the input is empty or the input value is not valid
	 */
	default Optional<T> getValueIfValid() {
		try {
			return Optional.ofNullable(getValue());
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return Optional.empty();
		}
	}

	/**
	 * Adds a {@link Validator} to validate the input value.
	 * @param validator The validator to add (not null)
	 * @return The validator registration reference
	 */
	Registration addValidator(Validator<T> validator);

	/**
	 * Sets whether to validate the value, using registered {@link Validator}s, every time the {@link Input} value
	 * changes.
	 * @param validateOnValueChange <code>true</code> to perform value validation every time the {@link Input} value
	 *        changes, <code>false</code> if not
	 */
	void setValidateOnValueChange(boolean validateOnValueChange);

	/**
	 * Gets whether to validate the value, using registered {@link Validator}s, every time the {@link Input} value
	 * changes.
	 * @return <code>true</code> if the value validation must be performed every time the {@link Input} value changes
	 */
	boolean isValidateOnValueChange();

	/**
	 * Set the {@link ValidationStatusHandler} to use to track validation status changes.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	void setValidationStatusHandler(ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler);

	/**
	 * Get the {@link ValidationStatusHandler} to use to track validation status changes, if available.
	 * @return the optional {@link ValidationStatusHandler}
	 */
	Optional<ValidationStatusHandler<ValidatableInput<T>>> getValidationStatusHandler();

	// Builders

	/**
	 * Create a {@link ValidatableInput} from given {@link Input} instance.
	 * @param <T> Value type
	 * @param input The {@link Input} instance (not null)
	 * @return A new {@link ValidatableInput} component which wraps the given <code>input</code>
	 */
	static <T> ValidatableInput<T> from(Input<T> input) {
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		return (input instanceof ValidatableInput) ? (ValidatableInput<T>) input : new ValidatableInputAdapter<>(input);
	}

	/**
	 * Create a {@link ValidatableInput} component type from given {@link HasValue} component.
	 * @param <T> Value type
	 * @param <F> {@link HasValue} component type
	 * @param field The field instance (not null)
	 * @return A new {@link ValidatableInput} component which wraps the given <code>field</code>
	 */
	static <F extends Component & HasValue<?, T>, T> ValidatableInput<T> from(F field) {
		return from(Input.from(field));
	}

	/**
	 * Get a fluent builder to create and setup a {@link ValidatableInput} from given {@link Input}.
	 * @param <T> Value type
	 * @param input Concrete input component (not null)
	 * @return {@link ValidatableInput} builder
	 */
	static <T> ValidatableInputBuilder<T> builder(Input<T> input) {
		return new DefaultValidatableInputBuilder<>(input);
	}

	// Renderers

	/**
	 * Try to obtain a {@link ValidatableInput} component to handle the value of given <code>property</code>.
	 * <p>
	 * The current {@link PropertyRendererRegistry} is used to look for a suitable {@link PropertyRenderer} to render
	 * the {@link ValidatableInput} using the provided {@link Property}.
	 * </p>
	 * @param <T> Property type
	 * @param property The property for which to obtain the {@link ValidatableInput} (not null)
	 * @return Optional property {@link ValidatableInput} component
	 * @see PropertyRendererRegistry#get()
	 */
	@SuppressWarnings("unchecked")
	static <T> Optional<ValidatableInput<T>> forProperty(Property<T> property) {
		return property.renderIfAvailable(ValidatableInput.class).map(input -> input);
	}

	/**
	 * Get a {@link ValidatableInput} component to handle the value of given <code>property</code>.
	 * <p>
	 * The current {@link PropertyRendererRegistry} is used to look for a suitable {@link PropertyRenderer} to render
	 * the {@link ValidatableInput} using the provided {@link Property}.
	 * </p>
	 * @param <T> Property type
	 * @param property The property for which to obtain the {@link ValidatableInput} (not null)
	 * @return The property {@link ValidatableInput} component
	 * @throws NoSuitableRendererAvailableException If a suitable PropertyRenderer is not available to render given
	 *         property as a ValidatableInput
	 * @see PropertyRendererRegistry#get()
	 */
	@SuppressWarnings("unchecked")
	static <T> ValidatableInput<T> create(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return property.renderIfAvailable(ValidatableInput.class).orElseGet(() -> from(Input.create(property)));
	}

}
