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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPasswordInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextFieldVariant;

/**
 * Builder to create {@link String} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface PasswordInputBuilder
		extends InputBuilder<String, ValueChangeEvent<String>, Input<String>, PasswordInputBuilder>,
		InputValueConfigurator<String, ValueChangeEvent<String>, PasswordInputBuilder>,
		TextInputConfigurator<PasswordInputBuilder>, HasSizeConfigurator<PasswordInputBuilder>,
		HasStyleConfigurator<PasswordInputBuilder>, HasAutofocusConfigurator<PasswordInputBuilder>,
		FocusableConfigurator<Component, PasswordInputBuilder>, HasPrefixAndSuffixConfigurator<PasswordInputBuilder>,
		CompositionNotifierConfigurator<PasswordInputBuilder>, HasPlaceholderConfigurator<PasswordInputBuilder>,
		HasLabelConfigurator<PasswordInputBuilder>, HasTitleConfigurator<PasswordInputBuilder>,
		HasPatternConfigurator<PasswordInputBuilder>,
		HasThemeVariantConfigurator<TextFieldVariant, PasswordInputBuilder>,
		DeferrableLocalizationConfigurator<PasswordInputBuilder> {

	/**
	 * Set to <code>false</code> to hide the eye icon which toggles the password visibility.
	 * @param revealButtonVisible <code>true</code> to set the button visible, <code>false</code> otherwise
	 * @return this
	 */
	PasswordInputBuilder revealButtonVisible(boolean revealButtonVisible);

	/**
	 * Get a new {@link PasswordInputBuilder} to create a {@link String} type {@link Input}.
	 * @return A new {@link PasswordInputBuilder}
	 */
	static PasswordInputBuilder create() {
		return new DefaultPasswordInputBuilder();
	}

}
