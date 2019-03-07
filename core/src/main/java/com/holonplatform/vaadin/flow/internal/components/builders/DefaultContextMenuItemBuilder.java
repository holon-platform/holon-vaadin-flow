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

import java.util.function.Function;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.contextmenu.ContextMenuBase;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.MenuItemBase;
import com.vaadin.flow.component.contextmenu.SubMenuBase;

/**
 * Default {@link MenuItemBuilder}.
 *
 * @param <M> Concrete ContextMenu component type
 * @param <I> Menu item type
 * @param <S> Sub menu type
 * @param <B> Parent configurator type
 *
 * @since 5.2.0
 */
public class DefaultContextMenuItemBuilder<M extends ContextMenuBase<M, I, S>, I extends MenuItemBase<M, I, S>, S extends SubMenuBase<M, I, S>, B extends ContextMenuConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B>>
		implements MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> {

	private final B parentBuilder;
	private final MenuItem menuItem;
	private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

	public DefaultContextMenuItemBuilder(B parentBuilder, MenuItem menuItem,
			Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
		super();
		ObjectUtils.argumentNotNull(parentBuilder, "Parent builder must be not null");
		ObjectUtils.argumentNotNull(menuItem, "Menu item must be not null");
		ObjectUtils.argumentNotNull(clickEventConverter, "Click event converter must be not null");
		this.parentBuilder = parentBuilder;
		this.menuItem = menuItem;
		this.clickEventConverter = clickEventConverter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> id(String id) {
		menuItem.setId(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> enabled(boolean enabled) {
		menuItem.setEnabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> text(Localizable text) {
		menuItem.setText(LocalizationProvider.localize(text).orElse(""));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickListener(com.holonplatform.
	 * vaadin.flow.components.events.ClickEventListener)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> withClickListener(
			ClickEventListener<MenuItem, ClickEvent<MenuItem>> clickEventListener) {
		ObjectUtils.argumentNotNull(clickEventListener, "Click listener must be not null");
		menuItem.addClickListener(e -> clickEventListener.onClickEvent(clickEventConverter.apply(e)));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder#add()
	 */
	@Override
	public B add() {
		return parentBuilder;
	}

}
