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

import com.vaadin.flow.component.HasStyle;

/**
 * Configurator for {@link HasStyle} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasStyleConfigurator<C extends HasStyleConfigurator<C>> {

	/**
	 * Adds one or more CSS style class names to this component.
	 * @param styleNames The CSS style class names to be added to the component
	 * @return this
	 */
	C styleNames(String... styleNames);

	/**
	 * Adds a CSS style class names to this component.
	 * <p>
	 * Multiple styles can be specified as a space-separated list of style names.
	 * </p>
	 * @param styleName The CSS style class name to be added to the component
	 * @return this
	 */
	C styleName(String styleName);

}
