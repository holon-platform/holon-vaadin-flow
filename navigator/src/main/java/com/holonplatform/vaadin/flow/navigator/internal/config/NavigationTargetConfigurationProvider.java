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

import com.holonplatform.vaadin.flow.navigator.exceptions.NavigationTargetConfigurationException;

/**
 * The {@link NavigationTargetConfiguration} provider.
 *
 * @since 5.2.0
 */
public interface NavigationTargetConfigurationProvider extends Serializable {

	/**
	 * Get the navigation target class configuration.
	 * @param navigationTarget navigation target class (not null)
	 * @return The {@link NavigationTargetConfigurationException} for given navigation target class
	 * @throws NavigationTargetConfigurationException If an error occurred
	 */
	NavigationTargetConfiguration getConfiguration(Class<?> navigationTarget)
			throws NavigationTargetConfigurationException;

	/**
	 * Get the {@link NavigationTargetConfigurationProvider} bound to given <code>classLoader</code>.
	 * @param classLoader The ClassLoader
	 * @return the {@link NavigationTargetConfigurationProvider} boudn to given classLoader
	 */
	static NavigationTargetConfigurationProvider get(ClassLoader classLoader) {
		return NavigationTargetConfigurationProviderRegistry.getProvider(classLoader);
	}

}
