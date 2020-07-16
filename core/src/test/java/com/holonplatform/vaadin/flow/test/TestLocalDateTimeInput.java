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

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.LocalDateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.test.util.TestAdapter;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.internal.CurrentInstance;

public class TestLocalDateTimeInput {

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

		LocalDateTimeInputBuilder builder = LocalDateTimeInputBuilder.create();
		assertNotNull(builder);
		Input<LocalDateTime> input = builder.build();
		assertNotNull(input);

		builder = Input.localDateTime();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.localDateTime();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<LocalDateTime> input = Input.localDateTime().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.localDateTime().build();
		assertTrue(input.isVisible());

		input = Input.localDateTime().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.localDateTime().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.localDateTime().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.localDateTime().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.localDateTime().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<LocalDateTime> input = Input.localDateTime().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.localDateTime().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<LocalDateTime> input = Input.localDateTime().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.localDateTime().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.localDateTime().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.localDateTime().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.localDateTime().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.localDateTime().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.localDateTime().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<LocalDateTime> input = Input.localDateTime().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.localDateTime().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.localDateTime().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.localDateTime().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<LocalDateTime> input = Input.localDateTime().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localDateTime().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localDateTime().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localDateTime().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input2 = Input.localDateTime()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input2 = Input.localDateTime().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input2 = Input.localDateTime().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<LocalDateTime> input = Input.localDateTime().build();
		assertFalse(input.isReadOnly());

		input = Input.localDateTime().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.localDateTime().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.localDateTime().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<LocalDateTime> input = Input.localDateTime().build();
		assertFalse(input.isRequired());

		input = Input.localDateTime().required(true).build();
		assertTrue(input.isRequired());

		input = Input.localDateTime().required(false).build();
		assertFalse(input.isRequired());

		input = Input.localDateTime().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		final LocalDateTime ld = LocalDateTime.of(2018, Month.OCTOBER, 22, 15, 30);

		Input<LocalDateTime> input = Input.localDateTime().withValue(ld).build();
		assertEquals(ld, input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.localDateTime().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(ld);
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<LocalDateTime> input = Input.localDateTime().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof DateTimePicker);
		assertEquals(77, ((DateTimePicker) input.getComponent()).getTabIndex());

	}

	@Test
	public void testPlaceholder() {

		Input<LocalDateTime> input = Input.localDateTime().placeholder(Localizable.builder().message("test").build())
				.build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localDateTime().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localDateTime().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localDateTime().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input2 = Input.localDateTime()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input2 = Input.localDateTime().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input2 = Input.localDateTime().deferLocalization().placeholder("test", "test.code")
					.build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testConfiguration() {

		Input<LocalDateTime> input = Input.localDateTime().locale(Locale.ITALIAN).build();
		assertTrue(input.getComponent() instanceof DateTimePicker);
		assertEquals(Locale.ITALIAN, ((DateTimePicker) input.getComponent()).getLocale());

		final Input<LocalDateTime> input2 = Input.localDateTime().updateLocaleOnAttach(true).build();

		assertEquals(Locale.US, ((DateTimePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.FRANCE);
		ComponentUtil.onComponentAttach(input2.getComponent(), true);
		assertEquals(Locale.FRANCE, ((DateTimePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.US);

		input = Input.localDateTime().min(LocalDateTime.of(2018, Month.OCTOBER, 22, 0, 0))
				.max(LocalDateTime.of(2018, Month.OCTOBER, 23, 0, 0)).build();
		assertEquals(LocalDateTime.of(2018, Month.OCTOBER, 22, 0, 0), ((DateTimePicker) input.getComponent()).getMin());
		assertEquals(LocalDateTime.of(2018, Month.OCTOBER, 23, 0, 0), ((DateTimePicker) input.getComponent()).getMax());

		input = Input.localDateTime().weekNumbersVisible(true).build();
		assertTrue(((DateTimePicker) input.getComponent()).isWeekNumbersVisible());
		input = Input.localDateTime().weekNumbersVisible(false).build();
		assertFalse(((DateTimePicker) input.getComponent()).isWeekNumbersVisible());

		input = Input.localDateTime().localization().today("_today").set().build();
		assertEquals("_today", ((DateTimePicker) input.getComponent()).getDatePickerI18n().getToday());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input3 = Input.localDateTime().localization().today("test", "test.code").set().build();
			assertEquals("TestUS", ((DateTimePicker) input3.getComponent()).getDatePickerI18n().getToday());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDateTime> input3 = Input.localDateTime().deferLocalization().localization()
					.today("test", "test.code").set().build();
			assertNull(((DateTimePicker) input3.getComponent()).getDatePickerI18n());
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", ((DateTimePicker) input3.getComponent()).getDatePickerI18n().getToday());
		});
	}

	@Test
	public void testHasValue() {

		final LocalDateTime ld = LocalDateTime.of(2018, Month.OCTOBER, 22, 15, 30);

		Input<LocalDateTime> input = Input.localDateTime().build();
		assertNull(input.getEmptyValue());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue(ld);
		assertEquals(ld, input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals(ld, input.getValueIfPresent().orElse(null));

		input.clear();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

	}

	@Test
	public void testAdapters() {

		Input<LocalDateTime> input = Input.localDateTime()
				.withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

		assertFalse(input.as(Collection.class).isPresent());

		input = Input.localDateTime().validatable().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789))
				.build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

	}

}
