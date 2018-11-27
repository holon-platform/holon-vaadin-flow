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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.flow.components.BoundComponentGroup;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;

/**
 * Component group configurator.
 *
 * @param <P> Property type
 * @param <T> Group item type
 * @param <E> Group element type
 * @param <G> Group type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface ComponentGroupConfigurator<P, T, E extends HasComponent, G extends BoundComponentGroup<P, E>, C extends ComponentGroupConfigurator<P, T, E, G, C>> {

	/**
	 * Set to use the provided {@link PropertyRendererRegistry} to render the group components.
	 * <p>
	 * By default, the {@link PropertyRendererRegistry#get()} method is used to obtain the
	 * {@link PropertyRendererRegistry} to use.
	 * </p>
	 * @param propertyRendererRegistry The {@link PropertyRendererRegistry} to use to render the group components
	 * @return this
	 */
	C usePropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry);

	/**
	 * Add a group value change listener.
	 * @param listener The {@link ValueChangeListener} to add (not null)
	 * @return this
	 */
	C withValueChangeListener(ValueChangeListener<T, GroupValueChangeEvent<T, P, E, G>> listener);

}
