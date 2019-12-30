package com.holonplatform.vaadin.flow.internal.components.events;

import java.util.Collections;
import java.util.List;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.events.ColumnReorderEvent;

/**
 * Default {@link ColumnReorderEvent} implementation.
 *
 * @param <T> Listing item type
 * @param <P> Listing item property type
 * 
 * @since 5.4.0
 */
public class DefaultColumnReorderEvent<T, P> implements ColumnReorderEvent<T, P> {

	private static final long serialVersionUID = -8327993040907036877L;
	
	private final ItemListing<T, P> source;
	private final boolean fromClient;
	private final List<P> columns;

	public DefaultColumnReorderEvent(ItemListing<T, P> source, boolean fromClient, List<P> columns) {
		super();
		this.source = source;
		this.fromClient = fromClient;
		this.columns = columns;
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
	public List<P> getColumns() {
		return Collections.unmodifiableList(columns);
	}

}
