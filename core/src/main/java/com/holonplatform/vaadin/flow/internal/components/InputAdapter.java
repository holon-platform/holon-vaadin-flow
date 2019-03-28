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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEventNotifier;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.support.RegistrationAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.HasValueChangeMode;

/**
 * Adapter to use a {@link HasValue} {@link Component} as an {@link Input}.
 * 
 * @param <T> Value type
 * @param <V> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 * 
 * @since 5.2.0
 */
public class InputAdapter<T, V extends HasValue<?, T>, C extends Component> implements Input<T> {

	private static final long serialVersionUID = -2456516308895591627L;

	/**
	 * Wrapped field
	 */
	private final V field;

	/**
	 * Field component
	 */
	private final C component;

	/**
	 * Overridden operations
	 */
	private Function<V, T> emptyValueSupplier;
	private Function<V, Boolean> isEmptySupplier;
	private Function<V, T> valueSupplier;
	private Consumer<V> focusOperation;
	private Function<V, HasSize> hasSizeSupplier;
	private Function<V, HasStyle> hasStyleSupplier;
	private Function<V, HasEnabled> hasEnabledSupplier;
	private Function<V, HasValueChangeMode> hasValueChangeModeSupplier;
	private Function<V, HasValidation> hasValidationSupplier;
	private Function<V, InvalidChangeEventNotifier> invalidChangeEventNotifierSupplier;

	/**
	 * Property handlers
	 */
	private PropertyHandler<Boolean, T, V, C> requiredPropertyHandler;
	private PropertyHandler<String, T, V, C> labelPropertyHandler;
	private PropertyHandler<String, T, V, C> titlePropertyHandler;
	private PropertyHandler<String, T, V, C> placeholderPropertyHandler;

	/**
	 * Adapters
	 */
	private final InputAdaptersContainer<T> adapters = InputAdaptersContainer.create();

	/**
	 * Default constructor.
	 * @param field The {@link HasValue} instance (not null)
	 * @param component The {@link Component} instance (not null)
	 */
	public InputAdapter(V field, C component) {
		super();
		ObjectUtils.argumentNotNull(field, "HasValue must be not null");
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		this.field = field;
		this.component = component;
		this.requiredPropertyHandler = tryToObtainRequiredPropertyHandler(component);
		this.labelPropertyHandler = tryToObtainLabelPropertyHandler(component);
		this.titlePropertyHandler = tryToObtainTitlePropertyHandler(component);
		this.placeholderPropertyHandler = tryToObtainPlaceholderPropertyHandler(component);
	}

	/**
	 * Get the empty value supplier, if available.
	 * @return Optional empty value supplier
	 */
	public Optional<Function<V, T>> getEmptyValueSupplier() {
		return Optional.ofNullable(emptyValueSupplier);
	}

	/**
	 * Set the empty value supplier.
	 * @param emptyValueSupplier the empty value supplier to set
	 */
	public void setEmptyValueSupplier(Function<V, T> emptyValueSupplier) {
		this.emptyValueSupplier = emptyValueSupplier;
	}

	/**
	 * Get the <em>is empty</em> value supplier, if available.
	 * @return Optional <em>is empty</em> value supplier
	 */
	public Optional<Function<V, Boolean>> getIsEmptySupplier() {
		return Optional.ofNullable(isEmptySupplier);
	}

	/**
	 * Set the <em>is empty</em> value supplier.
	 * @param isEmptySupplier the <em>is empty</em> value supplier to set
	 */
	public void setIsEmptySupplier(Function<V, Boolean> isEmptySupplier) {
		this.isEmptySupplier = isEmptySupplier;
	}

	/**
	 * Get the Input value supplier, if available.
	 * @return Optional Input value supplier
	 */
	public Optional<Function<V, T>> getValueSupplier() {
		return Optional.ofNullable(valueSupplier);
	}

