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
package com.holonplatform.vaadin.flow.components.converters;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.internal.converters.DefaultStringToNumberConverter;
import com.vaadin.flow.data.converter.Converter;

/**
 * A {@link Converter}s that convert from {@link Number} types to {@link String} and back.
 * <p>
 * The String value is trimmed before conversion. Null or empty String values will be converted into <code>null</code>
 * Number values.
 * </p>
 * <p>
 * When the {@link Locale} to ue for conversion is not available from conversion context or current UI, it is retrieved
 * from the current {@link LocalizationContext}, if available.
 * </p>
 * 
 * @param <T> Number type
 * 
 * @since 5.2.0
 */
public interface StringToNumberConverter<T extends Number> extends Converter<String, T> {

	/**
	 * Set whether to use grouping.
	 * @param useGrouping whether to use grouping
	 */
	void setUseGrouping(boolean useGrouping);

	/**
	 * Get whether to use grouping.
	 * @return whether to use grouping
	 */
	boolean isUseGrouping();

	/**
	 * Get whether to allow negative numbers.
	 * @return whether to allow negative number
	 */
	boolean isAllowNegatives();

	/**
	 * Set whether to allow negative numbers.
	 * @param allowNegatives whether to allow negative numbers
	 */
	void setAllowNegatives(boolean allowNegatives);

	/**
	 * Get the minimum decimal digits to display.
	 * @return the minimum decimal digits, <code>-1</code> if not configured
	 */
	int getMinDecimals();

	/**
	 * Set the minimum decimal digits to display.
	 * @param minDecimals the minimum decimal digits to display, <code>-1</code> for no limit
	 */
	void setMinDecimals(int minDecimals);

	/**
	 * Get the maximum decimal digits allowed.
	 * @return the maximum decimal digits, <code>-1</code> if no limit
	 */
	int getMaxDecimals();

	/**
	 * Set the maximum decimal digits allowed.
	 * @param maxDecimals the maximum decimal digits, <code>-1</code> for no limit
	 */
	void setMaxDecimals(int maxDecimals);

	/**
	 * Get the grouping separator character, if available
	 * @return the grouping separator character, or empty if grouping is disabled
	 */
	Optional<Character> getGroupingSymbol();

	/**
	 * Get the decimal separator character, if available
	 * @return the decimal separator character, or empty if the number type is not a decimal number
	 */
	Optional<Character> getDecimalSymbol();

	/**
	 * Get the regex validation pattern which corresponds to the converter configuration.
	 * @return the regex validation pattern
	 */
	String getValidationPattern();

	// direct builders

	/**
	 * Create a new {@link StringToNumberConverter} for given number type.
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @return A new {@link StringToNumberConverter}
	 */
	static <T extends Number> StringToNumberConverter<T> create(Class<T> numberType) {
		return builder(numberType).build();
	}

	/**
	 * Create a new {@link StringToNumberConverter} for given number type and given {@link Locale}.
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @param locale The {@link Locale} to use
	 * @return A new {@link StringToNumberConverter}
	 */
	static <T extends Number> StringToNumberConverter<T> create(Class<T> numberType, Locale locale) {
		return builder(numberType, locale).build();
	}

	/**
	 * Create a new {@link StringToNumberConverter} for given number type and using given number format pattern.
	 * <p>
	 * The pattern style must be consistent with the Java DecimalFormat pattern conventions.
	 * </p>
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @param numberFormatPattern The number format pattern to use
	 * @return A new {@link StringToNumberConverter}
	 */
	static <T extends Number> StringToNumberConverter<T> create(Class<T> numberType, String numberFormatPattern) {
		return builder(numberType, numberFormatPattern).build();
	}

	/**
	 * Create a new {@link StringToNumberConverter} for given number type and using given {@link NumberFormat}.
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @param numberFormat The {@link NumberFormat} to use
	 * @return A new {@link StringToNumberConverter}
	 */
	static <T extends Number> StringToNumberConverter<T> create(Class<T> numberType, NumberFormat numberFormat) {
		return builder(numberType, numberFormat).build();
	}

	// builders

	/**
	 * Get a {@link Builder} to create and configure a {@link StringToNumberConverter} for given number type.
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @return A new {@link StringToNumberConverter} builder
	 */
	static <T extends Number> Builder<T> builder(Class<? extends T> numberType) {
		return new DefaultStringToNumberConverter.DefaultBuilder<>(numberType);
	}

	/**
	 * Get a {@link Builder} to create and configure a {@link StringToNumberConverter} for given number type and given
	 * {@link Locale}.
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @param locale The {@link Locale} to use
	 * @return A new {@link StringToNumberConverter} builder
	 */
	static <T extends Number> Builder<T> builder(Class<? extends T> numberType, Locale locale) {
		return new DefaultStringToNumberConverter.DefaultBuilder<>(numberType, locale);
	}

	/**
	 * Get a {@link Builder} to create and configure a {@link StringToNumberConverter} for given number type and using
	 * given number format pattern.
	 * <p>
	 * The pattern style must be consistent with the Java DecimalFormat pattern conventions.
	 * </p>
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @param numberFormatPattern The number format pattern to use
	 * @return A new {@link StringToNumberConverter} builder
	 */
	static <T extends Number> Builder<T> builder(Class<? extends T> numberType, String numberFormatPattern) {
		return new DefaultStringToNumberConverter.DefaultBuilder<>(numberType, numberFormatPattern);
	}

	/**
	 * Get a {@link Builder} to create and configure a {@link StringToNumberConverter} for given number type and using
	 * given {@link NumberFormat}.
	 * @param <T> Number type
	 * @param numberType Number type (not null)
	 * @param numberFormat The {@link NumberFormat} to use
	 * @return A new {@link StringToNumberConverter} builder
	 */
	static <T extends Number> Builder<T> builder(Class<? extends T> numberType, NumberFormat numberFormat) {
		return new DefaultStringToNumberConverter.DefaultBuilder<>(numberType, numberFormat);
	}

	/**
	 * {@link StringToNumberConverter} builder.
	 * 
	 * @param <T> Number type
	 */
	public interface Builder<T extends Number> {

		/**
		 * Set whether to use grouping.
		 * <p>
		 * Default is <code>true</code>.
		 * </p>
		 * @param useGrouping whether to use grouping
		 * @return this
		 */
		Builder<T> grouping(boolean grouping);

		/**
		 * Set whether to allow negative numbers.
		 * <p>
		 * Default is <code>true</code>.
		 * </p>
		 * @param allowNegatives whether to allow negative numbers
		 * @return this
		 */
		Builder<T> negatives(boolean negatives);

		/**
		 * Set the maximum decimal digits allowed.
		 * @param maxDecimals the maximum decimal digits, <code>-1</code> for no limit
		 * @return this
		 */
		Builder<T> maxDecimals(int maxDecimals);

		/**
		 * Set the minimum decimal digits to display.
		 * @param minDecimals the minimum decimal digits to display, <code>-1</code> for no limit
		 * @return this
		 */
		Builder<T> minDecimals(int minDecimals);

		/**
		 * Build and obtain the {@link StringToNumberConverter}.
		 * @return the {@link StringToNumberConverter} instance
		 */
		StringToNumberConverter<T> build();

	}

}
