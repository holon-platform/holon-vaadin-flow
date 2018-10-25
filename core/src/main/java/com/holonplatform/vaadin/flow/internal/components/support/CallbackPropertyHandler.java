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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.function.BiFunction;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.operation.TriConsumer;
import com.holonplatform.vaadin.flow.components.Input.PropertyHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * A {@link PropertyHandler} implementation using callback functions to get and set the property value.
 * 
 * @param <P> Property value type
 * @param <T> Input value type
 * @param <V> {@link HasValue} type
 * @param <C> {@link Component} type
 * 
 * @since 5.2.0
 */
public class CallbackPropertyHandler<P, T, V extends HasValue<?, T>, C extends Component>
		implements PropertyHandler<P, T, V, C> {

	private final BiFunction<V, C, P> getter;
	private final TriConsumer<V, C, P> setter;

	public CallbackPropertyHandler(BiFunction<V, C, P> getter, TriConsumer<V, C, P> setter) {
		super();
		ObjectUtils.argumentNotNull(getter, "The property value getter function must be not null");
		ObjectUtils.argumentNotNull(setter, "The property value setter function must be not null");
		this.getter = getter;
		this.setter = setter;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public P apply(V t, C u) {
		return getter.apply(t, u);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.operation.TriConsumer#accept(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void accept(V f, C s, P t) {
		setter.accept(f, s, t);
	}

}
