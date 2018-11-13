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
package com.holonplatform.vaadin.flow.internal.data;

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;

/**
 * {@link ItemConverter} using a {@link Property} to represents the value within a {@link PropertyBox} item type.
 * 
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class PropertyItemConverter<T> implements ItemConverter<T, PropertyBox> {

	private static final Logger LOGGER = VaadinLogger.create();

	private final Property<T> property;
	private Function<T, Optional<PropertyBox>> toItemConverter;

	/**
	 * Constructor.
	 * @param property The property which represents the value (not null)
	 */
	public PropertyItemConverter(Property<T> property) {
		this(property, null);
	}

	/**
	 * Constructor.
	 * @param property The property which represents the value (not null)
	 * @param toItemConverter Optional function to convert a value into the corresponding item
	 */
	public PropertyItemConverter(Property<T> property, Function<T, Optional<PropertyBox>> toItemConverter) {
		super();
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		this.property = property;
		this.toItemConverter = toItemConverter;
	}

	/**
	 * Get the function to use to convert a value into a {@link PropertyBox} type item.
	 * @return the toItemConverter The function to use to convert a value into a {@link PropertyBox} type item,
	 *         <code>null</code> if not available
	 */
	public Function<T, Optional<PropertyBox>> getToItemConverter() {
		return toItemConverter;
	}

	/**
	 * Set the function to use to convert a value into a {@link PropertyBox} type item.
	 * @param toItemConverter the to item converter function to set
	 */
	public void setToItemConverter(Function<T, Optional<PropertyBox>> toItemConverter) {
		this.toItemConverter = toItemConverter;
	}

	@Override
	public T getValue(PropertyBox item) {
		return (item != null) ? item.getValue(property) : null;
	}

	@Override
	public Optional<PropertyBox> getItem(T value) {
		if (toItemConverter != null) {
			return toItemConverter.apply(value);
		}
		LOGGER.warn(
				"The value to PropertyBox item conversion logic was not configured, no item will never be returned");
		return Optional.empty();
	}

}
