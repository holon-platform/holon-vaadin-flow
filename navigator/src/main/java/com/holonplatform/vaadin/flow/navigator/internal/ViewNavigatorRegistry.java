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

import java.io.Serializable;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.vaadin.flow.navigator.ViewNavigator;
import com.vaadin.flow.component.UI;

/**
 * {@link ViewNavigator} instances registry.
 *
 * @since 5.2.0
 */
public interface ViewNavigatorRegistry extends Serializable {

	/**
	 * Default {@link Context} resource key.
	 */
	public static final String CONTEXT_KEY = ViewNavigatorRegistry.class.getName();

	/**
	 * Bind the given {@link ViewNavigator} instance to the given <code>ui</code>.
	 * <p>
	 * If another {@link ViewNavigator} instance was bound to given UI, it will be replaced by the new navigator
	 * instance.
	 * </p>
	 * @param ui The UI (not null)
	 * @param navigator The navigator (not null)
	 */
	void bind(UI ui, ViewNavigator navigator);

	/**
	 * Unbind the {@link ViewNavigator} from <code>ui</code>, if present.
	 * @param ui The UI (not null)
	 * @return The {@link ViewNavigator} which was bound to given UI, if any.
	 */
	Optional<ViewNavigator> unbind(UI ui);

	/**
	 * Get the {@link ViewNavigator} bound to given UI.
	 * @param ui The UI for which to obtain the navigator (not null)
	 * @return The {@link ViewNavigator} bound to given UI, if available.
	 */
	Optional<ViewNavigator> getNavigator(UI ui);

	/**
	 * Get the current {@link ViewNavigatorRegistry} made available as {@link Context} resource, using default
	 * {@link ClassLoader}, or the default {@link ViewNavigatorRegistry} if not available.
	 * @return The current {@link ViewNavigatorRegistry}, either from the current context or using the default instance
	 */
	static ViewNavigatorRegistry getCurrent() {
		return Context.get().resource(CONTEXT_KEY, ViewNavigatorRegistry.class).orElseGet(() -> getDefault());
	}

	/**
	 * Get the default navigator registry.
	 * @return the default navigator registry
	 */
	static ViewNavigatorRegistry getDefault() {
		return DefaultViewNavigatorRegistry.INSTANCE;
	}

}
