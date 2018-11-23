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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.vaadin.flow.navigator.internal.DefaultViewNavigator;
import com.holonplatform.vaadin.flow.navigator.internal.ViewNavigatorRegistry;
import com.vaadin.flow.component.UI;

/**
 * TODO
 */
public interface ViewNavigator extends Serializable {

	/**
	 * Navigate to the given location.
	 * <p>
	 * The location may include a set of query path parameters, using the default <code>?</code> separator.
	 * </p>
	 * <p>
	 * Besides the navigation to the given <code>location</code> (updating the UI to show the corresponding view), this
	 * method also updates the browser location (and page history).
	 * </p>
	 * @param location The location to navigate to. If <code>null</code>, the default (<code>""</code>) location will be
	 *        used
	 */
	void navigateToLocation(String location);

	/**
	 * Navigate to the given path, using the provided URL query parameters.
	 * <p>
	 * Besides the navigation to the given location (updating the UI to show the corresponding view), this method also
	 * updates the browser location (and page history).
	 * </p>
	 * @param path The path to navigate to. If <code>null</code>, the default (<code>""</code>) path will be used
	 * @param queryParameters the query parameters to use for navigation
	 */
	void navigateToLocation(String path, Map<String, List<String>> queryParameters);

	/**
	 * Navigates to the default view, i.e. navigates to the empty <code>""</code> route path.
	 */
	void navigateToDefault();

	/**
	 * Navigates back.
	 * <p>
	 * This has the same effect as if the user would press the back button in the browser.
	 * </p>
	 */
	void navigateBack();

	// ------- getters

	/**
	 * Get the {@link ViewNavigator} bound to the current UI, if available.
	 * @return Optional {@link ViewNavigator} bound to the current UI
	 */
	static Optional<ViewNavigator> get() {
		return Optional.ofNullable(UI.getCurrent()).flatMap(ui -> get(ui));
	}

	/**
	 * Get the {@link ViewNavigator} bound to the current UI, throwing an {@link IllegalStateException} if the current
	 * UI is not available or no {@link ViewNavigator} is bound to the current UI.
	 * @return The {@link ViewNavigator} bound to the current UI
	 * @throws IllegalStateException If the current UI is not available or no ViewNavigator is bound to the current UI
	 */
	static ViewNavigator require() {
		return require(Optional.ofNullable(UI.getCurrent())
				.orElseThrow(() -> new IllegalStateException("The current UI is not available")));
	}

	/**
	 * Get the {@link ViewNavigator} bound to given UI, if available.
	 * @param ui The UI (not null)
	 * @return Optional {@link ViewNavigator} bound to given UI
	 */
	static Optional<ViewNavigator> get(UI ui) {
		return ViewNavigatorRegistry.getCurrent().getNavigator(ui);
	}

	/**
	 * Get the {@link ViewNavigator} bound to given UI, throwing an {@link IllegalStateException} if not available.
	 * @param ui The UI (not null)
	 * @return The {@link ViewNavigator} bound to given UI
	 * @throws IllegalStateException If a ViewNavigator is not available for given UI
	 */
	static ViewNavigator require(UI ui) {
		return get(ui).orElseThrow(() -> new IllegalStateException("No ViewNavigator available for UI [" + ui + "]"));
	}

	// ------- builders

	/**
	 * Create a new {@link ViewNavigator} bound to given UI.
	 * @param ui The {@link UI} to which the navigator is bound (not null)
	 * @return A new {@link ViewNavigator} instance
	 */
	static ViewNavigator create(UI ui) {
		return new DefaultViewNavigator(ui);
	}

	// ------- exceptions

	/**
	 * Exception related to navigation errors.
	 */
	public class ViewNavigationException extends RuntimeException {

		private static final long serialVersionUID = -5449225408415110424L;

		/**
		 * The navigation location to which this exception is related.
		 */
		private final String location;

		/**
		 * Constructor with location and error message.
		 * @param location Navigation location
		 * @param message Error message
		 */
		public ViewNavigationException(String location, String message) {
			super(message);
			this.location = location;
		}

		/**
		 * Constructor with location and nested exception.
		 * @param location Navigation location
		 * @param cause Nested exception
		 */
		public ViewNavigationException(String location, Throwable cause) {
			this(location,
					(location != null) ? "Failed to navigate to location [" + location + "]" : "Navigation failed",
					cause);
		}

		/**
		 * Constructor with location, error message and nested exception.
		 * @param location Navigation location
		 * @param message Error message
		 * @param cause Nested exception
		 */
		public ViewNavigationException(String location, String message, Throwable cause) {
			super(message, cause);
			this.location = location;
		}

		/**
		 * Get the navigation location to which this exception is related, if available.
		 * @return Optional navigation location
		 */
		public Optional<String> getLocation() {
			return Optional.ofNullable(location);
		}

	}

}
