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

import java.text.NumberFormat;
import java.util.Locale;

import com.holonplatform.vaadin.flow.components.Input;
import com.vaadin.flow.component.Component;

/**
 * Builder to create {@link Number} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface NumberInputBuilder<T extends Number>
		extends InputBuilder<String, Input<String>, NumberInputBuilder<T>>,
		TextInputConfigurator<NumberInputBuilder<T>>, HasSizeConfigurator<NumberInputBuilder<T>>,
		HasStyleConfigurator<NumberInputBuilder<T>>, HasAutofocusConfigurator<NumberInputBuilder<T>>,
		FocusableConfigurator<Component, NumberInputBuilder<T>>, HasPrefixAndSuffixConfigurator<NumberInputBuilder<T>>,
		CompositionNotifierConfigurator<NumberInputBuilder<T>>, HasPlaceholderConfigurator<NumberInputBuilder<T>>,
		HasLabelConfigurator<NumberInputBuilder<T>>, HasTitleConfigurator<NumberInputBuilder<T>>,
		HasPatternConfigurator<NumberInputBuilder<T>>, DeferrableLocalizationConfigurator<NumberInputBuilder<T>> {

	/**
	 * Set the {@link Locale} to use to represent and convert number values.
	 * @param locale the {@link Locale} to set
	 * @return this
	 */
	NumberInputBuilder<T> locale(Locale locale);

	/**
	 * Sets the {@link NumberFormat} to use to represent and convert number values.
	 * @param numberFormat the {@link NumberFormat} to set
	 * @return this
	 */
	NumberInputBuilder<T> numberFormat(NumberFormat numberFormat);

	/**
	 * Sets whether to allow negative numbers input.
	 * @param allowNegative <code>true</code> to allow negative numbers input, <code>false</code> otherwise
	 * @return this
	 */
	NumberInputBuilder<T> allowNegative(boolean allowNegative);

	/**
	 * Sets whether to set html5 input type property as "number".
	 * @param html5NumberInputType <code>true</code> to set html5 input type property as "number", <code>false</code>
	 *        otherwise
	 * @return this
	 */
	NumberInputBuilder<T> html5NumberInputType(boolean html5NumberInputType);

}
