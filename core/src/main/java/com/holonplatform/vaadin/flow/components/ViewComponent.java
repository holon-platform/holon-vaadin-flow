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
package com.holonplatform.vaadin.flow.components;

import java.util.function.Function;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.dom.Element;

/**
 * A {@link ValueHolder} component to display a value in UI.
 * 
 * @param <V> Value type
 *
 * @since 5.2.0
 */
public interface ViewComponent<V> extends ValueHolder<V>, ValueComponent<V>, HasLabel, HasSize, HasStyle, HasEnabled {

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasElement#getElement()
	 */
	@Override
	default Element getElement() {
		return getComponent().getElement();
	}

	/**
	 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given value type.
	 * @param <T> Value type
	 * @param valueType Value type (not null)
	 * @return A {@link ViewComponentBuilder}
	 */
	static <T> ViewComponentBuilder<T> builder(Class<? extends T> valueType) {
		return ViewComponentBuilder.create(valueType);
	}

	/**
	 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given {@link Property} for label and
	 * value presentation through the {@link Property#present(Object)} method.
	 * @param <T> Value type
	 * @param property The property to use (not null)
	 * @return A {@link ViewComponentBuilder}
	 */
	static <T> ViewComponentBuilder<T> builder(Property<T> property) {
		return ViewComponentBuilder.create(property);
	}

	/**
	 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given function to convert the value to
	 * a {@link String} type representation.
	 * @param <T> Value type
	 * @param stringValueConverter Value converter function (not null)
	 * @return A {@link ViewComponentBuilder}
	 */
	static <T> ViewComponentBuilder<T> builder(Function<T, String> stringValueConverter) {
		return ViewComponentBuilder.create(stringValueConverter);
	}

	/**
	 * Create a {@link ViewComponent} using given {@link Property} for label and value presentation through the
	 * {@link Property#present(Object)} method.
	 * @param <T> Value type
	 * @param property The property to use (not null)
	 * @return A {@link ViewComponent} instance
	 */
	static <T> ViewComponent<T> create(Property<T> property) {
		return builder(property).build();
	}

}
