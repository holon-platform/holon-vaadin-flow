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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.events.CustomValueSetListener;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyFilterableSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.Renderer;

/**
 * Filterable {@link SingleSelect} input builder using a {@link ComboBox} as input component.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface FilterableSingleSelectConfigurator<T, ITEM, B extends FilterableSingleSelectConfigurator<T, ITEM, B>>
		extends SingleSelectableInputConfigurator<T, ITEM, B>, HasSizeConfigurator<B>, HasLabelConfigurator<B>,
		HasPlaceholderConfigurator<B>, HasPatternConfigurator<B>, HasAutofocusConfigurator<B>,
		FocusableConfigurator<Component, B> {

	/**
	 * Sets the {@link Renderer} responsible to render the individual items in the list of possible choices.
	 * <p>
	 * It doesn't affect how the selected item is rendered - that can be configured by using
	 * {@link #itemCaptionGenerator(com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)}.
	 * </p>
	 * @param renderer a renderer for the items in the selection list, not <code>null</code>
	 * @return this
	 */
	B renderer(Renderer<ITEM> renderer);

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
	 * Set whether custom values are allowed. If <code>true</code>, the user can input a value that is not present in
	 * the items list.
	 * @param allowCustomValue Whether custom values are allowed
	 * @return this
	 * @since 5.2.13
	 */
	B allowCustomValue(boolean allowCustomValue);

	/**
	 * Add a listener for custom value set event, which is fired when user types in a value that don't already exist in
	 * the select items set.
	 * <p>
	 * As a side effect makes the select allow custom values. If you don't want to allow a user to add new values to the
	 * list once the listener is added please disable it explicitly via the {@link #allowCustomValue(boolean)} method.
	 * </p>
	 * @param customValueSetListener The listener to add (not null)
	 * @return this
	 * @since 5.2.13
	 */
	B withCustomValueSetListener(CustomValueSetListener<T> customValueSetListener);

	// ------- specific configurators

	/**
	 * Filterable {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface FilterableSingleSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends FilterableSingleSelectInputConfigurator<T, ITEM, D, C>>
			extends FilterableSingleSelectConfigurator<T, ITEM, C>,
			HasBeanDatastoreFilterableDataProviderConfigurator<ITEM, String, D, C> {

	}

	/**
	 * Filterable {@link SingleSelect} input configurator with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastoreFilterableSingleSelectInputConfigurator<T, ITEM, C extends DatastoreFilterableSingleSelectInputConfigurator<T, ITEM, C>>
			extends FilterableSingleSelectConfigurator<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based filterable {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertyFilterableSingleSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertyFilterableSingleSelectInputConfigurator<T, D, C>>
			extends FilterableSingleSelectConfigurator<T, PropertyBox, C>,
			HasPropertyBoxDatastoreFilterableDataProviderConfigurator<String, D, C>,
			PropertySelectInputConfigurator<T, T, C> {

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target and given <code>properties</code> as query projection.
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param properties The property set to use as query projection (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		default D dataSource(Datastore datastore, DataTarget<?> target, Property<?>... properties) {
			return dataSource(datastore, target, PropertySet.of(properties));
		}

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target and given <code>properties</code> as query projection.
		 * @param <P> Property type
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @param properties The property set to use as query projection (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> D dataSource(Datastore datastore, DataTarget<?> target, Iterable<P> properties);

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
		D dataSource(Datastore datastore, DataTarget<?> target);

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
		D dataSource(Datastore datastore, DataTarget<?> target, Function<String, QueryFilter> filterConverter);

	}

	/**
	 * {@link Property} model based filterable {@link SingleSelect} input configurator with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertyFilterableSingleSelectInputConfigurator<T, C extends DatastorePropertyFilterableSingleSelectInputConfigurator<T, C>>
			extends FilterableSingleSelectConfigurator<T, PropertyBox, C>,
			DatastoreDataProviderConfigurator<PropertyBox, C>, PropertySelectInputConfigurator<T, T, C> {

		/**
		 * Set the function to use to convert the {@link String} type user filter into a {@link QueryFilter} type, to be
		 * used with a Datastore query. The {@link String} type user filter represents the user text input which can be
		 * used to filter the select suggestions within the available item set.
		 * @param filterConverter The {@link String} type user filter to {@link QueryFilter} converter (not null)
		 * @return this
		 */
		C filterConverter(Function<String, QueryFilter> filterConverter);

		/**
		 * Set to use given property to convert the {@link String} type user filter into a {@link QueryFilter},
		 * providing a filter to check if the property value <em>contains</em> the user filter text, ignoring case.
		 * @param property The property to use for the query filter (not null)
		 * @return this
		 * @see #filterConverter(Function)
		 */
		default C filterByContains(Property<String> property) {
			return filterByContains(property, true);
		}

		/**
		 * Set to use given property to convert the {@link String} type user filter into a {@link QueryFilter},
		 * providing a filter to check if the property value <em>contains</em> the user filter text.
		 * @param property The property to use for the query filter (not null)
		 * @param ignoreCase Whether to ignore case
		 * @return this
		 * @see #filterConverter(Function)
		 */
		default C filterByContains(Property<String> property, boolean ignoreCase) {
			return filterConverter(text -> QueryFilter.contains(property, text, ignoreCase));
		}

		/**
		 * Set to use given property to convert the {@link String} type user filter into a {@link QueryFilter},
		 * providing a filter to check if the property value <em>starts with</em> the user filter text, ignoring case.
		 * @param property The property to use for the query filter (not null)
		 * @return this
		 * @see #filterConverter(Function)
		 */
		default C filterByStarts(Property<String> property) {
			return filterByStarts(property, true);
		}

		/**
		 * Set to use given property to convert the {@link String} type user filter into a {@link QueryFilter},
		 * providing a filter to check if the property value <em>starts with</em> the user filter text.
		 * @param property The property to use for the query filter (not null)
		 * @param ignoreCase Whether to ignore case
		 * @return this
		 * @see #filterConverter(Function)
		 */
		default C filterByStarts(Property<String> property, boolean ignoreCase) {
			return filterConverter(text -> QueryFilter.startsWith(property, text, ignoreCase));
		}

	}

	// ------- actual builders

	/**
	 * Filterable {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableFilterableSingleSelectInputBuilder<T, ITEM> extends
			FilterableSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreFilterableSingleSelectInputBuilder<T, ITEM>, ValidatableFilterableSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableFilterableSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Filterable {@link SingleSelect} input builder with validation and {@link DatastoreDataProviderConfigurator}
	 * support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreFilterableSingleSelectInputBuilder<T, ITEM> extends
			DatastoreFilterableSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreFilterableSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastoreFilterableSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Filterable {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface FilterableSingleSelectInputBuilder<T, ITEM> extends
			FilterableSingleSelectInputConfigurator<T, ITEM, DatastoreFilterableSingleSelectInputBuilder<T, ITEM>, FilterableSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, FilterableSingleSelectInputBuilder<T, ITEM>, ValidatableFilterableSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Filterable {@link SingleSelect} input builder with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreFilterableSingleSelectInputBuilder<T, ITEM> extends
			DatastoreFilterableSingleSelectInputConfigurator<T, ITEM, DatastoreFilterableSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastoreFilterableSingleSelectInputBuilder<T, ITEM>, ValidatableDatastoreFilterableSingleSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based filterable {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertyFilterableSingleSelectInputBuilder<T> extends
			PropertyFilterableSingleSelectInputConfigurator<T, ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T>, ValidatablePropertyFilterableSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatablePropertyFilterableSingleSelectInputBuilder<T>> {
	}

	/**
	 * {@link Property} model based filterable {@link SingleSelect} input builder with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> extends
			DatastorePropertyFilterableSingleSelectInputConfigurator<T, ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based filterable {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyFilterableSingleSelectInputBuilder<T> extends
			PropertyFilterableSingleSelectInputConfigurator<T, DatastorePropertyFilterableSingleSelectInputBuilder<T>, PropertyFilterableSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, PropertyFilterableSingleSelectInputBuilder<T>, ValidatablePropertyFilterableSingleSelectInputBuilder<T>> {
	}

	/**
	 * {@link Property} model based filterable {@link SingleSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyFilterableSingleSelectInputBuilder<T> extends
			DatastorePropertyFilterableSingleSelectInputConfigurator<T, DatastorePropertyFilterableSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastorePropertyFilterableSingleSelectInputBuilder<T>, ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T>> {

	}

	// ------ builders

	/**
	 * Get a new {@link FilterableSingleSelectInputBuilder} to create a filterable {@link SingleSelect}, which uses a
	 * {@link ComboBox} as input component.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link FilterableSingleSelectInputBuilder}
	 */
	static <T, ITEM> FilterableSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultFilterableSingleSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Get a new {@link FilterableSingleSelectInputBuilder} to create a filterable {@link SingleSelect}, which uses a
	 * {@link ComboBox} as input component.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link FilterableSingleSelectInputBuilder}
	 */
	static <T> FilterableSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultFilterableSingleSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Get a new {@link PropertyFilterableSingleSelectInputBuilder} to create a {@link Property} model based filterable
	 * {@link SingleSelect}, which uses a {@link ComboBox} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
	 */
	static <T> PropertyFilterableSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyFilterableSingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Get a new {@link PropertyFilterableSingleSelectInputBuilder} to create a {@link Property} model based filterable
	 * {@link SingleSelect}, which uses a {@link ComboBox} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
	 */
	static <T> PropertyFilterableSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyFilterableSingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
