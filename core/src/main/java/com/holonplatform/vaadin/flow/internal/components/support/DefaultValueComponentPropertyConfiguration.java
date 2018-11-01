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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.ValueComponent;

/**
 * Default {@link ValueComponentPropertyConfiguration} implementation.
 *
 * @param <T> Property type
 * @param <V> ValueComponent type
 *
 * @since 5.2.0
 */
public class DefaultValueComponentPropertyConfiguration<T, V extends ValueComponent<T>>
		implements ValueComponentPropertyConfiguration<T, V> {

	private final Property<T> property;

	private V valueComponent;

	private boolean hidden;

	private PropertyRenderer<V, T> renderer;

	public DefaultValueComponentPropertyConfiguration(Property<T> property) {
		super();
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		this.property = property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#getProperty()
	 */
	@Override
	public Property<T> getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#getValueComponent()
	 */
	@Override
	public Optional<V> getValueComponent() {
		return Optional.ofNullable(valueComponent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#setValueComponent(
	 * com.holonplatform.vaadin.flow.components.ValueComponent)
	 */
	@Override
	public void setValueComponent(V valueComponent) {
		this.valueComponent = valueComponent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#isHidden()
	 */
	@Override
	public boolean isHidden() {
		return hidden;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#setHidden(boolean)
	 */
	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#getRenderer()
	 */
	@Override
	public Optional<PropertyRenderer<V, T>> getRenderer() {
		return Optional.ofNullable(renderer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ValueComponentPropertyConfiguration#setRenderer(com.
	 * holonplatform.core.property.PropertyRenderer)
	 */
	@Override
	public void setRenderer(PropertyRenderer<V, T> renderer) {
		this.renderer = renderer;
	}

}
