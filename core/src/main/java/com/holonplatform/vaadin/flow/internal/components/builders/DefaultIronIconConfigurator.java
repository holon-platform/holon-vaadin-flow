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
import com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator.IconConfigurator;
import com.vaadin.flow.component.icon.IronIcon;

/**
 * Default {@link IconConfigurator} implementation using an {@link IronIcon}.
 * 
 * @param <C> Parent configurator type
 *
 * @since 5.2.0
 */
public class DefaultIronIconConfigurator<C extends HasIconConfigurator<C>> implements IconConfigurator<C> {

	private final C parent;

	private final IronIcon icon;

	private final DefaultHasStyleConfigurator styleConfigurator;

	/**
	 * Constructor.
	 * @param parent Parent configurator (not null)
	 * @param icon Icon instance (not null)
	 */
	public DefaultIronIconConfigurator(C parent, IronIcon icon) {
		super();
		ObjectUtils.argumentNotNull(parent, "Parent must be not null");
		ObjectUtils.argumentNotNull(icon, "Icon must be not null");
		this.parent = parent;
		this.icon = icon;
		this.styleConfigurator = new DefaultHasStyleConfigurator(icon);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator.IconConfigurator#size(java.lang.String)
	 */
	@Override
	public IconConfigurator<C> size(String size) {
		icon.setSize(size);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator.IconConfigurator#color(java.lang.String)
	 */
	@Override
	public IconConfigurator<C> color(String color) {
		icon.setColor(color);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public IconConfigurator<C> styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public IconConfigurator<C> styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public IconConfigurator<C> removeStyleName(String styleName) {
		styleConfigurator.removeStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public IconConfigurator<C> replaceStyleName(String styleName) {
		styleConfigurator.replaceStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator.IconConfigurator#add()
	 */
	@Override
	public C add() {
		return parent.icon(icon);
	}

}
