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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.internal.CurrentInstance;

public class TestStringToNumberConverter {

	@Test
	public void testDoubleConverter() {

		final Double dbl = 12345.67d;

		StringToNumberConverter<Double> converter1 = StringToNumberConverter.create(Double.class);

		String text = converter1.convertToPresentation(dbl, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12,345.67", text);

		Result<Double> result = converter1.convertToModel(text, new ValueContext(Locale.US));
		assertNotNull(result);
		assertFalse(result.isError());
		assertEquals(dbl, result.getOrThrow(error -> new RuntimeException(error)));

		withUILocale(Locale.US, () -> {
			String text2 = converter1.convertToPresentation(dbl, new ValueContext());
			assertNotNull(text2);
			assertEquals("12,345.67", text2);

			assertTrue(converter1.getGroupingSymbol().isPresent());
			assertEquals(",", converter1.getGroupingSymbol().map(c -> String.valueOf(c)).orElse(null));
			assertTrue(converter1.getDecimalSymbol().isPresent());
			assertEquals(".", converter1.getDecimalSymbol().map(c -> String.valueOf(c)).orElse(null));
		});

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter1.convertToPresentation(dbl, new ValueContext());
			assertNotNull(text2);
			assertEquals("12.345,67", text2);

			assertTrue(converter1.getGroupingSymbol().isPresent());
			assertEquals(".", converter1.getGroupingSymbol().map(c -> String.valueOf(c)).orElse(null));
			assertTrue(converter1.getDecimalSymbol().isPresent());
			assertEquals(",", converter1.getDecimalSymbol().map(c -> String.valueOf(c)).orElse(null));
		});

		// fixed Locale
		StringToNumberConverter<Double> converter2 = StringToNumberConverter.create(Double.class, Locale.US);

