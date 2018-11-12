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

import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Configurator for components which supports a {@link DatastoreDataProvider}.
 * 
 * @param <ITEM> Item data type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface DatastoreDataProviderConfigurator<ITEM, C extends DatastoreDataProviderConfigurator<ITEM, C>> {

	/**
	 * Add a {@link QueryConfigurationProvider} to provide additional query configuration parameters, such as
	 * {@link QueryFilter} and {@link QuerySort}.
	 * @param queryConfigurationProvider the {@link QueryConfigurationProvider} to add (not null)
	 * @return this
	 */
	C withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

	/**
	 * Add a fixed {@link QueryFilter} to the data provider queries.
	 * @param queryFilter The filter to add (not null)
	 * @return this
	 */
	default C withQueryFilter(QueryFilter queryFilter) {
		return withQueryConfigurationProvider(QueryConfigurationProvider.create(queryFilter, null));
	}

	/**
	 * Add a fixed {@link QuerySort} to the data provider queries.
	 * <p>
	 * The provided sort will be appended to any other dynamic sort of the queries.
	 * </p>
	 * @param querySort The sort to add (not null)
	 * @return this
	 */
	default C withQuerySort(QuerySort querySort) {
		return withQueryConfigurationProvider(QueryConfigurationProvider.create(null, querySort));
	}

	/**
	 * Add a default {@link QuerySort} to the data provider queries.
	 * <p>
	 * The provided sort will be used when no other sort is available for the queries.
	 * </p>
	 * @param defaultQuerySort The default sort to add
	 * @return this
	 */
	C withDefaultQuerySort(QuerySort defaultQuerySort);

	/**
	 * Set the function to use to obtain the item identifiers.
	 * @param itemIdentifierProvider the function to use to obtain the item identifiers (not null)
	 * @return this
	 */
	C itemIdentifierProvider(Function<ITEM, Object> itemIdentifierProvider);

	/**
	 * Set the function to use to convert a {@link QuerySortOrder} declaration into a {@link QuerySort}.
	 * <p>
	 * By default, a property with a matching path name is used to obtain the {@link QuerySort}, if available in the
	 * configured query projection property set.
	 * </p>
	 * @param querySortOrderConverter the function to use to convert a {@link QuerySortOrder} declaration into a
	 *        {@link QuerySort} (not null)
	 * @return this
	 */
	C querySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter);

}
