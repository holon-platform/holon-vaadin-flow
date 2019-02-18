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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultViewComponent;
import com.vaadin.flow.component.html.Div;

/**
 * Default {@link ViewComponentBuilder} implementation.
 *
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class DefaultViewComponentBuilder<T>
		extends AbstractViewComponentBuilder<Div, T, DefaultViewComponent<T>, ViewComponentBuilder<T>>
		implements ViewComponentBuilder<T> {

	/**
	 * Constructor which uses a {@link StringValuePresenter} as value converter.
	 * @param type Value type (not null)
	 */
	public DefaultViewComponentBuilder(Class<? extends T> type) {
		this(v -> StringValuePresenter.getDefault().present(type, v, null));
	}

	/**
	 * Constructor which uses given <code>property</code> for value presentation and configuration parameters source.
	 * The {@link Property#present(Object)} method will be called when value presentation is required.
	 * @param property The property to use (not null)
	 */
	public DefaultViewComponentBuilder(Property<T> property) {
		this(v -> property.present(v));
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		label(property);
	}

	/**
	 * Constructor.
	 * @param stringValueConverter Value converter (not null)
	 */
	public DefaultViewComponentBuilder(Function<T, String> stringValueConverter) {
		super(new DefaultViewComponent<>(stringValueConverter));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ViewComponentBuilder<T> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder#html(boolean)
	 */
	@Override
	public ViewComponentBuilder<T> html(boolean html) {
		getComponent().setHtml(html);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder#build()
	 */
	@Override
	public ViewComponent<T> build() {
		return getComponent();
	}

}
