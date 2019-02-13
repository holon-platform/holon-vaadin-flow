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

import java.util.List;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ValidatableStringInputBuilder;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Default {@link ValidatableStringInputBuilder} implementation.
 *
 * @since 5.2.2
 */
public class DefaultValidatableStringInputBuilder extends AbstractStringInputBuilder<ValidatableStringInputBuilder>
		implements ValidatableStringInputBuilder {

	private final DefaultValidatableInputConfigurator<String> validatableInputConfigurator;

	public DefaultValidatableStringInputBuilder(TextField component, boolean emptyValuesAsNull,
			boolean blankValuesAsNull,
			List<ValueChangeListener<String, ValueChangeEvent<String>>> valueChangeListeners) {
		super(component, emptyValuesAsNull, blankValuesAsNull, valueChangeListeners);
		this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ValidatableStringInputBuilder getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder#build()
	 */
	@Override
	public ValidatableInput<String> build() {
		return validatableInputConfigurator.configure(buildAsValidatableInput());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.holonplatform.
	 * core.Validator)
	 */
	@Override
	public ValidatableStringInputBuilder withValidator(Validator<String> validator) {
		validatableInputConfigurator.withValidator(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validationStatusHandler(com.
	 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
	 */
	@Override
	public ValidatableStringInputBuilder validationStatusHandler(
			ValidationStatusHandler<ValidatableInput<String>> validationStatusHandler) {
		validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
	 */
	@Override
	public ValidatableStringInputBuilder validateOnValueChange(boolean validateOnValueChange) {
		validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.core.
	 * Validator)
	 */
	@Override
	public ValidatableStringInputBuilder required(Validator<String> validator) {
		validatableInputConfigurator.required(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public ValidatableStringInputBuilder required(Localizable message) {
		validatableInputConfigurator.required(message);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public ValidatableStringInputBuilder required(boolean required) {
		validatableInputConfigurator.required(required);
		return this;
	}

}
