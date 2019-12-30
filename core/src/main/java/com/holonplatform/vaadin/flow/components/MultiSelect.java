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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.ListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.PropertyListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsMultiSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.OptionsMultiSelectConfigurator.OptionsMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsMultiSelectConfigurator.PropertyOptionsMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.listbox.MultiSelectListBox;

/**
 * A {@link Selectable} component in which multiple items can be selected at the
 * same time. Selecting an item adds it to the selection.
 * 
 * @param <T> Selection item type
 * 
 * @since 5.2.0
 */
public interface MultiSelect<T> extends Selectable<T>, Input<Set<T>>, ItemSet {

	/**
	 * Adds the given item to the set of currently selected items.
	 * <p>
	 * By default this does not clear any previous selection. To do that, use
	 * {@link #deselectAll()}.
	 * <p>
	 * @param items Items to select (not null)
	 */
	void select(Iterable<T> items);

	/**
	 * Adds the given item to the set of currently selected items.
	 * <p>
	 * By default this does not clear any previous selection. To do that, use
	 * {@link #deselectAll()}.
	 * <p>
	 * @param items Items to select (not null)
	 */
	@SuppressWarnings("unchecked")
	default void select(T... items) {
		ObjectUtils.argumentNotNull(items, "Items to select must be not null");
		select(Stream.of(items).map(i -> {
			ObjectUtils.argumentNotNull(i, "Items to select must be not null");
			return i;
		}).collect(Collectors.toSet()));
	}

	/**
	 * Removes the given items from the set of currently selected items.
	 * @param items Items to deselect (not null)
	 */
	void deselect(Iterable<T> items);

	/**
	 * Removes the given items from the set of currently selected items.
	 * @param items Items to deselect (not null)
	 */
	@SuppressWarnings("unchecked")
	default void deselect(T... items) {
		ObjectUtils.argumentNotNull(items, "Items to deselect must be not null");
		deselect(Stream.of(items).map(i -> {
			ObjectUtils.argumentNotNull(i, "Items to deselect must be not null");
			return i;
		}).collect(Collectors.toSet()));
	}

	/**
	 * Selects all available the items.
	 */
	void selectAll();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	default SelectionMode getSelectionMode() {
		return SelectionMode.MULTI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	default Optional<T> getFirstSelectedItem() {
		return getSelectedItems().stream().findFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.components.Selectable#select(java.lang.Object)
	 */
	@Override
	default void select(T item) {
		ObjectUtils.argumentNotNull(item, "Item to select must be not null");
		select(Collections.singleton(item));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	default void deselect(T item) {
		ObjectUtils.argumentNotNull(item, "Item to deselect must be not null");
		deselect(Collections.singleton(item));
	}

	// ------- builders

	/**
	 * Gets a builder to create a <em>options</em> {@link MultiSelect}, which uses a
	 * {@link CheckboxGroup} as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #options(Class, Class, ItemConverter)}
	 * if not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link OptionsMultiSelectInputBuilder}
	 */
	static <T> OptionsMultiSelectInputBuilder<T, T> options(Class<T> type) {
		return OptionsMultiSelectConfigurator.create(type);
	}

	/**
	 * Gets a builder to create a <em>options</em> {@link MultiSelect}, which uses a
	 * {@link CheckboxGroup} as input component.
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
	 * @return A new {@link OptionsMultiSelectInputBuilder}
	 */
	static <T, ITEM> OptionsMultiSelectInputBuilder<T, ITEM> options(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return OptionsMultiSelectConfigurator.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>options</em>
	 * {@link MultiSelect}, which uses a {@link CheckboxGroup} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyOptionsMultiSelectInputBuilder}
	 */
	static <T> PropertyOptionsMultiSelectInputBuilder<T> options(final Property<T> selectionProperty) {
		return OptionsMultiSelectConfigurator.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>options</em>
	 * {@link MultiSelect}, which uses a {@link CheckboxGroup} as input component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyOptionsMultiSelectInputBuilder}
	 */
	static <T> PropertyOptionsMultiSelectInputBuilder<T> options(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return OptionsMultiSelectConfigurator.create(selectionProperty, itemConverter);
	}

	// ------- list mode

	/**
	 * Gets a builder to create a <em>list</em> {@link MultiSelect}, which uses a
	 * {@link MultiSelectListBox} as input component.
	 * <p>
	 * This builder can be used when the selection items type and the selection
	 * value type are consistent. Use {@link #list(Class, Class, ItemConverter)} if
	 * not.
	 * <p>
	 * @param <T>  Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link ListMultiSelectInputBuilder}
	 */
	static <T> ListMultiSelectInputBuilder<T, T> list(Class<T> type) {
		return ListMultiSelectConfigurator.create(type);
	}

	/**
	 * Gets a builder to create a <em>list</em> {@link MultiSelect}, which uses a
	 * {@link MultiSelectListBox} as input component.
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
	 * @return A new {@link ListMultiSelectInputBuilder}
	 */
	static <T, ITEM> ListMultiSelectInputBuilder<T, ITEM> list(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return ListMultiSelectConfigurator.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>list</em>
	 * {@link MultiSelect}, which uses a {@link MultiSelectListBox} as input
	 * component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @return A new {@link PropertyListMultiSelectInputBuilder}
	 */
	static <T> PropertyListMultiSelectInputBuilder<T> list(final Property<T> selectionProperty) {
		return ListMultiSelectConfigurator.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link Property} model based <em>list</em>
	 * {@link MultiSelect}, which uses a {@link MultiSelectListBox} as input
	 * component.
	 * @param <T>               Value type
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding {@link PropertyBox} item
	 * @return A new {@link PropertyListMultiSelectInputBuilder}
	 */
	static <T> PropertyListMultiSelectInputBuilder<T> list(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return ListMultiSelectConfigurator.create(selectionProperty, itemConverter);
	}

}
