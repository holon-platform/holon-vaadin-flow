package com.holonplatform.vaadin.flow.components.events;

import com.holonplatform.vaadin.flow.components.ItemListing;

/**
 * Item listing column resize event.
 * 
 * @param <T> Listing item type
 * @param <P> Listing item property type
 * 
 * @since 5.4.0
 */
public interface ColumnResizeEvent<T, P> extends Event<ItemListing<T, P>> {

	/**
	 * Checks if this event originated from the client side.
	 * @return <code>true</code> if the event originated from the client side,
	 *         <code>false</code> otherwise
	 */
	boolean isFromClient();

	/**
	 * Get the property which identifies the resized column.
	 * @return The resized column property
	 */
	P getResizedProperty();

	/**
	 * Get the new column width after the resize action.
	 * @return The new column width as a CSS-string.
	 */
	String getColumnWidth();

}