	/**
	 * Set the Input value supplier.
	 * @param valueSupplier the Input value supplier to set
	 */
	public void setValueSupplier(Function<V, T> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	/**
	 * Get the <code>focus</code> operation.
	 * @return Optional <code>focus</code> operation
	 */
	public Optional<Consumer<V>> getFocusOperation() {
		return Optional.ofNullable(focusOperation);
	}

	/**
	 * Set the <code>focus</code> operation.
	 * @param focusOperation the operation to set
	 */
	public void setFocusOperation(Consumer<V> focusOperation) {
		this.focusOperation = focusOperation;
	}

	/**
	 * Get the {@link HasSize} supplier.
	 * @return Optional {@link HasSize} supplier
	 */
	public Optional<Function<V, HasSize>> getHasSizeSupplier() {
		return Optional.ofNullable(hasSizeSupplier);
	}

	/**
	 * Set the {@link HasSize} supplier.
	 * @param hasSizeSupplier the supplier to set
	 */
	public void setHasSizeSupplier(Function<V, HasSize> hasSizeSupplier) {
		this.hasSizeSupplier = hasSizeSupplier;
	}

	/**
	 * Get the {@link HasStyle} supplier.
	 * @return Optional {@link HasStyle} supplier
	 */
	public Optional<Function<V, HasStyle>> getHasStyleSupplier() {
		return Optional.ofNullable(hasStyleSupplier);
	}

	/**
	 * Set the {@link HasStyle} supplier.
	 * @param hasStyleSupplier the supplier to set
	 */
	public void setHasStyleSupplier(Function<V, HasStyle> hasStyleSupplier) {
		this.hasStyleSupplier = hasStyleSupplier;
	}

	/**
	 * Get the {@link HasEnabled} supplier.
	 * @return Optional {@link HasEnabled} supplier
	 */
	public Optional<Function<V, HasEnabled>> getHasEnabledSupplier() {
		return Optional.ofNullable(hasEnabledSupplier);
	}

	/**
	 * Set the {@link HasEnabled} supplier.
	 * @param hasEnabledSupplier the supplier to set
	 */
	public void setHasEnabledSupplier(Function<V, HasEnabled> hasEnabledSupplier) {
		this.hasEnabledSupplier = hasEnabledSupplier;
	}

	/**
	 * Get the {@link HasValueChangeMode} supplier.
	 * @return Optional {@link HasValueChangeMode} supplier
	 */
	public Optional<Function<V, HasValueChangeMode>> getHasValueChangeModeSupplier() {
		return Optional.ofNullable(hasValueChangeModeSupplier);
	}

	/**
	 * Set the {@link HasValueChangeMode} supplier.
	 * @param hasValueChangeModeSupplier the supplier to set
	 */
	public void setHasValueChangeModeSupplier(Function<V, HasValueChangeMode> hasValueChangeModeSupplier) {
		this.hasValueChangeModeSupplier = hasValueChangeModeSupplier;
	}

	/**
	 * Get the {@link HasValidation} supplier.
	 * @return Optional {@link HasValidation} supplier
	 */
	public Optional<Function<V, HasValidation>> getHasValidationSupplier() {
		return Optional.ofNullable(hasValidationSupplier);
	}

	/**
	 * Set the {@link HasValidation} supplier.
	 * @param hasValidationSupplier the supplier to set
	 */
	public void setHasValidationSupplier(Function<V, HasValidation> hasValidationSupplier) {
		this.hasValidationSupplier = hasValidationSupplier;
	}

	/**
	 * Get the {@link InvalidChangeEventNotifier} supplier.
	 * @return Optional InvalidChangeEventNotifier} supplier
	 */
	public Optional<Function<V, InvalidChangeEventNotifier>> getInvalidChangeEventNotifierSupplier() {
		return Optional.ofNullable(invalidChangeEventNotifierSupplier);
	}

	/**
	 * Set the {@link InvalidChangeEventNotifier} supplier.
	 * @param invalidChangeEventNotifierSupplier the supplier to set
	 */
	public void setInvalidChangeEventNotifierSupplier(
			Function<V, InvalidChangeEventNotifier> invalidChangeEventNotifierSupplier) {
		this.invalidChangeEventNotifierSupplier = invalidChangeEventNotifierSupplier;
	}

	/**
	 * Get the <code>required</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<Boolean, T, V, C>> getRequiredPropertyHandler() {
		return Optional.ofNullable(requiredPropertyHandler);
	}

	/**
	 * Set the <code>required</code> property handler.
	 * @param requiredPropertyHandler the property handler to set
	 */
	public void setRequiredPropertyHandler(PropertyHandler<Boolean, T, V, C> requiredPropertyHandler) {
		this.requiredPropertyHandler = requiredPropertyHandler;
	}

	/**
	 * Get the <code>label</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<String, T, V, C>> getLabelPropertyHandler() {
		return Optional.ofNullable(labelPropertyHandler);
	}

	/**
	 * Set the <code>label</code> property handler.
	 * @param labelPropertyHandler the property handler to set
	 */
	public void setLabelPropertyHandler(PropertyHandler<String, T, V, C> labelPropertyHandler) {
		this.labelPropertyHandler = labelPropertyHandler;
	}

	/**
	 * Get the <code>title</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<String, T, V, C>> getTitlePropertyHandler() {
		return Optional.ofNullable(titlePropertyHandler);
	}

	/**
	 * Set the <code>title</code> property handler.
	 * @param titlePropertyHandler the property handler to set
	 */
	public void setTitlePropertyHandler(PropertyHandler<String, T, V, C> titlePropertyHandler) {
		this.titlePropertyHandler = titlePropertyHandler;
	}

	/**
	 * Get the <code>placeholder</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<String, T, V, C>> getPlaceholderPropertyHandler() {
		return Optional.ofNullable(placeholderPropertyHandler);
	}

	/**
	 * Set the <code>placeholder</code> property handler.
	 * @param placeholderPropertyHandler the property handler to set
	 */
	public void setPlaceholderPropertyHandler(PropertyHandler<String, T, V, C> placeholderPropertyHandler) {
		this.placeholderPropertyHandler = placeholderPropertyHandler;
	}

	/**
	 * Get the {@link HasValue} field.
	 * @return the {@link HasValue} field
	 */
	public V getField() {
		return field;
	}

	/**
	 * Get the Input {@link Component}.
	 * @return the Input {@link Component}
	 */
	public C getInputComponent() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public T getEmptyValue() {
		if (getEmptyValueSupplier().isPresent()) {
			return getEmptyValueSupplier().get().apply(getField());
		}
		return getField().getEmptyValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		getField().setValue(processValueToSet(value));
	}

	/**
	 * Process the value before set it in the field.
	 * @param value Value to process
	 * @return Processed value
	 */
	protected T processValueToSet(T value) {
		if (value == null && getField().getEmptyValue() != null) {
			return getField().getEmptyValue();
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValue()
	 */
	@Override
	public T getValue() {
		if (getValueSupplier().isPresent()) {
			return getValueSupplier().get().apply(getField());
		}
		return getField().getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (getIsEmptySupplier().isPresent()) {
			return getIsEmptySupplier().get().apply(getField());
		}
		return getField().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#clear()
	 */
	@Override
	public void clear() {
		getField().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		getField().setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return getField().isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return getRequiredPropertyHandler().map(h -> h.apply(getField(), getInputComponent()))
				.orElseGet(() -> getField().isRequiredIndicatorVisible());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		if (getRequiredPropertyHandler().isPresent()) {
			getRequiredPropertyHandler().get().accept(getField(), getInputComponent(), required);
		} else {
			getField().setRequiredIndicatorVisible(required);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveLabel#hasLabel()
	 */
	@Override
	public Optional<HasLabel> hasLabel() {
		return getLabelPropertyHandler().map(h -> HasLabel.create(() -> h.apply(getField(), getInputComponent()),
				v -> h.accept(getField(), getInputComponent(), v)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveTitle#hasTitle()
	 */
	@Override
	public Optional<HasTitle> hasTitle() {
		return getTitlePropertyHandler().map(h -> HasTitle.create(() -> h.apply(getField(), getInputComponent()),
				v -> h.accept(getField(), getInputComponent(), v)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHavePlaceholder#hasPlaceholder()
	 */
	@Override
	public Optional<HasPlaceholder> hasPlaceholder() {
		return getPlaceholderPropertyHandler()
				.map(h -> HasPlaceholder.create(() -> h.apply(getField(), getInputComponent()),
						v -> h.accept(getField(), getInputComponent(), v)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasEnabled()
	 */
	@Override
	public Optional<HasEnabled> hasEnabled() {
		if (getHasEnabledSupplier().isPresent()) {
			return getHasEnabledSupplier().map(s -> s.apply(getField()));
		}
		return Input.super.hasEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueComponent#hasValueChangeMode()
	 */
	@Override
	public Optional<HasValueChangeMode> hasValueChangeMode() {
		if (getHasValueChangeModeSupplier().isPresent()) {
			return getHasValueChangeModeSupplier().map(s -> s.apply(getField()));
		}
		return Input.super.hasValueChangeMode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#hasValidation()
	 */
	@Override
	public Optional<HasValidation> hasValidation() {
		if (getHasValidationSupplier().isPresent()) {
			return getHasValidationSupplier().map(s -> s.apply(getField()));
		}
		return Input.super.hasValidation();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#getInvalidChangeEventNotifier()
	 */
	@Override
	public Optional<InvalidChangeEventNotifier> hasInvalidChangeEventNotifier() {
		if (getInvalidChangeEventNotifierSupplier().isPresent()) {
			return getInvalidChangeEventNotifierSupplier().map(s -> s.apply(getField()));
		}
		return Input.super.hasInvalidChangeEventNotifier();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasStyle()
	 */
	@Override
	public Optional<HasStyle> hasStyle() {
		if (getHasStyleSupplier().isPresent()) {
			return getHasStyleSupplier().map(s -> s.apply(getField()));
		}
		return Input.super.hasStyle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasSize()
	 */
	@Override
	public Optional<HasSize> hasSize() {
		if (getHasSizeSupplier().isPresent()) {
			return getHasSizeSupplier().map(s -> s.apply(getField()));
		}
		return Input.super.hasSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		if (getFocusOperation().isPresent()) {
			getFocusOperation().get().accept(getField());
		} else {
			if (getField() instanceof Focusable) {
				((Focusable<?>) getField()).focus();
			} else if (getComponent() instanceof Focusable) {
				((Focusable<?>) getComponent()).focus();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getComponent()
	 */
	@Override
	public Component getComponent() {
		return component;
	}

	@Override
	public <A> Optional<A> as(Class<A> type) {
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		return adapters.getAs(this, type);
	}

	/**
	 * Set the adapter for given type.
	 * @param <A> Adapter type
	 * @param type Adapter type (not null)
	 * @param adapter Adapter function
	 */
	public <A> void setAdapter(Class<A> type, Function<Input<T>, A> adapter) {
		adapters.setAdapter(type, adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#asHasValue()
	 */
	@Override
	public HasValue<?, T> asHasValue() {
		return getField();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(Input.ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		return RegistrationAdapter.adapt(field.addValueChangeListener(
				e -> listener.valueChange(new DefaultValueChangeEvent<>(this, e.getOldValue(), e.getValue(),
						(e instanceof ComponentEvent) ? ((ComponentEvent<?>) e).isFromClient() : false))));
	}

	/**
	 * Try to obtain a default <code>required</code> {@link PropertyHandler} using given component.
	 * @param <T> Value type
	 * @param <V> Concrete {@link HasValue} type
	 * @param <C> Concrete {@link Component} type
	 * @param component The component
	 * @return A default <code>required</code> {@link PropertyHandler} for given component, or <code>null</code> if not
	 *         available
	 */
	private static <T, V extends HasValue<?, T>, C extends Component> PropertyHandler<Boolean, T, V, C> tryToObtainRequiredPropertyHandler(
			final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<Boolean, T, V, C>create((fld, cmp) -> ((TextField) cmp).isRequired(),
					(fld, cmp, value) -> ((TextField) cmp).setRequired(value));
		}
		if (component instanceof TextArea) {
			return PropertyHandler.<Boolean, T, V, C>create((fld, cmp) -> ((TextArea) cmp).isRequired(),
					(fld, cmp, value) -> ((TextArea) cmp).setRequired(value));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<Boolean, T, V, C>create((fld, cmp) -> ((PasswordField) cmp).isRequired(),
					(fld, cmp, value) -> ((PasswordField) cmp).setRequired(value));
		}
		if (component instanceof ComboBox) {
			return PropertyHandler.<Boolean, T, V, C>create((fld, cmp) -> ((ComboBox<?>) cmp).isRequired(),
					(fld, cmp, value) -> ((ComboBox<?>) cmp).setRequired(value));
		}
		if (component instanceof DatePicker) {
			return PropertyHandler.<Boolean, T, V, C>create((fld, cmp) -> ((DatePicker) cmp).isRequired(),
					(fld, cmp, value) -> ((DatePicker) cmp).setRequired(value));
		}
		return null;
	}

	/**
	 * Try to obtain a default <code>label</code> {@link PropertyHandler} using given component.
	 * @param <T> Value type
	 * @param <V> Concrete {@link HasValue} type
	 * @param <C> Concrete {@link Component} type
	 * @param component The component
	 * @return A default <code>label</code> {@link PropertyHandler} for given component, or <code>null</code> if not
	 *         available
	 */
	private static <T, V extends HasValue<?, T>, C extends Component> PropertyHandler<String, T, V, C> tryToObtainLabelPropertyHandler(
			final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((TextField) cmp).getLabel(),
					(fld, cmp, value) -> ((TextField) cmp).setLabel(value));
		}
		if (component instanceof TextArea) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((TextArea) cmp).getLabel(),
					(fld, cmp, value) -> ((TextArea) cmp).setLabel(value));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((PasswordField) cmp).getLabel(),
					(fld, cmp, value) -> ((PasswordField) cmp).setLabel(value));
		}
		if (component instanceof ComboBox) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((ComboBox<?>) cmp).getLabel(),
					(fld, cmp, value) -> ((ComboBox<?>) cmp).setLabel(value));
		}
		if (component instanceof DatePicker) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((DatePicker) cmp).getLabel(),
					(fld, cmp, value) -> ((DatePicker) cmp).setLabel(value));
		}
		if (component instanceof Checkbox) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((Checkbox) cmp).getLabel(),
					(fld, cmp, value) -> ((Checkbox) cmp).setLabel(value));
		}
		return null;
	}

	/**
	 * Try to obtain a default <code>placeholder</code> {@link PropertyHandler} using given component.
	 * @param <T> Value type
	 * @param <V> Concrete {@link HasValue} type
	 * @param <C> Concrete {@link Component} type
	 * @param component The component
	 * @return A default <code>placeholder</code> {@link PropertyHandler} for given component, or <code>null</code> if
	 *         not available
	 */
	private static <T, V extends HasValue<?, T>, C extends Component> PropertyHandler<String, T, V, C> tryToObtainPlaceholderPropertyHandler(
			final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((TextField) cmp).getPlaceholder(),
					(fld, cmp, value) -> ((TextField) cmp).setPlaceholder(value));
		}
		if (component instanceof TextArea) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((TextArea) cmp).getPlaceholder(),
					(fld, cmp, value) -> ((TextArea) cmp).setPlaceholder(value));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((PasswordField) cmp).getPlaceholder(),
					(fld, cmp, value) -> ((PasswordField) cmp).setPlaceholder(value));
		}
		if (component instanceof ComboBox) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((ComboBox<?>) cmp).getPlaceholder(),
					(fld, cmp, value) -> ((ComboBox<?>) cmp).setPlaceholder(value));
		}
		if (component instanceof DatePicker) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((DatePicker) cmp).getPlaceholder(),
					(fld, cmp, value) -> ((DatePicker) cmp).setPlaceholder(value));
		}
		return null;
	}

	/**
	 * Try to obtain a default <code>title</code> {@link PropertyHandler} using given component.
	 * @param <T> Value type
	 * @param <V> Concrete {@link HasValue} type
	 * @param <C> Concrete {@link Component} type
	 * @param component The component
	 * @return A default <code>title</code> {@link PropertyHandler} for given component, or <code>null</code> if not
	 *         available
	 */
	private static <T, V extends HasValue<?, T>, C extends Component> PropertyHandler<String, T, V, C> tryToObtainTitlePropertyHandler(
			final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((TextField) cmp).getTitle(),
					(fld, cmp, value) -> ((TextField) cmp).setTitle(value));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<String, T, V, C>create((fld, cmp) -> ((PasswordField) cmp).getTitle(),
					(fld, cmp, value) -> ((PasswordField) cmp).setTitle(value));
		}
		return null;
	}

}
