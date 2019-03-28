/*
 * Copyright 2016-2019 Axioma srl.
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
package com.holonplatform.vaadin.flow.components.support;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.support.DefaultInputAdaptersContainer;

/**
 * Input adapters container.
 * 
 * @param <T> Input type
 *
 * @since 5.2.5
 */
public interface InputAdaptersContainer<T> extends Serializable {

	/**
	 * Set the adapter for given type.
	 * @param <A> Adapter type
	 * @param type Adapter type (not null)
	 * @param adapter Adapter function
	 */
	<A> void setAdapter(Class<A> type, Function<Input<T>, A> adapter);

	/**
	 * Get the input adapted to given type.
	 * @param <A> Adapter type
	 * @param input The input
	 * @param type The adapter type (not null)
	 * @return Optional adapted instance
	 */
	<A> Optional<A> getAs(Input<T> input, Class<A> type);

	/**
	 * Get the available adapters.
	 * @param <A> Adapter type
	 * @return A map of type and adapter, empty if none
	 */
	<A> Map<Class<A>, Function<Input<T>, A>> getAdapters();

	/**
	 * Create a new {@link InputAdaptersContainer}.
	 * @param <T> Input type
	 * @return A new {@link InputAdaptersContainer} instance
	 */
	static <T> InputAdaptersContainer<T> create() {
		return new DefaultInputAdaptersContainer<>();
	}

}
