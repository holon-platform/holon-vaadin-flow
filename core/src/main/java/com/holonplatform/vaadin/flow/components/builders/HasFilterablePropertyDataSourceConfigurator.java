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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.data.ItemDataProvider;

/**
 * Configurator for {@link PropertyBox} item type {@link HasDataSourceConfigurator} builders, providing methods to
 * configure a {@link Datastore} and a {@link Property} set as DataSource.
 * 
 * @param <F> DataProvider filter type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasFilterablePropertyDataSourceConfigurator<F, C extends HasFilterablePropertyDataSourceConfigurator<F, C>>
		extends HasFilterableDataSourceConfigurator<PropertyBox, F, C> {

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source, providing optional fixed
	 * filters.
	 * <p>
	 * For dynamic filters and sorts configuration, a {@link QueryConfigurationProvider} can be used. See
	 * {@link #dataSource(Datastore, DataTarget, Iterable, QueryConfigurationProvider...)}.
	 * </p>
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @param properties Item property set (not null)
	 * @param filter Fixed {@link QueryFilter} to use (may be null)
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> C dataSource(Datastore datastore, DataTarget<?> dataTarget,
			Function<F, QueryFilter> filterConverter, Iterable<P> properties, QueryFilter filter) {
		return dataSource(datastore, dataTarget, filterConverter, properties,
				QueryConfigurationProvider.create(filter, null));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source, providing optional fixed
	 * filters and sorts.
	 * <p>
	 * For dynamic filters and sorts configuration, a {@link QueryConfigurationProvider} can be used. See
	 * {@link #dataSource(Datastore, DataTarget, Iterable, QueryConfigurationProvider...)}.
	 * </p>
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @param properties Item property set (not null)
	 * @param filter Fixed {@link QueryFilter} to use (may be null)
	 * @param sort Fixed {@link QuerySort} to use (may be null)
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> C dataSource(Datastore datastore, DataTarget<?> dataTarget,
			Function<F, QueryFilter> filterConverter, Iterable<P> properties, QueryFilter filter, QuerySort sort) {
		return dataSource(datastore, dataTarget, filterConverter, properties,
				QueryConfigurationProvider.create(filter, sort));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @param properties Item property set (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> C dataSource(Datastore datastore, DataTarget<?> dataTarget,
			Function<F, QueryFilter> filterConverter, Iterable<P> properties,
			QueryConfigurationProvider... queryConfigurationProviders) {
		return dataSource(ItemDataProvider.create(datastore, dataTarget, properties, queryConfigurationProviders),
				filterConverter);
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	default C dataSource(Datastore datastore, DataTarget<?> dataTarget, Function<F, QueryFilter> filterConverter,
			Property<?>... properties) {
		return dataSource(datastore, dataTarget, filterConverter, PropertySet.of(properties));
	}

}
