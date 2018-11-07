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
import com.holonplatform.vaadin.flow.components.events.ItemClickEvent;

/**
 * Default {@link ItemClickEvent} implementation.
 * 
 * @param <S> Source type
 * @param <T> Item type
 *
 * @since 5.2.0
 */
public class DefaultItemClickEvent<S, T> extends DefaultClickEvent<S> implements ItemClickEvent<S, T> {

	private static final long serialVersionUID = 4325465156285523799L;

	private final Supplier<T> itemSupplier;

	/**
	 * Constructor.
	 * @param source Event source (not null)
	 * @param fromClient Whether the event was originated from client
	 * @param itemSupplier Item supplier (not null)
	 */
	public DefaultItemClickEvent(S source, boolean fromClient, Supplier<T> itemSupplier) {
		super(source, fromClient);
		ObjectUtils.argumentNotNull(itemSupplier, "Item supplier must be not null");
		this.itemSupplier = itemSupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ItemClickEvent#getItem()
	 */
	@Override
	public T getItem() {
		return itemSupplier.get();
	}

}
