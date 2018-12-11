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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.internal.CurrentInstance;

public class TestDateInput {

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

	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Paris");

	private static LocalDate asLocalDate(Date date) {
		if (date != null) {
			return Instant.ofEpochMilli(date.getTime()).atZone(ZONE_ID).toLocalDate();
		}
		return null;
	}

	@Test
	public void testBuilders() {

		DateInputBuilder builder = DateInputBuilder.create();
		assertNotNull(builder);
		Input<Date> input = builder.build();
		assertNotNull(input);

		builder = Input.date();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.date();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<Date> input = Input.date().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.date().build();
		assertTrue(input.isVisible());

		input = Input.date().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.date().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.date().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.date().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.date().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<Date> input = Input.date().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.date().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<Date> input = Input.date().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.date().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.date().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.date().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.date().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.date().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.date().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.date().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.date().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.date().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.date().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.date().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.date().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<Date> input = Input.date().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.date().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.date().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.date().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<Date> input = Input.date().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.date().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.date().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.date().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.date()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.date().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.date().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<Date> input = Input.date().build();
		assertFalse(input.isReadOnly());

		input = Input.date().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.date().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.date().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<Date> input = Input.date().build();
		assertFalse(input.isRequired());

		input = Input.date().required(true).build();
		assertTrue(input.isRequired());

		input = Input.date().required(false).build();
		assertFalse(input.isRequired());

		input = Input.date().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		final Calendar calendar = Calendar.getInstance();
		calendar.set(2018, 9, 22);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final Date ld = calendar.getTime();

		Input<Date> input = Input.date().withValue(ld).build();
		assertEquals(ld, input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.date().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(ld);
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<Date> input = Input.date().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof DatePicker);
		assertEquals(77, ((DatePicker) input.getComponent()).getTabIndex());

	}

	@Test
	public void testPlaceholder() {

		Input<Date> input = Input.date().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.date().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.date().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.date().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.date()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.date().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.date().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testConfiguration() {

		final Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(2018, 9, 22);
		final Date date1 = calendar.getTime();
		final Calendar calendar2 = Calendar.getInstance(TIME_ZONE);
		calendar2.set(2018, 9, 23);
		final Date date2 = calendar.getTime();

		Input<Date> input = Input.date().locale(Locale.ITALIAN).build();
		assertTrue(input.getComponent() instanceof DatePicker);
		assertEquals(Locale.ITALIAN, ((DatePicker) input.getComponent()).getLocale());

		final Input<Date> input2 = Input.date().updateLocaleOnAttach(true).build();

		assertEquals(Locale.US, ((DatePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.FRANCE);
		ComponentUtil.onComponentAttach(input2.getComponent(), true);
		assertEquals(Locale.FRANCE, ((DatePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.US);

		input = Input.date().min(date1).max(date2).build();
		assertEquals(asLocalDate(date1), ((DatePicker) input.getComponent()).getMin());
		assertEquals(asLocalDate(date2), ((DatePicker) input.getComponent()).getMax());

		input = Input.date().initialPosition(date1).build();
		assertEquals(asLocalDate(date1), ((DatePicker) input.getComponent()).getInitialPosition());

		input = Input.date().weekNumbersVisible(true).build();
		assertTrue(((DatePicker) input.getComponent()).isWeekNumbersVisible());
		input = Input.date().weekNumbersVisible(false).build();
		assertFalse(((DatePicker) input.getComponent()).isWeekNumbersVisible());

		input = Input.date().localization().today("_today").set().build();
		assertEquals("_today", ((DatePicker) input.getComponent()).getI18n().getToday());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input3 = Input.date().localization().today("test", "test.code").set().build();
			assertEquals("TestUS", ((DatePicker) input3.getComponent()).getI18n().getToday());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input3 = Input.date().deferLocalization().localization().today("test", "test.code").set()
					.build();
			assertNull(((DatePicker) input3.getComponent()).getI18n());
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", ((DatePicker) input3.getComponent()).getI18n().getToday());
		});

	}

	@Test
	public void testHasValue() {

		final Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(2018, 9, 22);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final Date date = calendar.getTime();

		Input<Date> input = Input.date().build();
		assertNull(input.getEmptyValue());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue(date);
		assertEquals(date, input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals(date, input.getValueIfPresent().orElse(null));

		input.clear();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

	}

}
