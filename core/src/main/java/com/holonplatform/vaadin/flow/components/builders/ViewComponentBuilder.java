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
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultViewComponentBuilder;

/**
 * Builder to create {@link ViewComponent}s.
 * 
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public interface ViewComponentBuilder<T> extends HtmlComponentConfigurator<ViewComponentBuilder<T>>,
		HasEnabledConfigurator<ViewComponentBuilder<T>>, HasLabelConfigurator<ViewComponentBuilder<T>>,
		ClickNotifierConfigurator<ViewComponent<T>, ClickEvent<ViewComponent<T>>, ViewComponentBuilder<T>>,
		DeferrableLocalizationConfigurator<ViewComponentBuilder<T>> {

	/**
	 * Sets an initial value for the component.
	 * @param value The initial value to set
	 * @return this
	 */
	ViewComponentBuilder<T> withValue(T value);

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

	/**
	 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given value type.
	 * @param <T> Value type
	 * @param valueType Value type (not null)
	 * @return A {@link ViewComponentBuilder}
	 */
	static <T> ViewComponentBuilder<T> create(Class<? extends T> valueType) {
		return new DefaultViewComponentBuilder<>(valueType);
	}

	/**
	 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given function to convert the value to
	 * a {@link String} type representation.
	 * @param <T> Value type
	 * @param stringValueConverter Value converter function (not null)
	 * @return A {@link ViewComponentBuilder}
	 */
	static <T> ViewComponentBuilder<T> create(Function<T, String> stringValueConverter) {
		return new DefaultViewComponentBuilder<>(stringValueConverter);
	}

	/**
	 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given {@link Property} for label and
	 * value presentation through the {@link Property#present(Object)} method.
	 * @param <T> Value type
	 * @param property The property to use (not null)
	 * @return A {@link ViewComponentBuilder}
	 */
	static <T> ViewComponentBuilder<T> create(Property<T> property) {
		return new DefaultViewComponentBuilder<>(property);
	}

}
