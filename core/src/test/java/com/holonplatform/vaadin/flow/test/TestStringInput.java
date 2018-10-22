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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringAreaBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.textfield.TextField;

public class TestStringInput {

	@Test
	public void testBuilders() {

		StringInputBuilder builder1 = StringInputBuilder.create();
		assertNotNull(builder1);
		Input<String> input = builder1.build();
		assertNotNull(input);

		builder1 = Input.string();
		assertNotNull(builder1);
		input = builder1.build();
		assertNotNull(input);

		builder1 = Components.input.string();
		assertNotNull(builder1);
		input = builder1.build();
		assertNotNull(input);

		StringAreaBuilder builder2 = StringAreaBuilder.create();
		assertNotNull(builder2);
		input = builder2.build();
		assertNotNull(input);

		builder2 = Input.stringArea();
		assertNotNull(builder2);
		input = builder2.build();
		assertNotNull(input);

		builder2 = Components.input.stringArea();
		assertNotNull(builder2);
		input = builder2.build();
		assertNotNull(input);

		PasswordInputBuilder builder3 = PasswordInputBuilder.create();
		assertNotNull(builder3);
		input = builder3.build();
		assertNotNull(input);

		builder3 = Input.password();
		assertNotNull(builder3);
		input = builder3.build();
		assertNotNull(input);

		builder3 = Components.input.password();
		assertNotNull(builder3);
		input = builder3.build();
		assertNotNull(input);

		builder3 = Components.input.secretString();
		assertNotNull(builder3);
		input = builder3.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<String> input = Input.string().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.string().build();
		assertTrue(input.isVisible());

		input = Input.string().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.string().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.string().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.string().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.string().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<String> input = Input.string().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.string().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<String> input = Input.string().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.string().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.string().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.string().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.string().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.string().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.string().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.string().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.string().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.string().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.string().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.string().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.string().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<String> input = Input.string().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.string().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.string().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.string().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testText() {

		Input<String> input = Input.string().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().caption(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().caption("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().caption("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().caption("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testDescription() {

		Input<String> input = Input.string().title(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().title("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().title("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().title("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().description(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().description("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().description("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.string().description("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string()
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().title("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().deferLocalization().title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().withDeferredLocalization(true).label("test", "test.code")
					.title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<String> input = Input.string().build();
		assertFalse(input.isReadOnly());

		input = Input.string().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.string().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.string().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<String> input = Input.string().build();
		assertFalse(input.isRequired());

		input = Input.string().required(true).build();
		assertTrue(input.isRequired());

		input = Input.string().required(false).build();
		assertFalse(input.isRequired());

		input = Input.string().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		Input<String> input = Input.string().withValue("test").build();
		assertEquals("test", input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.string().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue("test");
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<String> input = Input.string().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof TextField);

		assertEquals(77, ((TextField) input.getComponent()).getTabIndex());

		input = Input.string().autofocus(false).build();
		assertFalse(((TextField) input.getComponent()).isAutofocus());

		input = Input.string().autofocus(true).build();
		assertTrue(((TextField) input.getComponent()).isAutofocus());

	}

	@Test
	public void testPlaceholder() {

		Input<String> input = Input.string().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.string().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.string().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.string().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.string().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

}
