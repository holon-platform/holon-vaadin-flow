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
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.internal.components.DefaultComponentContainerComposer;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasElement;

/**
 * An object which supports a {@link Composer}, which can be used to compose a set of elements on the content layout.
 * <p>
 * The available elements to compose can be obtained from the {@link ComponentGroup} provided to the composer
 * {@link Composer#compose(HasElement, ComponentGroup)} method.
 * </p>
 * 
 * @since 5.2.0
 */
public interface Composable {

	/**
	 * Compose the elements on the content layout, using the configured {@link Composer}.
	 * @throws IllegalStateException If a {@link Composer} is not available
	 */
	void compose();

	/**
	 * Provides the elements composition strategy on the content layout.
	 * 
	 * @param <C> Content layout type
	 * @param <E> Elements type
	 * @param <G> Elements group type
	 */
	@FunctionalInterface
	public interface Composer<C extends HasElement, E extends HasComponent, G extends ComponentGroup<E>> {

		/**
		 * Compose the components provided by given <code>source</code> group on the given <code>content</code> layout.
		 * @param content The content on which to compose the elements
		 * @param source The component group from which to obtain the elements to compose
		 */
		void compose(C content, G source);

	}

	/**
	 * Create a {@link Composer} which uses a {@link HasComponents} component as composition layout and adds the group
	 * elements components to the layout in the order they are returned from the group.
	 * @param <C> HasComponents type
	 * @param <E> Elements type
	 * @param <G> Elements group type
	 * @return A new {@link Composer} using a {@link HasComponent} compostion layout
	 */
	static <C extends HasComponents, E extends HasComponent, G extends ComponentGroup<E>> Composer<C, E, G> componentContainerComposer() {
		return new DefaultComponentContainerComposer<>();
	}

}
