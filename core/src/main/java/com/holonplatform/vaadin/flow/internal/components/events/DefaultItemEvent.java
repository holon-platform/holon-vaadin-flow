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

import java.util.function.Supplier;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.events.ItemEvent;

/**
 * Default {@link ItemEvent} implementation.
 * 
 * @param <S> Source type
 * @param <T> Item type
 *
 * @since 5.2.0
 */
public class DefaultItemEvent<S, T> implements ItemEvent<S, T> {

	private static final long serialVersionUID = -575204783489760297L;

	private final S source;
	private final Supplier<T> itemSupplier;

	public DefaultItemEvent(S source, Supplier<T> itemSupplier) {
		super();
		ObjectUtils.argumentNotNull(itemSupplier, "Item supplier must be not null");
		this.source = source;
		this.itemSupplier = itemSupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ItemEvent#getSource()
	 */
	@Override
	public S getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ItemEvent#getItem()
	 */
	@Override
	public T getItem() {
		return itemSupplier.get();
	}

}
