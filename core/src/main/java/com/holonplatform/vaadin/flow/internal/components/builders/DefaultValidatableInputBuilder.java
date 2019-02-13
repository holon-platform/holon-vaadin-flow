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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;

/**
 * Default {@link ValidatableInputBuilder}.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultValidatableInputBuilder<T> implements ValidatableInputBuilder<T> {

	private final ValidatableInput<T> instance;

	private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

	public DefaultValidatableInputBuilder(Input<T> input) {
		super();
		this.instance = ValidatableInput.from(input);
		this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#withValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public ValidatableInputBuilder<T> withValidator(Validator<T> validator) {
		validatableInputConfigurator.withValidator(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder#validationStatusHandler(com.
	 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
	 */
	@Override
	public ValidatableInputBuilder<T> validationStatusHandler(
			ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
		validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#validateOnValueChange(boolean)
	 */
	@Override
	public ValidatableInputBuilder<T> validateOnValueChange(boolean validateOnValueChange) {
		validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#required()
	 */
	@Override
	public ValidatableInputBuilder<T> required() {
		validatableInputConfigurator.required();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#required(com.holonplatform.core.Validator)
	 */
	@Override
	public ValidatableInputBuilder<T> required(Validator<T> validator) {
		validatableInputConfigurator.required(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#required(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public ValidatableInputBuilder<T> required(Localizable message) {
		validatableInputConfigurator.required(message);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput.Builder#build()
	 */
	@Override
	public ValidatableInput<T> build() {
		return validatableInputConfigurator.configure(instance);
	}

}
