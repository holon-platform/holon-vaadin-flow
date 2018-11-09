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
import java.util.stream.Stream;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyViewGroup;

/**
 * Provides functionalities to build and manage a group of {@link ViewComponent}s bound to a {@link Property} set.
 * 
 * @since 5.2.0
 */
public interface PropertyViewGroup extends PropertySetBound, ValueHolder<PropertyBox> {

	/**
	 * Gets all the {@link ViewComponent}s that have been bound to a property.
	 * @return An {@link Iterable} on all bound ViewComponents
	 */
	Iterable<ViewComponent<?>> getViewComponents();

	/**
	 * Get the {@link ViewComponent} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @return Optional {@link ViewComponent} bound to given <code>property</code>
	 */
	<T> Optional<ViewComponent<T>> getViewComponent(Property<T> property);

	/**
	 * Return a {@link Stream} of the properties and their bound {@link ViewComponent}s of this view group.
	 * @param <T> Property type
	 * @return Property-ViewComponent {@link PropertyBinding} stream
	 */
	<T> Stream<PropertyBinding<T, ViewComponent<T>>> stream();

	/**
	 * Clear the value of all the available {@link ViewComponent}s.
	 */
	@Override
	void clear();

	/**
	 * Get the current property values.
	 * @return A {@link PropertyBox} containing the property values (may be null)
	 */
	@Override
	PropertyBox getValue();

	/**
	 * Set the current {@link PropertyBox} value. For each property, if a {@link ViewComponent} is available, the
	 * property value will be set as the {@link ViewComponent} value.
	 * <p>
	 * Only the properties which belong to the group's property set are taken into account.
	 * </p>
	 * @param value The value to set. If <code>null</code>, all the {@link ViewComponent} components are cleared.
	 */
	@Override
	void setValue(PropertyBox value);

	// ------- Builders

	/**
	 * Get a {@link PropertyViewGroupBuilder} to create and setup a {@link PropertyViewGroup}.
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyViewGroupBuilder}
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyViewGroupBuilder builder(Iterable<P> properties) {
		return new DefaultPropertyViewGroup.DefaultBuilder(properties);
	}

	/**
	 * Get a {@link PropertyViewGroupBuilder} to create and setup a {@link PropertyViewGroup}.
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyViewGroupBuilder}
	 */
	static PropertyViewGroupBuilder builder(Property<?>... properties) {
		return builder(PropertySet.of(properties));
	}

}
