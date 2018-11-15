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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.PropertyBinding;
import com.holonplatform.vaadin.flow.components.ViewComponent;

/**
 * {@link Property} and {@link ViewComponent} binding registry.
 *
 * @since 5.2.0
 */
public interface ViewComponentPropertyRegistry {

	/**
	 * Clear all bindings.
	 */
	void clear();

	/**
	 * Bind given property to given component.
	 * @param <T> Property type
	 * @param property The property to bind (not null)
	 * @param component The component
	 */
	<T> void set(Property<T> property, ViewComponent<T> component);

	/**
	 * Get the {@link ViewComponent} bound to given property, if available.
	 * @param <T> Property type
	 * @param property The property for which to obtain the component (not null)
	 * @return Optional {@link ViewComponent} bound to given property
	 */
	<T> Optional<ViewComponent<T>> get(Property<T> property);

	/**
	 * Get the registered bindings.
	 * @param <T> Property type
	 * @return the registered bindings
	 */
	<T> Stream<PropertyBinding<T, ViewComponent<T>>> stream();

	/**
	 * Create a new {@link ViewComponentPropertyRegistry}.
	 * @return A new registry
	 */
	static ViewComponentPropertyRegistry create() {
		return new DefaultViewComponentPropertyRegistry();
	}

}
