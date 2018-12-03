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

import javax.servlet.ServletContext;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;

/**
 * Base {@link NavigationTargetConfigurationRegistry} initializer.
 *
 */
public abstract class AbstractNavigationTargetConfigurationRegistryInitializer {

	/**
	 * Get or create the {@link NavigationTargetConfigurationRegistry} bound to given servlet context.
	 * @param servletContext The servlet context (not null)
	 * @return The {@link NavigationTargetConfigurationRegistry} bound to given servlet context
	 */
	protected NavigationTargetConfigurationRegistry getRegistry(ServletContext servletContext) {
		return NavigationTargetConfigurationRegistry.servletContext(servletContext);
	}

	/**
	 * Check for valid navigation target class.
	 * @param clazz The class
	 * @return <code>true</code> if valid navigation target class, <code>false</code> otherwise
	 */
	protected boolean isNavigationTargetClass(Class<?> clazz) {
		return clazz.isAnnotationPresent(Route.class) && Component.class.isAssignableFrom(clazz);
	}

}
