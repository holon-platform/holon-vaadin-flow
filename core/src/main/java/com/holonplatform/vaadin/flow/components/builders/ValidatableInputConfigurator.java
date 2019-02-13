/*
 * Copyright 2016-2019 Axioma srl.
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

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;

/**
 * {@link ValidatableInput} configurator.
 *
 * @param <T> Value type
 * @param <I> Concrete validatable input type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public interface ValidatableInputConfigurator<T, I extends ValidatableInput<T>, C extends ValidatableInputConfigurator<T, I, C>> {

	/**
	 * Adds a {@link Validator} to the {@link Input} bound to given <code>property</code>.
	 * @param validator Validator to add (not null)
	 * @return this
	 */
	C withValidator(Validator<T> validator);

	/**
	 * Set the {@link ValidationStatusHandler} to use to input validation status changes.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set (not null)
	 * @return this
	 */
	C validationStatusHandler(ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler);

	/**
	 * Sets whether to validate the {@link Input} component value every time the {@link Input} value changes.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @param validateOnValueChange <code>true</code> to perform value validation every time the {@link Input} value
	 *        changes, <code>false</code> if not
	 * @return this
	 */
	C validateOnValueChange(boolean validateOnValueChange);

	/**
	 * Sets the {@link Input} as required. Required inputs must filled by the user, and its validation will fail when
	 * empty.
	 * @return this
	 */
	C required();

	/**
	 * Set the {@link Input} as required, using given {@link Validator} to check the input value. Required inputs must
	 * filled by the user, and its validation will fail when empty.
	 * @param validator The {@link Validator} to use to check the required input value (not null)
	 * @return this
	 */
	C required(Validator<T> validator);

	/**
	 * Sets the {@link Input} as required, using given message as validation error message. Required inputs must filled
	 * by the user, and its validation will fail when empty.
	 * @param message The message to use to notify the required validation failure
	 * @return this
	 */
	C required(Localizable message);

	/**
	 * Sets the {@link Input} as required, using given message as validation error message. Required inputs must filled
	 * by the user, and its validation will fail when empty.
	 * @param message The default message to use to notify the required validation failure
	 * @param messageCode The message localization code
	 * @param arguments Optional message translation arguments
	 * @return this
	 */
	default C required(String message, String messageCode, Object... arguments) {
		return required(
				Localizable.builder().message(message).messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Sets the {@link Input} as required, using given message as validation error message. Required inputs must filled
	 * by the user, and its validation will fail when empty.
	 * @param message The default message to use to notify the required validation failure
	 * @return this
	 */
	default C required(String message) {
		return required(Localizable.builder().message(message).build());
	}

}
