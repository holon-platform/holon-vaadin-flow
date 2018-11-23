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
package com.holonplatform.vaadin.flow.navigator.internal.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

/**
 * Navigation parameters value serializer and deserializer.
 *
 * @since 5.2.0
 */
public interface NavigationParameterMapper {

	/**
	 * Serialize given parameter <code>value</code>.
	 * @param value Parameter value (may be null)
	 * @return A collection of serialized parameter values, empty if none
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	Collection<String> serialize(Object value) throws InvalidNavigationParameterException;

	/**
	 * Deserialize given parameter <code>values</code>.
	 * @param <T> Parameter type
	 * @param type Parameter type (not null)
	 * @param values Parameter values (may be null)
	 * @return A collection of deserialized parameter values, empty if none
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	<T> Collection<T> deserialize(Class<T> type, List<String> values) throws InvalidNavigationParameterException;

	/**
	 * Deserialize given parameter <code>value</code>.
	 * @param <T> Parameter type
	 * @param type Parameter type (not null)
	 * @param value Parameter value (may be null)
	 * @return Optional deserialized parameter value
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	default <T> Optional<T> deserialize(Class<T> type, String value) throws InvalidNavigationParameterException {
		return deserialize(type, (value == null) ? Collections.emptyList() : Collections.singletonList(value)).stream()
				.findFirst();
	}

	/**
	 * Get the default {@link NavigationParameterMapper}.
	 * @return the default {@link NavigationParameterMapper}
	 */
	static NavigationParameterMapper getDefault() {
		return DefaultNavigationParameterMapper.INSTANCE;
	}

}
