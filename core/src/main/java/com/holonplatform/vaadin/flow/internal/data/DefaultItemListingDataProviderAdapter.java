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
package com.holonplatform.vaadin.flow.internal.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter;
import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

/**
 * Default {@link ItemListingDataProviderAdapter} implementation.
 *
 * @param <T> Data type
 * @param <F> Filter type
 * 
 * @since 5.2.2
 */
public class DefaultItemListingDataProviderAdapter<T, F> extends AbstractDataProvider<T, F>
		implements ItemListingDataProviderAdapter<T, F> {

	private static final long serialVersionUID = -5037549436183583417L;

	private final DataProvider<T, F> dataProvider;

	private boolean frozen = false;

	private final List<T> additionalItems = new LinkedList<>();

	/**
	 * Constructor.
	 * @param dataProvider Concrete data provider (not null)
	 */
	public DefaultItemListingDataProviderAdapter(DataProvider<T, F> dataProvider) {
		super();
		ObjectUtils.argumentNotNull(dataProvider, "DataProvider must be not null");
		this.dataProvider = dataProvider;
	}

	/**
	 * Get the concrete data provider.
	 * @return The concrete data provider
	 */
	protected DataProvider<T, F> getDataProvider() {
		return dataProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.DataProvider#isInMemory()
	 */
	@Override
	public boolean isInMemory() {
		return getDataProvider().isInMemory();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.DataProvider#size(com.vaadin.flow.data.provider.Query)
	 */
	@Override
	public int size(Query<T, F> query) {
		if (isFrozen()) {
			return 0;
		}
		return getDataProvider().size(query) + this.additionalItems.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.DataProvider#fetch(com.vaadin.flow.data.provider.Query)
	 */
	@Override
	public Stream<T> fetch(Query<T, F> query) {
		if (isFrozen()) {
			return Stream.empty();
		}
		if (!this.additionalItems.isEmpty()) {
			return Stream.concat(this.additionalItems.stream(), getDataProvider().fetch(query));
		}
		return getDataProvider().fetch(query);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter#isFrozen()
	 */
	@Override
	public boolean isFrozen() {
		return frozen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter#setFrozen(boolean)
	 */
	@Override
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter#getAdditionalItems()
	 */
	@Override
	public List<T> getAdditionalItems() {
		return Collections.unmodifiableList(this.additionalItems);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter#addAdditionalItem(java.lang.Object)
	 */
	@Override
	public void addAdditionalItem(T item) {
		ObjectUtils.argumentNotNull(item, "Additional item to add must be not null");
		this.additionalItems.add(item);
		refreshAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter#removeAdditionalItem(java.lang.Object)
	 */
	@Override
	public boolean removeAdditionalItem(T item) {
		ObjectUtils.argumentNotNull(item, "Additional item to remove must be not null");
		if (this.additionalItems.remove(item)) {
			refreshAll();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter#removeAdditionalItems()
	 */
	@Override
	public void removeAdditionalItems() {
		if (!this.additionalItems.isEmpty()) {
			this.additionalItems.clear();
			refreshAll();
		}
	}

}
