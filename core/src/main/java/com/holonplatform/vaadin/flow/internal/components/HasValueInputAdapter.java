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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * {@link Input} to {@link HasValue} adapter.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class HasValueInputAdapter<T> implements HasValue<ComponentValueChangeEvent<Component, T>, T> {

	private static final long serialVersionUID = 7777886517390394594L;

	private final Input<T> input;

	/**
	 * Constructor.
	 * @param input The {@link Input} to adapt (not null)
	 */
	public HasValueInputAdapter(Input<T> input) {
		super();
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		this.input = input;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#getEmptyValue()
	 */
	@Override
	public T getEmptyValue() {
		return input.getEmptyValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return input.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#clear()
	 */
	@Override
	public void clear() {
		input.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		input.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#getValue()
	 */
	@Override
	public T getValue() {
		return input.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.flow.component.HasValue#addValueChangeListener(com.vaadin.flow.component.HasValue.ValueChangeListener)
	 */
	@Override
	public com.vaadin.flow.shared.Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<Component, T>> listener) {
		final Registration r = input.addValueChangeListener(e -> listener.valueChanged(
				new ComponentValueChangeEvent<>(input.getComponent(), this, e.getOldValue(), e.isUserOriginated())));
		return () -> r.remove();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		input.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return input.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#setRequiredIndicatorVisible(boolean)
	 */
	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		input.setRequired(requiredIndicatorVisible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#isRequiredIndicatorVisible()
	 */
	@Override
	public boolean isRequiredIndicatorVisible() {
		return input.isRequired();
	}

}
