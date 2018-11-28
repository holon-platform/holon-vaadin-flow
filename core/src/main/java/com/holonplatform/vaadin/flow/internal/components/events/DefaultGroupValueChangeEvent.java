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
package com.holonplatform.vaadin.flow.internal.components.events;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.BoundComponentGroup;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;

/**
 * Default {@link GroupValueChangeEvent} implementation.
 *
 * @param <V> Value type
 * @param <P> Property type
 * @param <C> Group element type
 * @param <G> Component group type
 *
 * @since 5.2.0
 */
public class DefaultGroupValueChangeEvent<V, P, C extends HasComponent, G extends BoundComponentGroup<P, C>>
		extends DefaultValueChangeEvent<V> implements GroupValueChangeEvent<V, P, C, G> {

	private static final long serialVersionUID = 894250818129852050L;

	private final G inputGroup;
	private final P property;
	private final C component;

	/**
	 * Constructor without property and group element.
	 * @param inputGroup The input group (not null)
	 * @param source The source (not null)
	 * @param oldValue Old value
	 * @param value New value
	 * @param userOriginated Whether the event was originated from client
	 */
	public DefaultGroupValueChangeEvent(G inputGroup, ValueHolder<V, ?> source, V oldValue, V value,
			boolean userOriginated) {
		this(inputGroup, source, oldValue, value, userOriginated, null, null);
	}

	/**
	 * Constructor with property and group element.
	 * @param inputGroup The input group (not null)
	 * @param source The source (not null)
	 * @param oldValue Old value
	 * @param value New value
	 * @param userOriginated Whether the event was originated from client
	 * @param property Optional property
	 * @param component Optional group element
	 */
	public DefaultGroupValueChangeEvent(G inputGroup, ValueHolder<V, ?> source, V oldValue, V value,
			boolean userOriginated, P property, C component) {
		super(source, oldValue, value, userOriginated);
		ObjectUtils.argumentNotNull(inputGroup, "Input group must be not null");
		this.inputGroup = inputGroup;
		this.property = property;
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.InputGroupValueChangeEvent#getInputGroup()
	 */
	@Override
	public G getInputGroup() {
		return inputGroup;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.InputGroupValueChangeEvent#getProperty()
	 */
	@Override
	public Optional<P> getProperty() {
		return Optional.ofNullable(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent#getComponent()
	 */
	@Override
	public Optional<C> getComponent() {
		return Optional.ofNullable(component);
	}

}
