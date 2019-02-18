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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.vaadin.flow.component.Component;

/**
 * {@link ViewComponent} adapter.
 * 
 * @param <C> Content type
 * @param <T> Value type
 *
 * @since 5.2.2
 */
public class ViewComponentAdapter<C extends Component, T> extends AbstractViewComponent<C, T> {

	private static final long serialVersionUID = -8212334069453034725L;

	private final Function<T, C> componentProvider;

	/**
	 * Constructor.
	 * @param componentProvider The function to provide a content component each time the value changes (not null)
	 */
	public ViewComponentAdapter(Function<T, C> componentProvider) {
		super(null);
		ObjectUtils.argumentNotNull(componentProvider, "Component provider function must be not null");
		this.componentProvider = componentProvider;
	}

	/**
	 * Constructor.
	 * @param content The fixed content component (not null)
	 * @param valueConsumer The consumer to configure the content component each time the value changes (not null)
	 */
	public ViewComponentAdapter(C content, BiConsumer<C, T> valueConsumer) {
		super(content);
		ObjectUtils.argumentNotNull(content, "Content must be not null");
		ObjectUtils.argumentNotNull(valueConsumer, "Value consumer must be not null");
		this.componentProvider = value -> {
			valueConsumer.accept(content, value);
			return content;
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractViewComponent#updateValue(java.lang.Object)
	 */
	@Override
	protected Optional<C> updateValue(T value) {
		return Optional.ofNullable(componentProvider.apply(value));
	}

}
