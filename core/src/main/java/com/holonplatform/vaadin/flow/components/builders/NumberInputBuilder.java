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
package com.holonplatform.vaadin.flow.components.builders;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultNumberInputBuilder;
import com.vaadin.flow.component.Component;

/**
 * Builder to create {@link Number} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface NumberInputBuilder<T extends Number>
		extends InputBuilder<T, Input<T>, NumberInputBuilder<T>>, InputValueConfigurator<T, NumberInputBuilder<T>>,
		HasEnabledConfigurator<NumberInputBuilder<T>>, InputNotifierConfigurator<NumberInputBuilder<T>>,
		KeyNotifierConfigurator<NumberInputBuilder<T>>, HasValueChangeModeConfigurator<NumberInputBuilder<T>>,
		HasAutocompleteConfigurator<NumberInputBuilder<T>>, HasSizeConfigurator<NumberInputBuilder<T>>,
		HasStyleConfigurator<NumberInputBuilder<T>>, HasAutofocusConfigurator<NumberInputBuilder<T>>,
		FocusableConfigurator<Component, NumberInputBuilder<T>>, HasPrefixAndSuffixConfigurator<NumberInputBuilder<T>>,
		CompositionNotifierConfigurator<NumberInputBuilder<T>>, HasPlaceholderConfigurator<NumberInputBuilder<T>>,
		HasLabelConfigurator<NumberInputBuilder<T>>, HasTitleConfigurator<NumberInputBuilder<T>>,
		HasPatternConfigurator<NumberInputBuilder<T>>, DeferrableLocalizationConfigurator<NumberInputBuilder<T>> {

	/**
	 * Set the {@link Locale} to use to represent and convert number values.
	 * <p>
	 * The provided {@link Locale} will be always used to obtain the {@link NumberFormat} to represent and convert the
	 * values, regardless of the current {@link Locale}.
	 * </p>
	 * @param locale the {@link Locale} to set
	 * @return this
	 */
	NumberInputBuilder<T> locale(Locale locale);

	/**
	 * Sets the {@link NumberFormat} to use to represent and convert number values.
	 * <p>
	 * The provided {@link NumberFormat} will be always used, regardless of the current {@link Locale} or the
	 * {@link Locale} configured through {@link #locale(Locale)}.
	 * </p>
	 * @param numberFormat the {@link NumberFormat} to set
	 * @return this
	 */
	NumberInputBuilder<T> numberFormat(NumberFormat numberFormat);

	/**
	 * Sets the number format pattern to use to represent and convert number values.
	 * <p>
	 * The pattern style must be consistent with the {@link DecimalFormat} pattern conventions.
	 * </p>
	 * <p>
	 * The grouping and decimals separator symbols used will be obtained from the current {@link Locale} o from the
	 * {@link Locale} configured through {@link #locale(Locale)}.
	 * </p>
	 * @param numberFormatPattern the number format pattern to set
	 * @return this
	 */
	NumberInputBuilder<T> numberFormatPattern(String numberFormatPattern);

	/**
	 * Sets whether to allow negative numbers.
	 * @param allowNegative <code>true</code> to allow negative numbers, <code>false</code> otherwise
	 * @return this
	 */
	NumberInputBuilder<T> allowNegative(boolean allowNegative);

	/**
	 * Set whether to use the grouping symbol for number format and conversion.
	 * @param useGroupingwhether to use the grouping symbol for number format and conversion
	 * @return this
	 */
	NumberInputBuilder<T> useGrouping(boolean useGrouping);

	/**
	 * Sets the minimum number of digits allowed in the fraction portion of a number.
	 * @param minDecimals the minimum decimal digits, <code>-1</code> for no limit
	 * @return this
	 */
	NumberInputBuilder<T> minDecimals(int minDecimals);

	/**
	 * Sets the maximum number of digits allowed in the fraction portion of a number.
	 * @param maxDecimals the maximum decimal digits, <code>-1</code> for no limit
	 * @return this
	 */
	NumberInputBuilder<T> maxDecimals(int maxDecimals);

	/**
	 * Get a new {@link NumberInputBuilder} to create a numeric type {@link Input}.
	 * @param <T> Number type
	 * @param numberType Number class (not null)
	 * @return A new {@link NumberInputBuilder}
	 */
	static <T extends Number> NumberInputBuilder<T> create(Class<T> numberType) {
		return new DefaultNumberInputBuilder<>(numberType);
	}

}
