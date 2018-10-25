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

/**
 * A converter interface to obtain an item from a different type value.
 * 
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <CONTEXT> Conversion context type
 * 
 * @since 5.2.0
 */
public interface ItemConverter<T, ITEM, CONTEXT> {

	/**
	 * Convert an <code>item</code> instance into the required value type.
	 * @param context Conversion context
	 * @param item Item instance
	 * @return The converted value
	 */
	T getValue(CONTEXT context, ITEM item);

	/**
	 * Convert a <code>value</code> into an item instance.
	 * @param context Conversion context
	 * @param value The value to convert
	 * @return The item instance
	 */
	ITEM getItem(CONTEXT context, T value);

}
