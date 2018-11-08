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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.vaadin.flow.data.ItemDataSource.ItemSort;
import com.vaadin.flow.component.Component;

/**
 * A component to display a set of items as tabular data, using the item properties as column ids.
 * 
 * @param <T> Item type
 * @param <P> Item property type
 * 
 * @since 5.2.0
 */
public interface ItemListing<T, P> extends ItemSet, Selectable<T>, HasComponent {

	/**
	 * Gets the listing visible columns, in the order thay are displayed.
	 * @return The listing visible columns references, represented using the property id
	 */
	Set<P> getVisibleColumns();

	/**
	 * Show or hide the column which corresponds to given <code>property</code> id.
	 * @param property The property which represents the column (not null)
	 * @param visible <code>true</code> to show the column, <code>false</code> to hide it
	 * @throws IllegalArgumentException If the specified property is not bound to any column
	 */
	void setColumnVisible(P property, boolean visible);

	/**
	 * Gets whether the row details component for given <code>item</code> is visible.
	 * @param item Item to check whether the row details component is visible (not null)
	 * @return <code>true</code> if row details component is visible for given item, <code>false</code> otherwise
	 */
	boolean isItemDetailsVisible(T item);

	/**
	 * Set whether to show the row details component for given <code>item</code>.
	 * @param item Item for which to show or hide the row details (not null)
	 * @param visible <code>true</code> to show the row details component, <code>false</code> to hide it
	 */
	void setItemDetailsVisible(T item, boolean visible);

	/**
	 * Sort the listing using given {@link ItemSort} directives.
	 * @param sorts The item sorts to apply
	 */
	@SuppressWarnings("unchecked")
	default void sort(ItemSort<P>... sorts) {
		sort(Arrays.asList(sorts));
	}

	/**
	 * Sort the listing using given {@link ItemSort} directives.
	 * @param sorts The item sorts to apply
	 */
	void sort(List<ItemSort<P>> sorts);

	/**
	 * Set the listing selection mode
	 * @param selectionMode The selection mode to set (not null)
	 */
	void setSelectionMode(SelectionMode selectionMode);

	/**
	 * Refresh given item in data source
	 * @param item Item to refresh (not null)
	 */
	void refreshItem(T item);

	/**
	 * Get the item listing header section rows handler, if available.
	 * @return Optional item listing header handler
	 */
	Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getHeader();

	/**
	 * Get the item listing footer section rows handler, if available.
	 * @return Optional item listing footer handler
	 */
	Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getFooter();

	/**
	 * Opens the editor interface for the provided item.
	 * @param item the item to edit (not null) if already editing a different item in buffered mode
	 * @throws IllegalArgumentException if the <code>item</code> is not in the backing data provider
	 */
	void editItem(T item);

	/**
	 * Close the editor discarding any unsaved changes.
	 */
	void cancelEditing();

	// ------- listing sections handlers

	/**
	 * {@link ItemListing} section handler for header and footer configuration.
	 *
	 * @param <P> Item property type
	 * @param <R> Section row type
	 */
	public interface ItemListingSection<P, R extends ItemListingRow<P>> {

		/**
		 * Get all the section rows, in order from top to bottom.
		 * @return The section rows, an empty list if none
		 */
		List<R> getRows();

		/**
		 * Returns the first row of the section, if available.
		 * @return Optional section first row
		 */
		default Optional<R> getFirstRow() {
			return getRows().stream().findFirst();
		}

	}

	/**
	 * An {@link ItemListing} section row handler.
	 * 
	 * @param <P> Item property type
	 */
	public interface ItemListingRow<P> {

		/**
		 * Gets the cells that belong to this row as an unmodifiable list.
		 * @return the cells on this row
		 */
		List<ItemListingCell> getCells();

		/**
		 * Get the cell on this row corresponding to the given property, if available.
		 * @param property The property for which to obtain the cell (not null)
		 * @return Optional cell bound to the given property
		 */
		Optional<ItemListingCell> getCell(P property);

	}

	/**
	 * An {@link ItemListing} section row cell handler.
	 */
	public interface ItemListingCell {

		/**
		 * Sets the text content of this cell.
		 * <p>
		 * This will remove a component set with {@link #setComponent(Component)}.
		 * </p>
		 * @param text the text to be shown in this cell
		 */
		void setText(String text);

		/**
		 * Sets the component as the content of this cell.
		 * <p>
		 * This will remove text set with {@link #setText(String)}.
		 * </p>
		 * @param component the component to set
		 */
		void setComponent(Component component);

	}

}