		text = converter2.convertToPresentation(dbl, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12,345.67", text);
		text = converter2.convertToPresentation(dbl, new ValueContext());
		assertNotNull(text);
		assertEquals("12,345.67", text);
		text = converter2.convertToPresentation(dbl, new ValueContext(Locale.ITALIAN));
		assertNotNull(text);
		assertEquals("12,345.67", text);

		withUILocale(Locale.FRANCE, () -> {
			String text2 = converter2.convertToPresentation(dbl, new ValueContext());
			assertNotNull(text2);
			assertEquals("12,345.67", text2);
		});

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter2.convertToPresentation(dbl, new ValueContext());
			assertNotNull(text2);
			assertEquals("12,345.67", text2);
		});

		// grouping
		StringToNumberConverter<Double> converter3 = StringToNumberConverter.create(Double.class);
		converter3.setUseGrouping(false);

		assertFalse(converter3.isUseGrouping());
		assertFalse(converter3.getGroupingSymbol().isPresent());

		text = converter3.convertToPresentation(dbl, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12345.67", text);

		text = converter3.convertToPresentation(12345.6d, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12345.6", text);

		// fixed NumberFormat
		StringToNumberConverter<Double> converter4 = StringToNumberConverter.create(Double.class,
				new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.ITALY)));

		text = converter4.convertToPresentation(dbl, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12345,67", text);

		text = converter4.convertToPresentation(dbl, new ValueContext());
		assertNotNull(text);
		assertEquals("12345,67", text);

		text = converter4.convertToPresentation(12345.6d, new ValueContext());
		assertNotNull(text);
		assertEquals("12345,60", text);

	}

	@Test
	public void testIntegerConverter() {

		final Integer itg = 12345;

		StringToNumberConverter<Integer> converter1 = StringToNumberConverter.create(Integer.class);

		String text = converter1.convertToPresentation(itg, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12,345", text);

		Result<Integer> result = converter1.convertToModel(text, new ValueContext(Locale.US));
		assertNotNull(result);
		assertFalse(result.isError());
		assertEquals(itg, result.getOrThrow(error -> new RuntimeException(error)));

		withUILocale(Locale.US, () -> {
			String text2 = converter1.convertToPresentation(itg, new ValueContext());
			assertNotNull(text2);
			assertEquals("12,345", text2);

			assertTrue(converter1.getGroupingSymbol().isPresent());
			assertEquals(",", converter1.getGroupingSymbol().map(c -> String.valueOf(c)).orElse(null));
			assertFalse(converter1.getDecimalSymbol().isPresent());
		});

		withContextLocale(Locale.ITALY, () -> {
			String text2 = converter1.convertToPresentation(itg, new ValueContext());
			assertNotNull(text2);
			assertEquals("12.345", text2);

			assertTrue(converter1.getGroupingSymbol().isPresent());
			assertEquals(".", converter1.getGroupingSymbol().map(c -> String.valueOf(c)).orElse(null));
			assertFalse(converter1.getDecimalSymbol().isPresent());
		});

		// grouping
		StringToNumberConverter<Integer> converter2 = StringToNumberConverter.create(Integer.class);
		converter2.setUseGrouping(false);

		assertFalse(converter2.isUseGrouping());
		assertFalse(converter2.getGroupingSymbol().isPresent());

		text = converter2.convertToPresentation(itg, new ValueContext(Locale.US));
		assertNotNull(text);
		assertEquals("12345", text);

		// negatives
		StringToNumberConverter<Integer> converter3 = StringToNumberConverter.create(Integer.class);
		converter3.setAllowNegatives(false);

		result = converter3.convertToModel("-123", new ValueContext(Locale.US));
		assertNotNull(result);
		assertTrue(result.isError());

	}

	@Test
	public void testPattern() {

		final String NEGATIVE = "-?";

		// integers
		Pattern pattern = Pattern.compile("[0-9]*");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.6").matches());
		assertFalse(pattern.matcher("-12345").matches());

		// negatives
		pattern = Pattern.compile(NEGATIVE + "[0-9]*");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("-12345").matches());
		assertFalse(pattern.matcher("-123.45").matches());
		assertFalse(pattern.matcher("12345-").matches());
		assertFalse(pattern.matcher("123-45").matches());
		assertFalse(pattern.matcher("--12345").matches());

		// decimals
		pattern = Pattern.compile("[0-9]+\\.?[0-9]+");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.").matches());
		assertFalse(pattern.matcher(".12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("12345.67").matches());

		// negatives
		pattern = Pattern.compile(NEGATIVE + "[0-9]+\\.?[0-9]+");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertTrue(pattern.matcher("-12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("-12345.67").matches());

		// quote
		pattern = Pattern.compile("[0-9]+" + Pattern.quote(".") + "?[0-9]+");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.").matches());
		assertFalse(pattern.matcher(".12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("12345.67").matches());

		final String GROUPS = "^[0-9]{1,3}(" + Pattern.quote(",") + "?[0-9]{3})*";
		final String NEGATIVE_GROUPS = "^-?[0-9]{1,3}(" + Pattern.quote(",") + "?[0-9]{3})*";

		// grouping (integers)
		pattern = Pattern.compile(GROUPS);
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12,345").matches());
		assertTrue(pattern.matcher("12,789,345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.6").matches());
		assertFalse(pattern.matcher("-12345").matches());
		assertFalse(pattern.matcher("4,12").matches());
		assertFalse(pattern.matcher(",412").matches());

		// grouping (integers) - negatives
		pattern = Pattern.compile(NEGATIVE_GROUPS);
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12,345").matches());
		assertTrue(pattern.matcher("12,789,345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("-12345").matches());
		assertFalse(pattern.matcher("4,12").matches());
		assertFalse(pattern.matcher(",412").matches());
		assertTrue(pattern.matcher("-12,345").matches());
		assertTrue(pattern.matcher("-12,789,345").matches());

		// grouping (decimals)
		pattern = Pattern.compile(GROUPS + Pattern.quote(".") + "?[0-9]+");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.").matches());
		assertFalse(pattern.matcher(".12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("12345.67").matches());
		assertTrue(pattern.matcher("12,345.67").matches());
		assertFalse(pattern.matcher("12,34.5").matches());

		// grouping (decimals) - negatives
		pattern = Pattern.compile(NEGATIVE_GROUPS + Pattern.quote(".") + "?[0-9]+");
		assertFalse(pattern.matcher("a12345").matches());
		assertTrue(pattern.matcher("12345").matches());
		assertFalse(pattern.matcher("12345.").matches());
		assertFalse(pattern.matcher(".12345").matches());
		assertFalse(pattern.matcher("12.34.5").matches());
		assertTrue(pattern.matcher("12345.6").matches());
		assertTrue(pattern.matcher("12345.67").matches());
		assertTrue(pattern.matcher("12,345.67").matches());
		assertFalse(pattern.matcher("12,34.5").matches());
		assertTrue(pattern.matcher("-12345.67").matches());
		assertTrue(pattern.matcher("-12,345.67").matches());

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
