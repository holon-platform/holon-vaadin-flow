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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.internal.CurrentInstance;

public class TestStringToNumberConverter {

	@Test
	public void testDoubleConverter() {

		final Double dbl = 12345.67d;

		StringToNumberConverter<Double> converter = StringToNumberConverter.create(Double.class);

		String text = converter.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12345.67", text);

		StringToNumberConverter<Double> converter1 = StringToNumberConverter.create(Double.class);
		converter1.setUseGrouping(true);

		text = converter1.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12,345.67", text);

		Result<Double> result = converter1.convertToModel(text,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(result);
		assertFalse(result.isError());
		assertEquals(dbl, result.getOrThrow(error -> new RuntimeException(error)));

		withUILocale(Locale.US, () -> {
			String text2 = converter1.convertToPresentation(dbl,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
			assertNotNull(text2);
			assertEquals("12,345.67", text2);

			assertTrue(converter1.getDecimalSymbol().isPresent());
			assertEquals(".", converter1.getDecimalSymbol().map(c -> String.valueOf(c)).orElse(null));
		});

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter1.convertToPresentation(dbl,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, (Locale) null));
			assertNotNull(text2);
			assertEquals("12.345,67", text2);

			assertTrue(converter1.getDecimalSymbol().isPresent());
			assertEquals(",", converter1.getDecimalSymbol().map(c -> String.valueOf(c)).orElse(null));
		});

		// fixed Locale
		StringToNumberConverter<Double> converter2 = StringToNumberConverter.create(Double.class, Locale.US);
		converter2.setUseGrouping(true);

		text = converter2.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12,345.67", text);
		text = converter2.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
		assertNotNull(text);
		assertEquals("12,345.67", text);
		text = converter2.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.ITALIAN));
		assertNotNull(text);
		assertEquals("12,345.67", text);

		withUILocale(Locale.FRANCE, () -> {
			String text2 = converter2.convertToPresentation(dbl,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
			assertNotNull(text2);
			assertEquals("12,345.67", text2);
		});

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter2.convertToPresentation(dbl,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
			assertNotNull(text2);
			assertEquals("12,345.67", text2);
		});

		Double dv = converter2
				.convertToModel("123.",
						new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US))
				.getOrThrow(error -> new RuntimeException(error));
		assertEquals(Double.valueOf(123d), dv);

		// fixed NumberFormat
		StringToNumberConverter<Double> converter4 = StringToNumberConverter.create(Double.class,
				new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.ITALY)));

		text = converter4.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12345,67", text);

		text = converter4.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
		assertNotNull(text);
		assertEquals("12345,67", text);

		text = converter4.convertToPresentation(12345.6d,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
		assertNotNull(text);
		assertEquals("12345,60", text);

		// fixed pattern
		StringToNumberConverter<Double> converter5 = StringToNumberConverter.create(Double.class, "#.00");

		text = converter5.convertToPresentation(dbl,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12345.67", text);
		text = converter5.convertToPresentation(12345.6d,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12345.60", text);

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter5.convertToPresentation(dbl,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, (Locale) null));
			assertNotNull(text2);
			assertEquals("12345,67", text2);
		});

	}

	@Test
	public void testMinMaxDecimals() {

		StringToNumberConverter<Double> converter1 = StringToNumberConverter.builder(Double.class).minDecimals(2)
				.build();

		String text = converter1.convertToPresentation(123.5d,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("123.50", text);

		text = converter1.convertToPresentation(123d,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("123.00", text);

	}

	@Test
	public void testIntegerConverter() {

		final Integer itg = 12345;

		StringToNumberConverter<Integer> converter = StringToNumberConverter.create(Integer.class);

		String text = converter.convertToPresentation(itg,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12345", text);

		StringToNumberConverter<Integer> converter1 = StringToNumberConverter.create(Integer.class);
		converter1.setUseGrouping(true);

		text = converter1.convertToPresentation(itg,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(text);
		assertEquals("12,345", text);

		Result<Integer> result = converter1.convertToModel(text,
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(result);
		assertFalse(result.isError());
		assertEquals(itg, result.getOrThrow(error -> new RuntimeException(error)));

		withUILocale(Locale.US, () -> {
			String text2 = converter1.convertToPresentation(itg,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null));
			assertNotNull(text2);
			assertEquals("12,345", text2);

			assertFalse(converter1.getDecimalSymbol().isPresent());
		});

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter1.convertToPresentation(itg,
					new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, (Locale) null));
			assertNotNull(text2);
			assertEquals("12.345", text2);

			assertFalse(converter1.getDecimalSymbol().isPresent());
		});

		// negatives
		StringToNumberConverter<Integer> converter3 = StringToNumberConverter.create(Integer.class);
		converter3.setAllowNegatives(false);

		result = converter3.convertToModel("-123",
				new ValueContext((Binder<?>) null, (Component) null, (HasValue<?, ?>) null, Locale.US));
		assertNotNull(result);
		assertTrue(result.isError());

	}

	@Test
	public void testValidationPattern() {

		// integers
		String validationPattern = StringToNumberConverter.builder(Integer.class, Locale.US).negatives(false).build()
				.getValidationPattern();
		assertNotNull(validationPattern);
		Pattern pattern = Pattern.compile(validationPattern);
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.6").matches());
		assertFalse(pattern.matcher("-12345").matches());

		// integers - negatives
		validationPattern = StringToNumberConverter.builder(Integer.class, Locale.US).negatives(true).build()
				.getValidationPattern();
		assertNotNull(validationPattern);
		pattern = Pattern.compile(validationPattern);
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("-12345").matches());
		assertFalse(pattern.matcher("-123.45").matches());
		assertFalse(pattern.matcher("12345-").matches());
		assertFalse(pattern.matcher("123-45").matches());
		assertFalse(pattern.matcher("--12345").matches());

		// decimals
		validationPattern = StringToNumberConverter.builder(Double.class, Locale.US).negatives(false).build()
				.getValidationPattern();
		assertNotNull(validationPattern);
		pattern = Pattern.compile(validationPattern);
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher(".12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("12345.67").matches());

		// decimals - negatives
		validationPattern = StringToNumberConverter.builder(Double.class, Locale.US).negatives(true).build()
				.getValidationPattern();
		assertNotNull(validationPattern);
		pattern = Pattern.compile(validationPattern);
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertTrue(pattern.matcher("-12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("-12345.67").matches());

		// max decimals
		validationPattern = StringToNumberConverter.builder(Double.class, Locale.US).negatives(false).maxDecimals(2)
				.build().getValidationPattern();
		assertNotNull(validationPattern);
		pattern = Pattern.compile(validationPattern);
		assertTrue(pattern.matcher("12345").matches());
		assertTrue(pattern.matcher("12345.7").matches());
		assertTrue(pattern.matcher("12345.77").matches());
		assertFalse(pattern.matcher("12345.777").matches());

	}

	private static void withUILocale(Locale locale, Runnable operation) {
		try {
			UI ui = new UI();
			ui.setLocale(locale);
			CurrentInstance.set(UI.class, ui);

			operation.run();
		} finally {
			CurrentInstance.set(UI.class, null);
		}
	}

	private static void withContextLocale(Locale locale, Runnable operation) {
		Context.get().executeThreadBound(LocalizationContext.CONTEXT_KEY,
				LocalizationContext.builder().withInitialLocale(locale).build(), operation);
	}

}
