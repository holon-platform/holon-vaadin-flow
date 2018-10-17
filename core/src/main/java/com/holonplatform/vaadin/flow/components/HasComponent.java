/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components;

import com.vaadin.flow.component.Component;

/**
 * Represents and object which can be represented by a UI {@link Component}, which can be obtained using the
 * {@link #getComponent()} method.
 *
 * @since 5.0.5
 */
public interface HasComponent {

	/**
	 * Get the UI {@link Component} which represents this object.
	 * @return the UI component (not null)
	 */
	Component getComponent();

	/**
	 * Sets the component visibility value.
	 * <p>
	 * When a component is set as invisible, all the updates of the component from the server to the client are blocked
	 * until the component is set as visible again.
	 * <p>
	 * Invisible components don't receive any updates from the client-side. Unlike the server-side updates, client-side
	 * updates, if any, are discarded while the component is invisible, and are not transmitted to the server when the
	 * component is made visible.
	 * @param visible the component visibility value
	 */
	default void setVisible(boolean visible) {
		getComponent().setVisible(visible);
	}

	/**
	 * Gets the component visibility value.
	 * @return <code>true</code> if the component is visible, <code>false</code> otherwise
	 */
	default boolean isVisible() {
		return getComponent().isVisible();
	}

}
