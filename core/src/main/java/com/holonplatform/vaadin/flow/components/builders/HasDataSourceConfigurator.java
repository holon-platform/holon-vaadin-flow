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

import com.holonplatform.vaadin.flow.data.ItemDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

/**
 * Configurator for components which supports a DataSource using an {@link ItemDataProvider}.
 * 
 * @param <ITEM> Item type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasDataSourceConfigurator<ITEM, C extends HasDataSourceConfigurator<ITEM, C>>
		extends HasItemsConfigurator<ITEM, C> {

	/**
	 * Set the selection items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	C dataSource(ItemDataProvider<ITEM> dataProvider);

	/**
	 * Set the selection items data provider.
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	C dataSource(DataProvider<ITEM, ?> dataProvider);

}
