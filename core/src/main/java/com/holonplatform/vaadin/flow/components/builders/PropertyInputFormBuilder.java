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

import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.vaadin.flow.component.Component;

/**
 * {@link PropertyInputForm} builder.
 * 
 * @param <C> Content type
 * 
 * @since 5.2.0
 */
public interface PropertyInputFormBuilder<C extends Component>
		extends PropertyFormConfigurator<C, PropertyInputFormBuilder<C>>,
		PropertyInputGroupConfigurator<PropertyInputForm, PropertyInputFormBuilder<C>> {

	/**
	 * Build the {@link PropertyInputForm}.
	 * @return A new {@link PropertyInputForm}
	 */
	PropertyInputForm build();

}