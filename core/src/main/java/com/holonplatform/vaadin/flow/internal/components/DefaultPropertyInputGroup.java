/*
 * Copyright 2016-2017 Axioma srl.
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultGroupValidationStatusEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultGroupValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultUserInputValidator;
import com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfiguration;
import com.holonplatform.vaadin.flow.internal.components.support.InputPropertyConfigurationRegistry;
import com.holonplatform.vaadin.flow.internal.components.support.InputPropertyRegistry;
import com.holonplatform.vaadin.flow.internal.components.support.InputValidationStatus;
import com.holonplatform.vaadin.flow.internal.components.support.ValidationStatus;

/**
 * Default {@link PropertyInputGroup} implementation.
 *
 * @since 5.2.0
 */
public class DefaultPropertyInputGroup extends AbstractPropertySetGroup<Input<?>, PropertyInputGroup>
		implements PropertyInputGroup, GroupValidationStatusHandler<PropertyInputGroup, Property<?>, Input<?>> {

	private static final long serialVersionUID = -5441417959315472240L;

	protected final static Logger LOGGER = VaadinLogger.create();

	/**
	 * Property configurations
	 */
	private final InputPropertyConfigurationRegistry configuration = InputPropertyConfigurationRegistry.create();

	/**
	 * Property components
	 */
	private final InputPropertyRegistry components = InputPropertyRegistry.create();

	/**
	 * Validators
	 */
	private final List<Validator<PropertyBox>> validators = new LinkedList<>();

	/**
	 * Group validation status handler
	 */
	private GroupValidationStatusHandler<PropertyInputGroup, Property<?>, Input<?>> groupValidationStatusHandler;

	/**
	 * Group value validation status handler
	 */
	private ValidationStatusHandler<PropertyInputGroup> validationStatusHandler = ValidationStatusHandler.dialog();

	/**
	 * Whether to validate inputs at value change
	 */
	private boolean validateOnValueChange = false;

	/**
	 * Validation behaviour
	 */
	private boolean stopValidationAtFirstFailure = false;

	/**
	 * Overall validation behaviour
	 */
	private boolean stopOverallValidationAtFirstFailure = false;

	/**
	 * Refresh on value change
	 */
	private boolean enableRefreshOnValueChange = false;

	/**
	 * Constructor.
	 * @param propertySet The property set (not null)
	 */
	public DefaultPropertyInputGroup(PropertySet<?> propertySet) {
		super(propertySet);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractPropertySetGroup#getComponentGroup()
	 */
	@Override
	protected PropertyInputGroup getComponentGroup() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup#getBindings()
	 */
	@Override
	public Stream<Binding<Property<?>, Input<?>>> getBindings() {
		return components.stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup#getElement(java.lang.Object)
	 */
	@Override
	public Optional<Input<?>> getElement(Property<?> property) {
		return components.get(property).map(i -> i);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ComponentGroup#getElements()
	 */
	@Override
	public Stream<Input<?>> getElements() {
		return components.stream().map(b -> b.getElement());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyInputContainer#getInput(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		return components.get(property);
	}

	/**
	 * Get the {@link InputPropertyConfiguration} bound to given property.
	 * @param <T> Property type
	 * @param property The property for which to obtain the configuration (not null)
	 * @return The property configuration
	 */
	protected <T> InputPropertyConfiguration<T> getPropertyConfiguration(Property<T> property) {
		return configuration.get(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.InputGroup#clear()
	 */
	@Override
	public void clear() {
		setValue(null, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		// validate inputs
		validateInputs();
		// validate value
		validate(getValue(false));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractPropertySetGroup#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return getValue(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		final PropertyBox value = (getCurrentValue() != null) ? getCurrentValue()
				: PropertyBox.builder(getPropertySet()).invalidAllowed(true).build();
		flush(value, validate);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#getValueIfValid()
	 */
	@Override
	public Optional<PropertyBox> getValueIfValid() {
		try {
			return Optional.of(getValue(true));
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return Optional.empty();
		}
	}

	/**
	 * Writes the property bound {@link Input} components values, obtained through the {@link Input#getValue()} method,
	 * to given <code>propertyBox</code>,
	 * @param propertyBox the {@link PropertyBox} into which to write the property values (not null)
	 * @param validate whether to perform inputs and overall validation
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and validation fails
	 */
	private void flush(PropertyBox propertyBox, boolean validate) {
		ObjectUtils.argumentNotNull(propertyBox, "PropertyBox must be not null");

		if (validate) {
			// reset validation status
			resetValidationStatus();
		}

		if (validate) {
			// inputs validation
			validateInputs();
		}

		final boolean wasInvalidAllowed = propertyBox.isInvalidAllowed();
		propertyBox.setInvalidAllowed(true);
		// flush the Input values
		components.bindings().forEach(b -> {
			if (!b.getProperty().isReadOnly() && !b.getElement().isReadOnly()) { // exclude read-only properties
				propertyBox.setValue(b.getProperty(), b.getElement().getValue());
			}
		});
		if (!wasInvalidAllowed) {
			propertyBox.setInvalidAllowed(false);
		}

		if (validate) {
			// Overall validation
			validate(propertyBox);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.PropertyInputGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@Override
	public void setValue(PropertyBox propertyBox) {
		setValue(propertyBox, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@Override
	public void setValue(final PropertyBox propertyBox, boolean validate) {
		final PropertyBox oldValue = getCurrentValue();
		setCurrentValue(propertyBox);

		// reset validation status
		resetValidationStatus();

		// load values
		components.bindings().forEach(b -> {
			final Object value = getPropertyValue(propertyBox, b.getProperty())
					.orElseGet(() -> getDefaultValue(b.getProperty()));
			if (value == null) {
				b.getElement().clear();
			} else {
				b.getElement().setValue(value);
			}
		});

		// check validation
		if (validate) {
			validate();
		}

		// fire value change
		fireValueChange(oldValue);
	}

	/**
	 * Get the default value for given property, if available.
	 * @param <T> Property type
	 * @param property The property (not null)
	 * @return The property default value
	 */
	protected <T> T getDefaultValue(Property<T> property) {
		if (!property.isReadOnly()) {
			return configuration.get(property).getDefaultValueProvider()
					.map(defaultValueProvider -> defaultValueProvider.get()).orElse(null);
		}
		return null;
	}

	/**
	 * Get the value of given <code>property</code> using given <code>propertyBox</code>.
	 * @param <T> Property type
	 * @param propertyBox The PropertyBox value (may be null)
	 * @param property The property for which to obtain the value
	 * @return Optional property value, empty if the given PropertyBox was <code>null</code>
	 */
	protected <T> Optional<T> getPropertyValue(PropertyBox propertyBox, Property<T> property) {
		if (propertyBox != null) {
			// check vitual property
			if (VirtualProperty.class.isAssignableFrom(property.getClass())) {
				if (((VirtualProperty<T>) property).getValueProvider() != null) {
					return Optional.ofNullable(
							((VirtualProperty<T>) property).getValueProvider().getPropertyValue(propertyBox));
				}
				return null;
			}
			// get the property value
			if (propertyBox.containsValue(property)) {
				return Optional.ofNullable(propertyBox.getValue(property));
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValueIfPresent()
	 */
	@Override
	public Optional<PropertyBox> getValueIfPresent() {
		return getCurrentValueIfPresent().map(v -> getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return !getCurrentValueIfPresent().isPresent();
	}

	/**
	 * Get whether to validate inputs at value change.
	 * @return <code>true</code> to validate inputs at value change
	 */
	protected boolean isValidateOnValueChange() {
		return validateOnValueChange;
	}

	/**
	 * Set whether to validate inputs at value change.
	 * @param validateOnValueChange <code>true</code> to validate inputs at value change
	 */
	protected void setValidateOnValueChange(boolean validateOnValueChange) {
		this.validateOnValueChange = validateOnValueChange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		components.stream().map(b -> b.getElement()).forEach(i -> {
			if (i.hasEnabled().isPresent()) {
				i.hasEnabled().get().setEnabled(enabled);
			} else {
				i.getComponent().getElement().setEnabled(enabled);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		components.stream()
				.filter(b -> !b.getProperty().isReadOnly() && !configuration.get(b.getProperty()).isReadOnly())
				.map(b -> b.getElement()).forEach(c -> c.setReadOnly(readOnly));
	}

	/**
	 * Add an overall validator
	 * @param validator the {@link Validator} to add (not null)
	 */
	protected void addValidator(Validator<PropertyBox> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		validators.add(validator);
	}

	/**
	 * Get the overall {@link Validator}s.
	 * @return the overall validators
	 */
	protected List<Validator<PropertyBox>> getValidators() {
		return validators;
	}

	/**
	 * Get the {@link GroupValidationStatusHandler} to use.
	 * @return Optional group validation status handler
	 */
	protected Optional<GroupValidationStatusHandler<PropertyInputGroup, Property<?>, Input<?>>> getGroupValidationStatusHandler() {
		return Optional.ofNullable(groupValidationStatusHandler);
	}

	/**
	 * Set the {@link GroupValidationStatusHandler} to use.
	 * @param groupValidationStatusHandler the group validation status handler to set
	 */
	protected void setGroupValidationStatusHandler(
			GroupValidationStatusHandler<PropertyInputGroup, Property<?>, Input<?>> groupValidationStatusHandler) {
		this.groupValidationStatusHandler = groupValidationStatusHandler;
	}

	/**
	 * Set the overall {@link ValidationStatusHandler}.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	protected void setValidationStatusHandler(ValidationStatusHandler<PropertyInputGroup> validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/**
	 * Get the overall {@link ValidationStatusHandler}, if available.
	 * @return the optional overall {@link ValidationStatusHandler}
	 */
	protected Optional<ValidationStatusHandler<PropertyInputGroup>> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

	/**
	 * Get whether to stop validation at first validation failure.
	 * @return whether to stop validation at first validation failure
	 */
	protected boolean isStopValidationAtFirstFailure() {
		return stopValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop validation at first validation failure.
	 * @param stopValidationAtFirstFailure <code>true</code> to stop validation at first validation failure
	 */
	protected void setStopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
		this.stopValidationAtFirstFailure = stopValidationAtFirstFailure;
	}

	/**
	 * Get whether to stop overall validation at first validation failure.
	 * @return whether to stop overall validation at first validation failure
	 */
	protected boolean isStopOverallValidationAtFirstFailure() {
		return stopOverallValidationAtFirstFailure;
	}

	/**
	 * Set whether to stop overall validation at first validation failure.
	 * @param stopOverallValidationAtFirstFailure <code>true</code> to stop overall validation at first validation
	 *        failure
	 */
	protected void setStopOverallValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
		this.stopOverallValidationAtFirstFailure = stopOverallValidationAtFirstFailure;
	}

	/**
	 * Get whether to enable {@link VirtualProperty} input value refresh when any group input value changes.
	 * @return whether to enable {@link VirtualProperty} input value refresh when any group input value changes
	 */
	protected boolean isEnableRefreshOnValueChange() {
		return enableRefreshOnValueChange;
	}

	/**
	 * Set whether to enable {@link VirtualProperty} input value refresh when any group input value changes.
	 * @param enableRefreshOnValueChange whether to enable {@link VirtualProperty} input value refresh when any group
	 *        input value changes
	 */
	protected void setEnableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
		this.enableRefreshOnValueChange = enableRefreshOnValueChange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputBinder#refresh()
	 */
	@Override
	public void refresh() {
		final PropertyBox value = getValue(false);
		components.bindings().forEach(b -> {
			b.getElement().setValue((value != null) ? value.getValue(b.getProperty()) : null);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyInputBinder#refresh(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> boolean refresh(final Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		final PropertyBox value = getValue(false);
		return getInput(property).map(input -> {
			input.setValue((value != null) ? value.getValue(property) : null);
			return true;
		}).orElse(false);
	}

	/**
	 * Refresh all the input values bound to a {@link VirtualProperty}.
	 */
	protected void refreshVirtualProperties() {
		final PropertyBox value = getValue(false);
		components.bindings().filter(b -> b.getProperty() instanceof VirtualProperty).forEach(b -> {
			b.getElement().setValue((value != null) ? value.getValue(b.getProperty()) : null);
		});
	}

	/**
	 * Build and bind {@link Input}s to the properties of the property set.
	 */
	@SuppressWarnings("unchecked")
	protected void build() {
		components.clear();
		// render and bind components
		getPropertySet().stream().filter(property -> !configuration.get(property).isHidden())
				.forEach(property -> renderAndBind(property));
	}

	/**
	 * Render given property as a {@link Input} and register the binding.
	 * @param <T> Property type
	 * @param property The property to render and bind
	 */
	protected <T> void renderAndBind(final Property<T> property) {
		// configuration
		final InputPropertyConfiguration<T> propertyConfiguration = configuration.get(property);
		// render
		final Input<T> component = render(propertyConfiguration)
				// configure input
				.map(input -> configureInput(propertyConfiguration, input))
				// exception when Input not available
				.orElseThrow(() -> new NoSuitableRendererAvailableException(
						"No renderer available to render the property [" + property + "] as an Input"));
		// configure
		getPostProcessors().forEach(postProcessor -> postProcessor.accept(property, component));
		// register
		components.set(property, component);
	}

	/**
	 * Render given property configuration as a {@link ViewComponent}.
	 * @param <T> Property type
	 * @param propertyConfiguration Property configuration
	 * @return Optional rendered component
	 */
	@SuppressWarnings("unchecked")
	protected <T> Optional<Input<T>> render(InputPropertyConfiguration<T> propertyConfiguration) {
		// check custom renderer
		Optional<Input<T>> component = propertyConfiguration.getRenderer()
				.map(r -> r.render(propertyConfiguration.getProperty()));
		if (component.isPresent()) {
			return component;
		}
		// check specific registry
		if (getPropertyRendererRegistry().isPresent()) {
			return getPropertyRendererRegistry().get().getRenderer(Input.class, propertyConfiguration.getProperty())
					.map(r -> r.render(propertyConfiguration.getProperty()));
		} else {
			// use default
			return propertyConfiguration.getProperty().renderIfAvailable(Input.class).map(c -> (Input<T>) c);
		}
	}

	/**
	 * Configure the {@link Input} component using given configuration.
	 * @param <T> Property type
	 * @param configuration Property configuration (not null)
	 * @param input The {@link Input} component to configure
	 * @return The configured {@link Input}
	 */
	protected <T> Input<T> configureInput(final InputPropertyConfiguration<T> configuration, final Input<T> input) {
		// value change listeners
		configuration.getValueChangeListeners().forEach(vcl -> input.addValueChangeListener(e -> {
			vcl.valueChange(new DefaultGroupValueChangeEvent<>(getComponentGroup(), e.getSource(), e.getOldValue(),
					e.getValue(), e.isUserOriginated(), configuration.getProperty(), input));
		}));
		// Read-only
		if (configuration.getProperty().isReadOnly() || configuration.isReadOnly()) {
			input.setReadOnly(true);
			// validators ignored for read-only properties
			if (!configuration.getValidators().isEmpty()) {
				LOGGER.warn("Property [" + configuration.getProperty() + "] is read-only, validators will be ignored");
			}
		} else {
			// validation
			if (configuration.isRequired()) {
				input.setRequired(true);
			}
			// Validate on value change
			if (isValidateOnValueChange()) {
				input.addValueChangeListener(e -> validateOnValueChange(configuration.getProperty(), e.getValue()));
			}
			// Refresh on value change
			if (isEnableRefreshOnValueChange()) {
				input.addValueChangeListener(e -> refreshVirtualProperties());
			}
			// default validation status handler
			if (!configuration.getValidationStatusHandler().isPresent()) {
				configuration.setValidationStatusHandler(ValidationStatusHandler.getDefault());
			}
			// check invalid user originated events
			input.hasInvalidChangeEventNotifier().ifPresent(n -> {
				final DefaultUserInputValidator<T> uiv = new DefaultUserInputValidator<>();
				n.addInvalidChangeListener(uiv);
				configuration.setUserInputValidator(uiv);
			});
		}
		return input;
	}

	/**
	 * Validate the group value.
	 * @param value The value to validate
	 * @throws ValidationException If validation fails
	 */
	protected void validate(PropertyBox value) throws ValidationException {
		final ValidationStatus validation = getGroupValidation(value);
		// notify
		notifyValidationStatusChange(new DefaultGroupValidationStatusEvent<>(getComponentGroup(),
				validation.getStatus(), validation.getErrors(),
				components.bindings()
						.map(b -> GroupElementValidationStatusEvent.<PropertyInputGroup, Property<?>, Input<?>>valid(
								getComponentGroup(), b.getProperty(), b.getElement()))
						.collect(Collectors.toList())));
		// check validation exceptions
		if (!validation.getValidationExceptions().isEmpty()) {
			throw (validation.getValidationExceptions().size() == 1) ? validation.getValidationExceptions().get(0)
					: new ValidationException(validation.getValidationExceptions());
		}
	}

	/**
	 * Group value validation.
	 * @param value The value to validate
	 * @return The group validation status
	 */
	protected ValidationStatus getGroupValidation(PropertyBox value) {
		final LinkedList<ValidationException> failures = new LinkedList<>();
		// invoke group validators
		for (Validator<PropertyBox> validator : getValidators()) {
			try {
				validator.validate(value);
			} catch (ValidationException ve) {
				failures.add(ve);
				if (isStopOverallValidationAtFirstFailure()) {
					break;
				}
			}
		}
		// check validation failed
		if (!failures.isEmpty()) {
			return ValidationStatus.invalid(failures);
		}
		// validation succeeded
		return ValidationStatus.valid();
	}

	/**
	 * Validate the group inputs.
	 * @throws ValidationException If validation fails
	 */
	protected void validateInputs() throws ValidationException {
		final List<ValidationException> failures = new LinkedList<>();
		final List<GroupElementValidationStatusEvent<PropertyInputGroup, Property<?>, Input<?>>> inputsValidationStatus = new LinkedList<>();
		for (Entry<Property<?>, InputValidationStatus> e : getInputsValidation().entrySet()) {
			if (e.getValue().isInvalid()) {
				failures.addAll(e.getValue().getValidationExceptions());
			}
			inputsValidationStatus.add(asValidationStatusEvent(e.getKey(), e.getValue()));
		}
		// notify
		notifyValidationStatusChange(new DefaultGroupValidationStatusEvent<>(getComponentGroup(), Status.UNRESOLVED,
				Collections.emptyList(), inputsValidationStatus));
		// check validation exceptions
		if (!failures.isEmpty()) {
			throw (failures.size() == 1) ? failures.get(0) : new ValidationException(failures);
		}
	}

	/**
	 * Validate all the group {@link Input}s.
	 * @return The validation status of each property
	 */
	protected Map<Property<?>, InputValidationStatus> getInputsValidation() {
		final Map<Property<?>, InputValidationStatus> validations = new LinkedHashMap<>(components.size());
		for (Binding<Property<Object>, Input<Object>> b : components.bindings().collect(Collectors.toList())) {
			// exclude read-only
			if (!b.getElement().isReadOnly()) {
				// validate property
				final Optional<InputValidationStatus> validation = validateProperty(b.getProperty(),
						b.getElement().getValue());
				validation.ifPresent(v -> validations.put(b.getProperty(), v));
				if (isStopValidationAtFirstFailure() && validation.map(v -> v.isInvalid()).orElse(false)) {
					// break if stop validation at first failure
					break;
				}
			}
		}
		return validations;
	}

	/**
	 * Validate the input bound to the given property.
	 * @param <T> Property type
	 * @param property Property to validate
	 * @param value Value to validate
	 * @return Optional property validation status, empty if a bound Input is not avaialble
	 */
	protected <T> Optional<InputValidationStatus> validateProperty(Property<T> property, T value) {
		return getInput(property).map(input -> {
			final LinkedList<ValidationException> failures = new LinkedList<>();
			// required
			if (input.isRequired()) {
				RequiredInputValidator<T> requiredValidator = configuration.get(property).getRequiredMessage()
						.map(rm -> RequiredInputValidator.create(input, rm))
						.orElseGet(() -> RequiredInputValidator.create(input));
				try {
					requiredValidator.validate(value);
				} catch (ValidationException e) {
					failures.add(e);
				}
			}
			// user input
			configuration.get(property).getUserInputValidator().ifPresent(v -> {
				try {
					v.validate(value);
				} catch (ValidationException e) {
					failures.add(e);
				}
			});
			// property validators
			property.getValidators().forEach(v -> {
				try {
					v.validate(value);
				} catch (ValidationException e) {
					failures.add(e);
				}
			});
			// input validators
			configuration.get(property).getValidators().forEach(v -> {
				try {
					v.validate(value);
				} catch (ValidationException e) {
					failures.add(e);
				}
			});
			if (!failures.isEmpty()) {
				// validation failed
				return InputValidationStatus.invalid(input, failures);
			}
			// validation succeeded
			return InputValidationStatus.valid(input);
		});
	}

	/**
	 * Reset the group validation status, notifying a {@link Status#UNRESOLVED} to validation status handlers.
	 */
	protected void resetValidationStatus() {
		notifyValidationStatusChange(
				new DefaultGroupValidationStatusEvent<>(getComponentGroup(), Status.UNRESOLVED, Collections.emptyList(),
						components.bindings()
								.map(b -> GroupElementValidationStatusEvent
										.<PropertyInputGroup, Property<?>, Input<?>>unresolved(getComponentGroup(),
												b.getProperty(), b.getElement()))
								.collect(Collectors.toList())));
	}

	/**
	 * Notify a group validation status change.
	 * @param statusChangeEvent Group validation status change event
	 */
	protected void notifyValidationStatusChange(
			GroupValidationStatusEvent<PropertyInputGroup, Property<?>, Input<?>> statusChangeEvent) {
		ObjectUtils.argumentNotNull(statusChangeEvent, "GroupValidationStatusEvent must be not null");
		getGroupValidationStatusHandler().orElse(this).validationStatusChange(statusChangeEvent);
	}

	/**
	 * Translate given validation status into a {@link ValidationStatusEvent}.
	 * @param property Validation property
	 * @param validation Validation status
	 * @return The {@link ValidationStatusEvent}
	 */
	protected GroupElementValidationStatusEvent<PropertyInputGroup, Property<?>, Input<?>> asValidationStatusEvent(
			Property<?> property, InputValidationStatus validation) {
		switch (validation.getStatus()) {
		case INVALID:
			return GroupElementValidationStatusEvent.invalid(getComponentGroup(), property, validation.getInput(),
					validation.getErrors());
		case VALID:
			return GroupElementValidationStatusEvent.valid(getComponentGroup(), property, validation.getInput());
		case UNRESOLVED:
		default:
			return GroupElementValidationStatusEvent.unresolved(getComponentGroup(), property, validation.getInput());
		}
	}

	/**
	 * Validate the input bound to given property when input value changes.
	 * @param <T> Property type
	 * @param property The property
	 * @param value The new value
	 */
	protected <T> void validateOnValueChange(Property<T> property, T value) {
		validateProperty(property, value).ifPresent(validation -> {
			notifyValidationStatusChange(new DefaultGroupValidationStatusEvent<>(getComponentGroup(), Status.UNRESOLVED,
					Collections.emptyList(), Collections.singletonList(asValidationStatusEvent(property, validation))));
		});
	}

	/**
	 * Default group validation status handling.
	 * @param statusChangeEvent Group status change event
	 */
	@Override
	public void validationStatusChange(
			GroupValidationStatusEvent<PropertyInputGroup, Property<?>, Input<?>> statusChangeEvent) {
		// group elements
		statusChangeEvent.getInputsValidationStatus().forEach(s -> {
			configuration.get(s.getProperty()).getValidationStatusHandler().ifPresent(h -> h.validationStatusChange(
					ValidationStatusEvent.create(s.getElement(), s.getStatus(), s.getErrors())));
		});
		// group
		getValidationStatusHandler().ifPresent(h -> {
			switch (statusChangeEvent.getGroupStatus()) {
			case INVALID:
				h.validationStatusChange(
						ValidationStatusEvent.invalid(getComponentGroup(), statusChangeEvent.getGroupErrors()));
				break;
			case VALID:
				h.validationStatusChange(ValidationStatusEvent.valid(getComponentGroup()));
				break;
			case UNRESOLVED:
			default:
				h.validationStatusChange(ValidationStatusEvent.unresolved(getComponentGroup()));
				break;
			}
		});
	}

	// ------- Builders

	/**
	 * {@link PropertyInputGroup} builder.
	 */
	static class InternalBuilder extends AbstractBuilder<InternalBuilder> {

		public <P extends Property<?>> InternalBuilder(Iterable<P> properties) {
			super(properties);
		}

		@Override
		protected InternalBuilder builder() {
			return this;
		}

		public DefaultPropertyInputGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Default {@link PropertyInputGroupBuilder} implementation.
	 */
	public static class DefaultBuilder extends AbstractBuilder<PropertyInputGroupBuilder>
			implements PropertyInputGroupBuilder {

		public <P extends Property<?>> DefaultBuilder(Iterable<P> properties) {
			super(properties);
		}

		@Override
		protected PropertyInputGroupBuilder builder() {
			return this;
		}

		@Override
		public PropertyInputGroup build() {
			instance.build();
			return instance;
		}

	}

	/**
	 * Abstract configurator.
	 * 
	 * @param <B> Concrete builder type
	 */
	public abstract static class AbstractBuilder<B extends PropertyInputGroupConfigurator<B>>
			implements PropertyInputGroupConfigurator<B> {

		protected final DefaultPropertyInputGroup instance;

		public <P extends Property<?>> AbstractBuilder(Iterable<P> properties) {
			super();
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			this.instance = new DefaultPropertyInputGroup(
					(properties instanceof PropertySet<?>) ? (PropertySet<?>) properties : PropertySet.of(properties));
		}

		/**
		 * Actual builder
		 * @return Builder
		 */
		protected abstract B builder();

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#readOnly(com.holonplatform.
		 * core.property.Property, boolean)
		 */
		@Override
		public <T> B readOnly(Property<T> property, boolean readOnly) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setReadOnly(readOnly);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public B required(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setRequired(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public B required(Property<?> property, Localizable message) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setRequired(true);
			instance.getPropertyConfiguration(property).setRequiredMessage(message);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B hidden(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.getPropertyConfiguration(property).setHidden(true);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.Property,
		 * com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> B bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "Renderer must be not null");
			instance.getPropertyConfiguration(property).setRenderer(renderer);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#defaultValue(com.holonplatform.core.property
		 * .Property, com.holonplatform.vaadin.components.PropertyInputGroup.DefaultValueProvider)
		 */
		@Override
		public <T> B defaultValue(Property<T> property, Supplier<T> defaultValueProvider) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(defaultValueProvider, "The default value provider function must be not null");
			instance.getPropertyConfiguration(property).setDefaultValueProvider(defaultValueProvider);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> B withValidator(Property<T> property, Validator<? super T> validator) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validator, "Validator must be not null");
			instance.getPropertyConfiguration(property).addValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#groupValidationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.GroupValidationStatusHandler)
		 */
		@Override
		public B groupValidationStatusHandler(
				GroupValidationStatusHandler<PropertyInputGroup, Property<?>, Input<?>> groupValidationStatusHandler) {
			ObjectUtils.argumentNotNull(groupValidationStatusHandler, "GroupValidationStatusHandler must be not null");
			instance.setGroupValidationStatusHandler(groupValidationStatusHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform.core
		 * .property.Property, com.holonplatform.vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public B validationStatusHandler(Property<?> property,
				ValidationStatusHandler<Input<?>> validationStatusHandler) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validationStatusHandler, "ValidationStatusHandler must be not null");
			instance.getPropertyConfiguration(property).setValidationStatusHandler(validationStatusHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform.
		 * vaadin.components.ValidationStatusHandler)
		 */
		@Override
		public B validationStatusHandler(ValidationStatusHandler<PropertyInputGroup> validationStatusHandler) {
			instance.setValidationStatusHandler(validationStatusHandler);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public B withValidator(Validator<PropertyBox> validator) {
			instance.addValidator(validator);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#validateOnValueChange(boolean)
		 */
		@Override
		public B validateOnValueChange(boolean validateOnValueChange) {
			instance.setValidateOnValueChange(validateOnValueChange);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public B stopInputsValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			instance.setStopValidationAtFirstFailure(stopValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyInputGroup.Builder#setStopOverallValidationAtFirstFailure(
		 * boolean)
		 */
		@Override
		public B stopGroupValidationAtFirstFailure(boolean stopOverallValidationAtFirstFailure) {
			instance.setStopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyInputGroup.Builder#withPostProcessor(com.holonplatform.vaadin.
		 * components.PropertyBinding.PostProcessor)
		 */
		@Override
		public B withPostProcessor(BiConsumer<Property<?>, Input<?>> postProcessor) {
			ObjectUtils.argumentNotNull(postProcessor, "PostProcessor must be not null");
			instance.addPostProcessor(postProcessor);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.PropertyGroupConfigurator#withValueChangeListener(com.
		 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public B withValueChangeListener(
				ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, Input<?>, PropertyInputGroup>> listener) {
			ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
			instance.addValueChangeListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#withValueChangeListener(com.
		 * holonplatform.core.property.Property,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public <T> B withValueChangeListener(Property<T> property,
				ValueChangeListener<T, GroupValueChangeEvent<T, Property<?>, Input<?>, PropertyInputGroup>> listener) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
			instance.getPropertyConfiguration(property).addValueChangeListener(listener);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#usePropertyRendererRegistry(com.
		 * holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public B usePropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry) {
			instance.setPropertyRendererRegistry(propertyRendererRegistry);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#enableRefreshOnValueChange(
		 * boolean)
		 */
		@Override
		public B enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
			instance.setEnableRefreshOnValueChange(enableRefreshOnValueChange);
			return builder();
		}

	}

}
