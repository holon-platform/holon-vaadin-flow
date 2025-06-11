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
package com.holonplatform.vaadin.flow.navigator.internal.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.navigator.annotations.OnShow;
import com.vaadin.flow.server.VaadinContext;

/**
 * Navigation target class (view) configuration.
 * 
 * @since 5.2.0
 */
public interface NavigationTargetConfiguration extends Serializable {

	/**
	 * Get the navigation target class.
	 * @return the navigation target class
	 */
	Class<?> getNavigationTarget();

	/**
	 * Get the navigation target route path, if available.
	 * @return Optional navigation target route path
	 */
	Optional<String> getRoutePath();

	/**
	 * Get the navigation target caption, if available.
	 * @return Optional navigation target caption
	 */
	Optional<Localizable> getCaption();

	/**
	 * Get the navigation target class query parameters definitions, if any.
	 * @return the navigation target class query parameters definitions, or an empty map if none
	 */
	Map<String, QueryParameterDefinition> getQueryParameters();

	/**
	 * Get the navigation target class {@link OnShow} annotated methods, if any.
	 * @return the navigation target class {@link OnShow} annotated methods, or an empty list if none
	 */
	List<Method> getOnShowMethods();

	/**
	 * Check whether authentication is required to access this navigation target.
	 * @return whether authentication is required to access this navigation targe
	 */
	boolean isAuthenticationRequired();

	/**
	 * Get the navigation target class {@link Authenticate} annotation, if available.
	 * @return Optional navigation target class {@link Authenticate} annotation
	 */
	Optional<Authenticate> getAuthentication();

	/**
	 * Get the authorization roles required to access this navigation target, if any.
	 * @return the authorization roles, an empty set if none
	 */
	Set<String> getAuthorization();

	// ------- builders

	/**
	 * Create a new {@link NavigationTargetConfiguration} for given navigation target.
	 * @param navigationTarget The navigation target class (not null)
	 * @param vaadinContext The Vaadin context
	 * @return A new {@link NavigationTargetConfiguration}
	 */
	static NavigationTargetConfiguration create(Class<?> navigationTarget, VaadinContext vaadinContext) {
		return new DefaultNavigationTargetConfiguration(navigationTarget, vaadinContext);
	}

	// ------ parameter definitions

	/**
	 * URL query navigation parameter definition.
	 */
	public interface QueryParameterDefinition extends NavigationParameterDefinition {

		/**
		 * Get the query parameter name.
		 * @return the query parameter name.
		 */
		String getName();

	}

	/**
	 * Base navigation parameter definition.
	 */
	public interface NavigationParameterDefinition extends Serializable {

		/**
		 * Get the parameter value type.
		 * @return the parameter value type
		 */
		Class<?> getType();

		/**
		 * Get the parameter container type.
		 * @return the parameter container type
		 */
		ParameterContainerType getParameterContainerType();

		/**
		 * Get whether the parameter is required.
		 * @return <code>true</code> if parameter is required, <code>false</code> otherwise
		 */
		boolean isRequired();

		/**
		 * Get the parameter default value, if available.
		 * @return Optional parameter default value
		 */
		Optional<Object> getDefaultValue();

		/**
		 * Get the navigation target class field bound to this parameter.
		 * @return the navigation target class field bound to this parameter
		 */
		Field getField();

		/**
		 * Get the navigation target class field write method, if available.
		 * @return Optional navigation target class field write method
		 */
		Optional<Method> getWriteMethod();

	}

	/**
	 * Parameter container types.
	 */
	public enum ParameterContainerType {

		/**
		 * None
		 */
		NONE,

		/**
		 * {@link Optional} type container
		 */
		OPTIONAL,

		/**
		 * {@link Set} type contanier
		 */
		SET,

		/**
		 * {@link List} type contanier
		 */
		LIST;

	}

}
