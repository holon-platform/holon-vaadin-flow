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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.ItemSelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.SingleSelectInputAdapter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.DataProvider;

/**
 * Default {@link ItemSelectModeSingleSelectInputBuilder} implementation.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public class DefaultSelectModeSingleSelectInputBuilder<T, ITEM>
		extends AbstractSelectModeSingleSelectInputBuilder<T, ITEM, ItemSelectModeSingleSelectInputBuilder<T, ITEM>>
		implements ItemSelectModeSingleSelectInputBuilder<T, ITEM> {

	/**
	 * Constructor.
	 * @param itemConverter The item converter to use (not null)
	 */
	public DefaultSelectModeSingleSelectInputBuilder(ItemConverter<T, ITEM, DataProvider<ITEM, ?>> itemConverter) {
		super(itemConverter);
	}

	@Override
	protected SingleSelect<T> buildSelect(ComboBox<ITEM> component,
			ItemConverter<T, ITEM, DataProvider<ITEM, ?>> itemConverter) {

		final Input<ITEM> itemInput = Input.builder(component)
				.requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.focusOperation(f -> f.focus()).hasEnabledSupplier(f -> f).build();

		final Input<T> input = Input.from(itemInput,
				Converter.from(item -> Result.ok(itemConverter.getValue(component.getDataProvider(), item)),
						value -> itemConverter.getItem(component.getDataProvider(), value)));
		valueChangeListeners.forEach(listener -> input.addValueChangeListener(listener));

		return new SingleSelectInputAdapter<>(input, () -> component.getDataProvider().refreshAll());
	}

	@Override
	protected ItemSelectModeSingleSelectInputBuilder<T, ITEM> getConfigurator() {
		return this;
	}

}
