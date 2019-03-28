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
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.test.util.TestAdapter;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.internal.CurrentInstance;

public class TestLocalDateInput {

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

		LocalDateInputBuilder builder = LocalDateInputBuilder.create();
		assertNotNull(builder);
		Input<LocalDate> input = builder.build();
		assertNotNull(input);

		builder = Input.localDate();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.localDate();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<LocalDate> input = Input.localDate().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.localDate().build();
		assertTrue(input.isVisible());

		input = Input.localDate().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.localDate().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.localDate().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.localDate().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.localDate().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<LocalDate> input = Input.localDate().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.localDate().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<LocalDate> input = Input.localDate().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.localDate().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.localDate().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.localDate().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.localDate().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.localDate().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.localDate().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.localDate().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.localDate().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.localDate().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.localDate().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.localDate().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.localDate().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<LocalDate> input = Input.localDate().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.localDate().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.localDate().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.localDate().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<LocalDate> input = Input.localDate().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localDate().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localDate().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localDate().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input2 = Input.localDate()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input2 = Input.localDate().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input2 = Input.localDate().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<LocalDate> input = Input.localDate().build();
		assertFalse(input.isReadOnly());

		input = Input.localDate().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.localDate().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.localDate().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<LocalDate> input = Input.localDate().build();
		assertFalse(input.isRequired());

		input = Input.localDate().required(true).build();
		assertTrue(input.isRequired());

		input = Input.localDate().required(false).build();
		assertFalse(input.isRequired());

		input = Input.localDate().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		final LocalDate ld = LocalDate.of(2018, Month.OCTOBER, 22);

		Input<LocalDate> input = Input.localDate().withValue(ld).build();
		assertEquals(ld, input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.localDate().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(ld);
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<LocalDate> input = Input.localDate().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof DatePicker);
		assertEquals(77, ((DatePicker) input.getComponent()).getTabIndex());

	}

	@Test
	public void testPlaceholder() {

		Input<LocalDate> input = Input.localDate().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localDate().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localDate().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localDate().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input2 = Input.localDate()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input2 = Input.localDate().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input2 = Input.localDate().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testConfiguration() {

		Input<LocalDate> input = Input.localDate().locale(Locale.ITALIAN).build();
		assertTrue(input.getComponent() instanceof DatePicker);
		assertEquals(Locale.ITALIAN, ((DatePicker) input.getComponent()).getLocale());

		final Input<LocalDate> input2 = Input.localDate().updateLocaleOnAttach(true).build();

		assertEquals(Locale.US, ((DatePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.FRANCE);
		ComponentUtil.onComponentAttach(input2.getComponent(), true);
		assertEquals(Locale.FRANCE, ((DatePicker) input2.getComponent()).getLocale());
		ui.setLocale(Locale.US);

		input = Input.localDate().min(LocalDate.of(2018, Month.OCTOBER, 22)).max(LocalDate.of(2018, Month.OCTOBER, 23))
				.build();
		assertEquals(LocalDate.of(2018, Month.OCTOBER, 22), ((DatePicker) input.getComponent()).getMin());
		assertEquals(LocalDate.of(2018, Month.OCTOBER, 23), ((DatePicker) input.getComponent()).getMax());

		input = Input.localDate().initialPosition(LocalDate.of(2018, Month.OCTOBER, 22)).build();
		assertEquals(LocalDate.of(2018, Month.OCTOBER, 22), ((DatePicker) input.getComponent()).getInitialPosition());

		input = Input.localDate().weekNumbersVisible(true).build();
		assertTrue(((DatePicker) input.getComponent()).isWeekNumbersVisible());
		input = Input.localDate().weekNumbersVisible(false).build();
		assertFalse(((DatePicker) input.getComponent()).isWeekNumbersVisible());

		input = Input.localDate().localization().today("_today").set().build();
		assertEquals("_today", ((DatePicker) input.getComponent()).getI18n().getToday());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input3 = Input.localDate().localization().today("test", "test.code").set().build();
			assertEquals("TestUS", ((DatePicker) input3.getComponent()).getI18n().getToday());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalDate> input3 = Input.localDate().deferLocalization().localization().today("test", "test.code")
					.set().build();
			assertNull(((DatePicker) input3.getComponent()).getI18n());
			ComponentUtil.onComponentAttach(input3.getComponent(), true);
			assertEquals("TestUS", ((DatePicker) input3.getComponent()).getI18n().getToday());
		});

	}

	@Test
	public void testHasValue() {

		final LocalDate ld = LocalDate.of(2018, Month.OCTOBER, 22);

		Input<LocalDate> input = Input.localDate().build();
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

		Input<LocalDate> input = Input.localDate().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789))
				.build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

		assertFalse(input.as(Collection.class).isPresent());

		input = Input.localDate().validatable().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

	}

}
