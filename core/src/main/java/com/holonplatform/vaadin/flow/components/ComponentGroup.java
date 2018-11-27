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
package com.holonplatform.vaadin.flow.components;

import java.io.Serializable;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;

/**
 * A group of {@link HasComponent} type elements, which provides the actual {@link Component} through the
 * {@link HasComponent#getComponent()} method.
 * <p>
 * The elements of the group can be obtained using {@link #getElements()}.
 * </p>
 * 
 * @param <C> Group element type
 *
 * @since 5.2.0
 */
public interface ComponentGroup<C extends HasComponent> extends Serializable {

	/**
	 * Get the stream of the available group elements.
	 * @return the group elements stream
	 */
	Stream<C> getElements();

	/**
	 * Get the stream of the available group components.
	 * @return the group components stream
	 */
	default Stream<Component> getComponents() {
		return getElements().map(e -> e.getComponent());
	}

}
