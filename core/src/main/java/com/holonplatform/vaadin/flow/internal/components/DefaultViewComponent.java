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

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.html.Div;

/**
 * Default {@link ViewComponent} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultViewComponent<T> extends AbstractViewComponent<Div, T> {

	private static final long serialVersionUID = 7748055782623326295L;

	private static final PropertyDescriptor<String, String> innerHtmlDescriptor = PropertyDescriptors
			.propertyWithDefault("innerHTML", "");

	private final Function<T, String> stringValueConverter;

	private boolean html = false;

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
		super(new Div());
		ObjectUtils.argumentNotNull(stringValueConverter, "String value converter must be non null");
		this.stringValueConverter = stringValueConverter;

		getContent().addClassName("h-viewcomponent");
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
	 * @param html whether the view component is in HTML content mode
	 */
	public void setHtml(boolean html) {
		this.html = html;
	}

	/**
	 * Get the {@link Function} to use to convert the value in its {@link String} representation.
	 * @return the string value converter
	 */
	public Function<T, String> getStringValueConverter() {
		return stringValueConverter;
	}

	/**
	 * Set the content text.
	 * @param text The text to set
	 */
	protected void setText(String text) {
		getInternalContent().ifPresent(content -> {
			if (!isHtml() || text == null || text.trim().equals("")) {
				content.setText(text);
			} else {
				innerHtmlDescriptor.set(content, text);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractViewComponent#updateValue(java.lang.Object)
	 */
	@Override
	protected Optional<Div> updateValue(T value) {
		setText(getStringValueConverter().apply(value));
		return getInternalContent();
	}

}
