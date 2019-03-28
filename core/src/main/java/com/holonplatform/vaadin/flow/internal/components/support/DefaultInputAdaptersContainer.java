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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;

/**
 * Default {@link InputAdaptersContainer} implementation.
 *
 * @param <T> Input type
 *
 * @since 5.2.5
 */
public class DefaultInputAdaptersContainer<T> implements InputAdaptersContainer<T> {

	private static final long serialVersionUID = -7264567264012024353L;

	private Map<Class<?>, Function<Input<T>, Object>> adapters;

	@SuppressWarnings({ "cast", "unchecked", "rawtypes" })
	@Override
	public <A> void setAdapter(Class<A> type, Function<Input<T>, A> adapter) {
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		if (adapter != null) {
			if (adapters == null) {
				adapters = new HashMap<>(4);
			}
			adapters.put(type, (Function<Input<T>, Object>) (Function) adapter);
		} else {
			if (adapters != null) {
				adapters.remove(type);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A> Optional<A> getAs(Input<T> input, Class<A> type) {
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		if (adapters != null) {
			final Function<Input<T>, Object> adapter = adapters.get(type);
			if (adapter != null) {
				final Object value = adapter.apply(input);
				if (value != null) {
					return Optional.of((A) value);
				}
			}
		}
		return Optional.empty();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <A> Map<Class<A>, Function<Input<T>, A>> getAdapters() {
		return (adapters != null) ? (Map) adapters : Collections.emptyMap();
	}

}
