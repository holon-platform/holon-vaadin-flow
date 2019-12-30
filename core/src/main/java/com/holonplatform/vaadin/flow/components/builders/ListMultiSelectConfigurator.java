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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.ValidatableMultiSelect;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyListMultiSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/**
 * {@link MultiSelect} input builder using a {@link MultiSelectListBox} as input
 * component.
 *
 * @param <T>    Value type
 * @param <ITEM> Item type
 * @param <B>    Concrete builder type
 *
 * @since 5.4.0
 */
public interface ListMultiSelectConfigurator<T, ITEM, B extends ListMultiSelectConfigurator<T, ITEM, B>> extends
		MultiSelectableInputConfigurator<T, ITEM, B>, HasSizeConfigurator<B>, HasItemEnableConfigurator<ITEM, B> {

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

	// ------- specific configurators

	/**
	 * List {@link MultiSelect} input configurator.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 * @param <D>    Datastore data source configurator type
	 * @param <C>    Concrete configurator type
	 */
	public interface ListMultiSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends ListMultiSelectInputConfigurator<T, ITEM, D, C>>
			extends ListMultiSelectConfigurator<T, ITEM, C>, HasBeanDatastoreDataProviderConfigurator<ITEM, D, C> {

	}

	/**
	 * List {@link MultiSelect} input configurator with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type param
	 * @param <C>    Concrete configurator type
	 */
	public interface DatastoreListMultiSelectInputConfigurator<T, ITEM, C extends DatastoreListMultiSelectInputConfigurator<T, ITEM, C>>
			extends ListMultiSelectConfigurator<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based list {@link MultiSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertyListMultiSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertyListMultiSelectInputConfigurator<T, D, C>>
			extends ListMultiSelectConfigurator<T, PropertyBox, C>,
			HasPropertyBoxDatastoreDataProviderConfigurator<D, C>, PropertySelectInputConfigurator<Set<T>, T, C> {

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
	 * {@link Property} model based list {@link MultiSelect} input configurator with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertyListMultiSelectInputConfigurator<T, C extends DatastorePropertyListMultiSelectInputConfigurator<T, C>>
			extends ListMultiSelectConfigurator<T, PropertyBox, C>, DatastoreDataProviderConfigurator<PropertyBox, C>,
			PropertySelectInputConfigurator<Set<T>, T, C> {

	}

	// ------- actual builders

	/**
	 * List {@link MultiSelect} input builder with validation support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableListMultiSelectInputBuilder<T, ITEM> extends
			ListMultiSelectInputConfigurator<T, ITEM, ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM>, ValidatableListMultiSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatableListMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * List {@link MultiSelect} input builder with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> extends
			DatastoreListMultiSelectInputConfigurator<T, ITEM, ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * List {@link MultiSelect} input builder.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface ListMultiSelectInputBuilder<T, ITEM> extends
			ListMultiSelectInputConfigurator<T, ITEM, DatastoreListMultiSelectInputBuilder<T, ITEM>, ListMultiSelectInputBuilder<T, ITEM>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, ListMultiSelectInputBuilder<T, ITEM>, ValidatableListMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * List {@link MultiSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T>    Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreListMultiSelectInputBuilder<T, ITEM>
			extends DatastoreListMultiSelectInputConfigurator<T, ITEM, DatastoreListMultiSelectInputBuilder<T, ITEM>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, DatastoreListMultiSelectInputBuilder<T, ITEM>, ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based list {@link MultiSelect} input builder with
	 * validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertyListMultiSelectInputBuilder<T> extends
			PropertyListMultiSelectInputConfigurator<T, ValidatableDatastorePropertyListMultiSelectInputBuilder<T>, ValidatablePropertyListMultiSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatablePropertyListMultiSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based list {@link MultiSelect} input builder with
	 * validation and {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertyListMultiSelectInputBuilder<T> extends
			DatastorePropertyListMultiSelectInputConfigurator<T, ValidatableDatastorePropertyListMultiSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatableDatastorePropertyListMultiSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based list {@link MultiSelect} input builder.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyListMultiSelectInputBuilder<T> extends
			PropertyListMultiSelectInputConfigurator<T, DatastorePropertyListMultiSelectInputBuilder<T>, PropertyListMultiSelectInputBuilder<T>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, PropertyListMultiSelectInputBuilder<T>, ValidatablePropertyListMultiSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based list {@link MultiSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyListMultiSelectInputBuilder<T> extends
			DatastorePropertyListMultiSelectInputConfigurator<T, DatastorePropertyListMultiSelectInputBuilder<T>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, DatastorePropertyListMultiSelectInputBuilder<T>, ValidatableDatastorePropertyListMultiSelectInputBuilder<T>> {

	}

	// builders

	/**
	 * Get a new {@link ListMultiSelectInputBuilder} to create a {@link MultiSelect}
	 * which uses a {@link MultiSelectListBox} as input component.
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link ListMultiSelectInputBuilder}
	 */
	static <T, ITEM> ListMultiSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultListMultiSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Get a new {@link ListMultiSelectInputBuilder} to create a {@link MultiSelect}
	 * which uses a {@link MultiSelectListBox} as input component.
	 * @param type Selection value type (not null)
	 * @param <T>  Value type
	 * @return A new {@link ListMultiSelectInputBuilder}
	 */
	static <T> ListMultiSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultListMultiSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Get a new {@link PropertyListMultiSelectInputBuilder} to create a
	 * {@link Property} model based {@link MultiSelect}, which uses a
	 * {@link MultiSelectListBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyListMultiSelectInputBuilder}
	 */
	static <T> PropertyListMultiSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyListMultiSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Get a new {@link PropertyListMultiSelectInputBuilder} to create a
	 * {@link Property} model based {@link MultiSelect}, which uses a
	 * {@link MultiSelectListBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyListMultiSelectInputBuilder}
	 */
	static <T> PropertyListMultiSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyListMultiSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
