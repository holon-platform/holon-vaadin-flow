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
package com.holonplatform.vaadin.flow.navigator.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
import com.holonplatform.vaadin.flow.navigator.NavigationParameters;
import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

/**
 * Default {@link NavigationParameters} implementation.
 *
 * @since 5.2.2
 */
public class DefaultNavigationParameters implements NavigationParameters {

	private static final long serialVersionUID = -4884756428959813003L;

	private final Map<String, List<String>> queryParameters;

	/**
	 * Constructor.
	 * @param queryParameters Query parameter names and values
	 * @param decode Whether to decode the parameter names and values from URL representation, using the
	 *        <code>UTF-8</code> charset
	 */
	public DefaultNavigationParameters(Map<String, List<String>> queryParameters, boolean decode) {
		super();
		this.queryParameters = (queryParameters != null)
				? (decode ? NavigationParameterUtils.decodeParameters(queryParameters) : queryParameters)
				: Collections.emptyMap();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationParameters#getQueryParameters()
	 */
	@Override
	public Map<String, List<String>> getQueryParameters() {
		return queryParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationParameters#hasQueryParameter(java.lang.String)
	 */
	@Override
	public boolean hasQueryParameter(String name) {
		ObjectUtils.argumentNotNull(name, "The query parameter name must be not null");
		return !getNotNullOrEmptyValues(getQueryParameters().get(name)).isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationParameters#getQueryParameterValues(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> List<T> getQueryParameterValues(String name, Class<T> type) throws InvalidNavigationParameterException {
		ObjectUtils.argumentNotNull(name, "The query parameter name must be not null");
		ObjectUtils.argumentNotNull(type, "The query parameter type must be not null");
		return NavigationParameterMapper.get().deserialize(type,
				getNotNullOrEmptyValues(getQueryParameters().get(name)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationParameters#getQueryParameterValue(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> Optional<T> getQueryParameterValue(String name, Class<T> type)
			throws InvalidNavigationParameterException {
		final List<T> deserialized = NavigationParameterMapper.get().deserialize(type,
				getNotNullOrEmptyValues(getQueryParameters().get(name)));
		return deserialized.isEmpty() ? Optional.empty() : Optional.ofNullable(deserialized.get(0));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationParameters#getQueryParameterValue(java.lang.String,
	 * java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> T getQueryParameterValue(String name, Class<T> type, T defaultValue)
			throws InvalidNavigationParameterException {
		return getQueryParameterValue(name, type).orElse(defaultValue);
	}

	/**
	 * Get the given parameter values, excluding <code>null</code> or blank values.
	 * @param values The parameter values
	 * @return The parameter values without any <code>null</code> or blank value
	 */
	private static List<String> getNotNullOrEmptyValues(List<String> values) {
		if (values != null) {
			return values.stream().filter(v -> v != null).filter(v -> !v.trim().equals(""))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

}
