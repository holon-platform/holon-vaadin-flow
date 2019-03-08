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
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.HasAutocapitalize;
import com.vaadin.flow.component.textfield.HasAutocomplete;
import com.vaadin.flow.component.textfield.HasAutocorrect;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ElementConstants;

public class TestStringInput {

	@Test
	public void testBuilders() {

		StringInputBuilder builder = StringInputBuilder.create();
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.string();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.string();
		assertNotNull(builder);
		input = builder.build();
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
	public void testMinMaxSize() {
		Input<String> input = Input.string().minWidth("10em").build();
		assertEquals("10em", ComponentTestUtils.getStyleAttribute(input, ElementConstants.STYLE_MIN_WIDTH));

		input = Input.string().maxWidth("10em").build();
		assertEquals("10em", ComponentTestUtils.getStyleAttribute(input, ElementConstants.STYLE_MAX_WIDTH));

		input = Input.string().minHeight("10em").build();
		assertEquals("10em", ComponentTestUtils.getStyleAttribute(input, ElementConstants.STYLE_MIN_HEIGHT));

		input = Input.string().maxHeight("10em").build();
		assertEquals("10em", ComponentTestUtils.getStyleAttribute(input, ElementConstants.STYLE_MAX_HEIGHT));
	}

	@Test
	public void testElementConfigurator() {
		Input<String> input = Input.string().elementConfiguration(element -> {
			element.getStyle().set(ElementConstants.STYLE_COLOR, "#fff");
		}).build();
		assertEquals("#fff", ComponentTestUtils.getStyleAttribute(input, ElementConstants.STYLE_COLOR));
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

	@Test
	public void testLabel() {

		Input<String> input = Input.string().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.string().label("test", "test.code", "arg").build();
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
	public void testTitle() {

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

		input = Input.string().autoselect(true).build();
		assertTrue(((TextField) input.getComponent()).isAutoselect());

		input = Input.string().autoselect(false).build();
		assertFalse(((TextField) input.getComponent()).isAutoselect());

		input = Input.string().autoselect().build();
		assertTrue(((TextField) input.getComponent()).isAutoselect());

	}

	@Test
	public void testClearButton() {
		Input<String> input = Input.string().clearButtonVisible(true).build();
		assertTrue(input.getComponent() instanceof TextField);
		assertTrue(((TextField) input.getComponent()).isClearButtonVisible());

		input = Input.string().clearButtonVisible(false).build();
		assertFalse(((TextField) input.getComponent()).isClearButtonVisible());
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

	@Test
	public void testPattern() {

		Input<String> input = Input.string().pattern("[0-9]*").build();
		assertTrue(input.getComponent() instanceof TextField);
		assertEquals("[0-9]*", ((TextField) input.getComponent()).getPattern());

		input = Input.string().pattern("[0-9]*").preventInvalidInput(true).build();
		assertTrue(input.getComponent() instanceof TextField);
		assertEquals("[0-9]*", ((TextField) input.getComponent()).getPattern());
		assertTrue(((TextField) input.getComponent()).isPreventInvalidInput());

		input = Input.string().pattern("[0-9]*").preventInvalidInput().build();
		assertTrue(input.getComponent() instanceof TextField);
		assertTrue(((TextField) input.getComponent()).isPreventInvalidInput());

	}

	@Test
	public void testPrefixAndSuffix() {

		final Icon prefix = VaadinIcon.PICTURE.create();
		final Button suffix = new Button("suffix");

		Input<String> input = Input.string().prefixComponent(prefix).suffixComponent(suffix).build();
		assertTrue(input.getComponent() instanceof HasPrefixAndSuffix);
		assertEquals(prefix, ((HasPrefixAndSuffix) input.getComponent()).getPrefixComponent());
		assertEquals(suffix, ((HasPrefixAndSuffix) input.getComponent()).getSuffixComponent());

	}

	@Test
	public void testTextInput() {

		Input<String> input = Input.string().valueChangeMode(ValueChangeMode.ON_BLUR).build();
		assertTrue(input.getComponent() instanceof HasValueChangeMode);
		assertEquals(ValueChangeMode.ON_BLUR, ((HasValueChangeMode) input.getComponent()).getValueChangeMode());

		input = Input.string().autocomplete(Autocomplete.USERNAME).build();
		assertTrue(input.getComponent() instanceof HasAutocomplete);
		assertEquals(Autocomplete.USERNAME, ((HasAutocomplete) input.getComponent()).getAutocomplete());

		input = Input.string().autocapitalize(Autocapitalize.WORDS).build();
		assertTrue(input.getComponent() instanceof HasAutocapitalize);
		assertEquals(Autocapitalize.WORDS, ((HasAutocapitalize) input.getComponent()).getAutocapitalize());

		input = Input.string().autocorrect(true).build();
		assertTrue(input.getComponent() instanceof HasAutocorrect);
		assertTrue(((HasAutocorrect) input.getComponent()).isAutocorrect());

		input = Input.string().minLength(7).build();
		assertTrue(input.getComponent() instanceof TextField);
		assertEquals(7, ((TextField) input.getComponent()).getMinLength());

		input = Input.string().maxLength(77).build();
		assertTrue(input.getComponent() instanceof TextField);
		assertEquals(77, ((TextField) input.getComponent()).getMaxLength());

	}

	@Test
	public void testTextInputValues() {

		Input<String> input = Input.string().emptyValuesAsNull(false).blankValuesAsNull(false).build();
		assertEquals("", input.getValue());
		input.setValue(null);
		assertEquals("", input.getValue());

		input = Input.string().emptyValuesAsNull(true).blankValuesAsNull(false).build();
		assertNull(input.getValue());
		input.setValue(null);
		assertNull(input.getValue());
		input.setValue("");
		assertNull(input.getValue());
		input.setValue(" ");
		assertNotNull(input.getValue());

		input = Input.string().blankValuesAsNull(true).build();
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

		Input<String> input = Input.string().build();
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

}
