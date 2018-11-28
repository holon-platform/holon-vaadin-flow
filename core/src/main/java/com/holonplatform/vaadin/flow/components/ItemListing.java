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

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.data.ItemSort;
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
	List<P> getVisibleColumns();

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
	 * Gets whether this listing is editable.
	 * <p>
	 * When a listing is editable, the {@link #editItem(Object)} and {@link #cancelEditing()} methods can be used to
	 * control item editing.
	 * </p>
	 * @return Whether this listing is editable
	 */
	boolean isEditable();

	/**
	 * Checks whether the listing is in edit mode.
	 * @return If the listing is in edit mode, returns the currently item in editing. An empty Optional otherwise.
	 */
	Optional<T> isEditing();

	/**
	 * If the listing {@link #isEditable()}, opens the editor interface for the provided item.
	 * @param item the item to edit (not null) if already editing a different item in buffered mode
	 * @throws IllegalArgumentException if the <code>item</code> is not in the backing data provider
	 * @throws IllegalStateException If the listing is not editable
	 */
	void editItem(T item);

	/**
	 * If the listing {@link #isEditable()}, closes the editor discarding any unsaved changes.
	 * @throws IllegalStateException If the listing is not editable
	 */
	void cancelEditing();

	/**
	 * If the listing {@link #isEditable()}, saves the item which is currently in editing, if any.
	 * <p>
	 * When the listing editor is in buffered mode, this method will validate the item and will save any changes made to
	 * the editor fields to the edited item if all validators pass. If the write fails then there will be no events and
	 * the editor will stay open.
	 * </p>
	 * <p>
	 * A successful write will fire an editor <code>save</code> event and close the editor that will fire an editor
	 * <code>close</code> event .
	 * </p>
	 * <p>
	 * NOTE: When the listing editor is not in buffered mode, calling save will have no effect and always return
	 * <code>false</code>.
	 * </p>
	 * @return <code>true</code> if save succeeded, <code>false</code> otherwise
	 * @throws IllegalStateException If the listing is not editable
	 */
	boolean saveEditingItem();

	/**
	 * If the listing {@link #isEditable()}, refreshes the editor components for the current item being edited, if any.
	 * <p>
	 * This is useful when the state of the item is changed while the editor is open.
	 * </p>
	 * @throws IllegalStateException If the listing is not editable
	 */
	void refreshEditingItem();

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
		 * Sets the localizable text content of this cell.
		 * <p>
		 * In order for the localization to work, a {@link LocalizationContext} must be valid (localized) and available
		 * as a {@link Context} resource.
		 * </p>
		 * <p>
		 * This will remove a component set with {@link #setComponent(Component)}.
		 * </p>
		 * @param text the localizable text to be shown in this cell
		 * @see LocalizationContext#getCurrent()
		 */
		void setText(Localizable text);

		/**
		 * Sets the text content of this cell.
		 * <p>
		 * This will remove a component set with {@link #setComponent(Component)}.
		 * </p>
		 * @param text the text to be shown in this cell
		 */
		default void setText(String text) {
			setText(Localizable.of(text));
		}

		/**
		 * Sets the text content of this cell using a localizable <code>messageCode</code>.
		 * <p>
		 * In order for the localization to work, a {@link LocalizationContext} must be valid (localized) and available
		 * as a {@link Context} resource.
		 * </p>
		 * <p>
		 * This will remove a component set with {@link #setComponent(Component)}.
		 * </p>
		 * @param defaultText Default text content if no translation is available for given <code>messageCode</code>.
		 * @param messageCode Text translation message key
		 * @param arguments Optional translation arguments
		 * @see LocalizationContext#getCurrent()
		 */
		default void setText(String defaultText, String messageCode, Object... arguments) {
			setText(Localizable.builder().message((defaultText == null) ? "" : defaultText).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Sets the component as the content of this cell.
		 * <p>
		 * This will remove text set with {@link #setText(String)}.
		 * </p>
		 * @param component the component to set
		 */
		void setComponent(Component component);

	}

	// ------- ItemListing editor component group

	/**
	 * A {@link BoundComponentGroup} which represents the listing item editor {@link Input}s and their property
	 * bindings.
	 * 
	 * @param <P> Property type
	 * @param <T> Item type
	 * 
	 * @since 5.2.0
	 */
	public interface EditorComponentGroup<P, T> extends BoundComponentGroup<P, Input<?>> {

		/**
		 * Get the current editor item.
		 * @return the editor item
		 */
		T getItem();

	}

}
