/*
 * Copyright 2016-2019 Axioma srl.
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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigationParameters;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

/**
 * URL navigation parameter handler API, which provides deserialized URL query parameter values.
 * 
 * @since 5.2.2
 */
public interface NavigationParameters extends Serializable {

	/**
	 * Get the URL query parameters, without any value deserialization.
	 * @return A map of query parameter name and values
	 */
	Map<String, List<String>> getQueryParameters();

	/**
	 * Checks whether the query parameter with the given <code>name</code> is available and has a value.
	 * @param name The query parameter name (not null)
	 * @return <code>true</code> if the query parameter with the given <code>name</code> is available and has a value,
	 *         <code>false</code> otherwise
	 */
	boolean hasQueryParameter(String name);

	/**
	 * Get the values of the query parameter with the given <code>name</code>, deserializing it to the given value
	 * <code>type</code>.
	 * <p>
	 * The query parameter values are deserialized using the {@link NavigationParameterMapper}.
	 * </p>
	 * @param <T> Deserialized value type
	 * @param name The query parameter name (not null)
	 * @param type The type into which the query parameter values have to be deserialized (not null)
	 * @return The deserialized query parameter values, an empty <code>List</code> if none
	 * @throws InvalidNavigationParameterException If a deserialization error occurred
	 */
	<T> List<T> getQueryParameterValues(String name, Class<T> type) throws InvalidNavigationParameterException;

	/**
	 * Get the single value of the query parameter with the given <code>name</code>, deserializing it to the given value
	 * <code>type</code>.
	 * <p>
	 * If the query parameter has more than one value, only the first one is returned.
	 * </p>
	 * <p>
	 * The query parameter value is deserialized using the {@link NavigationParameterMapper}.
	 * </p>
	 * @param <T> Deserialized value type
	 * @param name The query parameter name (not null)
	 * @param type The type into which the query parameter value has to be deserialized (not null)
	 * @return Optional deserialized query parameter value, if available
	 * @throws InvalidNavigationParameterException If a deserialization error occurred
	 */
	<T> Optional<T> getQueryParameterValue(String name, Class<T> type) throws InvalidNavigationParameterException;

	/**
	 * Get the single value of the query parameter with the given <code>name</code>, deserializing it to the given value
	 * <code>type</code>.
	 * <p>
	 * If the query parameter has more than one value, only the first one is returned.
	 * </p>
	 * <p>
	 * The query parameter value is deserialized using the {@link NavigationParameterMapper}.
	 * </p>
	 * @param <T> Deserialized value type
	 * @param name The query parameter name (not null)
	 * @param type The type into which the query parameter value has to be deserialized (not null)
	 * @param defaultValue The default value to return if the query parameter has no value
	 * @return The deserialized query parameter value, or <code>defaultValue</code> if the query parameter has no value
	 * @throws InvalidNavigationParameterException If a deserialization error occurred
	 */
	<T> T getQueryParameterValue(String name, Class<T> type, T defaultValue) throws InvalidNavigationParameterException;

	// ------- builders

	/**
	 * Create a new {@link NavigationParameters} instance.
	 * <p>
	 * The query parameter names and values will be decoded from URL representation, using the <code>UTF-8</code>
	 * charset.
	 * </p>
	 * @param queryParameters Query parameter names and values
	 * @return A new {@link NavigationParameters} to handle given navigation parameters
	 */
	static NavigationParameters create(Map<String, List<String>> queryParameters) {
		return create(queryParameters, true);
	}

	/**
	 * Create a new {@link NavigationParameters} instance.
	 * @param queryParameters Query parameter names and values
	 * @param decode Whether to decode the parameter names and values from URL representation, using the
	 *        <code>UTF-8</code> charset
	 * @return A new {@link NavigationParameters} to handle given navigation parameters
	 */
	static NavigationParameters create(Map<String, List<String>> queryParameters, boolean decode) {
		return new DefaultNavigationParameters(queryParameters, decode);
	}

	/**
	 * Create a new {@link NavigationParameters} instance.
	 * <p>
	 * The query parameter names and values will be decoded from URL representation, using the <code>UTF-8</code>
	 * charset.
	 * </p>
	 * @param queryParameters The query parameters holder
	 * @return A new {@link NavigationParameters} to handle given navigation parameters
	 */
	static NavigationParameters create(QueryParameters queryParameters) {
		return create(queryParameters, true);
	}

	/**
	 * Create a new {@link NavigationParameters} instance.
	 * @param queryParameters The query parameters holder
	 * @param decode Whether to decode the parameter names and values from URL representation, using the
	 *        <code>UTF-8</code> charset
	 * @return A new {@link NavigationParameters} to handle given navigation parameters
	 */
	static NavigationParameters create(QueryParameters queryParameters, boolean decode) {
		return new DefaultNavigationParameters(
				(queryParameters != null) ? queryParameters.getParameters() : Collections.emptyMap(), decode);
	}

	/**
	 * Create a new {@link NavigationParameters} instance.
	 * <p>
	 * The query parameter names and values will be decoded from the URL representation, using the <code>UTF-8</code>
	 * charset.
	 * </p>
	 * @param location The URL location representation
	 * @return A new {@link NavigationParameters} to handle given location parameters
	 */
	static NavigationParameters create(Location location) {
		return create(location, true);
	}

	/**
	 * Create a new {@link NavigationParameters} instance.
	 * @param location The URL location representation
	 * @param decode Whether to decode the query parameter names and values from the URL representation, using the
	 *        <code>UTF-8</code> charset
	 * @return A new {@link NavigationParameters} to handle given location parameters
	 */
	static NavigationParameters create(Location location, boolean decode) {
		return create((location != null) ? location.getQueryParameters() : QueryParameters.empty(), decode);
	}

}
