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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.ContextMenuBase;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.contextmenu.MenuItem;

/**
 * {@link ContextMenu} component configurator.
 * 
 * @param <E> Menu item click event type
 * @param <M> Concrete ContextMenu component type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface ContextMenuConfigurator<E extends ClickEvent<MenuItem>, M extends ContextMenuBase<M>, C extends ContextMenuConfigurator<E, M, C>>
		extends ComponentConfigurator<C>, HasStyleConfigurator<C> {

	/**
	 * Create a new menu item with the given text content. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and available as a {@link Context} resource.
	 * <p>
	 * The {@link MenuItemBuilder#add()} method can be used to add the item to the context menu.
	 * </p>
	 * @param text Localizable menu item text content
	 * @return A {@link MenuItemConfigurator} to configure and add the menu item
	 * @see LocalizationContext#getCurrent()
	 */
	MenuItemBuilder<E, M, C> withItem(Localizable text);

	/**
	 * Create a new menu item with the given text content.
	 * <p>
	 * The {@link MenuItemBuilder#add()} method can be used to add the item to the context menu.
	 * </p>
	 * @param text Menu item text content
	 * @return A {@link MenuItemConfigurator} to configure and add the menu item
	 */
	default MenuItemBuilder<E, M, C> withItem(String text) {
		return withItem(Localizable.builder().message(text).build());
	}

	/**
	 * Create a new menu item using given <code>messageCode</code> for text content localization. In order for the
	 * localization to work, a {@link LocalizationContext} must be valid (localized) and available as a {@link Context}
	 * resource.
	 * <p>
	 * The {@link MenuItemBuilder#add()} method can be used to add the item to the context menu.
	 * </p>
	 * @param defaultText Default menu item text content if no translation is available for given
	 *        <code>messageCode</code>.
	 * @param messageCode Menu item text content translation message key
	 * @param arguments Optional translation arguments
	 * @return A {@link MenuItemConfigurator} to configure and add the menu item
	 * @see LocalizationContext#getCurrent()
	 */
	default MenuItemBuilder<E, M, C> withItem(String defaultText, String messageCode, Object... arguments) {
		return withItem(Localizable.builder().message((defaultText == null) ? "" : defaultText).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Create a new menu item with the given component inside.
	 * <p>
	 * The {@link MenuItemBuilder#add()} method can be used to add the item to the context menu.
	 * </p>
	 * @param component The menu item component (not null)
	 * @return A {@link MenuItemConfigurator} to configure and add the menu item
	 */
	MenuItemBuilder<E, M, C> withItem(Component component);

	/**
	 * Add a new menu item using given text content and a {@link ClickEventListener} for menu item clicks.
	 * @param text Menu item text content
	 * @param clickEventListener The listener to use to listen to menu item clicks (not null)
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	default C withItem(Localizable text, ClickEventListener<MenuItem, E> clickEventListener) {
		return withItem(text).withClickListener(clickEventListener).add();
	}

	/**
	 * Add a new menu item using given text content using given <code>messageCode</code> for text localization and a
	 * {@link ClickEventListener} for menu item clicks.
	 * @param defaultText Default menu item text content if no translation is available for given
	 *        <code>messageCode</code>.
	 * @param messageCode Menu item text content translation message key
	 * @param clickEventListener The listener to use to listen to menu item clicks (not null)
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	default C withItem(String defaultText, String messageCode, ClickEventListener<MenuItem, E> clickEventListener) {
		return withItem(defaultText, messageCode).withClickListener(clickEventListener).add();
	}

	/**
	 * Add a new menu item using given text content and a {@link ClickEventListener} for menu item clicks.
	 * @param text Menu item text content
	 * @param clickEventListener The listener to use to listen to menu item clicks (not null)
	 * @return this
	 */
	default C withItem(String text, ClickEventListener<MenuItem, E> clickEventListener) {
		return withItem(text).withClickListener(clickEventListener).add();
	}

	/**
	 * Add a new menu item with the given component inside and a {@link ClickEventListener} for menu item clicks.
	 * @param component The menu item component (not null)
	 * @param clickEventListener The listener to use to listen to menu item clicks (not null)
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	default C withItem(Component component, ClickEventListener<MenuItem, E> clickEventListener) {
		return withItem(component).withClickListener(clickEventListener).add();
	}

	/**
	 * Determines the way for opening the context menu.
	 * <p>
	 * By default, the context menu can be opened with a right click or a long touch on the target component.
	 * </p>
	 * @param openOnClick if <code>true</code>, the context menu can be opened with left click only. Otherwise the
	 *        context menu follows the default behavior.
	 * @return this
	 */
	C openOnClick(boolean openOnClick);

	/**
	 * Adds a listener to be notified when the context menu is opened or closed.
	 * @param listener the listener to add
	 * @return this
	 */
	C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent<M>> listener);

	/**
	 * Context menu item configurator.
	 * 
	 * @param <E> Click event type
	 * @param <M> Concrete ContextMenu component type
	 * @param <B> Parent configurator type
	 *
	 * @since 5.2.0
	 */
	public interface MenuItemBuilder<E extends ClickEvent<MenuItem>, M extends ContextMenuBase<M>, B extends ContextMenuConfigurator<E, M, B>>
			extends HasEnabledConfigurator<MenuItemBuilder<E, M, B>>, HasTextConfigurator<MenuItemBuilder<E, M, B>>,
			ClickNotifierConfigurator<MenuItem, E, MenuItemBuilder<E, M, B>> {

		/**
		 * Sets the id of the root element of the menu item.
		 * @param id the id to set
		 * @return this
		 */
		MenuItemBuilder<E, M, B> id(String id);

		/**
		 * Add the menu item to the context menu.
		 * @return The parent context menu builder
		 */
		B add();

	}

}
