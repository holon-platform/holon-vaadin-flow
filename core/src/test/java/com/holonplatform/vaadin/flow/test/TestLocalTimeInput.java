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

import java.time.LocalTime;
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
import com.holonplatform.vaadin.flow.components.builders.LocalTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.holonplatform.vaadin.flow.test.util.TestAdapter;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.internal.CurrentInstance;

public class TestLocalTimeInput {

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

		LocalTimeInputBuilder builder = LocalTimeInputBuilder.create();
		assertNotNull(builder);
		Input<LocalTime> input = builder.build();
		assertNotNull(input);

		builder = Input.localTime();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.localTime();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<LocalTime> input = Input.localTime().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.localTime().build();
		assertTrue(input.isVisible());

		input = Input.localTime().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.localTime().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.localTime().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.localTime().withAttachListener(e -> {
			attached.set(true);
		}).build();

		UI.getCurrent().add(input.getComponent());

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.localTime().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<LocalTime> input = Input.localTime().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.localTime().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<LocalTime> input = Input.localTime().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.localTime().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.localTime().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.localTime().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.localTime().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.localTime().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.localTime().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.localTime().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.localTime().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.localTime().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.localTime().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.localTime().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.localTime().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<LocalTime> input = Input.localTime().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.localTime().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.localTime().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.localTime().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<LocalTime> input = Input.localTime().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localTime().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localTime().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.localTime().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testTitle() {

		Input<LocalTime> input = Input.localTime().title(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().title("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().title("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().title("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().description(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().description("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().description("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.localTime().description("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime()
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().title("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().deferLocalization().title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().withDeferredLocalization(true).label("test", "test.code")
					.title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<LocalTime> input = Input.localTime().build();
		assertFalse(input.isReadOnly());

		input = Input.localTime().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.localTime().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.localTime().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<LocalTime> input = Input.localTime().build();
		assertFalse(input.isRequired());

		input = Input.localTime().required(true).build();
		assertTrue(input.isRequired());

		input = Input.localTime().required(false).build();
		assertFalse(input.isRequired());

		input = Input.localTime().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		Input<LocalTime> input = Input.localTime().withValue(LocalTime.of(15, 30)).build();
		assertEquals(LocalTime.of(15, 30), input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.localTime().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(LocalTime.of(23, 59));
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<LocalTime> input = Input.localTime().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof TimePicker);

		assertEquals(77, ((TimePicker) input.getComponent()).getTabIndex());

	}

	@Test
	public void testPlaceholder() {

		Input<LocalTime> input = Input.localTime().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localTime().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localTime().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.localTime().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<LocalTime> input2 = Input.localTime().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testHasValue() {

		Input<LocalTime> input = Input.localTime().build();
		assertNull(input.getEmptyValue());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue(LocalTime.of(22, 45));
		assertEquals(LocalTime.of(22, 45), input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals(LocalTime.of(22, 45), input.getValueIfPresent().orElse(null));

		input.clear();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

	}

	@Test
	public void testAdapters() {

		Input<LocalTime> input = Input.localTime().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789))
				.build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

		assertFalse(input.as(Collection.class).isPresent());

		input = Input.localTime().validatable().withAdapter(TestAdapter.class, i -> TestAdapter.create(i, 789)).build();

		assertTrue(input.as(TestAdapter.class).isPresent());
		assertEquals(Integer.valueOf(789), input.as(TestAdapter.class).map(a -> a.getId()).orElse(0));

	}

}
