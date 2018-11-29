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
import java.util.Map;
import java.util.Optional;

import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigator;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigator.DefaultNavigationBuilder;
import com.holonplatform.vaadin.flow.navigator.internal.NavigatorRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.shared.Registration;

/**
 * Handles the navigation between the available UI <em>routes</em>, relying on the UI {@link Router} to implement the
 * actual routing.
 * <p>
 * A route is decalred using the {@link Route} annotation and bound to a navigation path. See the {@link Router}
 * documentation for more information about route definitions and routing layouts.
 * </p>
 * <p>
 * The {@link Navigator} supports navigation parameters declaration and mapping, performing the parameters value
 * serialization to translate the Java type values into the {@link String} type URL parameter values.
 * </p>
 * <p>
 * It provides the {@link #navigateBack()} method to navigate back in the browser history.
 * </p>
 * <p>
 * The {@link #get()} and {@link #require()} static methods can be used to obtain the {@link Navigator} reference for
 * the current UI.
 * </p>
 * 
 * @since 5.2.0
 * 
 * @see QueryParameter
 * @see NavigationParameterMapper
 */
public interface Navigator extends Serializable {

	/**
	 * Navigate to the given location.
	 * <p>
	 * The location may include a set of query path parameters, using the default <code>?</code> separator.
	 * </p>
	 * @param location The location to navigate to. If <code>null</code>, the default (<code>""</code>) location will be
	 *        used
	 */
	void navigateToLocation(String location);

	/**
	 * Navigate to the given path.
	 * @param path The path to navigate to. If <code>null</code>, the default (<code>""</code>) path will be used
	 */
	default void navigateTo(String path) {
		navigateTo(path, Collections.emptyMap());
	}

	/**
	 * Navigate to the given path, using the provided query parameters.
	 * <p>
	 * The query parameter values will be serialized to be included in the navigation location URL, using a
	 * {@link NavigationParameterMapper}. See the {@link QueryParameter} annotation to learn about the supported query
	 * parameter value types.
	 * </p>
	 * @param path The path to navigate to. If <code>null</code>, the default (<code>""</code>) path will be used
	 * @param parameters A map of query parameters, which associates the parameter names to their values
	 */
	void navigateTo(String path, Map<String, Object> queryParameters);

	/**
	 * Navigate to the given navigation target, using the provided query parameters.
	 * <p>
	 * The query parameter values will be serialized to be included in the navigation location URL, using a
	 * {@link NavigationParameterMapper}. See the {@link QueryParameter} annotation to learn about the supported query
	 * parameter value types.
	 * </p>
	 * <p>
	 * The navigation target must be declared as a route, for example using the {@link Route} annotation.
	 * </p>
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @param parameters A map of query parameters, which associates the parameter names to their values
	 */
	void navigateTo(Class<? extends Component> navigationTarget, Map<String, Object> queryParameters);

	/**
	 * Navigate to the given navigation target.
	 * <p>
	 * The navigation target must be declared as a route, for example using the {@link Route} annotation.
	 * </p>
	 * @param navigationTarget The navigation target to navigate to (not null)
	 */
	default void navigateTo(Class<? extends Component> navigationTarget) {
		navigateTo(navigationTarget, Collections.emptyMap());
	}

	/**
	 * Navigates to the default navigation target, i.e. navigates to the empty <code>""</code> route path.
	 */
	void navigateToDefault();

	/**
	 * Navigates back.
	 * <p>
	 * This has the same effect as if the user would press the back button in the browser.
	 * </p>
	 */
	void navigateBack();

	/**
	 * Gets a {@link NavigationBuilder} to fluently build a navigation location for the given <code>path</code>,
	 * including any URL query parameter.
	 * <p>
	 * The {@link NavigationBuilder#navigate()} can be used to trigger the actual navigation. Otherwise, the navigation
	 * location can be obtained as a {@link Location}, using {@link NavigationBuilder#asLocation()} or as a URL, using
	 * {@link NavigationBuilder#asLocationURL()}.
	 * </p>
	 * @param path The path to navigate to. If <code>null</code>, the default (<code>""</code>) path will be used
	 * @return A new {@link NavigationBuilder}
	 */
	default NavigationBuilder navigation(String path) {
		return new DefaultNavigationBuilder(this, path);
	}

