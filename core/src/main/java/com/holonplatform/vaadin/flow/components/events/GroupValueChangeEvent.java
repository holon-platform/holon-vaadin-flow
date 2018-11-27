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
package com.holonplatform.vaadin.flow.components.events;

import java.util.Optional;

import com.holonplatform.vaadin.flow.components.BoundComponentGroup;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;

/**
 * A {@link ValueChangeEvent} originated from a components group.
 * 
 * @param <V> Value type
 * @param <P> Property type
 * @param <C> Group element type
 * @param <G> Component group type
 *
 * @since 5.2.0
 */
public interface GroupValueChangeEvent<V, P, C extends HasComponent, G extends BoundComponentGroup<P, C>>
		extends ValueChangeEvent<V> {

	/**
	 * Get the input group from which this value change event was originated.
	 * @return the input group
	 */
	G getInputGroup();

	/**
	 * Get the property which identifies the value change event source within the input group, if available.
	 * <p>
	 * If the value change event is not bound to any property, it represents a group value change event.
	 * </p>
	 * @return Optional property which identifies the value change event source within the input group, or empty if
	 *         represents a group value change event
	 */
	Optional<P> getProperty();

	/**
	 * Get the group component source of the value change event, if available.
	 * <p>
	 * If the value change event is not bound to any group component, it represents a group value change event.
	 * </p>
	 * @return Optional group component source of the value change event, or empty if represents a group value change
	 *         event
	 */
	Optional<C> getComponent();

}
