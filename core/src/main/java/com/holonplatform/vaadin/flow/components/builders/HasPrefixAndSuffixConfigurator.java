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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;

/**
 * Configurator for {@link HasPrefixAndSuffix} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasPrefixAndSuffixConfigurator<C extends HasPrefixAndSuffixConfigurator<C>> {

	/**
	 * Adds the given component into this field before the content, replacing any existing prefix component.
	 * @param component the component to set, can be <code>null</code> to remove existing prefix component
	 * @return this
	 */
	C prefixComponent(Component component);

	/**
	 * Adds the given component into this field after the content, replacing any existing suffix component.
	 * @param component the component to set, can be <code>null</code> to remove existing suffix component
	 * @return this
	 */
	C suffixComponent(Component component);

}
