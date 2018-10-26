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

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.vaadin.flow.data.ItemDataProvider;

/**
 * Configurator for components which supports a DataSource using an {@link ItemDataProvider}.
 * 
 * @param <ITEM> Item type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasDataSourceConfigurator<ITEM, C extends HasDataSourceConfigurator<ITEM, C>>
		extends HasItemsDataSourceConfigurator<ITEM, Object, C> {

	/**
	 * Set the items data provider using an {@link ItemDataProvider}.
	 * @param dataProvider The items data provider to set (not null)
	 * @return this
	 */
	default C dataSource(ItemDataProvider<ITEM> dataProvider) {
		return dataSource(ItemDataProvider.adapt(dataProvider, f -> null));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source, providing the items bean
	 * class to use as property set and query projection type.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param dataTarget The query {@link DataTarget} to use (not null)
	 * @param itemBeanClass Item bean class (not null)
	 * @return this
	 */
	default C dataSource(Datastore datastore, DataTarget<?> dataTarget, Class<ITEM> itemBeanClass) {
		return dataSource(ItemDataProvider.create(datastore, dataTarget, itemBeanClass));
	}

}
