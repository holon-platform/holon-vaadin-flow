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

import java.util.function.Consumer;

import com.holonplatform.vaadin.flow.components.ComponentGroup;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.HasElement;

/**
 * {@link Composable} component configurator.
 *
 * @param <L> Composition layout type
 * @param <E> Group elements type
 * @param <G> Component group type
 * @param <C> Concrete builder type
 * 
 * @since 5.2.0
 */
public interface ComposableConfigurator<L extends HasElement, E extends HasComponent, G extends ComponentGroup<E>, C extends ComposableConfigurator<L, E, G, C>> {

	/**
	 * Set a content initializer to setup the content component. The initiliazer is called every time the content
	 * composition is triggered.
	 * @param initializer The content initializer to set (not null)
	 * @return this
	 */
	C initializer(Consumer<L> initializer);

	/**
	 * Set the {@link Composer} to be used to compose the components into the content component.
	 * @param composer The composer to set (not null)
	 * @return this
	 */
	C composer(Composer<? super L, E, G> composer);

	/**
	 * Set whether to compose the group elements into the content layout when the content component is attached to a
	 * parent component.
	 * <p>
	 * If <code>false</code> the {@link Composable#compose()} method must be invoked to compose the group elements.
	 * </p>
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @param composeOnAttach <code>true</code> to compose the group elements when the content is attached to a parent
	 *        component. If <code>false</code>, the {@link Composable#compose()} method must be invoked to compose the
	 *        components.
	 * @return this
	 */
	C composeOnAttach(boolean composeOnAttach);

}