	/**
	 * Add a {@link NavigationChangeListener} to listen to navigation target/location changes.
	 * @param navigationChangeListener The listener to add (not null)
	 * @return The registration handler
	 */
	Registration addNavigationChangeListener(NavigationChangeListener navigationChangeListener);

	// ------- getters

	/**
	 * Get the {@link Navigator} bound to the current UI, if available.
	 * @return Optional {@link Navigator} bound to the current UI
	 */
	static Optional<Navigator> get() {
		return Optional.ofNullable(UI.getCurrent()).flatMap(ui -> get(ui));
	}

	/**
	 * Get the {@link Navigator} bound to the current UI, throwing an {@link IllegalStateException} if the current UI is
	 * not available or no {@link Navigator} is bound to the current UI.
	 * @return The {@link Navigator} bound to the current UI
	 * @throws IllegalStateException If the current UI is not available or no {@link Navigator} is bound to the current
	 *         UI
	 */
	static Navigator require() {
		return require(Optional.ofNullable(UI.getCurrent())
				.orElseThrow(() -> new IllegalStateException("The current UI is not available")));
	}

	/**
	 * Get the {@link Navigator} bound to given UI, if available.
	 * @param ui The UI (not null)
	 * @return Optional {@link Navigator} bound to given UI
	 */
	static Optional<Navigator> get(UI ui) {
		return NavigatorRegistry.getCurrent().getNavigator(ui);
	}

	/**
	 * Get the {@link Navigator} bound to given UI, throwing an {@link IllegalStateException} if not available.
	 * @param ui The UI (not null)
	 * @return The {@link Navigator} bound to given UI
	 * @throws IllegalStateException If a {@link Navigator} is not available for given UI
	 */
	static Navigator require(UI ui) {
		return get(ui).orElseThrow(() -> new IllegalStateException("No Navigator available for UI [" + ui + "]"));
	}

	// ------- creator

	/**
	 * Create a new {@link Navigator} bound to given UI.
	 * @param ui The {@link UI} to which the navigator is bound (not null)
	 * @return A new {@link Navigator} instance
	 */
	static Navigator create(UI ui) {
		return new DefaultNavigator(ui);
	}

	// ------- builders

	/**
	 * A builder to create a navigation location and trigger the navigation action.
	 * 
	 * @since 5.2.0
	 */
	public interface NavigationBuilder extends Serializable {

		/**
		 * Add query parameter values to be included in the navigation URL.
		 * <p>
		 * If one or more values was previously associated to given parameter <code>name</code>, the given
		 * <code>values</code> will be added to the existing values set.
		 * </p>
		 * @param name The parameter name (not null)
		 * @param values The parameter values
		 * @return this
		 */
		default NavigationBuilder withQueryParameter(String name, Object... values) {
			return withQueryParameter(name, (values != null) ? Arrays.asList(values) : Collections.emptyList());
		}

		/**
		 * Add query parameter values to be included in the navigation URL.
		 * <p>
		 * If one or more values was previously associated to given parameter <code>name</code>, the given
		 * <code>values</code> will be added to the existing values set.
		 * </p>
		 * @param name The parameter name (not null)
		 * @param values The parameter values
		 * @return this
		 */
		NavigationBuilder withQueryParameter(String name, List<?> values);

		/**
		 * Get the navigation location as a {@link Location}, including the navigation path and any declared query
		 * parameter.
		 * @return the navigation {@link Location}
		 */
		Location asLocation();

		/**
		 * Get the navigation location as the URL part which includes the navigation path and any declared query
		 * parameter.
		 * @return the navigation location URL part
		 */
		default String asLocationURL() {
			return asLocation().getPathWithQueryParameters();
		}

		/**
		 * Navigates to the location which is composed by the specified path and the provided query parameter values, if
		 * any.
		 */
		void navigate();

	}

}
