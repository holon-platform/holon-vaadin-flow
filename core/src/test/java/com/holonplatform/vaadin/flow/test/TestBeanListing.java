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
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryProjection;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.jdbc.BasicDataSource;
import com.holonplatform.jdbc.DatabasePlatform;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.AbstractItemListing;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
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

public class TestBeanListing {

	public static class TestBean {

		private long id;
		private String name;

		public TestBean() {
			super();
		}

		public TestBean(long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	private static final String ID = "id";
	private static final String NAME = "name";

	@Test
	public void testBuilders() {

		BeanListingBuilder<TestBean> builder = BeanListing.builder(TestBean.class);
		assertNotNull(builder);
		BeanListing<TestBean> listing = builder.build();
		assertNotNull(listing);

		builder = Components.listing.items(TestBean.class);
		assertNotNull(builder);
		listing = builder.build();
		assertNotNull(listing);

	}

	@Test
	public void testComponent() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).id("testid").build();
		assertNotNull(listing.getComponent());

		assertTrue(listing.getComponent().getId().isPresent());
		assertEquals("testid", listing.getComponent().getId().get());

		listing = BeanListing.builder(TestBean.class).build();
		assertTrue(listing.isVisible());

		listing = BeanListing.builder(TestBean.class).visible(true).build();
		assertTrue(listing.isVisible());

		listing = BeanListing.builder(TestBean.class).visible(false).build();
		assertFalse(listing.isVisible());

		listing = BeanListing.builder(TestBean.class).hidden().build();
		assertFalse(listing.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		listing = BeanListing.builder(TestBean.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(listing.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		listing = BeanListing.builder(TestBean.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(listing.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).styleName("test").build();
		assertNotNull(listing);
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test"));

		listing = BeanListing.builder(TestBean.class).styleNames("test1", "test2").build();
		assertNotNull(listing);
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test2"));

		listing = BeanListing.builder(TestBean.class).withThemeVariants(GridVariant.LUMO_COMPACT).build();
		assertTrue(listing.getComponent() instanceof Grid);

		assertTrue(
				((Grid<?>) listing.getComponent()).getThemeNames().contains(GridVariant.LUMO_COMPACT.getVariantName()));

	}

	@Test
	public void testSize() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(listing));

		listing = BeanListing.builder(TestBean.class).width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(listing));

		listing = BeanListing.builder(TestBean.class).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(listing));

