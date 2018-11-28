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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupBuilder;
import com.holonplatform.vaadin.flow.internal.components.RequiredInputValidator;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.converter.Converter;

public class TestPropertyInputGroup {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> pb.containsValue(NAME) ? "[" + pb.getValue(NAME) + "]" : null);

	private static final PropertySet<?> SET = PropertySet.of(ID, NAME, VIRTUAL);

	private class StringValue {
		String value;
	}

	private class PropertyBoxValue {
		PropertyBox value;
	}

	@Test
	public void testBuilder() {

		PropertyInputGroupBuilder builder = PropertyInputGroup.builder(SET);
		assertNotNull(builder);

		builder = Components.input.propertyGroup(SET);
		assertNotNull(builder);

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertNotNull(group);

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		final AtomicInteger size = new AtomicInteger(0);
		group.getProperties().iterator().forEachRemaining(p -> size.incrementAndGet());
		assertEquals(3, size.get());

		group = PropertyInputGroup.builder(ID, NAME, VIRTUAL).build();
		assertNotNull(group);

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		final Input<String> input = Input.create(String.class)
				.orElseThrow(() -> new RuntimeException("Failed to create Input"));

		final InputPropertyRenderer<String> renderer1 = InputPropertyRenderer.create(p -> input);

		group = PropertyInputGroup.builder(ID, NAME).bind(NAME, input).build();
		assertEquals(input, group.getInput(NAME).orElse(null));

		group = PropertyInputGroup.builder(ID, NAME).bind(NAME, renderer1).build();
		assertEquals(input, group.getInput(NAME).orElse(null));

		group = PropertyInputGroup.builder(ID, NAME).bind(NAME, p -> input).build();
		assertEquals(input, group.getInput(NAME).orElse(null));

		final TextField tf = new TextField();
		group = PropertyInputGroup.builder(ID, NAME).bindField(NAME, tf).build();
		assertEquals(tf, group.getInput(NAME).map(i -> i.getComponent()).orElse(null));

	}

	@Test
	public void testConverters() {

		final Input<String> input = Input.create(String.class)
				.orElseThrow(() -> new RuntimeException("Failed to create Input"));

		PropertyInputGroup group = PropertyInputGroup.builder(ID, NAME)
				.bind(ID, input, Converter.from(v -> Result.ok(Long.valueOf(v)), v -> String.valueOf(v))).build();
		Input<Long> li = group.getInput(ID).orElse(null);
		assertNotNull(li);
		li.setValue(1L);
		assertEquals(Long.valueOf(1), li.getValue());

		group.setValue(PropertyBox.builder(ID, NAME).set(NAME, "test").build());

		li.setValue(2L);

		PropertyBox value = group.getValue();
		assertNotNull(value);
		assertEquals(Long.valueOf(2), value.getValue(ID));

		group = PropertyInputGroup.builder(ID, NAME)
				.bindField(ID, new TextField(), Converter.from(v -> Result.ok(Long.valueOf(v)), v -> String.valueOf(v)))
				.build();
		li = group.getInput(ID).orElse(null);
		assertNotNull(li);
		li.setValue(1L);
		assertEquals(Long.valueOf(1), li.getValue());

		group.setValue(PropertyBox.builder(ID, NAME).set(NAME, "test").build());

		li.setValue(2L);

		value = group.getValue();
		assertNotNull(value);
		assertEquals(Long.valueOf(2), value.getValue(ID));

	}

	@Test
	public void testHidden() {

		PropertyInputGroup group = PropertyInputGroup.builder(ID, NAME).hidden(ID).build();
		assertTrue(group.hasProperty(ID));
		assertFalse(group.getInput(ID).isPresent());

		group.setValue(PropertyBox.builder(ID, NAME).set(ID, 7L).build());
		PropertyBox value = group.getValue();
		assertNotNull(value);
		assertEquals(Long.valueOf(7), value.getValue(ID));

	}

	@Test
	public void testReadOnly() {

		PropertyInputGroup group = PropertyInputGroup.builder(ID, NAME).readOnly(ID).build();
		assertTrue(group.hasProperty(ID));
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(ID).get().isReadOnly());

		group.setValue(PropertyBox.builder(ID, NAME).set(ID, 7L).build());
		PropertyBox value = group.getValue();
		assertNotNull(value);
		assertEquals(Long.valueOf(7), value.getValue(ID));

		// read-only property
		group = PropertyInputGroup.builder(SET).build();
		assertTrue(group.hasProperty(VIRTUAL));
		assertTrue(group.getInput(VIRTUAL).get().isReadOnly());

	}

	@Test
	public void testRequired() {

		PropertyInputGroup group = PropertyInputGroup.builder(ID, NAME).required(ID).build();
		assertTrue(group.hasProperty(ID));
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(ID).get().isRequired());

		group.setValue(PropertyBox.builder(ID, NAME).set(NAME, "test").build());
		assertFalse(group.isValid());

		ValidationException ve = assertThrows(ValidationException.class, () -> {
			PropertyInputGroup group2 = PropertyInputGroup.builder(ID, NAME).required(ID).build();
			group2.validate();
		});
		assertEquals(RequiredInputValidator.DEFAULT_REQUIRED_ERROR.getMessage(), ve.getMessage());

		ve = assertThrows(ValidationException.class, () -> {
			PropertyInputGroup group2 = PropertyInputGroup.builder(ID, NAME).required(ID, "test_req").build();
			group2.validate();
		});
		assertEquals("test_req", ve.getMessage());

		LocalizationTestUtils.withInputValidationLocalizationContext(() -> {
			PropertyInputGroup group2 = PropertyInputGroup.builder(ID, NAME).required(ID).build();
			ValidationException vex = assertThrows(ValidationException.class, () -> group2.validate());
			assertEquals(LocalizationTestUtils.TEST_LOCALIZED_REQUIRED_ERROR, vex.getLocalizedMessage());
		});

		LocalizationTestUtils.withInputValidationLocalizationContext(() -> {
			PropertyInputGroup group2 = PropertyInputGroup.builder(ID, NAME)
					.required(ID, "TEST", RequiredInputValidator.DEFAULT_REQUIRED_ERROR.getMessageCode()).build();
			ValidationException vex = assertThrows(ValidationException.class, () -> group2.validate());
			assertEquals(LocalizationTestUtils.TEST_LOCALIZED_REQUIRED_ERROR, vex.getLocalizedMessage());
		});

	}

	@Test
	public void testPostProcessor() {

		PropertyInputGroup group = PropertyInputGroup.builder(ID, NAME).withPostProcessor((property, component) -> {
			if (ID.equals(property)) {
				component.hasEnabled().ifPresent(e -> e.setEnabled(false));
			}
		}).build();

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(ID).isPresent());
		assertFalse(group.getInput(ID).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));
		assertTrue(group.getInput(NAME).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));

	}

	@Test
	public void testDefaultValue() {

		PropertyInputGroup group = PropertyInputGroup.builder(ID, NAME).defaultValue(NAME, () -> "DFT").build();
		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());

		group.setValue(PropertyBox.builder(ID, NAME).build());
		PropertyBox value = group.getValue();
		assertNotNull(value);
		assertEquals("DFT", value.getValue(NAME));

		group.setValue(PropertyBox.builder(ID, NAME).set(ID, 7L).build());
		value = group.getValue();
		assertNotNull(value);
		assertEquals(Long.valueOf(7), value.getValue(ID));
		assertEquals("DFT", value.getValue(NAME));

		group.setValue(PropertyBox.builder(ID, NAME).set(ID, 7L).set(NAME, "name").build());
		value = group.getValue();
		assertNotNull(value);
		assertEquals(Long.valueOf(7), value.getValue(ID));
		assertEquals("name", value.getValue(NAME));

	}

	@Test
	public void testClear() {

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());

		assertTrue(group.requireInput(ID).isEmpty());
		assertTrue(group.requireInput(NAME).isEmpty());

		group.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test").build());
		assertFalse(group.requireInput(ID).isEmpty());
		assertFalse(group.requireInput(NAME).isEmpty());
		assertEquals(Long.valueOf(1), group.requireInput(ID).getValue());
		assertEquals("test", group.requireInput(NAME).getValue());

		group.clear();
		assertTrue(group.requireInput(ID).isEmpty());
		assertTrue(group.requireInput(NAME).isEmpty());
	}

	@Test
	public void testGroup() {

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertNotNull(group);

		assertEquals(3, group.getProperties().size());
		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		assertEquals(3, group.getBindings().filter(b -> b.getComponent() != null).count());

		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());
		assertTrue(group.getInput(VIRTUAL).isPresent());

		assertTrue(group.isEmpty());
		assertNull(group.getValueIfPresent().orElse(null));

		group.setValue(PropertyBox.create(SET));
		assertNotNull(group.getValue());
		assertFalse(group.isEmpty());

		group.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test").build());
		assertNotNull(group.getValue());
		assertFalse(group.isEmpty());

		assertNotNull(group.getInput(ID).map(c -> c.getValue()).orElse(null));
		assertNotNull(group.getInput(NAME).map(c -> c.getValue()).orElse(null));
		assertNotNull(group.getInput(VIRTUAL).map(c -> c.getValue()).orElse(null));

		group.clear();
		assertTrue(group.isEmpty());

		final AtomicInteger fired = new AtomicInteger(0);

		group = PropertyInputGroup.builder(SET).withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		group.setValue(PropertyBox.builder(SET).set(ID, 2L).build());
		assertEquals(1, fired.get());

		group.clear();
		assertEquals(2, fired.get());

	}

	@Test
	public void testReadOnlyGroup() {

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());
		assertTrue(group.getInput(VIRTUAL).isPresent());

		group.setReadOnly(true);
		assertTrue(group.getInput(ID).get().isReadOnly());
		assertTrue(group.getInput(NAME).get().isReadOnly());
		assertTrue(group.getInput(VIRTUAL).get().isReadOnly());

		group.setReadOnly(false);
		assertFalse(group.getInput(ID).get().isReadOnly());
		assertFalse(group.getInput(NAME).get().isReadOnly());
		assertTrue(group.getInput(VIRTUAL).get().isReadOnly());

	}

	@Test
	public void testEnabledGroup() {

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());

		group.setEnabled(false);
		assertFalse(group.getInput(ID).get().hasEnabled().map(e -> e.isEnabled()).orElse(true));
		assertFalse(group.getInput(NAME).get().hasEnabled().map(e -> e.isEnabled()).orElse(true));

		group.setEnabled(true);
		assertTrue(group.getInput(ID).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));
		assertTrue(group.getInput(NAME).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));

	}

	@Test
	public void testValueChangeListeners() {

		final AtomicInteger fired = new AtomicInteger(0);
		final AtomicInteger propertyFired = new AtomicInteger(0);

		final PropertyBoxValue ov = new PropertyBoxValue();
		final PropertyBoxValue nv = new PropertyBoxValue();

		final StringValue opv = new StringValue();
		final StringValue npv = new StringValue();

		PropertyInputGroup group = PropertyInputGroup.builder(SET).withValueChangeListener(e -> {
			fired.incrementAndGet();
			ov.value = e.getOldValue();
			nv.value = e.getValue();
		}).withValueChangeListener(NAME, e -> {
			propertyFired.incrementAndGet();
			opv.value = (e.getOldValue() != null && e.getOldValue().length() == 0) ? null : e.getOldValue();
			npv.value = (e.getValue() != null && e.getValue().length() == 0) ? null : e.getValue();
		}).build();

		assertEquals(0, fired.get());
		assertEquals(0, propertyFired.get());

		group.requireInput(NAME).setValue("x");
		assertEquals(0, fired.get());
		assertEquals(1, propertyFired.get());
		assertNull(ov.value);
		assertNull(nv.value);
		assertNull(opv.value);
		assertEquals("x", npv.value);

		group.clear();
		assertEquals(1, fired.get());
		assertEquals(2, propertyFired.get());
		assertNull(ov.value);
		assertNull(nv.value);
		assertEquals("x", opv.value);
		assertNull(npv.value);

		final PropertyBox v = PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test").build();

		group.setValue(v);
		assertEquals(2, fired.get());
		assertEquals(3, propertyFired.get());
		assertNull(ov.value);
		assertEquals(v, nv.value);
		assertNull(opv.value);
		assertEquals("test", npv.value);

	}

	@Test
	public void testRefresh() {

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());
		assertTrue(group.getInput(VIRTUAL).isPresent());

		assertNull(group.getValue().getValue(VIRTUAL));

		group.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test").build());
		assertEquals("[test]", group.getValue().getValue(VIRTUAL));
		assertEquals("[test]", group.requireInput(VIRTUAL).getValue());

		group.requireInput(NAME).setValue("test2");
		assertEquals("[test]", group.requireInput(VIRTUAL).getValue());
		assertEquals("[test2]", group.getValue().getValue(VIRTUAL));

		group.refresh(VIRTUAL);
		assertEquals("[test2]", group.requireInput(VIRTUAL).getValue());

		group.requireInput(NAME).setValue("test3");
		assertEquals("[test2]", group.requireInput(VIRTUAL).getValue());
		group.refresh();
		assertEquals("[test3]", group.requireInput(VIRTUAL).getValue());

	}

	@Test
	public void testValidators() {

		final PropertyInputGroup group1 = PropertyInputGroup.builder(SET).build();
		assertTrue(group1.isValid());
		assertDoesNotThrow(() -> group1.validate());
		assertDoesNotThrow(() -> group1.getValue());
		assertDoesNotThrow(() -> group1.getValue(true));
		assertTrue(group1.getValueIfValid().isPresent());

		final PropertyInputGroup group2 = PropertyInputGroup.builder(SET).withValidator(NAME, Validator.max(2)).build();
		assertTrue(group2.isValid());
		assertDoesNotThrow(() -> group2.validate());
		group2.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "abc").build());
		assertFalse(group2.isValid());
		assertThrows(ValidationException.class, () -> group2.validate());
		assertThrows(ValidationException.class, () -> group2.getValue());
		assertThrows(ValidationException.class, () -> group2.getValue(true));
		assertDoesNotThrow(() -> group2.getValue(false));
		assertFalse(group2.getValueIfValid().isPresent());

		final PropertyInputGroup group4 = PropertyInputGroup.builder(SET)
				.withValidator(
						Validator.create(v -> (v == null || v.getValue(ID) == null || v.getValue(ID) > 0), "error"))
				.build();
		assertTrue(group4.isValid());
		group4.setValue(PropertyBox.builder(SET).set(NAME, "abc").build());
		assertTrue(group4.isValid());
		group4.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "abc").build());
		assertTrue(group4.isValid());
		group4.setValue(PropertyBox.builder(SET).set(ID, 0L).set(NAME, "abc").build());
		assertFalse(group4.isValid());

	}

	@Test
	public void testValidationStatusHandler() {

		final StringValue error = new StringValue();

		final PropertyInputGroup group = PropertyInputGroup.builder(SET)
				.withValidator(
						Validator.create(v -> (v == null || v.getValue(ID) == null || v.getValue(ID) > 0), "error"))
				.validationStatusHandler(e -> {
					error.value = e.isInvalid() ? e.getErrorMessage() : null;
				}).build();

		assertTrue(group.isValid());
		assertNull(error.value);

		group.setValue(PropertyBox.builder(SET).set(ID, 0L).set(NAME, "abc").build());

		ValidationException ve = assertThrows(ValidationException.class, () -> group.validate());
		assertNotNull(error.value);
		assertEquals(ve.getMessage(), error.value);

		group.clear();
		assertNull(error.value);

		group.setValue(PropertyBox.builder(SET).set(ID, 0L).set(NAME, "abc").build());

		ve = assertThrows(ValidationException.class, () -> group.validate());
		assertNotNull(error.value);
		assertEquals(ve.getMessage(), error.value);

		group.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "abc").build());
		assertNull(error.value);

		// property

		error.value = null;

		final StringValue perror = new StringValue();

		final PropertyInputGroup group2 = PropertyInputGroup.builder(SET)
				.withValidator(
						Validator.create(v -> (v == null || v.getValue(ID) == null || v.getValue(ID) > 0), "error"))
				.validationStatusHandler(e -> {
					error.value = e.isInvalid() ? e.getErrorMessage() : null;
				}).withValidator(NAME, Validator.max(2)).validationStatusHandler(NAME, e -> {
					perror.value = e.isInvalid() ? e.getErrorMessage() : null;
				}).build();

		assertTrue(group2.isValid());
		assertNull(error.value);
		assertNull(perror.value);

		group2.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "abc").build());
		ve = assertThrows(ValidationException.class, () -> group2.validate());
		assertNotNull(perror.value);
		assertNull(error.value);

		group2.clear();
		assertNull(perror.value);
		assertNull(error.value);

		error.value = null;
		perror.value = null;

		group2.setValue(PropertyBox.builder(SET).set(ID, 0L).set(NAME, "abc").build());
		ve = assertThrows(ValidationException.class, () -> group2.validate());
		assertNotNull(perror.value);
		assertNull(error.value);

		error.value = null;
		perror.value = null;

		group2.setValue(PropertyBox.builder(SET).set(ID, 0L).set(NAME, "ab").build());
		ve = assertThrows(ValidationException.class, () -> group2.validate());
		assertNull(perror.value);
		assertNotNull(error.value);

	}

}
