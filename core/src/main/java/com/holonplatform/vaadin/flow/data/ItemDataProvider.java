/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.data;

import java.util.function.Function;

import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.internal.data.DatastoreBeanItemDataProvider;
import com.holonplatform.vaadin.flow.internal.data.DatastoreItemDataProvider;
import com.holonplatform.vaadin.flow.internal.data.DefaultItemDataProvider;
import com.holonplatform.vaadin.flow.internal.data.ItemDataProviderAdapter;
import com.holonplatform.vaadin.flow.internal.data.ItemDataProviderWrapper;
import com.vaadin.flow.data.provider.DataProvider;

/**
 * Iterface to load items data from a data source.
 * 
 * @param <ITEM> Item data type
 * 
 * @since 5.2.0
 */
public interface ItemDataProvider<ITEM> extends ItemSetCounter, ItemSetLoader<ITEM> {

	/**
	 * Create an {@link ItemDataProvider} using given operations.
	 * @param <ITEM> Item data type
	 * @param counter Items counter (not null)
	 * @param loader Items loader (not null)
	 * @return A new {@link ItemDataProvider} instance
	 */
	static <ITEM> ItemDataProvider<ITEM> create(ItemSetCounter counter, ItemSetLoader<ITEM> loader) {
		return new DefaultItemDataProvider<>(counter, loader);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore}.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param properties Property set
	 * @return the {@link ItemDataProvider} instance
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> ItemDataProvider<PropertyBox> create(Datastore datastore, DataTarget<?> target,
			Iterable<P> properties) {
		return create(datastore, target, properties, new QueryConfigurationProvider[0]);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore}.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param properties Property set (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return the {@link ItemDataProvider} instance
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> ItemDataProvider<PropertyBox> create(Datastore datastore, DataTarget<?> target,
			Iterable<P> properties, QueryConfigurationProvider... queryConfigurationProviders) {
		DatastoreItemDataProvider provider = new DatastoreItemDataProvider(datastore, target,
				asPropertySet(properties));
		if (queryConfigurationProviders != null) {
			for (QueryConfigurationProvider queryConfigurationProvider : queryConfigurationProviders) {
				provider.addQueryConfigurationProvider(queryConfigurationProvider);
			}
		}
		return provider;
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore} and given <code>beanClass</code> as item type.
	 * <p>
	 * The query projection will be configured using the bean class property names and the query results will be
	 * obtained as instances of given bean class. The default {@link BeanIntrospector} will be used to inspect bean
	 * class properties.
	 * </p>
	 * @param <T> Bean type
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param beanClass Item bean type (not null)
	 * @return the {@link ItemDataProvider} instance
	 */
	static <T> ItemDataProvider<T> create(Datastore datastore, DataTarget<?> target, Class<T> beanClass) {
		return new DatastoreBeanItemDataProvider<>(datastore, target, beanClass);
	}

	/**
	 * Construct a {@link ItemDataProvider} using a {@link Datastore} and given <code>beanClass</code> as item type.
	 * <p>
	 * The query projection will be configured using the bean class property names and the query results will be
	 * obtained as instances of given bean class. The default {@link BeanIntrospector} will be used to inspect bean
	 * class properties.
	 * </p>
	 * @param <T> Bean type
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 * @param beanClass Item bean type (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return the {@link ItemDataProvider} instance
	 */
	static <T> ItemDataProvider<T> create(Datastore datastore, DataTarget<?> target, Class<T> beanClass,
			QueryConfigurationProvider... queryConfigurationProviders) {
		DatastoreBeanItemDataProvider<T> provider = new DatastoreBeanItemDataProvider<>(datastore, target, beanClass);
		if (queryConfigurationProviders != null) {
			for (QueryConfigurationProvider queryConfigurationProvider : queryConfigurationProviders) {
				provider.addQueryConfigurationProvider(queryConfigurationProvider);
			}
		}
		return provider;
	}

	/**
	 * Create a new {@link ItemDataProvider} which wraps a concrete data provider and converts items into a different
	 * type using a converter function.
	 * @param <ITEM> Item type
	 * @param <T> Converted type
	 * @param provider Concrete data privider (not null)
	 * @param converter Converter function (not null)
	 * @return the data provider wrapper
	 * @return the {@link ItemDataProvider} instance
	 */
	static <T, ITEM> ItemDataProvider<T> convert(ItemDataProvider<ITEM> provider, Function<ITEM, T> converter) {
		return new ItemDataProviderWrapper<>(provider, converter);
	}

	/**
	 * Get the properties {@link Iterable} as a {@link PropertySet}.
	 * @param properties Properties (not null)
	 * @return The {@link PropertySet}
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertySet<?> asPropertySet(Iterable<P> properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return (properties instanceof PropertySet) ? (PropertySet<?>) properties : PropertySet.of(properties);
	}

	/**
	 * Adapt given {@link ItemDataProvider} as a {@link DataProvider}.
	 * @param <ITEM> Item type
	 * @param <F> {@link DataProvider} filter type
	 * @param itemDataProvider The {@link ItemDataProvider} (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @return A new {@link DataProvider} backed by the provided {@link ItemDataProvider}
	 */
	static <ITEM, F> DataProvider<ITEM, F> adapt(ItemDataProvider<ITEM> itemDataProvider,
			Function<F, QueryFilter> filterConverter) {
		return new ItemDataProviderAdapter<>(itemDataProvider, filterConverter);
	}

	/**
	 * Adapt given {@link ItemDataProvider} as a {@link DataProvider}.
	 * @param <ITEM> Item type
	 * @param <F> {@link DataProvider} filter type
	 * @param itemDataProvider The {@link ItemDataProvider} (not null)
	 * @param filterConverter The function to use to convert the query filter type into a {@link QueryFilter} instance
	 *        (not null)
	 * @param itemIdentifier Provider for the item ids
	 * @return A new {@link DataProvider} backed by the provided {@link ItemDataProvider}
	 */
	static <ITEM, F> DataProvider<ITEM, F> adapt(ItemDataProvider<ITEM> itemDataProvider,
			Function<F, QueryFilter> filterConverter, ItemIdentifierProvider<ITEM, ?> itemIdentifier) {
		return new ItemDataProviderAdapter<>(itemDataProvider, filterConverter, itemIdentifier);
	}

}
