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

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.vaadin.flow.component.UI;

/**
 * Default {@link NavigatorRegistry} instance.
 *
 * @since 5.2.0
 */
public enum DefaultNavigatorRegistry implements NavigatorRegistry {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * UI navigator bindings.
	 */
	private static final Map<UI, Navigator> viewNavigators = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.ViewNavigatorRegistry#bind(com.vaadin.flow.component.UI,
	 * com.holonplatform.vaadin.flow.navigator.ViewNavigator)
	 */
	@Override
	public void bind(UI ui, Navigator navigator) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		ObjectUtils.argumentNotNull(navigator, "ViewNavigator must be not null");
		viewNavigators.put(ui, navigator);
		LOGGER.debug(
				() -> "ViewNavigator [" + navigator + "] bound to UI [" + ui + "] - UI id: [" + ui.getUIId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.ViewNavigatorRegistry#unbind(com.vaadin.flow.component.UI)
	 */
	@Override
	public Optional<Navigator> unbind(UI ui) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		final Navigator navigator = viewNavigators.remove(ui);
		if (navigator != null) {
			LOGGER.debug(() -> "ViewNavigator [" + navigator + "] unbound from UI [" + ui + "] - UI id: ["
					+ ui.getUIId() + "]");
		}
		return Optional.ofNullable(navigator);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.ViewNavigatorRegistry#getNavigator(com.vaadin.flow.component.UI)
	 */
	@Override
	public Optional<Navigator> getNavigator(UI ui) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		return Optional.ofNullable(viewNavigators.get(ui));
	}

}
