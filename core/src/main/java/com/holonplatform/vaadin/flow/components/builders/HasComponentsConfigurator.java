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
import java.util.stream.Collectors;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * Configurator for {@link HasComponents} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasComponentsConfigurator<C extends HasComponentsConfigurator<C>>
		extends HasElementConfigurator<C>, HasEnabledConfigurator<C> {

	/**
	 * Adds the given components as children of this component.
	 * @param components The components to add
	 * @return this
	 */
	C add(Component... components);

	/**
	 * Adds the components of each {@link HasComponent} as children of this component.
	 * @param components The {@link HasComponent}s to add
	 * @return this
	 */
	default C add(HasComponent... components) {
		return add(Arrays.asList(components).stream().map(c -> c.getComponent()).collect(Collectors.toList())
				.toArray(new Component[0]));
	}

}
