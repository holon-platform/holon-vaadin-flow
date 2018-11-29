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
package com.holonplatform.vaadin.flow.navigator;

import java.io.Serializable;
import java.util.EventListener;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.Location;

/**
 * A listener to listen to navigation target/location changes.
 *
 * @since 5.2.0
 */
@FunctionalInterface
public interface NavigationChangeListener extends EventListener, Serializable {

	/**
	 * Invoked after a navigation was performed and the navigation target/location changed.
	 * @param event The navigation change event
	 */
	void onNavigationChange(NavigationChangeEvent event);

	/**
	 * A navigation target change event.
	 * 
	 * @since 5.2.0
	 */
	public interface NavigationChangeEvent extends Serializable {

		/**
		 * Get the navigation location.
		 * @return the navigation location
		 */
		Location getLocation();

		/**
		 * Get the navigation target instance.
		 * @return the navigation target
		 */
		HasElement getNavigationTarget();

	}

}
