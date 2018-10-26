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

import com.vaadin.flow.data.provider.DataProvider;

/**
 * Configurator for components which supports a set of items and a items data source.
 * 
 * @param <ITEM> Item type
 * @param <F> DataProvider filter type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasItemsDataSourceConfigurator<ITEM, F, C extends HasItemsDataSourceConfigurator<ITEM, F, C>>
		extends HasItemsConfigurator<ITEM, C> {

	/**
	 * Set the items data provider.
	 * @param dataProvider The data provider to set (not null)
	 * @return this
	 */
	C dataSource(DataProvider<ITEM, F> dataProvider);

}
