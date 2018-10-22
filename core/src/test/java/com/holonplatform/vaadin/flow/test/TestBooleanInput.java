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
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.checkbox.Checkbox;

public class TestBooleanInput {

	@Test
	public void testBuilders() {

		BooleanInputBuilder builder = BooleanInputBuilder.create();
		assertNotNull(builder);
		Input<Boolean> input = builder.build();
		assertNotNull(input);

		builder = Input.boolean_();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.boolean_();
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<Boolean> input = Input.boolean_().id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.boolean_().build();
		assertTrue(input.isVisible());

		input = Input.boolean_().visible(true).build();
		assertTrue(input.isVisible());

		input = Input.boolean_().visible(false).build();
		assertFalse(input.isVisible());

		input = Input.boolean_().hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.boolean_().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.boolean_().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<Boolean> input = Input.boolean_().styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.boolean_().styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<Boolean> input = Input.boolean_().width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.boolean_().width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.boolean_().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.boolean_().height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.boolean_().height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.boolean_().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.boolean_().width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.boolean_().widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.boolean_().heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.boolean_().sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.boolean_().fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.boolean_().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.boolean_().fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<Boolean> input = Input.boolean_().build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.boolean_().enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.boolean_().enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.boolean_().disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLabel() {

		Input<Boolean> input = Input.boolean_().label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().caption(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().caption("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().caption("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.boolean_().caption("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Boolean> input2 = Input.boolean_()
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Boolean> input2 = Input.boolean_().label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Boolean> input2 = Input.boolean_().deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<Boolean> input = Input.boolean_().build();
		assertFalse(input.isReadOnly());

		input = Input.boolean_().readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.boolean_().readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.boolean_().readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<Boolean> input = Input.boolean_().build();
		assertFalse(input.isRequired());

		input = Input.boolean_().required(true).build();
		assertTrue(input.isRequired());

		input = Input.boolean_().required(false).build();
		assertFalse(input.isRequired());

		input = Input.boolean_().required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		Input<Boolean> input = Input.boolean_().withValue(Boolean.TRUE).build();
		assertEquals(Boolean.TRUE, input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.boolean_().withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(Boolean.TRUE);
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<Boolean> input = Input.boolean_().tabIndex(77).build();
		assertTrue(input.getComponent() instanceof Checkbox);

		assertEquals(77, ((Checkbox) input.getComponent()).getTabIndex());

		input = Input.boolean_().autofocus(false).build();
		assertFalse(((Checkbox) input.getComponent()).isAutofocus());

		input = Input.boolean_().autofocus(true).build();
		assertTrue(((Checkbox) input.getComponent()).isAutofocus());

	}

	@Test
	public void testHasValue() {

		Input<Boolean> input = Input.boolean_().build();
		assertEquals(Boolean.FALSE, input.getEmptyValue());

		assertFalse(input.getValue());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertEquals(Boolean.FALSE, input.getValue());

		input.setValue(Boolean.TRUE);
		assertEquals(Boolean.TRUE, input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals(Boolean.TRUE, input.getValueIfPresent().orElse(null));

		input.clear();
		assertEquals(Boolean.FALSE, input.getValue());
		assertTrue(input.isEmpty());

	}

}
