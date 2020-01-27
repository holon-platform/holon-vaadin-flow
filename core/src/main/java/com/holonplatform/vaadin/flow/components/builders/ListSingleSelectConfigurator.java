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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator.DatastoreDataProviderSelectConfigurator;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyListSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/**
 * {@link SingleSelect} input builder using a {@link ListBox} as input
 * component.
 *
 * @param <T>    Value type
 * @param <ITEM> Item type
 * @param <B>    Concrete builder type
 *
 * @since 5.2.3
 */
public interface ListSingleSelectConfigurator<T, ITEM, B extends ListSingleSelectConfigurator<T, ITEM, B>> extends
		SingleSelectableInputConfigurator<T, ITEM, B>, HasSizeConfigurator<B>, HasItemEnableConfigurator<ITEM, B> {

	/**
	 * Sets the {@link ComponentRenderer} responsible to render the individual items
	 * in the list of possible choices. The renderer is applied to each item to
	 * create a component which represents the item.
	 * @param renderer the item renderer, not <code>null</code>
	 * @return this
	 */
	B renderer(ComponentRenderer<? extends Component, ITEM> renderer);

	/**
	 * Adds the given component before the list items.
	 * @param component The component to add (not null)
	 * @return this
	 */
	B withPrefixComponent(Component component);

	/**
	 * Adds the given component before the given list item.
	 * @param beforeItem The item before which to add the component (not null)
	 * @param component  The component to add (not null)
	 * @return this
	 */
	B withComponentBefore(ITEM beforeItem, Component component);

	/**
	 * Adds the given component after the given list item.
	 * @param afterItem The item after which to add the component (not null)
	 * @param component The component to add (not null)
	 * @return this
	 */
	B withComponentAfter(ITEM afterItem, Component component);

	/**
	 * Set whether to fail throwing an {@link IllegalArgumentException} when a value
	 * not present in the item set is setted.
	 * <p>
	 * Default is <code>false</code>, meaning that when a value not present in the
	 * item set is setted, the <code>null</code> value is used
	 * </p>
	 * @param failWhenItemNotPresent Set whether to fail when a value not present in
	 *                               the item set is setted
	 * @return this
	 */
	B failWhenItemNotPresent(boolean failWhenItemNotPresent);

	// ------- specific configurators

	/**
	 * {@link SingleSelect} input configurator.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 * @param <D>    Datastore data source configurator type
	 * @param <C>    Concrete configurator type
	 */
	public interface ListSingleSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends ListSingleSelectInputConfigurator<T, ITEM, D, C>>
			extends ListSingleSelectConfigurator<T, ITEM, C>, HasBeanDatastoreDataProviderConfigurator<ITEM, D, C> {

	}

	/**
	 * {@link SingleSelect} input configurator with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 * @param <C>    Concrete configurator type
	 */
	public interface DatastoreListSingleSelectInputConfigurator<T, ITEM, C extends DatastoreListSingleSelectInputConfigurator<T, ITEM, C>>
			extends ListSingleSelectConfigurator<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertyListSingleSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertyListSingleSelectInputConfigurator<T, D, C>>
			extends ListSingleSelectConfigurator<T, PropertyBox, C>,
			HasPropertyBoxDatastoreDataProviderConfigurator<D, C>, PropertySelectInputConfigurator<T, T, C> {

		/**
		 * Set the data provider which acts as items data source, using given
		 * {@link Datastore} as backend data handler, given {@link DataTarget} as query
		 * target.
		 * <p>
		 * The query projection property set will be represented by the selection
		 * property only.
		 * </p>
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target    The {@link DataTarget} to use as query target (not null)
		 * @return An extended builder which allow further data provider configuration,
		 *         for example to add fixed {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		D dataSource(Datastore datastore, DataTarget<?> target);

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input configurator mode
	 * with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertyListSingleSelectInputConfigurator<T, C extends DatastorePropertyListSingleSelectInputConfigurator<T, C>>
			extends ListSingleSelectConfigurator<T, PropertyBox, C>, DatastoreDataProviderSelectConfigurator<PropertyBox, C>,
			PropertySelectInputConfigurator<T, T, C> {

	}

	// ------- actual builders

	/**
	 * {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableListSingleSelectInputBuilder<T, ITEM> extends
			ListSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM>, ValidatableListSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableListSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> extends
			DatastoreListSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface ListSingleSelectInputBuilder<T, ITEM> extends
			ListSingleSelectInputConfigurator<T, ITEM, DatastoreListSingleSelectInputBuilder<T, ITEM>, ListSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, ListSingleSelectInputBuilder<T, ITEM>, ValidatableListSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreListSingleSelectInputBuilder<T, ITEM>
			extends DatastoreListSingleSelectInputConfigurator<T, ITEM, DatastoreListSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastoreListSingleSelectInputBuilder<T, ITEM>, ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based {@link SingleSelect} input builder with
	 * validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertyListSingleSelectInputBuilder<T> extends
			PropertyListSingleSelectInputConfigurator<T, ValidatableDatastorePropertyListSingleSelectInputBuilder<T>, ValidatablePropertyListSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatablePropertyListSingleSelectInputBuilder<T>> {
	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder with
	 * validation and {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertyListSingleSelectInputBuilder<T> extends
			DatastorePropertyListSingleSelectInputConfigurator<T, ValidatableDatastorePropertyListSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastorePropertyListSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyListSingleSelectInputBuilder<T> extends
			PropertyListSingleSelectInputConfigurator<T, DatastorePropertyListSingleSelectInputBuilder<T>, PropertyListSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, PropertyListSingleSelectInputBuilder<T>, ValidatablePropertyListSingleSelectInputBuilder<T>> {
	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyListSingleSelectInputBuilder<T> extends
			DatastorePropertyListSingleSelectInputConfigurator<T, DatastorePropertyListSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastorePropertyListSingleSelectInputBuilder<T>, ValidatableDatastorePropertyListSingleSelectInputBuilder<T>> {

	}

	// ------ builders

	/**
	 * Get a new {@link ListSingleSelectInputBuilder} to create a
	 * {@link SingleSelect} which uses a {@link ListBox} as input component.
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link ListSingleSelectInputBuilder}
	 */
	static <T, ITEM> ListSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultListSingleSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Get a new {@link ListSingleSelectInputBuilder} to create a
	 * {@link SingleSelect} which uses a {@link ListBox} as input component.
	 * @param type Selection value type (not null)
	 * @param <T>  Value type
	 * @return A new {@link ListSingleSelectInputBuilder}
	 */
	static <T> ListSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultListSingleSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Get a new {@link PropertyListSingleSelectInputBuilder} to create a
	 * {@link Property} model based filterable {@link SingleSelect}, which uses a
	 * {@link ListBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyListSingleSelectInputBuilder}
	 */
	static <T> PropertyListSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyListSingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Get a new {@link PropertyListSingleSelectInputBuilder} to create a
	 * {@link Property} model based filterable {@link SingleSelect}, which uses a
	 * {@link ListBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyListSingleSelectInputBuilder}
	 */
	static <T> PropertyListSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyListSingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
