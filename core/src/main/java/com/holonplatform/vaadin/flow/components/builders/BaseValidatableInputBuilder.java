/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.vaadin.flow.components.ValidatableInput;

/**
 * Base {@link ValidatableInput} builder.
 * 
 * @param <T> Value type
 * @param <I> Concrete validatable input type
 * @param <B> Concrete builder type
 *
 * @since 5.2.2
 */
public interface BaseValidatableInputBuilder<T, I extends ValidatableInput<T>, B extends BaseValidatableInputBuilder<T, I, B>>
		extends ValidatableInputConfigurator<T, I, B> {

	/**
	 * Build the {@link ValidatableInput} instance.
	 * @return the {@link ValidatableInput} instance
	 */
	I build();

}
