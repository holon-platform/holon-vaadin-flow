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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.PropertyBinding;
import com.holonplatform.vaadin.flow.components.ViewComponent;

/**
 * Default {@link ViewComponentPropertyRegistry} implementation.
 *
 * @since 5.2.0
 */
public class DefaultViewComponentPropertyRegistry implements ViewComponentPropertyRegistry {

	private final Map<Property<?>, ViewComponent<?>> components = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyRegistry#clear()
	 */
	@Override
	public void clear() {
		components.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyRegistry#set(com.holonplatform.
	 * core.property.Property, com.holonplatform.vaadin.flow.components.ViewComponent)
	 */
	@Override
	public <T> void set(Property<T> property, ViewComponent<T> component) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		components.put(property, component);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyRegistry#get(com.holonplatform.
	 * core.property.Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<ViewComponent<T>> get(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		final ViewComponent<T> component = (ViewComponent<T>) components.get(property);
		return Optional.ofNullable(component);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyRegistry#stream()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> Stream<PropertyBinding<T, ViewComponent<T>>> stream() {
		return components.entrySet().stream().filter(e -> e.getValue() != null)
				.map(e -> (PropertyBinding) PropertyBinding.create(e.getKey(), e.getValue()))
				.map(b -> (PropertyBinding<T, ViewComponent<T>>) b);
	}

}
