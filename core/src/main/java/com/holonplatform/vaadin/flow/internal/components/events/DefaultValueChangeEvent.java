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
package com.holonplatform.vaadin.flow.internal.components.events;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;

/**
 * Default {@link ValueChangeEvent} implementation.
 * 
 * @param <V> Value type
 *
 * @since 5.2.0
 */
public class DefaultValueChangeEvent<V> implements ValueChangeEvent<V> {

	private static final long serialVersionUID = 1L;

	private final ValueHolder<V, ?> source;
	private final V oldValue;
	private final V value;
	private final boolean userOriginated;

	/**
	 * Constructor.
	 * @param source Source (not null)
	 * @param oldValue Old value
	 * @param value New value
	 * @param userOriginated Whether is a client-side (user originated) change event or server side
	 */
	public DefaultValueChangeEvent(ValueHolder<V, ?> source, V oldValue, V value, boolean userOriginated) {
		super();
		ObjectUtils.argumentNotNull(source, "Source must be not null");
		this.source = source;
		this.oldValue = oldValue;
		this.value = value;
		this.userOriginated = userOriginated;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#isUserOriginated()
	 */
	@Override
	public boolean isUserOriginated() {
		return userOriginated;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#getSource()
	 */
	@Override
	public ValueHolder<V, ?> getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#getOldValue()
	 */
	@Override
	public V getOldValue() {
		return oldValue;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder.ValueChangeEvent#getValue()
	 */
	@Override
	public V getValue() {
		return value;
	}

}
