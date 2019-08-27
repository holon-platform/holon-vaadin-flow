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
package com.holonplatform.vaadin.flow.internal.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Defaut {@link DatastoreDataProvider} implementation.
 *
 * @param <T> Data type
 * @param <F> Query filter type
 *
 * @since 5.2.0
 */
public class DefaultDatastoreDataProvider<T, F> extends AbstractBackEndDataProvider<T, F>
		implements DatastoreDataProvider<T, F> {

	private static final long serialVersionUID = -2782826188714473332L;

	/**
	 * Datastore
	 */
	private final Datastore datastore;

	/**
	 * Data target
	 */
	private final DataTarget<?> target;

	/**
	 * Property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Item converter
	 */
	private final Function<PropertyBox, T> itemConverter;

	/**
	 * Query filter converter
	 */
	private Function<F, QueryFilter> filterConverter;

	/**
	 * Query sort order converter
	 */
	private Function<QuerySortOrder, QuerySort> querySortOrderConverter;

	/**
	 * Item identifier
	 */
	private Function<T, Object> itemIdentifier;

	/**
	 * Query configuration providers
	 */
	private List<QueryConfigurationProvider> queryConfigurationProviders = new LinkedList<>();

	/**
	 * Default query sort
	 */
	private QuerySort defaultSort;

	/**
	 * Constructor.
	 * @param datastore The {@link Datastore} to use (not null)
	 * @param target The {@link DataTarget} to use as query target (not null)
	 * @param propertySet The property set to use as query projection (not null)
	 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} type results into the
	 *        required item type (not null)
	 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter} (not
	 *        null)
	 */
	public DefaultDatastoreDataProvider(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
			Function<PropertyBox, T> itemConverter, Function<F, QueryFilter> filterConverter) {
		super();
		ObjectUtils.argumentNotNull(datastore, "Datastore must be not null");
		ObjectUtils.argumentNotNull(target, "DataTarget must be not null");
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		ObjectUtils.argumentNotNull(itemConverter, "Item converter must be not null");
		ObjectUtils.argumentNotNull(filterConverter, "Filter converter must be not null");
		this.datastore = datastore;
		this.target = target;
		this.propertySet = propertySet;
		this.itemConverter = itemConverter;
		this.filterConverter = filterConverter;
		this.querySortOrderConverter = order -> fromOrder(propertySet, order);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#getItemIdentifier()
	 */
	@Override
	public Optional<Function<T, Object>> getItemIdentifier() {
		return Optional.ofNullable(itemIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#setItemIdentifier(java.util.function.Function)
	 */
	@Override
	public void setItemIdentifier(Function<T, Object> itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#getFilterConverter()
	 */
	@Override
	public Function<F, QueryFilter> getFilterConverter() {
		return filterConverter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#setFilterConverter(java.util.function.Function)
	 */
	@Override
	public void setFilterConverter(Function<F, QueryFilter> filterConverter) {
		ObjectUtils.argumentNotNull(filterConverter, "The filter converter function must be not null");
		this.filterConverter = filterConverter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#getQueryConfigurationProviders()
	 */
	@Override
	public Set<QueryConfigurationProvider> getQueryConfigurationProviders() {
		return Collections.unmodifiableSet(new HashSet<>(queryConfigurationProviders));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.data.DatastoreDataProvider#addQueryConfigurationProvider(com.holonplatform.core.
	 * query.QueryConfigurationProvider)
	 */
	@Override
	public void addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		ObjectUtils.argumentNotNull(queryConfigurationProvider, "QueryConfigurationProvider must be not null");
		this.queryConfigurationProviders.add(queryConfigurationProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#getDefaultSort()
	 */
	@Override
	public Optional<QuerySort> getDefaultSort() {
		return Optional.ofNullable(defaultSort);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.data.DatastoreDataProvider#setDefaultSort(com.holonplatform.core.query.QuerySort)
	 */
	@Override
	public void setDefaultSort(QuerySort defaultSort) {
		this.defaultSort = defaultSort;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider#getQuerySortOrderConverter()
	 */
	@Override
	public Function<QuerySortOrder, QuerySort> getQuerySortOrderConverter() {
		return querySortOrderConverter;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.data.DatastoreDataProvider#setQuerySortOrderConverter(java.util.function.Function)
	 */
	@Override
	public void setQuerySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
		ObjectUtils.argumentNotNull(querySortOrderConverter, "QuerySortOrder converter function must be not null");
		this.querySortOrderConverter = querySortOrderConverter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.DataProvider#isInMemory()
	 */
	@Override
	public boolean isInMemory() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.DataProvider#getId(java.lang.Object)
	 */
	@Override
	public Object getId(T item) {
		if (getItemIdentifier().isPresent()) {
			return getItemIdentifier().get().apply(item);
		}
		return super.getId(item);
	}

	@Override
	public Optional<QueryFilter> getQueryFilter() {
		final List<QueryFilter> filters = new LinkedList<>();
		queryConfigurationProviders.forEach(p -> {
			QueryFilter qf = p.getQueryFilter();
			if (qf != null) {
				filters.add(qf);
			}
		});
		return QueryFilter.allOf(filters);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.flow.data.provider.AbstractBackEndDataProvider#fetchFromBackEnd(com.vaadin.flow.data.provider.Query)
	 */
	@Override
	protected Stream<T> fetchFromBackEnd(Query<T, F> query) {
		return _query(query, true).stream(propertySet).map(item -> itemConverter.apply(item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.provider.AbstractBackEndDataProvider#sizeInBackEnd(com.vaadin.flow.data.provider.Query)
	 */
	@Override
	protected int sizeInBackEnd(Query<T, F> query) {
		return Long.valueOf(_query(query, false).count()).intValue();
	}

	/**
	 * Build a {@link Query} using the Datastore and configure it with the query filters and sorts.
	 * @param query The data provider query
	 * @param withSorts Whether to apply the query sorts, if any
	 * @return A new Datastore query
	 */
	protected com.holonplatform.core.query.Query _query(Query<?, F> query, boolean withSorts) {

		// build a new query using configured target
		com.holonplatform.core.query.Query q = datastore.query(target);

		// filters
		final List<QueryFilter> filters = new LinkedList<>();

		// data provider filter
		if (query != null) {
			query.getFilter().ifPresent(f -> {
				QueryFilter filter = filterConverter.apply(f);
				if (filter != null) {
					filters.add(filter);
				}
			});
		}

		// provided filters
		queryConfigurationProviders.forEach(p -> {
			QueryFilter qf = p.getQueryFilter();
			if (qf != null) {
				filters.add(qf);
			}
		});
		QueryFilter.allOf(filters).ifPresent(f -> q.filter(f));

		// sorts
		if (withSorts) {
			final List<QuerySort> sorts = new LinkedList<>();

			// data provider sorts
			if (query != null) {
				List<QuerySortOrder> orders = query.getSortOrders();
				if (orders != null && !orders.isEmpty()) {
					orders.forEach(order -> sorts.add(Optional.ofNullable(querySortOrderConverter.apply(order))
							.orElseThrow(() -> new IllegalStateException(
									"The query sort converter returned a null sort for [" + order + "]"))));
				}
			}

			// default sort
			if (sorts.isEmpty()) {
				getDefaultSort().ifPresent(ds -> sorts.add(ds));
			}

			// provided sorts
			queryConfigurationProviders.forEach(p -> {
				QuerySort qs = p.getQuerySort();
				if (qs != null) {
					sorts.add(qs);
				}
			});

			if (!sorts.isEmpty()) {
				q.sort(QuerySort.of(sorts));
			}
		}

		// parameters
		queryConfigurationProviders.forEach(p -> {
			ParameterSet parameters = p.getQueryParameters();
			if (parameters != null) {
				parameters.forEachParameter((n, v) -> q.parameter(n, v));
			}
		});

		// paging
		if (query != null) {
			if (query.getLimit() < Integer.MAX_VALUE) {
				q.limit(query.getLimit());
				q.offset(query.getOffset());
			}
		}

		return q;
	}

	// ------- Default query sort order conversion functions

	/**
	 * Get a {@link QuerySort} from given {@link QuerySortOrder}, using the provided property set.
	 * @param propertySet The property set to use to detect the sort properties
	 * @param order The {@link QuerySortOrder} to convert
	 * @return The {@link QuerySort} which represents given {@link QuerySortOrder}
	 * @throws IllegalArgumentException If a property with the required sort name is not available in the provided
	 *         property set
	 */
	private static QuerySort fromOrder(PropertySet<?> propertySet, QuerySortOrder order) {
		final Path<?> path = getPathByName(propertySet, order.getSorted())
				.orElseThrow(() -> new IllegalArgumentException(
						"No property of the set matches the sort name: " + order.getSorted()));
		final SortDirection direction = (order.getDirection() != null
				&& order.getDirection() == com.vaadin.flow.data.provider.SortDirection.DESCENDING)
						? SortDirection.DESCENDING
						: SortDirection.ASCENDING;
		return QuerySort.of(path, direction);
	}

	/**
	 * Try to obtain a {@link Path} which corresponds to given <code>propertyName</code>, using the provided property
	 * set.
	 * @param propertySet The property set to use
	 * @param propertyName The property name to look for
	 * @return Optional {@link Path} which corresponds to given <code>propertyName</code>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Optional<Path<?>> getPathByName(PropertySet propertySet, String propertyName) {
		if (propertySet != null && propertyName != null) {
			return propertySet.stream().filter(p -> Path.class.isAssignableFrom(p.getClass()))
					.filter(p -> propertyName.equals(((Path) p).getName())).findFirst();
		}
		return Optional.empty();
	}

	// ------ Builder

	/**
	 * Base {@link Builder} implementation.
	 *
	 * @param <T> Data type
	 * @param <F> Query filter type
	 * @param <B> Concrete builder type
	 */
	static abstract class AbstractBuilder<T, F, B extends Builder<T, F>> implements Builder<T, F> {

		protected final DefaultDatastoreDataProvider<T, F> dataProvider;

		/**
		 * Constructor.
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param propertySet The property set to use as query projection (not null)
		 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} type results into the
		 *        required item type (not null)
		 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter}
		 *        (not null)
		 */
		public AbstractBuilder(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
				Function<PropertyBox, T> itemConverter, Function<F, QueryFilter> filterConverter) {
			super();
			this.dataProvider = new DefaultDatastoreDataProvider<>(datastore, target, propertySet, itemConverter,
					filterConverter);
		}

		/**
		 * Get the concrete builder.
		 * @return the concrete builder
		 */
		protected abstract B getBuilder();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider.Builder#withQueryConfigurationProvider(com.
		 * holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public B withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
			dataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
			return getBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.data.DatastoreDataProvider.Builder#withDefaultQuerySort(com.holonplatform.core.
		 * query.QuerySort)
		 */
		@Override
		public B withDefaultQuerySort(QuerySort defaultQuerySort) {
			dataProvider.setDefaultSort(defaultQuerySort);
			return getBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.data.DatastoreDataProvider.Builder#itemIdentifierProvider(java.util.function.
		 * Function)
		 */
		@Override
		public B itemIdentifierProvider(Function<T, Object> itemIdentifierProvider) {
			dataProvider.setItemIdentifier(itemIdentifierProvider);
			return getBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.data.DatastoreDataProvider.Builder#querySortOrderConverter(java.util.function.
		 * Function)
		 */
		@Override
		public B querySortOrderConverter(Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			dataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return getBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider.Builder#build()
		 */
		@Override
		public DatastoreDataProvider<T, F> build() {
			return dataProvider;
		}

	}

	/**
	 * Default {@link Builder} implementation.
	 *
	 * @param <T> Data type
	 * @param <F> Query filter type
	 */
	public static class DefaultBuilder<T, F> extends AbstractBuilder<T, F, Builder<T, F>> {

		/**
		 * Constructor.
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param propertySet The property set to use as query projection (not null)
		 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} type results into the
		 *        required item type (not null)
		 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter}
		 *        (not null)
		 */
		public DefaultBuilder(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
				Function<PropertyBox, T> itemConverter, Function<F, QueryFilter> filterConverter) {
			super(datastore, target, propertySet, itemConverter, filterConverter);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.data.DefaultDatastoreDataProvider.AbstractBuilder#getBuilder()
		 */
		@Override
		protected Builder<T, F> getBuilder() {
			return this;
		}

	}

	/**
	 * Default {@link PropertyBoxItemBuilder} implementation.
	 *
	 * @param <F> Query filter type
	 */
	public static class DefaultPropertyBoxItemBuilder<F>
			extends AbstractBuilder<PropertyBox, F, PropertyBoxItemBuilder<F>> implements PropertyBoxItemBuilder<F> {

		/**
		 * Constructor.
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param propertySet The property set to use as query projection (not null)
		 * @param itemConverter The function to use to convert the Datastore {@link PropertyBox} type results into the
		 *        required item type (not null)
		 * @param filterConverter The function to use to convert the data provider filters into a {@link QueryFilter}
		 *        (not null)
		 */
		public DefaultPropertyBoxItemBuilder(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet,
				Function<PropertyBox, PropertyBox> itemConverter, Function<F, QueryFilter> filterConverter) {
			super(datastore, target, propertySet, itemConverter, filterConverter);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.data.DatastoreDataProvider.PropertyBoxItemBuilder#itemIdentifier(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public Builder<PropertyBox, F> itemIdentifier(Property<?> identifierProperty) {
			ObjectUtils.argumentNotNull(identifierProperty, "Identifier property must be not null");
			dataProvider.setItemIdentifier(item -> {
				if (item != null) {
					return item.getValueIfPresent(identifierProperty)
							.orElseThrow(() -> new DataAccessException("The identifier property [" + identifierProperty
									+ "] is not present in PropertyBox item [" + item + "]"));
				}
				return null;
			});
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.data.DefaultDatastoreDataProvider.AbstractBuilder#getBuilder()
		 */
		@Override
		protected PropertyBoxItemBuilder<F> getBuilder() {
			return this;
		}

	}

}