		listing = BeanListing.builder(TestBean.class).height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(listing));
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(listing));

		listing = BeanListing.builder(TestBean.class).heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(listing));
		assertNull(ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(listing));

		listing = BeanListing.builder(TestBean.class).fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

		listing = BeanListing.builder(TestBean.class).fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(listing));
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

	}

	@Test
	public void testEnabled() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
		assertTrue(ComponentTestUtils.isEnabled(listing));

		listing = BeanListing.builder(TestBean.class).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(listing));

		listing = BeanListing.builder(TestBean.class).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(listing));

		listing = BeanListing.builder(TestBean.class).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(listing));

	}

	@Test
	public void testFocus() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).tabIndex(77).build();
		assertTrue(listing.getComponent() instanceof Grid);

		assertEquals(77, ((Grid<?>) listing.getComponent()).getTabIndex());

	}

	@Test
	public void testConfiguration() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
		assertTrue(listing.getComponent() instanceof Grid);
		assertFalse(((Grid<?>) listing.getComponent()).isColumnReorderingAllowed());
		assertFalse(((Grid<?>) listing.getComponent()).isHeightByRows());
		assertTrue(((Grid<?>) listing.getComponent()).isDetailsVisibleOnClick());
		assertFalse(((Grid<?>) listing.getComponent()).isMultiSort());
		assertTrue(((Grid<?>) listing.getComponent()).isVerticalScrollingEnabled());

		listing = BeanListing.builder(TestBean.class).columnReorderingAllowed(true).build();
		assertTrue(((Grid<?>) listing.getComponent()).isColumnReorderingAllowed());

		listing = BeanListing.builder(TestBean.class).heightByRows(true).build();
		assertTrue(((Grid<?>) listing.getComponent()).isHeightByRows());

		listing = BeanListing.builder(TestBean.class).itemDetailsVisibleOnClick(false).build();
		assertFalse(((Grid<?>) listing.getComponent()).isDetailsVisibleOnClick());

		listing = BeanListing.builder(TestBean.class).multiSort(true).build();
		// assertTrue(((Grid<?>) listing.getComponent()).isMultiSort());
		assertNotNull(((Grid<?>) listing.getComponent()).getElement().getAttribute("multi-sort"));

		listing = BeanListing.builder(TestBean.class).pageSize(100).build();
		assertEquals(100, ((Grid<?>) listing.getComponent()).getPageSize());

		listing = BeanListing.builder(TestBean.class).verticalScrollingEnabled(false).build();
		assertFalse(((Grid<?>) listing.getComponent()).isVerticalScrollingEnabled());

	}

	@Test
	public void testColumnConfiguration() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();

		final ItemListingColumn<?, ?, ?> c = getImpl(listing).getColumnConfiguration(ID);
		assertNotNull(c.getColumnKey());
		assertEquals(ID, c.getColumnKey());
		assertFalse(c.isReadOnly());
		assertFalse(c.isFrozen());
		assertTrue(c.isVisible());
		assertEquals(SortMode.ENABLED, c.getSortMode());
		assertTrue(c.getSortProperties().size() > 0);

		listing = BeanListing.builder(TestBean.class).readOnly(ID, true).build();
		assertTrue(getImpl(listing).getColumnConfiguration(ID).isReadOnly());

		listing = BeanListing.builder(TestBean.class).visible(ID, false).build();
		assertFalse(getImpl(listing).getColumnConfiguration(ID).isVisible());

		listing = BeanListing.builder(TestBean.class).resizable(ID, true).build();
		assertTrue(getImpl(listing).getColumnConfiguration(ID).isResizable());

		listing = BeanListing.builder(TestBean.class).frozen(ID, true).build();
		assertTrue(getImpl(listing).getColumnConfiguration(ID).isFrozen());

		listing = BeanListing.builder(TestBean.class).width(ID, "50px").build();
		assertEquals("50px", getImpl(listing).getColumnConfiguration(ID).getWidth().orElse(null));

		listing = BeanListing.builder(TestBean.class).flexGrow(ID, 1).build();
		assertEquals(1, getImpl(listing).getColumnConfiguration(ID).getFlexGrow());

		listing = BeanListing.builder(TestBean.class).alignment(ID, ColumnAlignment.RIGHT)
				.alignment(NAME, ColumnAlignment.CENTER).build();
		assertEquals(ColumnAlignment.RIGHT, getImpl(listing).getColumnConfiguration(ID).getAlignment().orElse(null));
		assertEquals(ColumnAlignment.CENTER, getImpl(listing).getColumnConfiguration(NAME).getAlignment().orElse(null));

		listing = BeanListing.builder(TestBean.class).header(ID, "test").build();
		assertEquals("test", LocalizationContext
				.translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

		listing = BeanListing.builder(TestBean.class).header(ID, "test", "mc").build();
		assertEquals("test", LocalizationContext
				.translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

		listing = BeanListing.builder(TestBean.class).header(ID, Localizable.of("test")).build();
		assertEquals("test", LocalizationContext
				.translate(getImpl(listing).getColumnConfiguration(ID).getHeaderText().orElse(null), true));

		final Button btn = new Button("test");

		listing = BeanListing.builder(TestBean.class).headerComponent(ID, btn).build();
		assertEquals(btn, getImpl(listing).getColumnConfiguration(ID).getHeaderComponent().orElse(null));

		final Renderer<TestBean> rnd = TemplateRenderer.of("test");
		listing = BeanListing.builder(TestBean.class).renderer(ID, rnd).build();
		assertEquals(rnd, getImpl(listing).getColumnConfiguration(ID).getRenderer().orElse(null));

		final ValueProvider<TestBean, String> vp = item -> "test";
		listing = BeanListing.builder(TestBean.class).valueProvider(ID, vp).build();
		assertEquals(vp, getImpl(listing).getColumnConfiguration(ID).getValueProvider().orElse(null));

		final Comparator<TestBean> cmp = Comparator.<TestBean, Long>comparing(v -> v.getId(),
				Comparator.comparingLong(k -> k));
		listing = BeanListing.builder(TestBean.class).sortComparator(ID, cmp).build();
		assertEquals(cmp, getImpl(listing).getColumnConfiguration(ID).getComparator().orElse(null));

		listing = BeanListing.builder(TestBean.class).sortUsing(NAME, ID).build();
		assertEquals(ID, getImpl(listing).getColumnConfiguration(NAME).getSortProperties().get(0));

		final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

		final Input<Long> edt = Input.number(Long.class).build();
		listing = BeanListing.builder(TestBean.class).editor(ID, edt).build();
		assertEquals(edt, getImpl(listing).getColumnConfiguration(ID).getEditorInputRenderer()
				.map(r -> r.render(beanPropertySet.property(ID))).orElse(null));

		final Button ebtn = new Button("test");
		listing = BeanListing.builder(TestBean.class).editorComponent(ID, i -> ebtn).build();
		assertEquals(ebtn,
				getImpl(listing).getColumnConfiguration(ID).getEditorComponent().map(e -> e.apply(null)).orElse(null));
		listing = BeanListing.builder(TestBean.class).editorComponent(ID, ebtn).build();
		assertEquals(ebtn,
				getImpl(listing).getColumnConfiguration(ID).getEditorComponent().map(e -> e.apply(null)).orElse(null));

		final Validator<Long> vdt = Validator.max(3);
		listing = BeanListing.builder(TestBean.class).withValidator(ID, vdt).build();
		assertEquals(vdt, getImpl(listing).getColumnConfiguration(ID).getValidators().get(0));

	}

	@Test
	public void testColumns() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();
		assertTrue(listing.getComponent() instanceof Grid);

		final Grid<?> grid = (Grid<?>) listing.getComponent();
		assertEquals(2, grid.getColumns().size());

		assertNotNull(getImpl(listing).getColumnConfiguration(ID).getColumnKey());
		assertNotNull(getImpl(listing).getColumnConfiguration(NAME).getColumnKey());

		assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(ID).getColumnKey()));
		assertNotNull(grid.getColumnByKey(getImpl(listing).getColumnConfiguration(NAME).getColumnKey()));
	}

	@Test
	public void testDefaultHeader() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();

		assertTrue(listing.getHeader().isPresent());
		assertEquals(1, listing.getHeader().get().getRows().size());
		assertTrue(listing.getHeader().get().getFirstRow().isPresent());

		assertTrue(listing.getComponent() instanceof Grid);
		final Grid<?> grid = (Grid<?>) listing.getComponent();
		assertEquals(2, grid.getColumns().size());

		Column<?> c1 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(ID).getColumnKey());
		Column<?> c2 = grid.getColumnByKey(getImpl(listing).getColumnConfiguration(NAME).getColumnKey());

		assertNotNull(c1);
		assertNotNull(c2);
	}

	@Test
	public void testHeader() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).build();

		assertTrue(listing.getHeader().isPresent());

		assertEquals(1, listing.getHeader().get().getRows().size());
		assertTrue(listing.getHeader().get().getFirstRow().isPresent());

		assertEquals(2, listing.getHeader().get().getFirstRow().get().getCells().size());
		assertTrue(listing.getHeader().get().getFirstRow().get().getCell(ID).isPresent());
		assertTrue(listing.getHeader().get().getFirstRow().get().getCell(NAME).isPresent());

		listing = BeanListing.builder(TestBean.class).header(header -> {
			header.prependRow().join(ID, NAME).setText("joined");
		}).build();

		assertEquals(2, listing.getHeader().get().getRows().size());
		assertEquals(1, listing.getHeader().get().getRows().get(0).getCells().size());
		assertEquals(2, listing.getHeader().get().getRows().get(1).getCells().size());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			BeanListing<TestBean> listing2 = BeanListing.builder(TestBean.class).header(header -> {
				header.prependRow().join(ID, NAME)
						.setText(Localizable.builder().message("test").messageCode("test.code").build());
			}).build();
			assertEquals(2, listing2.getHeader().get().getRows().size());
		});
	}

	@Test
	public void testFooter() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).footer(footer -> {
			footer.appendRow().getCell(ID).get().setText("id");
		}).build();

		assertTrue(listing.getFooter().isPresent());
		assertEquals(1, listing.getFooter().get().getRows().size());

	}

	@Test
	public void testItemsDataSource() {

		final TestBean ITEM1 = new TestBean(1L, "test1");
		final TestBean ITEM2 = new TestBean(2L, "test2");

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).items(Arrays.asList(ITEM1, ITEM2)).build();

		List<TestBean> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

		listing = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

		listing = BeanListing.builder(TestBean.class).addItem(ITEM1).addItem(ITEM2).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.contains(ITEM1));
		assertTrue(items.contains(ITEM2));

		final List<TestBean> itemList = Arrays.asList(ITEM1, ITEM2);

		listing = BeanListing.builder(TestBean.class)
				.dataSource(DataProvider.fromCallbacks(q -> itemList.stream(), q -> 2)).build();
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

		final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET).build();

		List<TestBean> items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
		assertTrue(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

		listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
		assertTrue(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

		listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
				.withQueryFilter(beanPropertySet.property(ID).lt(2L)).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(1, items.size());
		assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
		assertFalse(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

		listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
				.withQueryConfigurationProvider(new QueryConfigurationProvider() {

					@Override
					public QueryFilter getQueryFilter() {
						return beanPropertySet.property(ID).lt(2L);
					}
				}).build();

		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(1, items.size());
		assertTrue(items.stream().filter(i -> i.getId() == 1L).findFirst().isPresent());
		assertFalse(items.stream().filter(i -> i.getId() == 2L).findFirst().isPresent());

		listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
				.withQuerySort(beanPropertySet.property(NAME).desc()).build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertEquals(2L, items.get(0).getId());
		assertEquals(1L, items.get(1).getId());

		listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
				.withQueryFilter(beanPropertySet.property(ID).loe(2L))
				.withQuerySort(beanPropertySet.property(NAME).desc()).build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertEquals(2L, items.get(0).getId());
		assertEquals(1L, items.get(1).getId());

		listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
				.withDefaultQuerySort(beanPropertySet.property(NAME).desc()).build();
		items = getDataProvider(listing).fetch(new Query<>()).collect(Collectors.toList());
		assertEquals(2, items.size());
		assertEquals(2L, items.get(0).getId());
		assertEquals(1L, items.get(1).getId());

		listing.sort(ItemSort.asc(ID));
		List<QuerySortOrder> bs = getDataCommunicator(listing).getBackEndSorting();
		assertEquals(1, bs.size());
		assertEquals(ID, bs.get(0).getSorted());
		assertEquals(SortDirection.ASCENDING, bs.get(0).getDirection());

		listing.sort(Collections.singletonList(ItemSort.desc(ID)));
		bs = getDataCommunicator(listing).getBackEndSorting();
		assertEquals(1, bs.size());
		assertEquals(ID, bs.get(0).getSorted());
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

		final BeanPropertySet<TestBean> beanPropertySet = BeanPropertySet.create(TestBean.class);

		final AtomicInteger fired = new AtomicInteger(0);
		final ItemValue value = new ItemValue();

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).dataSource(datastore, TARGET)
				.withItemRefreshListener(e -> {
					fired.incrementAndGet();
					value.item = e.getItem();
				}).build();

		assertNull(value.item);
		assertEquals(0, fired.get());

		listing.refresh();
		assertNull(value.item);
		assertEquals(1, fired.get());

		TestBean itm = datastore.query(TARGET).filter(beanPropertySet.property(ID).eq(1L))
				.findOne(QueryProjection.bean(TestBean.class))
				.orElseThrow(() -> new RuntimeException("item not found"));
		listing.refreshItem(itm);

		assertNotNull(value.item);
		assertEquals(itm, value.item);
		assertEquals(2, fired.get());

	}

	private class ItemValue {

		public TestBean item;

	}

	@Test
	public void testSelectable() {

		final TestBean ITEM1 = new TestBean(1L, "test1");
		final TestBean ITEM2 = new TestBean(2L, "test2");

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).build();

		assertEquals(SelectionMode.NONE, listing.getSelectionMode());
		assertEquals(0, listing.getSelectedItems().size());
		assertFalse(listing.getFirstSelectedItem().isPresent());
		assertFalse(listing.isSelected(ITEM1));
		assertFalse(listing.isSelected(ITEM2));

		assertThrows(IllegalStateException.class, () -> listing.select(ITEM1));

		BeanListing<TestBean> listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2)
				.selectionMode(SelectionMode.SINGLE).build();
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

		listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).singleSelect().build();
		assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
		listing2.select(ITEM2);
		assertEquals(1, listing2.getSelectedItems().size());

		listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).build();
		listing2.setSelectionMode(SelectionMode.SINGLE);
		assertEquals(SelectionMode.SINGLE, listing2.getSelectionMode());
		listing2.select(ITEM2);
		assertEquals(1, listing2.getSelectedItems().size());

		listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).multiSelect().build();
		assertEquals(SelectionMode.MULTI, listing2.getSelectionMode());
		assertEquals(0, listing.getSelectedItems().size());

		listing2.select(ITEM1);
		listing2.select(ITEM2);
		assertEquals(2, listing2.getSelectedItems().size());
		assertTrue(listing2.isSelected(ITEM1));
		assertTrue(listing2.isSelected(ITEM2));

		final Set<TestBean> selected = new HashSet<>();

		listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).singleSelect().withSelectionListener(e -> {
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

		listing2 = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2).singleSelect().multiSelect()
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

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).visibleColumns(NAME, ID).build();

		List<String> visible = listing.getVisibleColumns();
		assertEquals(2, visible.size());
		assertEquals(NAME, visible.get(0));
		assertEquals(ID, visible.get(1));

		listing = BeanListing.builder(TestBean.class).visibleColumns(NAME, ID).build();
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

		listing = BeanListing.builder(TestBean.class).visible(ID, false).build();
		visible = listing.getVisibleColumns();
		assertEquals(1, visible.size());
		assertTrue(visible.contains(NAME));
		assertFalse(visible.contains(ID));

	}

	@Test
	public void testComponentColumns() {

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).withComponentColumn(item -> new Button("x"))
				.displayBefore(ID).add().build();

		List<String> visible = listing.getVisibleColumns();
		assertEquals(3, visible.size());
		assertTrue(visible.get(0) != null);
		assertEquals(ID, visible.get(1));
		assertEquals(NAME, visible.get(2));

	}

	@Test
	public void testItemDetails() {

		final TestBean ITEM1 = new TestBean(1L, "test1");
		final TestBean ITEM2 = new TestBean(2L, "test2");

		BeanListing<TestBean> listing = BeanListing.builder(TestBean.class).items(ITEM1, ITEM2)
				.itemDetailsText(item -> item.getName()).build();

		assertFalse(listing.isItemDetailsVisible(ITEM1));

		listing.setItemDetailsVisible(ITEM1, true);
		assertTrue(listing.isItemDetailsVisible(ITEM1));
		assertFalse(listing.isItemDetailsVisible(ITEM2));

		listing.setItemDetailsVisible(ITEM1, false);
		assertFalse(listing.isItemDetailsVisible(ITEM1));
	}

	@SuppressWarnings("unchecked")
	private static DataProvider<TestBean, ?> getDataProvider(BeanListing<TestBean> listing) {
		assertTrue(listing.getComponent() instanceof Grid);
		return ((Grid<TestBean>) listing.getComponent()).getDataProvider();
	}

	@SuppressWarnings("unchecked")
	private static DataCommunicator<TestBean> getDataCommunicator(BeanListing<TestBean> listing) {
		assertTrue(listing.getComponent() instanceof Grid);
		return ((Grid<TestBean>) listing.getComponent()).getDataCommunicator();
	}

	@SuppressWarnings("unchecked")
	private static AbstractItemListing<TestBean, String> getImpl(BeanListing<TestBean> listing) {
		assertTrue(listing instanceof AbstractItemListing);
		return (AbstractItemListing<TestBean, String>) listing;
	}

}
