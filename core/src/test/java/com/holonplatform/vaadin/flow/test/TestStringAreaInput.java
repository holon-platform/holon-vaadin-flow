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
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;

public class TestStringAreaInput {

	@Test
	public void testBuilders() {

		StringAreaInputBuilder builder = StringAreaInputBuilder.create();
		assertNotNull(builder);
		Input<String> input = builder.build();
		assertNotNull(input);

		builder = Input.stringArea();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.stringArea();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<String> input = Input.stringArea().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.stringArea().build();
		assertTrue(input.isVisible());

		input = Input.stringArea().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.stringArea().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.stringArea().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.stringArea().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.stringArea().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<String> input = Input.stringArea().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.stringArea().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<String> input = Input.stringArea().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.stringArea().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.stringArea().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.stringArea().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.stringArea().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.stringArea().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.stringArea().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.stringArea().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.stringArea().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.stringArea().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.stringArea().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.stringArea().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.stringArea().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<String> input = Input.stringArea().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.stringArea().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.stringArea().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.stringArea().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLabel() {

		Input<String> input = Input.stringArea().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().caption(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().caption("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().caption("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.stringArea().caption("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.stringArea()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.stringArea().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.stringArea().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<String> input = Input.stringArea().build();
		assertFalse(input.isReadOnly());

		input = Input.stringArea().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.stringArea().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.stringArea().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<String> input = Input.stringArea().build();
		assertFalse(input.isRequired());

		input = Input.stringArea().required(true).build();
		assertTrue(input.isRequired());

		input = Input.stringArea().required(false).build();
		assertFalse(input.isRequired());

		input = Input.stringArea().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		Input<String> input = Input.stringArea().withValue("test").build();
		assertEquals("test", input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.stringArea().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue("test");
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<String> input = Input.stringArea().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof TextArea);

		assertEquals(77, ((TextArea) input.getComponent()).getTabIndex());

		input = Input.stringArea().autofocus(false).build();
		assertFalse(((TextArea) input.getComponent()).isAutofocus());

		input = Input.stringArea().autofocus(true).build();
		assertTrue(((TextArea) input.getComponent()).isAutofocus());

	}

	@Test
	public void testPlaceholder() {

		Input<String> input = Input.stringArea().placeholder(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.stringArea().placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.stringArea().placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.stringArea().placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.stringArea()
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.stringArea().placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<String> input2 = Input.stringArea().deferLocalization().placeholder("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testPrefixAndSuffix() {

		final Icon prefix = VaadinIcon.PICTURE.create();
		final Button suffix = new Button("suffix");

		Input<String> input = Input.stringArea().prefixComponent(prefix).suffixComponent(suffix).build();
		assertTrue(input.getComponent() instanceof HasPrefixAndSuffix);
		assertEquals(prefix, ((HasPrefixAndSuffix) input.getComponent()).getPrefixComponent());
		assertEquals(suffix, ((HasPrefixAndSuffix) input.getComponent()).getSuffixComponent());

	}

	@Test
	public void testTextInput() {

		Input<String> input = Input.stringArea().valueChangeMode(ValueChangeMode.ON_BLUR).build();
		assertTrue(input.getComponent() instanceof HasValueChangeMode);
		assertEquals(ValueChangeMode.ON_BLUR, ((HasValueChangeMode) input.getComponent()).getValueChangeMode());

		input = Input.stringArea().autocomplete(Autocomplete.USERNAME).build();
		assertTrue(input.getComponent() instanceof HasAutocomplete);
		assertEquals(Autocomplete.USERNAME, ((HasAutocomplete) input.getComponent()).getAutocomplete());

		input = Input.stringArea().autocapitalize(Autocapitalize.WORDS).build();
		assertTrue(input.getComponent() instanceof HasAutocapitalize);
		assertEquals(Autocapitalize.WORDS, ((HasAutocapitalize) input.getComponent()).getAutocapitalize());

		input = Input.stringArea().autocorrect(true).build();
		assertTrue(input.getComponent() instanceof HasAutocorrect);
		assertTrue(((HasAutocorrect) input.getComponent()).isAutocorrect());

		input = Input.stringArea().minLength(7).build();
		assertTrue(input.getComponent() instanceof TextArea);
		assertEquals(7, ((TextArea) input.getComponent()).getMinLength());

		input = Input.stringArea().maxLength(77).build();
		assertTrue(input.getComponent() instanceof TextArea);
		assertEquals(77, ((TextArea) input.getComponent()).getMaxLength());

	}

	@Test
	public void testTextInputValues() {

		Input<String> input = Input.stringArea().emptyValuesAsNull(false).blankValuesAsNull(false).build();
		assertEquals("", input.getValue());
		input.setValue(null);
		assertEquals("", input.getValue());

		input = Input.stringArea().emptyValuesAsNull(true).blankValuesAsNull(false).build();
		assertNull(input.getValue());
		input.setValue(null);
		assertNull(input.getValue());
		input.setValue("");
		assertNull(input.getValue());
		input.setValue(" ");
		assertNotNull(input.getValue());

		input = Input.stringArea().blankValuesAsNull(true).build();
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

		Input<String> input = Input.stringArea().build();
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
