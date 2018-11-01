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
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyBinding;

/**
 * Default {@link InputPropertyRegistry} implementation.
 *
 * @since 5.2.0
 */
public class DefaultInputPropertyRegistry implements InputPropertyRegistry {

	private final Map<Property<?>, Input<?>> components = new HashMap<>();

	@Override
	public void clear() {
		components.clear();
	}

	@Override
	public <T> void set(Property<T> property, Input<T> component) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		components.put(property, component);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<Input<T>> get(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		final Input<T> component = (Input<T>) components.get(property);
		return Optional.ofNullable(component);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> Stream<PropertyBinding<T, Input<T>>> stream() {
		return components.entrySet().stream().filter(e -> e.getValue() != null)
				.map(e -> (PropertyBinding) PropertyBinding.create(e.getKey(), e.getValue()))
				.map(b -> (PropertyBinding<T, Input<T>>) b);
	}

}
