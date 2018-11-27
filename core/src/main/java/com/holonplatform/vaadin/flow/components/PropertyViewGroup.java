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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyNotFoundException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyViewGroup;

/**
 * A {@link BoundComponentGroup} which handles {@link ViewComponent} elements type and uses a {@link Property} set to
 * bind and identify the elements within the group.
 * <p>
 * As a {@link ValueHolder}, allows to manage the overall group elements value, represented by a {@link PropertyBox}
 * instance.
 * </p>
 * <p>
 * By default, the {@link ViewComponent} components to bind to each property are obtained using the
 * {@link PropertyRenderer}s registered in the context {@link PropertyRendererRegistry}, if available. The
 * <code>bind(...)</code> methods of the {@link PropertyViewGroupBuilder} can be used to provided a specific renderer or
 * {@link ViewComponent} of one or more group property.
 * </p>
 * 
 * @since 5.2.0
 */
public interface PropertyViewGroup
		extends BoundComponentGroup<Property<?>, ViewComponent<?>>, ValueHolder<PropertyBox> {

	/**
	 * Get the {@link ViewComponent} bound to the given <code>property</code>, if available.
	 * @param <T> Property type
	 * @param property The property which identifies the {@link ViewComponent} within the group (not null)
	 * @return Optional {@link ViewComponent} bound to the given <code>property</code>
	 */
	<T> Optional<ViewComponent<T>> getViewComponent(Property<T> property);

	/**
	 * Get the {@link ViewComponent} bound to the given <code>property</code>, throwing a
	 * {@link PropertyNotFoundException} if not available.
	 * @param <T> Property type
	 * @param property The property which identifies the {@link ViewComponent} within the group (not null)
	 * @return The {@link ViewComponent} bound to the given <code>property</code>
	 * @throws PropertyNotFoundException If no {@link ViewComponent} is bound to given property
	 */
	default <T> ViewComponent<T> requireViewComponent(Property<T> property) {
		return getViewComponent(property).orElseThrow(() -> new PropertyNotFoundException(property,
				"No ViewComponent available for property [" + property + "]"));
	}

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
