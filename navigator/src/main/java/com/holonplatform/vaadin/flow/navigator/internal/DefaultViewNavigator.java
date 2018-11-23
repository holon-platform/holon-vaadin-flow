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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.ViewNavigator;
import com.holonplatform.vaadin.flow.navigator.internal.utils.LocationUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;

/**
 * Default {@link ViewNavigator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultViewNavigator implements ViewNavigator {

	private static final long serialVersionUID = -5365345141554389835L;

	/**
	 * The UI reference
	 */
	private final WeakReference<UI> uiReference;

	/**
	 * Constructor.
	 * @param ui The {@link UI} to which the navigator is bound (not null)
	 */
	public DefaultViewNavigator(UI ui) {
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
		navigateToLocation(viewLocation.getPath().orElse(""),
				viewLocation.getQuery().map(q -> LocationUtils.getQueryParameters(q)).orElse(Collections.emptyMap()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.ViewNavigator#navigateToLocation(java.lang.String, java.util.Map)
	 */
	@Override
	public void navigateToLocation(String location, Map<String, List<String>> queryParameters) {
		getUI().navigate((location != null) ? location : "",
				(queryParameters != null) ? new QueryParameters(queryParameters) : QueryParameters.empty());
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
