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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.Context;
import com.holonplatform.core.config.ConfigProperty;
import com.holonplatform.core.internal.utils.CalendarUtils;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.TemporalProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.CurrentInstance;

public class TestInputRenderer {

	private enum TestEnum {

		A, B, C;

	}

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

	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultRenderer() {

		final Property<String> p1 = StringProperty.create("test");
		final Input<String> i1 = p1.render(Input.class);
		assertNotNull(i1);
		assertNotNull(i1.getComponent());
		i1.setValue("X");
		assertEquals("X", i1.getValue());

		final Input<String> i1bis = Input.create(p1);
		assertNotNull(i1bis);
		assertNotNull(i1bis.getComponent());
		i1bis.setValue("X");
		assertEquals("X", i1bis.getValue());

		final Property<Boolean> p2 = BooleanProperty.create("test");
		final Input<Boolean> i2 = p2.render(Input.class);
		assertNotNull(i2);
		assertNotNull(i2.getComponent());
		i2.setValue(Boolean.TRUE);
		assertEquals(Boolean.TRUE, i2.getValue());

		final Property<TestEnum> p3 = PathProperty.create("test", TestEnum.class);
		final Input<TestEnum> i3 = p3.render(Input.class);
		assertNotNull(i3);
		assertNotNull(i3.getComponent());
		i3.setValue(TestEnum.B);
		assertEquals(TestEnum.B, i3.getValue());

		final Property<Integer> p4 = NumericProperty.integerType("test");
		final Input<Integer> i4 = p4.render(Input.class);
		assertNotNull(i4);
		assertNotNull(i4.getComponent());
		i4.setValue(Integer.valueOf(777));
		assertEquals(Integer.valueOf(777), i4.getValue());

		final Property<Double> p5 = NumericProperty.doubleType("test");
		final Input<Double> i5 = p5.render(Input.class);
		assertNotNull(i5);
		assertNotNull(i5.getComponent());
		i5.setValue(Double.valueOf(777.56));
		assertEquals(Double.valueOf(777.56), i5.getValue());

		final Property<LocalDate> p6 = TemporalProperty.localDate("test");
		final Input<LocalDate> i6 = p6.render(Input.class);
		assertNotNull(i6);
		assertNotNull(i6.getComponent());
		i6.setValue(LocalDate.of(2018, Month.OCTOBER, 30));
		assertEquals(LocalDate.of(2018, Month.OCTOBER, 30), i6.getValue());

		final Property<LocalTime> p7 = TemporalProperty.localTime("test");
		final Input<LocalTime> i7 = p7.render(Input.class);
		assertNotNull(i7);
		assertNotNull(i7.getComponent());
		i7.setValue(LocalTime.of(17, 54));
		assertEquals(LocalTime.of(17, 54), i7.getValue());

		final Property<LocalDateTime> p8 = TemporalProperty.localDateTime("test");
		final Input<LocalDateTime> i8 = p8.render(Input.class);
		assertNotNull(i8);
		assertNotNull(i8.getComponent());
		i8.setValue(LocalDateTime.of(2018, Month.OCTOBER, 30, 17, 54));
		assertEquals(LocalDateTime.of(2018, Month.OCTOBER, 30, 17, 54), i8.getValue());

		Calendar c = Calendar.getInstance();
		c.set(2018, 9, 30, 17, 57);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date dv = c.getTime();

		final Property<Date> p9 = TemporalProperty.date("test");
		final Input<Date> i9 = p9.render(Input.class);
		assertNotNull(i9);
		assertNotNull(i9.getComponent());
		i9.setValue(dv);
		assertEquals(CalendarUtils.floorTime(dv), i9.getValue());

		final Property<Date> p10 = TemporalProperty.date("test").temporalType(TemporalType.DATE_TIME);
		final Input<Date> i10 = p10.render(Input.class);
		assertNotNull(i10);
		assertNotNull(i10.getComponent());
		i10.setValue(dv);
		assertEquals(dv, i10.getValue());

	}

	@Test
	public void testReadOnly() {

		final Property<String> P1 = StringProperty.create("test");
		Input<String> input = Input.create(P1);
		assertFalse(input.isReadOnly());

		final VirtualProperty<String> VP = VirtualProperty.create(String.class, pb -> "TEST");
		input = Input.create(VP);
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testCustomRenderer() {

		final ConfigProperty<Boolean> REQUIRED = ConfigProperty.create("test_required", Boolean.class);

		final Property<String> P1 = StringProperty.create("test").withConfiguration(REQUIRED, true);

		Input<String> i1 = Input.create(P1);
		assertFalse(i1.isRequired());

		Context.get().executeThreadBound(PropertyRendererRegistry.CONTEXT_KEY, PropertyRendererRegistry.create(true),
				() -> {

					final PropertyRendererRegistry registry = PropertyRendererRegistry.get();

					registry.register(p -> P1.equals(p),
							InputPropertyRenderer.<String>create(p -> Input.string().required().build()));

					final Input<String> i2 = Input.create(P1);
					assertTrue(i2.isRequired());

				});

		Context.get().executeThreadBound(PropertyRendererRegistry.CONTEXT_KEY, PropertyRendererRegistry.create(true),
				() -> {

					final PropertyRendererRegistry registry = PropertyRendererRegistry.get();

					registry.forProperty(P1,
							InputPropertyRenderer.<String>create(p -> Input.string().required().build()));

					final Input<String> i2 = Input.create(P1);
					assertTrue(i2.isRequired());

				});

		Context.get().executeThreadBound(PropertyRendererRegistry.CONTEXT_KEY, PropertyRendererRegistry.create(true),
				() -> {

					final PropertyRendererRegistry registry = PropertyRendererRegistry.get();

					registry.forPropertyConfiguration(REQUIRED, true,
							InputPropertyRenderer.<String>create(p -> Input.string().required().build()));

					final Input<String> i2 = Input.create(P1);
					assertTrue(i2.isRequired());

				});

		i1 = Input.create(P1);
		assertFalse(i1.isRequired());

	}

}
