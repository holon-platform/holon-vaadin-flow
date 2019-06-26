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

import java.time.LocalDateTime;
import java.util.List;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ValidatableLocalDateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.holonplatform.vaadin.flow.internal.components.DateTimeField;
import com.vaadin.flow.shared.Registration;

/**
 * Default {@link ValidatableLocalDateTimeInputBuilder} implementation.
 *
 * @since 5.2.2
 */
public class DefaultValidatableLocalDateTimeInputBuilder
		extends AbstractLocalDateTimeInputBuilder<ValidatableLocalDateTimeInputBuilder>
		implements ValidatableLocalDateTimeInputBuilder {

	private final DefaultValidatableInputConfigurator<LocalDateTime> validatableInputConfigurator;

	public DefaultValidatableLocalDateTimeInputBuilder(DateTimeField component,
			Registration contextLocaleOnAttachRegistration, CalendarLocalization localization,
			LocalDateTime initialValue,
			List<ValueChangeListener<LocalDateTime, ValueChangeEvent<LocalDateTime>>> valueChangeListeners,
			List<ReadonlyChangeListener> readonlyChangeListeners, InputAdaptersContainer<LocalDateTime> adapters) {
		super(component, contextLocaleOnAttachRegistration, localization, initialValue, valueChangeListeners,
				readonlyChangeListeners, adapters);
		this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ValidatableLocalDateTimeInputBuilder getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder#build()
	 */
	@Override
	public ValidatableInput<LocalDateTime> build() {
		return validatableInputConfigurator.configure(buildAsValidatableInput());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.holonplatform.
	 * core.Validator)
	 */
	@Override
	public ValidatableLocalDateTimeInputBuilder withValidator(Validator<LocalDateTime> validator) {
		validatableInputConfigurator.withValidator(validator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validationStatusHandler(com.
	 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
	 */
	@Override
	public ValidatableLocalDateTimeInputBuilder validationStatusHandler(
			ValidationStatusHandler<ValidatableInput<LocalDateTime>> validationStatusHandler) {
		validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
	 */
	@Override
	public ValidatableLocalDateTimeInputBuilder validateOnValueChange(boolean validateOnValueChange) {
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
	public ValidatableLocalDateTimeInputBuilder required(Validator<LocalDateTime> validator) {
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
	public ValidatableLocalDateTimeInputBuilder required(Localizable message) {
		validatableInputConfigurator.required(message);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public ValidatableLocalDateTimeInputBuilder required(boolean required) {
		validatableInputConfigurator.required(required);
		return this;
	}

}
