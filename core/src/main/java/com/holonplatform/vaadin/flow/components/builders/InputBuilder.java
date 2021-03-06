/*
 * Copyright 2016-2017 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;

/**
 * Builder to create {@link Input} instances.
 * 
 * @param <T> Value type
 * @param <E> Value change event type
 * @param <C> Input type
 * @param <I> Validatable input type
 * @param <B> Concrete builder type
 * @param <V> Validatable input builder type
 * 
 * @since 5.2.0
 */
public interface InputBuilder<T, E extends ValueChangeEvent<T>, C extends Input<T>, I extends ValidatableInput<T>, B extends InputBuilder<T, E, C, I, B, V>, V extends BaseValidatableInputBuilder<T, I, V>>
		extends InputConfigurator<T, E, B> {

	/**
	 * Build and returns the {@link Input} instance.
	 * @return the {@link Input} instance
	 */
	C build();

	/**
	 * Build a {@link ValidatableInput} component.
	 * @return A {@link ValidatableInput} builder
	 */
	V validatable();

}
