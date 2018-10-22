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
package com.holonplatform.vaadin.flow.components.support;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.holonplatform.vaadin.flow.internal.components.support.CallbackPropertyHandler;

/**
 * Handler to manage a generic component property.
 * 
 * @param <T> Property type
 * 
 * @since 5.2.0
 */
public interface PropertyHandler<T> extends Supplier<T>, Consumer<T> {

	/**
	 * Get the property value.
	 * @return The property value
	 */
	T getPropertyValue();

	/**
	 * Set the property value.
	 * @param value The property value to set
	 */
	void setPropertyValue(T value);

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	default void accept(T t) {
		setPropertyValue(t);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Supplier#get()
	 */
	@Override
	default T get() {
		return getPropertyValue();
	}

	/**
	 * Create a new {@link PropertyHandler} using given callback functions to get and set the property value.
	 * @param <T> Property type
	 * @param getter The {@link Supplier} to use to get the property value (not null)
	 * @param setter The {@link Consumer} to use to set the property value (not null)
	 * @return A new {@link PropertyHandler} using given callback functions to get and set the property value
	 */
	static <T> PropertyHandler<T> create(Supplier<T> getter, Consumer<T> setter) {
		return new CallbackPropertyHandler<>(getter, setter);
	}

}
