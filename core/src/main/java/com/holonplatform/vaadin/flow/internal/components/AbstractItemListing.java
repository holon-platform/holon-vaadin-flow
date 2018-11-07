/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.data.ItemDataSource.ItemSort;
import com.holonplatform.vaadin.flow.exceptions.ComponentConfigurationException;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingFooterSection;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingHeaderSection;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.shared.Registration;

/**
 * Abstract {@link ItemListing} implementation using a {@link Grid}.
 *
 * @param <T> Item type
 * @param <P> Item property type
 * 
 * @since 5.2.0
 */
public abstract class AbstractItemListing<T, P> implements ItemListing<T, P> {

	private static final long serialVersionUID = 6298536849762717384L;

	/**
	 * Logger
	 */
	protected final static Logger LOGGER = VaadinLogger.create();

	/**
	 * Selection mode
	 */
	private SelectionMode selectionMode = SelectionMode.NONE;

	/**
	 * Optional visible columns
	 */
	private List<P> visibileColumns = Collections.emptyList();

	/**
	 * Grid
	 */
	private final Grid<T> grid;

	/**
	 * A list of the item properties which correspond to a listing column, in the display order
	 */
	private final LinkedList<P> properties = new LinkedList<>();

	/**
	 * Item property column definitions
	 */
	private final Map<P, ItemListingColumn<P, T>> propertyColumns = new HashMap<>();

	/**
	 * Column key suffix generator to ensure unique column names
	 */
	private final AtomicInteger columnKeySuffix = new AtomicInteger(0);

	/**
	 * Constructor.
	 */
	public AbstractItemListing() {
		this(new Grid<>());
	}

	/**
	 * Constructor.
	 * @param grid The Grid to use
	 */
	protected AbstractItemListing(Grid<T> grid) {
		super();
		ObjectUtils.argumentNotNull(grid, "Grid must be not null");
		this.grid = grid;
	}

