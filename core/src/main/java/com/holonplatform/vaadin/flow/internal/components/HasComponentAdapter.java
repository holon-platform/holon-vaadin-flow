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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.Component;

/**
 * Default {@link HasComponent} adapter.
 *
 * @since 5.2.0
 */
public class HasComponentAdapter implements HasComponent {

	private static final long serialVersionUID = 6659651206546109072L;

	private final Component component;

	/**
	 * Constructor.
	 * @param component The component (not null)
	 */
	public HasComponentAdapter(Component component) {
		super();
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return component;
	}

}
