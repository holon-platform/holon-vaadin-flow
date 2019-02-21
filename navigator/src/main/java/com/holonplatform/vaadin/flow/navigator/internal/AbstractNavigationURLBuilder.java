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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.NavigationURLBuilder;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

/**
 * Base {@link NavigationURLBuilder} implementation.
 * 
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public abstract class AbstractNavigationURLBuilder<B extends NavigationURLBuilder<B>>
		implements NavigationURLBuilder<B> {

	private static final long serialVersionUID = -3867878041666781468L;

	protected final StringBuilder path;

	protected final Map<String, List<Object>> queryParameters = new HashMap<>(8);

	protected boolean encodeQueryParameters = true;

	/**
	 * Constructor.
	 * @param path The navigation path
	 */
	public AbstractNavigationURLBuilder(String path) {
		super();
		this.path = new StringBuilder();
		if (path != null) {
			this.path.append(path);
		}
	}

	/**
	 * Get the actual builder.
	 * @return the actual builder
	 */
	protected abstract B getBuilder();

	/**
	 * Get the declared query parameters as a map of parameter names and values.
	 * @return The query parameters map
	 */
	protected Map<String, Object> getQueryParameters() {
		Map<String, Object> parameters = new HashMap<>(queryParameters.size());
		for (Entry<String, List<Object>> queryParameter : queryParameters.entrySet()) {
			if (queryParameter.getValue() != null && !queryParameter.getValue().isEmpty()) {
				parameters.put(queryParameter.getKey(),
						(queryParameter.getValue().size() == 1) ? queryParameter.getValue().get(0)
								: queryParameter.getValue());
			}
		}
		return parameters;
	}

	/**
	 * Get the navigation path.
	 * @return the navigation path
	 */
	protected String getPath() {
		return path.toString();
	}

	/**
	 * Get the {@link Location} using current path and query parameters.
	 * @return The location
	 */
	protected Location getLocation() {
		return getLocation(encodeQueryParameters);
	}

	/**
	 * Get the {@link Location} using current path and query parameters.
	 * @param encode Whether to URL-encode the query parameters
	 * @return The location
	 */
	protected Location getLocation(boolean encode) {
		final Map<String, List<String>> queryParameters = NavigationParameterUtils
				.serializeQueryParameters(getQueryParameters());
		return new Location(path.toString(), new QueryParameters(
				encode ? NavigationParameterUtils.encodeParameters(queryParameters) : queryParameters));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationURLBuilder#withQueryParameter(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public <T> B withQueryParameter(String name, List<T> values) {
		if (name == null || name.trim().equals("")) {
			throw new IllegalArgumentException("Parameter name must be nt null or blank");
		}
		if (values != null && !values.isEmpty()) {
			final List<Object> parameterValues = queryParameters.computeIfAbsent(name, n -> new LinkedList<>());
			values.stream().filter(v -> v != null).forEach(v -> parameterValues.add(v));
		}
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationURLBuilder#withPathParameter(java.lang.Object)
	 */
	@Override
	public <T> B withPathParameter(T pathParameter) {
		ObjectUtils.argumentNotNull(pathParameter, "Path parameter value must be not null");
		NavigationParameterUtils.serializePathParameters(Collections.singletonList(pathParameter)).forEach(p -> {
			path.append("/");
			path.append(p);
		});
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationURLBuilder#encodeQueryParameters(boolean)
	 */
	@Override
	public B encodeQueryParameters(boolean encode) {
		this.encodeQueryParameters = encode;
		return getBuilder();
	}

}
