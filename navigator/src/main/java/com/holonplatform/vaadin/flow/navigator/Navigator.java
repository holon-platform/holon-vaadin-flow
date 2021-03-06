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

import com.holonplatform.core.Context;
import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigator;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigator.DefaultNavigationBuilder;
import com.holonplatform.vaadin.flow.navigator.internal.NavigatorRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Router;

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
 * The {@link #get()} static method can be used to obtain the {@link Navigator} reference for the current UI.
 * </p>
 * 
 * @since 5.2.0
 * 
 * @see QueryParameter
 * @see NavigationParameterMapper
 */
public interface Navigator extends Serializable {

	/**
	 * Default navigation failed error message.
	 */
	public static final String DEFAULT_NAVIGATION_FAILED_MESSAGE = "Could not navigate to path";
	/**
	 * Default navigation failed error message localization code.
	 */
	public static final String DEFAULT_NAVIGATION_FAILED_MESSAGE_CODE = "com.holonplatform.vaadin.flow.navigator.error";

	/**
	 * Default {@link Context} resource key.
	 */
	public static final String CONTEXT_KEY = Navigator.class.getName();

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
	 * @param queryParameters A map of query parameters, which associates the parameter names to their values
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
	 * @param queryParameters A map of query parameters, which associates the parameter names to their values
	 */
	default void navigateTo(Class<? extends Component> navigationTarget, Map<String, Object> queryParameters) {
		navigateTo(getUrl(navigationTarget), queryParameters);
	}

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
	 * Navigate to the given navigation target and provide the given <code>pathParameter</code> in the URL.
	 * <p>
	 * The navigation target must be declared as a route and implement the {@link HasUrlParameter} interface.
	 * </p>
	 * @param <T> Path parameter type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @param pathParameter The parameter to include into the navigation url
	 */
	default <T, C extends Component & HasUrlParameter<T>> void navigateTo(Class<? extends C> navigationTarget,
			T pathParameter) {
		navigateTo(navigationTarget, Collections.singletonList(pathParameter), Collections.emptyMap());
	}

	/**
	 * Navigate to the given navigation target and provide the given <code>pathParameters</code> in the URL.
	 * <p>
	 * The navigation target must be declared as a route and implement the {@link HasUrlParameter} interface.
	 * </p>
	 * @param <T> Path parameter type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @param pathParameters The parameters to include into the navigation url
	 */
	default <T, C extends Component & HasUrlParameter<T>> void navigateTo(Class<? extends C> navigationTarget,
			List<T> pathParameters) {
		navigateTo(navigationTarget, pathParameters, Collections.emptyMap());
	}

	/**
	 * Navigate to the given navigation target, providing the given <code>pathParameter</code> in the URL and the given
	 * query parameters.
	 * <p>
	 * The navigation target must be declared as a route and implement the {@link HasUrlParameter} interface.
	 * </p>
	 * @param <T> Path parameter type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @param pathParameter The parameter to include into the navigation url (not null)
	 * @param queryParameters A map of query parameters, which associates the parameter names to their values
	 */
	default <T, C extends Component & HasUrlParameter<T>> void navigateTo(Class<? extends C> navigationTarget,
			T pathParameter, Map<String, Object> queryParameters) {
		navigateTo(navigationTarget, Collections.singletonList(pathParameter), queryParameters);
	}

	/**
	 * Navigate to the given navigation target, providing the given <code>pathParameters</code> in the URL and the given
	 * query parameters.
	 * <p>
	 * The navigation target must be declared as a route and implement the {@link HasUrlParameter} interface.
	 * </p>
	 * @param <T> Path parameter type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @param pathParameters The parameters to include into the navigation url (not null)
	 * @param queryParameters A map of query parameters, which associates the parameter names to their values
	 */
	default <T, C extends Component & HasUrlParameter<T>> void navigateTo(Class<? extends C> navigationTarget,
			List<T> pathParameters, Map<String, Object> queryParameters) {
		navigateTo(getUrl(navigationTarget, pathParameters), queryParameters);
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
	 * Navigates to the previous route, if available. If a previous route is not available, navigates to the default
	 * route.
	 * <p>
	 * This method can be used to navigate back in the route targets sequence handled by the UI Router or Navigator, it
	 * has not the same effect and purpose of the {@link #navigateBack()} method, which can be used to navigate back in
	 * the browser history.
	 * </p>
	 * @see #navigateToDefault()
	 */
	void navigateToPrevious();

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
	 * Gets a {@link NavigationBuilder} to fluently build a navigation location for the given
	 * <code>navigationTarget</code>, including any URL query parameter.
	 * <p>
	 * The {@link NavigationBuilder#navigate()} can be used to trigger the actual navigation. Otherwise, the navigation
	 * location can be obtained as a {@link Location}, using {@link NavigationBuilder#asLocation()} or as a URL, using
	 * {@link NavigationBuilder#asLocationURL()}.
	 * </p>
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @return A new {@link NavigationBuilder}
	 */
	default NavigationBuilder navigation(Class<? extends Component> navigationTarget) {
		return navigation(getUrl(navigationTarget));
	}

	/**
	 * Get the registered URL for given navigation target class.
	 * <p>
	 * This method ignores any declared URL parameter, and always returns the navigation target base URL. To obtain the
	 * URL for a navigation target with required URL parameters, use {@link #getUrl(Class, List)} or
	 * {@link #getUrl(Class, Object)}.
	 * </p>
	 * @param navigationTarget The navigation target to get URL for (not null)
	 * @return The URL for the given navigation target
	 */
	String getUrl(Class<? extends Component> navigationTarget);

	/**
	 * Get the registered URL for given navigation target class, including the given <code>parameter</code> in the url.
	 * @param <T> URL parameter type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to get url for (not null)
	 * @param parameter The parameter to include in the generated URL (not null)
	 * @return The URL for the given navigation target, including the given URL parameter
	 */
	default <T, C extends Component & HasUrlParameter<T>> String getUrl(Class<? extends C> navigationTarget,
			T parameter) {
		return getUrl(navigationTarget, Collections.singletonList(parameter));
	}

	/**
	 * Get the registered URL for given navigation target class, including the given <code>parameters</code> in the url.
	 * @param <T> URL parameters type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to get url for (not null)
	 * @param parameters The parameters to include in the generated URL (not null)
	 * 
	 * @return The URL for the given navigation target, including the given URL parameters
	 */
	@SuppressWarnings("unchecked")
	default <T, C extends Component & HasUrlParameter<T>> String getUrl(Class<? extends C> navigationTarget,
			T... parameters) {
		return getUrl(navigationTarget, Arrays.asList(parameters));
	}

	/**
	 * Get the registered URL for given navigation target class, including the given <code>parameters</code> in the url.
	 * @param <T> URL parameters type
	 * @param <C> Navigation target type
	 * @param navigationTarget The navigation target to get url for (not null)
	 * @param parameters The parameters to include in the generated URL (not null)
	 * 
	 * @return The URL for the given navigation target, including the given URL parameters
	 */
	<T, C extends Component & HasUrlParameter<T>> String getUrl(Class<? extends C> navigationTarget,
			List<T> parameters);

	/**
	 * Add a {@link NavigationChangeListener} to listen to navigation target/location changes.
	 * @param navigationChangeListener The listener to add (not null)
	 * @return The registration handler
	 */
	Registration addNavigationChangeListener(NavigationChangeListener navigationChangeListener);

	// ------- getter

	/**
	 * Get the {@link Navigator} bound to the current UI.
	 * <p>
	 * If a {@link Navigator} is available as a {@link Context} resource using the {@link #CONTEXT_KEY} resource key,
	 * that instance is returned.
	 * </p>
	 * <p>
	 * Otherwise, if a {@link Navigator} session registry is available, it is used to obtain the {@link Navigator} bound
	 * the current UI, or a new {@link Navigator} instance is created and returned is the session registry is missing.
	 * </p>
	 * <p>
	 * When a {@link Navigator} is not available as a {@link Context} resource, the {@link UI#getCurrent()} method is
	 * used to obtain the current UI and if it is not available an {@link IllegalStateException} is thrown.
	 * </p>
	 * @return The {@link Navigator} bound to the current UI
	 * @throws IllegalStateException If a {@link Navigator} is not available from {@link Context} and the current UI is
	 *         not available through {@link UI#getCurrent()}
	 */
	static Navigator get() {
		// check context
		return Context.get().resource(CONTEXT_KEY, Navigator.class).orElseGet(() -> {
			// use session registry, if available
			final UI ui = Optional.ofNullable(UI.getCurrent())
					.orElseThrow(() -> new IllegalStateException("A current UI is not available"));
			return NavigatorRegistry.getSessionRegistry().map(r -> r.getOrCreateNavigator(ui, u -> create(u)))
					.orElseGet(() -> create(ui));
		});
	}

	// ------- creator

	/**
	 * Create a new {@link Navigator} bound to given UI.
	 * <p>
	 * The recomended way to obtain a {@link Navigator} reference is to use the {@link #get()} method.
	 * </p>
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
	public interface NavigationBuilder extends NavigationURLBuilder<NavigationBuilder> {

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
