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

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultStringAreaInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextAreaVariant;

/**
 * Builder to create {@link String} type {@link Input} components rendered as a text area.
 * 
 * @since 5.2.0
 */
public interface StringAreaInputBuilder
		extends InputBuilder<String, ValueChangeEvent<String>, Input<String>, StringAreaInputBuilder>,
		InputValueConfigurator<String, ValueChangeEvent<String>, StringAreaInputBuilder>,
		TextInputConfigurator<StringAreaInputBuilder>, HasSizeConfigurator<StringAreaInputBuilder>,
		HasStyleConfigurator<StringAreaInputBuilder>, HasAutofocusConfigurator<StringAreaInputBuilder>,
		FocusableConfigurator<Component, StringAreaInputBuilder>,
		HasPrefixAndSuffixConfigurator<StringAreaInputBuilder>, CompositionNotifierConfigurator<StringAreaInputBuilder>,
		HasPlaceholderConfigurator<StringAreaInputBuilder>, HasLabelConfigurator<StringAreaInputBuilder>,
		HasThemeVariantConfigurator<TextAreaVariant, StringAreaInputBuilder>,
		DeferrableLocalizationConfigurator<StringAreaInputBuilder> {

	/**
	 * Get a new {@link StringAreaInputBuilder} to create a {@link String} type {@link Input}.
	 * @return A new {@link StringAreaInputBuilder}
	 */
	static StringAreaInputBuilder create() {
		return new DefaultStringAreaInputBuilder();
	}

}
