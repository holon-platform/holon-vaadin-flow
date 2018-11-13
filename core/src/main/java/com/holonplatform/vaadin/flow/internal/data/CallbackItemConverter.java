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
package com.holonplatform.vaadin.flow.internal.data;

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.data.ItemConverter;

/**
 * {@link ItemConverter} implementation using callback functions.
 * 
 * @param <T> Value type
 * @param <ITEM> Item type
 * 
 * @since 5.2.0
 */
public class CallbackItemConverter<T, ITEM> implements ItemConverter<T, ITEM> {

	private final Function<ITEM, T> toValue;
	private final Function<T, Optional<ITEM>> toItem;

	public CallbackItemConverter(Function<ITEM, T> toValue, Function<T, Optional<ITEM>> toItem) {
		super();
		ObjectUtils.argumentNotNull(toValue, "Conversion to value function must be not null");
		ObjectUtils.argumentNotNull(toItem, "Conversion to item function must be not null");
		this.toValue = toValue;
		this.toItem = toItem;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemConverter#getValue(java.lang.Object)
	 */
	@Override
	public T getValue(ITEM item) {
		return toValue.apply(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.data.ItemConverter#getItem(java.lang.Object)
	 */
	@Override
	public Optional<ITEM> getItem(T value) {
		return toItem.apply(value);
	}

}
