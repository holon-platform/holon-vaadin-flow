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

import java.util.function.BiFunction;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

/**
 * {@link ItemConverter} using a {@link Property} to represents the value within a {@link PropertyBox} item type.
 * 
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class PropertyItemConverter<T> implements ItemConverter<T, PropertyBox, DataProvider<PropertyBox, ?>> {

	private final Property<T> property;
	private final BiFunction<DataProvider<PropertyBox, ?>, T, PropertyBox> toItem;

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
	 * @param toItem Optional function to convert a value into the corresponding item
	 */
	public PropertyItemConverter(Property<T> property,
			BiFunction<DataProvider<PropertyBox, ?>, T, PropertyBox> toItem) {
		super();
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		this.property = property;
		this.toItem = toItem;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemConverter#getValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T getValue(DataProvider<PropertyBox, ?> context, PropertyBox item) {
		if (item != null) {
			return item.getValue(property);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemConverter#getItem(java.lang.Object, java.lang.Object)
	 */
	@Override
	public PropertyBox getItem(DataProvider<PropertyBox, ?> context, T value) {
		if (toItem != null) {
			return toItem.apply(context, value);
		}
		if (value != null) {
			// TODO THIS OPERATION IS HIGHLY INEFFICIENT
			return context.fetch(new Query<>()).filter(item -> value.equals(item.getValue(property))).findFirst()
					.orElse(null);
		}
		return null;
	}

}
