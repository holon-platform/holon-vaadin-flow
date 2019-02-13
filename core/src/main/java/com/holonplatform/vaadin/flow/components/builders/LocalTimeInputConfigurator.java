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

import java.time.LocalTime;
import java.util.Locale;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextFieldVariant;

/**
 * {@link LocalTime} type {@link Input} components configurator.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.2
 */
public interface LocalTimeInputConfigurator<C extends LocalTimeInputConfigurator<C>>
		extends InputValueConfigurator<LocalTime, ValueChangeEvent<LocalTime>, C>, HasEnabledConfigurator<C>,
		InputNotifierConfigurator<C>, KeyNotifierConfigurator<C>, HasValueChangeModeConfigurator<C>,
		HasAutocompleteConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasAutofocusConfigurator<C>,
		FocusableConfigurator<Component, C>, HasPrefixAndSuffixConfigurator<C>, CompositionNotifierConfigurator<C>,
		HasPlaceholderConfigurator<C>, HasLabelConfigurator<C>, HasTitleConfigurator<C>, HasPatternConfigurator<C>,
		HasThemeVariantConfigurator<TextFieldVariant, C>, DeferrableLocalizationConfigurator<C> {

	/**
	 * Set the {@link Locale} to use to represent and convert {@link LocalTime} values.
	 * <p>
	 * The provided {@link Locale} will be always used to obtain time separator character to use.
	 * </p>
	 * @param locale the {@link Locale} to set
	 * @return this
	 */
	C locale(Locale locale);

	/**
	 * Sets the time separator character to use.
	 * <p>
	 * The provided character will be always as time separator, regardless of the current {@link Locale}.
	 * </p>
	 * @param timeSeparator the time separator character to set
	 * @return this
	 */
	C timeSeparator(char timeSeparator);

	/**
	 * Sets the function to use to display the default input placeholder text.
	 * <p>
	 * The time separator character is provider as function argument.
	 * </p>
	 * @param placeholderProvider the function to use to display the default input placeholder text
	 * @return this
	 */
	C defaultPlaceholder(Function<Character, String> placeholderProvider);

}
