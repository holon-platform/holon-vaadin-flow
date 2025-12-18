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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorCancelListener;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorCloseListener;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorComponentGroup;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorOpenListener;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorSaveListener;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingCell;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingRow;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingSection;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.components.events.ColumnReorderListener;
import com.holonplatform.vaadin.flow.components.events.ColumnResizeListener;
import com.holonplatform.vaadin.flow.components.events.ItemClickEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEventListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingDnDListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragEndEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragStartEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingDropEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingItemEvent;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.contextmenu.GridSubMenu;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.data.renderer.ClickableRenderer.ItemClickListener;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * {@link ItemListing} configurator.
 *
 * @param <T> Item type
 * @param <P> Item property type
 * @param <L> Item listing type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ItemListingConfigurator<T, P, L extends ItemListing<T, P>, C extends ItemListingConfigurator<T, P, L, C>>
		extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C>,
		FocusableConfigurator<Component, C>, HasThemeVariantConfigurator<GridVariant, C>,
		InputGroupConfigurator<P, T, EditorComponentGroup<P, T>, C> {

	/**
	 * Configure the column represented by given <code>property</code> to be displayed before any other
	 * listing column by default.
	 * @param property The property which represents the column to display as first (not null)
	 * @return this
	 */
	C displayAsFirst(P property);

	/**
	 * Configure the column represented by given <code>property</code> to be displayed after any other
	 * listing column by default.
	 * @param property The property which represents the column to display as last (not null)
	 * @return this
	 */
	C displayAsLast(P property);

	/**
	 * Configure the column represented by given <code>property</code> id to be displayed before the
	 * column which corresponds to the id specified by the given <code>beforeProperty</code>.
	 * @param property Property which represents the column to display before the other property (not
	 *        null)
	 * @param beforeProperty Property which represents the column before which the first property has to
	 *        be displayed (not null)
	 * @return this
	 */
	C displayBefore(P property, P beforeProperty);

	/**
	 * Configure the column represented by given <code>property</code> id to be displayed after the
	 * column which corresponds to the id specified by the given <code>afterProperty</code>.
	 * @param property Property which represents the column to display after the other property (not
	 *        null)
	 * @param afterProperty Property which represents the column after which the first property has to
	 *        be displayed (not null)
	 * @return this
	 */
	C displayAfter(P property, P afterProperty);

	/**
	 * Add a <em>virtual</em> column which contents will be rendered using given
	 * <code>valueProvider</code>.
	 * @param <X> Column value type
	 * @param valueProvider The value provider to use to provide the column value, using the current row
	 *        item instance (not null)
	 * @return An {@link ItemListingColumnBuilder} which allow further column configuration and provides
	 *         the {@link ItemListingColumnBuilder#add()} method to add the column to the listing
	 */
	<X> ItemListingColumnBuilder<T, P, L, C> withColumn(ValueProvider<T, X> valueProvider);

	/**
	 * Add a column which contents will be rendered as a {@link Component} using given
	 * <code>valueProvider</code>.
	 * @param valueProvider The value provider to use to provide the column {@link Component} using the
	 *        current row item instance (not null)
	 * @return An {@link ItemListingColumnBuilder} which allow further column configuration and provides
	 *         the {@link ItemListingColumnBuilder#add()} method to add the column to the listing
	 */
	ItemListingColumnBuilder<T, P, L, C> withComponentColumn(ValueProvider<T, Component> valueProvider);

	/**
	 * Set the visible columns list, using the item properties as column reference. The columns will be
	 * displayed in the order thay are provided.
	 * <p>
	 * Any property display order declaration configured using {@link #displayAsFirst(Object)},
	 * {@link #displayAsLast(Object)}, {@link #displayBefore(Object, Object)} and
	 * {@link #displayAfter(Object, Object)} will be ignored.
	 * </p>
	 * @param visibleColumns The visible column properties (not null)
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default C visibleColumns(P... visibleColumns) {
		return visibleColumns(Arrays.asList(visibleColumns));
	}

	/**
	 * Set the visible columns list, using the item properties as column reference. The columns will be
	 * displayed in the order thay are provided.
	 * <p>
	 * Any property display order declaration configured using {@link #displayAsFirst(Object)},
	 * {@link #displayAsLast(Object)}, {@link #displayBefore(Object, Object)} and
	 * {@link #displayAfter(Object, Object)} will be ignored.
	 * </p>
	 * @param visibleColumns The visible column properties (not null)
	 * @return this
	 */
	C visibleColumns(List<? extends P> visibleColumns);

	/**
	 * Set whether all the item listing columns are user-sortable.
	 * @param sortable Whether all the item listing columns are user-sortable
	 * @return this
	 */
	C sortable(boolean sortable);

	/**
	 * Set whether the column which corresponds to given property is user-sortable.
	 * @param property The property to configure (not null)
	 * @param sortable Whether given property is user-sortable
	 * @return this
	 */
	C sortable(P property, boolean sortable);

	/**
	 * Set whether all the item listing columns are user-resizable.
	 * @param resizable Whether all the item listing columns are user-resizable
	 * @return this
	 */
	C resizable(boolean resizable);

	/**
	 * Set whether the column which corresponds to given property is user-resizable.
	 * @param property The property to configure (not null)
	 * @param resizable Whether given property is user-resizable
	 * @return this
	 */
	C resizable(P property, boolean resizable);

	/**
	 * Set whether the column which corresponds to given property is visible.
	 * @param property The property to configure (not null)
	 * @param visible Whether given property is visible
	 * @return this
	 */
	C visible(P property, boolean visible);

	/**
	 * Set the column which corresponds to given property as hidden.
	 * @param property The property to configure (not null)
	 * @return this
	 * @see #visible(Object, boolean)
	 */
	default C hidden(P property) {
		return visible(property, false);
	}

	/**
	 * Set whether the column which corresponds to given property is read-only.
	 * <p>
	 * When a column is read-only, no editor component will be provided when the item is in edit mode.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param readOnly Whether given property is read-only
	 * @return this
	 */
	C readOnly(P property, boolean readOnly);

	/**
	 * Set whether the column which corresponds to given property is frozen.
	 * @param property The property to configure (not null)
	 * @param frozen Whether given property is frozen
	 * @return this
	 */
	C frozen(P property, boolean frozen);

	/**
	 * Sets the number of frozen columns in this listing.
	 * @param frozenColumnsCount The number of columns that should be frozen
	 * @return this
	 */
	C frozenColumns(int frozenColumnsCount);

	/**
	 * Sets the width of the column which corresponds to given property as a CSS-string.
	 * @param property The property to configure (not null)
	 * @param width the width to set
	 * @return this
	 */
	C width(P property, String width);

	/**
	 * Enables or disables automatic width for the column which corresponds to given property.
	 * <p>
	 * Automatically sets the width of the column based on the columncontents when this is set to true.
	 * </p>
	 * <p>
	 * For performance reasons the column width is calculated automaticallyonly once when the grid items
	 * are rendered for the first time and thecalculation only considers the rows which are currently
	 * rendered inDOM (a bit more than what is currently visible). If the grid isscrolled, or the cell
	 * content changes, the column width might notmatch the contents anymore.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param autoWidth Whether to enable the column auto-width
	 * @return this
	 * @since 5.3.0
	 */
	C autoWidth(P property, boolean autoWidth);

	/**
	 * Enables automatic width for the column which corresponds to given property.
	 * <p>
	 * Automatically sets the width of the column based on the columncontents when this is set to true.
	 * </p>
	 * <p>
	 * For performance reasons the column width is calculated automaticallyonly once when the grid items
	 * are rendered for the first time and thecalculation only considers the rows which are currently
	 * rendered inDOM (a bit more than what is currently visible). If the grid isscrolled, or the cell
	 * content changes, the column width might notmatch the contents anymore.
	 * </p>
	 * @param property The property to configure (not null)
	 * @return this
	 * @since 5.3.0
	 */
	default C autoWidth(P property) {
		return autoWidth(property, true);
	}

	/**
	 * Sets the flex grow ratio for the column which corresponds to given property.
	 * <p>
	 * When set to 0, column width is fixed.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param flexGrow the flex grow ratio to set
	 * @return this
	 */
	C flexGrow(P property, int flexGrow);

	/**
	 * Sets the function that is used for generating CSS style class names for rows in this listing.
	 * <p>
	 * Returning <code>null</code> from the generator results in no custom class name being set.
	 * Multiple class names can be returned from the generator as space-separated.
	 * </p>
	 * <p>
	 * If a column level CSS class name generator is configured through
	 * {@link #styleNameGenerator(Object, Function)} together with this method, resulting class names
	 * from both methods will be effective. Class names generated by listing are applied to the cells
	 * before the class names generated by column. This means that if the classes contain conflicting
	 * style properties, column's classes will win.
	 * </p>
	 * @param styleNameGenerator The function to use to generate the row CSS style class name
	 * @return this
	 * @since 5.2.3
	 */
	C styleNameGenerator(Function<T, String> styleNameGenerator);

	/**
	 * Set the CSS style class name generator to use for the column which corresponds to given property.
	 * <p>
	 * Returning <code>null</code> from the generator results in no custom class name being set.
	 * Multiple class names can be returned from the generator as space-separated.
	 * </p>
	 * <p>
	 * If a row CSS style class name generator is configured through
	 * {@link #styleNameGenerator(Function)} together with this method, resulting class names from both
	 * generators will be effective. Class names generated by the listing are applied to the cells
	 * before the class names generated by column. This means that if the classes contain conflicting
	 * style properties, column's classes will win.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param styleNameGenerator The function to use to generate the cell CSS style class name
	 * @return this
	 * @since 5.2.3
	 */
	C styleNameGenerator(P property, Function<T, String> styleNameGenerator);

	/**
	 * Expand the column which corresponds to given property, taking all the available space.
	 * <p>
	 * The flew grow value for the provided column will be set to <code>1</code>, while the other
	 * columns flew grow value will be set to <code>0</code>.
	 * </p>
	 * @param property The property which identifies the column to expand
	 * @return this
	 */
	C expand(P property);

	/**
	 * Enables automatic width for all the listing columns.
	 * @return this
	 * @see #autoWidth(Object, boolean)
	 * @since 5.3.0
	 */
	C columnsAutoWidth();

	/**
	 * Sets the text alignment for the column which corresponds to given property.
	 * <p>
	 * Default is {@link ColumnAlignment#LEFT}.
	 * </p>
	 * @param property The property to configure (not null)
	 * @param alignment the text alignment to set
	 * @return this
	 */
	C alignment(P property, ColumnAlignment alignment);

	/**
	 * Sets the {@link Renderer} to use for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param renderer The column renderer to use
	 * @return this
	 */
	C renderer(P property, Renderer<T> renderer);

	/**
	 * Render the column which corresponds to given property as a {@link Component}, using given
	 * function to provide the component for each listing item.
	 * @param <R> Component type
	 * @param property The property to configure (not null)
	 * @param renderer The function to use to provide the component for each listing item (not null)
	 * @return this
	 */
	default <R extends Component> C componentRenderer(P property, Function<T, R> renderer) {
		ObjectUtils.argumentNotNull(renderer, "Renderer function must be not null");
		return renderer(property, new ComponentRenderer<>(item -> renderer.apply(item)));
	}

	/**
	 * Sets the {@link ValueProvider} to use to obtain the text to display in the column which
	 * corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param valueProvider The value provider to use
	 * @return this
	 */
	C valueProvider(P property, ValueProvider<T, String> valueProvider);

	/**
	 * Sets comparator to use with in-memory sorting for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param comparator The comparator to use with in-memory sorting
	 * @return this
	 */
	C sortComparator(P property, Comparator<T> comparator);

	/**
	 * Set the properties to use to implement the sort logic to apply when the column which corresponds
	 * to given property is user-sorted.
	 * @param property The property to configure (not null)
	 * @param sortProperties The properties to use to sort the column
	 * @return this
	 */
	C sortUsing(P property, List<P> sortProperties);

	/**
	 * Set the properties to use to implement the sort logic to apply when the column which corresponds
	 * to given property is user-sorted.
	 * @param property The property to configure (not null)
	 * @param sortProperties The properties to use to sort the column
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default C sortUsing(P property, P... sortProperties) {
		return sortUsing(property, Arrays.asList(sortProperties));
	}

	/**
	 * Set the function to use to obtain the {@link ItemSort}s to use when the column which corresponds
	 * to given property is user-sorted.
	 * @param property The property to configure (not null)
	 * @param sortProvider Sort provider
	 * @return this
	 */
	C sortProvider(P property, Function<SortDirection, Stream<ItemSort<P>>> sortProvider);

	/**
	 * Set the header text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param header Localizable column header text (not null)
	 * @return this
	 */
	C header(P property, Localizable header);

	/**
	 * Set the header text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param header The column header text
	 * @return this
	 */
	default C header(P property, String header) {
		return header(property, Localizable.builder().message(header).build());
	}

	/**
	 * Set the header text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param defaultHeader The default column header text
	 * @param headerMessageCode The column header text translation message code
	 * @return this
	 */
	default C header(P property, String defaultHeader, String headerMessageCode) {
		return header(property, Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
	}

	/**
	 * Set the {@link Component} to use as header for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param header The column header component
	 * @return this
	 */
	C headerComponent(P property, Component header);

	/**
	 * Set the footer text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param footer Localizable column footer text (not null)
	 * @return this
	 */
	C footer(P property, Localizable footer);

	/**
	 * Set the footer text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param footer The column footer text
	 * @return this
	 */
	default C footer(P property, String footer) {
		return footer(property, Localizable.builder().message(footer).build());
	}

	/**
	 * Set the footer text for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param defaultFooter The default column footer text
	 * @param footerMessageCode The column footer text translation message code
	 * @return this
	 */
	default C footer(P property, String defaultFooter, String footerMessageCode) {
		return footer(property, Localizable.builder().message(defaultFooter).messageCode(footerMessageCode).build());
	}

	/**
	 * Set the {@link Component} to use as footer for the column which corresponds to given property.
	 * @param property The property to configure (not null)
	 * @param footer The column footer component
	 * @return this
	 */
	C footerComponent(P property, Component footer);

	/**
	 * Add a {@link ColumnPostProcessor} which can be used to furtherly configure each listing column
	 * before adding it to the listing component.
	 * @param columnPostProcessor The post processor to add (not null)
	 * @return this
	 */
	C withColumnPostProcessor(ColumnPostProcessor<P> columnPostProcessor);

	/**
	 * Sets the page size, which is the number of items fetched at a time from the data source.
	 * <p>
	 * Note: the number of items in the server-side memory can be considerably higher than the page
	 * size, since the component can show more than one page at a time.
	 * </p>
	 * @param pageSize the maximum number of items sent per request. Should be greater than zero
	 * @return this
	 */
	C pageSize(int pageSize);

	/**
	 * If <code>true</code>, the listing's height is defined by the number of its rows. All items are
	 * fetched from the data provider, and the Grid shows no vertical scroll bar.
	 * @param allRowsVisible <code>true</code> to make listing compute its height by the number of rows,
	 *        <code>false</code> for the default behavior
	 * @return this
	 */
	C allRowsVisible(boolean allRowsVisible);

	/**
	 * Sets whether column reordering is allowed or not.
	 * @param columnReorderingAllowed <code>true</code> if column reordering is allowed
	 * @return this
	 */
	C columnReorderingAllowed(boolean columnReorderingAllowed);

	/**
	 * Set the renderer to use for displaying the item details rows.
	 * @param renderer the renderer to use for displaying item details rows
	 * @return this
	 */
	C itemDetailsRenderer(Renderer<T> renderer);

	/**
	 * Set the function to use for displaying the item details rows as a text.
	 * @param textProvider The function to provide the item details text
	 * @return this
	 */
	default C itemDetailsText(Function<T, String> textProvider) {
		ObjectUtils.argumentNotNull(textProvider, "Text provider must be not null");
		return itemDetailsRenderer(new TextRenderer<>(item -> textProvider.apply(item)));
	}

	/**
	 * Set the function to use for displaying the item details rows as a {@link Component}.
	 * @param componentProvider The function to provide the item details {@link Component}
	 * @return this
	 */
	default C itemDetailsComponent(Function<T, Component> componentProvider) {
		ObjectUtils.argumentNotNull(componentProvider, "Component provider must be not null");
		return itemDetailsRenderer(new ComponentRenderer<>(item -> componentProvider.apply(item)));
	}

	/**
	 * Sets whether the item details can be opened and closed by clicking the rows or not.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @param detailsVisibleOnClick <code>true</code> to enable opening and closing item details by
	 *        clicking the rows, <code>false</code> otherwise
	 * @return this
	 * @see #itemDetailsRenderer(Renderer)
	 */
	C itemDetailsVisibleOnClick(boolean detailsVisibleOnClick);

	/**
	 * Set the listing selection mode.
	 * @param selectionMode The selection mode to set (not null). Use {@link SelectionMode#NONE} to
	 *        disable selection.
	 * @return this
	 */
	C selectionMode(SelectionMode selectionMode);

	/**
	 * Sets the select all checkbox visibility mode.
	 * <p>
	 * The default value is {@link SelectAllCheckboxVisibility#DEFAULT}, which means that the checkbox
	 * is only visible if the data provider is in-memory.
	 * </p>
	 * @param selectAllCheckBoxVisibility The select all checkbox visibility mode
	 * @return this
	 */
	C selectAllCheckboxVisibility(SelectAllCheckboxVisibility selectAllCheckBoxVisibility);

	/**
	 * Set the listing selection mode as {@link SelectionMode#SINGLE}.
	 * @return this
	 */
	default C singleSelect() {
		return selectionMode(SelectionMode.SINGLE);
	}

	/**
	 * Set the listing selection mode as {@link SelectionMode#MULTI}.
	 * @return this
	 */
	default C multiSelect() {
		return selectionMode(SelectionMode.MULTI);
	}

	/**
	 * Set the listing selection mode as {@link SelectionMode#NONE}.
	 * @return this
	 */
	default C notSelectable() {
		return selectionMode(SelectionMode.NONE);
	}

	/**
	 * Add a {@link SelectionListener} to listen to items selection changes.
	 * <p>
	 * {@link SelectionListener}s are triggred only when listing is selectable, i.e. (i.e.
	 * {@link SelectionMode} is not {@link SelectionMode#NONE}).
	 * </p>
	 * @param selectionListener The selection listener to add (not null)
	 * @return this
	 */
	C withSelectionListener(SelectionListener<T> selectionListener);

	/**
	 * Adds a listener that gets notified when user clicks on an item row.
	 * @param listener The {@link ItemClickListener} to add (not null)
	 * @return this
	 */
	C withItemClickListener(ClickEventListener<L, ItemClickEvent<L, T>> listener);

	/**
	 * Adds a listener that gets notified when the item listing data is refreshed.
	 * <p>
	 * The refresh event is fired when tha data provider detect a data change event, either for all the
	 * items or for only one. When only one item is updated, it is available from
	 * {@link ItemEvent#getItem()}.
	 * </p>
	 * @param listener The {@link ItemEventListener} to add (not null)
	 * @return this
	 */
	C withItemRefreshListener(ItemEventListener<L, T, ItemEvent<L, T>> listener);

	/**
	 * Sets whether multiple column sorting is enabled on the client-side.
	 * @param multiSort <code>true</code> to enable sorting of multiple columns on the client-side,
	 *        <code>false</code> to disable
	 * @return this
	 */
	C multiSort(boolean multiSort);

	/**
	 * Get a {@link ItemListingContextMenuBuilder} to configure and add a context menu to show for each
	 * listing item.
	 * <p>
	 * Use the {@link ItemListingContextMenuBuilder#add()} method to add the context menu to the item
	 * listing.
	 * </p>
	 * <p>
	 * By default, the context menu can be opened with a right click or a long touch on the target
	 * component.
	 * </p>
	 * @return A {@link ItemListingContextMenuBuilder}
	 */
	ItemListingContextMenuBuilder<T, P, L, C> contextMenu();

	/**
	 * Provide a {@link Consumer} to configure the item listing header section, using the
	 * {@link ItemListingSection} API.
	 * @param headerConfigurator The item listing header section configurator (not null)
	 * @return this
	 */
	C header(Consumer<EditableItemListingSection<P>> headerConfigurator);

	/**
	 * Provide a {@link Consumer} to configure the item listing footer section, using the
	 * {@link ItemListingSection} API.
	 * @param footerConfigurator The item listing footer section configurator (not null)
	 * @return this
	 */
	C footer(Consumer<EditableItemListingSection<P>> footerConfigurator);

	/**
	 * Set whether the listing is editable.
	 * <p>
	 * Use <code>withEditorSaveListener</code> to register a listener for editor item save events.
	 * </p>
	 * @param editable whether the listing is editable
	 * @return this
	 */
	C editable(boolean editable);

	/**
	 * Set the listing as editable.
	 * @return this
	 */
	default C editable() {
		return editable(true);
	}

	/**
	 * Set the {@link Component} to display when the column bound to given <code>property</code> is in
	 * editing mode.
	 * @param property The property for which to set the editor component (not null)
	 * @param editorComponentProvider The editor component provider (not null)
	 * @return this
	 */
	C editorComponent(P property, Function<T, ? extends Component> editorComponentProvider);

	/**
	 * Set the {@link Component} to display when the column bound to given <code>property</code> is in
	 * editing mode.
	 * @param property The property for which to set the editor component (not null)
	 * @param editorComponent The editor component (not null)
	 * @return this
	 */
	default C editorComponent(P property, Component editorComponent) {
		return editorComponent(property, item -> editorComponent);
	}

	/**
	 * Set whether the editor is in buffered mode. Default is <code>true</code>.
	 * <p>
	 * When the editor is in buffered mode, edits are only committed when the user clicks the save
	 * button. In unbuffered mode valid changes are automatically committed.
	 * </p>
	 * @param buffered Whether the editor is in buffered mode
	 * @return this
	 */
	C editorBuffered(boolean buffered);

	/**
	 * Adds an item editor listener for editor <code>save</code> events.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withEditorSaveListener(EditorSaveListener<T, P> listener);

	/**
	 * Adds an item editor listener for editor <code>cancel</code> events.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withEditorCancelListener(EditorCancelListener<T, P> listener);

	/**
	 * Adds an item editor save listener for editor <code>open</code> events.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withEditorOpenListener(EditorOpenListener<T, P> listener);

	/**
	 * Adds an item editor save listener for editor <code>close</code> events.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withEditorCloseListener(EditorCloseListener<T, P> listener);

	/**
	 * Set whether the listing is <em>frozen</em>.
	 * <p>
	 * When the listing is <em>frozen</em>, it never shows any item and no fetch is performed from the
	 * data provider.
	 * </p>
	 * <p>
	 * When the {@link ItemListing#refresh()} method is called, the frozen state is automatically set to
	 * <code>false</code>.
	 * </p>
	 * @param frozen Whether the listing is <em>frozen</em>
	 * @since 5.2.2
	 * @return this
	 */
	C frozen(boolean frozen);

	/**
	 * Sets whether the user can drag the grid rows or not.
	 * @param rowsDraggable <code>true</code> if the rows can be dragged by the user, <code>false</code>
	 *        if not
	 * @return this
	 */
	C rowsDraggable(boolean rowsDraggable);

	/**
	 * Sets the drag filter for this drag source.
	 * <p>
	 * When the rows dragging is enabled, by default all the visible rows can be dragged.
	 * </p>
	 * <p>
	 * A drag filter function can be used to specify the rows that are available for dragging. The
	 * function receives an item and returns <code>true</code> if the row can be dragged,
	 * <code>false</code> otherwise.
	 * </p>
	 * <p>
	 * <em>NOTE: If the filtering conditions change dynamically, remember to explicitly invoke
	 * {@link ItemListing#refreshItem(Object)} for the relevant items to get the filters re-run for
	 * them.</em>
	 * </p>
	 * @param dragFilter The drag filter predicate (not null)
	 * @return this
	 */
	C dragFilter(Predicate<T> dragFilter);

	/**
	 * Sets a generator function for customizing drag data. The generated value will be accessible using
	 * the same <code>type</code> as the generator is set here. The function is executed for each item
	 * in the listing during data generation. Return a {@link String} to be appended to the row as
	 * <code>type</code> data.
	 * @param type Type of the generated data. The generated value will be accessible during drop using
	 *        this type (not null)
	 * @param dragDataGenerator Function to be executed on row data generation (not null)
	 * @return this
	 */
	C dragDataGenerator(String type, Function<T, String> dragDataGenerator);

	/**
	 * Adds a listing drag start listener.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withDragStartListener(ItemListingDnDListener<T, P, ItemListingDragStartEvent<T, P>> listener);

	/**
	 * Adds a listing drag end listener.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withDragEndListener(ItemListingDnDListener<T, P, ItemListingDragEndEvent<T, P>> listener);

	/**
	 * Sets the drop mode of this drop target. When set to not <code>null</code>, the listing fires drop
	 * events upon data drop over the listing or the listing rows.
	 * <p>
	 * When using {@link GridDropMode#ON_TOP}, and the listing is either empty or has empty space after
	 * the last row, the drop can still happen on the empty space, and the drop target item will not be
	 * available.
	 * </p>
	 * <p>
	 * When using {@link GridDropMode#BETWEEN} or {@link GridDropMode#ON_TOP_OR_BETWEEN}, and there is
	 * at least one row in the listing, any drop after the last row in the grid will get the last row as
	 * the drop target item. If there are no rows in the listing, then the drop target item will not be
	 * available.
	 * </p>
	 * <p>
	 * If using {@link GridDropMode#ON_GRID}, then the drop will not happen on any row, but instead just
	 * "on the listing". The target row will not be present in this case.
	 * </p>
	 * <p>
	 * <em>NOTE: Prefer not using a row specific {@link GridDropMode} with a listing that enables
	 * sorting. If for example a new row gets added to a specific location on drop event, it might not
	 * end up in the location of the drop but rather where the active sorting configuration prefers to
	 * place it. This behavior might feel unexpected for the users.</em>
	 * </p>
	 * @param dropMode Drop mode that describes the allowed drop locations within the listing's row. Can
	 *        be <code>null</code> to disable dropping
	 * @return this
	 */
	C dropMode(GridDropMode dropMode);

	/**
	 * Sets the drop filter for this drag target.
	 * <p>
	 * When the drop mode of the grid has been set to one of {@link GridDropMode#BETWEEN},
	 * {@link GridDropMode#ON_TOP} or {@link GridDropMode#ON_TOP_OR_BETWEEN}, by default all the visible
	 * rows can be dropped over.
	 * </p>
	 * <p>
	 * A drop filter function can be used to specify the rows that are available for dropping over. The
	 * function receives an item and should return <code>true</code> if the row can be dropped over,
	 * <code>false</code> otherwise.
	 * </p>
	 * <p>
	 * <em>NOTE: If the filtering conditions change dynamically, remember to explicitly invoke
	 * {@link ItemListing#refreshItem(Object)} for the relevant items to get the filters re-run for
	 * them.</em>
	 * </p>
	 * @param dropFilter The drop filter predicate (not null)
	 * @return this
	 */
	C dropFilter(Predicate<T> dropFilter);

	/**
	 * Adds a listing drop listener.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withDropListener(ItemListingDnDListener<T, P, ItemListingDropEvent<T, P>> listener);

	/**
	 * Add listener for column resize events.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withColumnResizeListener(ColumnResizeListener<T, P> listener);

	/**
	 * Add listener for column reorder events.
	 * @param listener The listener to add (not null)
	 * @return this
	 */
	C withColumnReorderListener(ColumnReorderListener<T, P> listener);

	/**
	 * Set a default text that should be displayed when the listing has no data
	 * @param text The text to display inside the empty listing
	 * @return this
	 */
	C emptyStateText(String text);

	/**
	 * Set a default component that should be displayed when the listing has no data
	 * @param component The component to display inside the empty listing
	 * @return this
	 */
	C emptyStateComponent(Component component);

	// -------

	/**
	 * Enumeration of column text alignments.
	 */
	public enum ColumnAlignment {

		/**
		 * Left aligned column text
		 */
		LEFT,

		/**
		 * Centered column text
		 */
		CENTER,

		/**
		 * Right aligned column text
		 */
		RIGHT;

	}

	/**
	 * Base ItemListing column configurator.
	 * 
	 * @param <C> Concrete configurator type
	 * 
	 * @since 5.2.11
	 */
	public interface BaseItemListingColumnConfigurator<C extends BaseItemListingColumnConfigurator<C>> {

		/**
		 * Set whether the column is user-resizable.
		 * @param resizable Whether the column is user-resizable
		 * @return this
		 */
		C resizable(boolean resizable);

		/**
		 * Set whether the column is visible.
		 * @param visible Whether the column is visible
		 * @return this
		 */
		C visible(boolean visible);

		/**
		 * Sets the width of the column as a CSS-string.
		 * @param width the width to set
		 * @return this
		 */
		C width(String width);

		/**
		 * Sets the flex grow ratio for the column.
		 * <p>
		 * When set to 0, column width is fixed.
		 * </p>
		 * @param flexGrow the flex grow ratio to set
		 * @return this
		 */
		C flexGrow(int flexGrow);

		/**
		 * Enables or disables automatic width for the column.
		 * <p>
		 * Automatically sets the width of the column based on the columncontents when this is set to true.
		 * </p>
		 * <p>
		 * For performance reasons the column width is calculated automaticallyonly once when the grid items
		 * are rendered for the first time and thecalculation only considers the rows which are currently
		 * rendered inDOM (a bit more than what is currently visible). If the grid isscrolled, or the cell
		 * content changes, the column width might notmatch the contents anymore.
		 * </p>
		 * @param autoWidth Whether to enable the column auto-width
		 * @return this
		 * @since 5.3.0
		 */
		C autoWidth(boolean autoWidth);

		/**
		 * Enables automatic width for the column.
		 * <p>
		 * Automatically sets the width of the column based on the columncontents when this is set to true.
		 * </p>
		 * <p>
		 * For performance reasons the column width is calculated automaticallyonly once when the grid items
		 * are rendered for the first time and thecalculation only considers the rows which are currently
		 * rendered inDOM (a bit more than what is currently visible). If the grid isscrolled, or the cell
		 * content changes, the column width might notmatch the contents anymore.
		 * </p>
		 * @return this
		 * @since 5.3.0
		 */
		default C autoWidth() {
			return autoWidth(true);
		}

		/**
		 * Sets the column text alignment.
		 * @param alignment the text alignment to set
		 * @return this
		 */
		C alignment(ColumnAlignment alignment);

	}

	/**
	 * ItemListing column configurator.
	 *
	 * @param <T> Item type
	 * @param <P> Item property type
	 * @param <C> Concrete configurator type
	 * 
	 * @since 5.2.0
	 */
	public interface ItemListingColumnConfigurator<T, P, C extends ItemListingColumnConfigurator<T, P, C>>
			extends BaseItemListingColumnConfigurator<C> {

		/**
		 * Set whether the column is frozen.
		 * @param frozen Whether the column is frozen
		 * @return this
		 */
		C frozen(boolean frozen);

		/**
		 * Set the CSS style class name generator to use for the column.
		 * <p>
		 * Returning <code>null</code> from the generator results in no custom class name being set.
		 * Multiple class names can be returned from the generator as space-separated.
		 * </p>
		 * @param styleNameGenerator The function to use to generate the cell CSS style class name
		 * @return this
		 * @since 5.2.3
		 */
		C styleNameGenerator(Function<T, String> styleNameGenerator);

		/**
		 * Sets comparator to use with in-memory sorting for the column.
		 * @param comparator The comparator to use with in-memory sorting
		 * @return this
		 */
		C sortComparator(Comparator<T> comparator);

		/**
		 * Set the properties to use to implement the sort logic to apply when the column is user-sorted.
		 * @param sortProperties The properties to use to sort the column
		 * @return this
		 */
		C sortUsing(List<P> sortProperties);

		/**
		 * Set the properties to use to implement the sort logic to apply when the column is user-sorted.
		 * @param sortProperties The properties to use to sort the column
		 * @return this
		 */
		@SuppressWarnings("unchecked")
		default C sortUsing(P... sortProperties) {
			return sortUsing(Arrays.asList(sortProperties));
		}

		/**
		 * Set the function to use to obtain the {@link ItemSort}s to use when the column is user-sorted.
		 * @param sortProvider Sort provider
		 * @return this
		 */
		C sortProvider(Function<SortDirection, Stream<ItemSort<P>>> sortProvider);

		/**
		 * Set the header text for the column.
		 * @param header Localizable column header text (not null)
		 * @return this
		 */
		C header(Localizable header);

		/**
		 * Set the header text for the column.
		 * @param header The column header text
		 * @return this
		 */
		default C header(String header) {
			return header(Localizable.builder().message(header).build());
		}

		/**
		 * Set the header text for the column.
		 * @param defaultHeader The default column header text
		 * @param headerMessageCode The column header text translation message code
		 * @return this
		 */
		default C header(String defaultHeader, String headerMessageCode) {
			return header(Localizable.builder().message(defaultHeader).messageCode(headerMessageCode).build());
		}

		/**
		 * Set the {@link Component} to use as header for the column.
		 * @param header The column header component
		 * @return this
		 */
		C headerComponent(Component header);

		/**
		 * Set the {@link Component} to display when the column is in editing mode.
		 * @param editorComponentProvider The editor component provider (not null)
		 * @return this
		 */
		C editorComponent(Function<T, ? extends Component> editorComponentProvider);

		/**
		 * Set the {@link Component} to display when the column is in editing mode.
		 * @param editorComponent The editor component (not null)
		 * @return this
		 */
		default C editorComponent(Component editorComponent) {
			return editorComponent(item -> editorComponent);
		}

		/**
		 * Configure the column to be displayed before any other listing column.
		 * @return this
		 */
		C displayAsFirst();

		/**
		 * Configure the column to be displayed after any other listing column by default.
		 * @return this
		 */
		C displayAsLast();

		/**
		 * Configure the column to be displayed before the column which corresponds to the id specified by
		 * the given <code>beforeProperty</code>.
		 * @param beforeProperty Property which represents the column before which this column has to be
		 *        displayed (not null)
		 * @return this
		 */
		C displayBefore(P beforeProperty);

		/**
		 * Configure the column to be displayed after the column which corresponds to the id specified by
		 * the given <code>afterProperty</code>.
		 * @param afterProperty Property which represents the column after which this column has to be
		 *        displayed (not null)
		 * @return this
		 */
		C displayAfter(P afterProperty);

	}

	/**
	 * ItemListing column builder.
	 * 
	 * @param <T> Item type
	 * @param <P> Item property type
	 * @param <L> Item listing type
	 * @param <B> Parent builder type
	 * 
	 * @since 5.2.0
	 */
	public interface ItemListingColumnBuilder<T, P, L extends ItemListing<T, P>, B extends ItemListingConfigurator<T, P, L, B>>
			extends ItemListingColumnConfigurator<T, P, ItemListingColumnBuilder<T, P, L, B>> {

		/**
		 * Add the column to the listing.
		 * @return The parent listing builder
		 */
		B add();

	}

	/**
	 * Listing column configurator to be used in a column post processor.
	 * 
	 * @since 5.2.11
	 */
	public interface ColumnConfigurator extends BaseItemListingColumnConfigurator<ColumnConfigurator> {

	}

	/**
	 * A post processor which can be used to furtherly configure a listing column before adding it to
	 * the listing component.
	 *
	 * @param <P> Property type
	 * 
	 * @since 5.2.11
	 */
	@FunctionalInterface
	public interface ColumnPostProcessor<P> {

		/**
		 * Configure the column which corresponds to given <code>property</code>.
		 * @param property The property which identifies the column
		 * @param header The column header
		 * @param configurator The {@link ColumnConfigurator} which can be used to configure the column
		 */
		void configureColumn(P property, String header, ColumnConfigurator configurator);

	}

	/**
	 * {@link ItemListing} context menu builder.
	 * 
	 * @param <T> Item type
	 * @param <P> Item property type
	 * @param <L> Item listing type
	 * @param <B> Parent item listing builder type
	 *
	 * @since 5.2.0
	 */
	public interface ItemListingContextMenuBuilder<T, P, L extends ItemListing<T, P>, B extends ItemListingConfigurator<T, P, L, B>>
			extends
			ContextMenuConfigurator<ItemEventListener<GridMenuItem<T>, T, ItemListingItemEvent<GridMenuItem<T>, T, P>>, GridContextMenu<T>, GridMenuItem<T>, GridSubMenu<T>, ItemListingContextMenuBuilder<T, P, L, B>> {

		/**
		 * Add the context menu to the item listing.
		 * @return The parent item listing builder
		 */
		B add();

	}

	/**
	 * {@link ItemListing} section handler for header and footer configuration.
	 *
	 * @param <P> Item property type
	 */
	public interface EditableItemListingSection<P> extends ItemListingSection<P, EditableItemListingRow<P>> {

		/**
		 * Adds a new row at the bottom of the section.
		 * @return the new row
		 */
		EditableItemListingRow<P> appendRow();

		/**
		 * Adds a new row at the top of the section.
		 * @return the new row
		 */
		EditableItemListingRow<P> prependRow();

	}

	/**
	 * An editable {@link ItemListing} section row handler.
	 * 
	 * @param <P> Item property type
	 */
	public interface EditableItemListingRow<P> extends ItemListingRow<P> {

		/**
		 * Joins the cells corresponding the given columns in the row.
		 * <p>
		 * The columns must be adjacent, and this row must be the out-most row.
		 * </p>
		 * <p>
		 * The way that the client-side web component works also causes some limitations to which cells can
		 * be joined. For example, if you join the first and second cell in the header, you cannot join the
		 * second and third cell in the footer.
		 * </p>
		 * @param properties The properties of the cells to join (not null)
		 * @return the merged cell
		 * @throws IllegalArgumentException if it's not possible to join the given cells
		 */
		ItemListingCell join(Collection<P> properties);

		/**
		 * Joins the cells corresponding the given columns in the row.
		 * <p>
		 * The columns must be adjacent, and this row must be the out-most row.
		 * </p>
		 * <p>
		 * The way that the client-side web component works also causes some limitations to which cells can
		 * be joined. For example, if you join the first and second cell in the header, you cannot join the
		 * second and third cell in the footer.
		 * </p>
		 * @param properties The properties of the cells to join (not null)
		 * @return the merged cell
		 * @throws IllegalArgumentException if it's not possible to join the given cells
		 */
		@SuppressWarnings("unchecked")
		default ItemListingCell join(P... properties) {
			return join(Arrays.asList(properties));
		}

	}

}
