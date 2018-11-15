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
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

/**
 * Configurator for {@link FlexComponent} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface FlexComponentConfigurator<C extends FlexComponentConfigurator<C>>
		extends HasComponentsConfigurator<C>, HasStyleConfigurator<C>, HasSizeConfigurator<C> {

	/**
	 * Sets the default alignment to be used by all components without individual alignments inside the layout.
	 * <p>
	 * The default alignment is {@link Alignment#STRETCH}.
	 * @param alignment the alignment to apply to the components. Setting <code>null</code> will reset the alignment to
	 *        its default
	 * @return this
	 */
	C alignItems(Alignment alignment);

	/**
	 * Sets an alignment for individual element container inside the layout. This individual alignment for the element
	 * container overrides any alignment set using <code>alignItems</code>.
	 * <p>
	 * The default alignment for individual components is {@link Alignment#AUTO}.
	 * @param alignment the individual alignment for the children components. Setting <code>null</code> will reset the
	 *        alignment to its default
	 * @param elementContainers The element containers (components) to which the individual alignment should be set
	 * @return this
	 */
	C alignSelf(Alignment alignment, HasElement... elementContainers);

	/**
	 * Set the alignment for an individual component inside the layout.
	 * @param component the component to set the alignment for
	 * @param alignment the Alignment to set
	 * @return this
	 */
	default C align(Component component, Alignment alignment) {
		return alignSelf(alignment, component);
	}

	/**
	 * Sets the flex grow property of the components inside the layout. The flex grow property specifies what amount of
	 * the available space inside the layout the component should take up, proportionally to the other components.
	 * <p>
	 * For example, if all components have a flex grow property value set to 1, the remaining space in the layout will
	 * be distributed equally to all components inside the layout. If you set a flex grow property of one component to
	 * 2, that component will take twice the available space as the other components, and so on.
	 * <p>
	 * Setting to flex grow property value 0 disables the expansion of the element container. Negative values are not
	 * allowed.
	 * @param flexGrow the proportion of the available space the element container should take up
	 * @param elementContainers the containers (components) to apply the flex grow property
	 * @return this
	 */
	C flexGrow(double flexGrow, HasElement... elementContainers);

	/**
	 * Gets the {@link JustifyContentMode} used by this layout.
	 * <p>
	 * The default justify content mode is {@link JustifyContentMode#START}.
	 * @param justifyContentMode the justify content mode of the layout, never <code>null</code>
	 * @return this
	 */
	C justifyContentMode(JustifyContentMode justifyContentMode);

	/**
	 * Expands the given components.
	 * <p>
	 * It effectively sets {@code 1} as a flex grow property value for each component.
	 * @param componentsToExpand components to expand
	 * @return this
	 */
	C expand(Component... componentsToExpand);

	/**
	 * Adds a component to the layout using given <code>alignment</code>.
	 * @param component The component to add
	 * @param alignment the Alignment to use for the component
	 * @return this
	 */
	default C addAndAlign(Component component, Alignment alignment) {
		add(component);
		return alignSelf(alignment, component);
	}

}
