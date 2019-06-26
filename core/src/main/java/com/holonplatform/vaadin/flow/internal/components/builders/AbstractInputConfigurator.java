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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.holonplatform.vaadin.flow.components.builders.InputConfigurator;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.vaadin.flow.component.Component;

/**
 * Base {@link InputConfigurator} class.
 * 
 * @param <T> Value type
 * @param <E> Value change event type
 * @param <C> Input component type
 * @param <B> Concrete configurator type
 *
 * @since 5.2.5
 */
public abstract class AbstractInputConfigurator<T, E extends ValueChangeEvent<T>, C extends Component, B extends InputConfigurator<T, E, B> & DeferrableLocalizationConfigurator<B>>
		extends AbstractLocalizableComponentConfigurator<C, B> implements InputConfigurator<T, E, B> {

	private final List<ValueChangeListener<T, E>> valueChangeListeners = new LinkedList<>();
	private final List<ReadonlyChangeListener> readonlyChangeListeners = new LinkedList<>();

	private final InputAdaptersContainer<T> adapters;

	/**
	 * Constructor.
	 * @param component The input component (not null)
	 * @param adapters Input adapters container
	 */
	public AbstractInputConfigurator(C component, InputAdaptersContainer<T> adapters) {
		super(component);
		this.adapters = (adapters != null) ? adapters : InputAdaptersContainer.create();
	}

	@Override
	public B withValueChangeListener(ValueChangeListener<T, E> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	@Override
	public B withReadonlyChangeListener(ReadonlyChangeListener listener) {
		ObjectUtils.argumentNotNull(listener, "ReadonlyChangeListener must be not null");
		this.readonlyChangeListeners.add(listener);
		return getConfigurator();
	}

	@Override
	public <A> B withAdapter(Class<A> type, Function<Input<T>, A> adapter) {
		adapters.setAdapter(type, adapter);
		return getConfigurator();
	}

	/**
	 * Init the value change listeners.
	 * @param valueChangeListeners the listeners to add
	 */
	protected void initValueChangeListeners(Iterable<ValueChangeListener<T, E>> valueChangeListeners) {
		if (valueChangeListeners != null) {
			valueChangeListeners.forEach(l -> this.valueChangeListeners.add(l));
		}
	}

	/**
	 * Init the read-only change listeners.
	 * @param readonlyChangeListeners the listeners to add
	 */
	protected void initReadonlyChangeListeners(Iterable<ReadonlyChangeListener> readonlyChangeListeners) {
		if (readonlyChangeListeners != null) {
			readonlyChangeListeners.forEach(l -> this.readonlyChangeListeners.add(l));
		}
	}

	/**
	 * Get the configured value change listeners.
	 * @return the value change listeners
	 */
	protected List<ValueChangeListener<T, E>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	/**
	 * Get the configured read-only change listeners.
	 * @return the read-only change listeners
	 */
	protected List<ReadonlyChangeListener> getReadonlyChangeListeners() {
		return readonlyChangeListeners;
	}

	/**
	 * Get the configured adapters.
	 * @return The input adapters
	 */
	protected InputAdaptersContainer<T> getAdapters() {
		return adapters;
	}

}
