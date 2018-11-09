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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.ViewComponent;

/**
 * Property group configurator.
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface PropertyGroupConfigurator<C extends PropertyGroupConfigurator<C>> {

	/**
	 * Set the given property as hidden. If a property is hidden, the {@link ViewComponent} bound to the property will
	 * never be generated, but its value will be written to a {@link PropertyBox} using
	 * {@link PropertyViewGroup#getValue()}.
	 * @param <T> Property type
	 * @param property Property to set as hidden (not null)
	 * @return this
	 */
	<T> C hidden(Property<T> property);

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
	 * Add a {@link ValueChangeListener} to the group.
	 * @param listener The ValueChangeListener to add
	 * @return this
	 */
	C withValueChangeListener(ValueChangeListener<PropertyBox> listener);

}
