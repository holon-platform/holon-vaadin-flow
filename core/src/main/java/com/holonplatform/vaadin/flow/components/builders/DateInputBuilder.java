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
import com.vaadin.flow.component.Component;

/**
 * Builder to create {@link LocalDate} type {@link Input} components.
 * 
 * @since 5.2.0
 */
public interface DateInputBuilder extends InputBuilder<String, Input<String>, DateInputBuilder>,
		HasSizeConfigurator<DateInputBuilder>, HasStyleConfigurator<DateInputBuilder>,
		HasAutofocusConfigurator<DateInputBuilder>, FocusableConfigurator<Component, DateInputBuilder>,
		HasPlaceholderConfigurator<DateInputBuilder>, HasLabelConfigurator<DateInputBuilder>,
		HasTitleConfigurator<DateInputBuilder>, DeferrableLocalizationConfigurator<DateInputBuilder> {

}
