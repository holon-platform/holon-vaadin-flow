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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentAdapterBuilder;
import com.holonplatform.vaadin.flow.internal.components.ViewComponentAdapter;
import com.vaadin.flow.component.Component;

/**
 * Default {@link ViewComponentAdapterBuilder} implementation.
 *
 * @param <C> Content type
 * @param <T> Value type
 *
 * @since 5.2.2
 */
public class DefaultViewComponentAdapterBuilder<C extends Component, T>
		extends AbstractViewComponentBuilder<C, T, ViewComponentAdapter<C, T>, ViewComponentAdapterBuilder<T>>
		implements ViewComponentAdapterBuilder<T> {

	/**
	 * Constructor.
	 * @param componentProvider The function to provide a content component each time the value changes (not null)
	 */
	public DefaultViewComponentAdapterBuilder(Function<T, C> componentProvider) {
		super(new ViewComponentAdapter<>(componentProvider));
	}

	/**
	 * Constructor.
	 * @param content The fixed content component (not null)
	 * @param valueConsumer The consumer to configure the content component each time the value changes (not null)
	 */
	public DefaultViewComponentAdapterBuilder(C content, BiConsumer<C, T> valueConsumer) {
		super(new ViewComponentAdapter<>(content, valueConsumer));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ViewComponentAdapterBuilder<T> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentAdapterBuilder#build()
	 */
	@Override
	public ViewComponent<T> build() {
		return getComponent();
	}

}
