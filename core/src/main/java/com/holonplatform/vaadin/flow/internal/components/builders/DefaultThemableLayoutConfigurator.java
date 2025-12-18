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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.dom.DomEventListener;

/**
 * Default {@link ThemableLayoutConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultThemableLayoutConfigurator
		implements ThemableLayoutConfigurator<DefaultThemableLayoutConfigurator> {

	private final ThemableLayout component;

	public DefaultThemableLayoutConfigurator(ThemableLayout component) {
		super();
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public DefaultThemableLayoutConfigurator withThemeName(String themeName) {
		component.getThemeList().add(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public DefaultThemableLayoutConfigurator withEventListener(String eventType, DomEventListener listener) {
		component.getElement().addEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public DefaultThemableLayoutConfigurator withEventListener(String eventType, DomEventListener listener,
			String filter) {
		component.getElement().addEventListener(eventType, listener).setFilter(filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#margin(boolean)
	 */
	@Override
	public DefaultThemableLayoutConfigurator margin(boolean margin) {
		component.setMargin(margin);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#padding(boolean)
	 */
	@Override
	public DefaultThemableLayoutConfigurator padding(boolean padding) {
		component.setPadding(padding);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#spacing(boolean)
	 */
	@Override
	public DefaultThemableLayoutConfigurator spacing(boolean spacing) {
		component.setSpacing(spacing);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#boxSizing(com.vaadin.flow.component.
	 * orderedlayout.BoxSizing)
	 */
	@Override
	public DefaultThemableLayoutConfigurator boxSizing(BoxSizing boxSizing) {
		component.setBoxSizing(boxSizing);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#wrap(boolean)
	 */
	@Override
	public DefaultThemableLayoutConfigurator wrap(boolean wrap) {
		component.setWrap(wrap);
		return this;
	}

}
