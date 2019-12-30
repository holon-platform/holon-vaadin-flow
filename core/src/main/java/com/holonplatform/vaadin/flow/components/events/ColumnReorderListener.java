package com.holonplatform.vaadin.flow.components.events;

import java.io.Serializable;
import java.util.EventListener;

/**
 * A listener for {@link ColumnReorderEvent}s.
 *
 * @param <T> Listing item type
 * @param <P> Listing item property type
 * 
 * @since 5.4.0
 */
@FunctionalInterface
public interface ColumnReorderListener<T, P> extends EventListener, Serializable {

	/**
	 * Invoked when a column reorder event event has been fired.
	 * @param event The column reorder event
	 */
	void onColumnReorderEvent(ColumnReorderEvent<T, P> event);

}
