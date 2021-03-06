/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.value.HasValueChangeMode;

/**
 * Represents an object which can contain a value and provides its UI {@link Component} representation.
 * 
 * @param <V> Value type
 * 
 * @since 5.0.0
 */
public interface ValueComponent<V> extends HasComponent {

	/**
	 * Gets the current value of this value component.
	 * @return the current value
	 */
	V getValue();

	/**
	 * Checks whether the {@link Component} supports a value change mode, using the {@link HasValueChangeMode}
	 * interface.
	 * @return If the component supports a value change mode, return the {@link HasValueChangeMode} reference. An empty
	 *         Optional otherwise.
	 */
	default Optional<HasValueChangeMode> hasValueChangeMode() {
		return (getComponent() instanceof HasValueChangeMode) ? Optional.of((HasValueChangeMode) getComponent())
				: Optional.empty();
	}

}
