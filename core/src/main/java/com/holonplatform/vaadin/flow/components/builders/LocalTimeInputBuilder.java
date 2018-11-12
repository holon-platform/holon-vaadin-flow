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

import java.time.LocalTime;
import java.util.Locale;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLocalTimeInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextFieldVariant;

/**
 * Builder to create {@link LocalTime} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface LocalTimeInputBuilder extends InputBuilder<LocalTime, Input<LocalTime>, LocalTimeInputBuilder>,
		InputValueConfigurator<LocalTime, LocalTimeInputBuilder>, HasEnabledConfigurator<LocalTimeInputBuilder>,
		InputNotifierConfigurator<LocalTimeInputBuilder>, KeyNotifierConfigurator<LocalTimeInputBuilder>,
		HasValueChangeModeConfigurator<LocalTimeInputBuilder>, HasAutocompleteConfigurator<LocalTimeInputBuilder>,
		HasSizeConfigurator<LocalTimeInputBuilder>, HasStyleConfigurator<LocalTimeInputBuilder>,
		HasAutofocusConfigurator<LocalTimeInputBuilder>, FocusableConfigurator<Component, LocalTimeInputBuilder>,
		HasPrefixAndSuffixConfigurator<LocalTimeInputBuilder>, CompositionNotifierConfigurator<LocalTimeInputBuilder>,
		HasPlaceholderConfigurator<LocalTimeInputBuilder>, HasLabelConfigurator<LocalTimeInputBuilder>,
		HasTitleConfigurator<LocalTimeInputBuilder>, HasPatternConfigurator<LocalTimeInputBuilder>,
		HasThemeVariantConfigurator<TextFieldVariant, LocalTimeInputBuilder>,
		DeferrableLocalizationConfigurator<LocalTimeInputBuilder> {

	/**
	 * Set the {@link Locale} to use to represent and convert {@link LocalTime} values.
	 * <p>
	 * The provided {@link Locale} will be always used to obtain time separator character to use.
	 * </p>
	 * @param locale the {@link Locale} to set
	 * @return this
	 */
	LocalTimeInputBuilder locale(Locale locale);

	/**
	 * Sets the time separator character to use.
	 * <p>
	 * The provided character will be always as time separator, regardless of the current {@link Locale}.
	 * </p>
	 * @param timeSeparator the time separator character to set
	 * @return this
	 */
	LocalTimeInputBuilder timeSeparator(char timeSeparator);

	/**
	 * Sets the function to use to display the default input placeholder text.
	 * <p>
	 * The time separator character is provider as function argument.
	 * </p>
	 * @param placeholderProvider the function to use to display the default input placeholder text
	 * @return this
	 */
	LocalTimeInputBuilder defaultPlaceholder(Function<Character, String> placeholderProvider);

	/**
	 * Get a new {@link LocalTimeInputBuilder} to create a {@link LocalTime} type {@link Input}.
	 * @return A new {@link LocalTimeInputBuilder}
	 */
	static LocalTimeInputBuilder create() {
		return new DefaultLocalTimeInputBuilder();
	}

}
