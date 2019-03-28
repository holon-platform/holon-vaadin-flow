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
package com.holonplatform.vaadin.flow.internal.converters;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * {@link ItemConverter} to {@link Converter} adapter.
 * 
 * @param <ITEM> Item type
 * @param <T> Value type
 *
 * @since 5.2.5
 */
public class ItemConverterConverter<ITEM, T> implements Converter<ITEM, T> {

	private static final long serialVersionUID = 8754357108936048024L;

	private final ItemConverter<T, ITEM> converter;

	public ItemConverterConverter(ItemConverter<T, ITEM> converter) {
		super();
		ObjectUtils.argumentNotNull(converter, "ItemConverter must be not null");
		this.converter = converter;
	}

	@Override
	public Result<T> convertToModel(ITEM value, ValueContext context) {
		return Result.ok(converter.getValue(value));
	}

	@Override
	public ITEM convertToPresentation(T value, ValueContext context) {
		return converter.getItem(value).orElse(null);
	}

}
