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

import com.holonplatform.core.property.Property;

/**
 * Configurator for selectable inputs with {@link Property} support.
 * 
 * @param <T> Value type
 * @param <S> Selection type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.1
 */
public interface PropertySelectInputConfigurator<T, S, C extends PropertySelectInputConfigurator<T, S, C>>
		extends SelectableInputConfigurator<T, S, C> {

	/**
	 * Use given property value to generate the select items captions.
	 * @param property The property to use as item captions (not null)
	 * @return this
	 */
	C itemCaptionProperty(Property<?> property);

}
