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
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;

/**
 * A {@link ValueHolder} component to display a value in UI.
 * 
 * @param <V> Value type
 *
 * @since 5.2.0
 */
public interface ViewComponent<V> extends ValueHolder<V>, ValueComponent<V>, HasSize, HasStyle, HasEnabled, HasText {

	/**
	 * Gets a builder to create {@link ViewComponent} instances.
	 * @param <T> Value type
	 * @param valueType Value type handled by the ViewComponent
	 * @return ViewComponent instance
	 */
	static <T> ViewComponentBuilder<T> builder(Class<? extends T> valueType) {
		// return new DefaultViewComponentBuilder<>(valueType);
		// TODO
		return null;
	}

}
