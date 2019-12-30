package com.holonplatform.vaadin.flow.components.events;

import java.io.Serializable;
import java.util.EventListener;

/**
 * A listener for {@link ColumnResizeEvent}s.
 *
 * @param <T> Listing item type
 * @param <P> Listing item property type
 * 
 * @since 5.4.0
 */
@FunctionalInterface
public interface ColumnResizeListener<T, P> extends EventListener, Serializable {

	/**
	 * Invoked when a column resize event event has been fired.
	 * @param event The column resize event
	 */
	void onColumnResizeEvent(ColumnResizeEvent<T, P> event);

}
