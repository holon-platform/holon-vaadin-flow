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
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.internal.components.DateTimeField;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.CurrentInstance;

public class TestDateTimeInput {

	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");
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

	private static LocalDate asLocalDate(Date date) {
		if (date != null) {
			return Instant.ofEpochMilli(date.getTime()).atZone(ZONE_ID).toLocalDate();
		}
		return null;
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

	@SuppressWarnings("deprecation")
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

		input = Input.dateTime().caption(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.dateTime().caption("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.dateTime().caption("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.dateTime().caption("test", "test.code", "arg").build();
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
		assertTrue(input.getComponent() instanceof DateTimeField);
		assertEquals(77, ((DateTimeField) input.getComponent()).getTabIndex());

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
	public void testHoursPlaceholder() {

		Input<Date> input = Input.dateTime().hoursPlaceholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getHoursPlaceholder());

		input = Input.dateTime().hoursPlaceholder("test").build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getHoursPlaceholder());

		input = Input.dateTime().hoursPlaceholder("test", "test.code").build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getHoursPlaceholder());

		input = Input.dateTime().hoursPlaceholder("test", "test.code", "arg").build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getHoursPlaceholder());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime()
					.hoursPlaceholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ((DateTimeField) input2.getComponent()).getHoursPlaceholder());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().hoursPlaceholder("test", "test.code").build();
			assertEquals("TestUS", ((DateTimeField) input2.getComponent()).getHoursPlaceholder());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().deferLocalization().hoursPlaceholder("test", "test.code").build();
			assertEquals("test", ((DateTimeField) input2.getComponent()).getHoursPlaceholder());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ((DateTimeField) input2.getComponent()).getHoursPlaceholder());
		});

	}

	@Test
	public void testMinutesPlaceholder() {

		Input<Date> input = Input.dateTime().minutesPlaceholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getMinutesPlaceholder());

		input = Input.dateTime().minutesPlaceholder("test").build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getMinutesPlaceholder());

		input = Input.dateTime().minutesPlaceholder("test", "test.code").build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getMinutesPlaceholder());

		input = Input.dateTime().minutesPlaceholder("test", "test.code", "arg").build();
		assertEquals("test", ((DateTimeField) input.getComponent()).getMinutesPlaceholder());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime()
					.minutesPlaceholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ((DateTimeField) input2.getComponent()).getMinutesPlaceholder());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().minutesPlaceholder("test", "test.code").build();
			assertEquals("TestUS", ((DateTimeField) input2.getComponent()).getMinutesPlaceholder());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input2 = Input.dateTime().deferLocalization().minutesPlaceholder("test", "test.code").build();
			assertEquals("test", ((DateTimeField) input2.getComponent()).getMinutesPlaceholder());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ((DateTimeField) input2.getComponent()).getMinutesPlaceholder());
		});

	}

	@Test
	public void testConfiguration() {

		final Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(2018, 9, 22, 11, 20);
		final Date date1 = calendar.getTime();
		final Calendar calendar2 = Calendar.getInstance(TIME_ZONE);
		calendar2.set(2018, 9, 23, 16, 30);
		final Date date2 = calendar.getTime();

		Input<Date> input = Input.dateTime().locale(Locale.ITALIAN).build();
		assertTrue(input.getComponent() instanceof DateTimeField);
		assertEquals(Locale.ITALIAN, ((DateTimeField) input.getComponent()).getLocale());

		final Input<Date> input2 = Input.dateTime().useContextLocale(true).build();

		final LocalizationContext lc = LocalizationContext.builder().withInitialLocale(Locale.FRANCE).build();
		Context.get().executeThreadBound(LocalizationContext.CONTEXT_KEY, lc, () -> {
			assertEquals(Locale.US, ((DateTimeField) input2.getComponent()).getLocale());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals(Locale.FRANCE, ((DateTimeField) input2.getComponent()).getLocale());
		});

		input = Input.dateTime().min(date1).max(date2).build();
		assertEquals(asLocalDate(date1), ((DateTimeField) input.getComponent()).getMin());
		assertEquals(asLocalDate(date2), ((DateTimeField) input.getComponent()).getMax());

		input = Input.dateTime().initialPosition(date1).build();
		assertEquals(LocalDateTime.of(asLocalDate(date1), LocalTime.of(0, 0)),
				((DateTimeField) input.getComponent()).getInitialPosition());

		input = Input.dateTime().weekNumbersVisible(true).build();
		assertTrue(((DateTimeField) input.getComponent()).isWeekNumbersVisible());
		input = Input.dateTime().weekNumbersVisible(false).build();
		assertFalse(((DateTimeField) input.getComponent()).isWeekNumbersVisible());

		input = Input.dateTime().localization().today("_today").set().build();
		assertEquals("_today", ((DateTimeField) input.getComponent()).getI18n().getToday());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input3 = Input.dateTime().localization().today("test", "test.code").set().build();
			assertEquals("TestUS", ((DateTimeField) input3.getComponent()).getI18n().getToday());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Date> input3 = Input.dateTime().deferLocalization().localization().today("test", "test.code").set()
					.build();
			assertNull(((DateTimeField) input3.getComponent()).getI18n());
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", ((DateTimeField) input3.getComponent()).getI18n().getToday());
		});

		input = Input.dateTime().timeSeparator("-").build();
		assertEquals("-", ((DateTimeField) input.getComponent()).getTimeSeparator());

		input = Input.dateTime().spacing(true).build();
		assertTrue(((DateTimeField) input.getComponent()).isSpacing());
		input = Input.dateTime().spacing(false).build();
		assertFalse(((DateTimeField) input.getComponent()).isSpacing());

		input = Input.dateTime().timeInputsWidth("50px").build();
		assertEquals("50px", ((DateTimeField) input.getComponent()).getTimeInputsWidth());
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

}
