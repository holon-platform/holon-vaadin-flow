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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Configurator for components which supports an icon.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasIconConfigurator<C extends HasIconConfigurator<C>> {

	/**
	 * Set the component icon. If <code>null</code>, the icon is removed.
	 * @param icon The icon {@link Component} to set (may be null)
	 * @return this
	 */
	C icon(Component icon);

	/**
	 * Set the component icon using a {@link VaadinIcon}. If <code>null</code>, the icon is removed.
	 * @param icon The icon to set (may be null)
	 * @return this
	 */
	default C icon(VaadinIcon icon) {
		return icon((icon != null) ? icon.create() : null);
	}

	/**
	 * Set the component icon using a {@link IronIcon}. If <code>icon</code> name is <code>null</code>,
	 * the icon is removed.
	 * @param collection the icon collection
	 * @param icon the icon name
	 * @return this
	 */
	default C icon(String collection, String icon) {
		return icon((icon != null) ? new Icon(collection, icon) : null);
	}

	/**
	 * Obtain an {@link IconConfigurator} for given icon to configure it and add it to che component
	 * using {@link IconConfigurator#add()}.
	 * @param icon The icon to configure and add to the component (not null)
	 * @return The icon configurator
	 */
	IconConfigurator<C> iconConfigurator(Icon icon);

	/**
	 * Obtain an {@link IconConfigurator} for given {@link VaadinIcon} representation to configure the
	 * icon and add it to che component using {@link IconConfigurator#add()}.
	 * @param icon The icon to configure and add to the component (not null)
	 * @return The icon configurator
	 */
	default IconConfigurator<C> iconConfigurator(VaadinIcon icon) {
		ObjectUtils.argumentNotNull(icon, "Icon must be not null");
		return iconConfigurator(icon.create());
	}

	/**
	 * Obtain an {@link IconConfigurator} for given {@link IronIcon} representation to configure the
	 * icon and add it to che component using {@link IconConfigurator#add()}.
	 * @param collection the icon collection
	 * @param icon the icon name (not null)
	 * @return The icon configurator
	 */
	default IconConfigurator<C> iconConfigurator(String collection, String icon) {
		ObjectUtils.argumentNotNull(icon, "Icon must be not null");
		return iconConfigurator(new Icon(collection, icon));
	}

	/**
	 * Configurator to setup a component icon.
	 * 
	 * @param <C> Parent configurator
	 */
	public interface IconConfigurator<C extends HasIconConfigurator<C>>
			extends HasStyleConfigurator<IconConfigurator<C>> {

		/**
		 * Sets the width and the height of the icon.
		 * <p>
		 * The size should be in a format understood by the browser, e.g. "100px" or "2.5em".
		 * </p>
		 * @param size the size to set, may be <code>null</code> to clear the value
		 * @return this
		 */
		IconConfigurator<C> size(String size);

		/**
		 * Sets the fill color of the icon.
		 * <p>
		 * The color should be in a format understood by the browser, e.g. "orange", "#FF9E2C" or "rgb(255,
		 * 158, 44)".
		 * </p>
		 * @param color the fill color to set, may be <code>null</code> to clear the value
		 * @return this
		 */
		IconConfigurator<C> color(String color);

		/**
		 * Add the configured icon to the component.
		 * @return Parent component configurator
		 */
		C add();

	}

}
