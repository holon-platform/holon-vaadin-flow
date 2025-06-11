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
import java.util.Set;

import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.exceptions.NavigationTargetConfigurationException;
import com.vaadin.flow.server.VaadinContext;

import jakarta.servlet.ServletContext;

/**
 * Registry for the {@link NavigationTargetConfiguration} bound to navigation target classes.
 * 
 * @since 5.2.0
 */
public interface NavigationTargetConfigurationRegistry extends Serializable {

	/**
	 * Get the navigation target class configuration.
	 * @param navigationTarget navigation target class (not null)
	 * @return The {@link NavigationTargetConfigurationException} for given navigation target class
	 * @throws NavigationTargetConfigurationException If an error occurred
	 */
	NavigationTargetConfiguration getConfiguration(Class<?> navigationTarget, VaadinContext vaadinContext)
			throws NavigationTargetConfigurationException;

	/**
	 * Get whether the registry was pre-initialized with a navigation target configuration set.
	 * @return Whether the registry was pre-initialized
	 */
	boolean isInitialized();

//	/**
//	 * Initialize the registry with the given class set.
//	 * @param classes The initial class set
//	 */
//	void initialize(Set<Class<?>> classes);

	/**
	 * Initialize the registry with the given class set.
	 * @param classes The initial class set
	 * @param vaadinContext The Vaadin context
	 */
	void initialize(Set<Class<?>> classes, VaadinContext vaadinContext);

	/**
	 * Create a new {@link NavigationTargetConfigurationRegistry}.
	 * @return A new {@link NavigationTargetConfigurationRegistry} instance
	 */
	static NavigationTargetConfigurationRegistry create() {
		return new DefaultNavigationTargetConfigurationRegistry();
	}

	/**
	 * Get the {@link NavigationTargetConfigurationRegistry} bound to given ClassLoader.
	 * @param classLoader The ClassLoader, if <code>null</code> the default ClassLoader will be used
	 * @return The {@link NavigationTargetConfigurationRegistry} bound to given ClassLoader
	 */
	static NavigationTargetConfigurationRegistry classLoader(ClassLoader classLoader) {
		return ClassLoaderNavigationTargetConfigurationRegistry
				.getRegistry((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Get the {@link NavigationTargetConfigurationRegistry} bound to the default ClassLoader.
	 * @return The {@link NavigationTargetConfigurationRegistry} bound to the default ClassLoader
	 */
	static NavigationTargetConfigurationRegistry classLoader() {
		return classLoader(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Get the {@link NavigationTargetConfigurationRegistry} bound to given servlet context.
	 * @param servletContext The servlet context (not null)
	 * @return The {@link NavigationTargetConfigurationRegistry} bound to given servlet context
	 */
	static NavigationTargetConfigurationRegistry servletContext(ServletContext servletContext) {
		ObjectUtils.argumentNotNull(servletContext, "ServletContext must be not null");
		Object attribute;
		synchronized (servletContext) {
			attribute = servletContext.getAttribute(NavigationTargetConfigurationRegistry.class.getName());
			if (attribute == null) {
				attribute = NavigationTargetConfigurationRegistry.create();
				servletContext.setAttribute(NavigationTargetConfigurationRegistry.class.getName(), attribute);
			}
		}
		if (attribute instanceof NavigationTargetConfigurationRegistry) {
			return (NavigationTargetConfigurationRegistry) attribute;
		} else {
			throw new IllegalStateException("The servlet context attribute named ["
					+ NavigationTargetConfigurationRegistry.class.getName() + "] is of inconsistent type: "
					+ ((attribute == null) ? null : attribute.getClass().getName()));
		}
	}

}
