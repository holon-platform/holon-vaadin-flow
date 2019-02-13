/*
 * Copyright 2016-2019 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextFieldVariant;

/**
 * {@link Number} type {@link Input} components configurator.
 * 
 * @param <T> Number type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.2
 */
public interface NumberInputConfigurator<T extends Number, C extends NumberInputConfigurator<T, C>>
		extends InputValueConfigurator<T, ValueChangeEvent<T>, C>, HasEnabledConfigurator<C>,
		InputNotifierConfigurator<C>, KeyNotifierConfigurator<C>, HasValueChangeModeConfigurator<C>,
		HasAutocompleteConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasAutofocusConfigurator<C>,
		FocusableConfigurator<Component, C>, HasPrefixAndSuffixConfigurator<C>, CompositionNotifierConfigurator<C>,
		HasPlaceholderConfigurator<C>, HasLabelConfigurator<C>, HasTitleConfigurator<C>, HasPatternConfigurator<C>,
		HasThemeVariantConfigurator<TextFieldVariant, C>, DeferrableLocalizationConfigurator<C> {

	/**
	 * Set the {@link Locale} to use to represent and convert number values.
	 * <p>
	 * The provided {@link Locale} will be always used to obtain the {@link NumberFormat} to represent and convert the
	 * values, regardless of the current {@link Locale}.
	 * </p>
	 * @param locale the {@link Locale} to set
	 * @return this
	 */
	C locale(Locale locale);

	/**
	 * Sets the {@link NumberFormat} to use to represent and convert number values.
	 * <p>
	 * The provided {@link NumberFormat} will be always used, regardless of the current {@link Locale} or the
	 * {@link Locale} configured through {@link #locale(Locale)}.
	 * </p>
	 * @param numberFormat the {@link NumberFormat} to set
	 * @return this
	 */
	C numberFormat(NumberFormat numberFormat);

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
	C numberFormatPattern(String numberFormatPattern);

	/**
	 * Sets whether to allow negative numbers.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @param allowNegative <code>true</code> to allow negative numbers, <code>false</code> otherwise
	 * @return this
	 */
	C allowNegative(boolean allowNegative);

	/**
	 * Sets the minimum number of digits allowed in the fraction portion of a number.
	 * @param minDecimals the minimum decimal digits, <code>-1</code> for no limit
	 * @return this
	 */
	C minDecimals(int minDecimals);

	/**
	 * Sets the maximum number of digits allowed in the fraction portion of a number.
	 * @param maxDecimals the maximum decimal digits, <code>-1</code> for no limit
	 * @return this
	 */
	C maxDecimals(int maxDecimals);

}
