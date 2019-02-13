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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultItemOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyOptionsModeSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * {@link SingleSelect} input builder for the <em>options</em> rendering mode.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface OptionsModeSingleSelectInputBuilder<T, ITEM, B extends OptionsModeSingleSelectInputBuilder<T, ITEM, B>>
		extends SingleSelectInputConfigurator<T, ITEM, B>, HasLabelConfigurator<B> {

	/**
	 * Sets the {@link ComponentRenderer} responsible to render the individual items in the list of possible choices.
	 * The renderer is applied to each item to create a component which represents the item.
	 * @param renderer the item renderer, not <code>null</code>
	 * @return this
	 */
	B renderer(ComponentRenderer<? extends Component, ITEM> renderer);

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
	 * {@link #itemCaptionGenerator(com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)}.
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
	 * <p>
	 * Filtering will use a case insensitive match to show all items where the filter text is a substring of the label
	 * displayed for that item, which you can configure with
	 * {@link #itemCaptionGenerator(com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)}.
	 * </p>
	 * @param dataProvider The data provider to set
	 * @return this
	 */
	B dataSource(ListDataProvider<ITEM> dataProvider);

	// ------- specific configurators

	/**
	 * {@link SingleSelect} input configurator for the <em>options</em> rendering mode.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface ItemOptionsModeSingleSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends ItemOptionsModeSingleSelectInputConfigurator<T, ITEM, D, C>>
			extends OptionsModeSingleSelectInputBuilder<T, ITEM, C>,
			HasBeanDatastoreDataProviderConfigurator<ITEM, D, C> {

	}

	/**
	 * {@link SingleSelect} input configurator for the <em>options</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastoreItemOptionsModeSingleSelectInputConfigurator<T, ITEM, C extends DatastoreItemOptionsModeSingleSelectInputConfigurator<T, ITEM, C>>
			extends OptionsModeSingleSelectInputBuilder<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input configurator for the <em>options</em> rendering mode.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertyOptionsModeSingleSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertyOptionsModeSingleSelectInputConfigurator<T, D, C>>
			extends OptionsModeSingleSelectInputBuilder<T, PropertyBox, C>,
			HasPropertyBoxDatastoreDataProviderConfigurator<D, C>, PropertySelectInputConfigurator<T, T, C> {

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

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input configurator for the <em>options</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertyOptionsModeSingleSelectInputConfigurator<T, C extends DatastorePropertyOptionsModeSingleSelectInputConfigurator<T, C>>
			extends OptionsModeSingleSelectInputBuilder<T, PropertyBox, C>,
			DatastoreDataProviderConfigurator<PropertyBox, C>, PropertySelectInputConfigurator<T, T, C> {

	}

	// ------- actual builders

	/**
	 * {@link SingleSelect} input builder for the <em>options</em> rendering mode with validation support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableItemOptionsModeSingleSelectInputBuilder<T, ITEM> extends
			ItemOptionsModeSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>, ValidatableItemOptionsModeSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableItemOptionsModeSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder for the <em>options</em> rendering mode with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM> extends
			DatastoreItemOptionsModeSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder for the <em>options</em> rendering mode.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ItemOptionsModeSingleSelectInputBuilder<T, ITEM> extends
			ItemOptionsModeSingleSelectInputConfigurator<T, ITEM, DatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>, ItemOptionsModeSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, ItemOptionsModeSingleSelectInputBuilder<T, ITEM>, ValidatableItemOptionsModeSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder for the <em>options</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM> extends
			DatastoreItemOptionsModeSingleSelectInputConfigurator<T, ITEM, DatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>, ValidatableDatastoreItemOptionsModeSingleSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>options</em> rendering mode with
	 * validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertyOptionsModeSingleSelectInputBuilder<T> extends
			PropertyOptionsModeSingleSelectInputConfigurator<T, ValidatableDatastorePropertyOptionsModeSingleSelectInputBuilder<T>, ValidatablePropertyOptionsModeSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatablePropertyOptionsModeSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>options</em> rendering mode with
	 * validation and {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertyOptionsModeSingleSelectInputBuilder<T> extends
			DatastorePropertyOptionsModeSingleSelectInputConfigurator<T, ValidatableDatastorePropertyOptionsModeSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastorePropertyOptionsModeSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>options</em> rendering mode.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyOptionsModeSingleSelectInputBuilder<T> extends
			PropertyOptionsModeSingleSelectInputConfigurator<T, DatastorePropertyOptionsModeSingleSelectInputBuilder<T>, PropertyOptionsModeSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, PropertyOptionsModeSingleSelectInputBuilder<T>, ValidatablePropertyOptionsModeSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>options</em> rendering mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyOptionsModeSingleSelectInputBuilder<T> extends
			DatastorePropertyOptionsModeSingleSelectInputConfigurator<T, DatastorePropertyOptionsModeSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastorePropertyOptionsModeSingleSelectInputBuilder<T>, ValidatableDatastorePropertyOptionsModeSingleSelectInputBuilder<T>> {

	}

	// ------- builders

	/**
	 * Create a new {@link ItemOptionsModeSingleSelectInputBuilder}.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemOptionsModeSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultItemOptionsModeSingleSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Create a new {@link ItemOptionsModeSingleSelectInputBuilder}.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <T> ItemOptionsModeSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultItemOptionsModeSingleSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Create a new {@link PropertyOptionsModeSingleSelectInputBuilder} using given selection {@link Property} and
	 * converter.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyOptionsModeSingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Create a new {@link PropertyOptionsModeSingleSelectInputBuilder} using given selection {@link Property}.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyOptionsModeSingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
