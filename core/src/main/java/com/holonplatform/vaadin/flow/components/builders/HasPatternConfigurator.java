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

/**
 * Configurator for components which supports an input pattern.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasPatternConfigurator<C extends HasPatternConfigurator<C>> {

	/**
	 * A regular expression that the value is checked against. The pattern must match the entire value, not just some
	 * subset.
	 * @param pattern the value to set
	 * @return this
	 */
	C pattern(String pattern);

	/**
	 * When set to true, user is prevented from typing a value that conflicts with the pattern configured through
	 * {@link #pattern(String)}.
	 * @param preventInvalidInput the value to set
	 * @return this
	 */
	C preventInvalidInput(boolean preventInvalidInput);

	/**
	 * Set that the user is prevented from typing a value that conflicts with the pattern configured through
	 * {@link #pattern(String)}.
	 * @return this
	 */
	default C preventInvalidInput() {
		return preventInvalidInput(true);
	}

}