	/**
	 * Get the {@link Grid} component.
	 * @return the grid component
	 */
	protected Grid<T> getGrid() {
		return grid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return getGrid();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getHeader()
	 */
	@Override
	public Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getHeader() {
		return Optional.of(new DefaultItemListingHeaderSection<>(getGrid(), property -> getColumn(property).orElse(null)));
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getFooter()
	 */
	@Override
	public Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getFooter() {
		return Optional.of(new DefaultItemListingFooterSection<>(getGrid(), property -> getColumn(property).orElse(null)));
	}

	/**
	 * Get the column configuration for given property.
	 * @param property The property (not null)
	 * @return The property column configuration (never null)
	 */
	protected ItemListingColumn<P, T> getColumnConfiguration(P property) {
		return propertyColumns.computeIfAbsent(property,
				p -> new DefaultItemListingColumn<>(property, ensureUniqueColumnKey(generateColumnKey(p))));
	}

	/**
	 * Get the column key which corresponds to given item property id.
	 * @param property The item property id for which to obtain the column key
	 * @return The column key (not null)
	 */
	protected abstract String generateColumnKey(P property);

	/**
	 * Ensure unique column key, appending a numeric suffix to duplicate key names if required.
	 * @param key The column key
	 * @return The unique column key
	 */
	protected String ensureUniqueColumnKey(String key) {
		if (propertyColumns.values().stream().map(column -> column.getColumnKey())
				.anyMatch(columnKey -> columnKey.equals(key))) {
			return key + "_" + columnKeySuffix.incrementAndGet();
		}
		return key;
	}

	/**
	 * Get the column key for given property.
	 * @param property The property (not null)
	 * @return The column key for given property
	 */
	protected String getColumnKey(P property) {
		return getColumnConfiguration(property).getColumnKey();
	}

	/**
	 * Get the Grid column bound to given property, if available.
	 * @param property The property
	 * @return Optional Grid column bound to given property
	 */
	protected Optional<Column<T>> getColumn(P property) {
		return Optional.ofNullable(getGrid().getColumnByKey(getColumnKey(property)));
	}

	/**
	 * Get the property which corresponds to given column key, if available.
	 * @param column The column key (not null)
	 * @return Optional property which corresponds to given column key
	 */
	protected Optional<P> getProperty(String columnKey) {
		if (columnKey == null) {
			return Optional.empty();
		}
		return propertyColumns.entrySet().stream().filter(entry -> columnKey.equals(entry.getValue().getColumnKey()))
				.map(entry -> entry.getKey()).findFirst();
	}

	/**
	 * Get the property which corresponds to given column, if available.
	 * @param column The column (not null)
	 * @return Optional property which corresponds to given column
	 */
	protected Optional<P> getProperty(Column<?> column) {
		ObjectUtils.argumentNotNull(column, "Column must be not null");
		return getProperty(column.getKey());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getVisibleColumns()
	 */
	@Override
	public Set<P> getVisibleColumns() {
		return getVisibleColumnProperties().stream()
				.filter(property -> getColumn(property).map(column -> column.isVisible()).orElse(false))
				.collect(Collectors.toSet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#setColumnVisible(java.lang.Object, boolean)
	 */
	@Override
	public void setColumnVisible(P property, boolean visible) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getColumn(property)
				.orElseThrow(
						() -> new IllegalArgumentException("No column is bound to the property [" + property + "]"))
				.setVisible(visible);
	}

	/**
	 * Add an item property to be rendered as a listing column.
	 * @param property The item property id to add (not null)
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T> addPropertyColumn(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		properties.add(property);
		return getColumnConfiguration(property);
	}

	/**
	 * Add an item property to be rendered as a listing column as first column.
	 * @param property The item property id to add (not null)
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T> addPropertyColumnAsFirst(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		properties.addFirst(property);
		return getColumnConfiguration(property);
	}

	/**
	 * Add an item property to be rendered as a listing column as last column.
	 * @param property The item property id to add (not null)
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T> addPropertyColumnAsLast(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		properties.addLast(property);
		return getColumnConfiguration(property);
	}

	/**
	 * Add an item property to be rendered as a listing column before the <code>beforeProperty</code> property id.
	 * @param property The item property id to add (not null)
	 * @param beforeProperty The property before to add the item property
	 * @return <code>true</code> if the <code>beforeProperty</code> is available. If <code>false</code>, the property
	 *         column will be added at the end of the list
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T> addPropertyColumnBefore(P property, P beforeProperty) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (beforeProperty != null) {
			int idx = properties.indexOf(beforeProperty);
			if (idx >= 0) {
				properties.add(idx, property);
				return getColumnConfiguration(property);
			}
		}
		return addPropertyColumn(property);
	}

	/**
	 * Add an item property to be rendered as a listing column after the <code>afterProperty</code> property id.
	 * @param property The item property id to add (not null)
	 * @param afterProperty The property after to add the item property
	 * @return <code>true</code> if the <code>afterProperty</code> is available. If <code>false</code>, the property
	 *         column will be added at the end of the list
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T> addPropertyColumnAfter(P property, P afterProperty) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		if (afterProperty != null) {
			int idx = properties.indexOf(afterProperty);
			if (idx >= 0 && idx < (properties.size() - 1)) {
				properties.add(idx + 1, property);
				return getColumnConfiguration(property);
			}
		}
		return addPropertyColumn(property);
	}

	/**
	 * Set to display given property as the first one.
	 * @param property The property
	 * @return <code>true</code> if the property is in the property set and was moved within the list
	 */
	protected boolean setDisplayAsFirst(P property) {
		if (property != null && properties.contains(property)) {
			properties.remove(property);
			properties.addFirst(property);
			return true;
		}
		return false;
	}

	/**
	 * Set to display given property as the last one.
	 * @param property The property
	 * @return <code>true</code> if the property is in the property set and was moved within the list
	 */
	protected boolean setDisplayAsLast(P property) {
		if (property != null && properties.contains(property)) {
			properties.remove(property);
			properties.addLast(property);
			return true;
		}
		return false;
	}

	/**
	 * Set to display given property before the provided <code>beforeProperty</code>.
	 * @param property The property
	 * @param beforeProperty The property before to display the property
	 * @return <code>true</code> if the property is in the property set and was moved within the list
	 */
	protected boolean setDisplayBefore(P property, P beforeProperty) {
		if (property != null && properties.contains(property) && beforeProperty != null
				&& properties.contains(beforeProperty) && !property.equals(beforeProperty)) {
			int idx = properties.indexOf(beforeProperty);
			if (idx >= 0) {
				properties.remove(property);
				properties.add(idx, property);
				return true;
			}
		}
		return false;
	}

	/**
	 * Set to display given property after the provided <code>afterProperty</code>.
	 * @param property The property
	 * @param afterProperty The property after to display the property
	 * @return <code>true</code> if the property is in the property set and was moved within the list
	 */
	protected boolean setDisplayAfter(P property, P afterProperty) {
		if (property != null && properties.contains(property) && afterProperty != null
				&& properties.contains(afterProperty) && !property.equals(afterProperty)) {
			int idx = properties.indexOf(afterProperty);
			if (idx >= 0 && idx < (properties.size() - 1)) {
				properties.remove(property);
				properties.add(idx + 1, property);
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the grid visible columns.
	 * @param visibileColumns The visible columns to set
	 */
	@SuppressWarnings("unchecked")
	protected void setVisibleColumns(List<? extends P> visibileColumns) {
		if (visibileColumns != null) {
			for (P property : visibileColumns) {
				if (!properties.contains(property)) {
					throw new ComponentConfigurationException("The property [" + property
							+ "] to set as visible column is not part of the listing property set");
				}
			}
		}
		this.visibileColumns = (visibileColumns != null) ? (List<P>) visibileColumns : Collections.emptyList();
	}

	/**
	 * Get the visibile columns properties.
	 * @return the visibile columns properties
	 */
	protected List<P> getVisibleColumnProperties() {
		return visibileColumns.isEmpty() ? properties : visibileColumns;
	}

	/**
	 * Build the listing, adding a Grid column for each item property.
	 */
	public void build() {
		// remove all columns
		getGrid().getColumns().forEach(column -> getGrid().removeColumn(column));
		// add a column for each visible property
		getVisibleColumnProperties().forEach(property -> addGridColumn(property));
	}

	/**
	 * Add a column bound to given item property to the grid.
	 * @param property Item property id (not null)
	 * @return The key of the added column
	 */
	protected String addGridColumn(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		// get the column configuration
		final ItemListingColumn<P, T> configuration = preProcessConfiguration(getColumnConfiguration(property));
		// add the column
		final Column<T> column = generateGridColumn(configuration);
		if (column == null) {
			throw new ComponentConfigurationException(
					"Failed to generate listing column for property [" + configuration.getProperty() + "]");
		}
		// configure the column
		column.setKey(configuration.getColumnKey());
		// header
		getColumnHeader(configuration).ifPresent(h -> {
			column.setHeader(LocalizationContext.translate(h, true));
		});
		configuration.getHeaderComponent().ifPresent(c -> column.setHeader(c));
		// visible
		column.setVisible(configuration.isVisible());
		// resizable
		column.setResizable(configuration.isResizable());
		// frozen
		column.setFrozen(configuration.isFrozen());
		// width
		configuration.getWidth().ifPresent(w -> column.setWidth(w));
		column.setFlexGrow(configuration.getFlexGrow());
		// sort
		if (configuration.getSortMode() == SortMode.ENABLED) {
			column.setSortable(true);
			configuration.getComparator().ifPresent(c -> column.setComparator(c));
			if (configuration.getSortOrderProvider().isPresent()) {
				column.setSortOrderProvider(configuration.getSortOrderProvider().get());
			} else {
				// check sort properties
				if (!configuration.getSortProperties().isEmpty()) {
					final List<String> sorts = configuration.getSortProperties().stream()
							.map(p -> getSortPropertyName(property).orElse(null)).filter(p -> p != null)
							.collect(Collectors.toList());
					column.setSortOrderProvider(
							direction -> sorts.stream().map(sort -> new QuerySortOrder(sort, direction)));
				}
			}
		} else {
			column.setSortable(false);
		}
		// return the column key
		return configuration.getColumnKey();
	}

	/**
	 * Generate a grid column using given configuration.
	 * @param configuration Property column configuration
	 * @return The generated column
	 */
	protected Column<T> generateGridColumn(ItemListingColumn<P, T> configuration) {
		// check renderer
		if (configuration.getRenderer().isPresent()) {
			return getGrid().addColumn(configuration.getRenderer().get());
		}
		// check value provider
		if (configuration.getValueProvider().isPresent()) {
			return getGrid().addColumn(configuration.getValueProvider().get());
		}
		return generateDefaultGridColumn(configuration);
	}

	/**
	 * Generate the default grid column using given configuration.
	 * @param configuration Property column configuration
	 * @return The generated column
	 */
	protected abstract Column<T> generateDefaultGridColumn(ItemListingColumn<P, T> configuration);

	/**
	 * Process column configuration before adding the column to the grid.
	 * @param configuration Property column configuration
	 * @return Processed property column configuration
	 */
	protected abstract ItemListingColumn<P, T> preProcessConfiguration(ItemListingColumn<P, T> configuration);

	/**
	 * Get the {@link QuerySortOrder} property name for given property, if available.
	 * @param property Property to sort
	 * @return Optional sort property name
	 */
	protected abstract Optional<String> getSortPropertyName(P property);

	/**
	 * Get the column header text to use for given property, if available.
	 * @param configuration Property column configuration
	 * @return Optional column header text
	 */
	protected Optional<Localizable> getColumnHeader(ItemListingColumn<P, T> configuration) {
		Optional<Localizable> header = configuration.getHeaderText();
		if (header.isPresent()) {
			return header;
		}
		return getDefaultColumnHeader(configuration.getProperty());
	}

	/**
	 * Get the default column header text for given property, if available.
	 * @param property The property (not null)
	 * @return Optional default column header
	 */
	protected abstract Optional<Localizable> getDefaultColumnHeader(P property);

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#isItemDetailsVisible(java.lang.Object)
	 */
	@Override
	public boolean isItemDetailsVisible(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		return getGrid().isDetailsVisible(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#setItemDetailsVisible(java.lang.Object, boolean)
	 */
	@Override
	public void setItemDetailsVisible(T item, boolean visible) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		getGrid().setDetailsVisible(item, visible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#sort(java.util.List)
	 */
	@Override
	public void sort(List<ItemSort<P>> sorts) {
		List<GridSortOrder<T>> orders = new LinkedList<>();
		if (sorts != null) {
			sorts.stream().forEach(sort -> {
				getColumn(sort.getProperty())
						.map(c -> new GridSortOrder<>(c,
								sort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING))
						.ifPresent(o -> orders.add(o));
			});
		}
		getGrid().sort(orders);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		getGrid().getDataProvider().refreshAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#refreshItem(java.lang.Object)
	 */
	@Override
	public void refreshItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		getGrid().getDataProvider().refreshItem(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectionMode()
	 */
	@Override
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#setSelectionMode(com.holonplatform.vaadin.flow.components.
	 * Selectable.SelectionMode)
	 */
	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		ObjectUtils.argumentNotNull(selectionMode, "Selection mode must be not null");
		getGrid().setSelectionMode(asGridSelectionMode(selectionMode));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		return getGrid().getSelectedItems();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	public Optional<T> getFirstSelectedItem() {
		return getGrid().getSelectedItems().stream().findFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		ObjectUtils.argumentNotNull(item, "Item to select must be not null");
		getGrid().select(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		ObjectUtils.argumentNotNull(item, "Item to deselect must be not null");
		getGrid().deselect(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		getGrid().deselectAll();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.Selectable#addSelectionListener(com.holonplatform.vaadin.flow.components
	 * .Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		return getGrid().addSelectionListener(e -> {
			selectionListener.onSelectionChange(new DefaultSelectionEvent<>(e.getAllSelectedItems(), e.isFromClient()));
		});
	}

	/**
	 * Get the given {@link SelectionMode} as a Grid SelectionMode.
	 * @param selectionMode The selection mode (not null)
	 * @return The Grid SelectionMode
	 */
	private static com.vaadin.flow.component.grid.Grid.SelectionMode asGridSelectionMode(SelectionMode selectionMode) {
		switch (selectionMode) {
		case MULTI:
			return com.vaadin.flow.component.grid.Grid.SelectionMode.MULTI;
		case SINGLE:
			return com.vaadin.flow.component.grid.Grid.SelectionMode.SINGLE;
		case NONE:
		default:
			return com.vaadin.flow.component.grid.Grid.SelectionMode.NONE;
		}
	}

}
