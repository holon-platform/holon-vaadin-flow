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
package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.jdbc.BasicDataSource;
import com.holonplatform.jdbc.DatabasePlatform;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.AbstractItemListing;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.ValueProvider;

public class TestPropertyListing {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name").messageCode("test.code");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> pb.containsValue(NAME) ? "[" + pb.getValue(NAME) + "]" : null);

	private static final PropertySet<?> SET = PropertySet.builderOf(ID, NAME, VIRTUAL).identifier(ID).build();

	@Test
	public void testBuilders() {

		PropertyListingBuilder builder = PropertyListing.builder(SET);
		assertNotNull(builder);
		PropertyListing listing = builder.build();
		assertNotNull(listing);

		builder = PropertyListing.builder(ID, NAME, VIRTUAL);
		assertNotNull(builder);
		listing = builder.build();
		assertNotNull(listing);

		builder = Components.listing.properties(SET);
		assertNotNull(builder);
		listing = builder.build();
		assertNotNull(listing);

	}

	@Test
	public void testComponent() {

		PropertyListing listing = PropertyListing.builder(SET).id("testid").build();
		assertNotNull(listing.getComponent());

		assertTrue(listing.getComponent().getId().isPresent());
		assertEquals("testid", listing.getComponent().getId().get());

		listing = PropertyListing.builder(SET).build();
		assertTrue(listing.isVisible());

		listing = PropertyListing.builder(SET).visible(true).build();
		assertTrue(listing.isVisible());

		listing = PropertyListing.builder(SET).visible(false).build();
		assertFalse(listing.isVisible());

		listing = PropertyListing.builder(SET).hidden().build();
		assertFalse(listing.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		listing = PropertyListing.builder(SET).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(listing.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		listing = PropertyListing.builder(SET).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(listing.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		PropertyListing listing = PropertyListing.builder(SET).styleName("test").build();
		assertNotNull(listing);
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test"));

		listing = PropertyListing.builder(SET).styleNames("test1", "test2").build();
		assertNotNull(listing);
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test2"));

		listing = PropertyListing.builder(SET).withThemeVariants(GridVariant.LUMO_COMPACT).build();
		assertTrue(listing.getComponent() instanceof Grid);

		assertTrue(
				((Grid<?>) listing.getComponent()).getThemeNames().contains(GridVariant.LUMO_COMPACT.getVariantName()));

	}

	@Test
	public void testSize() {

		PropertyListing listing = PropertyListing.builder(SET).width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(listing));
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(listing));
		assertNull(ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(listing));
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

	}

	@Test
	public void testEnabled() {

		PropertyListing listing = PropertyListing.builder(SET).build();
		assertTrue(ComponentTestUtils.isEnabled(listing));

		listing = PropertyListing.builder(SET).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(listing));

		listing = PropertyListing.builder(SET).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(listing));

