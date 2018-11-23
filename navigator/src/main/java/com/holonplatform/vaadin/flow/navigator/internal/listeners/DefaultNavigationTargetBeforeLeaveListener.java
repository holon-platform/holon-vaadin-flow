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

import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfigurationProvider;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;

/**
 * TODO
 */
public class DefaultNavigationTargetBeforeLeaveListener extends AbstractNavigationTargetListener
		implements BeforeLeaveListener {

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.router.internal.BeforeLeaveHandler#beforeLeave(com.vaadin.flow.router.BeforeLeaveEvent)
	 */
	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		// TODO
		final Class<?> navigationTarget = event.getNavigationTarget();
		if (navigationTarget != null) {
			final NavigationTargetConfiguration configuration = NavigationTargetConfigurationProvider
					.get(navigationTarget.getClassLoader()).getConfiguration(navigationTarget);
			// TODO fire @OnLeave methods
		}
	}

}
