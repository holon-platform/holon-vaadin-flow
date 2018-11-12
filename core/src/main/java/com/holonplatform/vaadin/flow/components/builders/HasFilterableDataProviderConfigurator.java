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

import com.vaadin.flow.data.binder.HasFilterableDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializableFunction;

/**
 * {@link HasFilterableDataProvider} components type configurator.
 * 
 * @param <T> Data item type
 * @param <FILTER> Query filter type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasFilterableDataProviderConfigurator<T, FILTER, C extends HasFilterableDataProviderConfigurator<T, FILTER, C>>
		extends HasItemsConfigurator<T, C> {

	/**
	 * Set the data provider which acts as items data source.
	 * @param dataProvider The data provider to set (not null)
	 * @return this
	 */
	C dataSource(DataProvider<T, FILTER> dataProvider);

	/**
	 * Set the items data source using a {@link DataProvider} with a different filter type and providing the filter
	 * conversion function.
	 * @param <F> DataProvider filter type
	 * @param dataProvider The data provider to set (not null)
	 * @param filterConverter a function that converts the filter values with the required type into the filter values
	 *        expected by the data provider (not null)
	 * @return this
	 */
	<F> C dataSource(DataProvider<T, F> dataProvider, SerializableFunction<FILTER, F> filterConverter);

}
