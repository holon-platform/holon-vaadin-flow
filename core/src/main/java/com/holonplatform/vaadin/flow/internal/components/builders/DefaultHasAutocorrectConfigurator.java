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
import com.holonplatform.vaadin.flow.components.builders.HasAutocorrectConfigurator;
import com.vaadin.flow.component.textfield.HasAutocorrect;

/**
 * Default {@link HasAutocorrectConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasAutocorrectConfigurator
		implements HasAutocorrectConfigurator<DefaultHasAutocorrectConfigurator> {

	private final HasAutocorrect component;

	/**
	 * Constructor.
	 * @param component Component to configure (not null)
	 */
	public DefaultHasAutocorrectConfigurator(HasAutocorrect component) {
		super();
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutocorrectConfigurator#autocorrect(boolean)
	 */
	@Override
	public DefaultHasAutocorrectConfigurator autocorrect(boolean autocorrect) {
		component.setAutocorrect(autocorrect);
		return this;
	}

}
