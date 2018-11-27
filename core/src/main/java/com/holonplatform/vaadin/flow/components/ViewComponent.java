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

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;

/**
 * A {@link ValueHolder} component to display a value in UI.
 * 
 * @param <V> Value type
 *
 * @since 5.2.0
 */
public interface ViewComponent<V> extends ValueHolder<V>, ValueComponent<V> {

	/**
	 * Checks whether this component supports a title, which text can be handled using the {@link HasTitle} interface.
	 * @return If this component supports a title, return the {@link HasTitle} reference. An empty Optional is returned
	 *         otherwise.
	 */
	default Optional<HasTitle> hasTitle() {
		return Optional.empty();
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
	 * Create a {@link ViewComponent} using given value type.
	 * @param <T> Value type
	 * @param valueType Value type (not null)
	 * @return A {@link ViewComponent} instance
	 */
	static <T> ViewComponent<T> create(Class<T> valueType) {
		return builder(valueType).build();
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

	/**
	 * A {@link PropertyRenderer} for the {@link ViewComponent} rendering type.
	 * 
	 * @param <T> ViewComponent type
	 */
	@FunctionalInterface
	public interface ViewComponentPropertyRenderer<T> extends PropertyRenderer<ViewComponent<T>, T> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		default Class<? extends ViewComponent<T>> getRenderType() {
			return (Class<? extends ViewComponent<T>>) (Class<?>) ViewComponent.class;
		}

		/**
		 * Create a new {@link ViewComponentPropertyRenderer} using given function.
		 * @param <T> ViewComponent type
		 * @param function Function to use to provide the {@link ViewComponent} component (not null)
		 * @return A new {@link ViewComponentPropertyRenderer}
		 */
		static <T> ViewComponentPropertyRenderer<T> create(Function<Property<? extends T>, ViewComponent<T>> function) {
			ObjectUtils.argumentNotNull(function, "The rendering function must be not null");
			return property -> function.apply(property);
		}

	}

}
