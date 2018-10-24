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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputValueConversionException;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.HasAutocomplete;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;

public class TestNumberInput {

	@Test
	public void testBuilders() {

		NumberInputBuilder<Long> builder = NumberInputBuilder.create(Long.class);
		assertNotNull(builder);
		Input<Long> input = builder.build();
		assertNotNull(input);

		builder = Input.number(Long.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

		builder = Components.input.number(Long.class);
		assertNotNull(builder);
		input = builder.build();
		assertNotNull(input);

	}

	@Test
	public void testComponent() {

		Input<Integer> input = Input.number(Integer.class).id("testid").build();
		assertNotNull(input.getComponent());

		assertTrue(input.getComponent().getId().isPresent());
		assertEquals("testid", input.getComponent().getId().get());

		input = Input.number(Integer.class).build();
		assertTrue(input.isVisible());

		input = Input.number(Integer.class).visible(true).build();
		assertTrue(input.isVisible());

		input = Input.number(Integer.class).visible(false).build();
		assertFalse(input.isVisible());

		input = Input.number(Integer.class).hidden().build();
		assertFalse(input.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		input = Input.number(Integer.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(input.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		input = Input.number(Integer.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(input.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Input<Integer> input = Input.number(Integer.class).styleName("test").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test"));

		input = Input.number(Integer.class).styleNames("test1", "test2").build();
		assertNotNull(input);
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(input).contains("test2"));

	}

	@Test
	public void testSize() {

		Input<Integer> input = Input.number(Integer.class).width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.number(Integer.class).width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(input));

		input = Input.number(Integer.class).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(input));

		input = Input.number(Integer.class).height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));

		input = Input.number(Integer.class).heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(input));
		assertNull(ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));

