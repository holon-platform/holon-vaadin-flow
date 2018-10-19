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

import com.vaadin.flow.component.textfield.HasAutocorrect;

/**
 * Configurator for {@link HasAutocorrect} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasAutocorrectConfigurator<C extends HasAutocorrectConfigurator<C>> {

	/**
	 * Enable or disable <code>autocorrect</code> for the field.
	 * <p>
	 * If not set, devices may apply their own defaults.
	 * <p>
	 * @param autocorrect true to enable <code>autocorrect</code>, false to disable
	 * @return this
	 */
	C autocorrect(boolean autocorrect);

}
