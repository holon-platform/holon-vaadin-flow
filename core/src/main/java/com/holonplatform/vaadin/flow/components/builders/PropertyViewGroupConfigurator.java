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

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.ViewComponent.ViewComponentPropertyRenderer;

/**
 * {@link PropertyViewGroup} configurator.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface PropertyViewGroupConfigurator<C extends PropertyViewGroupConfigurator<C>>
		extends ComponentGroupConfigurator<Property<?>, PropertyBox, ViewComponent<?>, PropertyViewGroup, C> {

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
	 * Set the {@link PropertyRenderer} to use to render the {@link ViewComponent} bound to given <code>property</code>.
	 * @param <T> Property type
	 * @param property The property to render (not null)
	 * @param renderer The renderer to use to render the property {@link ViewComponent} (not null)
	 * @return this
	 */
	<T> C bind(Property<T> property, PropertyRenderer<ViewComponent<T>, T> renderer);

	/**
	 * Set the function to use to render the {@link ViewComponent} bound to given <code>property</code>.
	 * @param <T> Property type
	 * @param property The property to render (not null)
	 * @param function The function to use to render the property {@link ViewComponent} (not null)
	 * @return this
	 */
	default <T> C bind(Property<T> property, Function<Property<? extends T>, ViewComponent<T>> function) {
		return bind(property, ViewComponentPropertyRenderer.create(function));
	}

	/**
	 * Bind the given <code>property</code> to given <code>viewComponent</code> instance.
	 * @param <T> Property type
	 * @param property The property to render (not null)
	 * @param viewComponent The {@link ViewComponent} to use to render the property (not null)
	 * @return this
	 */
	default <T> C bind(Property<T> property, ViewComponent<T> viewComponent) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(viewComponent, "ViewComponent must be not null");
		return bind(property, ViewComponentPropertyRenderer.create(p -> viewComponent));
	}

	/**
	 * Add a {@link BiConsumer} to allow further {@link ViewComponent} configuration after generation and before the
	 * {@link ViewComponent} is actually bound to a property.
	 * @param postProcessor the post processor to add (not null)
	 * @return this
	 */
	C withPostProcessor(BiConsumer<Property<?>, ViewComponent<?>> postProcessor);

}
