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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.ComponentGroup;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.HasElement;

/**
 * Property form configurator.
 * 
 * @param <L> Composition layout type
 * @param <E> Group elements type
 * @param <G> Component group type
 * @param <C> Concrete builder type
 *
 * @since 5.2.0
 */
public interface PropertyFormConfigurator<L extends HasElement, E extends HasComponent, G extends ComponentGroup<E>, C extends PropertyFormConfigurator<L, E, G, C>>
		extends ComposableConfigurator<L, E, G, C>, ComponentConfigurator<C> {

	/**
	 * Set the caption for the component bound to given property. By default, the caption is obtained from
	 * {@link Property} itself (which is {@link Localizable}).
	 * @param property Property for which to set the caption (not null)
	 * @param caption Localizable caption
	 * @return this
	 */
	C propertyCaption(Property<?> property, Localizable caption);

	/**
	 * Set the caption for the component bound to given property. By default, the caption is obtained from
	 * {@link Property} itself (which is {@link Localizable}).
	 * @param property Property for which to set the caption (not null)
	 * @param caption The caption
	 * @return this
	 */
	C propertyCaption(Property<?> property, String caption);

	/**
	 * Set the caption for the component bound to given property. By default, the caption is obtained from
	 * {@link Property} itself (which is {@link Localizable}).
	 * @param property Property for which to set the caption (not null)
	 * @param defaultCaption Default caption if no translation is available for given <code>messageCode</code> for
	 *        current Locale, or no {@link LocalizationContext} is available at all
	 * @param messageCode Caption translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	C propertyCaption(Property<?> property, String defaultCaption, String messageCode, Object... arguments);

	/**
	 * Hide the caption for the component bound to given property.
	 * @param property Property for which to hide the caption (not null)
	 * @return this
	 */
	C hidePropertyCaption(Property<?> property);

}
