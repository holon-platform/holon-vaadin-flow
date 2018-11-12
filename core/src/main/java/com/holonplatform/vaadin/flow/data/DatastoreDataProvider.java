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
package com.holonplatform.vaadin.flow.data;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.internal.data.BeanPropertySetItemConverter;
import com.holonplatform.vaadin.flow.internal.data.DefaultDatastoreDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * A {@link DataProvider} which uses a {@link Datastore} for fetching data from a backend.
 * 
 * @param <T> Data type
 * @param <F> Query filter type
 *
 * @since 5.2.0
 */
public interface DatastoreDataProvider<T, F> extends DataProvider<T, F> {

	/**
	 * Get the item identifier function, if available.
	 * @return Optional item identifier function
	 */
	Optional<Function<T, Object>> getItemIdentifier();

	/**
	 * Set the item identifier function.
	 * @param itemIdentifier the item identifier function to set
	 */
	void setItemIdentifier(Function<T, Object> itemIdentifier);

	/**
	 * Get the {@link QueryConfigurationProvider}s.
	 * @return the query configuration providers, empty if none
	 */
	Set<QueryConfigurationProvider> getQueryConfigurationProviders();

	/**
	 * Add a {@link QueryConfigurationProvider}.
	 * @param queryConfigurationProvider The {@link QueryConfigurationProvider} to add (not null)
	 */
	void addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

	/**
	 * Get the default query sort, if available.
	 * @return Optional default query sort
	 */
	Optional<QuerySort> getDefaultSort();

	/**
	 * Set the default query sort.
	 * @param defaultSort the default query sort to set
	 */
	void setDefaultSort(QuerySort defaultSort);

	/**
	 * Get the function to use to convert a {@link QuerySortOrder} declaration into a {@link QuerySort}.
	 * @return the {@link QuerySortOrder} converter function
	 */
	Function<QuerySortOrder, QuerySort> getQuerySortOrderConverter();

