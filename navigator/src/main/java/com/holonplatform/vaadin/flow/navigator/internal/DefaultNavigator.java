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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.NavigationChangeListener;
import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.internal.utils.LocationUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.shared.Registration;

/**
 * Default {@link Navigator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultNavigator implements Navigator {

	private static final long serialVersionUID = -5365345141554389835L;

	/**
	 * The UI reference
	 */
	private final WeakReference<UI> uiReference;

	/**
	 * Constructor.
	 * @param ui The {@link UI} to which the navigator is bound (not null)
	 */
	public DefaultNavigator(UI ui) {
		super();
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		this.uiReference = new WeakReference<>(ui);
	}

	/**
	 * Get the {@link UI} reference.
	 * @return the UI reference
	 * @throws IllegalStateException If the navigator UI reference is no more available
	 */
	protected UI getUI() {
		final UI ui = uiReference.get();
		if (ui == null) {
			throw new IllegalStateException("The navigator UI reference is no more available");
		}
		return ui;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.ViewNavigator#navigateToLocation(java.lang.String)
	 */
	@Override
	public void navigateToLocation(String location) {
		final ViewLocation viewLocation = getViewLocation(location);
		navigate(viewLocation.getPath().orElse(""),
				viewLocation.getQuery().map(q -> LocationUtils.getQueryParameters(q)).orElse(Collections.emptyMap()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.ViewNavigator#navigateTo(java.lang.String, java.util.Map)
	 */
	@Override
	public void navigateTo(String path, Map<String, Object> queryParameters) {
		navigate(path, serializeQueryParameters(queryParameters));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.Navigator#navigateTo(java.lang.Class, java.util.Map)
	 */
	@Override
	public void navigateTo(Class<? extends Component> navigationTarget, Map<String, Object> queryParameters) {
		navigate(getUI().getRouter().getUrlBase(navigationTarget), serializeQueryParameters(queryParameters));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.ViewNavigator#navigateToDefault()
	 */
	@Override
	public void navigateToDefault() {
		getUI().navigate("");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.ViewNavigator#navigateBack()
	 */
	@Override
	public void navigateBack() {
		getUI().getPage().getHistory().back();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.Navigator#addNavigationChangeListener(com.holonplatform.vaadin.flow.navigator.NavigationChangeListener)
	 */
	@Override
	public Registration addNavigationChangeListener(NavigationChangeListener navigationChangeListener) {
		ObjectUtils.argumentNotNull(navigationChangeListener, "NavigationChangeListener must be not null");
		return getUI().addAfterNavigationListener(e -> {
			HasElement target = null;
			if (e.getActiveChain() != null && !e.getActiveChain().isEmpty()) {
				target = e.getActiveChain().get(0);
			}
			navigationChangeListener.onNavigationChange(new DefaultNavigationChangeEvent(e.getLocation(), target));
		});
	}

	/**
	 * Navigate to given path.
	 * @param path The navigation path. If <code>null</code>, the default <code>""</code> path will be used.
	 * @param parameters Optional query parameters
	 */
	protected void navigate(String path, Map<String, List<String>> queryParameters) {
		getUI().navigate((path != null) ? path : "",
				(queryParameters != null) ? new QueryParameters(queryParameters) : QueryParameters.empty());
	}

	/**
	 * Serialize given query parameters.
	 * @param parameters The query parameters
	 * @return The serialized query parameters
	 */
	protected static Map<String, List<String>> serializeQueryParameters(Map<String, Object> parameters) {
		if (parameters != null && !parameters.isEmpty()) {
			final NavigationParameterMapper mapper = NavigationParameterMapper.get();
			final Map<String, List<String>> serialized = new HashMap<>(parameters.size());
			for (Entry<String, Object> e : parameters.entrySet()) {
				serialized.put(e.getKey(), mapper.serialize(e.getValue()));
			}
			return serialized;
		}
		return Collections.emptyMap();
	}

	/**
	 * Default {@link NavigationBuilder} implementation.
	 * 
	 * @since 5.2.0
	 */
	public static class DefaultNavigationBuilder implements NavigationBuilder {

		private static final long serialVersionUID = -3835424377998263190L;

		private final Navigator navigator;

		private final String path;

		private final Map<String, List<Object>> queryParameters = new HashMap<>(8);

		/**
		 * Constructor.
		 * @param path The navigation path
		 */
		public DefaultNavigationBuilder(Navigator navigator, String path) {
			super();
			ObjectUtils.argumentNotNull(navigator, "Navigator must be not null");
			this.navigator = navigator;
			this.path = (path != null) ? path : "";
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.navigator.ViewNavigator.NavigationBuilder#withQueryParameter(java.lang.String,
		 * java.util.List)
		 */
		@Override
		public NavigationBuilder withQueryParameter(String name, List<?> values) {
			if (name == null || name.trim().equals("")) {
				throw new IllegalArgumentException("Parameter name must be nt null or blank");
			}
			if (values != null && !values.isEmpty()) {
				final List<Object> parameterValues = queryParameters.computeIfAbsent(name, n -> new LinkedList<>());
				values.stream().filter(v -> v != null).forEach(v -> parameterValues.add(v));
			}
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.navigator.Navigator.NavigationBuilder#asLocation()
		 */
		@Override
		public Location asLocation() {
			return new Location(path, new QueryParameters(serializeQueryParameters(getQueryParameters())));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.navigator.ViewNavigator.NavigationBuilder#navigate()
		 */
		@Override
		public void navigate() {
			navigator.navigateTo(path, getQueryParameters());
		}

		/**
		 * Get the declared query parameters as a map of parameter names and values.
		 * @return The query parameters map
		 */
		private Map<String, Object> getQueryParameters() {
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

	}

	/**
	 * Get view navigation location representation using given <code>location</code>.
	 * @param location The navigation location
	 * @return A new view navigation location representation using given <code>location</code>
	 */
	private static ViewLocation getViewLocation(String location) {
		return new ViewLocation(location);
	}

	/**
	 * View navigation location representation.
	 * 
	 * @since 5.2.0
	 */
	private static class ViewLocation implements Serializable {

		private static final long serialVersionUID = 7865173610251184055L;

		private final String path;
		private final String query;

		/**
		 * Construction
		 * @param location The navigation URL
		 */
		public ViewLocation(String location) {
			super();
			this.path = getPathPart(location);
			this.query = getQueryPart(location);
		}

		/**
		 * Get the navigation URL path part, if available.
		 * @return Optional navigation URL path part
		 */
		public Optional<String> getPath() {
			return Optional.ofNullable(path);
		}

		/**
		 * Get the navigation URL query part, if available.
		 * @return Optional navigation URL query part
		 */
		public Optional<String> getQuery() {
			return Optional.ofNullable(query);
		}

		/**
		 * Get the navigation URL path part.
		 * @param location The full navigation location
		 * @return the navigation URL path part, or <code>null</code> if not available
		 */
		private static String getPathPart(String location) {
			if (location != null) {
				int idx = location.indexOf('?');
				if (idx > 0) {
					return getPart(location.substring(0, idx));
				}
			}
			return location;
		}

		/**
		 * Get the navigation URL query part.
		 * @param location The full navigation location
		 * @return the navigation URL query part, or <code>null</code> if not available
		 */
		private static String getQueryPart(String location) {
			if (location != null) {
				int idx = location.indexOf('?');
				if (idx > -1 && idx < (location.length() - 1)) {
					return getPart(location.substring(idx + 1));
				}
			}
			return null;
		}

		/**
		 * Get the given part trimming any whitespace.
		 * @param part The part
		 * @return The trimmed part
		 */
		private static String getPart(String part) {
			if (part != null && part.trim().equals("")) {
				return null;
			}
			return part;
		}

	}

}
