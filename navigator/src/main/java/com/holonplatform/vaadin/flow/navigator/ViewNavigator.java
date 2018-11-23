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
import java.util.Optional;

import com.holonplatform.vaadin.flow.navigator.internal.DefaultViewNavigator;
import com.holonplatform.vaadin.flow.navigator.internal.ViewNavigatorRegistry;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Router;

/**
 * TODO
 */
public interface ViewNavigator extends Serializable {

	/**
	 * Get the {@link ViewNavigator} bound to the current UI, if available.
	 * @return Optional {@link ViewNavigator} bound to the current UI
	 */
	static Optional<ViewNavigator> get() {
		return Optional.ofNullable(UI.getCurrent()).flatMap(ui -> get(ui));
	}

	/**
	 * Get the {@link ViewNavigator} bound to the current UI, throwing an {@link IllegalStateException} if the current
	 * UI is not available or no {@link ViewNavigator} is bound to the current UI.
	 * @return The {@link ViewNavigator} bound to the current UI
	 * @throws IllegalStateException If the current UI is not available or no ViewNavigator is bound to the current UI
	 */
	static ViewNavigator require() {
		return require(Optional.ofNullable(UI.getCurrent())
				.orElseThrow(() -> new IllegalStateException("The current UI is not available")));
	}

	/**
	 * Get the {@link ViewNavigator} bound to given UI, if available.
	 * @param ui The UI (not null)
	 * @return Optional {@link ViewNavigator} bound to given UI
	 */
	static Optional<ViewNavigator> get(UI ui) {
		return ViewNavigatorRegistry.getCurrent().getNavigator(ui);
	}

	/**
	 * Get the {@link ViewNavigator} bound to given UI, throwing an {@link IllegalStateException} if not available.
	 * @param ui The UI (not null)
	 * @return The {@link ViewNavigator} bound to given UI
	 * @throws IllegalStateException If a ViewNavigator is not available for given UI
	 */
	static ViewNavigator require(UI ui) {
		return get(ui).orElseThrow(() -> new IllegalStateException("No ViewNavigator available for UI [" + ui + "]"));
	}

	// ------- builders

	/**
	 * Create a new {@link ViewNavigator} using given router.
	 * @param router The {@link Router} to use (not null)
	 * @return A new {@link ViewNavigator} instance
	 */
	static ViewNavigator create(Router router) {
		return new DefaultViewNavigator(router);
	}

}
