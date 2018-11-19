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
package com.holonplatform.vaadin.flow.navigator.internal.listeners;

import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfigurationProvider;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

/**
 * TODO
 */
public class DefaultNavigationTargetAfterNavigationListener extends AbstractNavigationTargetListener
		implements AfterNavigationListener {

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.router.internal.AfterNavigationHandler#afterNavigation(com.vaadin.flow.router.
	 * AfterNavigationEvent)
	 */
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		if (event.getActiveChain().size() > 0) {
			// current view
			final HasElement view = event.getActiveChain().get(0);
			if (view != null) {
				LOGGER.debug(() -> "Process navigation target [" + view.getClass().getName() + "] after navigation");
				// configuration
				final NavigationTargetConfiguration configuration = NavigationTargetConfigurationProvider
						.get(view.getClass().getClassLoader()).getConfiguration(view.getClass());
				// set path parameters
				configuration.getPathParameters().forEach(parameter -> {
					LOGGER.debug(() -> "Process path parameter [" + parameter.getName() + "] for navigation target ["
							+ view.getClass().getName() + "]");
					setPathParameterValue(parameter, event.getLocation());
				});
				// set query parameters
				configuration.getQueryParameters().forEach(parameter -> {
					LOGGER.debug(() -> "Process query parameter [" + parameter.getName() + "] for navigation target ["
							+ view.getClass().getName() + "]");
					setQueryParameterValue(parameter, event.getLocation());
				});
				// fire OnShow methods
				configuration.getOnShowMethods().forEach(method -> {
					LOGGER.debug(() -> "Invoke OnShow method [" + method.getName() + "] for navigation target ["
							+ view.getClass().getName() + "]");
					invokeMethod(view, method, event);
				});
			}
		}
	}

	private void setPathParameterValue(NavigationParameterDefinition definition, Location location) {
		// TODO
	}

	private void setQueryParameterValue(NavigationParameterDefinition definition, Location location) {
		// TODO
		final QueryParameters queryParameters = location.getQueryParameters();
	}

}
