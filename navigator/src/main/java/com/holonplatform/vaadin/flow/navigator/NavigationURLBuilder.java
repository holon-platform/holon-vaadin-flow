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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A builder to compose a navigation URL.
 * 
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface NavigationURLBuilder<B extends NavigationURLBuilder<B>> extends Serializable {

	/**
	 * Add query parameter values to be included in the navigation URL.
	 * <p>
	 * If one or more values was previously associated to given parameter <code>name</code>, the given
	 * <code>values</code> will be added to the existing values set.
	 * </p>
	 * @param <T> Parameter type
	 * @param name The parameter name (not null)
	 * @param values The parameter values
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default <T> B withQueryParameter(String name, T... values) {
		return withQueryParameter(name, (values != null) ? Arrays.asList(values) : Collections.emptyList());
	}

	/**
	 * Add query parameter values to be included in the navigation URL.
	 * <p>
	 * If one or more values was previously associated to given parameter <code>name</code>, the given
	 * <code>values</code> will be added to the existing values set.
	 * </p>
	 * @param <T> Parameter type
	 * @param name The parameter name (not null)
	 * @param values The parameter values
	 * @return this
	 */
	<T> B withQueryParameter(String name, List<T> values);

	/**
	 * Add a URL path parameter value to be included in the navigation URL.
	 * @param <T> Parameter type
	 * @param pathParameter The parameter value (not null)
	 * @return this
	 */
	<T> B withPathParameter(T pathParameter);

	/**
	 * Set whether to URL-encode the query parameter names and values, using the <code>UTF-8</code> charset.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @param encode Whether to URL-encode the query parameter
	 * @return this
	 */
	B encodeQueryParameters(boolean encode);

}
