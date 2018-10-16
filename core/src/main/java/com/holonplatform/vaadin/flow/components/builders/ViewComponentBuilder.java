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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.function.Function;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.vaadin.flow.component.HtmlComponent;

/**
 * Builder to create {@link ViewComponent}s.
 * 
 * @param <T> Value type
 * 
 * @since 5.0.0
 */
public interface ViewComponentBuilder<T>
		extends HtmlComponentConfigurator<ViewComponentBuilder<T>>, HasEnabledConfigurator<ViewComponentBuilder<T>>,
		HasTextConfigurator<ViewComponentBuilder<T>>, HasHtmlTextConfigurator<ViewComponentBuilder<T>>,
		ClickNotifierConfigurator<HtmlComponent, ViewComponentBuilder<T>>,
		DeferrableLocalizationConfigurator<ViewComponentBuilder<T>> {

	/**
	 * Build a {@link ViewComponent} using given <code>property</code> for value presentation and configuration
	 * parameters source. The {@link Property#present(Object)} method will be called when value presentation is
	 * required.
	 * @param property The property to use (not null)
	 * @return this
	 */
	ViewComponentBuilder<T> forProperty(Property<T> property);

	/**
	 * Sets the converter to use to display values
	 * @param stringConverter the StringValueConverter to set
	 * @return this
	 */
	ViewComponentBuilder<T> valueDisplayConverter(Function<T, String> stringConverter);

	/**
	 * Add a {@link ValueChangeListener} to the component.
	 * @param listener The ValueChangeListener to add
	 * @return this
	 */
	ViewComponentBuilder<T> withValueChangeListener(ValueChangeListener<T> listener);

	/**
	 * Build the {@link ViewComponent} instance.
	 * @return the {@link ViewComponent} instance
	 */
	ViewComponent<T> build();

}
