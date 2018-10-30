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

import java.util.function.Function;

/**
 * Configurator for input component which supports a date with time.
 *
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasTimeInputConfigurator<C extends HasTimeInputConfigurator<C>> {

	/**
	 * Set whether to display a space between the <code>date</code>, <code>time</code> input fields.
	 * @param spacing whether to enable spacing
	 * @return this
	 */
	C spacing(boolean spacing);

	/**
	 * Set the time separator character.
	 * @param timeSeparator the time separator to set
	 * @return this
	 */
	C timeSeparator(char timeSeparator);

	/**
	 * Set the time input width.
	 * @param timeInputWidth the time input width to set
	 * @return this
	 */
	C timeInputWidth(String timeInputWidth);

	/**
	 * Sets the function to use to display the time input placeholder text.
	 * <p>
	 * The time separator character is provider as function argument.
	 * </p>
	 * @param timePlaceholder the function to use to display the time input placeholder text
	 * @return this
	 */
	C timePlaceholder(Function<Character, String> timePlaceholder);

}
