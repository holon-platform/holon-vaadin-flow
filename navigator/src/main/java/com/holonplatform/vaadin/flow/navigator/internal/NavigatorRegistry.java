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
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.vaadin.flow.component.UI;

/**
 * {@link Navigator} instances registry.
 *
 * @since 5.2.0
 */
public interface NavigatorRegistry extends Serializable {

	/**
	 * Default {@link Context} resource key.
	 */
	public static final String CONTEXT_KEY = NavigatorRegistry.class.getName();

	/**
	 * Bind the given {@link Navigator} instance to the given <code>ui</code>.
	 * <p>
	 * If another {@link Navigator} instance was bound to given UI, it will be replaced by the new navigator
	 * instance.
	 * </p>
	 * @param ui The UI (not null)
	 * @param navigator The navigator (not null)
	 */
	void bind(UI ui, Navigator navigator);

	/**
	 * Unbind the {@link Navigator} from <code>ui</code>, if present.
	 * @param ui The UI (not null)
	 * @return The {@link Navigator} which was bound to given UI, if any.
	 */
	Optional<Navigator> unbind(UI ui);

	/**
	 * Get the {@link Navigator} bound to given UI.
	 * @param ui The UI for which to obtain the navigator (not null)
	 * @return The {@link Navigator} bound to given UI, if available.
	 */
	Optional<Navigator> getNavigator(UI ui);

	/**
	 * Get the current {@link NavigatorRegistry} made available as {@link Context} resource, using default
	 * {@link ClassLoader}, or the default {@link NavigatorRegistry} if not available.
	 * @return The current {@link NavigatorRegistry}, either from the current context or using the default instance
	 */
	static NavigatorRegistry getCurrent() {
		return Context.get().resource(CONTEXT_KEY, NavigatorRegistry.class).orElseGet(() -> getDefault());
	}

	/**
	 * Get the default navigator registry.
	 * @return the default navigator registry
	 */
	static NavigatorRegistry getDefault() {
		return DefaultNavigatorRegistry.INSTANCE;
	}

}
