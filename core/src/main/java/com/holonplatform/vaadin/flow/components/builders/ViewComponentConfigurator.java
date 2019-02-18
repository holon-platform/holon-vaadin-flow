/*
 * Copyright 2016-2019 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;

/**
 * {@link ViewComponent} configurator.
 *
 * @param <T> Value type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.2
 */
public interface ViewComponentConfigurator<T, C extends ViewComponentConfigurator<T, C>>
		extends HtmlComponentConfigurator<C>, HasEnabledConfigurator<C>, HasLabelConfigurator<C>,
		ClickNotifierConfigurator<ViewComponent<T>, ClickEvent<ViewComponent<T>>, C>,
		DeferrableLocalizationConfigurator<C> {

	/**
	 * Set whether the label is visible.
	 * @param visible whether the label is visible
	 * @return this
	 */
	C labelVisible(boolean visible);

	/**
	 * Sets an initial value for the component.
	 * @param value The initial value to set
	 * @return this
	 */
	C withValue(T value);

	/**
	 * Add a {@link ValueChangeListener} to the component.
	 * @param listener The {@link ValueChangeListener} to add (not null)
	 * @return this
	 */
	C withValueChangeListener(ValueChangeListener<T, ValueChangeEvent<T>> listener);

}
