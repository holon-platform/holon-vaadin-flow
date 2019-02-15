/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal;

import javax.annotation.Priority;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.ViewComponent;

/**
 * Default {@link PropertyRenderer} to create {@link ViewComponent} type {@link Property} representations.
 * 
 * @param <T> Property type
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE)
@SuppressWarnings("rawtypes")
public class DefaultViewComponentPropertyRenderer<T> implements PropertyRenderer<ViewComponent, T> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
	 */
	@Override
	public Class<? extends ViewComponent> getRenderType() {
		return ViewComponent.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
	 */
	@Override
	public ViewComponent render(final Property<? extends T> property) {
		return ViewComponent.create(property);
	}

}
