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

import java.util.Map;
import java.util.WeakHashMap;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.exceptions.NavigationTargetConfigurationException;

/**
 * Default {@link NavigationTargetConfigurationRegistry} implementation.
 *
 * @since 5.2.0
 */
public class DefaultNavigationTargetConfigurationRegistry implements NavigationTargetConfigurationRegistry {

	private static final long serialVersionUID = -8266974913448585865L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Configurations
	 */
	private Map<Class<?>, NavigationTargetConfiguration> configurations = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfigurationRegistry#getConfiguration(
	 * java.lang.Class)
	 */
	@Override
	public NavigationTargetConfiguration getConfiguration(Class<?> navigationTarget)
			throws NavigationTargetConfigurationException {
		ObjectUtils.argumentNotNull(navigationTarget, "Navigation target class must be not null");
		return configurations.computeIfAbsent(navigationTarget, ntc -> {
			LOGGER.debug(() -> "Create NavigationTargetConfiguration for [" + navigationTarget.getName() + "]");
			return NavigationTargetConfiguration.create(ntc);
		});
	}

}
