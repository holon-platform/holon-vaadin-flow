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
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.vaadin.flow.component.HasSize;

/**
 * Default {@link HasSizeConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasSizeConfigurator implements HasSizeConfigurator<DefaultHasSizeConfigurator> {

	private final HasSize component;

	/**
	 * Constructor.
	 * @param component Component to configure (not null)
	 */
	public DefaultHasSizeConfigurator(HasSize component) {
		super();
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public DefaultHasSizeConfigurator width(String width) {
		component.setWidth(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public DefaultHasSizeConfigurator height(String height) {
		component.setHeight(height);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
	 */
	@Override
	public DefaultHasSizeConfigurator minWidth(String minWidth) {
		component.setMinWidth(minWidth);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
	 */
	@Override
	public DefaultHasSizeConfigurator maxWidth(String maxWidth) {
		component.setMaxWidth(maxWidth);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
	 */
	@Override
	public DefaultHasSizeConfigurator minHeight(String minHeight) {
		component.setMinHeight(minHeight);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
	 */
	@Override
	public DefaultHasSizeConfigurator maxHeight(String maxHeight) {
		component.setMaxHeight(maxHeight);
		return this;
	}

}
