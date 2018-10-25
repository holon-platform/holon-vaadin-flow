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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.data.ItemDataProvider;

/**
 * A {@link Selectable} input configurator which supports the {@link Property} model, using a {@link PropertyBox} as
 * DataSource item type.
 *
 * @param <T> Value type
 * @param <CONTEXT> Item conversion context type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface SelectablePropertyInputConfigurator<T, CONTEXT, C extends SelectablePropertyInputConfigurator<T, CONTEXT, C>>
		extends SelectableDataSourceInputConfigurator<T, PropertyBox, CONTEXT, C> {

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param properties Item property set (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> C dataSource(Datastore datastore, DataTarget<?> dataTarget, Iterable<P> properties,
			QueryConfigurationProvider... queryConfigurationProviders) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return dataSource(ItemDataProvider.create(datastore, dataTarget,
				(properties instanceof PropertySet) ? (PropertySet<?>) properties : PropertySet.of(properties),
				queryConfigurationProviders));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	default C dataSource(Datastore datastore, DataTarget<?> dataTarget, Property<?>... properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return dataSource(datastore, dataTarget, PropertySet.of(properties));
	}

}
