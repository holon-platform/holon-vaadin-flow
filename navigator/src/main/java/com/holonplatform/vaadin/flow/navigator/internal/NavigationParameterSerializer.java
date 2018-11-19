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
package com.holonplatform.vaadin.flow.navigator.internal;

import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

/**
 * Navigation parameters value serializer and deserializer.
 *
 * @since 5.2.0
 */
public interface NavigationParameterSerializer {

	/**
	 * Serialize given parameter <code>value</code>.
	 * @param value Parameter value (may be null)
	 * @return Serialized parameter value (may be null)
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	String serialize(Object value) throws InvalidNavigationParameterException;

	/**
	 * Serialize given parameter <code>value</code>.
	 * @param <T> Parameter type
	 * @param type Parameter type (not null)
	 * @param value Parameter value (may be null)
	 * @return Deserialized parameter value (may be null)
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	<T> T deserialize(Class<T> type, String value) throws InvalidNavigationParameterException;

	/**
	 * Get the default {@link NavigationParameterSerializer}.
	 * @return the default {@link NavigationParameterSerializer}
	 */
	static NavigationParameterSerializer getDefault() {
		return DefaultNavigationParameterSerializer.INSTANCE;
	}

}
