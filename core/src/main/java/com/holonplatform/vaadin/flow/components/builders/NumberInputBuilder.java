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
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultNumberInputBuilder;

/**
 * Builder to create {@link Number} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface NumberInputBuilder<T extends Number> extends NumberInputConfigurator<T, NumberInputBuilder<T>>,
		InputBuilder<T, ValueChangeEvent<T>, Input<T>, ValidatableInput<T>, NumberInputBuilder<T>, ValidatableNumberInputBuilder<T>> {

	/**
	 * Get a new {@link NumberInputBuilder} to create a numeric type {@link Input}.
	 * @param <T> Number type
	 * @param numberType Number class (not null)
	 * @return A new {@link NumberInputBuilder}
	 */
	static <T extends Number> NumberInputBuilder<T> create(Class<T> numberType) {
		return new DefaultNumberInputBuilder<>(numberType);
	}

}
