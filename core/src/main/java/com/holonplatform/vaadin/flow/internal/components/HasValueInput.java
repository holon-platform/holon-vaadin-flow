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

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
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
 * @param <F> Concrete {@link HasValue} field type
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class HasValueInput<F extends HasValue.ValueChangeEvent<T>, T> implements Input<T> {

	private static final long serialVersionUID = -2456516308895591627L;

	/**
	 * Wrapped field
	 */
	private final HasValue<F, T> field;

	/**
	 * Field component
	 */
	private final Component component;

	private final Supplier<Boolean> isRequiredGetter;
	private final Consumer<Boolean> isRequiredSetter;

	/**
	 * Constructor
	 * @param <H> Wrapped field type
	 * @param field Wrapped field and component (not null)
	 */
	public <H extends Component & HasValue<F, T>> HasValueInput(H field) {
		this(field, field);
	}

	/**
	 * Constructor
	 * @param <H> Wrapped field type
	 * @param field Wrapped field and component (not null)
	 * @param isRequiredGetter Optional <code>isRequired</code> logic getter
	 * @param isRequiredSetter Optional <code>isRequired</code> logic setter
	 */
	public <H extends Component & HasValue<F, T>> HasValueInput(H field, Supplier<Boolean> isRequiredGetter,
			Consumer<Boolean> isRequiredSetter) {
		this(field, field, isRequiredGetter, isRequiredSetter);
	}

	/**
	 * Constructor
	 * @param field Wrapped field (not null)
	 * @param component Field component
	 */
	public HasValueInput(HasValue<F, T> field, Component component) {
		this(field, component, tryToObtainAnIsRequiredGetter(component), tryToObtainAnIsRequiredSetter(component));
	}

	/**
	 * Constructor
	 * @param field Wrapped field (not null)
	 * @param component Field component
	 * @param isRequiredGetter Optional <code>isRequired</code> logic getter
	 * @param isRequiredSetter Optional <code>isRequired</code> logic setter
	 */
	public HasValueInput(HasValue<F, T> field, Component component, Supplier<Boolean> isRequiredGetter,
			Consumer<Boolean> isRequiredSetter) {
		super();
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		this.field = field;
		this.component = component;
		this.isRequiredGetter = isRequiredGetter;
		this.isRequiredSetter = isRequiredSetter;
	}

	private static Supplier<Boolean> tryToObtainAnIsRequiredGetter(final Component component) {
		if (component instanceof TextField) {
			return () -> ((TextField) component).isRequired();
		}
		if (component instanceof TextArea) {
			return () -> ((TextArea) component).isRequired();
		}
		if (component instanceof PasswordField) {
			return () -> ((PasswordField) component).isRequired();
		}
		if (component instanceof ComboBox) {
			return () -> ((ComboBox<?>) component).isRequired();
		}
		if (component instanceof DatePicker) {
			return () -> ((DatePicker) component).isRequired();
		}
		return null;
	}

	private static Consumer<Boolean> tryToObtainAnIsRequiredSetter(final Component component) {
		if (component instanceof TextField) {
			return (required) -> ((TextField) component).setRequired(required);
		}
		if (component instanceof TextArea) {
			return (required) -> ((TextArea) component).setRequired(required);
		}
		if (component instanceof PasswordField) {
			return (required) -> ((PasswordField) component).setRequired(required);
		}
		if (component instanceof ComboBox) {
			return (required) -> ((ComboBox<?>) component).setRequired(required);
		}
		if (component instanceof DatePicker) {
			return (required) -> ((DatePicker) component).setRequired(required);
		}
		return null;
	}

	/**
	 * Get the {@link HasValue} field.
	 * @return the {@link HasValue} field
	 */
	public HasValue<F, T> getField() {
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
		if (isRequiredGetter != null) {
			return isRequiredGetter.get();
		}
		return getField().isRequiredIndicatorVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		if (isRequiredSetter != null) {
			isRequiredSetter.accept(required);
		} else {
			getField().setRequiredIndicatorVisible(required);
		}
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
	public Registration addValueChangeListener(final Input.ValueChangeListener<T> listener) {
		return ValueChangeListenerUtils.adapt(field, this, listener);
	}

}
