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

import java.time.LocalDate;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLocalDateInputBuilder;

/**
 * {@link LocalDate} type {@link Input} components builder.
 *
 * @since 5.2.0
 */
public interface LocalDateInputBuilder extends LocalDateInputConfigurator<LocalDateInputBuilder>,
		InputBuilder<LocalDate, ValueChangeEvent<LocalDate>, Input<LocalDate>, ValidatableInput<LocalDate>, LocalDateInputBuilder, ValidatableLocalDateInputBuilder> {

	/**
	 * Create a new {@link LocalDateInputBuilder}.
	 * @return A new {@link LocalDateInputBuilder}
	 */
	static LocalDateInputBuilder create() {
		return new DefaultLocalDateInputBuilder();
	}

}
