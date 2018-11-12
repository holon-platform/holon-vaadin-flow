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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;

/**
 * {@link HasDataProviderConfigurator} with {@link Datastore} configuration support and known bean type.
 * 
 * @param <T> Bean item type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasBeanDatastoreDataProviderConfigurator<T, D extends DatastoreDataProviderConfigurator<T, D>, C extends HasBeanDatastoreDataProviderConfigurator<T, D, C>>
		extends HasDataProviderConfigurator<T, C> {

	/**
	 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
	 * given {@link DataTarget} as query target and given <code>properties</code> as query projection. An
	 * <code>itemConverter</code> function is used to convert the Datastore {@link PropertyBox} data type into the
	 * required bean type.
	 * @param <P> Property type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} data type into the required
	 *        item type
	 * @param properties The property set to use as query projection (not null)
	 * @return An extended builder which allow further data provider configuration, for example to add fixed
	 *         {@link QueryFilter} and {@link QuerySort}.
	 * @see DatastoreDataProviderConfigurator
	 */
	@SuppressWarnings("rawtypes")
	<P extends Property> D dataSource(Datastore datastore, DataTarget<?> target, Function<PropertyBox, T> itemConverter,
			Iterable<P> properties);

	/**
	 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
	 * given {@link DataTarget} as query target and given <code>properties</code> as query projection. An
	 * <code>itemConverter</code> function is used to convert the Datastore {@link PropertyBox} data type into the
	 * required bean type.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} data type into the required
	 *        item type
	 * @param properties The property set to use as query projection (not null)
	 * @return An extended builder which allow further data provider configuration, for example to add fixed
	 *         {@link QueryFilter} and {@link QuerySort}.
	 * @see DatastoreDataProviderConfigurator
	 */
	default D dataSource(Datastore datastore, DataTarget<?> target, Function<PropertyBox, T> itemConverter,
			Property<?>... properties) {
		return dataSource(datastore, target, itemConverter, PropertySet.of(properties));
	}

	/**
	 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler and
	 * given {@link DataTarget} as query target. The Datastore {@link PropertyBox} type items will be converted into the
	 * required bean type.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @return An extended builder which allow further data provider configuration, for example to add fixed
	 *         {@link QueryFilter} and {@link QuerySort}.
	 * @see DatastoreDataProviderConfigurator
	 */
	D dataSource(Datastore datastore, DataTarget<?> target);

}
