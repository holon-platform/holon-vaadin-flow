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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultOptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertyOptionsSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/**
 * {@link SingleSelect} input builder using a {@link RadioButtonGroup} as input component.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface OptionsSingleSelectConfigurator<T, ITEM, B extends OptionsSingleSelectConfigurator<T, ITEM, B>> extends
		SingleSelectableInputConfigurator<T, ITEM, B>, HasLabelConfigurator<B>, HasItemEnableConfigurator<ITEM, B> {

	/**
	 * Sets the {@link ComponentRenderer} responsible to render the individual items in the list of possible choices.
	 * The renderer is applied to each item to create a component which represents the item.
	 * @param renderer the item renderer, not <code>null</code>
	 * @return this
	 */
	B renderer(ComponentRenderer<? extends Component, ITEM> renderer);

	// ------- specific configurators

	/**
	 * Options {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface OptionsSingleSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends OptionsSingleSelectInputConfigurator<T, ITEM, D, C>>
			extends OptionsSingleSelectConfigurator<T, ITEM, C>, HasBeanDatastoreDataProviderConfigurator<ITEM, D, C> {

	}

	/**
	 * Options {@link SingleSelect} input configurator with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastoreOptionsSingleSelectInputConfigurator<T, ITEM, C extends DatastoreOptionsSingleSelectInputConfigurator<T, ITEM, C>>
			extends OptionsSingleSelectConfigurator<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based options {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertyOptionsSingleSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertyOptionsSingleSelectInputConfigurator<T, D, C>>
			extends OptionsSingleSelectConfigurator<T, PropertyBox, C>,
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
	 * {@link Property} model based options {@link SingleSelect} input configurator with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertyOptionsSingleSelectInputConfigurator<T, C extends DatastorePropertyOptionsSingleSelectInputConfigurator<T, C>>
			extends OptionsSingleSelectConfigurator<T, PropertyBox, C>,
			DatastoreDataProviderSelectConfigurator<PropertyBox, C>, PropertySelectInputConfigurator<T, T, C> {

	}

	// ------- actual builders

	/**
	 * Options {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableOptionsSingleSelectInputBuilder<T, ITEM> extends
			OptionsSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreOptionsSingleSelectInputBuilder<T, ITEM>, ValidatableOptionsSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableOptionsSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Options {@link SingleSelect} input builder with validation and {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreOptionsSingleSelectInputBuilder<T, ITEM> extends
			DatastoreOptionsSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreOptionsSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastoreOptionsSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Options {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface OptionsSingleSelectInputBuilder<T, ITEM> extends
			OptionsSingleSelectInputConfigurator<T, ITEM, DatastoreOptionsSingleSelectInputBuilder<T, ITEM>, OptionsSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, OptionsSingleSelectInputBuilder<T, ITEM>, ValidatableOptionsSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * Options {@link SingleSelect} input builder with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreOptionsSingleSelectInputBuilder<T, ITEM> extends
			DatastoreOptionsSingleSelectInputConfigurator<T, ITEM, DatastoreOptionsSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastoreOptionsSingleSelectInputBuilder<T, ITEM>, ValidatableDatastoreOptionsSingleSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based options {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertyOptionsSingleSelectInputBuilder<T> extends
			PropertyOptionsSingleSelectInputConfigurator<T, ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T>, ValidatablePropertyOptionsSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatablePropertyOptionsSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based options {@link SingleSelect} input builder with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> extends
			DatastorePropertyOptionsSingleSelectInputConfigurator<T, ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based options {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 */
	public interface PropertyOptionsSingleSelectInputBuilder<T> extends
			PropertyOptionsSingleSelectInputConfigurator<T, DatastorePropertyOptionsSingleSelectInputBuilder<T>, PropertyOptionsSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, PropertyOptionsSingleSelectInputBuilder<T>, ValidatablePropertyOptionsSingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based options {@link SingleSelect} input builder with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertyOptionsSingleSelectInputBuilder<T> extends
			DatastorePropertyOptionsSingleSelectInputConfigurator<T, DatastorePropertyOptionsSingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastorePropertyOptionsSingleSelectInputBuilder<T>, ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T>> {

	}

	// ------- builders

	/**
	 * Get a new {@link OptionsSingleSelectInputBuilder} to create a {@link SingleSelect} which uses a
	 * {@link RadioButtonGroup} as input component.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link OptionsSingleSelectInputBuilder}
	 */
	static <T, ITEM> OptionsSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultOptionsSingleSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Get a new {@link OptionsSingleSelectInputBuilder} to create a {@link SingleSelect} which uses a
	 * {@link RadioButtonGroup} as input component.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link OptionsSingleSelectInputBuilder}
	 */
	static <T> OptionsSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultOptionsSingleSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Get a new {@link PropertyOptionsSingleSelectInputBuilder} to create a {@link Property} model based
	 * {@link SingleSelect}, which uses a {@link RadioButtonGroup} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyOptionsSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertyOptionsSingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Get a new {@link PropertyOptionsSingleSelectInputBuilder} to create a {@link Property} model based
	 * {@link SingleSelect}, which uses a {@link RadioButtonGroup} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyOptionsSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertyOptionsSingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
