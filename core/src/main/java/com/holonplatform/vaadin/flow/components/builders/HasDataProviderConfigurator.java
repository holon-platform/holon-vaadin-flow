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

import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

/**
 * {@link HasDataProvider} components type configurator.
 * 
 * @param <T> Data item type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasDataProviderConfigurator<T, C extends HasDataProviderConfigurator<T, C>>
		extends HasItemsConfigurator<T, C> {

	/**
	 * Set the data provider which acts as items data source.
	 * @param dataProvider The data provider to set (not null)
	 * @return this
	 */
	C dataSource(DataProvider<T, ?> dataProvider);

	/**
	 * Set the items which acts as in-memory items data source.
	 * <p>
	 * When an in-memory item set is provided, any data source configured using {@link #dataSource(DataProvider)} will
	 * be ignored.
	 * </p>
	 * @param items The data items to set
	 * @return this
	 */
	@Override
	C items(Iterable<T> items);

	/**
	 * Set the items which acts as in-memory items data source.
	 * <p>
	 * When an in-memory item set is provided, any data source configured using {@link #dataSource(DataProvider)} will
	 * be ignored.
	 * </p>
	 * @param items The data items to set
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	@Override
	C items(T... items);

	/**
	 * Add given <code>item</code> to the item set which acts as in-memory items data source.
	 * <p>
	 * When an in-memory item set is provided, any data source configured using {@link #dataSource(DataProvider)} will
	 * be ignored.
	 * </p>
	 * @param item The data item to add (not null)
	 * @return this
	 */
	@Override
	C addItem(T item);

}
