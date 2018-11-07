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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.holonplatform.vaadin.flow.components.support.ClickEvent;
import com.holonplatform.vaadin.flow.data.ItemDataSource.ItemSort;

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
	 * Event fired when a listing item is clicked.
	 * @param <T> Item type
	 */
	public interface ItemClickEvent<T> extends ClickEvent {

		/**
		 * Gets the clicked item.
		 * @return the clicked item
		 */
		T getItem();

	}

	/**
	 * Listener for user click events on an item (a listing row).
	 * @param <T> Item type
	 */
	@FunctionalInterface
	public interface ItemClickListener<T> extends Serializable {

		/**
		 * Invoked when the user clicks on a listing item.
		 * @param event Item click event
		 */
		void onItemClick(ItemClickEvent<T> event);

	}

}