	/**
	 * Set the function to use to convert a {@link QuerySortOrder} declaration into a {@link QuerySort}.
	 * @param querySortOrderConverter the query sort order converter to set (not null)
	 */
	void setQuerySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter);
	
	// ------- builders

	/**
	 * Create a new {@link DatastoreDataProvider} which uses {@link PropertyBox} items type and {@link QueryFilter} type
	 * data provider filters.
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @return A new {@link DatastoreDataProvider}
	 */
	static DatastoreDataProvider<PropertyBox, QueryFilter> create(Datastore datastore, DataTarget<?> target,
			PropertySet<?> propertySet) {
		return builder(datastore, target, propertySet).build();
	}

	/**
	 * Create a new {@link DatastoreDataProvider} which uses {@link PropertyBox} items type.
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 * @return A new {@link DatastoreDataProvider}
	 */
	static <F> DatastoreDataProvider<PropertyBox, F> create(Datastore datastore, DataTarget<?> target,
			PropertySet<?> propertySet, Function<F, QueryFilter> filterConverter) {
		return builder(datastore, target, propertySet, filterConverter).build();
	}

	/**
	 * Create a new {@link DatastoreDataProvider} which uses given <code>beanType</code> as items type and
	 * {@link QueryFilter} type data provider filters.
	 * @param <T> Bean type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param beanType The bean class (not null)
	 * @return A new {@link DatastoreDataProvider}
	 */
	static <T> DatastoreDataProvider<T, QueryFilter> create(Datastore datastore, DataTarget<?> target,
			Class<T> beanType) {
		return builder(datastore, target, beanType).build();
	}

	/**
	 * Create a new {@link DatastoreDataProvider} which uses given <code>beanType</code> as items type.
	 * @param <T> Bean type
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param beanType The bean class (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 * @return A new {@link DatastoreDataProvider}
	 */
	static <T, F> DatastoreDataProvider<T, F> create(Datastore datastore, DataTarget<?> target, Class<T> beanType,
			Function<F, QueryFilter> filterConverter) {
		return builder(datastore, target, beanType, filterConverter).build();
	}

	/**
	 * Create a new {@link DatastoreDataProvider}.
	 * @param <T> Data type
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} type results into the
	 *        required item type (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 * @return A new {@link DatastoreDataProvider}
	 */
	static <T, F> DatastoreDataProvider<T, F> create(Datastore datastore, DataTarget<?> target,
			PropertySet<?> propertySet, Function<PropertyBox, T> itemConverter,
			Function<F, QueryFilter> filterConverter) {
		return builder(datastore, target, propertySet, itemConverter, filterConverter).build();
	}

	/**
	 * Get a builder to create and configure a new {@link DatastoreDataProvider} which uses {@link PropertyBox} items
	 * type and {@link QueryFilter} type data provider filters.
	 * @param <T> Data type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @return a new {@link DatastoreDataProvider} builder
	 */
	static PropertyBoxItemBuilder<QueryFilter> builder(Datastore datastore, DataTarget<?> target,
			PropertySet<?> propertySet) {
		return new DefaultDatastoreDataProvider.DefaultPropertyBoxItemBuilder<>(datastore, target, propertySet,
				Function.identity(), Function.identity());
	}

	/**
	 * Get a builder to create and configure a new {@link DatastoreDataProvider} which uses {@link PropertyBox} items
	 * type.
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 * @return a new {@link DatastoreDataProvider} builder
	 */
	static <F> PropertyBoxItemBuilder<F> builder(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			Function<F, QueryFilter> filterConverter) {
		return new DefaultDatastoreDataProvider.DefaultPropertyBoxItemBuilder<>(datastore, target, propertySet,
				Function.identity(), filterConverter);
	}

	/**
	 * Get a builder to create and configure a new {@link DatastoreDataProvider} using given <code>beanType</code> as
	 * item type and {@link QueryFilter} type data provider filters.
	 * <p>
	 * The provided <code>beanType</code> class must be compliant with the JavaBeans specifications.
	 * </p>
	 * @param <T> Bean type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param beanType The bean type (not null)
	 * @return a new {@link DatastoreDataProvider} builder
	 */
	static <T> Builder<T, QueryFilter> builder(Datastore datastore, DataTarget<?> target, Class<T> beanType) {
		final BeanPropertySet<T> beanPropertySet = BeanPropertySet.create(beanType);
		return new DefaultDatastoreDataProvider.DefaultBuilder<>(datastore, target, beanPropertySet,
				new BeanPropertySetItemConverter<>(beanPropertySet), Function.identity());
	}

	/**
	 * Get a builder to create and configure a new {@link DatastoreDataProvider} using given <code>beanType</code> as
	 * item type.
	 * <p>
	 * The provided <code>beanType</code> class must be compliant with the JavaBeans specifications.
	 * </p>
	 * @param <T> Data type
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param beanType The bean type (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 * @return a new {@link DatastoreDataProvider} builder
	 */
	static <T, F> Builder<T, F> builder(Datastore datastore, DataTarget<?> target, Class<T> beanType,
			Function<F, QueryFilter> filterConverter) {
		final BeanPropertySet<T> beanPropertySet = BeanPropertySet.create(beanType);
		return new DefaultDatastoreDataProvider.DefaultBuilder<>(datastore, target, beanPropertySet,
				new BeanPropertySetItemConverter<>(beanPropertySet), filterConverter);
	}

	/**
	 * Get a builder to create and configure a new {@link DatastoreDataProvider}.
	 * @param <T> Data type
	 * @param <F> Query filter type
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} type results into the
	 *        required item type (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 * @return a new {@link DatastoreDataProvider} builder
	 */
	static <T, F> Builder<T, F> builder(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			Function<PropertyBox, T> itemConverter, Function<F, QueryFilter> filterConverter) {
		return new DefaultDatastoreDataProvider.DefaultBuilder<>(datastore, target, propertySet, itemConverter,
				filterConverter);
	}

	// -------

	/**
	 * Convert given properties into a {@link PropertySet}.
	 * @param <P> Property type
	 * @param properties The properties to convert (not null)
	 * @return A {@link PropertySet} with given properties
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertySet<?> asPropertySet(Iterable<P> properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return (properties instanceof PropertySet<?>) ? (PropertySet<?>) properties : PropertySet.of(properties);
	}

	/**
	 * Convert given properties into a {@link PropertySet}.
	 * @param properties The properties to convert (not null)
	 * @return A {@link PropertySet} with given properties
	 */
	static PropertySet<?> asPropertySet(Property<?>... properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return PropertySet.of(properties);
	}

	// -------

	/**
	 * {@link DatastoreDataProvider} builder.
	 * 
	 * @param <T> Data type
	 * @param <F> Query filter type
	 */
	public interface Builder<T, F> {

		/**
		 * Add a {@link QueryConfigurationProvider} to provide additional query configuration parameters, such as
		 * {@link QueryFilter} and {@link QuerySort}.
		 * @param queryConfigurationProvider the {@link QueryConfigurationProvider} to add (not null)
		 * @return this
		 */
		Builder<T, F> withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider);

		/**
		 * Add a fixed {@link QueryFilter} to the data provider queries.
		 * @param queryFilter The filter to add (not null)
		 * @return this
		 */
		default Builder<T, F> withQueryFilter(QueryFilter queryFilter) {
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
		default Builder<T, F> withQuerySort(QuerySort querySort) {
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
		Builder<T, F> withDefaultQuerySort(QuerySort defaultQuerySort);

		/**
		 * Set the function to use to obtain the item identifiers.
		 * @param itemIdentifierProvider the function to use to obtain the item identifiers (not null)
		 * @return this
		 */
		Builder<T, F> itemIdentifierProvider(Function<T, Object> itemIdentifierProvider);

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
		Builder<T, F> querySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter);

		/**
		 * Build the {@link DatastoreDataProvider}.
		 * @return The {@link DatastoreDataProvider} instance
		 */
		DatastoreDataProvider<T, F> build();

	}

	/**
	 * A {@link DatastoreDataProvider} with {@link PropertyBox} items type builder.
	 * 
	 * @param <F> Query filter type
	 */
	public interface PropertyBoxItemBuilder<F> extends Builder<PropertyBox, F> {

		/**
		 * Use given <code>identifierProperty</code> value as items identifier.
		 * @param identifierProperty The property which acts as item identifier (not null)
		 * @return this
		 */
		Builder<PropertyBox, F> itemIdentifier(Property<?> identifierProperty);

	}

}
