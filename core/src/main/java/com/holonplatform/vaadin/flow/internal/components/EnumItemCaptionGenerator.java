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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultEnumCaptionRegistry;

/**
 * Enumeration item caption generator.
 * 
 * @param <E> Enum type
 *
 * @since 5.2.0
 */
public class EnumItemCaptionGenerator<E extends Enum<E>> implements ItemCaptionGenerator<E> {

	private static final long serialVersionUID = -6778505462268286279L;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator#getItemCaption(java.
	 * lang.Object)
	 */
	@Override
	public String getItemCaption(E item) {
		if (item != null) {
			return LocalizationProvider.localize(DefaultEnumCaptionRegistry.getEnumCaption(item)).orElse(item.name());
		}
		return "";
	}

}
