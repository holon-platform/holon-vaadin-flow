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
import java.util.Map;
import java.util.WeakHashMap;

import com.holonplatform.vaadin.flow.navigator.exceptions.NavigationTargetConfigurationException;

/**
 * {@link NavigationTargetConfiguration} registry.
 *
 * @since 5.2.0
 */
public final class NavigationTargetConfigurationRegistry implements Serializable {

	private static final long serialVersionUID = -4473679591541295868L;

	private final static Map<Class<?>, NavigationTargetConfiguration> configurations = new WeakHashMap<>();

	private NavigationTargetConfigurationRegistry() {
	}

	/**
	 * Get the navigation target class configuration.
	 * @param navigationTargetClass navigation target class (not null)
	 * @return The {@link NavigationTargetConfigurationException} for given navigation target class
	 * @throws NavigationTargetConfigurationException If an error occurred
	 */
	public static NavigationTargetConfiguration getNavigationTargetConfiguration(Class<?> navigationTargetClass)
			throws NavigationTargetConfigurationException {
		return configurations.computeIfAbsent(navigationTargetClass, ntc -> NavigationTargetConfiguration.create(ntc));
	}

}
