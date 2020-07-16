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

import java.util.Calendar;
import java.util.Collection;
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
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.test.util.TestAdapter;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.internal.CurrentInstance;

public class TestDateTimeInput {

	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Paris");

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

		DateTimeInputBuilder builder = DateTimeInputBuilder.create();
		assertNotNull(builder);
		Input<Date> input = builder.build();
		assertNotNull(input);

		builder = Input.dateTime();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.dateTime();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<Date> input = Input.dateTime().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.dateTime().build();
		assertTrue(input.isVisible());

		input = Input.dateTime().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.dateTime().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.dateTime().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.dateTime().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.dateTime().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<Date> input = Input.dateTime().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.dateTime().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<Date> input = Input.dateTime().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.dateTime().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.dateTime().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.dateTime().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.dateTime().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.dateTime().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.dateTime().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.dateTime().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.dateTime().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.dateTime().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.dateTime().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.dateTime().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.dateTime().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<Date> input = Input.dateTime().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.dateTime().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.dateTime().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.dateTime().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<Date> input = Input.dateTime().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.dateTime().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.dateTime().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.dateTime().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<Date> input = Input.dateTime().build();
		assertFalse(input.isReadOnly());

		input = Input.dateTime().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.dateTime().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.dateTime().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<Date> input = Input.dateTime().build();
		assertFalse(input.isRequired());

		input = Input.dateTime().required(true).build();
		assertTrue(input.isRequired());

		input = Input.dateTime().required(false).build();
		assertFalse(input.isRequired());

		input = Input.dateTime().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		final Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(2018, 9, 22);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 45);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final Date ld = calendar.getTime();

		Input<Date> input = Input.dateTime().withValue(ld).build();
		assertEquals(ld, input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.dateTime().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(ld);
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<Date> input = Input.dateTime().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof DateTimePicker);
		assertEquals(77, ((DateTimePicker) input.getComponent()).getTabIndex());

	}

	@Test
	public void testPlaceholder() {

		Input<Date> input = Input.dateTime().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.dateTime().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.dateTime().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.dateTime().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testConfiguration() {

		final Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(2018, 9, 22, 11, 20);

		final Calendar calendar2 = Calendar.getInstance(TIME_ZONE);
		calendar2.set(2018, 9, 23, 16, 30);

		Input<Date> input = Input.dateTime().locale(Locale.ITALIAN).build();
		assertTrue(input.getComponent() instanceof DateTimePicker);
		assertEquals(Locale.ITALIAN, ((DateTimePicker) input.getComponent()).getLocale());

		final Input<Date> input2 = Input.dateTime().updateLocaleOnAttach(true).build();

		assertEquals(Locale.US, ((DateTimePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.FRANCE);
		ComponentUtil.onComponentAttach(input2.getComponent(), true);
		assertEquals(Locale.FRANCE, ((DateTimePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.US);

		input = Input.dateTime().weekNumbersVisible(true).build();
		assertTrue(((DateTimePicker) input.getComponent()).isWeekNumbersVisible());
		input = Input.dateTime().weekNumbersVisible(false).build();
		assertFalse(((DateTimePicker) input.getComponent()).isWeekNumbersVisible());

		input = Input.dateTime().localization().today("_today").set().build();
		assertEquals("_today", ((DateTimePicker) input.getComponent()).getDatePickerI18n().getToday());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input3 = Input.dateTime().localization().today("test", "test.code").set().build();
			assertEquals("TestUS", ((DateTimePicker) input3.getComponent()).getDatePickerI18n().getToday());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input3 = Input.dateTime().deferLocalization().localization().today("test", "test.code").set()
					.build();
			assertNull(((DateTimePicker) input3.getComponent()).getDatePickerI18n());
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", ((DateTimePicker) input3.getComponent()).getDatePickerI18n().getToday());
		});
	}

	@Test
	public void testHasValue() {

		final Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(2018, 9, 22);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final Date date = calendar.getTime();

		Input<Date> input = Input.dateTime().build();
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

	@Test
	public void testAdapters() {

		Input<Date> input = Input.dateTime().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

		assertFalse(input.as(Collection.class).isPresent());

		input = Input.dateTime().validatable().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

	}

}
