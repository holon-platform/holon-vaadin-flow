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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.jdbc.JdbcDatastore;
import com.holonplatform.jdbc.BasicDataSource;
import com.holonplatform.jdbc.DatabasePlatform;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.ValidatableMultiSelect;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.ListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.PropertyListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.test.util.BeanTest1;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.TestAdapter;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

public class TestListMultiSelectInput {

	@Test
	public void testBuilders() {

		ListMultiSelectInputBuilder<String, String> builder = ListMultiSelectConfigurator.create(String.class);
		assertNotNull(builder);
		Input<Set<String>> input = builder.build();
		assertNotNull(input);

		builder = Input.multiListSelect(String.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.multiListSelect(String.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		ListMultiSelectInputBuilder<String, Integer> builder2 = ListMultiSelectConfigurator.create(String.class,
				Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

		builder2 = Input.multiListSelect(String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

		builder2 = Components.input.multiListSelect(String.class, Integer.class,
				ItemConverter.create(item -> item.toString(), value -> Optional.of(Integer.valueOf(value))));
		input = builder2.build();
		assertNotNull(input);

	}

	@Test
	public void testPropertyBuilders() {

		final Property<String> PROPERTY = StringProperty.create("test");

		PropertyListMultiSelectInputBuilder<String> builder = ListMultiSelectConfigurator.create(PROPERTY);
		assertNotNull(builder);
		Input<Set<String>> input = builder.build();
		assertNotNull(input);

		builder = Input.multiListSelect(PROPERTY);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.multiListSelect(PROPERTY);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = ListMultiSelectConfigurator.create(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Input.multiListSelect(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.multiListSelect(PROPERTY, value -> Optional.empty());
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<Set<String>> input = Input.multiListSelect(String.class).id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.multiListSelect(String.class).build();
		assertTrue(input.isVisible());

		input = Input.multiListSelect(String.class).visible(true).build();
		assertTrue(input.isVisible());

		input = Input.multiListSelect(String.class).visible(false).build();
		assertFalse(input.isVisible());

		input = Input.multiListSelect(String.class).hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.multiListSelect(String.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.multiListSelect(String.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testEnabled() {

		Input<Set<String>> input = Input.multiListSelect(String.class).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.multiListSelect(String.class).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.multiListSelect(String.class).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.multiListSelect(String.class).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testReadOnly() {

		Input<Set<String>> input = Input.multiListSelect(String.class).build();
		assertFalse(input.isReadOnly());

		input = Input.multiListSelect(String.class).readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.multiListSelect(String.class).readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.multiListSelect(String.class).readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testHasValue() {

		Input<Set<String>> input = Input.multiListSelect(String.class).items("test1", "test2").build();
		assertNotNull(input.getEmptyValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());
		assertTrue(input.getValue().isEmpty());

		input.setValue(null);
		assertNotNull(input.getValue());
		assertTrue(input.getValue().isEmpty());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue(Collections.singleton("test1"));
		assertFalse(input.getValue().isEmpty());
		assertEquals("test1", input.getValue().iterator().next());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals("test1", input.getValueIfPresent().orElse(null).iterator().next());

		input.clear();
		assertNotNull(input.getValue());
		assertTrue(input.getValue().isEmpty());
		assertTrue(input.isEmpty());

		input.setValue(Collections.singleton("testx"));
		assertTrue(input.isEmpty());

		input.setValue(new HashSet<>(Arrays.asList("testx", "test2")));
		assertFalse(input.getValue().isEmpty());
		assertEquals("test2", input.getValue().iterator().next());

	}

	@Test
	public void testSelect() {

		MultiSelect<String> input = Input.multiListSelect(String.class).addItem("a").addItem("b").build();
		assertNotNull(input);

		assertEquals(SelectionMode.MULTI, input.getSelectionMode());

		assertTrue(input.isEmpty());
		assertNotNull(input.getValue());
		assertTrue(input.getValue().isEmpty());

		input.select("a");
		assertFalse(input.isEmpty());
		assertEquals(Collections.singleton("a"), input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());

		assertNotNull(input.getSelectedItems());

		assertEquals(Collections.singleton("a"), input.getSelectedItems());
		assertEquals("a", input.getFirstSelectedItem().orElse(null));
		assertEquals(1, input.getSelectedItems().size());
		assertEquals("a", input.getSelectedItems().iterator().next());
		assertTrue(input.isSelected("a"));
		assertFalse(input.isSelected("b"));

		input.deselect("a");
		assertTrue(input.isEmpty());
		assertFalse(input.getFirstSelectedItem().isPresent());
		assertEquals(0, input.getSelectedItems().size());

		final Set<String> set = new HashSet<>();
		set.add("a");
		set.add("b");

		input.select("a");
		input.select("b");
		assertEquals(2, input.getSelectedItems().size());
		assertEquals(set, input.getSelectedItems());
		assertTrue(input.isSelected("a"));
		assertTrue(input.isSelected("b"));

		input.deselect("a");
		assertEquals(Collections.singleton("b"), input.getSelectedItems());
		assertEquals("b", input.getFirstSelectedItem().orElse(null));
		assertEquals(1, input.getSelectedItems().size());
		assertEquals("b", input.getSelectedItems().iterator().next());
		assertTrue(input.isSelected("b"));
		assertFalse(input.isSelected("a"));

		input.deselectAll();
		assertTrue(input.isEmpty());
		assertFalse(input.getFirstSelectedItem().isPresent());
		assertEquals(0, input.getSelectedItems().size());
		assertFalse(input.isSelected("a"));
		assertFalse(input.isSelected("b"));

		input.select(set);
		assertEquals(2, input.getSelectedItems().size());
		assertEquals(set, input.getSelectedItems());
		assertTrue(input.isSelected("a"));
		assertTrue(input.isSelected("b"));

		input.select("a", "b");
		assertEquals(2, input.getSelectedItems().size());
		assertEquals(set, input.getSelectedItems());
		assertTrue(input.isSelected("a"));
		assertTrue(input.isSelected("b"));

		input.clear();

		final SetValue sv = new SetValue();

		input.addSelectionListener(e -> {
			sv.value = e.getAllSelectedItems();
		});

		assertTrue(sv.value.isEmpty());

		input.select("a");
		assertFalse(sv.value.isEmpty());
		assertEquals("a", sv.value.iterator().next());

		input.deselectAll();
		assertTrue(sv.value.isEmpty());

		input.select("a", "b");
		assertFalse(sv.value.isEmpty());
		assertTrue(sv.value.contains("a"));
		assertTrue(sv.value.contains("b"));

		sv.value = new HashSet<>();

		input = Input.multiListSelect(String.class).addItem("a").addItem("b")
				.withSelectionListener(e -> sv.value = e.getAllSelectedItems()).build();

		assertTrue(sv.value.isEmpty());

		input.select("b");
		assertEquals("b", sv.value.iterator().next());

		input.deselectAll();
		assertTrue(sv.value.isEmpty());

		final Set<String> val = new HashSet<>();
		val.add("a");
		val.add("b");

		input.setValue(val);
		assertFalse(sv.value.isEmpty());
		assertTrue(sv.value.contains("a"));
		assertTrue(sv.value.contains("b"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testItemsDataProvider() {

		MultiSelect<String> input = Input.multiListSelect(String.class).items(Arrays.asList("one", "two")).build();

		DataProvider<String, ?> dp = ((MultiSelectListBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		Set<String> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

		input = Input.multiListSelect(String.class).items("one", "two").build();

		dp = ((MultiSelectListBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);
		assertEquals(2, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains("one"));
		assertTrue(items.contains("two"));

		input = Input.multiListSelect(String.class).addItem("one").addItem("two").build();

		dp = ((MultiSelectListBox<String>) input.getComponent()).getDataProvider();
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

		MultiSelect<String> input = Input.multiListSelect(String.class)
				.dataSource(DataProvider.ofCollection(Arrays.asList("a", "b", "c"))).build();

		DataProvider<String, ?> dp = ((MultiSelectListBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		Set<String> items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("b"));
		assertTrue(items.contains("c"));

		input = Input.multiListSelect(String.class).dataSource(DataProvider.ofCollection(Arrays.asList("a", "aa", "b")))
				.build();

		dp = ((MultiSelectListBox<String>) input.getComponent()).getDataProvider();
		assertNotNull(dp);

		assertEquals(3, dp.size(new Query<>()));

		items = dp.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(3, items.size());
		assertTrue(items.contains("a"));
		assertTrue(items.contains("aa"));
		assertTrue(items.contains("b"));

		final DataProvider<String, ?> idp = DataProvider
				.<String>fromCallbacks(q -> Arrays.asList("a", "b", "c").stream(), q -> 3);

		input = Input.multiListSelect(String.class).dataSource(idp).build();

		dp = ((MultiSelectListBox<String>) input.getComponent()).getDataProvider();
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

		MultiSelect<BeanTest1> input1 = Input.multiListSelect(BeanTest1.class).dataSource(datastore, TARGET1).build();
		assertNotNull(input1);

		DataProvider<BeanTest1, ?> dp1 = ((MultiSelectListBox<BeanTest1>) input1.getComponent()).getDataProvider();
		assertNotNull(dp1);

		assertEquals(2, dp1.size(new Query<>()));

		Set<BeanTest1> items = dp1.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, items.size());
		assertTrue(items.contains(new BeanTest1("A")));
		assertTrue(items.contains(new BeanTest1("B")));

		MultiSelect<String> input2 = Input
				.multiListSelect(String.class, BeanTest1.class,
						ItemConverter.create(item -> item.getCode(), value -> Optional.of(new BeanTest1(value))))
				.dataSource(datastore, TARGET1).build();
		assertNotNull(input2);

		DataProvider<String, String> dp3 = (DataProvider<String, String>) ((MultiSelectListBox<String>) input2
				.getComponent()).getDataProvider();
		assertNotNull(dp3);

		assertEquals(2, dp3.size(new Query<>()));

		Set<String> sitems = dp3.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, sitems.size());

		// properties

		MultiSelect<String> input4 = Input.multiListSelect(CODE).dataSource(datastore, TARGET1, TEST1).build();
		assertNotNull(input4);

		DataProvider<PropertyBox, String> dp4 = (DataProvider<PropertyBox, String>) ((MultiSelectListBox<PropertyBox>) input4
				.getComponent()).getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		Set<PropertyBox> pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description B".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.multiListSelect(CODE).dataSource(datastore, TARGET1, CODE, DESCRIPTION).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((MultiSelectListBox<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description B".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.multiListSelect(CODE).dataSource(datastore, TARGET1, TEST1).withQueryFilter(CODE.eq("A"))
				.build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((MultiSelectListBox<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);

		assertEquals(1, dp4.size(new Query<>()));

		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(1, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "Description A".equals(i.getValue(DESCRIPTION))).count());

		input4 = Input.multiListSelect(CODE).dataSource(datastore, TARGET1, TEST1).withQueryFilter(CODE.isNotNull())
				.withQuerySort(DESCRIPTION.desc()).build();
		assertNotNull(input4);

		dp4 = (DataProvider<PropertyBox, String>) ((MultiSelectListBox<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);

		assertEquals(2, dp4.size(new Query<>()));

		PropertyBox itm = dp4.fetch(new Query<>()).findFirst().orElse(null);
		assertNotNull(itm);
		assertEquals("B", itm.getValue(CODE));

		// single property

		input4 = Input.multiListSelect(CODE).dataSource(datastore, TARGET1).build();

		dp4 = (DataProvider<PropertyBox, String>) ((MultiSelectListBox<PropertyBox>) input4.getComponent())
				.getDataProvider();
		assertNotNull(dp4);
		assertEquals(2, dp4.size(new Query<>()));
		pitems = dp4.fetch(new Query<>()).collect(Collectors.toSet());
		assertEquals(2, pitems.size());
		assertEquals(1, pitems.stream().filter(i -> "A".equals(i.getValue(CODE))).count());
		assertEquals(1, pitems.stream().filter(i -> "B".equals(i.getValue(CODE))).count());

	}

	@Test
	public void testValidatable() {

		ValidatableMultiSelect<String> input = Input.multiListSelect(String.class).validatable()
				.withValidator(Validator.notEmpty()).id("testid").addItem("a").addItem("b").build();

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		Assertions.assertThrows(ValidationException.class, () -> input.validate());

		ValidatableMultiSelect<String> input2 = Input.multiListSelect(String.class).validatable().required()
				.addItem("a").addItem("b").build();

		Assertions.assertThrows(ValidationException.class, () -> input2.validate());

		input2.select("a");
		Assertions.assertDoesNotThrow(() -> input2.validate());

		input2.clear();
		Assertions.assertThrows(ValidationException.class, () -> input2.validate());

		input2.select("b");
		Assertions.assertDoesNotThrow(() -> input2.validate());

		input2.clear();
		Assertions.assertThrows(ValidationException.class, () -> input2.validate());
	}

	@Test
	public void testAdapters() {

		MultiSelect<String> input = Input.multiListSelect(String.class)
				.withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

		assertFalse(input.as(Collection.class).isPresent());

		input = Input.multiListSelect(String.class).validatable()
				.withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

	}

	private class SetValue {
		Set<String> value = new HashSet<>();
	}

}
