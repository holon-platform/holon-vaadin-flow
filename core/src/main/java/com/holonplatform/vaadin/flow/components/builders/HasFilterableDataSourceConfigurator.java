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

import java.util.function.Function;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.data.ItemDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Configurator for components which supports a DataSource using an {@link ItemDataProvider}.
 * 
 * @param <ITEM> Item type
 * @param <F> DataProvider filter type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasFilterableDataSourceConfigurator<ITEM, F, C extends HasFilterableDataSourceConfigurator<ITEM, F, C>>
		extends HasItemsDataSourceConfigurator<ITEM, F, C> {

	/**
	 * Set the items data provider, using a {@link DataProvider} with a different filter type.
	 * @param <FILTER> DataProvider filter type
	 * @param dataProvider The data provider to set (not null)
	 * @param filterConverter a function that converts filter values with the required type into filter values expected
	 *        by the provided data provider (not null)
	 * @return this
	 */
	<FILTER> C dataSource(DataProvider<ITEM, FILTER> dataProvider, SerializableFunction<F, FILTER> filterConverter);

	/**
	 * Set the items data provider using an {@link ItemDataProvider}.
	 * @param dataProvider The items data provider (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @return this
	 */
	default C dataSource(ItemDataProvider<ITEM> dataProvider, Function<F, QueryFilter> filterConverter) {
		return dataSource(ItemDataProvider.adapt(dataProvider, filterConverter));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source, providing the items bean
	 * class to use as property set and query projection type.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param dataTarget The query {@link DataTarget} to use (not null)
	 * @param itemBeanClass Item bean class (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @return this
	 */
	default C dataSource(Datastore datastore, DataTarget<?> dataTarget, Class<ITEM> itemBeanClass,
			Function<F, QueryFilter> filterConverter) {
		return dataSource(ItemDataProvider.create(datastore, dataTarget, itemBeanClass), filterConverter);
	}

}
