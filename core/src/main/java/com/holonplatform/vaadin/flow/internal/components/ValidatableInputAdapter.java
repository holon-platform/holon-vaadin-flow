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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.beans.Validators;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValidationStatusEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.shared.Registration;

/**
 * Adapter to convert an {@link Input} instance into a {@link ValidatableInput} one.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class ValidatableInputAdapter<T> implements ValidatableInput<T> {

	private static final long serialVersionUID = -2291397152828158839L;

	/**
	 * Wrapped input
	 */
	private final Input<T> input;

	/**
	 * Validators
	 */
	private final List<Validator<T>> validators = new LinkedList<>();

	/**
	 * Validation status handler
	 */
	private ValidationStatusHandler<T> validationStatusHandler = ValidationStatusHandler.getDefault();

	/**
	 * Whether to validate the input when value changes
	 */
	private boolean validateOnValueChange;

	/**
	 * The validation listener registration reference
	 */
	private Registration validationListenerRegistration = null;

	/**
	 * Constructor
	 * @param input Wrapped input
	 */
	public ValidatableInputAdapter(Input<T> input) {
		super();
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		this.input = input;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		input.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return input.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return input.isRequired();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		input.setRequired(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveLabel#hasLabel()
	 */
	@Override
	public Optional<HasLabel> hasLabel() {
		return input.hasLabel();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveTitle#hasTitle()
	 */
	@Override
	public Optional<HasTitle> hasTitle() {
		return input.hasTitle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHavePlaceholder#hasPlaceholder()
	 */
	@Override
	public Optional<HasPlaceholder> hasPlaceholder() {
		return input.hasPlaceholder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		input.setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return input.isVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasEnabled()
	 */
	@Override
	public Optional<HasEnabled> hasEnabled() {
		return input.hasEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasStyle()
	 */
	@Override
	public Optional<HasStyle> hasStyle() {
		return input.hasStyle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasSize()
	 */
	@Override
	public Optional<HasSize> hasSize() {
		return input.hasSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#hasValidation()
	 */
	@Override
	public Optional<HasValidation> hasValidation() {
		return input.hasValidation();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		input.focus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public T getEmptyValue() {
		return input.getEmptyValue();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return input.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		input.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#getValue()
	 */
	@Override
	public T getValue() {
		return input.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValidatableInput#getValueIfValid()
	 */
	@Override
	public T getValueIfValid() {
		validate();
		return getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		ValidatableInput.super.clear();
		// notify ValidationStatusHandler
		getValidationStatusHandler().ifPresent(vsh -> vsh
				.validationStatusChange(new DefaultValidationStatusEvent<>(Status.UNRESOLVED, null, this, null)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<T> listener) {
		return input.addValueChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return input.getComponent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		// check HasValidation
		Optional<ValidationException> ve = hasValidation().filter(v -> v.isInvalid()).map(v -> new ValidationException(
				(v.getErrorMessage() != null) ? v.getErrorMessage() : "Invalid input value"));
		if (ve.isPresent()) {
			throw ve.get();
		}
		// validate using validators
		validate(getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#addValidator(com.holonplatform.core.Validator)
	 */
	@Override
	public Registration addValidator(Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		validators.add(validator);
		return () -> validators.remove(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#setValidateOnValueChange(boolean)
	 */
	@Override
	public void setValidateOnValueChange(boolean validateOnValueChange) {
		this.validateOnValueChange = validateOnValueChange;
		if (validateOnValueChange) {
			if (validationListenerRegistration == null) {
				this.validationListenerRegistration = this.input.addValueChangeListener(e -> {
					try {
						validate(e.getValue());
					} catch (@SuppressWarnings("unused") ValidationException ve) {
						// swallow
					}
				});
			}
		} else {
			if (validationListenerRegistration != null) {
				validationListenerRegistration.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#isValidateOnValueChange()
	 */
	@Override
	public boolean isValidateOnValueChange() {
		return validateOnValueChange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#setValidationStatusHandler(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler)
	 */
	@Override
	public void setValidationStatusHandler(ValidationStatusHandler<T> validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#getValidationStatusHandler()
	 */
	@Override
	public Optional<ValidationStatusHandler<T>> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

	/**
	 * Get all registered {@link Validator}s.
	 * @return the registered validators, an empty {@link List} if none
	 */
	protected List<Validator<T>> getValidators() {
		return validators;
	}

	/**
	 * Validate given value using registered {@link Validators}.
	 * @param value Value to validate
	 * @throws ValidationException If the value is not valid
	 */
	protected void validate(T value) throws ValidationException {

		LinkedList<ValidationException> failures = new LinkedList<>();
		for (Validator<T> validator : getValidators()) {
			try {
				validator.validate(value);
			} catch (ValidationException ve) {
				failures.add(ve);
			}
		}

		// collect validation exceptions, if any
		if (!failures.isEmpty()) {

			ValidationException validationException = (failures.size() == 1) ? failures.getFirst()
					: new ValidationException(failures.toArray(new ValidationException[0]));

			// notify ValidationStatusHandler
			getValidationStatusHandler()
					.ifPresent(vsh -> vsh.validationStatusChange(new DefaultValidationStatusEvent<>(Status.INVALID,
							validationException.getValidationMessages(), this, null)));

			throw validationException;
		}

		// VALID: notify ValidationStatusHandler
		getValidationStatusHandler().ifPresent(
				vsh -> vsh.validationStatusChange(new DefaultValidationStatusEvent<>(Status.VALID, null, this, null)));
	}

}
