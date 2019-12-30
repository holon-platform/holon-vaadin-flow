/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.FilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.PropertyFilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator.ListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator.PropertyListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.OptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.PropertyOptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator.PropertySingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator.SingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;

/**
 * A {@link Selectable} component in which at most one item can be selected at a
 * time.
 * 
 * @param <T> Selection item type
 * 
 * @since 5.0.0
 */
public interface SingleSelect<T> extends Selectable<T>, Input<T>, ItemSet {

	/**
	 * Get the currently selected item.
	 * @return The currently selected item, empty if no item is selected
	 */
	Optional<T> getSelectedItem();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	default SelectionMode getSelectionMode() {
		return SelectionMode.SINGLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@Override
	default Set<T> getSelectedItems() {
		return getSelectedItem().map(Collections::singleton).orElse(Collections.emptySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	default Optional<T> getFirstSelectedItem() {
		return getSelectedItem();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAll()
	 */
	@Override
	default void deselectAll() {
		getSelectedItem().ifPresent(this::deselect);
	}

	// ------- builders

	/**
	 * Gets a builder to create a <em>filterable</em> {@link SingleSelect}, which
	 * uses a {@link ComboBox} as input component.
	 * <p>
	 * Alias for {@link #filterable(Class)}.
	 * </p>
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #create(Class, Class, ItemConverter)}
	 * if not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link FilterableSingleSelectInputBuilder}
	 */
	static <T> FilterableSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return filterable(type);
	}

	/**
	 * Gets a builder to create a <em>filterable</em> {@link SingleSelect}, which
	 * uses a {@link ComboBox} as input component.
	 * <p>
	 * Alias for {@link #filterable(Class, Class, ItemConverter)}.
	 * </p>
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are not consistent (i.e. of different type). When the the
	 * selection item and the selection value types are consistent, the
	 * {@link #create(Class)} method can be used.
	 * <p>
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item
	 *                      into a selection (Input) value and back (not null)
	 * @return A new {@link FilterableSingleSelectInputBuilder}
	 */
	static <T, ITEM> FilterableSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return filterable(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>filterable</em>
	 * {@link SingleSelect}, which uses a {@link ComboBox} as input component.
	 * <p>
	 * Alias for {@link #filterable(Property)}.
	 * </p>
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
	 */
	static <T> PropertyFilterableSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return filterable(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>filterable</em>
	 * {@link SingleSelect}, which uses a {@link ComboBox} as input component.
	 * <p>
	 * Alias for {@link #filterable(Property, Function)}.
	 * </p>
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
	 */
	static <T> PropertyFilterableSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return filterable(selectionProperty, itemConverter);
	}

	// ------- simple select

	/**
	 * Gets a builder to create a {@link SingleSelect}, which uses a {@link Select}
	 * as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #select(Class, Class, ItemConverter)}
	 * if not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link SingleSelectInputBuilder}
	 */
	static <T> SingleSelectInputBuilder<T, T> select(Class<T> type) {
		return SingleSelectConfigurator.create(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}, which uses a {@link Select}
	 * as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are not consistent (i.e. of different type). When the the
	 * selection item and the selection value types are consistent, the
	 * {@link #select(Class)} method can be used.
	 * <p>
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item
	 *                      into a selection (Input) value and back (not null)
	 * @return A new {@link SingleSelectInputBuilder}
	 */
	static <T, ITEM> SingleSelectInputBuilder<T, ITEM> select(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return SingleSelectConfigurator.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based {@link SingleSelect},
	 * which uses a {@link Select} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertySingleSelectInputBuilder}
	 */
	static <T> PropertySingleSelectInputBuilder<T> select(final Property<T> selectionProperty) {
		return SingleSelectConfigurator.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based {@link SingleSelect},
	 * which uses a {@link Select} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertySingleSelectInputBuilder}
	 */
	static <T> PropertySingleSelectInputBuilder<T> select(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return SingleSelectConfigurator.create(selectionProperty, itemConverter);
	}

	// ------- filterable select

	/**
	 * Gets a builder to create a <em>filterable</em> {@link SingleSelect}, which
	 * uses a {@link ComboBox} as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #filterable(Class, Class, ItemConverter)}
	 * if not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link FilterableSingleSelectInputBuilder}
	 */
	static <T> FilterableSingleSelectInputBuilder<T, T> filterable(Class<T> type) {
		return FilterableSingleSelectConfigurator.create(type);
	}

	/**
	 * Gets a builder to create a <em>filterable</em> {@link SingleSelect}, which
	 * uses a {@link ComboBox} as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are not consistent (i.e. of different type). When the the
	 * selection item and the selection value types are consistent, the
	 * {@link #filterable(Class)} method can be used.
	 * <p>
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item
	 *                      into a selection (Input) value and back (not null)
	 * @return A new {@link FilterableSingleSelectInputBuilder}
	 */
	static <T, ITEM> FilterableSingleSelectInputBuilder<T, ITEM> filterable(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return FilterableSingleSelectConfigurator.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>filterable</em>
	 * {@link SingleSelect}, which uses a {@link ComboBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
	 */
	static <T> PropertyFilterableSingleSelectInputBuilder<T> filterable(final Property<T> selectionProperty) {
		return FilterableSingleSelectConfigurator.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>filterable</em>
	 * {@link SingleSelect}, which uses a {@link ComboBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
	 */
	static <T> PropertyFilterableSingleSelectInputBuilder<T> filterable(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return FilterableSingleSelectConfigurator.create(selectionProperty, itemConverter);
	}

	// ------- options mode

	/**
	 * Gets a builder to create a <em>options</em> {@link SingleSelect}, which uses
	 * a {@link RadioButtonGroup} as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #options(Class, Class, ItemConverter)}
	 * if not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link OptionsSingleSelectInputBuilder}
	 */
	static <T> OptionsSingleSelectInputBuilder<T, T> options(Class<T> type) {
		return OptionsSingleSelectConfigurator.create(type);
	}

	/**
	 * Gets a builder to create a <em>options</em> {@link SingleSelect}, which uses
	 * a {@link RadioButtonGroup} as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are not consistent (i.e. of different type). When the the
	 * selection item and the selection value types are consistent, the
	 * {@link #options(Class)} method can be used.
	 * <p>
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item
	 *                      into a selection (Input) value and back (not null)
	 * @return A new {@link OptionsSingleSelectInputBuilder}
	 */
	static <T, ITEM> OptionsSingleSelectInputBuilder<T, ITEM> options(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return OptionsSingleSelectConfigurator.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>options</em>
	 * {@link SingleSelect}, which uses a {@link RadioButtonGroup} as input
	 * component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyOptionsSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsSingleSelectInputBuilder<T> options(final Property<T> selectionProperty) {
		return OptionsSingleSelectConfigurator.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>options</em>
	 * {@link SingleSelect}, which uses a {@link RadioButtonGroup} as input
	 * component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyOptionsSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsSingleSelectInputBuilder<T> options(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return OptionsSingleSelectConfigurator.create(selectionProperty, itemConverter);
	}

	// ------- list select

	/**
	 * Gets a builder to create a {@link SingleSelect}, which uses a {@link ListBox}
	 * as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #list(Class, Class, ItemConverter)} if
	 * not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link ListSingleSelectInputBuilder}
	 * @since 5.4.0
	 */
	static <T> ListSingleSelectInputBuilder<T, T> list(Class<T> type) {
		return ListSingleSelectConfigurator.create(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}, which uses a {@link ListBox}
	 * as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are not consistent (i.e. of different type). When the the
	 * selection item and the selection value types are consistent, the
	 * {@link #list(Class)} method can be used.
	 * <p>
	 * @param <T>           Value type
	 * @param <ITEM>        Item type
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item
	 *                      into a selection (Input) value and back (not null)
	 * @return A new {@link ListSingleSelectInputBuilder}
	 * @since 5.4.0
	 */
	static <T, ITEM> ListSingleSelectInputBuilder<T, ITEM> list(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return ListSingleSelectConfigurator.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based {@link SingleSelect},
	 * which uses a {@link ListBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyListSingleSelectInputBuilder}
	 * @since 5.4.0
	 */
	static <T> PropertyListSingleSelectInputBuilder<T> list(final Property<T> selectionProperty) {
		return ListSingleSelectConfigurator.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based {@link SingleSelect},
	 * which uses a {@link ListBox} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyListSingleSelectInputBuilder}
	 * @since 5.4.0
	 */
	static <T> PropertyListSingleSelectInputBuilder<T> list(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return ListSingleSelectConfigurator.create(selectionProperty, itemConverter);
	}

}
