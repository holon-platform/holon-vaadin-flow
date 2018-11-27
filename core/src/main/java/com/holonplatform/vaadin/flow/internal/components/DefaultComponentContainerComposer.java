/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.vaadin.flow.components.ComponentGroup;
import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.HasComponents;

/**
 * Default {@link Composer} which uses a {@link HasComponents} component as composition layout and adds the group
 * elements components to the layout in the order they are returned from the group.
 * 
 * @param <C> HasComponents type
 * @param <E> Elements type
 * @param <G> Elements group type
 * 
 * @since 5.2.0
 */
public class DefaultComponentContainerComposer<C extends HasComponents, E extends HasComponent, G extends ComponentGroup<E>>
		implements Composer<C, E, G> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Composable.Composer#compose(com.vaadin.flow.component.HasElement,
	 * com.holonplatform.vaadin.flow.components.ComponentGroup)
	 */
	@Override
	public void compose(C content, G source) {
		// remove all components
		content.removeAll();
		// add components
		source.getComponents().forEach(component -> content.add(component));
	}

}
