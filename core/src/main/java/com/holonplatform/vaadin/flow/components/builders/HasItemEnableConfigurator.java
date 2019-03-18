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

import java.util.function.Predicate;

/**
 * Configurator for components which support item enabled/disabled state.
 * 
 * @param <ITEM> Item type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.3
 */
public interface HasItemEnableConfigurator<ITEM, C extends HasItemEnableConfigurator<ITEM, C>> {

	/**
	 * Sets the item enabled state predicate. The predicate is applied to each item to determine whether the item should
	 * be enabled (<code>true</code>) or disabled (<code>false</code>).
	 * @param itemEnabledProvider The item enable predicate, not <code>null</code>
	 * @return this
	 */
	C itemEnabledProvider(Predicate<ITEM> itemEnabledProvider);

}
