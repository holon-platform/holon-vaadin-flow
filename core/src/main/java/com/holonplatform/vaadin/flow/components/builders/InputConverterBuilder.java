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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultInputConverterBuilder;
import com.vaadin.flow.data.converter.Converter;

/**
 * Input converter builder.
 *
 * @param <T> Presentation value type
 * @param <V> Model value type
 *
 * @since 5.2.5
 */
public interface InputConverterBuilder<T, V>
		extends InputConfigurator<T, ValueChangeEvent<T>, InputConverterBuilder<T, V>> {

	/**
	 * Add a set of {@link ValueChangeListener}s to be notified when the input value changes.
	 * @param listeners The {@link ValueChangeListener}s to add (not null)
	 * @return this
	 */
	InputConverterBuilder<T, V> withValueChangeListeners(
			Collection<ValueChangeListener<T, ValueChangeEvent<T>>> listeners);

	/**
	 * Add a set of adapters.
	 * @param <A> Adapter type
	 * @param adapters The adapters to add
	 * @return this
	 */
	<A> InputConverterBuilder<T, V> withAdapters(Map<Class<A>, Function<Input<T>, A>> adapters);

	/**
	 * Add a set of adapters.
	 * @param adapters The adapters to add
	 * @return this
	 */
	default InputConverterBuilder<T, V> withAdapters(InputAdaptersContainer<T> adapters) {
		return withAdapters((adapters != null) ? adapters.getAdapters() : Collections.emptyMap());
	}

	/**
	 * Build and returns the {@link Input} instance.
	 * @return the {@link Input} instance
	 */
	Input<T> build();

	/**
	 * Create a new {@link InputConverterBuilder}.
	 * @param <T> Presentation value type
	 * @param <V> Model value type
	 * @param input The original input (not null)
	 * @param converter The value converter (not null)
	 * @return A new {@link InputConverterBuilder}
	 */
	static <T, V> InputConverterBuilder<T, V> create(Input<V> input, Converter<V, T> converter) {
		return new DefaultInputConverterBuilder<>(input, converter);
	}

}
