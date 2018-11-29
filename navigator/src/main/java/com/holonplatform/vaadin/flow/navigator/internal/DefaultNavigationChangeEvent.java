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

import com.holonplatform.vaadin.flow.navigator.NavigationChangeListener.NavigationChangeEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.Location;

/**
 * Default {@link NavigationChangeEvent} implementation.
 *
 * @since 5.2.0
 */
public class DefaultNavigationChangeEvent implements NavigationChangeEvent {

	private static final long serialVersionUID = 3909804813928195300L;

	private final Location location;
	private final HasElement navigationTarget;

	/**
	 * Constructor.
	 * @param location The new navigation location
	 * @param navigationTarget The new navigation target instance
	 */
	public DefaultNavigationChangeEvent(Location location, HasElement navigationTarget) {
		super();
		this.location = location;
		this.navigationTarget = navigationTarget;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationChangeListener.NavigationChangeEvent#getLocation()
	 */
	@Override
	public Location getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.NavigationChangeListener.NavigationChangeEvent#getNavigationTarget()
	 */
	@Override
	public HasElement getNavigationTarget() {
		return navigationTarget;
	}

}
