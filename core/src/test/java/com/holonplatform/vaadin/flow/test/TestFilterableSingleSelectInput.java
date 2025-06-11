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

import static com.holonplatform.vaadin.flow.test.util.DatastoreTestUtils.CODE;
import static com.holonplatform.vaadin.flow.test.util.DatastoreTestUtils.DESCRIPTION;
import static com.holonplatform.vaadin.flow.test.util.DatastoreTestUtils.TARGET1;
import static com.holonplatform.vaadin.flow.test.util.DatastoreTestUtils.TEST1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.jdbc.BasicDataSource;
import com.holonplatform.jdbc.DatabasePlatform;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.FilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.PropertyFilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.test.util.BeanTest1;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.internal.CurrentInstance;

public class TestFilterableSingleSelectInput {

	private static UI ui;

	@BeforeAll
	public static void beforeAll() {
		ui = new UI();
		ui.setLocale(Locale.US);
		CurrentInstance.set(UI.class, ui);
	}

	@BeforeEach
	public void before() {
		CurrentInstance.set(UI.class, ui);
	}

	@AfterEach
	public void after() {
		CurrentInstance.set(UI.class, null);
	}

	@Test
	public void testBuilders() {

		FilterableSingleSelectInputBuilder<String, String> builder = FilterableSingleSelectConfigurator
				.create(String.class);
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.singleSelect(String.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.singleSelect(String.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		FilterableSingleSelectInputBuilder<String, Integer> builder2 = FilterableSingleSelectConfigurator.create(
				String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

		builder2 = Input.singleSelect(String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

		builder2 = Components.input.singleSelect(String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

	}

	@Test
	public void testPropertyBuilders() {

		final Property<String> PROPERTY = StringProperty.create("test");

		PropertyFilterableSingleSelectInputBuilder<String> builder = FilterableSingleSelectConfigurator
				.create(PROPERTY);
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.singleSelect(PROPERTY);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.singleSelect(PROPERTY);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = FilterableSingleSelectConfigurator.create(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Input.singleSelect(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.singleSelect(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<String> input = Input.singleSelect(String.class).id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.singleSelect(String.class).build();
		assertTrue(input.isVisible());

		input = Input.singleSelect(String.class).visible(true).build();
		assertTrue(input.isVisible());

		input = Input.singleSelect(String.class).visible(false).build();
		assertFalse(input.isVisible());

		input = Input.singleSelect(String.class).hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.singleSelect(String.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		UI.getCurrent().add(input.getComponent());
		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.singleSelect(String.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<String> input = Input.singleSelect(String.class).styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.singleSelect(String.class).styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<String> input = Input.singleSelect(String.class).width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.singleSelect(String.class).width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.singleSelect(String.class).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.singleSelect(String.class).height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.singleSelect(String.class).heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.singleSelect(String.class).fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.singleSelect(String.class).fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<String> input = Input.singleSelect(String.class).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.singleSelect(String.class).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.singleSelect(String.class).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.singleSelect(String.class).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<String> input = Input.singleSelect(String.class).label(Localizable.builder().message("test").build())
				.build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.singleSelect(String.class).label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.singleSelect(String.class).label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.singleSelect(String.class).label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleSelect(String.class)
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleSelect(String.class).label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleSelect(String.class).deferLocalization().label("test", "test.code")
					.build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<String> input = Input.singleSelect(String.class).build();
		assertFalse(input.isReadOnly());

		input = Input.singleSelect(String.class).readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.singleSelect(String.class).readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.singleSelect(String.class).readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<String> input = Input.singleSelect(String.class).build();
		assertFalse(input.isRequired());

		input = Input.singleSelect(String.class).required(true).build();
		assertTrue(input.isRequired());

		input = Input.singleSelect(String.class).required(false).build();
		assertFalse(input.isRequired());

		input = Input.singleSelect(String.class).required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testFocus() {

		Input<String> input = Input.singleSelect(String.class).tabIndex(77).build();
		assertTrue(input.getComponent() instanceof Focusable<?>);

		assertEquals(77, ((Focusable<?>) input.getComponent()).getTabIndex());

		assertTrue(input.getComponent() instanceof ComboBox<?>);

		input = Input.singleSelect(String.class).autofocus(false).build();
		assertFalse(((ComboBox<?>) input.getComponent()).isAutofocus());

		input = Input.singleSelect(String.class).autofocus(true).build();
		assertTrue(((ComboBox<?>) input.getComponent()).isAutofocus());

	}

	@Test
	public void testAutoOpen() {

		Input<String> input = Input.singleSelect(String.class).autoOpen(true).build();
		assertTrue(input.getComponent() instanceof ComboBox<?>);
		assertTrue(((ComboBox<?>) input.getComponent()).isAutoOpen());

		input = Input.singleSelect(String.class).autoOpen(false).build();
		assertTrue(input.getComponent() instanceof ComboBox<?>);
		assertFalse(((ComboBox<?>) input.getComponent()).isAutoOpen());

	}

	@Test
	public void testPlaceholder() {

		Input<String> input = Input.singleSelect(String.class)
				.placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.singleSelect(String.class).placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.singleSelect(String.class).placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.singleSelect(String.class).placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleSelect(String.class)
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleSelect(String.class).placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleSelect(String.class).deferLocalization().placeholder("test", "test.code")
					.build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testPattern() {

		Input<String> input = Input.singleSelect(String.class).pattern("[0-9]*").build();
		assertTrue(input.getComponent() instanceof ComboBox<?>);
		assertEquals("[0-9]*", ((ComboBox<?>) input.getComponent()).getPattern());

	}

	@Test
	public void testPageSize() {

		Input<String> input = Input.singleSelect(String.class).pageSize(10).build();
		assertTrue(input.getComponent() instanceof ComboBox<?>);
		assertEquals(10, ((ComboBox<?>) input.getComponent()).getPageSize());

	}

	@Test
	public void testRenderer() {

		final Renderer<String> renderer = LitRenderer.of("[[index]]");
		Input.singleSelect(String.class).renderer(renderer).build();

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testItemCaptions() {

		final ItemCaptionGenerator<String> icg = value -> "X";

		Input<String> input = Input.singleSelect(String.class).itemCaptionGenerator(icg).build();
		assertTrue(input.getComponent() instanceof ComboBox<?>);
		assertNotNull(((ComboBox<?>) input.getComponent()).getItemLabelGenerator());
		assertEquals("X", ((ComboBox<String>) input.getComponent()).getItemLabelGenerator().apply("aaa"));

		final ItemCaptionGenerator<Integer> icg2 = value -> "N." + value;

		final List<Integer> ints = Arrays.asList(1, 2, 3);

		Input<Integer> input2 = Input.singleSelect(Integer.class).items(ints).itemCaptionGenerator(icg2).build();

		ItemLabelGenerator<Integer> lg = ((ComboBox<Integer>) input2.getComponent()).getItemLabelGenerator();
		assertEquals("N.1", lg.apply(ints.get(0)));
		assertEquals("N.2", lg.apply(ints.get(1)));
		assertEquals("N.3", lg.apply(ints.get(2)));

		input2 = Input.singleSelect(Integer.class).items(ints).itemCaption(1, "N.1").itemCaption(2, "N.2")
				.itemCaption(3, "N.3").build();
		lg = ((ComboBox<Integer>) input2.getComponent()).getItemLabelGenerator();
		assertEquals("N.1", lg.apply(ints.get(0)));
		assertEquals("N.2", lg.apply(ints.get(1)));
		assertEquals("N.3", lg.apply(ints.get(2)));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input3 = Input.singleSelect(Integer.class).items(ints)
					.itemCaption(2, Localizable.builder().message("test").messageCode("test.code").build()).build();
			ItemLabelGenerator<Integer> lgx = ((ComboBox<Integer>) input3.getComponent()).getItemLabelGenerator();
			assertEquals("1", lgx.apply(ints.get(0)));
			assertEquals("TestUS", lgx.apply(ints.get(1)));
			assertEquals("3", lgx.apply(ints.get(2)));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input3 = Input.singleSelect(Integer.class).items(ints).itemCaption(2, "test", "test.code")
					.build();
			ItemLabelGenerator<Integer> lgx = ((ComboBox<Integer>) input3.getComponent()).getItemLabelGenerator();
			assertEquals("TestUS", lgx.apply(ints.get(1)));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input3 = Input.singleSelect(Integer.class).items(ints).deferLocalization()
					.itemCaption(2, "test", "test.code").build();
			ItemLabelGenerator<Integer> lgx = ((ComboBox<Integer>) input3.getComponent()).getItemLabelGenerator();
			assertEquals("1", lgx.apply(ints.get(0)));
			assertEquals("test", lgx.apply(ints.get(1)));
			UI.getCurrent().add(input3.getComponent());
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", lgx.apply(ints.get(1)));
		});

	}

	@Test
	public void testHasValue() {

		Input<String> input = Input.singleSelect(String.class).addItem("test").build();
		assertNull(input.getEmptyValue());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue("test");
		assertEquals("test", input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals("test", input.getValueIfPresent().orElse(null));

		input.clear();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

	}

	@Test
	public void testSelect() {

		SingleSelect<String> input = Input.singleSelect(String.class).addItem("a").addItem("b").build();
		assertNotNull(input);

		assertEquals(SelectionMode.SINGLE, input.getSelectionMode());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.select("a");
		assertFalse(input.isEmpty());
		assertEquals("a", input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());

		assertEquals("a", input.getSelectedItem().orElse(""));
		assertEquals("a", input.getFirstSelectedItem().orElse(""));
		assertEquals(1, input.getSelectedItems().size());
		assertEquals("a", input.getSelectedItems().iterator().next());
		assertTrue(input.isSelected("a"));
		assertFalse(input.isSelected("b"));

		input.deselect("a");
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());
		assertFalse(input.getSelectedItem().isPresent());
		assertFalse(input.getFirstSelectedItem().isPresent());
		assertEquals(0, input.getSelectedItems().size());

		input.select("a");
		input.select("b");
		assertEquals("b", input.getValue());
		assertTrue(input.isSelected("b"));

		input.deselectAll();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());
		assertFalse(input.getSelectedItem().isPresent());
		assertFalse(input.getFirstSelectedItem().isPresent());
		assertEquals(0, input.getSelectedItems().size());
		assertFalse(input.isSelected("a"));
		assertFalse(input.isSelected("b"));

		final StringValue sv = new StringValue();

		input.addSelectionListener(e -> {
			sv.value = e.getFirstSelectedItem().orElse(null);
		});

		assertNull(sv.value);

		input.select("a");
		assertEquals("a", sv.value);

		input.deselectAll();
		assertNull(sv.value);

		sv.value = null;

		input = Input.singleSelect(String.class).addItem("a").addItem("b")
				.withSelectionListener(e -> sv.value = e.getFirstSelectedItem().orElse(null)).build();

		assertNull(sv.value);

		input.select("b");
		assertEquals("b", sv.value);

		input.deselectAll();
		assertNull(sv.value);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEnumSelect() {

		SingleSelect<TestEnum> input = Input.enumSelect(TestEnum.class).build();
		assertNotNull(input);

		DataProvider<TestEnum, ?> dp = ((ComboBox<TestEnum>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		Set<TestEnum> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains(TestEnum.A));
		assertTrue(items.contains(TestEnum.B));
		assertTrue(items.contains(TestEnum.C));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testItemsDataProvider() {

		SingleSelect<String> input = Input.singleSelect(String.class).items(Arrays.asList("one", "two")).build();

		DataProvider<String, ?> dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		Set<String> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

		input = Input.singleSelect(String.class).items("one", "two").build();

		dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

		input = Input.singleSelect(String.class).addItem("one").addItem("two").build();

		dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDataProvider() {

		SingleSelect<String> input = Input.singleSelect(String.class)
				.dataSource(DataProvider.ofCollection(Arrays.asList("a", "b", "c"))).build();

		DataProvider<String, ?> dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		Set<String> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("b"));
		assertTrue(items.contains("c"));

		input = Input.singleSelect(String.class)
				.dataSource(DataProvider.ofCollection(Arrays.asList("a", "aa", "b")).filteringByPrefix(v -> v)).build();

		dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("aa"));
		assertTrue(items.contains("b"));

		items = ((DataProvider<String, String>) dp).fetch(new Query<>("a")).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("aa"));

		final DataProvider<String, Integer> dp2 = DataProvider
				.<String, Integer>fromFilteringCallbacks(q -> Arrays.asList("a", "aa", "b").stream().filter(item -> {
					int filter = q.getFilter().orElse(0);
					if (filter > 0) {
						return item.startsWith("a");
					}
					return true;
				}), q -> {
					int filter = q.getFilter().orElse(0);
					if (filter > 0) {
						return 2;
					}
					return 3;
				});

		input = Input.singleSelect(String.class).dataSource(dp2, filter -> {
			if ("a".equals(filter)) {
				return 1;
			}
			return 0;
		}).build();

		dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertEquals(dp2, dp);

		assertEquals(3, dp2.size(new Query<>()));

		items = dp2.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("aa"));
		assertTrue(items.contains("b"));

		items = dp2.fetch(new Query<>(1)).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("aa"));

		final DataProvider<String, ?> idp = DataProvider
				.<String>fromCallbacks(q -> Arrays.asList("a", "b", "c").stream(), q -> 3);

		input = Input.singleSelect(String.class).dataSource(idp, f -> null).build();

		dp = ((ComboBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("b"));
		assertTrue(items.contains("c"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDatastoreDataProvider() {

		final Datastore datastore = JdbcDatastore.builder()
				.dataSource(
						BasicDataSource.builder().url("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:test_init.sql'")
								.username("sa").driverClassName(DatabasePlatform.H2.getDriverClassName()).build())
				.traceEnabled(true).build();

		SingleSelect<BeanTest1> input1 = Input.singleSelect(BeanTest1.class).dataSource(datastore, TARGET1, f -> null)
				.build();
		assertNotNull(input1);

		DataProvider<BeanTest1, ?> dp1 = ((ComboBox<BeanTest1>) input1.getComponent()).getDataProvider();
		assertNotNull(dp1);

		assertEquals(2, dp1.size(new Query<>()));

		Set<BeanTest1> items = dp1.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains(new BeanTest1("A")));
		assertTrue(items.contains(new BeanTest1("B")));

		input1 = Input.singleSelect(BeanTest1.class).dataSource(datastore, TARGET1, f -> CODE.contains(f, false))
				.build();
		assertNotNull(input1);

		dp1 = ((ComboBox<BeanTest1>) input1.getComponent()).getDataProvider();
		assertNotNull(dp1);

		assertEquals(2, dp1.size(new Query<>()));

		items = dp1.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains(new BeanTest1("A")));
		assertTrue(items.contains(new BeanTest1("B")));

		items = ((DataProvider<BeanTest1, String>) dp1).fetch(new Query<>("A")).collect(Collectors.toSet());
		assertEquals(1, items.size());
		assertTrue(items.contains(new BeanTest1("A")));

		SingleSelect<String> input2 = Input
				.singleSelect(String.class, BeanTest1.class,
						ItemConverter.create(item -> item.getCode(), value -> Optional.of(new BeanTest1(value))))
				.dataSource(datastore, TARGET1, f -> CODE.contains(f, false)).build();
		assertNotNull(input2);

		DataProvider<String, String> dp3 = (DataProvider<String, String>) ((ComboBox<String>) input2.getComponent())
				.getDataProvider();
		assertNotNull(dp3);

		assertEquals(2, dp3.size(new Query<>()));

		Set<String> sitems = dp3.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, sitems.size());

		// properties

		SingleSelect<String> input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, TEST1).build();
		assertNotNull(input4);

		DataProvider<PropertyBox, String> dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4
				.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		Set<PropertyBox> pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description B".equals(i.getValue(DESCRIPTION))).count());

		pitems = dp4.fetch(new Query<>("A")).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, CODE, DESCRIPTION).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description B".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, TEST1).withQueryFilter(CODE.eq("A")).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(1, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, TEST1).withQueryFilter(CODE.isNotNull())
				.withQuerySort(DESCRIPTION.desc()).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		PropertyBox itm = dp4.fetch(new Query<>()).findFirst().orElse(null);
		assertNotNull(itm);
		assertEquals("B", itm.getValue(CODE));

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, f -> DESCRIPTION.endsWith(f), TEST1).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>("B")).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());

		input4 = Input.singleSelect(CODE)
				.dataSource(datastore, TARGET1, f -> DESCRIPTION.endsWith(f), CODE, DESCRIPTION).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>("B")).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, CODE, DESCRIPTION)
				.filterConverter(f -> DESCRIPTION.endsWith(f)).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>("B")).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1, f -> DESCRIPTION.endsWith(f), TEST1)
				.withQueryFilter(CODE.eq("A")).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(1, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>("B")).collect(Collectors.toSet());
		assertEquals(0, pitems.size());

		// single property

		input4 = Input.singleSelect(CODE).dataSource(datastore, TARGET1).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((ComboBox<PropertyBox>) input4.getComponent()).getDataProvider();
		assertNotNull(dp4);
		assertEquals(2, dp4.size(new Query<>()));
		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());

	}

	@Test
	public void testValidatable() {

		ValidatableSingleSelect<String> input = Input.singleSelect(String.class).validatable().label("test")
				.withValidator(Validator.notNull()).id("testid").addItem("a").addItem("b").build();

		assertEquals("test", ComponentTestUtils.getLabel(input));
		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		Assertions.assertThrows(ValidationException.class, () -> input.validate());

		ValidatableSingleSelect<String> input2 = Input.singleSelect(String.class).validatable().label("test2")
				.required().addItem("a").addItem("b").build();

		assertEquals("test2", ComponentTestUtils.getLabel(input2));

		Assertions.assertThrows(ValidationException.class, () -> input2.validate());

		input2.setValue("a");
		Assertions.assertDoesNotThrow(() -> input2.validate());

		input2.clear();
		Assertions.assertThrows(ValidationException.class, () -> input2.validate());

		input2.select("b");
		Assertions.assertDoesNotThrow(() -> input2.validate());

		input2.clear();
		Assertions.assertThrows(ValidationException.class, () -> input2.validate());
	}

	private class StringValue {
		String value;
	}

	private enum TestEnum {

		A, B, C;

	}

}
