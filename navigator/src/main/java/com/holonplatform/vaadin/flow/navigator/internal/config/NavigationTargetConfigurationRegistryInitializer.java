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

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/**
 * Servlet initializer to initialize the {@link NavigationTargetConfigurationRegistry}.
 *
 */
@HandlesTypes({ Route.class, RouteAlias.class })
public class NavigationTargetConfigurationRegistryInitializer
		extends AbstractNavigationTargetConfigurationRegistryInitializer implements ServletContainerInitializer {

	private static final Logger LOGGER = VaadinLogger.create();

	@Override
	public void onStartup(Set<Class<?>> classSet, ServletContext servletContext) throws ServletException {
		LOGGER.debug(() -> "Initializing the servlet context bound NavigationTargetConfigurationRegistry...");
		final NavigationTargetConfigurationRegistry registry = getRegistry(servletContext);
		if (classSet != null) {
			registry.initialize(classSet.stream().filter(this::isNavigationTargetClass).collect(Collectors.toSet()));
		}
		LOGGER.debug(() -> "Servlet context bound NavigationTargetConfigurationRegistry initialized.");
	}

}
