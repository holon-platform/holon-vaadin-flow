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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Registration;
import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorComponentGroup;
import com.holonplatform.vaadin.flow.components.Validatable;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder;
import com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnPostProcessor;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingSection;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ItemListingContextMenuBuilder;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.components.events.ColumnReorderListener;
import com.holonplatform.vaadin.flow.components.events.ColumnResizeListener;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.components.events.ItemClickEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEventListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingDnDListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragEndEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragStartEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingDropEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingItemEvent;
import com.holonplatform.vaadin.flow.data.ItemListingDataProviderAdapter;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasEnabledConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasSizeConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasStyleConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultShortcutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultColumnReorderEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultColumnResizeEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultGroupValidationStatusEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultGroupValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemEditorEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingClickEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingDragEndEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingDragStartEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingDropEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultItemListingItemEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultSelectionEvent;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingFooterSection;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultItemListingHeaderSection;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultUserInputValidator;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.holonplatform.vaadin.flow.internal.utils.CollectionUtils;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.grid.GridNoneSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu.GridContextMenuItemClickEvent;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.contextmenu.GridSubMenu;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * Abstract {@link ItemListing} implementation using a {@link Grid}.
 *
 * @param <T> Item type
 * @param <P> Item property type
 * 
 * @since 5.2.0
 */
