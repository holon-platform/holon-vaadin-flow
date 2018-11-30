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
import java.util.function.Function;

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
public class DefaultNavigatorRegistry implements NavigatorRegistry {

	private static final long serialVersionUID = -4967741563966590261L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = VaadinLogger.create();

	/**
	 * UI navigator bindings.
	 */
	private final Map<UI, Navigator> navigators = new WeakHashMap<>(8);

	/**
	 * Get the number of the registered navigators.
	 * @return the number of the registered navigators
	 */
	public int getNavigatorCount() {
		return navigators.size();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigatorRegistry#setNavigator(com.vaadin.flow.component.UI,
	 * com.holonplatform.vaadin.flow.navigator.Navigator)
	 */
	@Override
	public void setNavigator(UI ui, Navigator navigator) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		ObjectUtils.argumentNotNull(navigator, "ViewNavigator must be not null");
		navigators.put(ui, navigator);
		LOGGER.debug(() -> "Navigator [" + navigator + "] bound to UI [" + ui + "] - UI id: [" + ui.getUIId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigatorRegistry#getNavigator(com.vaadin.flow.component.UI)
	 */
	@Override
	public Optional<Navigator> getNavigator(UI ui) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		return Optional.ofNullable(navigators.get(ui));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigatorRegistry#getOrCreateNavigator(com.vaadin.flow.component
	 * .UI, java.util.function.Function)
	 */
	@Override
	public Navigator getOrCreateNavigator(UI ui, Function<UI, Navigator> creator) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		ObjectUtils.argumentNotNull(creator, "Creator function must be not null");
		return navigators.computeIfAbsent(ui, u -> creator.apply(u));
	}

}