		listing = PropertyListing.builder(SET).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(listing));

	}

	@Test
	public void testFocus() {

		PropertyListing listing = PropertyListing.builder(SET).tabIndex(77).build();
		assertTrue(listing.getComponent() instanceof Grid);

		assertEquals(77, ((Grid<?>) listing.getComponent()).getTabIndex());

	}

	@Test
	public void testConfiguration() {

		PropertyListing listing = PropertyListing.builder(SET).build();
		assertTrue(listing.getComponent() instanceof Grid);
		assertFalse(((Grid<?>) listing.getComponent()).isColumnReorderingAllowed());
		assertFalse(((Grid<?>) listing.getComponent()).isHeightByRows());
		assertTrue(((Grid<?>) listing.getComponent()).isDetailsVisibleOnClick());
		assertFalse(((Grid<?>) listing.getComponent()).isMultiSort());
		assertTrue(((Grid<?>) listing.getComponent()).isVerticalScrollingEnabled());

		listing = PropertyListing.builder(SET).columnReorderingAllowed(true).build();
		assertTrue(((Grid<?>) listing.getComponent()).isColumnReorderingAllowed());

		listing = PropertyListing.builder(SET).heightByRows(true).build();
		assertTrue(((Grid<?>) listing.getComponent()).isHeightByRows());

		listing = PropertyListing.builder(SET).itemDetailsVisibleOnClick(false).build();
		assertFalse(((Grid<?>) listing.getComponent()).isDetailsVisibleOnClick());

		listing = PropertyListing.builder(SET).multiSort(true).build();
		// assertTrue(((Grid<?>) listing.getComponent()).isMultiSort());
		assertNotNull(((Grid<?>) listing.getComponent()).getElement().getAttribute("multi-sort"));

		listing = PropertyListing.builder(SET).pageSize(100).build();
		assertEquals(100, ((Grid<?>) listing.getComponent()).getPageSize());

		listing = PropertyListing.builder(SET).verticalScrollingEnabled(false).build();
		assertFalse(((Grid<?>) listing.getComponent()).isVerticalScrollingEnabled());

	}

	@Test
	public void testColumnConfiguration() {

		PropertyListing listing = PropertyListing.builder(SET).build();

		final ItemListingColumn<?, ?, ?> c = getImpl(listing).getColumnConfiguration(ID);
		assertNotNull(c.getColumnKey());
		assertEquals(ID.getName(), c.getColumnKey());
		assertFalse(c.isReadOnly());
		assertFalse(c.isFrozen());
		assertTrue(c.isVisible());
		assertEquals(SortMode.ENABLED, c.getSortMode());
		assertTrue(c.getSortProperties().size() > 0);

		final ItemListingColumn<?, ?, ?> c2 = getImpl(listing).getColumnConfiguration(VIRTUAL);
		assertTrue(c2.isReadOnly());
		assertNotNull(c2.getColumnKey());
		assertEquals(SortMode.DEFAULT, c2.getSortMode());
		assertTrue(c2.getSortProperties().isEmpty());

		listing = PropertyListing.builder(SET).readOnly(ID, true).build();
		assertTrue(getImpl(listing).getColumnConfiguration(ID).isReadOnly());

		listing = PropertyListing.builder(SET).visible(ID, false).build();
		assertFalse(getImpl(listing).getColumnConfiguration(ID).isVisible());

		listing = PropertyListing.builder(SET).resizable(ID, true).build();
		assertTrue(getImpl(listing).getColumnConfiguration(ID).isResizable());

		listing = PropertyListing.builder(SET).frozen(ID, true).build();
		assertTrue(getImpl(listing).getColumnConfiguration(ID).isFrozen());

		listing = PropertyListing.builder(SET).width(ID, "50px").build();
		assertEquals("50px", getImpl(listing).getColumnConfiguration(ID).getWidth().orElse(null));

		listing = PropertyListing.builder(SET).flexGrow(ID, 1).build();
		assertEquals(1, getImpl(listing).getColumnConfiguration(ID).getFlexGrow());

		listing = PropertyListing.builder(SET).alignment(ID, ColumnAlignment.RIGHT)
				.alignment(NAME, ColumnAlignment.CENTER).build();
		assertEquals(ColumnAlignment.RIGHT, getImpl(listing).getColumnConfiguration(ID).getAlignment().orElse(null));
		assertEquals(ColumnAlignment.CENTER, getImpl(listing).getColumnConfiguration(NAME).getAlignment().orElse(null));
		assertEquals(ColumnAlignment.LEFT,
				getImpl(listing).getColumnConfiguration(VIRTUAL).getAlignment().orElse(ColumnAlignment.LEFT));

		listing = PropertyListing.builder(SET).header(ID, "test").build();
		assertEquals("test", LocalizationContext
				.translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

		listing = PropertyListing.builder(SET).header(ID, "test", "mc").build();
		assertEquals("test", LocalizationContext
				.translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

		listing = PropertyListing.builder(SET).header(ID, Localizable.of("test")).build();
		assertEquals("test", LocalizationContext
				.translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

		final Button btn = new Button("test");

		listing = PropertyListing.builder(SET).headerComponent(ID, btn).build();
		assertEquals(btn, getImpl(listing).getColumnConfiguration(ID).getHeaderComponent().orElse(null));

		final Renderer<PropertyBox> rnd = TemplateRenderer.of("test");
		listing = PropertyListing.builder(SET).renderer(ID, rnd).build();
		assertEquals(rnd, getImpl(listing).getColumnConfiguration(ID).getRenderer().orElse(null));

		final ValueProvider<PropertyBox, String> vp = item -> "test";
		listing = PropertyListing.builder(SET).valueProvider(ID, vp).build();
		assertEquals(vp, getImpl(listing).getColumnConfiguration(ID).getValueProvider().orElse(null));

		final Comparator<PropertyBox> cmp = Comparator.<PropertyBox, Long>comparing(v -> v.getValue(ID),
				Comparator.comparingLong(k -> k));
		listing = PropertyListing.builder(SET).sortComparator(ID, cmp).build();
		assertEquals(cmp, getImpl(listing).getColumnConfiguration(ID).getComparator().orElse(null));

		listing = PropertyListing.builder(SET).sortUsing(VIRTUAL, ID).build();
		assertEquals(ID, getImpl(listing).getColumnConfiguration(VIRTUAL).getSortProperties().get(0));

		final Input<Long> edt = Input.number(Long.class).build();
		listing = PropertyListing.builder(SET).editor(ID, edt).build();
		assertEquals(edt, getImpl(listing).getColumnConfiguration(ID).getEditor().orElse(null));

		final Validator<Long> vdt = Validator.max(3);
		listing = PropertyListing.builder(SET).withValidator(ID, vdt).build();
		assertEquals(vdt, getImpl(listing).getColumnConfiguration(ID).getValidators().get(0));

	}

	@Test
	public void testColumns() {

		PropertyListing listing = PropertyListing.builder(SET).build();
		assertTrue(listing.getComponent() instanceof Grid);

		final Grid<?> grid = (Grid<?>) listing.getComponent();
		assertEquals(3, grid.getColumns().size());

		assertNotNull(getImpl(listing).getColumnConfiguration(ID).getColumnKey());
		assertNotNull(getImpl(listing).getColumnConfiguration(NAME).getColumnKey());
		assertNotNull(getImpl(listing).getColumnConfiguration(VIRTUAL).getColumnKey());

		assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(ID).getColumnKey()));
		assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(NAME).getColumnKey()));
		assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(VIRTUAL).getColumnKey()));
	}

	@Test
	public void testDefaultHeader() {

		PropertyListing listing = PropertyListing.builder(SET).build();

		assertTrue(listing.getHeader().isPresent());
		assertEquals(1, listing.getHeader().get().getRows().size());
		assertTrue(listing.getHeader().get().getFirstRow().isPresent());

		assertTrue(listing.getComponent() instanceof Grid);
		final Grid<?> grid = (Grid<?>) listing.getComponent();
		assertEquals(3, grid.getColumns().size());

		Column<?> c1 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(ID).getColumnKey());
		Column<?> c2 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(NAME).getColumnKey());
		Column<?> c3 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(VIRTUAL).getColumnKey());

		assertNotNull(c1);
		assertNotNull(c2);
		assertNotNull(c3);
	}

	@Test
	public void testHeader() {

		PropertyListing listing = PropertyListing.builder(SET).build();

		assertTrue(listing.getHeader().isPresent());

		assertEquals(1, listing.getHeader().get().getRows().size());
		assertTrue(listing.getHeader().get().getFirstRow().isPresent());

		assertEquals(3, listing.getHeader().get().getFirstRow().get().getCells().size());
		assertTrue(listing.getHeader().get().getFirstRow().get().getCell(ID).isPresent());
		assertTrue(listing.getHeader().get().getFirstRow().get().getCell(NAME).isPresent());

		listing = PropertyListing.builder(SET).header(VIRTUAL, "virtual").header(header -> {
			header.prependRow().join(ID, NAME).setText("joined");
		}).build();

		assertEquals(2, listing.getHeader().get().getRows().size());
		assertEquals(2, listing.getHeader().get().getRows().get(0).getCells().size());
		assertEquals(3, listing.getHeader().get().getRows().get(1).getCells().size());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			PropertyListing listing2 = PropertyListing.builder(SET).header(VIRTUAL, "virtual").header(header -> {
				header.prependRow().join(ID, NAME)
						.setText(Localizable.builder().message("test").messageCode("test.code").build());
			}).build();
			assertEquals(2, listing2.getHeader().get().getRows().size());
		});
	}

	@Test
	public void testFooter() {

		PropertyListing listing = PropertyListing.builder(SET).footer(footer -> {
			footer.appendRow().getCell(ID).get().setText("id");
		}).build();

		assertTrue(listing.getFooter().isPresent());
		assertEquals(1, listing.getFooter().get().getRows().size());

	}

	@Test
	public void testItemsDataSource() {

		final PropertyBox ITEM1 = PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test1").build();
		final PropertyBox ITEM2 = PropertyBox.builder(SET).set(ID, 2L).set(NAME, "test2").build();

		PropertyListing listing = PropertyListing.builder(SET).items(Arrays.asList(ITEM1, ITEM2)).build();

		List<PropertyBox> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

		listing = PropertyListing.builder(SET).items(ITEM1, ITEM2).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

		listing = PropertyListing.builder(SET).addItem(ITEM1).addItem(ITEM2).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

		final List<PropertyBox> itemList = Arrays.asList(ITEM1, ITEM2);

		listing = PropertyListing.builder(SET).dataSource(DataProvider.fromCallbacks(q -> itemList.stream(), q -> 2))
				.build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDatastoreDataSource() {

		final DataTarget<?> TARGET = DataTarget.named("test2");

		final Datastore datastore = JdbcDatastore.builder()
				.dataSource(
						BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
								.username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
				.traceEnabled(true).build();

		PropertyListing listing = PropertyListing.builder(SET).dataSource(datastore, TARGET).build();

		List<PropertyBox> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.stream().filter(i -> i.getValue(ID).longValue() == 1L).findFirst().isPresent());
		assertTrue(items.stream().filter(i -> i.getValue(ID).longValue() == 2L).findFirst().isPresent());

		listing = PropertyListing.builder(SET).dataSource(datastore, TARGET).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.stream().filter(i -> i.getValue(ID).longValue() == 1L).findFirst().isPresent());
		assertTrue(items.stream().filter(i -> i.getValue(ID).longValue() == 2L).findFirst().isPresent());

		listing = PropertyListing.builder(SET).dataSource(datastore, TARGET).withQueryFilter(ID.lt(2L)).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(1, items.size());
		assertTrue(items.stream().filter(i -> i.getValue(ID).longValue() == 1L).findFirst().isPresent());
		assertFalse(items.stream().filter(i -> i.getValue(ID).longValue() == 2L).findFirst().isPresent());

		listing = PropertyListing.builder(SET).dataSource(datastore, TARGET)
				.withQueryConfigurationProvider(new QueryConfigurationProvider() {

					@Override
					public QueryFilter getQueryFilter() {
						return ID.lt(2L);
					}
				}).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(1, items.size());
		assertTrue(items.stream().filter(i -> i.getValue(ID).longValue() == 1L).findFirst().isPresent());
		assertFalse(items.stream().filter(i -> i.getValue(ID).longValue() == 2L).findFirst().isPresent());

		listing = PropertyListing.builder(SET).dataSource(datastore, TARGET).withQuerySort(NAME.desc()).build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertEquals(Long.valueOf(2L), items.get(0).getValue(ID));
		assertEquals(Long.valueOf(1L), items.get(1).getValue(ID));

		listing = PropertyListing.builder(SET).dataSource(datastore, TARGET).withQueryFilter(ID.loe(2L))
				.withQuerySort(NAME.desc()).build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertEquals(Long.valueOf(2L), items.get(0).getValue(ID));
		assertEquals(Long.valueOf(1L), items.get(1).getValue(ID));

		listing = PropertyListing.builder(SET).dataSource(datastore, TARGET).withDefaultQuerySort(NAME.desc()).build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertEquals(Long.valueOf(2L), items.get(0).getValue(ID));
		assertEquals(Long.valueOf(1L), items.get(1).getValue(ID));

		listing.sort(ItemSort.asc(ID));
		List<QuerySortOrder> bs = getDataCommunicator(listing).getBackEndSorting();
		assertEquals(1, bs.size());
		assertEquals(ID.getName(), bs.get(0).getSorted());
		assertEquals(SortDirection.ASCENDING, bs.get(0).getDirection());

		listing.sort(Collections.singletonList(ItemSort.desc(ID)));
		bs = getDataCommunicator(listing).getBackEndSorting();
		assertEquals(1, bs.size());
		assertEquals(ID.getName(), bs.get(0).getSorted());
		assertEquals(SortDirection.DESCENDING, bs.get(0).getDirection());

	}

	@Test
	public void testRefresh() {

		final DataTarget<?> TARGET = DataTarget.named("test2");

		final Datastore datastore = JdbcDatastore.builder()
				.dataSource(
						BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
								.username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
				.traceEnabled(true).build();

		final AtomicInteger fired = new AtomicInteger(0);
		final ItemValue value = new ItemValue();

		PropertyListing listing = PropertyListing.builder(SET).dataSource(datastore, TARGET)
				.withItemRefreshListener(e -> {
					fired.incrementAndGet();
					value.item = e.getItem();
				}).build();

		assertNull(value.item);
		assertEquals(0, fired.get());

		listing.refresh();
		assertNull(value.item);
		assertEquals(1, fired.get());

		PropertyBox itm = datastore.query(TARGET).filter(ID.eq(1L)).findOne(SET)
				.orElseThrow(() -> new RuntimeException("item not found"));
		listing.refreshItem(itm);

		assertNotNull(value.item);
		assertEquals(itm, value.item);
		assertEquals(2, fired.get());

	}

	private class ItemValue {

		public PropertyBox item;

	}

	@Test
	public void testSelectable() {

		final PropertyBox ITEM1 = PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test1").build();
		final PropertyBox ITEM2 = PropertyBox.builder(SET).set(ID, 2L).set(NAME, "test2").build();

		PropertyListing listing = PropertyListing.builder(SET).items(ITEM1, ITEM2).build();

		assertEquals(SelectionMode.NONE, listing.getSelectionMode());
		assertEquals(0, listing.getSelectedItems().size());
		assertFalse(listing.getFirstSelectedItem().isPresent());
		assertFalse(listing.isSelected(ITEM1));
		assertFalse(listing.isSelected(ITEM2));

		assertThrows(IllegalStateException.class, () -> listing.select(ITEM1));

		PropertyListing listing2 = PropertyListing.builder(SET).items(ITEM1, ITEM2).selectionMode(SelectionMode.SINGLE)
				.build();
		assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
		assertEquals(0, listing2.getSelectedItems().size());
		assertFalse(listing2.getFirstSelectedItem().isPresent());

		listing2.select(ITEM2);
		assertEquals(1, listing2.getSelectedItems().size());
		assertTrue(listing2.getFirstSelectedItem().isPresent());
		assertEquals(ITEM2, listing2.getFirstSelectedItem().orElse(null));
		assertFalse(listing2.isSelected(ITEM1));
		assertTrue(listing2.isSelected(ITEM2));

		listing2.deselect(ITEM2);
		assertEquals(0, listing2.getSelectedItems().size());
		assertFalse(listing2.getFirstSelectedItem().isPresent());

		listing2.select(ITEM1);
		listing2.deselect(ITEM2);
		assertEquals(1, listing2.getSelectedItems().size());
		assertTrue(listing2.getFirstSelectedItem().isPresent());
		assertTrue(listing2.isSelected(ITEM1));

		listing2.deselectAll();
		assertEquals(0, listing2.getSelectedItems().size());
		assertFalse(listing2.getFirstSelectedItem().isPresent());

		listing2 = PropertyListing.builder(SET).items(ITEM1, ITEM2).singleSelect().build();
		assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
		listing2.select(ITEM2);
		assertEquals(1, listing2.getSelectedItems().size());

		listing2 = PropertyListing.builder(SET).items(ITEM1, ITEM2).build();
		listing2.setSelectionMode(SelectionMode.SINGLE);
		assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
		listing2.select(ITEM2);
		assertEquals(1, listing2.getSelectedItems().size());

		listing2 = PropertyListing.builder(SET).items(ITEM1, ITEM2).multiSelect().build();
		assertEquals(SelectionMode.MULTI, listing2.getSelectionMode());
		assertEquals(0, listing.getSelectedItems().size());

		listing2.select(ITEM1);
		listing2.select(ITEM2);
		assertEquals(2, listing2.getSelectedItems().size());
		assertTrue(listing2.isSelected(ITEM1));
		assertTrue(listing2.isSelected(ITEM2));

		final Set<PropertyBox> selected = new HashSet<>();

		listing2 = PropertyListing.builder(SET).items(ITEM1, ITEM2).singleSelect().withSelectionListener(e -> {
			selected.clear();
			selected.addAll(e.getAllSelectedItems());
		}).build();

		assertEquals(0, selected.size());

		listing2.select(ITEM1);

		assertEquals(1, selected.size());
		assertTrue(selected.contains(ITEM1));

		listing2.select(ITEM2);

		assertEquals(1, selected.size());
		assertTrue(selected.contains(ITEM2));

		listing2.deselectAll();
		assertEquals(0, selected.size());
		assertFalse(selected.contains(ITEM1));
		assertFalse(selected.contains(ITEM2));

		selected.clear();

		listing2 = PropertyListing.builder(SET).items(ITEM1, ITEM2).singleSelect().multiSelect()
				.withSelectionListener(e -> {
					selected.clear();
					selected.addAll(e.getAllSelectedItems());
				}).build();

		assertEquals(0, selected.size());

		listing2.select(ITEM1);

		assertEquals(1, selected.size());
		assertTrue(selected.contains(ITEM1));

		listing2.select(ITEM2);

		assertEquals(2, selected.size());
		assertTrue(selected.contains(ITEM1));
		assertTrue(selected.contains(ITEM2));

		listing2.deselectAll();
		assertEquals(0, selected.size());
		assertFalse(selected.contains(ITEM1));
		assertFalse(selected.contains(ITEM2));

	}

	@Test
	public void testVisibleColumns() {

		PropertyListing listing = PropertyListing.builder(SET).visibleColumns(NAME, ID).build();

		List<Property<?>> properties = ConversionUtils.iterableAsList(listing.getProperties());
		assertEquals(3, properties.size());
		assertTrue(properties.contains(ID));
		assertTrue(properties.contains(NAME));
		assertTrue(properties.contains(VIRTUAL));

		List<Property<?>> visible = listing.getVisibleColumns();
		assertEquals(2, visible.size());
		assertEquals(NAME, visible.get(0));
		assertEquals(ID, visible.get(1));

		listing = PropertyListing.builder(SET).visibleColumns(NAME, ID).build();
		listing.setColumnVisible(ID, false);

		visible = listing.getVisibleColumns();
		assertEquals(1, visible.size());
		assertTrue(visible.contains(NAME));
		assertFalse(visible.contains(ID));

		listing.setColumnVisible(ID, true);
		visible = listing.getVisibleColumns();
		assertEquals(2, visible.size());
		assertTrue(visible.contains(NAME));
		assertTrue(visible.contains(ID));

		listing = PropertyListing.builder(SET).visible(ID, false).build();
		visible = listing.getVisibleColumns();
		assertEquals(2, visible.size());
		assertTrue(visible.contains(NAME));
		assertTrue(visible.contains(VIRTUAL));
		assertFalse(visible.contains(ID));

	}

	@Test
	public void testComponentColumns() {

		final VirtualProperty<Component> VC = VirtualProperty.create(Component.class,
				item -> new Button(item.getValue(NAME)));

		PropertyListing listing = PropertyListing.builder(ID, NAME).withComponentColumn(VC).add().build();

		List<Property<?>> visible = listing.getVisibleColumns();
		assertEquals(3, visible.size());
		assertEquals(ID, visible.get(0));
		assertEquals(NAME, visible.get(1));
		assertEquals(VC, visible.get(2));

		ItemListingColumn<?, ?, ?> c = getImpl(listing).getColumnConfiguration(VC);
		assertNotNull(c.getColumnKey());
		assertTrue(c.isReadOnly());
		assertFalse(c.isFrozen());
		assertTrue(c.isVisible());
		assertEquals(SortMode.DEFAULT, c.getSortMode());
		assertTrue(c.getSortProperties().size() == 0);

		listing = PropertyListing.builder(ID, NAME).withComponentColumn(VC).frozen(true).sortUsing(ID).displayAsFirst()
				.add().build();

		visible = listing.getVisibleColumns();
		assertEquals(3, visible.size());
		assertEquals(VC, visible.get(0));
		assertEquals(ID, visible.get(1));
		assertEquals(NAME, visible.get(2));

		c = getImpl(listing).getColumnConfiguration(VC);
		assertNotNull(c.getColumnKey());
		assertTrue(c.isReadOnly());
		assertTrue(c.isFrozen());
		assertTrue(c.isVisible());
		assertEquals(SortMode.ENABLED, c.getSortMode());
		assertTrue(c.getSortProperties().size() == 1);
		assertEquals(ID, c.getSortProperties().get(0));

		listing = PropertyListing.builder(ID, NAME).withComponentColumn(item -> new Button(item.getValue(NAME)))
				.displayAfter(ID).add().build();

		visible = listing.getVisibleColumns();
		assertEquals(3, visible.size());
		assertEquals(ID, visible.get(0));
		assertTrue(Component.class.isAssignableFrom(visible.get(1).getType()));
		assertEquals(NAME, visible.get(2));

		listing = PropertyListing.builder(ID, NAME).withComponentColumn(item -> new Button(item.getValue(NAME)))
				.displayBefore(ID).add().build();

		visible = listing.getVisibleColumns();
		assertEquals(3, visible.size());
		assertTrue(Component.class.isAssignableFrom(visible.get(0).getType()));
		assertEquals(ID, visible.get(1));
		assertEquals(NAME, visible.get(2));

	}

	@Test
	public void testItemDetails() {

		final PropertyBox ITEM1 = PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test1").build();
		final PropertyBox ITEM2 = PropertyBox.builder(SET).set(ID, 2L).set(NAME, "test2").build();

		PropertyListing listing = PropertyListing.builder(SET).items(ITEM1, ITEM2)
				.itemDetailsText(item -> item.getValue(NAME)).build();

		assertFalse(listing.isItemDetailsVisible(ITEM1));

		listing.setItemDetailsVisible(ITEM1, true);
		assertTrue(listing.isItemDetailsVisible(ITEM1));
		assertFalse(listing.isItemDetailsVisible(ITEM2));

		listing.setItemDetailsVisible(ITEM1, false);
		assertFalse(listing.isItemDetailsVisible(ITEM1));
	}

	@Test
	public void testPropertySet() {

		PropertyListing listing = PropertyListing.builder(SET).build();

		assertTrue(listing.hasProperty(ID));
		assertTrue(listing.hasProperty(NAME));
		assertTrue(listing.hasProperty(VIRTUAL));

		List<Property<?>> properties = ConversionUtils.iterableAsList(listing.getProperties());
		assertEquals(3, properties.size());
		assertTrue(properties.contains(ID));
		assertTrue(properties.contains(NAME));
		assertTrue(properties.contains(VIRTUAL));

		properties = listing.propertyStream().collect(Collectors.toList());
		assertEquals(3, properties.size());
		assertTrue(properties.contains(ID));
		assertTrue(properties.contains(NAME));
		assertTrue(properties.contains(VIRTUAL));

	}

	@SuppressWarnings("unchecked")
	private static DataProvider<PropertyBox, ?> getDataProvider(PropertyListing listing) {
		assertTrue(listing.getComponent() instanceof Grid);
		return ((Grid<PropertyBox>) listing.getComponent()).getDataProvider();
	}

	@SuppressWarnings("unchecked")
	private static DataCommunicator<PropertyBox> getDataCommunicator(PropertyListing listing) {
		assertTrue(listing.getComponent() instanceof Grid);
		return ((Grid<PropertyBox>) listing.getComponent()).getDataCommunicator();
	}

	@SuppressWarnings("unchecked")
	private static AbstractItemListing<PropertyBox, Property<?>> getImpl(PropertyListing listing) {
		assertTrue(listing instanceof AbstractItemListing);
		return (AbstractItemListing<PropertyBox, Property<?>>) listing;
	}

}
