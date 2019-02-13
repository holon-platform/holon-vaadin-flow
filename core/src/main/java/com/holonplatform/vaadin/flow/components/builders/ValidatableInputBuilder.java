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

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultValidatableInputBuilder;

/**
 * {@link ValidatableInput} builder.
 * 
 * @param <T> Value type
 *
 * @since 5.0.0
 */
public interface ValidatableInputBuilder<T>
		extends BaseValidatableInputBuilder<T, ValidatableInput<T>, ValidatableInputBuilder<T>> {

	/**
	 * Get a builder to create and setup a {@link ValidatableInput} from given {@link Input}.
	 * @param <T> Value type
	 * @param input The Input instance (not null)
	 * @return A new {@link ValidatableInput} builder
	 */
	static <T> ValidatableInputBuilder<T> create(Input<T> input) {
		return new DefaultValidatableInputBuilder<>(input);
	}

}
