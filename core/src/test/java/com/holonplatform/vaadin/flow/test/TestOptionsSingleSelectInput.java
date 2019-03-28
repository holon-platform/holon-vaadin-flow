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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

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
import com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.OptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.PropertyOptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.test.util.BeanTest1;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.test.util.TestAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

public class TestOptionsSingleSelectInput {

	@Test
	public void testBuilders() {

		OptionsSingleSelectInputBuilder<String, String> builder = OptionsSingleSelectConfigurator.create(String.class);
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.singleOptionSelect(String.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.singleOptionSelect(String.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		OptionsSingleSelectInputBuilder<String, Integer> builder2 = OptionsSingleSelectConfigurator.create(String.class,
				Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

		builder2 = Input.singleOptionSelect(String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

		builder2 = Components.input.singleOptionSelect(String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

	}

	@Test
	public void testPropertyBuilders() {

		final Property<String> PROPERTY = StringProperty.create("test");

		PropertyOptionsSingleSelectInputBuilder<String> builder = OptionsSingleSelectConfigurator.create(PROPERTY);
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.singleOptionSelect(PROPERTY);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.singleOptionSelect(PROPERTY);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = OptionsSingleSelectConfigurator.create(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Input.singleOptionSelect(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.singleOptionSelect(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<String> input = Input.singleOptionSelect(String.class).id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.singleOptionSelect(String.class).build();
		assertTrue(input.isVisible());

		input = Input.singleOptionSelect(String.class).visible(true).build();
		assertTrue(input.isVisible());

		input = Input.singleOptionSelect(String.class).visible(false).build();
		assertFalse(input.isVisible());

		input = Input.singleOptionSelect(String.class).hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.singleOptionSelect(String.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.singleOptionSelect(String.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<String> input = Input.singleOptionSelect(String.class).styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.singleOptionSelect(String.class).styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testEnabled() {

		Input<String> input = Input.singleOptionSelect(String.class).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.singleOptionSelect(String.class).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.singleOptionSelect(String.class).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.singleOptionSelect(String.class).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<String> input = Input.singleOptionSelect(String.class)
				.label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.singleOptionSelect(String.class).label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.singleOptionSelect(String.class).label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.singleOptionSelect(String.class).label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleOptionSelect(String.class)
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleOptionSelect(String.class).label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.singleOptionSelect(String.class).deferLocalization().label("test", "test.code")
					.build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<String> input = Input.singleOptionSelect(String.class).build();
		assertFalse(input.isReadOnly());

		input = Input.singleOptionSelect(String.class).readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.singleOptionSelect(String.class).readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.singleOptionSelect(String.class).readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<String> input = Input.singleOptionSelect(String.class).build();
		assertFalse(input.isRequired());

		input = Input.singleOptionSelect(String.class).required(true).build();
		assertTrue(input.isRequired());

		input = Input.singleOptionSelect(String.class).required(false).build();
		assertFalse(input.isRequired());

		input = Input.singleOptionSelect(String.class).required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testRenderer() {

		final ComponentRenderer<? extends Component, String> renderer = new TextRenderer<>(item -> "TEST");

		Input<String> input = Input.singleOptionSelect(String.class).renderer(renderer).build();
		assertTrue(input.getComponent() instanceof RadioButtonGroup<?>);
		assertEquals(renderer, ((RadioButtonGroup<?>) input.getComponent()).getItemRenderer());

	}

	@Test
	public void testItemCaptions() {

		final ItemCaptionGenerator<String> icg = value -> "X";

		Input<String> input = Input.singleOptionSelect(String.class).itemCaptionGenerator(icg).build();
		assertTrue(input.getComponent() instanceof RadioButtonGroup<?>);
		assertEquals("X", getLabel(input, "aaa"));

		final ItemCaptionGenerator<Integer> icg2 = value -> "N." + value;

		final List<Integer> ints = Arrays.asList(1, 2, 3);

		Input<Integer> input2 = Input.singleOptionSelect(Integer.class).items(ints).itemCaptionGenerator(icg2).build();
		assertEquals("N.1", getLabel(input2, ints.get(0)));
		assertEquals("N.2", getLabel(input2, ints.get(1)));
		assertEquals("N.3", getLabel(input2, ints.get(2)));

		input2 = Input.singleOptionSelect(Integer.class).items(ints).itemCaption(1, "N.1").itemCaption(2, "N.2")
				.itemCaption(3, "N.3").build();
		assertEquals("N.1", getLabel(input2, ints.get(0)));
		assertEquals("N.2", getLabel(input2, ints.get(1)));
		assertEquals("N.3", getLabel(input2, ints.get(2)));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input3 = Input.singleOptionSelect(Integer.class).items(ints)
					.itemCaption(2, Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("1", getLabel(input3, ints.get(0)));
			assertEquals("TestUS", getLabel(input3, ints.get(1)));
			assertEquals("3", getLabel(input3, ints.get(2)));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input3 = Input.singleOptionSelect(Integer.class).items(ints)
					.itemCaption(2, "test", "test.code").build();
			assertEquals("TestUS", getLabel(input3, ints.get(1)));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input3 = Input.singleOptionSelect(Integer.class).items(ints).deferLocalization()
					.itemCaption(2, "test", "test.code").build();
			assertEquals("1", getLabel(input3, ints.get(0)));
			assertEquals("test", getLabel(input3, ints.get(1)));
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", getLabel(input3, ints.get(1)));
		});

	}

	@SuppressWarnings("unchecked")
	private static <ITEM> String getLabel(Input<?> input, ITEM item) {
		assertTrue(input.getComponent() instanceof RadioButtonGroup<?>);
		ComponentRenderer<?, ?> renderer = ((RadioButtonGroup<?>) input.getComponent()).getItemRenderer();
		if (renderer instanceof TextRenderer) {
			TextRenderer<ITEM> tr = (TextRenderer<ITEM>) renderer;
			Component c = tr.createComponent(item);
			if (c != null) {
				return c.getElement().getText();
			}
		}
		return null;
	}

	@Test
	public void testHasValue() {

		Input<String> input = Input.singleOptionSelect(String.class).build();
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

		SingleSelect<String> input = Input.singleOptionSelect(String.class).addItem("a").addItem("b").build();
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

		input = Input.singleOptionSelect(String.class).addItem("a").addItem("b")
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

		SingleSelect<TestEnum> input = Input.enumOptionSelect(TestEnum.class).build();
		assertNotNull(input);

		DataProvider<TestEnum, ?> dp = ((RadioButtonGroup<TestEnum>) input.getComponent()).getDataProvider();
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

		SingleSelect<String> input = Input.singleOptionSelect(String.class).items(Arrays.asList("one", "two")).build();

		DataProvider<String, ?> dp = ((RadioButtonGroup<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		Set<String> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

		input = Input.singleOptionSelect(String.class).items("one", "two").build();

		dp = ((RadioButtonGroup<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

		input = Input.singleOptionSelect(String.class).addItem("one").addItem("two").build();

		dp = ((RadioButtonGroup<String>) input.getComponent()).getDataProvider();
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

		SingleSelect<String> input = Input.singleOptionSelect(String.class)
				.dataSource(DataProvider.ofCollection(Arrays.asList("a", "b", "c"))).build();

		DataProvider<String, ?> dp = ((RadioButtonGroup<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		Set<String> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("b"));
		assertTrue(items.contains("c"));

		input = Input.singleOptionSelect(String.class)
				.dataSource(DataProvider.ofCollection(Arrays.asList("a", "aa", "b"))).build();

		dp = ((RadioButtonGroup<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("aa"));
		assertTrue(items.contains("b"));

		final DataProvider<String, ?> idp = DataProvider
				.<String>fromCallbacks(q -> Arrays.asList("a", "b", "c").stream(), q -> 3);

		input = Input.singleOptionSelect(String.class).dataSource(idp).build();

		dp = ((RadioButtonGroup<String>) input.getComponent()).getDataProvider();
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

		SingleSelect<BeanTest1> input1 = Input.singleOptionSelect(BeanTest1.class).dataSource(datastore, TARGET1)
				.build();
		assertNotNull(input1);

		DataProvider<BeanTest1, ?> dp1 = ((RadioButtonGroup<BeanTest1>) input1.getComponent()).getDataProvider();
		assertNotNull(dp1);

		assertEquals(2, dp1.size(new Query<>()));

		Set<BeanTest1> items = dp1.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains(new BeanTest1("A")));
		assertTrue(items.contains(new BeanTest1("B")));

		SingleSelect<String> input2 = Input
				.singleOptionSelect(String.class, BeanTest1.class,
						ItemConverter.create(item -> item.getCode(), value -> Optional.of(new BeanTest1(value))))
				.dataSource(datastore, TARGET1).build();
		assertNotNull(input2);

		DataProvider<String, String> dp3 = (DataProvider<String, String>) ((RadioButtonGroup<String>) input2
				.getComponent()).getDataProvider();
		assertNotNull(dp3);

		assertEquals(2, dp3.size(new Query<>()));

		Set<String> sitems = dp3.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, sitems.size());

		// properties

		SingleSelect<String> input4 = Input.singleOptionSelect(CODE).dataSource(datastore, TARGET1, TEST1).build();
		assertNotNull(input4);

		DataProvider<PropertyBox, String> dp4 = (DataProvider<PropertyBox, String>) ((RadioButtonGroup<PropertyBox>) input4
				.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		Set<PropertyBox> pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description B".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.singleOptionSelect(CODE).dataSource(datastore, TARGET1, CODE, DESCRIPTION).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((RadioButtonGroup<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description B".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.singleOptionSelect(CODE).dataSource(datastore, TARGET1, TEST1).withQueryFilter(CODE.eq("A"))
				.build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((RadioButtonGroup<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);

		assertEquals(1, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.singleOptionSelect(CODE).dataSource(datastore, TARGET1, TEST1).withQueryFilter(CODE.isNotNull())
				.withQuerySort(DESCRIPTION.desc()).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((RadioButtonGroup<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		PropertyBox itm = dp4.fetch(new Query<>()).findFirst().orElse(null);
		assertNotNull(itm);
		assertEquals("B", itm.getValue(CODE));

		// single property

		input4 = Input.singleOptionSelect(CODE).dataSource(datastore, TARGET1).build();

		dp4 = (DataProvider<PropertyBox, String>) ((RadioButtonGroup<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);
		assertEquals(2, dp4.size(new Query<>()));
		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());

	}

	@Test
	public void testAdapters() {

		Input<String> input = Input.singleOptionSelect(CODE)
				.withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

		assertFalse(input.as(Collection.class).isPresent());

		input = Input.singleOptionSelect(String.class).validatable()
				.withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

	}

	private class StringValue {
		String value;
	}

	private enum TestEnum {

		A, B, C;

	}

}
