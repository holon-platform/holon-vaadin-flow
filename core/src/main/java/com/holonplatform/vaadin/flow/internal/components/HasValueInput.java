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
import java.util.function.Supplier;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.support.PropertyHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

/**
 * Adapter to use a {@link HasValue} {@link Component} as an {@link Input}.
 * 
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class HasValueInput<T> implements Input<T> {

	private static final long serialVersionUID = -2456516308895591627L;

	/**
	 * Wrapped field
	 */
	private final HasValue<?, T> field;

	/**
	 * Field component
	 */
	private final Component component;

	/**
	 * Property handlers
	 */
	private PropertyHandler<Boolean> requiredPropertyHandler;
	private PropertyHandler<String> labelPropertyHandler;
	private PropertyHandler<String> titlePropertyHandler;
	private PropertyHandler<String> placeholderPropertyHandler;

	/**
	 * Constructor using a {@link HasValue} and {@link Component} field instance.
	 * @param <E> ValueChangeEvent type
	 * @param <H> Actual HasValue component type
	 * @param field The {@link HasValue} field and component (not null)
	 */
	public <E extends HasValue.ValueChangeEvent<T>, H extends Component & HasValue<E, T>> HasValueInput(H field) {
		this(field, field);
	}

	/**
	 * Constructor using separate {@link HasValue} and {@link Component} field instances.
	 * @param <E> ValueChangeEvent type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 */
	public <E extends HasValue.ValueChangeEvent<T>> HasValueInput(HasValue<E, T> field, Component component) {
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
	 * Get the <code>required</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<Boolean>> getRequiredPropertyHandler() {
		return Optional.ofNullable(requiredPropertyHandler);
	}

	/**
	 * Set the <code>required</code> property handler using given callback functions.
	 * @param getter The {@link Supplier} to use to get the <code>required</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>required</code> property value (not null)
	 */
	public void setRequiredPropertyHandler(Supplier<Boolean> getter, Consumer<Boolean> setter) {
		setRequiredPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Set the <code>required</code> property handler.
	 * @param requiredPropertyHandler the property handler to set
	 */
	public void setRequiredPropertyHandler(PropertyHandler<Boolean> requiredPropertyHandler) {
		this.requiredPropertyHandler = requiredPropertyHandler;
	}

	/**
	 * Get the <code>label</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<String>> getLabelPropertyHandler() {
		return Optional.ofNullable(labelPropertyHandler);
	}

	/**
	 * Set the <code>label</code> property handler using given callback functions.
	 * @param getter The {@link Supplier} to use to get the <code>label</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>label</code> property value (not null)
	 */
	public void setLabelPropertyHandler(Supplier<String> getter, Consumer<String> setter) {
		setLabelPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Set the <code>label</code> property handler.
	 * @param labelPropertyHandler the property handler to set
	 */
	public void setLabelPropertyHandler(PropertyHandler<String> labelPropertyHandler) {
		this.labelPropertyHandler = labelPropertyHandler;
	}

	/**
	 * Get the <code>title</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<String>> getTitlePropertyHandler() {
		return Optional.ofNullable(titlePropertyHandler);
	}

	/**
	 * Set the <code>title</code> property handler using given callback functions.
	 * @param getter The {@link Supplier} to use to get the <code>title</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>title</code> property value (not null)
	 */
	public void setTitlePropertyHandler(Supplier<String> getter, Consumer<String> setter) {
		setTitlePropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Set the <code>title</code> property handler.
	 * @param titlePropertyHandler the property handler to set
	 */
	public void setTitlePropertyHandler(PropertyHandler<String> titlePropertyHandler) {
		this.titlePropertyHandler = titlePropertyHandler;
	}

	/**
	 * Get the <code>placeholder</code> property handler, if available.
	 * @return Optional required property handler
	 */
	public Optional<PropertyHandler<String>> getPlaceholderPropertyHandler() {
		return Optional.ofNullable(placeholderPropertyHandler);
	}

	/**
	 * Set the <code>placeholder</code> property handler using given callback functions.
	 * @param getter The {@link Supplier} to use to get the <code>placeholder</code> property value (not null)
	 * @param setter The {@link Consumer} to use to set the <code>placeholder</code> property value (not null)
	 */
	public void setPlaceholderPropertyHandler(Supplier<String> getter, Consumer<String> setter) {
		setPlaceholderPropertyHandler(PropertyHandler.create(getter, setter));
	}

	/**
	 * Set the <code>placeholder</code> property handler.
	 * @param placeholderPropertyHandler the property handler to set
	 */
	public void setPlaceholderPropertyHandler(PropertyHandler<String> placeholderPropertyHandler) {
		this.placeholderPropertyHandler = placeholderPropertyHandler;
	}

	/**
	 * Get the {@link HasValue} field.
	 * @return the {@link HasValue} field
	 */
	public HasValue<?, T> getField() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public T getEmptyValue() {
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
		if (value == null && getEmptyValue() != null) {
			return getEmptyValue();
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValue()
	 */
	@Override
	public T getValue() {
		return getField().getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
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
		return getRequiredPropertyHandler().map(h -> h.getPropertyValue())
				.orElseGet(() -> getField().isRequiredIndicatorVisible());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		if (getRequiredPropertyHandler().isPresent()) {
			getRequiredPropertyHandler().get().setPropertyValue(required);
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
		return getLabelPropertyHandler().map(h -> HasLabel.create(h, h));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveTitle#hasTitle()
	 */
	@Override
	public Optional<HasTitle> hasTitle() {
		return getTitlePropertyHandler().map(h -> HasTitle.create(h, h));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHavePlaceholder#hasPlaceholder()
	 */
	@Override
	public Optional<HasPlaceholder> hasPlaceholder() {
		return getPlaceholderPropertyHandler().map(h -> HasPlaceholder.create(h, h));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		if (getField() instanceof Focusable) {
			((Focusable<?>) getField()).focus();
		} else if (getComponent() instanceof Focusable) {
			((Focusable<?>) getComponent()).focus();
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#addValueChangeListener(com.holonplatform.vaadin.components.Input.
	 * ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(Input.ValueChangeListener<T> listener) {
		return ValueChangeListenerUtils.adapt(field, this, listener);
	}

	/**
	 * Try to obtain a default <code>required</code> {@link PropertyHandler} using given component.
	 * @param component The component
	 * @return A default <code>required</code> {@link PropertyHandler} for given component, or <code>null</code> if not
	 *         available
	 */
	private static PropertyHandler<Boolean> tryToObtainRequiredPropertyHandler(final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<Boolean>create(() -> ((TextField) component).isRequired(),
					required -> ((TextField) component).setRequired(required));
		}
		if (component instanceof TextArea) {
			return PropertyHandler.<Boolean>create(() -> ((TextArea) component).isRequired(),
					required -> ((TextArea) component).setRequired(required));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<Boolean>create(() -> ((PasswordField) component).isRequired(),
					required -> ((PasswordField) component).setRequired(required));
		}
		if (component instanceof ComboBox) {
			return PropertyHandler.<Boolean>create(() -> ((ComboBox<?>) component).isRequired(),
					required -> ((ComboBox<?>) component).setRequired(required));
		}
		if (component instanceof DatePicker) {
			return PropertyHandler.<Boolean>create(() -> ((DatePicker) component).isRequired(),
					required -> ((DatePicker) component).setRequired(required));
		}
		return null;
	}

	/**
	 * Try to obtain a default <code>label</code> {@link PropertyHandler} using given component.
	 * @param component The component
	 * @return A default <code>label</code> {@link PropertyHandler} for given component, or <code>null</code> if not
	 *         available
	 */
	private static PropertyHandler<String> tryToObtainLabelPropertyHandler(final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<String>create(() -> ((TextField) component).getLabel(),
					label -> ((TextField) component).setLabel(label));
		}
		if (component instanceof TextArea) {
			return PropertyHandler.<String>create(() -> ((TextArea) component).getLabel(),
					label -> ((TextArea) component).setLabel(label));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<String>create(() -> ((PasswordField) component).getLabel(),
					label -> ((PasswordField) component).setLabel(label));
		}
		if (component instanceof ComboBox) {
			return PropertyHandler.<String>create(() -> ((ComboBox<?>) component).getLabel(),
					label -> ((ComboBox<?>) component).setLabel(label));
		}
		if (component instanceof DatePicker) {
			return PropertyHandler.<String>create(() -> ((DatePicker) component).getLabel(),
					label -> ((DatePicker) component).setLabel(label));
		}
		return null;
	}

	/**
	 * Try to obtain a default <code>placeholder</code> {@link PropertyHandler} using given component.
	 * @param component The component
	 * @return A default <code>placeholder</code> {@link PropertyHandler} for given component, or <code>null</code> if
	 *         not available
	 */
	private static PropertyHandler<String> tryToObtainPlaceholderPropertyHandler(final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<String>create(() -> ((TextField) component).getPlaceholder(),
					placeholder -> ((TextField) component).setPlaceholder(placeholder));
		}
		if (component instanceof TextArea) {
			return PropertyHandler.<String>create(() -> ((TextArea) component).getPlaceholder(),
					placeholder -> ((TextArea) component).setPlaceholder(placeholder));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<String>create(() -> ((PasswordField) component).getPlaceholder(),
					placeholder -> ((PasswordField) component).setPlaceholder(placeholder));
		}
		if (component instanceof ComboBox) {
			return PropertyHandler.<String>create(() -> ((ComboBox<?>) component).getPlaceholder(),
					placeholder -> ((ComboBox<?>) component).setPlaceholder(placeholder));
		}
		if (component instanceof DatePicker) {
			return PropertyHandler.<String>create(() -> ((DatePicker) component).getPlaceholder(),
					placeholder -> ((DatePicker) component).setPlaceholder(placeholder));
		}
		return null;
	}

	/**
	 * Try to obtain a default <code>title</code> {@link PropertyHandler} using given component.
	 * @param component The component
	 * @return A default <code>title</code> {@link PropertyHandler} for given component, or <code>null</code> if not
	 *         available
	 */
	private static PropertyHandler<String> tryToObtainTitlePropertyHandler(final Component component) {
		if (component instanceof TextField) {
			return PropertyHandler.<String>create(() -> ((TextField) component).getTitle(),
					label -> ((TextField) component).setLabel(label));
		}
		if (component instanceof PasswordField) {
			return PropertyHandler.<String>create(() -> ((PasswordField) component).getTitle(),
					label -> ((PasswordField) component).setLabel(label));
		}
		return null;
	}

}
