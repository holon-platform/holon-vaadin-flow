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

import com.holonplatform.core.Registration;
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
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEventNotifier;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultUserInputValidator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.data.value.HasValueChangeMode;

/**
 * Base adapter to convert an {@link Input} instance into a {@link ValidatableInput} one.
 * 
 * @param <T> Value type
 * @param <I> Input type
 *
 * @since 5.2.2
 */
public abstract class AbstractValidatableInputAdapter<T, I extends Input<T>> implements ValidatableInput<T> {

	private static final long serialVersionUID = -2291397152828158839L;

	/**
	 * Wrapped input
	 */
	private final I input;

	/**
	 * Validators
	 */
	private final List<Validator<T>> validators = new LinkedList<>();

	/**
	 * Validation status handler
	 */
	private ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler = ValidationStatusHandler.getDefault();

	/**
	 * Whether to validate the input when value changes
	 */
	private boolean validateOnValueChange;

	/**
	 * The validation listener registration reference
	 */
	private Registration validationListenerRegistration = null;

	/**
	 * Constructor.
	 * @param input Wrapped input (not null)
	 */
	public AbstractValidatableInputAdapter(I input) {
		super();
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		this.input = input;
		// check user input validation
		input.hasInvalidChangeEventNotifier().ifPresent(n -> {
			final DefaultUserInputValidator<T> uiv = new DefaultUserInputValidator<>();
			n.addInvalidChangeListener(uiv);
			addValidator(uiv);
		});
	}

	/**
	 * Get the wrapped input.
	 * @return the wrapped input
	 */
	protected I getInput() {
		return input;
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

	@Override
	public Registration addReadonlyChangeListener(ReadonlyChangeListener listener) {
		return input.addReadonlyChangeListener(listener);
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
	 * @see com.holonplatform.vaadin.flow.components.Input#hasInvalidChangeEventNotifier()
	 */
	@Override
	public Optional<InvalidChangeEventNotifier> hasInvalidChangeEventNotifier() {
		return input.hasInvalidChangeEventNotifier();
	}

	@Override
	public Optional<HasValueChangeMode> hasValueChangeMode() {
		return input.hasValueChangeMode();
	}

	@Override
	public <A> Optional<A> as(Class<A> type) {
		return input.as(type);
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

	/*
	 * (non-Javadoc)
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
		validate();
		return input.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		ValidatableInput.super.clear();
		// reset validation status
		getValidationStatusHandler()
				.ifPresent(vsh -> vsh.validationStatusChange(ValidationStatusEvent.unresolved(this)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<T, ValueChangeEvent<T>> listener) {
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
		// reset validation status
		getValidationStatusHandler()
				.ifPresent(vsh -> vsh.validationStatusChange(ValidationStatusEvent.unresolved(this)));
		// validate using validators
		validate(input.getValue());
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
	public void setValidationStatusHandler(ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidatableInput#getValidationStatusHandler()
	 */
	@Override
	public Optional<ValidationStatusHandler<ValidatableInput<T>>> getValidationStatusHandler() {
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

			final ValidationException validationException = (failures.size() == 1) ? failures.getFirst()
					: new ValidationException(failures.toArray(new ValidationException[0]));

			// INVALID: notify ValidationStatusHandler
			getValidationStatusHandler().ifPresent(vsh -> vsh.validationStatusChange(
					ValidationStatusEvent.invalid(this, validationException.getValidationMessages())));

			throw validationException;
		}

		// VALID: notify ValidationStatusHandler
		getValidationStatusHandler().ifPresent(vsh -> vsh.validationStatusChange(ValidationStatusEvent.valid(this)));
	}

}
