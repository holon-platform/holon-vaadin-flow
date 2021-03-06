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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasEnabledConfigurator;
import com.vaadin.flow.component.HasEnabled;

/**
 * Configurator for {@link HasEnabled} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasEnabledConfigurator<C extends HasEnabledConfigurator<C>> {

	/**
	 * Set whether the component is enabled.
	 * @param enabled if <code>false</code> then explicitly disables the object, if <code>true</code> then enables the
	 *        object so that its state depends on parent
	 * @return this
	 */
	C enabled(boolean enabled);

	/**
	 * Set the component as not enabled.
	 * @return this
	 * @see #enabled(boolean)
	 */
	default C disabled() {
		return enabled(false);
	}

	/**
	 * Base {@link HasEnabledConfigurator}.
	 */
	public interface BaseHasEnabledConfigurator extends HasEnabledConfigurator<BaseHasEnabledConfigurator> {

	}

	/**
	 * Create a new {@link BaseHasEnabledConfigurator}.
	 * @param component Component to configure (not null)
	 * @return A new {@link BaseHasEnabledConfigurator}
	 */
	static BaseHasEnabledConfigurator create(HasEnabled component) {
		return new DefaultHasEnabledConfigurator(component);
	}

}
