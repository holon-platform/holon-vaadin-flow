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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultStringInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextFieldVariant;

/**
 * Builder to create {@link String} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface StringInputBuilder extends InputBuilder<String, Input<String>, StringInputBuilder>,
		InputValueConfigurator<String, StringInputBuilder>, TextInputConfigurator<StringInputBuilder>,
		HasSizeConfigurator<StringInputBuilder>, HasStyleConfigurator<StringInputBuilder>,
		HasAutofocusConfigurator<StringInputBuilder>, FocusableConfigurator<Component, StringInputBuilder>,
		HasPrefixAndSuffixConfigurator<StringInputBuilder>, CompositionNotifierConfigurator<StringInputBuilder>,
		HasPlaceholderConfigurator<StringInputBuilder>, HasLabelConfigurator<StringInputBuilder>,
		HasTitleConfigurator<StringInputBuilder>, HasPatternConfigurator<StringInputBuilder>,
		HasThemeVariantConfigurator<TextFieldVariant, StringInputBuilder>,
		DeferrableLocalizationConfigurator<StringInputBuilder> {

	/**
	 * Get a new {@link StringInputBuilder} to create a {@link String} type {@link Input}.
	 * @return A new {@link StringInputBuilder}
	 */
	static StringInputBuilder create() {
		return new DefaultStringInputBuilder();
	}

}
