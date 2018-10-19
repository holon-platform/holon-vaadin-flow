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

import com.holonplatform.vaadin.flow.components.Input;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.internal.CurrentInstance;

public class TestInput {

	private class StringValue {

		String value;

	}

	@Test
	public void testBuilders() {

		final TextArea ta = new TextArea();

		Input<String> i = Input.from(ta);
		assertNotNull(i);

		assertFalse(i.isReadOnly());
		assertFalse(i.isRequired());
		assertTrue(i.isVisible());

		assertTrue(i.isEmpty());
		assertEquals("", i.getValue());

		i.setValue("test");
		assertEquals("test", i.getValue());
		assertEquals("test", i.getValueIfPresent().orElse(null));

		assertEquals(ta.getValue(), i.getValue());

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

	}

}
