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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.ValueComponent;

/**
 * Configuration for a {@link Property} bound to a {@link ValueComponent}.
 * 
 * @param <T> Property type
 * @param <V> ValueComponent type
 *
 * @since 5.2.0
 */
public interface ValueComponentPropertyConfiguration<T, V extends ValueComponent<T>> {

	/**
	 * Get the property.
	 * @return the property.
	 */
	Property<T> getProperty();

	/**
	 * Get whether the property is hidden.
	 * @return whether the property is hidden
	 */
	boolean isHidden();

	/**
	 * Set whether the property is hidden.
	 * @param hidden whether the property is hidden
	 */
	public void setHidden(boolean hidden);

	/**
	 * Get the property renderer, if available.
	 * @return Optional property renderer
	 */
	Optional<PropertyRenderer<V, T>> getRenderer();

	/**
	 * Set the property renderer.
	 * @param renderer The property renderer to set (may be null)
	 */
	void setRenderer(PropertyRenderer<V, T> renderer);

	/**
	 * Create a new {@link ValueComponentPropertyConfiguration} for given property.
	 * @param <T> Property type
	 * @param <V> ValueComponent type
	 * @param property The property (not null)
	 * @return A new {@link ValueComponentPropertyConfiguration} for given property
	 */
	static <T, V extends ValueComponent<T>> ValueComponentPropertyConfiguration<T, V> create(Property<T> property) {
		return new DefaultValueComponentPropertyConfiguration<>(property);
	}

}
