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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.InputConverterBuilder;
import com.holonplatform.vaadin.flow.internal.components.InputConverterAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.data.converter.Converter;

/**
 * Default {@link InputConverterBuilder} implementation.
 * 
 * @param <T> Presentation value type
 * @param <V> Model value type
 *
 * @since 5.2.5
 */
public class DefaultInputConverterBuilder<T, V> extends
		AbstractComponentConfigurator<Component, InputConverterBuilder<T, V>> implements InputConverterBuilder<T, V> {

	private final InputConverterAdapter<V, T> input;

	/**
	 * Constructor.
	 * @param input The actual Input (not null)
	 * @param converter The value converter (not null)
	 */
	public DefaultInputConverterBuilder(Input<V> input, Converter<V, T> converter) {
		super(input.getComponent());
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		ObjectUtils.argumentNotNull(converter, "Converter must be not null");
		this.input = new InputConverterAdapter<>(input, converter);
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.empty();
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.empty();
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.empty();
	}

	@Override
	protected InputConverterBuilder<T, V> getConfigurator() {
		return this;
	}

	@Override
	public InputConverterBuilder<T, V> readOnly(boolean readOnly) {
		input.setReadOnly(readOnly);
		return this;
	}

	@Override
	public InputConverterBuilder<T, V> withValueChangeListener(ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		input.addValueChangeListener(listener);
		return this;
	}

	@Override
	public <A> InputConverterBuilder<T, V> withAdapter(Class<A> type, Function<Input<T>, A> adapter) {
		input.setAdapter(type, adapter);
		return this;
	}

	@Override
	public InputConverterBuilder<T, V> required(boolean required) {
		input.setRequired(required);
		return this;
	}

	@Override
	public InputConverterBuilder<T, V> required() {
		return required(true);
	}

	@Override
	public InputConverterBuilder<T, V> withValueChangeListeners(
			Collection<ValueChangeListener<T, ValueChangeEvent<T>>> listeners) {
		if (listeners != null) {
			listeners.forEach(l -> input.addValueChangeListener(l));
		}
		return this;
	}

	@Override
	public <A> InputConverterBuilder<T, V> withAdapters(Map<Class<A>, Function<Input<T>, A>> adapters) {
		if (adapters != null) {
			adapters.forEach((t, a) -> input.setAdapter(t, a));
		}
		return this;
	}

	@Override
	public Input<T> build() {
		return input;
	}

}
