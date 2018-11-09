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
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.events.ItemListingItemEvent;

/**
 * Default {@link ItemListingItemEvent} implementation.
 * 
 * @param <S> Source type
 * @param <T> Item type
 * @param <P> Item property type
 *
 * @since 5.2.0
 */
public class DefaultItemListingItemEvent<S, T, P> extends DefaultItemEvent<S, T>
		implements ItemListingItemEvent<S, T, P> {

	private static final long serialVersionUID = 6849204632601290820L;

	private final ItemListing<T, P> itemListing;

	/**
	 * Constructor.
	 * @param source Event source (not null)
	 * @param itemListing The item listing (not null)
	 * @param itemSupplier Item supplier (not null)
	 */
	public DefaultItemListingItemEvent(S source, ItemListing<T, P> itemListing, Supplier<T> itemSupplier) {
		super(source, itemSupplier);
		ObjectUtils.argumentNotNull(itemListing, "ItemListing must be not null");
		this.itemListing = itemListing;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ItemListingClickEvent#getItemListing()
	 */
	@Override
	public ItemListing<T, P> getItemListing() {
		return itemListing;
	}

}
