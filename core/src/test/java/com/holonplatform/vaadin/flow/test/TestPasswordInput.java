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
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.HasAutocapitalize;
import com.vaadin.flow.component.textfield.HasAutocomplete;
import com.vaadin.flow.component.textfield.HasAutocorrect;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.internal.CurrentInstance;

public class TestPasswordInput {

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

		PasswordInputBuilder builder = PasswordInputBuilder.create();
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.password();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.password();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.secretString();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<String> input = Input.password().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.password().build();
		assertTrue(input.isVisible());

		input = Input.password().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.password().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.password().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.password().withAttachListener(e -> {
			attached.set(true);
		}).build();

		UI.getCurrent().add(input.getComponent());
		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.password().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<String> input = Input.password().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.password().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<String> input = Input.password().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.password().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.password().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.password().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.password().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.password().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.password().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.password().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.password().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.password().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.password().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.password().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.password().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<String> input = Input.password().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.password().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.password().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.password().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@Test
	public void testLabel() {

		Input<String> input = Input.password().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.password().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.password().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.password().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testTitle() {

		Input<String> input = Input.password().title(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().title("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().title("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().title("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().description(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().description("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().description("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.password().description("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password()
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().title("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().deferLocalization().title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().withDeferredLocalization(true).label("test", "test.code")
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

		Input<String> input = Input.password().build();
		assertFalse(input.isReadOnly());

		input = Input.password().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.password().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.password().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<String> input = Input.password().build();
		assertFalse(input.isRequired());

		input = Input.password().required(true).build();
		assertTrue(input.isRequired());

		input = Input.password().required(false).build();
		assertFalse(input.isRequired());

		input = Input.password().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		Input<String> input = Input.password().withValue("test").build();
		assertEquals("test", input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.password().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue("test");
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<String> input = Input.password().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof PasswordField);

		assertEquals(77, ((PasswordField) input.getComponent()).getTabIndex());

		input = Input.password().autofocus(false).build();
		assertFalse(((PasswordField) input.getComponent()).isAutofocus());

		input = Input.password().autofocus(true).build();
		assertTrue(((PasswordField) input.getComponent()).isAutofocus());

		input = Input.password().autoselect(true).build();
		assertTrue(((PasswordField) input.getComponent()).isAutoselect());

		input = Input.password().autoselect(false).build();
		assertFalse(((PasswordField) input.getComponent()).isAutoselect());

		input = Input.password().autoselect().build();
		assertTrue(((PasswordField) input.getComponent()).isAutoselect());

	}

	@Test
	public void testClearButton() {
		Input<String> input = Input.password().clearButtonVisible(true).build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertTrue(((PasswordField) input.getComponent()).isClearButtonVisible());

		input = Input.password().clearButtonVisible(false).build();
		assertFalse(((PasswordField) input.getComponent()).isClearButtonVisible());
	}

	@Test
	public void testPlaceholder() {

		Input<String> input = Input.password().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.password().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.password().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.password().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.password().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			UI.getCurrent().add(input2.getComponent());
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testPattern() {

		Input<String> input = Input.password().pattern("[0-9]*").build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertEquals("[0-9]*", ((PasswordField) input.getComponent()).getPattern());

		input = Input.password().pattern("[0-9]*").preventInvalidInput(true).build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertEquals("[0-9]*", ((PasswordField) input.getComponent()).getPattern());
		assertTrue(((PasswordField) input.getComponent()).isPreventInvalidInput());

		input = Input.password().pattern("[0-9]*").preventInvalidInput().build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertTrue(((PasswordField) input.getComponent()).isPreventInvalidInput());

	}

	@Test
	public void testPrefixAndSuffix() {

		final Icon prefix = VaadinIcon.PICTURE.create();
		final Button suffix = new Button("suffix");

		Input<String> input = Input.password().prefixComponent(prefix).suffixComponent(suffix).build();
		assertTrue(input.getComponent() instanceof HasPrefixAndSuffix);
		assertEquals(prefix, ((HasPrefixAndSuffix) input.getComponent()).getPrefixComponent());
		assertEquals(suffix, ((HasPrefixAndSuffix) input.getComponent()).getSuffixComponent());

	}

	@Test
	public void testTextInput() {

		Input<String> input = Input.password().valueChangeMode(ValueChangeMode.ON_BLUR).build();
		assertTrue(input.getComponent() instanceof HasValueChangeMode);
		assertEquals(ValueChangeMode.ON_BLUR, ((HasValueChangeMode) input.getComponent()).getValueChangeMode());

		input = Input.password().autocomplete(Autocomplete.USERNAME).build();
		assertTrue(input.getComponent() instanceof HasAutocomplete);
		assertEquals(Autocomplete.USERNAME, ((HasAutocomplete) input.getComponent()).getAutocomplete());

		input = Input.password().autocapitalize(Autocapitalize.WORDS).build();
		assertTrue(input.getComponent() instanceof HasAutocapitalize);
		assertEquals(Autocapitalize.WORDS, ((HasAutocapitalize) input.getComponent()).getAutocapitalize());

		input = Input.password().autocorrect(true).build();
		assertTrue(input.getComponent() instanceof HasAutocorrect);
		assertTrue(((HasAutocorrect) input.getComponent()).isAutocorrect());

		input = Input.password().minLength(7).build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertEquals(7, ((PasswordField) input.getComponent()).getMinLength());

		input = Input.password().maxLength(77).build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertEquals(77, ((PasswordField) input.getComponent()).getMaxLength());

	}

	@Test
	public void testTextInputValues() {

		Input<String> input = Input.password().emptyValuesAsNull(false).blankValuesAsNull(false).build();
		assertEquals("", input.getValue());
		input.setValue(null);
		assertEquals("", input.getValue());

		input = Input.password().emptyValuesAsNull(true).blankValuesAsNull(false).build();
		assertNull(input.getValue());
		input.setValue(null);
		assertNull(input.getValue());
		input.setValue("");
		assertNull(input.getValue());
		input.setValue(" ");
		assertNotNull(input.getValue());

		input = Input.password().blankValuesAsNull(true).build();
		assertNull(input.getValue());
		input.setValue(null);
		assertNull(input.getValue());
		input.setValue("");
		assertNull(input.getValue());
		input.setValue(" ");
		assertNull(input.getValue());

	}

	@Test
	public void testHasValue() {

		Input<String> input = Input.password().build();
		assertEquals("", input.getEmptyValue());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue("test");
		assertEquals("test", input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals("test", input.getValueIfPresent().orElse(null));

		input.clear();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

	}

	@Test
	public void testRevealButton() {

		Input<String> input = Input.password().revealButtonVisible(true).build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertTrue(((PasswordField) input.getComponent()).isRevealButtonVisible());

		input = Input.password().revealButtonVisible(false).build();
		assertTrue(input.getComponent() instanceof PasswordField);
		assertFalse(((PasswordField) input.getComponent()).isRevealButtonVisible());

	}

}
