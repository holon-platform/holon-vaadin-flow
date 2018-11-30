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
import java.util.function.Function;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

/**
 * {@link Navigator} instances registry.
 *
 * @since 5.2.0
 */
public interface NavigatorRegistry extends Serializable {

	/**
	 * Bind the given {@link Navigator} instance to the given <code>ui</code>.
	 * <p>
	 * If another {@link Navigator} instance was bound to given UI, it will be replaced by the new navigator instance.
	 * </p>
	 * @param ui The UI (not null)
	 * @param navigator The navigator (not null)
	 */
	void setNavigator(UI ui, Navigator navigator);

	/**
	 * Get the {@link Navigator} bound to given UI, if available.
	 * @param ui The UI for which to obtain the navigator (not null)
	 * @return Optional {@link Navigator} bound to given UI
	 */
	Optional<Navigator> getNavigator(UI ui);

	/**
	 * Get the {@link Navigator} bound to given UI, creating it if not present.
	 * @param ui The UI for which to obtain the navigator (not null)
	 * @param creator The function to use to create a new {@link Navigator} and bound it to the UI (not null)
	 * @return The {@link Navigator} bound to given UI
	 */
	Navigator getOrCreateNavigator(UI ui, Function<UI, Navigator> creator);

	/**
	 * Get the {@link NavigatorRegistry} associated with current session, if available.
	 * @return Optional session registry
	 */
	static Optional<NavigatorRegistry> getSessionRegistry() {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			return Optional.ofNullable(session.getAttribute(NavigatorRegistry.class));
		}
		return Optional.empty();
	}

}
