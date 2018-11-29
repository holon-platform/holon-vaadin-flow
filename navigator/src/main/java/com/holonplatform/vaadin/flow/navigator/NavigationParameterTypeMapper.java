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
package com.holonplatform.vaadin.flow.navigator;

import java.util.ServiceLoader;

import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

/**
 * A delegate for the {@link NavigationParameterMapper} to handle a specific navigation parameter value type, provided
 * by the {@link #getParameterType()} method.
 * <p>
 * Should provide the parameter value serialization logic, to obtain the value to include in the navigation URL, and the
 * deserialization logic, to obtain a value from a {@link String} type navigation URL fragment.
 * </p>
 * <p>
 * A new {@link NavigationParameterTypeMapper} registration can be performed using the default Java
 * {@link ServiceLoader} extensions, providing a
 * <code>com.holonplatform.vaadin.flow.navigator.NavigationParameterTypeMapper</code> file in the
 * <code>META-INF/services</code> folder, containing the {@link NavigationParameterTypeMapper} concrete class names to
 * register.
 * </p>
 * <p>
 * When more than one {@link NavigationParameterTypeMapper} is available for the same parameter value type, no selection
 * order is guaranteed, and the last registered mapper will be used.
 * </p>
 * 
 * @param <T> Parameter value type
 *
 * @since 5.2.0
 */
public interface NavigationParameterTypeMapper<T> {

	/**
	 * Get the parameter value type to which this mapper is bound.
	 * @return the parameter value type to which this mapper is bound, must be not <code>null</code>
	 */
	Class<T> getParameterType();

	/**
	 * Serialize given parameter <code>value</code>.
	 * @param value The parameter value (never <code>null</code>)
	 * @return The serialized parameter value, must be not <code>null</code>
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	String serialize(T value) throws InvalidNavigationParameterException;

	/**
	 * Deserialize given parameter <code>value</code>.
	 * @param value The parameter value, not <code>null</code> nor blank
	 * @return The deserialized parameter value, must be not <code>null</code>
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	T deserialize(String value) throws InvalidNavigationParameterException;

}
