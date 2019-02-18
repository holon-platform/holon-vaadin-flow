/*
 * Copyright 2016-2019 Axioma srl.
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

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultViewComponentAdapterBuilder;
import com.vaadin.flow.component.Component;

/**
 * {@link ViewComponent} adapter builder.
 *
 * @param <T> Value type
 * 
 * @since 5.2.2
 */
public interface ViewComponentAdapterBuilder<T> extends ViewComponentConfigurator<T, ViewComponentAdapterBuilder<T>> {

	/**
	 * Build the {@link ViewComponent} instance.
	 * @return the {@link ViewComponent} instance
	 */
	ViewComponent<T> build();

	/**
	 * Create a new {@link ViewComponentAdapterBuilder}.
	 * @param <T> Value type
	 * @param <C> View component content type
	 * @param type Value type
	 * @param componentProvider The function to provide a content component each time the value changes (not null)
	 * @return A new {@link ViewComponentAdapterBuilder}
	 */
	static <T, C extends Component> ViewComponentAdapterBuilder<T> create(Class<T> type,
			Function<T, C> componentProvider) {
		return new DefaultViewComponentAdapterBuilder<>(componentProvider);
	}

	/**
	 * Create a new {@link ViewComponentAdapterBuilder}.
	 * @param <T> Value type
	 * @param <C> View component content type
	 * @param type Value type
	 * @param content The fixed content component (not null)
	 * @param valueConsumer The consumer to configure the content component each time the value changes (not null)
	 * @return A new {@link ViewComponentAdapterBuilder}
	 */
	static <T, C extends Component> ViewComponentAdapterBuilder<T> create(Class<T> type, C content,
			BiConsumer<C, T> valueConsumer) {
		return new DefaultViewComponentAdapterBuilder<>(content, valueConsumer);
	}

}
