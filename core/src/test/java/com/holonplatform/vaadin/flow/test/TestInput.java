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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputFieldPropertyRenderer;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.Input.PropertyHandler;
import com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToBooleanConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.internal.CurrentInstance;

public class TestInput {

	private class StringValue {

		String value;

	}

	@Test
	public void testBuilders() {

		final TextField field = new TextField();

		HasValueInputBuilder<String, TextField, TextField> builder = Input.builder(field);
		assertNotNull(builder);

		builder = Input.builder(field, field);
		assertNotNull(builder);

		Input<String> i = Input.builder(field).requiredPropertyHandler(
				PropertyHandler.create((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))).build();
		assertNotNull(i);

		assertFalse(i.isRequired());
		i.setRequired(true);
		assertTrue(i.isRequired());
		assertTrue(field.isRequired());
		i.setRequired(false);
		assertFalse(i.isRequired());
		assertFalse(field.isRequired());

		i = Input.builder(field)
				.labelPropertyHandler(PropertyHandler.create((f, c) -> f.getLabel(), (f, c, v) -> f.setLabel(v)))
				.build();
		assertNotNull(i);

		assertTrue(i.hasLabel().isPresent());
		i.hasLabel().ifPresent(t -> t.setLabel("test"));
		assertEquals("test", field.getLabel());
		assertEquals("test", ComponentTestUtils.getLabel(i));

		i = Input.builder(field)
				.titlePropertyHandler(PropertyHandler.create((f, c) -> f.getTitle(), (f, c, v) -> field.setTitle(v)))
				.build();
		assertNotNull(i);

		assertTrue(i.hasTitle().isPresent());
		i.hasTitle().ifPresent(t -> t.setTitle("test"));
		assertEquals("test", field.getTitle());
		assertEquals("test", ComponentTestUtils.getTitle(i));

		i = Input.builder(field).placeholderPropertyHandler(
				PropertyHandler.create((f, c) -> f.getPlaceholder(), (f, c, v) -> f.setPlaceholder(v))).build();
		assertNotNull(i);

		assertTrue(i.hasPlaceholder().isPresent());
		i.hasPlaceholder().ifPresent(t -> t.setPlaceholder("test"));
		assertEquals("test", field.getPlaceholder());
		assertEquals("test", ComponentTestUtils.getPlaceholder(i));
	}

	@Test
	public void testAdapters() {

		final TextField field = new TextField();

		Input<String> i = Input.from(field);
		assertNotNull(i);

		assertFalse(i.isReadOnly());
		assertFalse(i.isRequired());
		assertTrue(i.isVisible());

		assertTrue(i.isEmpty());
		assertEquals("", i.getValue());

		i.setValue("test");
		assertEquals("test", i.getValue());
		assertEquals("test", i.getValueIfPresent().orElse(null));

		assertEquals(field.getValue(), i.getValue());

		i.clear();
		assertTrue(i.isEmpty());
		assertEquals("", i.getValue());

		i.setValue("test");
		assertEquals("test", i.getValue());

		i.setValue(null);
		assertTrue(i.isEmpty());
		assertEquals("", i.getValue());

		i.setVisible(false);
		assertFalse(i.isVisible());
		i.setVisible(true);
		assertTrue(i.isVisible());

		i.setRequired(true);
		assertTrue(i.isRequired());
		i.setRequired(false);
		assertFalse(i.isRequired());

		i.setReadOnly(true);
		assertTrue(i.isReadOnly());
		i.setReadOnly(false);
		assertFalse(i.isReadOnly());

		final AtomicInteger fired = new AtomicInteger(0);

		final StringValue osv = new StringValue();
		final StringValue nsv = new StringValue();

		i.addValueChangeListener(e -> {
			fired.incrementAndGet();
			osv.value = (e.getOldValue() != null && e.getOldValue().length() == 0) ? null : e.getOldValue();
			nsv.value = (e.getValue() != null && e.getValue().length() == 0) ? null : e.getValue();
		});

		assertEquals(0, fired.get());
		assertNull(osv.value);
		assertNull(nsv.value);

		i.setValue("test");

		assertEquals(1, fired.get());
		assertNull(osv.value);
		assertEquals("test", nsv.value);

		UI ui = new UI();
		ui.setLocale(Locale.ITALY);
		CurrentInstance.set(UI.class, ui);

		Input<LocalDate> i2 = Input.from(new DatePicker());
		assertNotNull(i2);

	}

