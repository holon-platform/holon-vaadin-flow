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
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator.BaseHasStyleConfigurator;
import com.vaadin.flow.component.HasStyle;

/**
 * Default {@link HasStyleConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasStyleConfigurator implements BaseHasStyleConfigurator {

	private final HasStyle component;

	/**
	 * Constructor.
	 * @param component Component to configure (not null)
	 */
	public DefaultHasStyleConfigurator(HasStyle component) {
		super();
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public DefaultHasStyleConfigurator styleNames(String... styleNames) {
		component.addClassNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public DefaultHasStyleConfigurator styleName(String styleName) {
		if (styleName != null) {
			if (styleName.indexOf(' ') > 0) {
				component.addClassNames(styleName.split("\\s+"));
			} else {
				component.addClassName(styleName);
			}
		}
		return this;
	}

}
