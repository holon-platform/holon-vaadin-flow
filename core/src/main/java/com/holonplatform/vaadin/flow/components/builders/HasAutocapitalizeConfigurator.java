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

import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.HasAutocapitalize;

/**
 * Configurator for {@link HasAutocapitalize} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasAutocapitalizeConfigurator<C extends HasAutocapitalizeConfigurator<C>> {

	/**
	 * Sets the {@link Autocapitalize} attribute for indicating whether the value of this component can be automatically
	 * completed by the browser.
	 * <p>
	 * If not set, devices may apply their own default.
	 * <p>
	 * @param autocapitalize the {@link Autocapitalize} value, or <code>null</code> to unset
	 * @return this
	 */
	C autocapitalize(Autocapitalize autocapitalize);

}
