/*
 * Copyright 2016-2019 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.vaadin.flow.component.Component;

/**
 * {@link PropertyInputForm} configurator.
 * 
 * @param <C> Content type
 * @param <B> Actual configurator type
 * 
 * @since 5.2.0
 */
public interface PropertyInputFormConfigurator<C extends Component, B extends PropertyInputFormConfigurator<C, B>>
		extends PropertyFormConfigurator<C, Input<?>, PropertyInputGroup, B>, PropertyInputGroupConfigurator<B> {

}
