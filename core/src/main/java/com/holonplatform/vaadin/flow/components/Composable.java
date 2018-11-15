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
 * An object which supports a {@link Composer} delegate to compose a set of components into its content.
 * 
 * @since 5.2.0
 */
public interface Composable {

	/**
	 * Compose the components using the previously configured {@link Composer}.
	 * @throws IllegalStateException If the {@link Composer} is not available
	 */
	void compose();

	/**
	 * Delegate interface to compose a set of components from a component source into a {@link HasElement} type content.
	 * 
	 * @param <C> Content component type
	 * @param <S> Components source
	 */
	@FunctionalInterface
	public interface Composer<C extends HasElement, S extends PropertyComponentSource> {

		/**
		 * Compose the components provided by given <code>source</code> into given <code>content</code>.
		 * @param content Content on which to compose the components (never null)
		 * @param source Components source (never null)
		 */
		void compose(C content, S source);

	}

	/**
	 * Create a {@link Composer} which uses a {@link HasComponents} component as composition layout and adds the
	 * property components to the parent component in the order they are returned from the
	 * {@link PropertyValueComponentSource}.
	 * @param <C> Actual ComponentContainer type
	 * @param <S> Actual components source
	 * @return A new {@link Composer}
	 */
	static <C extends HasComponents, S extends PropertyComponentSource> Composer<C, S> componentContainerComposer() {
		return new DefaultComponentContainerComposer<>();
	}

}