	@SuppressWarnings("serial")
	@Test
	public void testConverters() {

		Input<Integer> i1 = Input.from(new TextField(), new StringToIntegerConverter("Conversion failed"));
		assertNotNull(i1);

		assertTrue(i1.isEmpty());
		assertNull(i1.getValue());

		i1.setValue(7);
		assertFalse(i1.isEmpty());
		assertEquals(Integer.valueOf(7), i1.getValue());

		i1.clear();
		assertTrue(i1.isEmpty());
		assertNull(i1.getValue());

		final NumericProperty<Long> prp = NumericProperty.longType("test");

		Input<Long> i2 = Input.from(new TextField(), prp, new PropertyValueConverter<Long, String>() {

			@Override
			public Long fromModel(String value, Property<Long> property) throws PropertyConversionException {
				return (value == null || value.length() == 0) ? null : Long.parseLong(value);
			}

			@Override
			public String toModel(Long value, Property<Long> property) throws PropertyConversionException {
				return (value == null) ? null : String.valueOf(value);
			}

			@Override
			public Class<Long> getPropertyType() {
				return Long.class;
			}

			@Override
			public Class<String> getModelType() {
				return String.class;
			}
		});

		assertTrue(i2.isEmpty());
		assertNull(i2.getValue());

		i2.setValue(7L);
		assertFalse(i2.isEmpty());
		assertEquals(Long.valueOf(7), i2.getValue());

		i2.clear();
		assertTrue(i2.isEmpty());
		assertNull(i2.getValue());

		final Input<String> si = Input.from(new TextField());

		Input<Boolean> i3 = Input.from(si, new StringToBooleanConverter("Conversion failed"));
		assertTrue(i3.isEmpty());
		assertNull(i3.getValue());

		i3.setValue(Boolean.TRUE);
		assertFalse(i3.isEmpty());
		assertEquals(Boolean.TRUE, i3.getValue());

		i3.setValue(null);
		assertTrue(i3.isEmpty());
		assertNull(i3.getValue());

		final BooleanProperty prp2 = BooleanProperty.create("test");

		Input<Boolean> i4 = Input.from(i1, prp2, PropertyValueConverter.numericBoolean(Integer.class));
		assertTrue(i4.isEmpty());
		assertEquals(Boolean.FALSE, i4.getValue());

		i4.setValue(Boolean.TRUE);
		assertFalse(i4.isEmpty());
		assertEquals(Boolean.TRUE, i4.getValue());

		i4.setValue(null);
		assertEquals(Boolean.FALSE, i4.getValue());

	}

	@Test
	public void testInputRenderer() {

		final StringProperty prp = StringProperty.create("test");

		InputPropertyRenderer<String> r1 = property -> Input.from(new TextField());
		assertNotNull(r1);

		Input<String> i = r1.render(prp);
		assertNotNull(i);

		InputFieldPropertyRenderer<String, ComponentValueChangeEvent<TextField, String>, TextField> r2 = property -> new TextField();
		i = r2.render(prp);
		assertNotNull(i);

	}

	@Test
	public void testHasEnabled() {

		final TextField field = new TextField();

		final Input<String> input = Input.from(field);
		assertTrue(input.hasEnabled().isPresent());

		assertTrue(input.hasEnabled().map(e -> e.isEnabled()).orElse(false));

		input.hasEnabled().ifPresent(e -> e.setEnabled(false));
		assertFalse(field.isEnabled());

		input.hasEnabled().ifPresent(e -> e.setEnabled(true));
		assertTrue(field.isEnabled());

	}

	@Test
	public void testHasStyle() {

		final TextField field = new TextField();

		final Input<String> input = Input.from(field);
		assertTrue(input.hasStyle().isPresent());

		input.hasStyle().ifPresent(s -> s.addClassName("test-input"));
		assertTrue(input.hasStyle().map(s -> s.getClassNames().contains("test-input")).orElse(false));
		assertTrue(field.getClassNames().contains("test-input"));

	}

	@Test
	public void testSize() {

		final TextField field = new TextField();

		final Input<String> input = Input.from(field);
		assertTrue(input.hasSize().isPresent());

		input.hasSize().ifPresent(s -> s.setWidth("77px"));
		assertEquals("77px", input.hasSize().map(s -> s.getWidth()).orElse(null));
		assertEquals("77px", field.getWidth());

	}

}
