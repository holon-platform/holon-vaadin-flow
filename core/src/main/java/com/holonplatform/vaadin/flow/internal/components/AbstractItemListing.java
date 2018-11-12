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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.Validatable;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingSection;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingContextMenuBuilder;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.components.events.ItemClickEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEventListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingItemEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingRefreshListener;
import com.holonplatform.vaadin.flow.data.ItemDataSource.ItemSort;
import com.holonplatform.vaadin.flow.exceptions.ComponentConfigurationException;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasEnabledConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasSizeConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasStyleConfigurator;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingClickEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingItemEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingRefreshEvent;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingFooterSection;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingHeaderSection;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.contextmenu.ContextMenuBase;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridContextMenu;
import com.vaadin.flow.component.grid.GridContextMenu.GridContextMenuItemClickEvent;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorCancelListener;
import com.vaadin.flow.component.grid.editor.EditorCloseListener;
import com.vaadin.flow.component.grid.editor.EditorOpenListener;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
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
	private final Map<P, ItemListingColumn<P, T, ?>> propertyColumns = new HashMap<>();

	/**
	 * Column key suffix generator to ensure unique column names
	 */
	private final AtomicInteger columnKeySuffix = new AtomicInteger(0);

	/**
	 * Whether the listing is editable
	 */
	private boolean editable = false;

	/**
	 * Item validators
	 */
	private final List<Validator<T>> validators = new LinkedList<>();

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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		getGrid().setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return getGrid().isVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasEnabled()
	 */
	@Override
	public Optional<HasEnabled> hasEnabled() {
		return Optional.of(getGrid());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasStyle()
	 */
	@Override
	public Optional<HasStyle> hasStyle() {
		return Optional.of(getGrid());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasSize()
	 */
	@Override
	public Optional<HasSize> hasSize() {
		return Optional.of(getGrid());
	}

	/**
	 * Get the header section handler.
	 * @return the header section handler
	 */
	protected EditableItemListingSection<P> getHeaderSection() {
		return new DefaultItemListingHeaderSection<>(getGrid(), property -> getColumn(property).orElse(null));
	}

	/**
	 * Get the footer section handler.
	 * @return the footer section handler
	 */
	protected EditableItemListingSection<P> getFooterSection() {
		return new DefaultItemListingFooterSection<>(getGrid(), property -> getColumn(property).orElse(null));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getHeader()
	 */
	@Override
	public Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getHeader() {
		return Optional.of(getHeaderSection());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getFooter()
	 */
	@Override
	public Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getFooter() {
		return Optional.of(getFooterSection());
	}

	/**
	 * Get the column configuration for given property.
	 * @param property The property (not null)
	 * @return The property column configuration (never null)
	 */
	protected ItemListingColumn<P, T, ?> getColumnConfiguration(P property) {
		return propertyColumns.computeIfAbsent(property, p -> new DefaultItemListingColumn<>(property,
				ensureUniqueColumnKey(generateColumnKey(p)), isAlwaysReadOnly(p)));
	}

	/**
	 * Get the column key which corresponds to given item property id.
	 * @param property The item property id for which to obtain the column key
	 * @return The column key (not null)
	 */
	protected abstract String generateColumnKey(P property);

	/**
	 * Gets whether the given property is always read-only.
	 * @param property The property
	 * @return whether the given property is always read-only
	 */
	protected abstract boolean isAlwaysReadOnly(P property);

	/**
	 * Get the listing property set.
	 * @return the property set
	 */
	protected List<P> getListingProperties() {
		return properties;
	}

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
	public List<P> getVisibleColumns() {
		return getVisibleColumnProperties().stream()
				.filter(property -> getColumn(property).map(column -> column.isVisible()).orElse(false))
				.collect(Collectors.toList());
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
	protected ItemListingColumn<P, T, ?> addPropertyColumn(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		properties.add(property);
		return getColumnConfiguration(property);
	}

	/**
	 * Add an item property to be rendered as a listing column as first column.
	 * @param property The item property id to add (not null)
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T, ?> addPropertyColumnAsFirst(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		properties.addFirst(property);
		return getColumnConfiguration(property);
	}

	/**
	 * Add an item property to be rendered as a listing column as last column.
	 * @param property The item property id to add (not null)
	 * @return the column configuration
	 */
	protected ItemListingColumn<P, T, ?> addPropertyColumnAsLast(P property) {
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
	protected ItemListingColumn<P, T, ?> addPropertyColumnBefore(P property, P beforeProperty) {
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
	protected ItemListingColumn<P, T, ?> addPropertyColumnAfter(P property, P afterProperty) {
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
	 * Build the listing, adding a Grid column for each item property and setting up the item editor if
	 * <code>editable</code> is <code>true</code>.
	 * @param editable Whether the listing is editable
	 */
	public void build(boolean editable) {
		this.editable = editable;
		// remove all columns
		getGrid().getColumns().forEach(column -> getGrid().removeColumn(column));
		// add a column for each visible property
		getVisibleColumnProperties().forEach(property -> addGridColumn(property));
		// check init editor
		if (editable) {
			initEditor(getVisibleColumnProperties());
		}
	}

	/**
	 * Add a column bound to given item property to the grid.
	 * @param property Item property id (not null)
	 * @return The key of the added column
	 */
	protected String addGridColumn(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		// get the column configuration
		final ItemListingColumn<P, T, ?> configuration = preProcessConfiguration(getColumnConfiguration(property));
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
		// alignment
		configuration.getAlignment().ifPresent(a -> column.setTextAlign(asColumnTextAlign(a)));
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
	protected Column<T> generateGridColumn(ItemListingColumn<P, T, ?> configuration) {
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
	protected abstract Column<T> generateDefaultGridColumn(ItemListingColumn<P, T, ?> configuration);

	/**
	 * Process column configuration before adding the column to the grid.
	 * @param configuration Property column configuration
	 * @return Processed property column configuration
	 */
	protected abstract ItemListingColumn<P, T, ?> preProcessConfiguration(ItemListingColumn<P, T, ?> configuration);

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
	protected Optional<Localizable> getColumnHeader(ItemListingColumn<P, T, ?> configuration) {
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
		// check editing
		if (isEditable()) {
			cancelEditing();
		}
		getGrid().getDataProvider().refreshAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#refreshItem(java.lang.Object)
	 */
	@Override
	public void refreshItem(T item) {
		// check editing
		if (isEditable()) {
			cancelEditing();
		}
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#editItem(java.lang.Object)
	 */
	@Override
	public void editItem(T item) {
		if (!isEditable()) {
			throw new IllegalStateException("The item listing is not editable");
		}
		ObjectUtils.argumentNotNull(item, "Item to edit must be not null");
		getEditor().editItem(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#cancelEditing()
	 */
	@Override
	public void cancelEditing() {
		if (!isEditable()) {
			throw new IllegalStateException("The item listing is not editable");
		}
		if (getEditor().isOpen()) {
			getEditor().cancel();
		}
	}

	/**
	 * Get the listing editor.
	 * @return The listing editor
	 */
	protected Editor<T> getEditor() {
		return getGrid().getEditor();
	}

	/**
	 * Add an item validator.
	 * @param validator The validator to add (not null)
	 */
	protected void addValidator(Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		this.validators.add(validator);

	}

	/**
	 * Init the grid editor
	 * @param properties Visible properties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void initEditor(List<P> properties) {
		// property set
		final Map<String, PropertyDefinition<T, ?>> definitions = new HashMap<>(properties.size());
		final PropertySet<T> propertySet = new ItemListingPropertySet<>(definitions);
		for (P property : properties) {
			final ItemListingColumn<P, T, ?> configuration = getColumnConfiguration(property);
			// exclude read-only
			if (!configuration.isReadOnly()) {
				definitions.put(configuration.getColumnKey(),
						new ItemListingPropertyDefinition(propertySet, configuration.getColumnKey(),
								getPropertyType(property), getPropertyValueGetter(property),
								getPropertyValueSetter(property).orElse(null)));
			}
		}
		// binder
		final Binder<T> binder = Binder.withPropertySet(new ItemListingPropertySet<>(definitions));
		// item validators
		validators.forEach(validator -> binder.withValidator(Validatable.adapt(validator)));
		// property editors and validators
		for (P property : properties) {
			final ItemListingColumn<P, T, ?> configuration = getColumnConfiguration(property);
			if (!configuration.isReadOnly()) {
				getColumn(property).ifPresent(column -> {
					getPropertyEditor(configuration).ifPresent(editor -> {
						final BindingBuilder<T, ?> builder = getGrid().getEditor().getBinder()
								.forField(editor.asHasValue());
						// property validators
						getDefaultPropertyValidators(property)
								.forEach(validator -> builder.withValidator(Validatable.adapt(validator)));
						// additional validators
						configuration.getValidators()
								.forEach(validator -> builder.withValidator(Validatable.adapt((Validator) validator)));
						// bind to column
						column.setEditorBinding(builder.bind(configuration.getColumnKey()));
					});
				});

			}
		}
		getEditor().setBinder(binder);
	}

	/**
	 * Get the property editor to use with given property configuration, if available.
	 * @param configuration The property configuration
	 * @return Optional property editor
	 */
	protected Optional<Input<?>> getPropertyEditor(ItemListingColumn<P, T, ?> configuration) {
		if (configuration.getEditor().isPresent()) {
			return configuration.getEditor().map(i -> i);
		}
		return getDefaultPropertyEditor(configuration.getProperty());
	}

	/**
	 * Get the default property editor to use with given property, if available.
	 * @param property The property
	 * @return Optional default property editor
	 */
	protected abstract Optional<Input<?>> getDefaultPropertyEditor(P property);

	/**
	 * Get the default property validators, if available.
	 * @param property The property
	 * @return The default property validators, empty if none
	 */
	protected abstract Collection<Validator<Object>> getDefaultPropertyValidators(P property);

	/**
	 * Get the property value type.
	 * @param property The property
	 * @return the property value type
	 */
	protected abstract Class<?> getPropertyType(P property);

	/**
	 * Get the property value getter.
	 * @param property The property
	 * @return the property value getter
	 */
	protected abstract ValueProvider<T, ?> getPropertyValueGetter(P property);

	/**
	 * Get the property value setter.
	 * @param property The property
	 * @return Optional property value setter
	 */
	protected abstract Optional<Setter<T, ?>> getPropertyValueSetter(P property);

	/**
	 * ItemListing {@link PropertySet}.
	 *
	 * @param <T> Item type
	 */
	@SuppressWarnings("serial")
	static class ItemListingPropertySet<T> implements PropertySet<T> {

		private final Map<String, PropertyDefinition<T, ?>> definitions;

		public ItemListingPropertySet(Map<String, PropertyDefinition<T, ?>> definitions) {
			super();
			this.definitions = definitions;
		}

		@Override
		public Stream<PropertyDefinition<T, ?>> getProperties() {
			return definitions.values().stream();
		}

		@Override
		public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
			return Optional.ofNullable(definitions.get(name));
		}

	}

	/**
	 * ItemListing {@link PropertyDefinition}.
	 *
	 * @param <T> Item type
	 * @param <V> Value type
	 */
	private static class ItemListingPropertyDefinition<T, V> implements PropertyDefinition<T, V> {

		private static final long serialVersionUID = 3723795023936471324L;

		private final PropertySet<T> propertySet;
		private final String name;
		private final Class<V> type;
		private final ValueProvider<T, V> getter;
		private final Setter<T, V> setter;

		public ItemListingPropertyDefinition(PropertySet<T> propertySet, String name, Class<V> type,
				ValueProvider<T, V> getter, Setter<T, V> setter) {
			super();
			this.propertySet = propertySet;
			this.name = name;
			this.type = type;
			this.getter = getter;
			this.setter = setter;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getPropertyHolderType()
		 */
		@Override
		public Class<?> getPropertyHolderType() {
			return ItemListingPropertySet.class;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getPropertySet()
		 */
		@Override
		public PropertySet<T> getPropertySet() {
			return propertySet;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getName()
		 */
		@Override
		public String getName() {
			return name;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getGetter()
		 */
		@Override
		public ValueProvider<T, V> getGetter() {
			return getter;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getSetter()
		 */
		@Override
		public Optional<Setter<T, V>> getSetter() {
			return Optional.ofNullable(setter);
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getType()
		 */
		@Override
		public Class<V> getType() {
			return type;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getCaption()
		 */
		@Override
		public String getCaption() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getParent()
		 */
		@Override
		public PropertyDefinition<T, ?> getParent() {
			return null;
		}

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

	/**
	 * Convert the given {@link ColumnAlignment} into a {@link ColumnTextAlign}.
	 * @param alignment Alignment to convert
	 * @return Converted alignment
	 */
	private static ColumnTextAlign asColumnTextAlign(ColumnAlignment alignment) {
		switch (alignment) {
		case CENTER:
			return ColumnTextAlign.CENTER;
		case RIGHT:
			return ColumnTextAlign.END;
		case LEFT:
		default:
			return ColumnTextAlign.START;
		}
	}

	/**
	 * Convert given {@link SortDirection} into a {@link com.holonplatform.core.query.QuerySort.SortDirection}.
	 * @param direction Sort direction to convert
	 * @return Converted sort direction
	 */
	static com.holonplatform.core.query.QuerySort.SortDirection convert(SortDirection direction) {
		switch (direction) {
		case DESCENDING:
			return com.holonplatform.core.query.QuerySort.SortDirection.DESCENDING;
		case ASCENDING:
		default:
			return com.holonplatform.core.query.QuerySort.SortDirection.ASCENDING;
		}
	}

	// --------- configurator

	static abstract class AbstractItemListingConfigurator<T, P, L extends AbstractItemListing<T, P>, C extends ItemListingConfigurator<T, P, C>>
			extends AbstractComponentConfigurator<Grid<T>, C> implements ItemListingConfigurator<T, P, C> {

		protected final DefaultHasSizeConfigurator sizeConfigurator;
		protected final DefaultHasStyleConfigurator styleConfigurator;
		protected final DefaultHasEnabledConfigurator enabledConfigurator;

		protected final L instance;

		protected Set<T> items = new HashSet<>();

		private final List<ItemListingRefreshListener<T, P>> refreshListeners = new LinkedList<>();

		private boolean editable;
		private boolean editorBuffered;

		private int frozenColumnsCount;

		private Consumer<EditableItemListingSection<P>> headerConfigurator;
		private Consumer<EditableItemListingSection<P>> footerConfigurator;

		public AbstractItemListingConfigurator(L instance) {
			super(instance.getGrid());
			this.instance = instance;
			this.sizeConfigurator = new DefaultHasSizeConfigurator(instance.getGrid());
			this.styleConfigurator = new DefaultHasStyleConfigurator(instance.getGrid());
			this.enabledConfigurator = new DefaultHasEnabledConfigurator(instance.getGrid());
		}

		@Override
		public abstract C getConfigurator();

		protected L getInstance() {
			return instance;
		}

		/**
		 * Configure the listing.
		 * @return the listing
		 */
		protected L configureAndBuild() {

			// items
			if (!items.isEmpty()) {
				instance.getGrid().setItems(items);
			}

			// refresh listeners
			if (instance.getGrid().getDataProvider() != null) {
				refreshListeners.forEach(l -> instance.getGrid().getDataProvider().addDataProviderListener(e -> {
					l.onRefreshEvent(new DefaultItemListingRefreshEvent<>(instance,
							(e instanceof DataRefreshEvent) ? ((DataRefreshEvent<T>) e).getItem() : null));
				}));
			}

			// forzen columns
			if (frozenColumnsCount > 0) {
				int cnt = 0;
				for (P property : instance.getVisibleColumnProperties()) {
					cnt++;
					instance.getColumnConfiguration(property).setFrozen(cnt <= frozenColumnsCount);
				}
			}

			// editable
			if (editable) {
				instance.getEditor().setBuffered(editorBuffered);
			}

			// build
			instance.build(editable);

			// header and footer
			if (headerConfigurator != null) {
				headerConfigurator.accept(instance.getHeaderSection());
			}
			if (footerConfigurator != null) {
				footerConfigurator.accept(instance.getFooterSection());
			}

			return instance;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
		 */
		@Override
		public C width(String width) {
			sizeConfigurator.width(width);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public C height(String height) {
			sizeConfigurator.height(height);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public C styleNames(String... styleNames) {
			styleConfigurator.styleNames(styleNames);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public C styleName(String styleName) {
			styleConfigurator.styleName(styleName);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
		 */
		@Override
		public C removeStyleName(String styleName) {
			styleConfigurator.removeStyleName(styleName);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
		 */
		@Override
		public C replaceStyleName(String styleName) {
			styleConfigurator.replaceStyleName(styleName);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public C enabled(boolean enabled) {
			enabledConfigurator.enabled(enabled);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public C tabIndex(int tabIndex) {
			instance.getGrid().setTabIndex(tabIndex);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@SuppressWarnings("serial")
		@Override
		public C withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
			instance.getGrid().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<Grid<T>>>() {

				@Override
				public void onComponentEvent(FocusEvent<Grid<T>> event) {
					listener.onComponentEvent(new FocusEvent<Component>(event.getSource(), event.isFromClient()));
				}

			});
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@SuppressWarnings("serial")
		@Override
		public C withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
			instance.getGrid().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<Grid<T>>>() {

				@Override
				public void onComponentEvent(BlurEvent<Grid<T>> event) {
					listener.onComponentEvent(new BlurEvent<Component>(event.getSource(), event.isFromClient()));
				}

			});
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemsDataSourceConfigurator#dataSource(com.vaadin.flow.
		 * data.provider.DataProvider)
		 */
		@Override
		public C dataSource(DataProvider<T, Object> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "DataProvider must be not null");
			instance.getGrid().setDataProvider(dataProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
		 */
		@Override
		public C items(Iterable<T> items) {
			this.items = (items != null) ? ConversionUtils.iterableAsSet(items) : new HashSet<>();
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
		 */
		@Override
		public C addItem(T item) {
			ObjectUtils.argumentNotNull(item, "Item must be not null");
			this.items.add(item);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayAsFirst(java.lang.Object)
		 */
		@Override
		public C displayAsFirst(P property) {
			instance.setDisplayAsFirst(property);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayAsLast(java.lang.Object)
		 */
		@Override
		public C displayAsLast(P property) {
			instance.setDisplayAsLast(property);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayBefore(java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public C displayBefore(P property, P beforeProperty) {
			instance.setDisplayBefore(property, beforeProperty);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayAfter(java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public C displayAfter(P property, P afterProperty) {
			instance.setDisplayAfter(property, afterProperty);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#visibleColumns(java.util.List)
		 */
		@Override
		public C visibleColumns(List<? extends P> columns) {
			instance.setVisibleColumns(columns);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortable(boolean)
		 */
		@Override
		public C sortable(boolean sortable) {
			instance.getListingProperties().forEach(p -> instance.getColumnConfiguration(p)
					.setSortMode(sortable ? SortMode.ENABLED : SortMode.DISABLED));
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortable(java.lang.Object,
		 * boolean)
		 */
		@Override
		public C sortable(P property, boolean sortable) {
			instance.getColumnConfiguration(property).setSortMode(sortable ? SortMode.ENABLED : SortMode.DISABLED);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#resizable(boolean)
		 */
		@Override
		public C resizable(boolean resizable) {
			instance.getListingProperties().forEach(p -> instance.getColumnConfiguration(p).setResizable(resizable));
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#resizable(java.lang.Object,
		 * boolean)
		 */
		@Override
		public C resizable(P property, boolean resizable) {
			instance.getColumnConfiguration(property).setResizable(resizable);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#visible(java.lang.Object,
		 * boolean)
		 */
		@Override
		public C visible(P property, boolean visible) {
			instance.getColumnConfiguration(property).setVisible(visible);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#frozen(java.lang.Object,
		 * boolean)
		 */
		@Override
		public C frozen(P property, boolean frozen) {
			instance.getColumnConfiguration(property).setFrozen(frozen);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#frozenColumns(int)
		 */
		@Override
		public C frozenColumns(int frozenColumnsCount) {
			this.frozenColumnsCount = frozenColumnsCount;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#width(java.lang.Object,
		 * java.lang.String)
		 */
		@Override
		public C width(P property, String width) {
			instance.getColumnConfiguration(property).setWidth(width);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#flexGrow(java.lang.Object,
		 * int)
		 */
		@Override
		public C flexGrow(P property, int flexGrow) {
			instance.getColumnConfiguration(property).setFlexGrow(flexGrow);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#alignment(java.lang.Object,
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment)
		 */
		@Override
		public C alignment(P property, ColumnAlignment alignment) {
			instance.getColumnConfiguration(property).setAlignment(alignment);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#renderer(java.lang.Object,
		 * com.vaadin.flow.data.renderer.Renderer)
		 */
		@Override
		public C renderer(P property, Renderer<T> renderer) {
			instance.getColumnConfiguration(property).setRenderer(renderer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#valueProvider(java.lang.Object,
		 * com.vaadin.flow.function.ValueProvider)
		 */
		@Override
		public C valueProvider(P property, ValueProvider<T, String> valueProvider) {
			instance.getColumnConfiguration(property).setValueProvider(valueProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortComparator(java.lang.Object,
		 * java.util.Comparator)
		 */
		@Override
		public C sortComparator(P property, Comparator<T> comparator) {
			instance.getColumnConfiguration(property).setComparator(comparator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortUsing(java.lang.Object,
		 * java.util.List)
		 */
		@Override
		public C sortUsing(P property, List<P> sortProperties) {
			instance.getColumnConfiguration(property).setSortProperties(sortProperties);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortProvider(java.lang.Object,
		 * java.util.function.Function)
		 */
		@Override
		public C sortProvider(P property,
				Function<com.holonplatform.core.query.QuerySort.SortDirection, Stream<ItemSort<P>>> sortProvider) {
			ObjectUtils.argumentNotNull(sortProvider, "Sort provider must be not null");
			instance.getColumnConfiguration(property).setSortOrderProvider(direction -> {
				return sortProvider.apply(AbstractItemListing.convert(direction))
						.map(is -> new QuerySortOrder(instance.getColumnKey(is.getProperty()), direction));
			});
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#header(java.lang.Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public C header(P property, Localizable header) {
			instance.getColumnConfiguration(property).setHeaderText(header);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#headerComponent(java.lang.Object,
		 * com.vaadin.flow.component.Component)
		 */
		@Override
		public C headerComponent(P property, Component header) {
			instance.getColumnConfiguration(property).setHeaderComponent(header);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#pageSize(int)
		 */
		@Override
		public C pageSize(int pageSize) {
			instance.getGrid().setPageSize(pageSize);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#heightByRows(boolean)
		 */
		@Override
		public C heightByRows(boolean heightByRows) {
			instance.getGrid().setHeightByRows(heightByRows);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#columnReorderingAllowed(boolean)
		 */
		@Override
		public C columnReorderingAllowed(boolean columnReorderingAllowed) {
			instance.getGrid().setColumnReorderingAllowed(columnReorderingAllowed);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#itemDetailsRenderer(com.vaadin.flow
		 * .data.renderer.Renderer)
		 */
		@Override
		public C itemDetailsRenderer(Renderer<T> renderer) {
			instance.getGrid().setItemDetailsRenderer(renderer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#itemDetailsVisibleOnClick(boolean)
		 */
		@Override
		public C itemDetailsVisibleOnClick(boolean detailsVisibleOnClick) {
			instance.getGrid().setDetailsVisibleOnClick(detailsVisibleOnClick);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#selectionMode(com.holonplatform.
		 * vaadin.flow.components.Selectable.SelectionMode)
		 */
		@Override
		public C selectionMode(SelectionMode selectionMode) {
			instance.setSelectionMode(selectionMode);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public C withSelectionListener(SelectionListener<T> selectionListener) {
			instance.addSelectionListener(selectionListener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withItemClickListener(com.
		 * holonplatform.vaadin.flow.components.events.ClickEventListener)
		 */
		@Override
		public C withItemClickListener(
				ClickEventListener<ItemListing<T, P>, ItemClickEvent<ItemListing<T, P>, T>> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			instance.getGrid().addItemClickListener(e -> {
				listener.onClickEvent(
						new DefaultItemListingClickEvent<>(instance, e.isFromClient(), instance, () -> e.getItem()));
			});
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withItemRefreshListener(com.
		 * holonplatform.vaadin.flow.components.events.ItemListingRefreshListener)
		 */
		@Override
		public C withItemRefreshListener(ItemListingRefreshListener<T, P> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			refreshListeners.add(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#multiSort(boolean)
		 */
		@Override
		public C multiSort(boolean multiSort) {
			instance.getGrid().setMultiSort(multiSort);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#verticalScrollingEnabled(boolean)
		 */
		@Override
		public C verticalScrollingEnabled(boolean enabled) {
			instance.getGrid().setVerticalScrollingEnabled(enabled);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withThemeVariants(com.vaadin.flow.
		 * component.grid.GridVariant[])
		 */
		@Override
		public C withThemeVariants(GridVariant... variants) {
			instance.getGrid().addThemeVariants(variants);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#contextMenu()
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> contextMenu() {
			return new DefaultItemListingContextMenuBuilder<>(instance, instance.getGrid().addContextMenu(),
					getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#header(java.util.function.Consumer)
		 */
		@Override
		public C header(Consumer<EditableItemListingSection<P>> headerConfigurator) {
			this.headerConfigurator = headerConfigurator;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#footer(java.util.function.Consumer)
		 */
		@Override
		public C footer(Consumer<EditableItemListingSection<P>> footerConfigurator) {
			this.footerConfigurator = footerConfigurator;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#editable(boolean)
		 */
		@Override
		public C editable(boolean editable) {
			this.editable = editable;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#editorBuffered(boolean)
		 */
		@Override
		public C editorBuffered(boolean buffered) {
			this.editorBuffered = buffered;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorSaveListener(com.vaadin.
		 * flow.component.grid.editor.EditorSaveListener)
		 */
		@Override
		public C withEditorSaveListener(EditorSaveListener<T> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			this.editable = true;
			instance.getEditor().addSaveListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorCancelListener(com.vaadin
		 * .flow.component.grid.editor.EditorCancelListener)
		 */
		@Override
		public C withEditorCancelListener(EditorCancelListener<T> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			this.editable = true;
			instance.getEditor().addCancelListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorOpenListener(com.vaadin.
		 * flow.component.grid.editor.EditorOpenListener)
		 */
		@Override
		public C withEditorOpenListener(EditorOpenListener<T> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			this.editable = true;
			instance.getEditor().addOpenListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorCloseListener(com.vaadin.
		 * flow.component.grid.editor.EditorCloseListener)
		 */
		@Override
		public C withEditorCloseListener(EditorCloseListener<T> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			this.editable = true;
			instance.getEditor().addCloseListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withValidator(com.holonplatform.
		 * core.Validator)
		 */
		@Override
		public C withValidator(Validator<T> validator) {
			instance.addValidator(validator);
			return getConfigurator();
		}

	}

	private static class DefaultItemListingContextMenuBuilder<T, P, C extends ItemListingConfigurator<T, P, C>>
			extends AbstractComponentConfigurator<GridContextMenu<T>, ItemListingContextMenuBuilder<T, P, C>>
			implements ItemListingContextMenuBuilder<T, P, C> {

		private final ItemListing<T, P> itemListing;
		private final C parentBuilder;
		private final DefaultHasStyleConfigurator styleConfigurator;

		public DefaultItemListingContextMenuBuilder(ItemListing<T, P> itemListing, GridContextMenu<T> contextMenu,
				C parentBuilder) {
			super(contextMenu);
			this.itemListing = itemListing;
			this.parentBuilder = parentBuilder;
			this.styleConfigurator = new DefaultHasStyleConfigurator(contextMenu);

		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#openOnClick(boolean)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> openOnClick(boolean openOnClick) {
			getComponent().setOpenOnClick(openOnClick);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withOpenedChangeListener(com.vaadin
		 * .flow.component.ComponentEventListener)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> withOpenedChangeListener(
				ComponentEventListener<OpenedChangeEvent<GridContextMenu<T>>> listener) {
			getComponent().addOpenedChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> styleNames(String... styleNames) {
			styleConfigurator.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> styleName(String styleName) {
			styleConfigurator.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> removeStyleName(String styleName) {
			styleConfigurator.removeStyleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, C> replaceStyleName(String styleName) {
			styleConfigurator.replaceStyleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.holonplatform.core.
		 * i18n.Localizable)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, GridContextMenu<T>, ItemListingContextMenuBuilder<T, P, C>> withItem(
				Localizable text) {
			final ContextMenuItemListenerHandler<T, P> handler = new ContextMenuItemListenerHandler<>(itemListing);
			final MenuItem item = getComponent().addItem(LocalizationContext.translate(text, true), handler);
			return new ItemListingContextMenuItemBuilder<>(getConfigurator(), item, handler);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.vaadin.flow.component.
		 * Component)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, GridContextMenu<T>, ItemListingContextMenuBuilder<T, P, C>> withItem(
				Component component) {
			final ContextMenuItemListenerHandler<T, P> handler = new ContextMenuItemListenerHandler<>(itemListing);
			final MenuItem item = getComponent().addItem(component, handler);
			return new ItemListingContextMenuItemBuilder<>(getConfigurator(), item, handler);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingContextMenuBuilder#add()
		 */
		@Override
		public C add() {
			return parentBuilder;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
		 */
		@Override
		protected ItemListingContextMenuBuilder<T, P, C> getConfigurator() {
			return this;
		}
	}

	private static class ItemListingContextMenuItemBuilder<T, P, M extends ContextMenuBase<M>, B extends ContextMenuConfigurator<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, M, B>>
			implements MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, M, B> {

		private final B parentBuilder;
		private final MenuItem menuItem;
		private final ContextMenuItemListenerHandler<T, P> handler;

		public ItemListingContextMenuItemBuilder(B parentBuilder, MenuItem menuItem,
				ContextMenuItemListenerHandler<T, P> handler) {
			super();
			ObjectUtils.argumentNotNull(parentBuilder, "Parent builder must be not null");
			ObjectUtils.argumentNotNull(menuItem, "Menu item must be not null");
			this.parentBuilder = parentBuilder;
			this.menuItem = menuItem;
			this.handler = handler;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, M, B> id(
				String id) {
			menuItem.setId(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, M, B> enabled(
				boolean enabled) {
			menuItem.setEnabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, M, B> text(
				Localizable text) {
			menuItem.setText(LocalizationContext.translate(text, true));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder#withClickListener(
		 * java.util.EventListener)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>, M, B> withClickListener(
				ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>> menuItemClickListener) {
			ObjectUtils.argumentNotNull(menuItemClickListener, "Click listener must be not null");
			handler.addListener(menuItemClickListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder#add()
		 */
		@Override
		public B add() {
			return parentBuilder;
		}

	}

	@SuppressWarnings("serial")
	private static class ContextMenuItemListenerHandler<T, P>
			implements ComponentEventListener<GridContextMenuItemClickEvent<T>> {

		private final List<ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>>> listeners = new LinkedList<>();
		private final ItemListing<T, P> itemListing;

		public ContextMenuItemListenerHandler(ItemListing<T, P> itemListing) {
			super();
			this.itemListing = itemListing;
		}

		public void addListener(ItemEventListener<MenuItem, T, ItemListingItemEvent<MenuItem, T, P>> listener) {
			this.listeners.add(listener);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.vaadin.flow.component.ComponentEventListener#onComponentEvent(com.vaadin.flow.component.ComponentEvent)
		 */
		@Override
		public void onComponentEvent(GridContextMenuItemClickEvent<T> event) {
			listeners.forEach(l -> l.onItemEvent(new DefaultItemListingItemEvent<>(event.getSource(), itemListing,
					() -> event.getItem().orElse(null))));
		}

	}

	/**
	 * Default {@link ItemListingColumnBuilder} implementation.
	 * 
	 * @param <T> Item type
	 * @param <P> Item property type
	 * @param <B> Parent builder type
	 */
	static class DefaultItemListingColumnBuilder<T, P, B extends ItemListingConfigurator<T, P, B>>
			implements ItemListingColumnBuilder<T, P, B> {

		private final P property;
		private final AbstractItemListing<T, P> listing;
		private final B parent;

		public DefaultItemListingColumnBuilder(P property, AbstractItemListing<T, P> listing, B parent) {
			super();
			this.property = property;
			this.listing = listing;
			this.parent = parent;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * resizable(boolean)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> resizable(boolean resizable) {
			listing.getColumnConfiguration(property).setResizable(resizable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * visible(boolean)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> visible(boolean visible) {
			listing.getColumnConfiguration(property).setVisible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * frozen(boolean)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> frozen(boolean frozen) {
			listing.getColumnConfiguration(property).setFrozen(frozen);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#width
		 * (java.lang.String)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> width(String width) {
			listing.getColumnConfiguration(property).setWidth(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * flexGrow(int)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> flexGrow(int flexGrow) {
			listing.getColumnConfiguration(property).setFlexGrow(flexGrow);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * sortComparator(java.util.Comparator)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> sortComparator(Comparator<T> comparator) {
			listing.getColumnConfiguration(property).setComparator(comparator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * sortUsing(java.util.List)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> sortUsing(List<P> sortProperties) {
			listing.getColumnConfiguration(property).setSortProperties(sortProperties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * sortProvider(java.util.function.Function)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> sortProvider(
				Function<com.holonplatform.core.query.QuerySort.SortDirection, Stream<ItemSort<P>>> sortProvider) {
			listing.getColumnConfiguration(property).setSortOrderProvider(direction -> {
				return sortProvider.apply(AbstractItemListing.convert(direction))
						.map(is -> new QuerySortOrder(listing.getColumnKey(is.getProperty()), direction));
			});
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * header(com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> header(Localizable header) {
			listing.getColumnConfiguration(property).setHeaderText(header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * headerComponent(com.vaadin.flow.component.Component)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> headerComponent(Component header) {
			listing.getColumnConfiguration(property).setHeaderComponent(header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * displayAsFirst()
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> displayAsFirst() {
			listing.setDisplayAsFirst(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * displayAsLast()
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> displayAsLast() {
			listing.setDisplayAsLast(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * displayBefore(java.lang.Object)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> displayBefore(P beforeProperty) {
			listing.setDisplayBefore(property, beforeProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnConfigurator#
		 * displayAfter(java.lang.Object)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, B> displayAfter(P afterProperty) {
			listing.setDisplayAfter(property, afterProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnBuilder#add()
		 */
		@Override
		public B add() {
			return parent;
		}

	}

}
