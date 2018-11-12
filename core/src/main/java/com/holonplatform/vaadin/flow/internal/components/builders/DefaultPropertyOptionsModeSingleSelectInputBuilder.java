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

import java.util.function.BiFunction;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.PropertyOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.data.PropertyItemConverter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * Default {@link PropertyOptionsModeSingleSelectInputBuilder} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultPropertyOptionsModeSingleSelectInputBuilder<T>
		implements PropertyOptionsModeSingleSelectInputBuilder<T> {

	private final ItemOptionsModeSingleSelectInputBuilder<T, PropertyBox> builder;

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 */
	public DefaultPropertyOptionsModeSingleSelectInputBuilder(final Property<T> selectionProperty) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param toItem The function to use to convert a selection value into the corresponding selection item
	 */
	public DefaultPropertyOptionsModeSingleSelectInputBuilder(Property<T> selectionProperty,
			BiFunction<DataProvider<PropertyBox, ?>, T, PropertyBox> itemConverter) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty, itemConverter));
	}

	/**
	 * Constructor.
	 * @param itemConverter Item converter to use (not null)
	 */
	protected DefaultPropertyOptionsModeSingleSelectInputBuilder(Property<T> selectionProperty,
			ItemConverter<T, PropertyBox, DataProvider<PropertyBox, ?>> itemConverter) {
		super();
		ObjectUtils.argumentNotNull(selectionProperty, "Selection property must be not null");
		this.builder = new DefaultItemOptionsModeSingleSelectInputBuilder<>(selectionProperty.getType(),
				PropertyBox.class, itemConverter);
		this.builder.itemCaptionGenerator(item -> {
			if (item != null) {
				return selectionProperty.present(item.getValue(selectionProperty));
			}
			return String.valueOf(item);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withSelectionListener(
			SelectionListener<T> selectionListener) {
		builder.withSelectionListener(selectionListener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#renderer(com.vaadin.flow.
	 * data.renderer.ComponentRenderer)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> renderer(
			ComponentRenderer<? extends Component, PropertyBox> renderer) {
		builder.renderer(renderer);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com.
	 * vaadin.flow.function.SerializablePredicate)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> itemEnabledProvider(
			SerializablePredicate<PropertyBox> itemEnabledProvider) {
		builder.itemEnabledProvider(itemEnabledProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com.
	 * holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> itemCaptionGenerator(
			ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
		builder.itemCaptionGenerator(itemCaptionGenerator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
		builder.itemCaption(item, caption);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public SingleSelect<T> build() {
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> validatable() {
		return builder.validatable();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
		builder.readOnly(readOnly);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withValueChangeListener(ValueChangeListener<T> listener) {
		builder.withValueChangeListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> visible(boolean visible) {
		builder.visible(visible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withAttachListener(
			ComponentEventListener<AttachEvent> listener) {
		builder.withAttachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withDetachListener(
			ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withEventListener(String eventType,
			DomEventListener listener) {
		builder.withEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener,
			String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> removeStyleName(String styleName) {
		builder.removeStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> replaceStyleName(String styleName) {
		builder.replaceStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> enabled(boolean enabled) {
		builder.enabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization(
	 * boolean)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
		builder.withDeferredLocalization(deferredLocalization);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
	 */
	@Override
	public boolean isDeferredLocalizationEnabled() {
		return builder.isDeferredLocalizationEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasItemsDataSourceConfigurator#dataSource(com.vaadin.flow.data.
	 * provider.DataProvider)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, Object> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
		builder.items(items);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> addItem(PropertyBox item) {
		builder.addItem(item);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public PropertyOptionsModeSingleSelectInputBuilder<T> label(Localizable label) {
		builder.label(label);
		return this;
	}

}
