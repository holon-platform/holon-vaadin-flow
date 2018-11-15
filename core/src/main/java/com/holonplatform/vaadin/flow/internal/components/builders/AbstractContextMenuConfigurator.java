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

import java.util.function.BiFunction;
import java.util.function.Function;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.ContextMenuBase;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.contextmenu.MenuItem;

/**
 * Abstract {@link ContextMenuConfigurator}.
 *
 * @param <M> Concrete ContextMenu component type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public abstract class AbstractContextMenuConfigurator<M extends ContextMenuBase<M>, C extends ContextMenuConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, C>>
		extends AbstractComponentConfigurator<M, C>
		implements ContextMenuConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, C> {

	protected final DefaultHasStyleConfigurator styleConfigurator;

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
		this.styleConfigurator = new DefaultHasStyleConfigurator(instance);
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
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, C> withItem(Localizable text) {
		ObjectUtils.argumentNotNull(text, "Text must be not null");
		return new DefaultContextMenuItemBuilder<>(getConfigurator(),
				textMenuItemProvider.apply(instance, LocalizationContext.translate(text, true)), clickEventConverter);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.vaadin.flow.component.
	 * Component)
	 */
	@Override
	public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, C> withItem(Component component) {
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
