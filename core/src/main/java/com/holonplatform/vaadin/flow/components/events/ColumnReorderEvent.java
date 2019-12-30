package com.holonplatform.vaadin.flow.components.events;

import java.util.List;

import com.holonplatform.vaadin.flow.components.ItemListing;

/**
 * Item listing column reorder event.
 * 
 * @param <T> Listing item type
 * @param <P> Listing item property type
 * 
 * @since 5.4.0
 */
public interface ColumnReorderEvent<T, P> extends Event<ItemListing<T, P>> {

	/**
	 * Checks if this event originated from the client side.
	 * @return <code>true</code> if the event originated from the client side,
	 *         <code>false</code> otherwise
	 */
	boolean isFromClient();

	/**
	 * Gets the new order of the listing columns.
	 * @return The listing columns property ids, in the order thay are displayed
	 */
	List<P> getColumns();

}
