package com.holonplatform.vaadin.flow.internal.components.events;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.events.ColumnResizeEvent;

/**
 * Default {@link ColumnResizeEvent} implementation.
 *
 * @param <T> Listing item type
 * @param <P> Listing item property type
 * 
 * @since 5.4.0
 */
public class DefaultColumnResizeEvent<T, P> implements ColumnResizeEvent<T, P> {

	private static final long serialVersionUID = 5796516429244341732L;

	private final ItemListing<T, P> source;
	private final boolean fromClient;
	private final P resizedProperty;
	private final String columnWidth;

	public DefaultColumnResizeEvent(ItemListing<T, P> source, boolean fromClient, P resizedProperty,
			String columnWidth) {
		super();
		this.source = source;
		this.fromClient = fromClient;
		this.resizedProperty = resizedProperty;
		this.columnWidth = columnWidth;
	}

	@Override
	public ItemListing<T, P> getSource() {
		return source;
	}

	@Override
	public boolean isFromClient() {
		return fromClient;
	}

	@Override
	public P getResizedProperty() {
		return resizedProperty;
	}

	@Override
	public String getColumnWidth() {
		return columnWidth;
	}

}
