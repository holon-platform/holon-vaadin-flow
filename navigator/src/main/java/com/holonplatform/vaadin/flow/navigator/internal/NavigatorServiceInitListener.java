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

import java.util.ServiceLoader;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfigurationProvider;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfigurationProviderRegistry;
import com.holonplatform.vaadin.flow.navigator.internal.listeners.DefaultNavigationTargetAfterNavigationListener;
import com.holonplatform.vaadin.flow.navigator.internal.listeners.DefaultNavigationTargetBeforeEnterListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * A {@link VaadinServiceInitListener} to configure the UI event listeners used by {@link Navigator} for navigation
 * targets configuration.
 * <p>
 * The listener is automatically registered using {@link ServiceLoader}.
 * </p>
 *
 * @since 5.2.0
 */
public class NavigatorServiceInitListener implements VaadinServiceInitListener {

	private static final long serialVersionUID = 7465077536980858875L;

	private static final Logger LOGGER = VaadinLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
	 */
	@Override
	public void serviceInit(ServiceInitEvent event) {
		// navigation target configuration provider
		event.getSource().addUIInitListener(e -> {
			final NavigationTargetConfigurationProvider provider = NavigationTargetConfigurationProviderRegistry
					.getProvider(e.getUI().getClass().getClassLoader());
			e.getUI().getRouter().getRegistry().getRegisteredRoutes()
					.forEach(route -> provider.getConfiguration(route.getNavigationTarget()));
		});
		event.getSource().addSessionInitListener(e -> {
			// Session navigators registry
			if (e.getSession().getAttribute(NavigatorRegistry.class) == null) {
				e.getSession().setAttribute(NavigatorRegistry.class, new DefaultNavigatorRegistry());
			}
		});
		// UI navigation listeners
		event.getSource().addUIInitListener(e -> {
			e.getUI().addBeforeEnterListener(new DefaultNavigationTargetBeforeEnterListener());
			e.getUI().addAfterNavigationListener(new DefaultNavigationTargetAfterNavigationListener());
			LOGGER.debug(() -> "Navigator event listeners setted up");
		});
	}

}