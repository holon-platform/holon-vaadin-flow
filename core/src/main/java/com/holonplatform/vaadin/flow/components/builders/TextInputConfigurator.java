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

/**
 * Configurator for text type {@link Input} components.
 *
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface TextInputConfigurator<C extends TextInputConfigurator<C>> extends HasEnabledConfigurator<C>,
		HasAutocompleteConfigurator<C>, HasAutocapitalizeConfigurator<C>, HasAutocorrectConfigurator<C>,
		InputNotifierConfigurator<C>, KeyNotifierConfigurator<C>, HasValueChangeModeConfigurator<C> {

	/**
	 * Set the maximum number of characters (in Unicode code points) that the user can enter.
	 * @param maxLength the maximum length
	 * @return this
	 */
	C maxLength(int maxLength);

	/**
	 * Set the minimum number of characters (in Unicode code points) that the user can enter.
	 * @param minLength the minimum length
	 * @return this
	 */
	C minLength(int minLength);

	/**
	 * Enable or disable treating empty String values as <code>null</code> values.
	 * <p>
	 * By default this behaviour is enabled.
	 * </p>
	 * @param emptyValuesAsNull True to treat empty String values as <code>null</code> values
	 * @return this
	 */
	C emptyValuesAsNull(boolean emptyValuesAsNull);

	/**
	 * Enable or disable treating blank String values (with 0 length or whitespaces only Strings) as <code>null</code>
	 * values.
	 * <p>
	 * By default this behaviour is disabled.
	 * </p>
	 * @param blankValuesAsNull True to treat blank String values (with 0 length or whitespaces only Strings) as
	 *        <code>null</code> values
	 * @return this
	 */
	C blankValuesAsNull(boolean blankValuesAsNull);

	/**
	 * Set whether to automatically select the input value when the input component gains focus.
	 * @param autoselect <code>true</code> to automatically select the input value when the input component gains focus,
	 *        <code>false</code> otherwise
	 * @return this
	 * @since 5.2.3
	 */
	C autoselect(boolean autoselect);

	/**
	 * Automatically select the input value when the input component gains focus.
	 * @return this
	 * @since 5.2.3
	 */
	default C autoselect() {
		return autoselect(true);
	}

	/**
	 * Set whether to show a <em>clear</em> button which can be used to clear the input value.
	 * @param clearButtonVisible <code>true</code> to show the clear button, <code>false</code> to hide it
	 * @return this
	 * @since 5.2.3
	 */
	C clearButtonVisible(boolean clearButtonVisible);

}
