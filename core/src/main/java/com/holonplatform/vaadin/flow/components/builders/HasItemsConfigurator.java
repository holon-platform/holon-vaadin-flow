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

import java.util.Arrays;
import java.util.Collections;

/**
 * Configurator for components which supports a set of items.
 * 
 * @param <ITEM> Item type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface HasItemsConfigurator<ITEM, C extends HasItemsConfigurator<ITEM, C>> {

	/**
	 * Sets the data items of this component.
	 * @param items The items to set
	 * @return this
	 */
	C items(Iterable<ITEM> items);

	/**
	 * Sets the data items of this component.
	 * @param items The items to set
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default C items(ITEM... items) {
		return items((items == null) ? Collections.emptyList() : Arrays.asList(items));
	}

	/**
	 * Add given <code>item</code> to the data items of this component.
	 * @param item The item to add (not null)
	 * @return this
	 */
	C addItem(ITEM item);

}
