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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.contextmenu.ContextMenuBase;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.MenuItemBase;
import com.vaadin.flow.component.contextmenu.SubMenuBase;

/**
 * Abstract {@link ContextMenuConfigurator}.
 *
 * @param <M> Concrete ContextMenu component type
 * @param <I> Menu item type
 * @param <S> Sub menu type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public abstract class AbstractContextMenuConfigurator<M extends ContextMenuBase<M, I, S>, I extends MenuItemBase<M, I, S>, S extends SubMenuBase<M, I, S>, C extends ContextMenuConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C>>
		extends AbstractComponentConfigurator<M, C>
		implements ContextMenuConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> {

	private final M instance;
	private final BiFunction<M, String, MenuItem> textMenuItemProvider;
	private final BiFunction<M, Component, MenuItem> componentMenuItemProvider;
	private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

	public AbstractContextMenuConfigurator(M instance, BiFunction<M, String, MenuItem> textMenuItemProvider,
			BiFunction<M, Component, MenuItem> componentMenuItemProvider,
			Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
		super(instance);
		this.instance = instance;
		this.textMenuItemProvider = textMenuItemProvider;
		this.componentMenuItemProvider = componentMenuItemProvider;
		this.clickEventConverter = clickEventConverter;
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.empty();
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	/**
	 * Get the context menu instance.
	 * @return the context menu instance
	 */
	protected M getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> withItem(Localizable text) {
		ObjectUtils.argumentNotNull(text, "Text must be not null");
		return new DefaultContextMenuItemBuilder<>(getConfigurator(),
				textMenuItemProvider.apply(instance, LocalizationProvider.localize(text).orElse("")),
				clickEventConverter);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.vaadin.flow.component.
	 * Component)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> withItem(
			Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		return new DefaultContextMenuItemBuilder<>(getConfigurator(),
				componentMenuItemProvider.apply(instance, component), clickEventConverter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#openOnClick(boolean)
	 */
	@Override
	public C openOnClick(boolean openOnClick) {
		instance.setOpenOnClick(openOnClick);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withOpenedChangeListener(com.vaadin.
	 * flow.component.ComponentEventListener)
	 */
	@Override
	public C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent<M>> listener) {
		instance.addOpenedChangeListener(listener);
		return getConfigurator();
	}

}
