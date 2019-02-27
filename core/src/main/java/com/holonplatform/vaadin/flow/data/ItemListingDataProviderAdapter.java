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
package com.holonplatform.vaadin.flow.data;

import java.util.List;

import com.holonplatform.vaadin.flow.internal.data.DefaultItemListingDataProviderAdapter;
import com.vaadin.flow.data.provider.DataProvider;

/**
 * A {@link DataProvider} adapter to be used in item listings.
 * 
 * @param <T> Data type
 * @param <F> Filter type
 * 
 * @since 5.2.2
 */
public interface ItemListingDataProviderAdapter<T, F> extends DataProvider<T, F> {

	/**
	 * Get whether the data provider is <em>frozen</em>.
	 * <p>
	 * When the data provider is <em>frozen</em>, it never returns any item (the
	 * {@link #size(com.vaadin.flow.data.provider.Query)} will always be <code>0</code>).
	 * </p>
	 * @return Whether the data provider is <em>frozen</em>
	 */
	boolean isFrozen();

	/**
	 * Set whether the data provider is <em>frozen</em>.
	 * <p>
	 * When the data provider is <em>frozen</em>, it never returns any item (the
	 * {@link #size(com.vaadin.flow.data.provider.Query)} will always be <code>0</code>).
	 * </p>
	 * @param frozen Whether the data provider is <em>frozen</em>
	 */
	void setFrozen(boolean frozen);

	/**
	 * Get the current additional items, if any.
	 * <p>
	 * Additional items are provided in addition to the ones returned by the concrete data provider, for example from a
	 * backend service. They are always provided before any other item, and do not take part in filters and sorts.
	 * </p>
	 * @return The additional items, in the order they were added. An empty list if none. The list is not modifiable.
	 * @see #addAdditionalItem(Object)
	 */
	List<T> getAdditionalItems();

	/**
	 * Add an additional item to the data provider.
	 * <p>
	 * Additional items are provided in addition to the ones returned by the concrete data provider, for example from a
	 * backend service. They are always provided before any other item, and do not take part in filters and sorts.
	 * </p>
	 * <p>
	 * The additional items should be used, for example, to edit item values before saving into the backend. Then they
	 * should be removed from the data provider.
	 * </p>
	 * <p>
	 * NOTE: Additional items are identified in the same way than any other item, using the {@link #getId(Object)}
	 * method. So the id providing logic should be consistent with any other item.
	 * </p>
	 * @param item The item to add (not null)
	 * @see #removeAdditionalItem(Object)
	 */
	void addAdditionalItem(T item);

	/**
	 * Remove an additional item from the data provider.
	 * @param item The item to remove (not null)
	 * @return <code>true</code> if given item was an additional item and it's been removed, <code>false</code>
	 *         otherwise
	 * @see #addAdditionalItem(Object)
	 * @see #removeAdditionalItems()
	 */
	boolean removeAdditionalItem(T item);

	/**
	 * Remove all the additional items from the data provider.
	 * @see #addAdditionalItem(Object)
	 */
	void removeAdditionalItems();

	// ------- builders

	/**
	 * Create a new {@link ItemListingDataProviderAdapter} using given concrete <code>dataProvider</code>.
	 * @param <T> Data type
	 * @param <F> Filter type
	 * @param dataProvider The concrete data provider (not null)
	 * @return A new {@link ItemListingDataProviderAdapter}
	 */
	static <T, F> ItemListingDataProviderAdapter<T, F> adapt(DataProvider<T, F> dataProvider) {
		return new DefaultItemListingDataProviderAdapter<>(dataProvider);
	}

}
