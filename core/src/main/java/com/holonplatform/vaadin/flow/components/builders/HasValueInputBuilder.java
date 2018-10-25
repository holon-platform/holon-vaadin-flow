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

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasValueInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * A builder to create {@link Input} components from a {@link HasValue} component.
 * 
 * @param <T> Value type
 * @param <H> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 *
 * @since 5.2.0
 */
public interface HasValueInputBuilder<T, H extends HasValue<?, T>, C extends Component>
		extends InputAdapterBuilder<T, T, H, C, HasValueInputBuilder<T, H, C>> {

	// statics

	/**
	 * Create a new {@link HasValueInputBuilder} using given {@link HasValue} {@link Component} field instance.
	 * @param <T> Value type
	 * @param <H> Actual field type
	 * @param field {@link HasValue} {@link Component} field (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, H extends Component & HasValue<?, T>> HasValueInputBuilder<T, H, H> create(H field) {
		return new DefaultHasValueInputBuilder<>(field, field);
	}

	/**
	 * Create a new {@link HasValueInputBuilder} using given {@link HasValue} and {@link Component} field instances.
	 * @param <T> Value type
	 * @param <V> {@link HasValue} type
	 * @param <C> {@link Component} type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, V extends HasValue<?, T>, C extends Component> HasValueInputBuilder<T, V, C> create(V field,
			C component) {
		return new DefaultHasValueInputBuilder<>(field, component);
	}

}
