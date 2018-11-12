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
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;

/**
 * {@link HasDataProviderConfigurator} with {@link Datastore} configuration support and using {@link PropertyBox} type
 * items and a fixed property set.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasPropertySetDatastoreDataProviderConfigurator<D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends HasPropertySetDatastoreDataProviderConfigurator<D, C>>
		extends HasDataProviderConfigurator<PropertyBox, C> {

	/**
	 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
	 * given {@link DataTarget} as query target and given <code>properties</code> as query projection.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @return An extended builder which allow further data provider configuration, for example to add fixed
	 *         {@link QueryFilter} and {@link QuerySort}.
	 * @see DatastoreDataProviderConfigurator
	 */
	D dataSource(Datastore datastore, DataTarget<?> target);

}
