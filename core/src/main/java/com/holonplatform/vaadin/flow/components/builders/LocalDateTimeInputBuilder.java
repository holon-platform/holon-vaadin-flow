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

import java.time.LocalDateTime;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLocalDateTimeInputBuilder;

/**
 * A {@link BaseDateInputBuilder} to create {@link LocalDateTime} type {@link Input} components.
 *
 * @since 5.2.0
 */
public interface LocalDateTimeInputBuilder extends BaseDateInputBuilder<LocalDateTime, LocalDateTimeInputBuilder>,
		HasTimeInputConfigurator<LocalDateTimeInputBuilder> {

	/**
	 * Create a new {@link LocalDateTimeInputBuilder}.
	 * @return A new {@link LocalDateTimeInputBuilder}
	 */
	static LocalDateTimeInputBuilder create() {
		return new DefaultLocalDateTimeInputBuilder();
	}

}
