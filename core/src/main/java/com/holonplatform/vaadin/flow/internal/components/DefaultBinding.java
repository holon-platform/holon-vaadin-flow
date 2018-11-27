/*
 * Copyright 2000-2017 Holon TDCN.
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
import com.holonplatform.vaadin.flow.components.BoundComponentGroup.Binding;
import com.holonplatform.vaadin.flow.components.HasComponent;

/**
 * Default {@link Binding} implementation.
 * 
 * @param <P> The property type
 * @param <C> The element type
 *
 * @since 5.2.0
 */
public class DefaultBinding<P, C extends HasComponent> implements Binding<P, C> {

	private static final long serialVersionUID = -6134557608912391745L;

	private final P property;
	private final C element;

	/**
	 * Constructor
	 * @param property Property (not null)
	 * @param element Bound element (not null)
	 */
	public DefaultBinding(P property, C element) {
		super();
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(element, "Element must be not null");
		this.property = property;
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup.Binding#getProperty()
	 */
	@Override
	public P getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup.Binding#getElement()
	 */
	@Override
	public C getElement() {
		return element;
	}

}
