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

import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.PropertyComponentSource;
import com.vaadin.flow.component.HasComponents;

/**
 * Default {@link Composer} using a {@link HasComponents} as composition root and adding the componens to the layout in
 * the order they are returned from the a {@link PropertyComponentSource}.
 * 
 * @param <C> Actual HasComponents type
 * @param <S> Component source
 * 
 * @since 5.0.0
 */
public class DefaultComponentContainerComposer<C extends HasComponents, S extends PropertyComponentSource>
		implements Composer<C, S> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ComposableComponent.Composer#compose(com.vaadin.ui.Component,
	 * java.lang.Object)
	 */
	@Override
	public void compose(C content, S source) {
		// remove all components
		content.removeAll();
		// add components
		source.getComponents().forEach(component -> content.add(component));
	}

}
