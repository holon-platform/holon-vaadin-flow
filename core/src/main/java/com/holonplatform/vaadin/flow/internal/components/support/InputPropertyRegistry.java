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

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.BoundComponentGroup.Binding;
import com.holonplatform.vaadin.flow.components.Input;

/**
 * {@link Property} and {@link Input} binding registry.
 *
 * @since 5.2.0
 */
public interface InputPropertyRegistry extends Serializable {

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
	<T> void set(Property<T> property, Input<T> component);

	/**
	 * Get the {@link Input} bound to given property, if available.
	 * @param <T> Property type
	 * @param property The property for which to obtain the component (not null)
	 * @return Optional {@link Input} bound to given property
	 */
	<T> Optional<Input<T>> get(Property<T> property);

	/**
	 * Get the registered bindings.
	 * @return the registered bindings
	 */
	Stream<Binding<Property<?>, Input<?>>> stream();

	/**
	 * Get the registered bindings.
	 * @return the registered bindings
	 */
	Stream<Binding<Property<Object>, Input<Object>>> bindings();

	/**
	 * Create a new {@link InputPropertyRegistry}.
	 * @return A new registry
	 */
	static InputPropertyRegistry create() {
		return new DefaultInputPropertyRegistry();
	}

}
