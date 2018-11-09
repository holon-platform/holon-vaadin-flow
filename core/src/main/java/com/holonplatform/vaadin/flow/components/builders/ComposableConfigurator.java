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

import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.PropertyComponentSource;
import com.vaadin.flow.component.HasElement;

/**
 * {@link Composable} component configurator.
 *
 * @param <S> Components source type
 * @param <E> Content type
 * @param <C> Concrete builder type
 * 
 * @since 5.2.0
 */
public interface ComposableConfigurator<S extends PropertyComponentSource, E extends HasElement, C extends ComposableConfigurator<S, E, C>> {

	/**
	 * Set a content initializer to setup the content component. This initiliazer is called every time the content
	 * composition is triggered.
	 * @param initializer Content initializer (not null)
	 * @return this
	 */
	C initializer(Consumer<E> initializer);

	/**
	 * Set the {@link Composer} to be used to compose the components into the content component.
	 * @param composer The composer to set (not null)
	 * @return this
	 */
	C composer(Composer<? super E, S> composer);

	/**
	 * Set whether to compose the components into the content component when the content is attached to a parent
	 * component, only if the component was not already composed using {@link Composable#compose()}.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @param composeOnAttach <code>true</code> to compose the components when the content is attached to a parent
	 *        component. If <code>false</code>, the {@link Composable#compose()} method must be invoked to compose the
	 *        components.
	 * @return this
	 */
	C composeOnAttach(boolean composeOnAttach);

}