public abstract class AbstractItemListing<T, P> implements ItemListing<T, P>, EditorComponentGroup<P, T>,
		GroupValidationStatusHandler<EditorComponentGroup<P, T>, P, Input<?>> {

	private static final long serialVersionUID = 6298536849762717384L;

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Selection mode
	 */
	private SelectionMode selectionMode = SelectionMode.NONE;

	/**
	 * Optional visible columns
	 */
	private transient List<P> visibileColumns = Collections.emptyList();

	/**
	 * Grid
	 */
	private final Grid<T> grid;

	/**
	 * Data provider
	 */
	private ItemListingDataProviderAdapter<T, ?> dataProvider;

	/**
	 * A list of the item properties which correspond to a listing column, in the
	 * display order
	 */
	private final transient LinkedList<P> properties = new LinkedList<>();

	/**
	 * Optional {@link PropertyRendererRegistry} to use
	 */
	private transient PropertyRendererRegistry propertyRendererRegistry;

	/**
	 * Item property column definitions
	 */
	private final transient Map<P, ItemListingColumn<P, T, ?>> propertyColumns = new HashMap<>();

	/**
	 * Column headers.
	 */
	private final transient Map<P, String> columnsHeaders = new HashMap<>();

	/**
	 * Column key suffix generator to ensure unique column names
	 */
	private final AtomicInteger columnKeySuffix = new AtomicInteger(0);

	/**
	 * Property for which to expand the corresponding column
	 */
	private transient P propertyToExpand;

	/**
	 * Default auto-width for columns
	 */
	private boolean columnsAutoWidth = false;

	/**
	 * Listing columns post processors
	 */
	private final transient List<ColumnPostProcessor<P>> columnPostProcessors = new LinkedList<>();

	/**
	 * Whether the listing is editable
	 */
	private boolean editable = false;

	/**
	 * Property editors
	 */
	private final transient Map<P, Input<?>> editors = new LinkedHashMap<>();
	private final transient Map<com.vaadin.flow.data.binder.Binder.Binding<T, ?>, P> editorBindings = new HashMap<>();

	/**
	 * Property editor post-processors
	 */
	private final transient List<BiConsumer<P, Input<?>>> postProcessors = new LinkedList<>();

	/**
	 * Value change listeners
	 */
	private final List<ValueChangeListener<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>>> valueChangeListeners = new LinkedList<>();

	/**
	 * Item validators
	 */
	private final List<Validator<T>> validators = new LinkedList<>();

	/**
	 * Group validation status handler
	 */
	private GroupValidationStatusHandler<EditorComponentGroup<P, T>, P, Input<?>> groupValidationStatusHandler;

	/**
	 * Group value validation status handler
	 */
	private ValidationStatusHandler<EditorComponentGroup<P, T>> validationStatusHandler;

	/**
	 * Refresh property editors on value change
	 */
	private boolean enableRefreshOnValueChange = false;

	/**
	 * The previous editor value
	 */
	private transient T oldEditorValue;

	/**
	 * Selection listeners
	 */
	private final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	/**
	 * Selection listener registrations
	 */
	private final Map<SelectionListener<T>, com.vaadin.flow.shared.Registration> selectionListenerRegistrations = new HashMap<>(
			2);

	/**
	 * Editor open listeners
	 */
	private final List<EditorOpenListener<T, P>> editorOpenListeners = new LinkedList<>();
	/**
	 * Editor open listeners
	 */
	private final List<EditorCloseListener<T, P>> editorCloseListeners = new LinkedList<>();
	/**
	 * Editor open listeners
	 */
	private final List<EditorSaveListener<T, P>> editorSaveListeners = new LinkedList<>();
	/**
	 * Editor open listeners
	 */
	private final List<EditorCancelListener<T, P>> editorCancelListeners = new LinkedList<>();

	/**
	 * Whether the listing was built
	 */
	private boolean built = false;

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
	 * Get whether the listing was built.
	 * @return Whether the listing was built
	 */
	protected boolean isBuilt() {
		return built;
	}

	/**
	 * Get the {@link Grid} component.
	 * @return the grid component
	 */
	protected Grid<T> getGrid() {
		return grid;
	}

	/**
	 * Set the {@link DataProvider} to use for the backing Grid.
	 * @param dataProvider The data provider to set
	 */
	protected void setDataProvider(DataProvider<T, ?> dataProvider) {
		ObjectUtils.argumentNotNull(dataProvider, "DataProvider must be not null");
		this.dataProvider = ItemListingDataProviderAdapter.adapt(dataProvider);
		getGrid().setDataProvider(this.dataProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getDataProvider()
	 */
	@Override
	public DataProvider<T, ?> getDataProvider() {
		if (this.dataProvider != null) {
			return this.dataProvider;
		}
		return getGrid().getDataProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getColumnSorts()
	 */
	@Override
	public List<QuerySortOrder> getColumnSorts() {
		List<QuerySortOrder> sortProperties = new LinkedList<>();
		getGrid().getSortOrder().stream().map(order -> order.getSorted().getSortOrder(order.getDirection()))
				.forEach(s -> s.forEach(sortProperties::add));
		return sortProperties;
	}

	/**
	 * Get the {@link ItemListingDataProviderAdapter} type data provider, if
	 * available.
	 * @return Optional data provider
	 */
	protected Optional<ItemListingDataProviderAdapter<T, ?>> getItemListingDataProvider() {
		if (this.dataProvider != null) {
			return Optional.ofNullable(this.dataProvider);
		}
		DataProvider<T, ?> gridDataProvider = getGrid().getDataProvider();
		if (gridDataProvider != null && gridDataProvider instanceof ItemListingDataProviderAdapter) {
			return Optional.of((ItemListingDataProviderAdapter<T, ?>) gridDataProvider);
		}
		return Optional.empty();
	}

	/**
	 * Requires an {@link ItemListingDataProviderAdapter} type data provider
	 * @return The {@link ItemListingDataProviderAdapter} type data provider
	 * @throws IllegalStateException If a data provider is not available or it is
	 *                               not a {@link ItemListingDataProviderAdapter}
	 *                               data provider type
	 */
	protected ItemListingDataProviderAdapter<T, ?> requireItemListingDataProvider() {
		return getItemListingDataProvider().orElseThrow(() -> new IllegalStateException(
				"No suitable DataProvider available. The DataProvider should be a ItemListingDataProviderAdapter."));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return getGrid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.HasComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		getGrid().setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return getGrid().isVisible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasEnabled()
	 */
	@Override
	public Optional<HasEnabled> hasEnabled() {
		return Optional.of(getGrid());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasStyle()
	 */
	@Override
	public Optional<HasStyle> hasStyle() {
		return Optional.of(getGrid());
	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getHeader()
	 */
	@Override
	public Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getHeader() {
		return Optional.of(getHeaderSection());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#getFooter()
	 */
	@Override
	public Optional<ItemListingSection<P, ? extends ItemListingRow<P>>> getFooter() {
		return Optional.of(getFooterSection());
	}

	/**
	 * Get the specific {@link PropertyRendererRegistry} to use to render the
	 * editors.
	 * @return Optional property renderer registry
	 */
	protected Optional<PropertyRendererRegistry> getPropertyRendererRegistry() {
		return Optional.ofNullable(propertyRendererRegistry);
	}

	/**
	 * Set the specific {@link PropertyRendererRegistry} to use to render the
	 * editors.
	 * @param propertyRendererRegistry the property renderer registry to set
	 */
	protected void setPropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry) {
		this.propertyRendererRegistry = propertyRendererRegistry;
	}

	/**
	 * Get the column configuration for given property.
	 * @param property The property (not null)
	 * @return The property column configuration (never null)
	 */
	public ItemListingColumn<P, T, ?> getColumnConfiguration(P property) {
		return propertyColumns.computeIfAbsent(property, p -> new DefaultItemListingColumn<>(property,
				ensureUniqueColumnKey(generateColumnKey(p)), isReadOnlyByDefault(p)));
	}

	/**
	 * Get the column key which corresponds to given item property id.
	 * @param property The item property id for which to obtain the column key
	 * @return The column key (not null)
	 */
	protected abstract String generateColumnKey(P property);

	/**
	 * Gets whether the given property is read-only by default.
	 * @param property The property
	 * @return whether the given property is read-only by default
	 */
	protected abstract boolean isReadOnlyByDefault(P property);

	/**
	 * Get the listing property set.
	 * @return the property set
	 */
	protected List<P> getListingProperties() {
		return properties;
	}

	/**
	 * Ensure unique column key, appending a numeric suffix to duplicate key names
	 * if required.
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
	 * @param columnKey The column key (not null)
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
	 * 
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
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#setColumnVisible(java.
	 * lang.Object, boolean)
	 */
	@Override
	public void setColumnVisible(P property, boolean visible) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getColumn(property)
				.orElseThrow(
						() -> new IllegalArgumentException("No column is bound to the property [" + property + "]"))
				.setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#getColumnHeader(java.
	 * lang.Object)
	 */
	@Override
	public Optional<String> getColumnHeader(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(columnsHeaders.get(property));
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
	 * Add an item property to be rendered as a listing column before the
	 * <code>beforeProperty</code> property id.
	 * @param property       The item property id to add (not null)
	 * @param beforeProperty The property before to add the item property
	 * @return <code>true</code> if the <code>beforeProperty</code> is available. If
	 *         <code>false</code>, the property column will be added at the end of
	 *         the list
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
	 * Add an item property to be rendered as a listing column after the
	 * <code>afterProperty</code> property id.
	 * @param property      The item property id to add (not null)
	 * @param afterProperty The property after to add the item property
	 * @return <code>true</code> if the <code>afterProperty</code> is available. If
	 *         <code>false</code>, the property column will be added at the end of
	 *         the list
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
	 * @return <code>true</code> if the property is in the property set and was
	 *         moved within the list
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
	 * @return <code>true</code> if the property is in the property set and was
	 *         moved within the list
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
	 * Set to display given property before the provided
	 * <code>beforeProperty</code>.
	 * @param property       The property
	 * @param beforeProperty The property before to display the property
	 * @return <code>true</code> if the property is in the property set and was
	 *         moved within the list
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
	 * @param property      The property
	 * @param afterProperty The property after to display the property
	 * @return <code>true</code> if the property is in the property set and was
	 *         moved within the list
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
					throw new IllegalArgumentException("The property [" + property
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
	 * Get the property for which to expand the corresponding column.
	 * @return the property to expand
	 */
	protected P getPropertyToExpand() {
		return propertyToExpand;
	}

	/**
	 * Set the property for which to expand the corresponding column.
	 * @param propertyToExpand the property to set
	 */
	protected void setPropertyToExpand(P propertyToExpand) {
		this.propertyToExpand = propertyToExpand;
	}

	/**
	 * Get whether to apply automatic width for all the columns.
	 * @return Whether to apply automatic width for all the columns.
	 */
	protected boolean isColumnsAutoWidth() {
		return columnsAutoWidth;
	}

	/**
	 * Set whether to apply automatic width for all the columns.
	 * @param columnsAutoWidth Whether to apply automatic width for all the columns
	 */
	protected void setColumnsAutoWidth(boolean columnsAutoWidth) {
		this.columnsAutoWidth = columnsAutoWidth;
	}

	/**
	 * Build the listing, adding a Grid column for each item property and setting up
	 * the item editor if <code>editable</code> is <code>true</code>.
	 * @param editable Whether the listing is editable
	 */
	public void build(boolean editable) {
		this.editable = editable;
		// remove all columns
		getGrid().getColumns().forEach(column -> getGrid().removeColumn(column));
		columnsHeaders.clear();
		// add a column for each visible property
		getVisibleColumnProperties().forEach(property -> addGridColumn(property));
		// selection listeners
		setupSelectionListeners();
		// check init editor
		if (editable) {
			initEditor(getVisibleColumnProperties());
		}
		// built
		this.built = true;
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
		// configure the column
		column.setKey(configuration.getColumnKey());
		// header
		getColumnHeader(configuration).flatMap(t -> LocalizationProvider.localize(t)).ifPresent(h -> {
			column.setHeader(h);
			columnsHeaders.put(property, h);
		});
		configuration.getHeaderComponent().ifPresent(c -> column.setHeader(c));
		// footer
		configuration.getFooterText().flatMap(t -> LocalizationProvider.localize(t))
				.ifPresent(f -> column.setFooter(f));
		configuration.getFooterComponent().ifPresent(c -> column.setFooter(c));
		// visible
		column.setVisible(configuration.isVisible());
		// resizable
		column.setResizable(configuration.isResizable());
		// frozen
		column.setFrozen(configuration.isFrozen());
		// width
		configuration.getWidth().ifPresent(w -> column.setWidth(w));
		if (!configuration.getWidth().isPresent() && (configuration.isAutoWidth() || isColumnsAutoWidth())) {
			column.setAutoWidth(true);
		}
		// flex grow
		if (getPropertyToExpand() != null) {
			if (property.equals(getPropertyToExpand())) {
				column.setFlexGrow(1);
			} else {
				column.setFlexGrow(0);
			}
		} else {
			column.setFlexGrow(configuration.getFlexGrow());
		}
		// style class name
		configuration.getStyleNameGenerator().ifPresent(g -> column.setClassNameGenerator(item -> g.apply(item)));
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
			if (configuration.getSortMode() == SortMode.DISABLED) {
				column.setSortable(false);
			}
		}
		// post processors
		final String columnHeader = columnsHeaders.getOrDefault(property, "");
		final ColumnConfigurator columnConfigurator = new DefaultColumnConfigurator<>(column);
		getColumnPostProcessors().forEach(cpp -> {
			cpp.configureColumn(property, columnHeader, columnConfigurator);
		});
		// return the column key
		return configuration.getColumnKey();
	}

	private static final class DefaultColumnConfigurator<T> implements ColumnConfigurator {

		private final Column<T> column;

		public DefaultColumnConfigurator(Column<T> column) {
			super();
			this.column = column;
		}

		@Override
		public ColumnConfigurator resizable(boolean resizable) {
			column.setResizable(resizable);
			return this;
		}

		@Override
		public ColumnConfigurator visible(boolean visible) {
			column.setVisible(visible);
			return this;
		}

		@Override
		public ColumnConfigurator width(String width) {
			if (width != null) {
				column.setWidth(width);
			}
			return this;
		}

		@Override
		public ColumnConfigurator flexGrow(int flexGrow) {
			column.setFlexGrow(flexGrow);
			return this;
		}

		@Override
		public ColumnConfigurator autoWidth(boolean autoWidth) {
			column.setAutoWidth(autoWidth);
			return this;
		}

		@Override
		public ColumnConfigurator alignment(ColumnAlignment alignment) {
			if (alignment != null) {
				column.setTextAlign(asColumnTextAlign(alignment));
			}
			return this;
		}

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
	 * Get the {@link QuerySortOrder} property name for given property, if
	 * available.
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
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#isItemDetailsVisible(
	 * java.lang.Object)
	 */
	@Override
	public boolean isItemDetailsVisible(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		return getGrid().isDetailsVisible(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#setItemDetailsVisible(
	 * java.lang.Object, boolean)
	 */
	@Override
	public void setItemDetailsVisible(T item, boolean visible) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		getGrid().setDetailsVisible(item, visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#sort(java.util.List)
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
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		// check editing
		if (isEditable()) {
			cancelEditing();
		}
		// check frozen
		getItemListingDataProvider().ifPresent(p -> {
			p.setFrozen(false);
		});
		// refresh
		getGrid().getDataProvider().refreshAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#refreshItem(java.lang.
	 * Object)
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

	@Override
	public void recalculateColumnWidths() {
		getGrid().recalculateColumnWidths();
	}

	@Override
	public void scrollToIndex(int rowIndex) {
		getGrid().scrollToIndex(rowIndex);
	}

	@Override
	public void scrollToStart() {
		getGrid().scrollToStart();
	}

	@Override
	public void scrollToEnd() {
		getGrid().scrollToEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#isFrozen()
	 */
	@Override
	public boolean isFrozen() {
		return getItemListingDataProvider().map(p -> p.isFrozen()).orElse(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#setFrozen(boolean)
	 */
	@Override
	public void setFrozen(boolean frozen) {
		requireItemListingDataProvider().setFrozen(frozen);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#getAdditionalItems()
	 */
	@Override
	public List<T> getAdditionalItems() {
		return requireItemListingDataProvider().getAdditionalItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#addAdditionalItem(java.
	 * lang.Object)
	 */
	@Override
	public void addAdditionalItem(T item) {
		requireItemListingDataProvider().addAdditionalItem(item);
		// ensure item key is created and registered
		// this allows to handle the new item without errors, for example to use it in
		// grid editor just after it's added
		getGrid().getDataCommunicator().getKeyMapper().key(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#removeAdditionalItem(
	 * java.lang.Object)
	 */
	@Override
	public boolean removeAdditionalItem(T item) {
		// ensure editor closed
		boolean buffered = getEditor().isBuffered();
		if (getEditor().isOpen()) {
			if (buffered) {
				getEditor().setBuffered(false);
			}
			getEditor().closeEditor();
		}
		// remove item
		boolean removed = requireItemListingDataProvider().removeAdditionalItem(item);
		// restore buffered
		if (buffered && !getEditor().isBuffered()) {
			getEditor().setBuffered(true);
		}
		return removed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#removeAdditionalItems()
	 */
	@Override
	public void removeAdditionalItems() {
		// ensure editor closed
		boolean buffered = getEditor().isBuffered();
		if (getEditor().isOpen()) {
			if (buffered) {
				getEditor().setBuffered(false);
			}
			getEditor().closeEditor();
		}
		// remove items
		requireItemListingDataProvider().removeAdditionalItems();
		// restore buffered
		if (buffered && !getEditor().isBuffered()) {
			getEditor().setBuffered(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		return getGrid().getSelectedItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	public Optional<T> getFirstSelectedItem() {
		return getGrid().getSelectedItems().stream().findFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		if (SelectionMode.NONE == getSelectionMode()) {
			throw new IllegalStateException("The listing selection mode is NONE: no selection is allowed");
		}
		ObjectUtils.argumentNotNull(item, "Item to select must be not null");
		getGrid().select(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselect(java.lang.
	 * Object)
	 */
	@Override
	public void deselect(T item) {
		if (SelectionMode.NONE == getSelectionMode()) {
			throw new IllegalStateException("The listing selection mode is NONE: no selection is allowed");
		}
		ObjectUtils.argumentNotNull(item, "Item to deselect must be not null");
		getGrid().deselect(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		getGrid().deselectAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectionMode()
	 */
	@Override
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#setSelectionMode(com.
	 * holonplatform.vaadin.flow.components. Selectable.SelectionMode)
	 */
	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		ObjectUtils.argumentNotNull(selectionMode, "Selection mode must be not null");
		this.selectionMode = selectionMode;
		getGrid().setSelectionMode(asGridSelectionMode(selectionMode));
		// check built
		if (isBuilt()) {
			setupSelectionListeners();
		}
	}

	/**
	 * Sets the select all checkbox visibility mode.
	 * @param selectAllCheckBoxVisibility the visiblity mode to use
	 */
	public void setSelectAllCheckboxVisibility(SelectAllCheckboxVisibility selectAllCheckBoxVisibility) {
		if (selectAllCheckBoxVisibility != null && getGrid().getSelectionModel() instanceof GridMultiSelectionModel) {
			((GridMultiSelectionModel<T>) getGrid().getSelectionModel())
					.setSelectAllCheckboxVisibility(selectAllCheckBoxVisibility);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.Selectable#addSelectionListener(com.
	 * holonplatform.vaadin.flow.components .Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		this.selectionListeners.add(selectionListener);
		// check built
		if (isBuilt() && isSelectableSelectionModel()) {
			addAndRegisterSelectionListener(selectionListener);
		}
		return () -> {
			final com.vaadin.flow.shared.Registration registration = this.selectionListenerRegistrations
					.get(selectionListener);
			if (registration != null) {
				registration.remove();
			}
		};
	}

	/**
	 * Setup and register selection listeners for current selection model.
	 */
	protected void setupSelectionListeners() {
		this.selectionListenerRegistrations.values().forEach(r -> {
			if (r != null) {
				r.remove();
			}
		});
		this.selectionListenerRegistrations.clear();
		if (isSelectableSelectionModel()) {
			this.selectionListeners.forEach(selectionListener -> {
				addAndRegisterSelectionListener(selectionListener);
			});
		}
	}

	/**
	 * Get whether the current selection model is valid and is not
	 * <code>NONE</code>.
	 * @return Whether the current selection model is valid and is not
	 *         <code>NONE</code>
	 */
	private boolean isSelectableSelectionModel() {
		final GridSelectionModel<T> selectionModel = getGrid().getSelectionModel();
		if (selectionModel != null && !(selectionModel instanceof GridNoneSelectionModel)) {
			return true;
		}
		return false;
	}

	/**
	 * Add the selection listener to the current Grid selection model.
	 * @param selectionListener The selection listener to add
	 */
	private void addAndRegisterSelectionListener(SelectionListener<T> selectionListener) {
		final com.vaadin.flow.shared.Registration registration = getGrid().addSelectionListener(e -> {
			selectionListener.onSelectionChange(new DefaultSelectionEvent<>(e.getAllSelectedItems(), e.isFromClient()));
		});
		this.selectionListenerRegistrations.put(selectionListener, registration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#isEditing()
	 */
	@Override
	public Optional<T> isEditing() {
		if (isEditable()) {
			return Optional.ofNullable(getEditor().getItem());
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#editItem(java.lang.
	 * Object)
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
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ItemListing#saveEditingItem()
	 */
	@Override
	public boolean saveEditingItem() {
		if (!isEditable()) {
			throw new IllegalStateException("The item listing is not editable");
		}
		return getEditor().save();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing#refreshEditingItem()
	 */
	@Override
	public void refreshEditingItem() {
		if (!isEditable()) {
			throw new IllegalStateException("The item listing is not editable");
		}
		getEditor().refresh();
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
	 * Add a column post processor.
	 * @param columnPostProcessor The column post processor to add (not null)
	 */
	protected void addColumnPostProcessor(ColumnPostProcessor<P> columnPostProcessor) {
		ObjectUtils.argumentNotNull(columnPostProcessor, "Column post-processor must be not null");
		this.columnPostProcessors.add(columnPostProcessor);
	}

	/**
	 * Get the column post processors.
	 * @return the column post processors
	 */
	protected List<ColumnPostProcessor<P>> getColumnPostProcessors() {
		return columnPostProcessors;
	}

	/**
	 * Add a editor post processor.
	 * @param postProcessor The editor post processor to add (not null)
	 */
	protected void addEditorPostProcessor(BiConsumer<P, Input<?>> postProcessor) {
		ObjectUtils.argumentNotNull(postProcessor, "Editor post-processor must be not null");
		this.postProcessors.add(postProcessor);
	}

	/**
	 * Get the editors post processors.
	 * @return the editors post processors
	 */
	protected List<BiConsumer<P, Input<?>>> getEditorPostProcessors() {
		return postProcessors;
	}

	/**
	 * Add a new {@link EditorOpenListener}.
	 * @param listener The listener to add
	 */
	protected void addEditorOpenListener(EditorOpenListener<T, P> listener) {
		ObjectUtils.argumentNotNull(listener, "Editor listener must be not null");
		editorOpenListeners.add(listener);
	}

	/**
	 * Add a new {@link EditorCloseListener}.
	 * @param listener The listener to add
	 */
	protected void addEditorCloseListener(EditorCloseListener<T, P> listener) {
		ObjectUtils.argumentNotNull(listener, "Editor listener must be not null");
		editorCloseListeners.add(listener);
	}

	/**
	 * Add a new {@link EditorSaveListener}.
	 * @param listener The listener to add
	 */
	protected void addEditorSaveListener(EditorSaveListener<T, P> listener) {
		ObjectUtils.argumentNotNull(listener, "Editor listener must be not null");
		editorSaveListeners.add(listener);
	}

	/**
	 * Add a new {@link EditorCancelListener}.
	 * @param listener The listener to add
	 */
	protected void addEditorCancelListener(EditorCancelListener<T, P> listener) {
		ObjectUtils.argumentNotNull(listener, "Editor listener must be not null");
		editorCancelListeners.add(listener);
	}

	/**
	 * Add a editor value change listener.
	 * @param valueChangeListener the value change listener to add (not null)
	 * @return The listener registration
	 */
	protected Registration addValueChangeListener(
			ValueChangeListener<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>> valueChangeListener) {
		ObjectUtils.argumentNotNull(valueChangeListener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(valueChangeListener);
		return () -> valueChangeListeners.remove(valueChangeListener);
	}

	/**
	 * Get the editor value change listeners.
	 * @return the editor value change listeners
	 */
	protected List<ValueChangeListener<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	/**
	 * Get the listing {@link ValidationStatusHandler}, if available.
	 * @return Optional listing {@link ValidationStatusHandler}
	 */
	protected Optional<ValidationStatusHandler<EditorComponentGroup<P, T>>> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

	/**
	 * Set the listing {@link ValidationStatusHandler}.
	 * @param validationStatusHandler the {@link ValidationStatusHandler} to set
	 */
	protected void setValidationStatusHandler(
			ValidationStatusHandler<EditorComponentGroup<P, T>> validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/**
	 * Get the group validation status handler to use.
	 * @return Optional group validation status handler
	 */
	protected Optional<GroupValidationStatusHandler<EditorComponentGroup<P, T>, P, Input<?>>> getGroupValidationStatusHandler() {
		return Optional.ofNullable(groupValidationStatusHandler);
	}

	/**
	 * Set the group validation status handler to use.
	 * @param groupValidationStatusHandler the group validation status handler to
	 *                                     set
	 */
	protected void setGroupValidationStatusHandler(
			GroupValidationStatusHandler<EditorComponentGroup<P, T>, P, Input<?>> groupValidationStatusHandler) {
		this.groupValidationStatusHandler = groupValidationStatusHandler;
	}

	/**
	 * Get whether to refresh the property editors when an Input value changes.
	 * @return whether to refresh the property editors when an Input value changes
	 */
	protected boolean isEnableRefreshOnValueChange() {
		return enableRefreshOnValueChange;
	}

	/**
	 * Set whether to refresh the property editors when an Input value changes
	 * @param enableRefreshOnValueChange whether to refresh the property editors
	 *                                   when an Input value changes
	 */
	protected void setEnableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
		this.enableRefreshOnValueChange = enableRefreshOnValueChange;
	}

	/**
	 * Init the grid editor
	 * @param properties Visible properties
	 */
	protected void initEditor(List<P> properties) {
		editors.clear();
		editorBindings.clear();
		// property set
		final Map<String, PropertyDefinition<T, ?>> definitions = new HashMap<>(properties.size());
		final PropertySet<T> propertySet = new ItemListingPropertySet<>(definitions);
		for (P property : properties) {
			final ItemListingColumn<P, T, ?> configuration = getColumnConfiguration(property);
			// exclude read-only
			if (!configuration.isReadOnly()) {
				definitions.put(configuration.getColumnKey(),
						ItemListingPropertyDefinition.create(propertySet, configuration.getColumnKey(),
								getPropertyType(property), getPropertyValueGetter(property),
								getPropertyValueSetter(property).orElse(null)));
			}
		}
		// binder
		final Binder<T> binder = new DefaultItemListingBinder<>(new ItemListingPropertySet<>(definitions));
		// validation status handler
		binder.setValidationStatusHandler(e -> {
			getGroupValidationStatusHandler().orElse(this).validationStatusChange(asGroupValidationStatus(e));
		});
		// item validators
		validators.forEach(validator -> binder.withValidator(Validatable.adapt(validator)));
		// property editors
		for (P property : properties) {
			final ItemListingColumn<P, T, ?> configuration = getColumnConfiguration(property);
			if (!configuration.isReadOnly()) {
				// editor component
				if (configuration.getEditorComponent().isPresent()) {
					getColumn(property).ifPresent(column -> {
						// set the column editor
						column.setEditorComponent(i -> configuration.getEditorComponent().get().apply(i));
					});
				} else {
					// editor input
					getColumn(property).ifPresent(column -> {
						buildPropertyEditor(configuration).ifPresent(editor -> {
							editors.put(property, editor);
							// remove label
							editor.hasLabel().ifPresent(l -> l.setLabel(""));
							// set the column editor
							column.setEditorComponent(editor.getComponent());
							// configure and bind
							editorBindings.put(configureAndBind(binder, configuration, editor), property);
						});
					});
				}
			}
		}
		// set the binder as Editor Binder
		getEditor().setBinder(binder);
		// value change
		getEditor().addOpenListener(e -> {
			this.oldEditorValue = e.getItem();
			// fire listeners
			final ItemEditorEvent<T, P> event = createItemEditorEvent(e.getSource(), e.getItem());
			editorOpenListeners.forEach(l -> l.onEditorOpen(event));
		});
		getEditor().addCloseListener(e -> {
			this.oldEditorValue = null;
			// fire listeners
			final ItemEditorEvent<T, P> event = createItemEditorEvent(e.getSource(), e.getItem());
			editorCloseListeners.forEach(l -> l.onEditorClose(event));
		});
		getEditor().addSaveListener(e -> {
			fireValueChangeListeners(e.getItem());
			// fire listeners
			final ItemEditorEvent<T, P> event = createItemEditorEvent(e.getSource(), e.getItem());
			editorSaveListeners.forEach(l -> l.onEditorSave(event));
		});
		getEditor().addCancelListener(e -> {
			// fire listeners
			final ItemEditorEvent<T, P> event = createItemEditorEvent(e.getSource(), e.getItem());
			editorCancelListeners.forEach(l -> l.onEditorCancel(event));
		});
	}

	private ItemEditorEvent<T, P> createItemEditorEvent(Editor<T> editor, T item) {
		return new DefaultItemEditorEvent<>(this, editor, item, () -> editors);
	}

	protected EditorComponentGroup<P, T> getEditorComponentGroup() {
		return this;
	}

	/**
	 * Configure the {@link Input} editor component using given configuration and
	 * bind it to the editor Binder.
	 * @param binder        The editor Binder
	 * @param configuration Property column configuration (not null)
	 * @param input         The {@link Input} component to configure
	 * @return The editor binding
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected com.vaadin.flow.data.binder.Binder.Binding<T, ?> configureAndBind(Binder<T> binder,
			ItemListingColumn<P, T, ?> configuration, final Input<?> input) {
		return configureInput(binder, (ItemListingColumn) configuration, input).bind(configuration.getColumnKey());
	}

	/**
	 * Configure the {@link Input} editor component using given configuration.
	 * @param <V>           Input type
	 * @param binder        The editor Binder
	 * @param configuration Property column configuration (not null)
	 * @param input         The {@link Input} component to configure
	 * @return The editor binding builder
	 */
	protected <V> BindingBuilder<T, V> configureInput(Binder<T> binder, ItemListingColumn<P, T, V> configuration,
			final Input<V> input) {
		final BindingBuilder<T, V> binding = binder.forField(input.asHasValue());
		// value change listeners
		configuration.getValueChangeListeners().forEach(vcl -> input.addValueChangeListener(e -> {
			vcl.valueChange(new DefaultGroupValueChangeEvent<>(this, e.getSource(), e.getOldValue(), e.getValue(),
					e.isUserOriginated(), configuration.getProperty(), input));
		}));
		// Refresh on value change
		if (isEnableRefreshOnValueChange()) {
			input.addValueChangeListener(e -> refreshVirtualProperties());
		}
		// required validator
		if (configuration.isRequired()) {
			input.setRequired(true);
			final RequiredInputValidator<V> requiredValidator = configuration.getRequiredMessage()
					.map(rm -> RequiredInputValidator.create(input, rm))
					.orElseGet(() -> RequiredInputValidator.create(input));
			binding.withValidator(Validatable.adapt(requiredValidator));
		}
		// user input validator
		input.hasInvalidChangeEventNotifier().ifPresent(n -> {
			final DefaultUserInputValidator<V> uiv = new DefaultUserInputValidator<>();
			n.addInvalidChangeListener(uiv);
			binding.withValidator(Validatable.adapt(uiv));
		});
		// property validators
		getDefaultPropertyValidators(configuration.getProperty())
				.forEach(validator -> binding.withValidator(Validatable.adapt(validator)));
		// additional validators
		configuration.getValidators().forEach(validator -> binding.withValidator(Validatable.adapt(validator)));
		// post-processors
		getEditorPostProcessors().forEach(postProcessor -> postProcessor.accept(configuration.getProperty(), input));
		// done
		return binding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler#
	 * validationStatusChange(com.holonplatform.
	 * vaadin.flow.components.GroupValidationStatusHandler.
	 * GroupValidationStatusEvent)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void validationStatusChange(
			GroupValidationStatusEvent<EditorComponentGroup<P, T>, P, Input<?>> statusChangeEvent) {
		// group elements
		statusChangeEvent.getInputsValidationStatus().forEach(s -> {
			getColumnConfiguration(s.getProperty()).getValidationStatusHandler()
					.orElseGet(() -> ValidationStatusHandler.getDefault(ValidationStatusHandler.dialog()))
					.validationStatusChange(
							ValidationStatusEvent.create((Input) s.getElement(), s.getStatus(), s.getErrors()));
		});
		// group
		final ValidationStatusHandler<EditorComponentGroup<P, T>> groupHandler = getValidationStatusHandler()
				.orElseGet(() -> ValidationStatusHandler.dialog());
		switch (statusChangeEvent.getGroupStatus()) {
		case INVALID:
			groupHandler.validationStatusChange(
					ValidationStatusEvent.invalid(getEditorComponentGroup(), statusChangeEvent.getGroupErrors()));
			break;
		case VALID:
			groupHandler.validationStatusChange(ValidationStatusEvent.valid(getEditorComponentGroup()));
			break;
		case UNRESOLVED:
		default:
			groupHandler.validationStatusChange(ValidationStatusEvent.unresolved(getEditorComponentGroup()));
			break;
		}
	}

	/**
	 * Convert given binder validation status to a
	 * {@link GroupValidationStatusEvent}.
	 * @param binderStatus The binder validation status
	 * @return The {@link GroupValidationStatusEvent}
	 */
	private GroupValidationStatusEvent<EditorComponentGroup<P, T>, P, Input<?>> asGroupValidationStatus(
			BinderValidationStatus<T> binderStatus) {
		// inputs
		final List<GroupElementValidationStatusEvent<EditorComponentGroup<P, T>, P, Input<?>>> inputsValidationStatus = binderStatus
				.getFieldValidationStatuses().stream().map(vs -> {
					final P property = editorBindings.get(vs.getBinding());
					final Input<?> input = editors.get(property);
					switch (vs.getStatus()) {
					case ERROR:
						return GroupElementValidationStatusEvent.<EditorComponentGroup<P, T>, P, Input<?>>invalid(
								getEditorComponentGroup(), property, input,
								vs.getValidationResults().stream().filter(ValidationResult::isError)
										.map(r -> r.getErrorMessage()).filter(m -> m != null && !m.trim().equals(""))
										.map(m -> Localizable.of(m)).collect(Collectors.toList()));
					case OK:
						return GroupElementValidationStatusEvent.<EditorComponentGroup<P, T>, P, Input<?>>valid(
								getEditorComponentGroup(), property, input);
					case UNRESOLVED:
					default:
						return GroupElementValidationStatusEvent.<EditorComponentGroup<P, T>, P, Input<?>>unresolved(
								getEditorComponentGroup(), property, input);
					}
				}).collect(Collectors.toList());

		// group
		final Status groupStatus;
		final List<Localizable> errors;
		if (!binderStatus.getBeanValidationErrors().isEmpty()) {
			groupStatus = Status.INVALID;
			errors = binderStatus.getBeanValidationErrors().stream().map(r -> r.getErrorMessage())
					.filter(m -> m != null && !m.trim().equals("")).map(m -> Localizable.of(m))
					.collect(Collectors.toList());
		} else {
			errors = Collections.emptyList();
			// check unresolved
			if (binderStatus.getFieldValidationStatuses().stream().allMatch(
					s -> com.vaadin.flow.data.binder.BindingValidationStatus.Status.UNRESOLVED == s.getStatus())) {
				groupStatus = Status.UNRESOLVED;
			} else {
				groupStatus = Status.VALID;
			}
		}
		return new DefaultGroupValidationStatusEvent<>(this, groupStatus, errors, inputsValidationStatus);
	}

	/**
	 * Fire the group value change listeners.
	 * @param value The new value
	 */
	protected void fireValueChangeListeners(T value) {
		@SuppressWarnings("serial")
		final ValueHolder<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>> vh = new ValueHolder<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>>() {

			@Override
			public void setValue(T value) {
				editItem(value);
			}

			@Override
			public T getValue() {
				return isEditing().orElse(null);
			}

			@Override
			public Registration addValueChangeListener(
					ValueChangeListener<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>> listener) {
				return AbstractItemListing.this.addValueChangeListener(listener);
			}

		};
		final GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>> event = new DefaultGroupValueChangeEvent<>(
				this, vh, oldEditorValue, value, true);
		valueChangeListeners.forEach(l -> l.valueChange(event));
	}

	/**
	 * Refresh all the editor inputs bound to a {@link VirtualProperty}.
	 */
	protected abstract void refreshVirtualProperties();

	/**
	 * Build and obtain the property editor to use with given property, if
	 * available.
	 * @param <V>           Property value type
	 * @param configuration The property column configuration
	 * @return Optional property editor
	 */
	protected abstract <V> Optional<Input<V>> buildPropertyEditor(ItemListingColumn<P, T, V> configuration);

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

	// ------- EditorComponentGroup

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.ComponentGroup#getElements()
	 */
	@Override
	public Stream<Input<?>> getElements() {
		return editors.values().stream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.BoundComponentGroup#getElement(java.
	 * lang.Object)
	 */
	@Override
	public Optional<Input<?>> getElement(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return Optional.ofNullable(editors.get(property));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.BoundComponentGroup#getBindings()
	 */
	@Override
	public Stream<Binding<P, Input<?>>> getBindings() {
		return editors.entrySet().stream().filter(e -> e.getValue() != null)
				.map(e -> Binding.create(e.getKey(), e.getValue()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.ItemListing.EditorComponentGroup#
	 * getItem()
	 */
	@Override
	public T getItem() {
		return getEditor().getItem();
	}

	// ------- utility methods

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
	 * Convert given {@link SortDirection} into a
	 * {@link com.holonplatform.core.query.QuerySort.SortDirection}.
	 * @param direction Sort direction to convert
	 * @return Converted sort direction
	 */
	protected static com.holonplatform.core.query.QuerySort.SortDirection convert(SortDirection direction) {
		switch (direction) {
		case DESCENDING:
			return com.holonplatform.core.query.QuerySort.SortDirection.DESCENDING;
		case ASCENDING:
		default:
			return com.holonplatform.core.query.QuerySort.SortDirection.ASCENDING;
		}
	}

	// ------- support classes

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

		@SuppressWarnings({ "unchecked", "rawtypes" })
		static <T> ItemListingPropertyDefinition<T, ?> create(PropertySet<T> propertySet, String name, Class<?> type,
				ValueProvider<T, ?> getter, Setter<T, ?> setter) {
			return new ItemListingPropertyDefinition<>(propertySet, name, (Class) type, (ValueProvider) getter,
					(Setter) setter);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getPropertyHolderType()
		 */
		@Override
		public Class<?> getPropertyHolderType() {
			return ItemListingPropertySet.class;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getPropertySet()
		 */
		@Override
		public PropertySet<T> getPropertySet() {
			return propertySet;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getName()
		 */
		@Override
		public String getName() {
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getGetter()
		 */
		@Override
		public ValueProvider<T, V> getGetter() {
			return getter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getSetter()
		 */
		@Override
		public Optional<Setter<T, V>> getSetter() {
			return Optional.ofNullable(setter);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getType()
		 */
		@Override
		public Class<V> getType() {
			return type;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getCaption()
		 */
		@Override
		public String getCaption() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.PropertyDefinition#getParent()
		 */
		@Override
		public PropertyDefinition<T, ?> getParent() {
			return null;
		}

	}

	/**
	 * Binder extension to avoid item validation failure in buffered editor mode.
	 * 
	 * @param <T> Item type
	 */
	private static class DefaultItemListingBinder<T> extends Binder<T> {

		private static final long serialVersionUID = -5155452231265408090L;

		public DefaultItemListingBinder(PropertySet<T> propertySet) {
			super(propertySet);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.vaadin.flow.data.binder.Binder#validate()
		 */
		@Override
		public com.vaadin.flow.data.binder.BinderValidationStatus<T> validate() {
			// noop
			return new BinderValidationStatus<>(this, Collections.emptyList(), Collections.emptyList());
		}

	}

	// --------- configurator

	static abstract class AbstractItemListingConfigurator<T, P, L extends ItemListing<T, P>, I extends AbstractItemListing<T, P>, C extends ItemListingConfigurator<T, P, L, C> & HasDataProviderConfigurator<T, C>>
			extends AbstractComponentConfigurator<Grid<T>, C>
			implements ItemListingConfigurator<T, P, L, C>, HasDataProviderConfigurator<T, C> {

		protected final DefaultHasSizeConfigurator sizeConfigurator;
		protected final DefaultHasStyleConfigurator styleConfigurator;
		protected final DefaultHasEnabledConfigurator enabledConfigurator;

		protected final I instance;

		protected Set<T> items = new LinkedHashSet<>();

		private final List<ItemEventListener<L, T, ItemEvent<L, T>>> refreshListeners = new LinkedList<>();

		private boolean editable;
		private boolean editorBuffered;

		private int frozenColumnsCount;

		private Consumer<EditableItemListingSection<P>> headerConfigurator;
		private Consumer<EditableItemListingSection<P>> footerConfigurator;

		private boolean frozen;

		public AbstractItemListingConfigurator(I instance) {
			super(instance.getGrid());
			this.instance = instance;
			this.sizeConfigurator = new DefaultHasSizeConfigurator(instance.getGrid());
			this.styleConfigurator = new DefaultHasStyleConfigurator(instance.getGrid());
			this.enabledConfigurator = new DefaultHasEnabledConfigurator(instance.getGrid());
		}

		@Override
		public abstract C getConfigurator();

		/**
		 * Get the listing instance.
		 * @return The listing instance
		 */
		protected abstract L getItemListing();

		@Override
		protected Optional<HasSize> hasSize() {
			return Optional.of(getComponent());
		}

		@Override
		protected Optional<HasStyle> hasStyle() {
			return Optional.of(getComponent());
		}

		@Override
		protected Optional<HasEnabled> hasEnabled() {
			return Optional.of(getComponent());
		}

		/**
		 * Get the listing instance.
		 * @return the listing instance
		 */
		protected I getInstance() {
			return instance;
		}

		/**
		 * Configure the listing.
		 * @return the listing
		 */
		protected I configureAndBuild() {

			// items
			if (!items.isEmpty()) {
				instance.setDataProvider(DataProvider.ofCollection(items));
			}

			// refresh listeners
			if (instance.getGrid().getDataProvider() != null) {
				refreshListeners.forEach(l -> instance.getGrid().getDataProvider().addDataProviderListener(e -> {
					l.onItemEvent(new DefaultItemEvent<>(getItemListing(),
							() -> (e instanceof DataRefreshEvent) ? ((DataRefreshEvent<T>) e).getItem() : null));
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

			if (frozen) {
				instance.getItemListingDataProvider().ifPresent(p -> p.setFrozen(frozen));
			}

			return instance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
		 * java.lang.String)
		 */
		@Override
		public C width(String width) {
			sizeConfigurator.width(width);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
		 * java.lang.String)
		 */
		@Override
		public C height(String height) {
			sizeConfigurator.height(height);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleNames(java.lang.String[])
		 */
		@Override
		public C styleNames(String... styleNames) {
			styleConfigurator.styleNames(styleNames);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleName(java.lang.String)
		 */
		@Override
		public C styleName(String styleName) {
			styleConfigurator.styleName(styleName);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
		 * enabled(boolean)
		 */
		@Override
		public C enabled(boolean enabled) {
			enabledConfigurator.enabled(enabled);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * tabIndex(int)
		 */
		@Override
		public C tabIndex(int tabIndex) {
			instance.getGrid().setTabIndex(tabIndex);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * withFocusListener(com.vaadin.flow. component.ComponentEventListener)
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
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * withBlurListener(com.vaadin.flow. component.ComponentEventListener)
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
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * withFocusShortcut(com.vaadin.flow. component.Key)
		 */
		@Override
		public ShortcutConfigurator<C> withFocusShortcut(Key key) {
			return new DefaultShortcutConfigurator<>(instance.getGrid().addFocusShortcut(key), getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasItemsDataSourceConfigurator#dataSource(com.vaadin.flow.
		 * data.provider.DataProvider)
		 */
		@Override
		public C dataSource(DataProvider<T, ?> dataProvider) {
			ObjectUtils.argumentNotNull(dataProvider, "DataProvider must be not null");
			instance.setDataProvider(dataProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(
		 * java.lang.Iterable)
		 */
		@Override
		public C items(Iterable<T> items) {
			this.items = CollectionUtils.iterableAsSet(items);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #items(java.lang.Object[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		public C items(T... items) {
			return items((items == null) ? Collections.emptyList() : Arrays.asList(items));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#
		 * addItem(java.lang.Object)
		 */
		@Override
		public C addItem(T item) {
			ObjectUtils.argumentNotNull(item, "Item must be not null");
			this.items.add(item);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayAsFirst(java.lang.Object)
		 */
		@Override
		public C displayAsFirst(P property) {
			instance.setDisplayAsFirst(property);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayAsLast(java.lang.Object)
		 */
		@Override
		public C displayAsLast(P property) {
			instance.setDisplayAsLast(property);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayBefore(java.lang.Object, java.lang.Object)
		 */
		@Override
		public C displayBefore(P property, P beforeProperty) {
			instance.setDisplayBefore(property, beforeProperty);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayAfter(java.lang.Object, java.lang.Object)
		 */
		@Override
		public C displayAfter(P property, P afterProperty) {
			instance.setDisplayAfter(property, afterProperty);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * visibleColumns(java.util.List)
		 */
		@Override
		public C visibleColumns(List<? extends P> columns) {
			instance.setVisibleColumns(columns);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortable(boolean)
		 */
		@Override
		public C sortable(boolean sortable) {
			instance.getListingProperties().forEach(p -> instance.getColumnConfiguration(p)
					.setSortMode(sortable ? SortMode.ENABLED : SortMode.DISABLED));
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortable(java.lang.Object, boolean)
		 */
		@Override
		public C sortable(P property, boolean sortable) {
			instance.getColumnConfiguration(property).setSortMode(sortable ? SortMode.ENABLED : SortMode.DISABLED);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * resizable(boolean)
		 */
		@Override
		public C resizable(boolean resizable) {
			instance.getListingProperties().forEach(p -> instance.getColumnConfiguration(p).setResizable(resizable));
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * resizable(java.lang.Object, boolean)
		 */
		@Override
		public C resizable(P property, boolean resizable) {
			instance.getColumnConfiguration(property).setResizable(resizable);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * visible(java.lang.Object, boolean)
		 */
		@Override
		public C visible(P property, boolean visible) {
			instance.getColumnConfiguration(property).setVisible(visible);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * readOnly(java.lang.Object, boolean)
		 */
		@Override
		public C readOnly(P property, boolean readOnly) {
			instance.getColumnConfiguration(property).setReadOnly(readOnly);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * frozen(java.lang.Object, boolean)
		 */
		@Override
		public C frozen(P property, boolean frozen) {
			instance.getColumnConfiguration(property).setFrozen(frozen);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * frozenColumns(int)
		 */
		@Override
		public C frozenColumns(int frozenColumnsCount) {
			this.frozenColumnsCount = frozenColumnsCount;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * width(java.lang.Object, java.lang.String)
		 */
		@Override
		public C width(P property, String width) {
			if (width != null) {
				instance.getColumnConfiguration(property).setFlexGrow(0);
			}
			instance.getColumnConfiguration(property).setWidth(width);
			return getConfigurator();
		}

		@Override
		public C autoWidth(P property, boolean autoWidth) {
			instance.getColumnConfiguration(property).setAutoWidth(autoWidth);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * flexGrow(java.lang.Object, int)
		 */
		@Override
		public C flexGrow(P property, int flexGrow) {
			instance.getColumnConfiguration(property).setFlexGrow(flexGrow);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * styleNameGenerator(java.util. function.Function)
		 */
		@Override
		public C styleNameGenerator(Function<T, String> styleNameGenerator) {
			if (styleNameGenerator != null) {
				instance.getGrid().setClassNameGenerator(item -> styleNameGenerator.apply(item));
			}
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * styleNameGenerator(java.lang. Object, java.util.function.Function)
		 */
		@Override
		public C styleNameGenerator(P property, Function<T, String> styleNameGenerator) {
			instance.getColumnConfiguration(property).setStyleNameGenerator(styleNameGenerator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * expand(java.lang.Object)
		 */
		@Override
		public C expand(P property) {
			instance.setPropertyToExpand(property);
			return getConfigurator();
		}

		@Override
		public C columnsAutoWidth() {
			instance.setColumnsAutoWidth(true);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * alignment(java.lang.Object,
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ColumnAlignment)
		 */
		@Override
		public C alignment(P property, ColumnAlignment alignment) {
			instance.getColumnConfiguration(property).setAlignment(alignment);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * renderer(java.lang.Object, com.vaadin.flow.data.renderer.Renderer)
		 */
		@Override
		public C renderer(P property, Renderer<T> renderer) {
			instance.getColumnConfiguration(property).setRenderer(renderer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * valueProvider(java.lang.Object, com.vaadin.flow.function.ValueProvider)
		 */
		@Override
		public C valueProvider(P property, ValueProvider<T, String> valueProvider) {
			instance.getColumnConfiguration(property).setValueProvider(valueProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortComparator(java.lang.Object, java.util.Comparator)
		 */
		@Override
		public C sortComparator(P property, Comparator<T> comparator) {
			instance.getColumnConfiguration(property).setComparator(comparator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortUsing(java.lang.Object, java.util.List)
		 */
		@Override
		public C sortUsing(P property, List<P> sortProperties) {
			instance.getColumnConfiguration(property).setSortProperties(sortProperties);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortProvider(java.lang.Object, java.util.function.Function)
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
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * header(java.lang.Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public C header(P property, Localizable header) {
			instance.getColumnConfiguration(property).setHeaderText(header);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * headerComponent(java.lang.Object, com.vaadin.flow.component.Component)
		 */
		@Override
		public C headerComponent(P property, Component header) {
			instance.getColumnConfiguration(property).setHeaderComponent(header);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * footer(java.lang.Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public C footer(P property, Localizable footer) {
			instance.getColumnConfiguration(property).setFooterText(footer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * footerComponent(java.lang.Object, com.vaadin.flow.component.Component)
		 */
		@Override
		public C footerComponent(P property, Component footer) {
			instance.getColumnConfiguration(property).setFooterComponent(footer);
			return getConfigurator();
		}

		@Override
		public C withColumnPostProcessor(ColumnPostProcessor<P> columnPostProcessor) {
			instance.addColumnPostProcessor(columnPostProcessor);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * editorComponent(java.lang.Object, java.util.function.Function)
		 */
		@Override
		public C editorComponent(P property, Function<T, ? extends Component> editorComponentProvider) {
			instance.getColumnConfiguration(property).setEditorComponent(editorComponentProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * pageSize(int)
		 */
		@Override
		public C pageSize(int pageSize) {
			instance.getGrid().setPageSize(pageSize);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * heightByRows(boolean)
		 */
		@Override
		public C heightByRows(boolean heightByRows) {
			instance.getGrid().setHeightByRows(heightByRows);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * columnReorderingAllowed(boolean)
		 */
		@Override
		public C columnReorderingAllowed(boolean columnReorderingAllowed) {
			instance.getGrid().setColumnReorderingAllowed(columnReorderingAllowed);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * itemDetailsRenderer(com.vaadin.flow .data.renderer.Renderer)
		 */
		@Override
		public C itemDetailsRenderer(Renderer<T> renderer) {
			instance.getGrid().setItemDetailsRenderer(renderer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * itemDetailsVisibleOnClick(boolean)
		 */
		@Override
		public C itemDetailsVisibleOnClick(boolean detailsVisibleOnClick) {
			instance.getGrid().setDetailsVisibleOnClick(detailsVisibleOnClick);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * selectionMode(com.holonplatform.
		 * vaadin.flow.components.Selectable.SelectionMode)
		 */
		@Override
		public C selectionMode(SelectionMode selectionMode) {
			instance.setSelectionMode(selectionMode);
			return getConfigurator();
		}

		@Override
		public C selectAllCheckboxVisibility(SelectAllCheckboxVisibility selectAllCheckBoxVisibility) {
			instance.setSelectAllCheckboxVisibility(selectAllCheckBoxVisibility);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public C withSelectionListener(SelectionListener<T> selectionListener) {
			instance.addSelectionListener(selectionListener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withItemClickListener(com.
		 * holonplatform.vaadin.flow.components.events.ClickEventListener)
		 */
		@Override
		public C withItemClickListener(ClickEventListener<L, ItemClickEvent<L, T>> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			instance.getGrid().addItemClickListener(e -> {
				listener.onClickEvent(new DefaultItemListingClickEvent<>(getItemListing(), e.isFromClient(), instance,
						() -> e.getItem()));
			});
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withItemRefreshListener(com.
		 * holonplatform.vaadin.flow.components.events.ItemEventListener)
		 */
		@Override
		public C withItemRefreshListener(ItemEventListener<L, T, ItemEvent<L, T>> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			refreshListeners.add(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * multiSort(boolean)
		 */
		@Override
		public C multiSort(boolean multiSort) {
			instance.getGrid().setMultiSort(multiSort);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * verticalScrollingEnabled(boolean)
		 */
		@Override
		public C verticalScrollingEnabled(boolean enabled) {
			instance.getGrid().setVerticalScrollingEnabled(enabled);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withThemeVariants(com.vaadin.flow. component.grid.GridVariant[])
		 */
		@Override
		public C withThemeVariants(GridVariant... variants) {
			instance.getGrid().addThemeVariants(variants);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * contextMenu()
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, L, C> contextMenu() {
			return new DefaultItemListingContextMenuBuilder<>(instance, instance.getGrid().addContextMenu(),
					getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * header(java.util.function.Consumer)
		 */
		@Override
		public C header(Consumer<EditableItemListingSection<P>> headerConfigurator) {
			this.headerConfigurator = headerConfigurator;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * footer(java.util.function.Consumer)
		 */
		@Override
		public C footer(Consumer<EditableItemListingSection<P>> footerConfigurator) {
			this.footerConfigurator = footerConfigurator;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * editable(boolean)
		 */
		@Override
		public C editable(boolean editable) {
			this.editable = editable;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * editorBuffered(boolean)
		 */
		@Override
		public C editorBuffered(boolean buffered) {
			this.editorBuffered = buffered;
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorSaveListener(com.vaadin.
		 * flow.component.grid.editor.EditorSaveListener)
		 */
		@Override
		public C withEditorSaveListener(EditorSaveListener<T, P> listener) {
			this.editable = true;
			instance.addEditorSaveListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorCancelListener(com.vaadin
		 * .flow.component.grid.editor.EditorCancelListener)
		 */
		@Override
		public C withEditorCancelListener(EditorCancelListener<T, P> listener) {
			this.editable = true;
			instance.addEditorCancelListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorOpenListener(com.vaadin.
		 * flow.component.grid.editor.EditorOpenListener)
		 */
		@Override
		public C withEditorOpenListener(EditorOpenListener<T, P> listener) {
			this.editable = true;
			instance.addEditorOpenListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorCloseListener(com.vaadin.
		 * flow.component.grid.editor.EditorCloseListener)
		 */
		@Override
		public C withEditorCloseListener(EditorCloseListener<T, P> listener) {
			this.editable = true;
			instance.addEditorCloseListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withValidator(com.holonplatform. core.Validator)
		 */
		@Override
		public C withValidator(Validator<T> validator) {
			instance.addValidator(validator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * required(java.lang.Object)
		 */
		@Override
		public C required(P property) {
			instance.getColumnConfiguration(property).setRequired(true);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * required(java.lang.Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public C required(P property, Localizable message) {
			instance.getColumnConfiguration(property).setRequired(true);
			instance.getColumnConfiguration(property).setRequiredMessage(message);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * withPostProcessor(java.util.function .BiConsumer)
		 */
		@Override
		public C withPostProcessor(BiConsumer<P, Input<?>> postProcessor) {
			instance.addEditorPostProcessor(postProcessor);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * validationStatusHandler(java.lang. Object,
		 * com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public C validationStatusHandler(P property, ValidationStatusHandler<Input<?>> validationStatusHandler) {
			instance.getColumnConfiguration(property)
					.setValidationStatusHandler((ValidationStatusHandler) validationStatusHandler);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public C validationStatusHandler(ValidationStatusHandler<EditorComponentGroup<P, T>> validationStatusHandler) {
			instance.setValidationStatusHandler(validationStatusHandler);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * groupValidationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.GroupValidationStatusHandler)
		 */
		@Override
		public C groupValidationStatusHandler(
				GroupValidationStatusHandler<EditorComponentGroup<P, T>, P, Input<?>> groupValidationStatusHandler) {
			instance.setGroupValidationStatusHandler(groupValidationStatusHandler);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * enableRefreshOnValueChange(boolean)
		 */
		@Override
		public C enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
			instance.setEnableRefreshOnValueChange(enableRefreshOnValueChange);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#
		 * usePropertyRendererRegistry(com.
		 * holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public C usePropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry) {
			instance.setPropertyRendererRegistry(propertyRendererRegistry);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#
		 * withValueChangeListener(com.
		 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public C withValueChangeListener(
				ValueChangeListener<T, GroupValueChangeEvent<T, P, Input<?>, EditorComponentGroup<P, T>>> listener) {
			instance.addValueChangeListener(listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * frozen(boolean)
		 */
		@Override
		public C frozen(boolean frozen) {
			this.frozen = frozen;
			return getConfigurator();
		}

		@Override
		public C rowsDraggable(boolean rowsDraggable) {
			instance.getGrid().setRowsDraggable(rowsDraggable);
			return getConfigurator();
		}

		@Override
		public C dragFilter(Predicate<T> dragFilter) {
			ObjectUtils.argumentNotNull(dragFilter, "Drag filter predicate must be not null");
			instance.getGrid().setDragFilter(r -> dragFilter.test(r));
			return getConfigurator();
		}

		@Override
		public C dragDataGenerator(String type, Function<T, String> dragDataGenerator) {
			ObjectUtils.argumentNotNull(dragDataGenerator, "Drag data generator function must be not null");
			instance.getGrid().setDragDataGenerator(type, r -> dragDataGenerator.apply(r));
			return getConfigurator();
		}

		@Override
		public C withDragStartListener(ItemListingDnDListener<T, P, ItemListingDragStartEvent<T, P>> listener) {
			ObjectUtils.argumentNotNull(listener, "Drag start listener must be not null");
			instance.getGrid().addDragStartListener(
					event -> listener.onDndEvent(new DefaultItemListingDragStartEvent<>(instance, event)));
			return getConfigurator();
		}

		@Override
		public C withDragEndListener(ItemListingDnDListener<T, P, ItemListingDragEndEvent<T, P>> listener) {
			ObjectUtils.argumentNotNull(listener, "Drag end listener must be not null");
			instance.getGrid().addDragEndListener(
					event -> listener.onDndEvent(new DefaultItemListingDragEndEvent<>(instance, event)));
			return getConfigurator();
		}

		@Override
		public C dropMode(GridDropMode dropMode) {
			instance.getGrid().setDropMode(dropMode);
			return getConfigurator();
		}

		@Override
		public C dropFilter(Predicate<T> dropFilter) {
			ObjectUtils.argumentNotNull(dropFilter, "Drop filter predicate must be not null");
			instance.getGrid().setDropFilter(r -> dropFilter.test(r));
			return getConfigurator();
		}

		@Override
		public C withDropListener(ItemListingDnDListener<T, P, ItemListingDropEvent<T, P>> listener) {
			ObjectUtils.argumentNotNull(listener, "Drop listener must be not null");
			instance.getGrid()
					.addDropListener(event -> listener.onDndEvent(new DefaultItemListingDropEvent<>(instance, event)));
			return getConfigurator();
		}

		@Override
		public C withColumnResizeListener(ColumnResizeListener<T, P> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			instance.getGrid().addColumnResizeListener(event -> {
				instance.getProperty(event.getResizedColumn()).ifPresent(property -> {
					listener.onColumnResizeEvent(new DefaultColumnResizeEvent<>(instance, event.isFromClient(),
							property, event.getResizedColumn().getWidth()));
				});
			});
			return getConfigurator();
		}

		@Override
		public C withColumnReorderListener(ColumnReorderListener<T, P> listener) {
			ObjectUtils.argumentNotNull(listener, "Listener must be not null");
			instance.getGrid().addColumnReorderListener(event -> {
				final List<Grid.Column<T>> columns = event.getColumns();
				if (columns != null) {
					listener.onColumnReorderEvent(new DefaultColumnReorderEvent<>(instance, event.isFromClient(),
							columns.stream().map(c -> instance.getProperty(c).orElse(null)).filter(c -> c != null)
									.collect(Collectors.toList())));
				}
			});
			return getConfigurator();
		}

	}

	static class DefaultItemListingContextMenuBuilder<T, P, L extends ItemListing<T, P>, C extends ItemListingConfigurator<T, P, L, C>>
			extends AbstractComponentConfigurator<GridContextMenu<T>, ItemListingContextMenuBuilder<T, P, L, C>>
			implements ItemListingContextMenuBuilder<T, P, L, C> {

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

		@Override
		protected Optional<HasSize> hasSize() {
			return Optional.empty();
		}

		@Override
		protected Optional<HasStyle> hasStyle() {
			return Optional.of(getComponent());
		}

		@Override
		protected Optional<HasEnabled> hasEnabled() {
			return Optional.of(getComponent());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#
		 * openOnClick(boolean)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, L, C> openOnClick(boolean openOnClick) {
			getComponent().setOpenOnClick(openOnClick);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#
		 * withOpenedChangeListener(com.vaadin .flow.component.ComponentEventListener)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, L, C> withOpenedChangeListener(
				ComponentEventListener<OpenedChangeEvent<GridContextMenu<T>>> listener) {
			getComponent().addOpenedChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleNames(java.lang.String[])
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, L, C> styleNames(String... styleNames) {
			styleConfigurator.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleName(java.lang.String)
		 */
		@Override
		public ItemListingContextMenuBuilder<T, P, L, C> styleName(String styleName) {
			styleConfigurator.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#
		 * withItem(com.holonplatform.core. i18n.Localizable)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, ItemListingContextMenuBuilder<T, P, L, C>> withItem(
				Localizable text) {
			final ContextMenuItemListenerHandler<T, P> handler = new ContextMenuItemListenerHandler<>(itemListing);
			final GridMenuItem<T> item = getComponent().addItem(LocalizationProvider.localize(text).orElse(""),
					handler);
			return new ItemListingContextMenuItemBuilder<>(getConfigurator(), item, handler);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#
		 * withItem(com.vaadin.flow.component. Component)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, ItemListingContextMenuBuilder<T, P, L, C>> withItem(
				Component component) {
			final ContextMenuItemListenerHandler<T, P> handler = new ContextMenuItemListenerHandler<>(itemListing);
			final GridMenuItem<T> item = getComponent().addItem(component, handler);
			return new ItemListingContextMenuItemBuilder<>(getConfigurator(), item, handler);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingContextMenuBuilder#add()
		 */
		@Override
		public C add() {
			return parentBuilder;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.internal.components.builders.
		 * AbstractComponentConfigurator#getConfigurator()
		 */
		@Override
		protected ItemListingContextMenuBuilder<T, P, L, C> getConfigurator() {
			return this;
		}

	}

	private static class ItemListingContextMenuItemBuilder<T, P, B extends ContextMenuConfigurator<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B>>
			implements
			MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> {

		private final B parentBuilder;
		private final GridMenuItem<T> menuItem;
		private final ContextMenuItemListenerHandler<T, P> handler;

		public ItemListingContextMenuItemBuilder(B parentBuilder, GridMenuItem<T> menuItem,
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
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> id(
				String id) {
			menuItem.setId(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
		 * enabled(boolean)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> enabled(
				boolean enabled) {
			menuItem.setEnabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(
		 * com.holonplatform.core.i18n. Localizable)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> text(
				Localizable text) {
			menuItem.setText(LocalizationProvider.localize(text).orElse(""));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.
		 * MenuItemBuilder#checkable(boolean)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> checkable(
				boolean checkable) {
			menuItem.setCheckable(checkable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.
		 * MenuItemBuilder#checked(boolean)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> checked(
				boolean checked) {
			if (checked && !menuItem.isCheckable()) {
				menuItem.setCheckable(true);
			}
			menuItem.setChecked(checked);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.
		 * MenuItemBuilder#withClickListener( java.util.EventListener)
		 */
		@Override
		public MenuItemBuilder<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, B> withClickListener(
				ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>> menuItemClickListener) {
			ObjectUtils.argumentNotNull(menuItemClickListener, "Click listener must be not null");
			handler.addListener(menuItemClickListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.
		 * MenuItemBuilder#add()
		 */
		@Override
		public B add() {
			return parentBuilder;
		}

	}

	@SuppressWarnings("serial")
	private static class ContextMenuItemListenerHandler<T, P>
			implements ComponentEventListener<GridContextMenuItemClickEvent<T>> {

		private final List<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>> listeners = new LinkedList<>();
		private final ItemListing<T, P> itemListing;

		public ContextMenuItemListenerHandler(ItemListing<T, P> itemListing) {
			super();
			this.itemListing = itemListing;
		}

		public void addListener(
				ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>> listener) {
			this.listeners.add(listener);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.vaadin.flow.component.ComponentEventListener#onComponentEvent(com.vaadin.
		 * flow.component.ComponentEvent)
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
	 * @param <L> Item listing type
	 * @param <B> Parent builder type
	 */
	static class DefaultItemListingColumnBuilder<T, P, L extends ItemListing<T, P>, B extends ItemListingConfigurator<T, P, L, B>>
			implements ItemListingColumnBuilder<T, P, L, B> {

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
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# resizable(boolean)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> resizable(boolean resizable) {
			listing.getColumnConfiguration(property).setResizable(resizable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# visible(boolean)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> visible(boolean visible) {
			listing.getColumnConfiguration(property).setVisible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# frozen(boolean)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> frozen(boolean frozen) {
			listing.getColumnConfiguration(property).setFrozen(frozen);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator#width (java.lang.String)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> width(String width) {
			if (width != null) {
				listing.getColumnConfiguration(property).setFlexGrow(0);
			}
			listing.getColumnConfiguration(property).setWidth(width);
			return this;
		}

		@Override
		public ItemListingColumnBuilder<T, P, L, B> autoWidth(boolean autoWidth) {
			listing.getColumnConfiguration(property).setAutoWidth(autoWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# flexGrow(int)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> flexGrow(int flexGrow) {
			listing.getColumnConfiguration(property).setFlexGrow(flexGrow);
			return this;
		}

		@Override
		public ItemListingColumnBuilder<T, P, L, B> alignment(ColumnAlignment alignment) {
			listing.getColumnConfiguration(property).setAlignment(alignment);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator#
		 * styleNameGenerator(java.util.function.Function)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> styleNameGenerator(Function<T, String> styleNameGenerator) {
			listing.getColumnConfiguration(property).setStyleNameGenerator(styleNameGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# sortComparator(java.util.Comparator)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> sortComparator(Comparator<T> comparator) {
			listing.getColumnConfiguration(property).setComparator(comparator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# sortUsing(java.util.List)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> sortUsing(List<P> sortProperties) {
			listing.getColumnConfiguration(property).setSortProperties(sortProperties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# sortProvider(java.util.function.Function)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> sortProvider(
				Function<com.holonplatform.core.query.QuerySort.SortDirection, Stream<ItemSort<P>>> sortProvider) {
			listing.getColumnConfiguration(property).setSortOrderProvider(direction -> {
				return sortProvider.apply(AbstractItemListing.convert(direction))
						.map(is -> new QuerySortOrder(listing.getColumnKey(is.getProperty()), direction));
			});
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator#
		 * header(com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> header(Localizable header) {
			listing.getColumnConfiguration(property).setHeaderText(header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator#
		 * headerComponent(com.vaadin.flow.component.Component)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> headerComponent(Component header) {
			listing.getColumnConfiguration(property).setHeaderComponent(header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# editorComponent(java.util.function.Function)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> editorComponent(
				Function<T, ? extends Component> editorComponentProvider) {
			listing.getColumnConfiguration(property).setEditorComponent(editorComponentProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# displayAsFirst()
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> displayAsFirst() {
			listing.setDisplayAsFirst(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# displayAsLast()
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> displayAsLast() {
			listing.setDisplayAsLast(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# displayBefore(java.lang.Object)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> displayBefore(P beforeProperty) {
			listing.setDisplayBefore(property, beforeProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnConfigurator# displayAfter(java.lang.Object)
		 */
		@Override
		public ItemListingColumnBuilder<T, P, L, B> displayAfter(P afterProperty) {
			listing.setDisplayAfter(property, afterProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.
		 * ItemListingColumnBuilder#add()
		 */
		@Override
		public B add() {
			return parent;
		}

	}

}
