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

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.support.PropertyHandler;

/**
 * A {@link PropertyHandler} implementation using callback functions to get and set the property value.
 * 
 * @param <T> Property type
 * 
 * @since 5.2.0
 */
public class CallbackPropertyHandler<T> implements PropertyHandler<T> {

	private final Supplier<T> getter;
	private final Consumer<T> setter;

	public CallbackPropertyHandler(Supplier<T> getter, Consumer<T> setter) {
		super();
		ObjectUtils.argumentNotNull(getter, "The property value getter function must be not null");
		ObjectUtils.argumentNotNull(setter, "The property value setter function must be not null");
		this.getter = getter;
		this.setter = setter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.support.PropertyHandler#getPropertyValue()
	 */
	@Override
	public T getPropertyValue() {
		return getter.get();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.support.PropertyHandler#setPropertyValue(java.lang.Object)
	 */
	@Override
	public void setPropertyValue(T value) {
		setter.accept(value);
	}

}
