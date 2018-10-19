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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

/**
 * A wrapper to wrap a {@link HasValue} component into a {@link Input} component.
 * 
 * @param <V> Value type
 * 
 * @since 5.2.0
 */
public class InputFieldWrapper<E extends HasValue.ValueChangeEvent<V>, V> implements Input<V> {

	private static final long serialVersionUID = -2456516308895591627L;

	/**
	 * Wrapped field
	 */
	private final HasValue<E, V> field;
	/**
	 * Field component
	 */
	private final Component component;

	/**
	 * Constructor
	 * @param <H> Wrapped field type
	 * @param field Wrapped field and component (not null)
	 */
	public <H extends Component & HasValue<E, V>> InputFieldWrapper(H field) {
		this(field, field);
	}

	/**
	 * Constructor
	 * @param field Wrapped field (not null)
	 * @param component Field component
	 */
	public InputFieldWrapper(HasValue<E, V> field, Component component) {
		super();
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		this.field = field;
		this.component = component;
	}

	/**
	 * Get the wrapped {@link HasValue}.
	 * @return the wrapped field
	 */
	public HasValue<E, V> getField() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(V value) {
		field.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#getValue()
	 */
	@Override
	public V getValue() {
		return field.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return field.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#clear()
	 */
	@Override
	public void clear() {
		field.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		field.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return field.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return field.isRequiredIndicatorVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		field.setRequiredIndicatorVisible(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Input#focus()
	 */
	@Override
	public void focus() {
		if (field instanceof Focusable) {
			((Focusable<?>) field).focus();
		} else if (component instanceof Focusable) {
			((Focusable<?>) component).focus();
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
	public Registration addValueChangeListener(final Input.ValueChangeListener<V> listener) {
		return ValueChangeListenerUtils.adapt(field, this, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.MaySupportValueChangeMode#isValueChangeModeSupported()
	 */
	@Override
	public boolean isValueChangeModeSupported() {
		return field instanceof HasValueChangeMode;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ValueHolder.MaySupportValueChangeMode#setValueChangeMode(com.vaadin.shared.ui
	 * .ValueChangeMode)
	 */
	@Override
	public void setValueChangeMode(ValueChangeMode valueChangeMode) {
		ObjectUtils.argumentNotNull(valueChangeMode, "ValueChangeMode must be not null");
		if (isValueChangeModeSupported()) {
			((HasValueChangeMode) field).setValueChangeMode(valueChangeMode);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.MaySupportValueChangeMode#getValueChangeMode()
	 */
	@Override
	public ValueChangeMode getValueChangeMode() {
		if (isValueChangeModeSupported()) {
			return ((HasValueChangeMode) field).getValueChangeMode();
		}
		return ValueChangeMode.ON_BLUR;
	}

}