		input = Input.number(Integer.class).fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(input));

		input = Input.number(Integer.class).fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(input));
		assertEquals("100%", ComponentTestUtils.getHeight(input));

	}

	@Test
	public void testEnabled() {

		Input<Integer> input = Input.number(Integer.class).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.number(Integer.class).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(input));

		input = Input.number(Integer.class).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(input));

		input = Input.number(Integer.class).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(input));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLabel() {

		Input<Integer> input = Input.number(Integer.class).label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).caption(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).caption("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).caption("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		input = Input.number(Integer.class).caption("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class)
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).deferLocalization().label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
		});

	}

	@Test
	public void testTitle() {

		Input<Integer> input = Input.number(Integer.class).title(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).title("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).title("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).title("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).description(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).description("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).description("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		input = Input.number(Integer.class).description("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class)
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).title("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).deferLocalization().title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).withDeferredLocalization(true)
					.label("test", "test.code").title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(input2));
			assertEquals("test", ComponentTestUtils.getTitle(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(input2));
			assertEquals("TestUS", ComponentTestUtils.getTitle(input2));
		});

	}

	@Test
	public void testReadOnly() {

		Input<Integer> input = Input.number(Integer.class).build();
		assertFalse(input.isReadOnly());

		input = Input.number(Integer.class).readOnly(true).build();
		assertTrue(input.isReadOnly());

		input = Input.number(Integer.class).readOnly(false).build();
		assertFalse(input.isReadOnly());

		input = Input.number(Integer.class).readOnly().build();
		assertTrue(input.isReadOnly());

	}

	@Test
	public void testRequired() {

		Input<Integer> input = Input.number(Integer.class).build();
		assertFalse(input.isRequired());

		input = Input.number(Integer.class).required(true).build();
		assertTrue(input.isRequired());

		input = Input.number(Integer.class).required(false).build();
		assertFalse(input.isRequired());

		input = Input.number(Integer.class).required().build();
		assertTrue(input.isRequired());

	}

	@Test
	public void testValueBuilder() {

		Input<Integer> input = Input.number(Integer.class).withValue(123).build();
		assertEquals(Integer.valueOf(123), input.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		input = Input.number(Integer.class).withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		input.setValue(456);
		assertEquals(1, fired.get());

	}

	@Test
	public void testFocus() {

		Input<Integer> input = Input.number(Integer.class).tabIndex(77).build();
		assertTrue(input.getComponent() instanceof TextField);

		assertEquals(77, ((TextField) input.getComponent()).getTabIndex());

		input = Input.number(Integer.class).autofocus(false).build();
		assertFalse(((TextField) input.getComponent()).isAutofocus());

		input = Input.number(Integer.class).autofocus(true).build();
		assertTrue(((TextField) input.getComponent()).isAutofocus());

	}

	@Test
	public void testPlaceholder() {

		Input<Integer> input = Input.number(Integer.class).placeholder(Localizable.builder().message("test").build())
				.build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.number(Integer.class).placeholder("test").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.number(Integer.class).placeholder("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		input = Input.number(Integer.class).placeholder("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getPlaceholder(input));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class)
					.placeholder(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).placeholder("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Integer> input2 = Input.number(Integer.class).deferLocalization().placeholder("test", "test.code")
					.build();
			assertEquals("test", ComponentTestUtils.getPlaceholder(input2));
			ComponentUtil.onComponentAttach(input2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getPlaceholder(input2));
		});

	}

	@Test
	public void testPrefixAndSuffix() {

		final Icon prefix = VaadinIcon.PICTURE.create();
		final Button suffix = new Button("suffix");

		Input<Integer> input = Input.number(Integer.class).prefixComponent(prefix).suffixComponent(suffix).build();
		assertTrue(input.getComponent() instanceof HasPrefixAndSuffix);
		assertEquals(prefix, ((HasPrefixAndSuffix) input.getComponent()).getPrefixComponent());
		assertEquals(suffix, ((HasPrefixAndSuffix) input.getComponent()).getSuffixComponent());

	}

	@Test
	public void testTextInput() {

		Input<Integer> input = Input.number(Integer.class).valueChangeMode(ValueChangeMode.ON_BLUR).build();
		assertTrue(input.getComponent() instanceof HasValueChangeMode);
		assertEquals(ValueChangeMode.ON_BLUR, ((HasValueChangeMode) input.getComponent()).getValueChangeMode());

		input = Input.number(Integer.class).autocomplete(Autocomplete.USERNAME).build();
		assertTrue(input.getComponent() instanceof HasAutocomplete);
		assertEquals(Autocomplete.USERNAME, ((HasAutocomplete) input.getComponent()).getAutocomplete());

	}

	@Test
	public void testHasValue() {

		Input<Integer> input = Input.number(Integer.class).build();
		assertNull(input.getEmptyValue());

		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

		input.setValue(null);
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());

		input.setValue(123);
		assertEquals(Integer.valueOf(123), input.getValue());
		assertTrue(input.getValueIfPresent().isPresent());
		assertEquals(Integer.valueOf(123), input.getValueIfPresent().orElse(null));

		input.clear();
		assertNull(input.getValue());
		assertFalse(input.getValueIfPresent().isPresent());
		assertTrue(input.isEmpty());

	}

	@Test
	public void testHasValueByType() {

		Input<Integer> input1 = Input.number(Integer.class).build();
		assertNull(input1.getEmptyValue());
		input1.setValue(123);
		assertEquals(Integer.valueOf(123), input1.getValue());
		input1.setValue(-123);
		assertEquals(Integer.valueOf(-123), input1.getValue());
		input1.setValue(0);
		assertEquals(Integer.valueOf(0), input1.getValue());

		Input<Long> input2 = Input.number(Long.class).build();
		assertNull(input2.getEmptyValue());
		input2.setValue(123L);
		assertEquals(Long.valueOf(123), input2.getValue());
		input2.setValue(-123L);
		assertEquals(Long.valueOf(-123), input2.getValue());

		final Short sv = (short) 64;
		Input<Short> input3 = Input.number(Short.class).build();
		assertNull(input3.getEmptyValue());
		input3.setValue(sv);
		assertEquals(sv, input3.getValue());

		final Byte bv = (byte) 8;
		Input<Byte> input4 = Input.number(Byte.class).build();
		assertNull(input4.getEmptyValue());
		input4.setValue(bv);
		assertEquals(bv, input4.getValue());

		Input<Float> input5 = Input.number(Float.class).build();
		assertNull(input5.getEmptyValue());
		input5.setValue(123f);
		assertEquals(Float.valueOf(123f), input5.getValue());
		input5.setValue(123.1234567f);
		assertEquals(Float.valueOf(123.1234567f), input5.getValue());

		Input<Double> input6 = Input.number(Double.class).build();
		assertNull(input6.getEmptyValue());
		input6.setValue(123d);
		assertEquals(Double.valueOf(123d), input6.getValue());
		input6.setValue(123.5678d);
		assertEquals(Double.valueOf(123.5678d), input6.getValue());
		input6.setValue(123.1234567812345678d);
		assertEquals(Double.valueOf(123.1234567812345678d), input6.getValue());
		input6.setValue(-123.1234567812345678d);
		assertEquals(Double.valueOf(-123.1234567812345678d), input6.getValue());

		Input<BigDecimal> input7 = Input.number(BigDecimal.class).build();
		assertNull(input7.getEmptyValue());
		input7.setValue(BigDecimal.valueOf(123));
		assertEquals(BigDecimal.valueOf(123), input7.getValue());
		input7.setValue(BigDecimal.valueOf(123.5678d));
		assertEquals(BigDecimal.valueOf(123.5678d), input7.getValue());
		input7.setValue(BigDecimal.valueOf(123.1234567812345678d));
		assertEquals(BigDecimal.valueOf(123.1234567812345678d), input7.getValue());
		input7.setValue(BigDecimal.valueOf(-123.1234567812345678d));
		assertEquals(BigDecimal.valueOf(-123.1234567812345678d), input7.getValue());

		Input<BigInteger> input8 = Input.number(BigInteger.class).build();
		assertNull(input8.getEmptyValue());
		input8.setValue(BigInteger.valueOf(1231234567812345678L));
		assertEquals(BigInteger.valueOf(1231234567812345678L), input8.getValue());

	}

	@Test
	public void testNegatives() {

		Input<Integer> input = Input.number(Integer.class).allowNegative(false).build();
		assertNull(input.getEmptyValue());
		input.setValue(123);
		assertEquals(Integer.valueOf(123), input.getValue());
		input.setValue(-123);

		Assertions.assertThrows(InputValueConversionException.class, () -> input.getValue());

	}

	@Test
	public void testMaxDecimals() {

		Input<Double> input = Input.number(Double.class).maxDecimals(2).build();
		assertNull(input.getEmptyValue());
		input.setValue(123.567d);
		assertEquals(Double.valueOf(123.57d), input.getValue());

	}

}
