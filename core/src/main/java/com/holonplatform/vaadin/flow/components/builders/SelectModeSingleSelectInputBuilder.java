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

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultItemSelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertySelectModeSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.Renderer;

/**
 * {@link SingleSelect} input builder for the <em>select</em> rendering mode.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface SelectModeSingleSelectInputBuilder<T, ITEM, B extends SelectModeSingleSelectInputBuilder<T, ITEM, B>>
		extends SingleSelectInputBuilder<T, ITEM, B>, HasSizeConfigurator<B>, HasLabelConfigurator<B>,
		HasPlaceholderConfigurator<B>, HasPatternConfigurator<B>, HasAutofocusConfigurator<B>,
		FocusableConfigurator<Component, B> {

	/**
	 * Sets the Renderer responsible to render the individual items in the list of possible choices.
	 * <p>
	 * It doesn't affect how the selected item is rendered - that can be configured by using
	 * {@link #itemCaptionGenerator(ItemCaptionGenerator)}.
	 * </p>
	 * @param renderer a renderer for the items in the selection list, not <code>null</code>
	 * @return this
	 */
	B renderer(Renderer<ITEM> renderer);

	/**
	 * Set the generator to be used to display item captions (i.e. labels).
	 * @param itemCaptionGenerator The generator to set (not null)
	 * @return this
	 */
	B itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator);

	/**
	 * Set an explicit caption for given item.
	 * <p>
	 * This is an alternative for {@link #itemCaptionGenerator(ItemCaptionGenerator)}. When an
	 * {@link ItemCaptionGenerator} is configured, explicit item captions will be ignored.
	 * </p>
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption (not null)
	 * @return this
	 */
	B itemCaption(ITEM item, Localizable caption);

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption
	 * @return this
	 */
	default B itemCaption(ITEM item, String caption) {
		return itemCaption(item, Localizable.builder().message(caption).build());
	}

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption
	 * @param messageCode Item caption translation code
	 * @return this
	 */
	default B itemCaption(ITEM item, String caption, String messageCode) {
		return itemCaption(item, Localizable.builder().message(caption).messageCode(messageCode).build());
	}

	/**
	 * Sets the page size, which is the number of items fetched at a time from the data provider.
	 * <p>
	 * The page size is also the largest number of items that can support client-side filtering. If you provide more
	 * items than the page size, the component has to fall back to server-side filtering.
	 * <p>
	 * The default page size is 50.
	 * @param pageSize the maximum number of items sent per request, must be greater than zero
	 * @return this
	 */
	B pageSize(int pageSize);

	/**
	 * Set the items data provider using a {@link ListDataProvider}.
	 * <p>
	 * Filtering will use a case insensitive match to show all items where the filter text is a substring of the label
	 * displayed for that item, which you can configure with {@link #itemCaptionGenerator(ItemCaptionGenerator)}.
	 * </p>
	 * @param dataProvider The data provider to set
	 * @return this
	 */
	B dataSource(ListDataProvider<ITEM> dataProvider);

	// specific builders

	/**
	 * {@link SingleSelect} input builder for the <em>select</em> rendering mode.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ItemSelectModeSingleSelectInputBuilder<T, ITEM>
			extends SelectModeSingleSelectInputBuilder<T, ITEM, ItemSelectModeSingleSelectInputBuilder<T, ITEM>>,
			HasBeanDatastoreFilterableDataProviderConfigurator<ITEM, String, DatastoreItemSelectModeSingleSelectInputBuilder<T, ITEM>, ItemSelectModeSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder for the <em>select</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreItemSelectModeSingleSelectInputBuilder<T, ITEM> extends
			SelectModeSingleSelectInputBuilder<T, ITEM, DatastoreItemSelectModeSingleSelectInputBuilder<T, ITEM>>,
			DatastoreDataProviderConfigurator<ITEM, DatastoreItemSelectModeSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>select</em> rendering mode.
	 *
	 * @param <T> Value type
	 */
	public interface PropertySelectModeSingleSelectInputBuilder<T>
			extends SelectModeSingleSelectInputBuilder<T, PropertyBox, PropertySelectModeSingleSelectInputBuilder<T>>,
			HasPropertyBoxDatastoreFilterableDataProviderConfigurator<String, DatastorePropertySelectModeSingleSelectInputBuilder<T>, PropertySelectModeSingleSelectInputBuilder<T>> {

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target and given <code>properties</code> as query projection.
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param filterConverter Data provider filter type to {@link QueryFilter} converter (not null)
		 * @param properties The property set to use as query projection (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		default DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target, Property<?>... properties) {
			return dataSource(datastore, target, PropertySet.of(properties));
		}

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target and given <code>properties</code> as query projection.
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param filterConverter Data provider filter type to {@link QueryFilter} converter (not null)
		 * @param properties The property set to use as query projection (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target, Iterable<P> properties);

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target.
		 * <p>
		 * The query projection property set will be represented by the selection property only.
		 * </p>
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target);

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target.
		 * <p>
		 * The query projection property set will be represented by the selection property only.
		 * </p>
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param filterConverter Data provider filter type to {@link QueryFilter} converter (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target,
				Function<String, QueryFilter> filterConverter);

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>select</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertySelectModeSingleSelectInputBuilder<T> extends
			SelectModeSingleSelectInputBuilder<T, PropertyBox, DatastorePropertySelectModeSingleSelectInputBuilder<T>>,
			DatastoreDataProviderConfigurator<PropertyBox, DatastorePropertySelectModeSingleSelectInputBuilder<T>> {

	}

	// builders

	/**
	 * Create a new {@link ItemSelectModeSingleSelectInputBuilder}.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemSelectModeSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultItemSelectModeSingleSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Create a new {@link ItemSelectModeSingleSelectInputBuilder}.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T> ItemSelectModeSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultItemSelectModeSingleSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Create a new {@link PropertySelectModeSingleSelectInputBuilder} using given selection {@link Property} and
	 * converter.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertySelectModeSingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Create a new {@link PropertySelectModeSingleSelectInputBuilder} using given selection {@link Property}.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertySelectModeSingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
