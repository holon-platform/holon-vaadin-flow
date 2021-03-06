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

import java.io.Serializable;

import com.holonplatform.vaadin.flow.components.ItemSet;
import com.holonplatform.vaadin.flow.internal.components.EnumItemCaptionGenerator;

/**
 * {@link ItemSet} configurator.
 *
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ItemSetConfigurator<C extends ItemSetConfigurator<C>> {

	/**
	 * Item caption generator.
	 * 
	 * @param <ITEM> Item type
	 * 
	 * @since 5.2.0
	 */
	@FunctionalInterface
	public interface ItemCaptionGenerator<ITEM> extends Serializable {

		/**
		 * Get the caption for given <code>item</code>.
		 * @param item the item for which to get the caption
		 * @return Item caption (not null)
		 */
		String getItemCaption(ITEM item);

	}

	/**
	 * Create the default {@link ItemCaptionGenerator} for enumeration types.
	 * @param <E> Enum type
	 * @return Default {@link ItemCaptionGenerator} for enumeration types
	 */
	static <E extends Enum<E>> ItemCaptionGenerator<E> enumCaptionGenerator() {
		return new EnumItemCaptionGenerator<>();
	}

}
