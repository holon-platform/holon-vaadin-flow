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
package com.holonplatform.vaadin.flow.data;

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.internal.data.CallbackItemConverter;

/**
 * Item to value converter.
 * 
 * @param <T> Value type
 * @param <ITEM> Item type
 * 
 * @since 5.2.0
 */
public interface ItemConverter<T, ITEM> {

	/**
	 * Convert an <code>item</code> instance into the required value type.
	 * @param item Item instance
	 * @return The converted value
	 */
	T getValue(ITEM item);

	/**
	 * Convert a <code>value</code> into an item instance, if available.
	 * @param value The value to convert
	 * @return Optional item instance
	 */
	Optional<ITEM> getItem(T value);

	// ------- builders

	/**
	 * Create a new {@link ItemConverter} using given functions to perform conversions.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param toValue Function to convert the item into a value (not null)
	 * @param toItem Function to convert the value into an item, if available (not null)
	 * @return A new {@link ItemConverter}
	 */
	static <T, ITEM> ItemConverter<T, ITEM> create(Function<ITEM, T> toValue, Function<T, Optional<ITEM>> toItem) {
		return new CallbackItemConverter<>(toValue, toItem);
	}

	/**
	 * Create a new {@link ItemConverter} for consistent item and value types, which do not perform any conversion.
	 * @param <T> Value and item type
	 * @return A new identity {@link ItemConverter}
	 */
	static <T> ItemConverter<T, T> identity() {
		return create(item -> item, value -> Optional.ofNullable(value));
	}

}
