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
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.ItemOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.PropertyOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.ItemSelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.PropertySelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;

/**
 * A {@link Selectable} component in which at most one item can be selected at a time.
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
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	default SelectionMode getSelectionMode() {
		return SelectionMode.SINGLE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@Override
	default Set<T> getSelectedItems() {
		return getSelectedItem().map(Collections::singleton).orElse(Collections.emptySet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	default Optional<T> getFirstSelectedItem() {
		return getSelectedItem();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAll()
	 */
	@Override
	default void deselectAll() {
		getSelectedItem().ifPresent(this::deselect);
	}

	// ------- builders

	/**
	 * Gets a builder to create a {@link SingleSelect}.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are consistent. Use
	 * {@link #singleSelect(ItemConverter)} if not.
	 * <p>
	 * @param <T> Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T> ItemSelectModeSingleSelectInputBuilder<T, T> create(Class<T> type) {
		return SelectModeSingleSelectInputBuilder.create(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are not consistent (i.e. of
	 * different type). When the the selection item and the selection value types are consistent, the
	 * {@link #singleSelect()} method can be used.
	 * <p>
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item into a selection (Input) value and
	 *        back (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemSelectModeSingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return SelectModeSingleSelectInputBuilder.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}, using given selection {@link Property}.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return SelectModeSingleSelectInputBuilder.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}, using given selection {@link Property} and converter.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return SelectModeSingleSelectInputBuilder.create(selectionProperty, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} using the <em>options</em> rendering mode, i.e. a radio button
	 * group.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are consistent. Use
	 * {@link #singleSelect(ItemConverter)} if not.
	 * <p>
	 * @param <T> Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <T> ItemOptionsModeSingleSelectInputBuilder<T, T> options(Class<T> type) {
		return OptionsModeSingleSelectInputBuilder.create(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} using the <em>options</em> rendering mode, i.e. a radio button
	 * group.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are not consistent (i.e. of
	 * different type). When the the selection item and the selection value types are consistent, the
	 * {@link #singleOptionSelect()} method can be used.
	 * <p>
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item into a selection (Input) value and
	 *        back (not null)
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemOptionsModeSingleSelectInputBuilder<T, ITEM> options(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return OptionsModeSingleSelectInputBuilder.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}, using given selection {@link Property} and the <em>options</em>
	 * rendering mode, i.e. a radio button group.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeSingleSelectInputBuilder<T> options(final Property<T> selectionProperty) {
		return OptionsModeSingleSelectInputBuilder.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect}, using given selection {@link Property}, a converter and the
	 * <em>options</em> rendering mode, i.e. a radio button group.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeSingleSelectInputBuilder<T> options(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return OptionsModeSingleSelectInputBuilder.create(selectionProperty, itemConverter);
	}

}
