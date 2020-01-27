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
import com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator.DatastoreDataProviderSelectConfigurator;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultOptionsMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyOptionsMultiSelectInputBuilder;
import com.vaadin.flow.component.checkbox.CheckboxGroup;

/**
 * {@link MultiSelect} input builder using a {@link CheckboxGroup} as input component.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface OptionsMultiSelectConfigurator<T, ITEM, B extends OptionsMultiSelectConfigurator<T, ITEM, B>> extends
		MultiSelectableInputConfigurator<T, ITEM, B>, HasLabelConfigurator<B>, HasItemEnableConfigurator<ITEM, B> {

	// ------- specific configurators

	/**
	 * Options {@link MultiSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface OptionsMultiSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends OptionsMultiSelectInputConfigurator<T, ITEM, D, C>>
			extends OptionsMultiSelectConfigurator<T, ITEM, C>, HasBeanDatastoreDataProviderConfigurator<ITEM, D, C> {

	}

	/**
	 * Options {@link MultiSelect} input configurator with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type param
	 * @param <C> Concrete configurator type
	 */
	public interface DatastoreOptionsMultiSelectInputConfigurator<T, ITEM, C extends DatastoreOptionsMultiSelectInputConfigurator<T, ITEM, C>>
			extends OptionsMultiSelectConfigurator<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based options {@link MultiSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertyOptionsMultiSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertyOptionsMultiSelectInputConfigurator<T, D, C>>
			extends OptionsMultiSelectConfigurator<T, PropertyBox, C>,
			HasPropertyBoxDatastoreDataProviderConfigurator<D, C>, PropertySelectInputConfigurator<Set<T>, T, C> {

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
	 * {@link Property} model based options {@link MultiSelect} input configurator with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertyOptionsMultiSelectInputConfigurator<T, C extends DatastorePropertyOptionsMultiSelectInputConfigurator<T, C>>
			extends OptionsMultiSelectConfigurator<T, PropertyBox, C>,
			DatastoreDataProviderSelectConfigurator<PropertyBox, C>, PropertySelectInputConfigurator<Set<T>, T, C> {

	}

	// ------- actual builders

	/**
	 * Options {@link MultiSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableOptionsMultiSelectInputBuilder<T, ITEM> extends
			OptionsMultiSelectInputConfigurator<T, ITEM, ValidatableDatastoreOptionsMultiSelectInputBuilder<T, ITEM>, ValidatableOptionsMultiSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatableOptionsMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Options {@link MultiSelect} input builder with validation and {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreOptionsMultiSelectInputBuilder<T, ITEM> extends
			DatastoreOptionsMultiSelectInputConfigurator<T, ITEM, ValidatableDatastoreOptionsMultiSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatableDatastoreOptionsMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Options {@link MultiSelect} input builder.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface OptionsMultiSelectInputBuilder<T, ITEM> extends
			OptionsMultiSelectInputConfigurator<T, ITEM, DatastoreOptionsMultiSelectInputBuilder<T, ITEM>, OptionsMultiSelectInputBuilder<T, ITEM>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, OptionsMultiSelectInputBuilder<T, ITEM>, ValidatableOptionsMultiSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Options {@link MultiSelect} input builder with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreOptionsMultiSelectInputBuilder<T, ITEM> extends
			DatastoreOptionsMultiSelectInputConfigurator<T, ITEM, DatastoreOptionsMultiSelectInputBuilder<T, ITEM>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, DatastoreOptionsMultiSelectInputBuilder<T, ITEM>, ValidatableDatastoreOptionsMultiSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based options {@link MultiSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertyOptionsMultiSelectInputBuilder<T> extends
			PropertyOptionsMultiSelectInputConfigurator<T, ValidatableDatastorePropertyOptionsMultiSelectInputBuilder<T>, ValidatablePropertyOptionsMultiSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatablePropertyOptionsMultiSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based options {@link MultiSelect} input builder with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertyOptionsMultiSelectInputBuilder<T> extends
			DatastorePropertyOptionsMultiSelectInputConfigurator<T, ValidatableDatastorePropertyOptionsMultiSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<Set<T>, ValidatableMultiSelect<T>, ValidatableDatastorePropertyOptionsMultiSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based options {@link MultiSelect} input builder.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyOptionsMultiSelectInputBuilder<T> extends
			PropertyOptionsMultiSelectInputConfigurator<T, DatastorePropertyOptionsMultiSelectInputBuilder<T>, PropertyOptionsMultiSelectInputBuilder<T>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, PropertyOptionsMultiSelectInputBuilder<T>, ValidatablePropertyOptionsMultiSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based options {@link MultiSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyOptionsMultiSelectInputBuilder<T> extends
			DatastorePropertyOptionsMultiSelectInputConfigurator<T, DatastorePropertyOptionsMultiSelectInputBuilder<T>>,
			InputBuilder<Set<T>, ValueChangeEvent<Set<T>>, MultiSelect<T>, ValidatableMultiSelect<T>, DatastorePropertyOptionsMultiSelectInputBuilder<T>, ValidatableDatastorePropertyOptionsMultiSelectInputBuilder<T>> {

	}

	// builders

	/**
	 * Get a new {@link OptionsMultiSelectInputBuilder} to create a {@link MultiSelect} which uses a
	 * {@link CheckboxGroup} as input component.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link OptionsMultiSelectInputBuilder}
	 */
	static <T, ITEM> OptionsMultiSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultOptionsMultiSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Get a new {@link OptionsMultiSelectInputBuilder} to create a {@link MultiSelect} which uses a
	 * {@link CheckboxGroup} as input component.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link OptionsMultiSelectInputBuilder}
	 */
	static <T> OptionsMultiSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultOptionsMultiSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Get a new {@link PropertyOptionsMultiSelectInputBuilder} to create a {@link Property} model based
	 * {@link MultiSelect}, which uses a {@link CheckboxGroup} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyOptionsMultiSelectInputBuilder}
	 */
	static <T> PropertyOptionsMultiSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyOptionsMultiSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Get a new {@link PropertyOptionsMultiSelectInputBuilder} to create a {@link Property} model based
	 * {@link MultiSelect}, which uses a {@link CheckboxGroup} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyOptionsMultiSelectInputBuilder}
	 */
	static <T> PropertyOptionsMultiSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyOptionsMultiSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
