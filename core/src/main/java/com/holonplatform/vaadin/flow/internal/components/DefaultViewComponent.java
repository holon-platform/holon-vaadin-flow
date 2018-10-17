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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.shared.Registration;

/**
 * Default {@link ViewComponent} implementation.
 *
 * @since 5.2.0
 */
public class DefaultViewComponent<T> extends Composite<Div>
		implements ViewComponent<T>, HasText, ClickNotifier<Component> {

	private static final long serialVersionUID = 7748055782623326295L;

	private static final PropertyDescriptor<String, String> innerHtmlDescriptor = PropertyDescriptors
			.propertyWithDefault("innerHTML", "");

	private final Label label;
	private final Div text;

	private final Function<T, String> stringValueConverter;

	private boolean html;

	private T value;

	private final List<ValueChangeListener<T>> valueChangeListeners = new LinkedList<>();

	/**
	 * Constructor which uses a {@link StringValuePresenter} as value converter.
	 * @param type Value type
	 */
	public DefaultViewComponent(Class<? extends T> type) {
		this((v) -> StringValuePresenter.getDefault().present(type, v, null));
	}

	/**
	 * Constructor.
	 * @param stringValueConverter Value converter
	 */
	public DefaultViewComponent(Function<T, String> stringValueConverter) {
		super();
		ObjectUtils.argumentNotNull(stringValueConverter, "String value converter must be non null");
		this.stringValueConverter = stringValueConverter;

		this.label = new Label();
		this.label.addClassName("caption");
		this.text = new Div();

		getContent().addClassName("h-viewcomponent");
		getContent().add(label, text);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Get whether the view component is in HTML content mode.
	 * @return <code>true</code> if the view component is in HTML content mode, <code>false</code> otherwise
	 */
	public boolean isHtml() {
		return html;
	}

	/**
	 * Set whether the view component is in HTML content mode.
	 * @param html <code>true</code> to set the view component is in HTML content mode, <code>false</code> to set it is
	 *        plain text mode
	 */
	public void setHtml(boolean html) {
		boolean wasHtml = this.html;
		this.html = html;
		// update text contents
		if (html && !wasHtml) {
			setText(this.text.getText());
		} else if (!html && wasHtml) {
			setText(get(innerHtmlDescriptor));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasLabel#getLabel()
	 */
	@Override
	public String getLabel() {
		return this.label.getText();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasLabel#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String label) {
		this.label.setText(label);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		// update internal component
		updateTextValue(value);
		// fire value change
		fireValueChange(oldValue, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.flow.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> valueChangeListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		if (!isHtml() || text == null || text.trim().equals("")) {
			this.text.setText(text);
		} else {
			set(innerHtmlDescriptor, text);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasText#getText()
	 */
	@Override
	public String getText() {
		return isHtml() ? get(innerHtmlDescriptor) : this.text.getText();
	}

	/**
	 * Get the {@link Function} to use to convert the value in its {@link String} representation.
	 * @return the string value converter
	 */
	public Function<T, String> getStringValueConverter() {
		return stringValueConverter;
	}

	/**
	 * Update the text value of the component, using {@link #getStringValueConverter()} to obtain the value conversion
	 * function.
	 * @param value The value to update
	 */
	protected void updateTextValue(T value) {
		setText(getStringValueConverter().apply(value));
	}

	/**
	 * Emits the value change event
	 * @param oldValue the old value
	 * @param value the changed value
	 */
	protected void fireValueChange(T oldValue, T value) {
		final ValueChangeEvent<T> valueChangeEvent = new DefaultValueChangeEvent<>(this, oldValue, value, false);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

}
