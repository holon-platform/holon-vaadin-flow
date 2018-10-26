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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.function.BiFunction;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultItemSelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertySelectModeSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.Renderer;

/**
 * Base {@link SingleSelect} input builder for the <em>select</em> rendering mode.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface SelectModeSingleSelectInputBuilder<T, ITEM, B extends SelectModeSingleSelectInputBuilder<T, ITEM, B>>
		extends SingleSelectInputBuilder<T, ITEM, DataProvider<ITEM, ?>, B>, HasLabelConfigurator<B>,
		HasPlaceholderConfigurator<B>, HasPatternConfigurator<B>, HasAutofocusConfigurator<B>,
		FocusableConfigurator<Component, B>, HasFilterableDataSourceConfigurator<ITEM, String, B> {

	/**
	 * Sets the Renderer responsible to render the individual items in the list of possible choices.
	 * <p>
	 * It doesn't affect how the selected item is rendered - that can be configured by using
	 * {@link #itemCaptionGenerator(ItemCaptionGenerator)}.
	 * </p>
	 * @param renderer a renderer for the items in the selection list, not <code>null</code>
	 * @return this
	 */
	B renderer(Renderer<ITEM> renderer);

	/**
	 * Set the generator to be used to display item captions (i.e. labels).
	 * @param itemCaptionGenerator The generator to set (not null)
	 * @return this
	 */
	B itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator);

	/**
	 * Set an explicit caption for given item.
	 * <p>
	 * This is an alternative for {@link #itemCaptionGenerator(ItemCaptionGenerator)}. When an
	 * {@link ItemCaptionGenerator} is configured, explicit item captions will be ignored.
	 * </p>
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption (not null)
	 * @return this
	 */
	B itemCaption(ITEM item, Localizable caption);

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption
	 * @return this
	 */
	default B itemCaption(ITEM item, String caption) {
		return itemCaption(item, Localizable.builder().message(caption).build());
	}

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for (not null)
	 * @param caption Item caption
	 * @param messageCode Item caption translation code
	 * @return this
	 */
	default B itemCaption(ITEM item, String caption, String messageCode) {
		return itemCaption(item, Localizable.builder().message(caption).messageCode(messageCode).build());
	}

	/**
	 * {@link SingleSelect} input builder for the <em>select</em> rendering mode.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ItemSelectModeSingleSelectInputBuilder<T, ITEM>
			extends SelectModeSingleSelectInputBuilder<T, ITEM, ItemSelectModeSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder for the <em>select</em> rendering mode.
	 *
	 * @param <T> Value type
	 */
	public interface PropertySelectModeSingleSelectInputBuilder<T>
			extends SelectModeSingleSelectInputBuilder<T, PropertyBox, PropertySelectModeSingleSelectInputBuilder<T>>,
			HasFilterablePropertyDataSourceConfigurator<String, PropertySelectModeSingleSelectInputBuilder<T>> {

	}

	// builders

	/**
	 * Create a new {@link ItemSelectModeSingleSelectInputBuilder}.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemSelectModeSingleSelectInputBuilder<T, ITEM> create(
			ItemConverter<T, ITEM, DataProvider<ITEM, ?>> itemConverter) {
		return new DefaultItemSelectModeSingleSelectInputBuilder<>(itemConverter);
	}

	/**
	 * Create a new {@link ItemSelectModeSingleSelectInputBuilder}.
	 * @param <T> Value type
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T> ItemSelectModeSingleSelectInputBuilder<T, T> create() {
		return new DefaultItemSelectModeSingleSelectInputBuilder<>(ItemConverter.identity());
	}

	// property

	/**
	 * Create a new {@link PropertySelectModeSingleSelectInputBuilder} using given selection {@link Property} and
	 * converter.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertySelectModeSingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Create a new {@link PropertySelectModeSingleSelectInputBuilder} using given selection {@link Property}.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			BiFunction<DataProvider<PropertyBox, ?>, T, PropertyBox> itemConverter) {
		return new DefaultPropertySelectModeSingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
