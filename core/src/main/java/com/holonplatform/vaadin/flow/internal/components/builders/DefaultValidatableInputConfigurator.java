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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator;
import com.holonplatform.vaadin.flow.internal.components.RequiredInputValidator;

/**
 * Default {@link ValidatableInputConfigurator} implementation.
 *
 * @param <T> Value type
 *
 * @since 5.2.2
 */
public class DefaultValidatableInputConfigurator<T>
		implements ValidatableInputConfigurator<T, ValidatableInput<T>, DefaultValidatableInputConfigurator<T>> {

	private boolean required = false;

	private Validator<T> requiredValidator;

	private Localizable requiredMessage;

	private final List<Validator<T>> validators = new LinkedList<>();

	private ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler;

	private boolean validateOnValueChange;

	public DefaultValidatableInputConfigurator() {
		super();
	}

	protected Optional<Validator<T>> getRequiredValidator() {
		return Optional.ofNullable(requiredValidator);
	}

	protected Optional<Localizable> getRequiredMessage() {
		return Optional.ofNullable(requiredMessage);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.holonplatform.
	 * core.Validator)
	 */
	@Override
	public DefaultValidatableInputConfigurator<T> withValidator(Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		this.validators.add(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validationStatusHandler(com.
	 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
	 */
	@Override
	public DefaultValidatableInputConfigurator<T> validationStatusHandler(
			ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
		ObjectUtils.argumentNotNull(validationStatusHandler, "ValidationStatusHandler must be not null");
		this.validationStatusHandler = validationStatusHandler;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
	 */
	@Override
	public DefaultValidatableInputConfigurator<T> validateOnValueChange(boolean validateOnValueChange) {
		this.validateOnValueChange = validateOnValueChange;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required()
	 */
	@Override
	public DefaultValidatableInputConfigurator<T> required() {
		this.required = true;
		return this;
	}

	/**
	 * Sets whether the {@link Input} is required.
	 * @param required Whether the {@link Input} is required
	 * @return this
	 */
	public DefaultValidatableInputConfigurator<T> required(boolean required) {
		this.required = required;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.core.
	 * Validator)
	 */
	@Override
	public DefaultValidatableInputConfigurator<T> required(Validator<T> validator) {
		this.required = true;
		this.requiredValidator = validator;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public DefaultValidatableInputConfigurator<T> required(Localizable message) {
		this.required = true;
		this.requiredMessage = message;
		return this;
	}

	/**
	 * Configure the validatable input.
	 * @param <V> Input type
	 * @param input The input to configure
	 * @return The configured input
	 */
	public <V extends ValidatableInput<T>> V configure(V input) {
		// configure
		validators.forEach(v -> input.addValidator(v));
		if (validationStatusHandler != null) {
			input.setValidationStatusHandler(validationStatusHandler);
		}
		if (validateOnValueChange) {
			input.setValidateOnValueChange(validateOnValueChange);
		}
		// check required
		if (required) {
			input.setRequired(true);
			// add required validator
			input.addValidator(getRequiredValidator().orElse(new RequiredInputValidator<>(input,
					getRequiredMessage().orElse(RequiredInputValidator.DEFAULT_REQUIRED_ERROR))));
		}
		return input;
	}

}
