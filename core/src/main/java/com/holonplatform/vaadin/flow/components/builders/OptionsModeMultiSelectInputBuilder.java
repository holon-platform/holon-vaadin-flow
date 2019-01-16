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
import java.util.Set;
import java.util.function.Function;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultItemOptionsModeMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyOptionsModeMultiSelectInputBuilder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * {@link MultiSelect} input builder for the <em>options</em> rendering mode.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface OptionsModeMultiSelectInputBuilder<T, ITEM, B extends OptionsModeMultiSelectInputBuilder<T, ITEM, B>>
		extends MultiSelectInputBuilder<T, ITEM, B>, HasLabelConfigurator<B> {

	/**
	 * Sets the item enabled state predicate. The predicate is applied to each item to determine whether the item should
	 * be enabled (<code>true</code>) or disabled (<code>false</code>).
	 * @param itemEnabledProvider the item enable predicate, not <code>null</code>
	 * @return this
	 */
	B itemEnabledProvider(SerializablePredicate<ITEM> itemEnabledProvider);

	/**
	 * Set the generator to be used to display item captions (i.e. labels).
	 * @param itemCaptionGenerator The generator to set (not null)
	 * @return this
	 */
	B itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator);

	/**
	 * Set an explicit caption for given item.
	 * <p>
	 * This is an alternative for
	 * {@link #itemCaptionGenerator(com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)}
	 * When an {@link ItemCaptionGenerator} is configured, explicit item captions will be ignored.
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
	 * Set the items data provider using a {@link ListDataProvider}.
	 * @param dataProvider The data provider to set
	 * @return this
	 */
	B dataSource(ListDataProvider<ITEM> dataProvider);

	// specific builders

	/**
	 * {@link MultiSelect} input builder for the <em>options</em> rendering mode.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ItemOptionsModeMultiSelectInputBuilder<T, ITEM>
			extends OptionsModeMultiSelectInputBuilder<T, ITEM, ItemOptionsModeMultiSelectInputBuilder<T, ITEM>>,
			HasBeanDatastoreDataProviderConfigurator<ITEM, DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM>, ItemOptionsModeMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link MultiSelect} input builder for the <em>options</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> extends
			OptionsModeMultiSelectInputBuilder<T, ITEM, DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM>>,
			DatastoreDataProviderConfigurator<ITEM, DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link Property} model based {@link MultiSelect} input builder for the <em>options</em> rendering mode.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyOptionsModeMultiSelectInputBuilder<T>
			extends OptionsModeMultiSelectInputBuilder<T, PropertyBox, PropertyOptionsModeMultiSelectInputBuilder<T>>,
			HasPropertyBoxDatastoreDataProviderConfigurator<DatastorePropertyOptionsModeMultiSelectInputBuilder<T>, PropertyOptionsModeMultiSelectInputBuilder<T>>,
			PropertySelectInputConfigurator<Set<T>, T, PropertyOptionsModeMultiSelectInputBuilder<T>> {

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
		DatastorePropertyOptionsModeMultiSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target);

	}

	/**
	 * {@link Property} model based {@link MultiSelect} input builder for the <em>options</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyOptionsModeMultiSelectInputBuilder<T> extends
			OptionsModeMultiSelectInputBuilder<T, PropertyBox, DatastorePropertyOptionsModeMultiSelectInputBuilder<T>>,
			DatastoreDataProviderConfigurator<PropertyBox, DatastorePropertyOptionsModeMultiSelectInputBuilder<T>>,
			PropertySelectInputConfigurator<Set<T>, T, DatastorePropertyOptionsModeMultiSelectInputBuilder<T>> {

	}

	// builders

	/**
	 * Create a new {@link ItemOptionsModeMultiSelectInputBuilder}.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link ItemOptionsModeMultiSelectInputBuilder}
	 */
	static <T, ITEM> ItemOptionsModeMultiSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultItemOptionsModeMultiSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Create a new {@link ItemOptionsModeMultiSelectInputBuilder}.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link ItemOptionsModeMultiSelectInputBuilder}
	 */
	static <T> ItemOptionsModeMultiSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultItemOptionsModeMultiSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Create a new {@link PropertyOptionsModeMultiSelectInputBuilder} using given selection {@link Property} and
	 * converter.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyOptionsModeMultiSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeMultiSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyOptionsModeMultiSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Create a new {@link PropertyOptionsModeMultiSelectInputBuilder} using given selection {@link Property}.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyOptionsModeMultiSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeMultiSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyOptionsModeMultiSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
