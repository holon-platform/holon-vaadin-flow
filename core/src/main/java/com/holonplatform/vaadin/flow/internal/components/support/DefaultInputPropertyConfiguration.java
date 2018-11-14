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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup.DefaultValueProvider;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;

/**
 * Default {@link InputPropertyConfiguration} implementation.
 *
 * @param <T> Property type
 * 
 * @since 5.2.0
 */
public class DefaultInputPropertyConfiguration<T> extends DefaultValueComponentPropertyConfiguration<T, Input<T>>
		implements InputPropertyConfiguration<T> {

	private boolean readOnly;
	private boolean required;
	private Localizable requiredMessage;
	private DefaultValueProvider<T> defaultValueProvider;
	private List<Validator<T>> validators = new LinkedList<>();
	private Validator<T> userInputValidator;
	private ValidationStatusHandler<PropertyInputGroup, T, Input<T>> validationStatusHandler;
	private List<ValueChangeListener<T>> valueChangeListeners = new LinkedList<>();

	public DefaultInputPropertyConfiguration(Property<T> property) {
		super(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#getRequiredMessage()
	 */
	@Override
	public Optional<Localizable> getRequiredMessage() {
		return Optional.ofNullable(requiredMessage);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#setRequiredMessage(com.
	 * holonplatform.core.i18n.Localizable)
	 */
	@Override
	public void setRequiredMessage(Localizable requiredMessage) {
		this.requiredMessage = requiredMessage;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#getDefaultValueProvider()
	 */
	@Override
	public Optional<DefaultValueProvider<T>> getDefaultValueProvider() {
		return Optional.ofNullable(defaultValueProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#setDefaultValueProvider(com.
	 * holonplatform.vaadin.flow.components.PropertyInputGroup.DefaultValueProvider)
	 */
	@Override
	public void setDefaultValueProvider(DefaultValueProvider<T> defaultValueProvider) {
		this.defaultValueProvider = defaultValueProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#getValidators()
	 */
	@Override
	public List<Validator<T>> getValidators() {
		return validators;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#addValidator(com.
	 * holonplatform.core.Validator)
	 */
	@Override
	public void addValidator(Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		this.validators.add(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#getUserInputValidator()
	 */
	@Override
	public Optional<Validator<T>> getUserInputValidator() {
		return Optional.ofNullable(userInputValidator);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#setUserInputValidator(com.
	 * holonplatform.core.Validator)
	 */
	@Override
	public void setUserInputValidator(Validator<T> validator) {
		this.userInputValidator = validator;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#getValidationStatusHandler()
	 */
	@Override
	public Optional<ValidationStatusHandler<PropertyInputGroup, T, Input<T>>> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#setValidationStatusHandler(
	 * com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
	 */
	@Override
	public void setValidationStatusHandler(
			ValidationStatusHandler<PropertyInputGroup, T, Input<T>> validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#getValueChangeListeners()
	 */
	@Override
	public List<ValueChangeListener<T>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration#addValueChangeListener(com.
	 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public void addValueChangeListener(ValueChangeListener<T> valueChangeListener) {
		ObjectUtils.argumentNotNull(valueChangeListener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(valueChangeListener);
	}

}
