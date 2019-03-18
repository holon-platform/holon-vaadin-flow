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

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.PropertyFilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.data.PropertyItemConverter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Default {@link PropertyFilterableSingleSelectInputBuilder} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultPropertyFilterableSingleSelectInputBuilder<T>
		implements PropertyFilterableSingleSelectInputBuilder<T> {

	private final FilterableSingleSelectInputBuilder<T, PropertyBox> builder;

	private final Property<T> selectionProperty;

	private PropertyItemConverter<T> propertyItemConverter;

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 */
	public DefaultPropertyFilterableSingleSelectInputBuilder(final Property<T> selectionProperty) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding selection item
	 */
	public DefaultPropertyFilterableSingleSelectInputBuilder(Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty, itemConverter));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter the item converter to use (not null)
	 */
	protected DefaultPropertyFilterableSingleSelectInputBuilder(Property<T> selectionProperty,
			ItemConverter<T, PropertyBox> itemConverter) {
		super();
		ObjectUtils.argumentNotNull(selectionProperty, "Selection property must be not null");
		this.selectionProperty = selectionProperty;
		this.builder = new DefaultFilterableSingleSelectInputBuilder<>(selectionProperty.getType(),
				PropertyBox.class, itemConverter);
		this.builder.itemCaptionGenerator(item -> {
			if (item != null) {
				return selectionProperty.present(item.getValue(selectionProperty));
			}
			return String.valueOf(item);
		});
		if (itemConverter instanceof PropertyItemConverter) {
			this.propertyItemConverter = (PropertyItemConverter<T>) itemConverter;
		}
	}

	/**
	 * Setup a item converter function if the current item converter is a {@link PropertyItemConverter}, using the
	 * selection property to retrieve an item.
	 * @param datastore The datastore
	 * @param target The query target
	 * @param propertySet The query projection
	 */
	protected void setupDatastoreItemConverter(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet) {
		if (propertyItemConverter != null && propertyItemConverter.getToItemConverter() == null) {
			propertyItemConverter.setToItemConverter(value -> {
				return (value == null) ? Optional.empty()
						: datastore.query(target).filter(QueryFilter.eq(selectionProperty, value)).findOne(propertySet);
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPropertyBoxDatastoreFilterableDataProviderConfigurator#
	 * dataSource(com.holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(Datastore datastore,
			DataTarget<?> target, Iterable<P> properties) {
		@SuppressWarnings("unchecked")
		final DatastoreDataProvider<PropertyBox, String> datastoreDataProvider = DatastoreDataProvider.create(datastore,
				target, DatastoreDataProvider.asPropertySet(properties), value -> {
					if (TypeUtils.isString(selectionProperty.getType())) {
						return QueryFilter.startsWith((TypedExpression<String>) selectionProperty, value, true);
					}
					return null;
				});
		builder.dataSource(datastoreDataProvider);
		setupDatastoreItemConverter(datastore, target, DatastoreDataProvider.asPropertySet(properties));
		return new DefaultDatastorePropertyFilterableSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPropertyBoxDatastoreFilterableDataProviderConfigurator#
	 * dataSource(com.holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
	 * java.util.function.Function, java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(Datastore datastore,
			DataTarget<?> target, Function<String, QueryFilter> filterConverter, Iterable<P> properties) {
		final DatastoreDataProvider<PropertyBox, String> datastoreDataProvider = DatastoreDataProvider.create(datastore,
				target, DatastoreDataProvider.asPropertySet(properties), filterConverter);
		builder.dataSource(datastoreDataProvider);
		setupDatastoreItemConverter(datastore, target, DatastoreDataProvider.asPropertySet(properties));
		return new DefaultDatastorePropertyFilterableSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
	 * PropertySelectModeSingleSelectInputBuilder#dataSource(com.holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(Datastore datastore,
			DataTarget<?> target) {
		return dataSource(datastore, target, Collections.singletonList(selectionProperty));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
	 * PropertySelectModeSingleSelectInputBuilder#dataSource(com.holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function)
	 */
	@Override
	public DatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target,
			Function<String, QueryFilter> filterConverter) {
		return dataSource(datastore, target, filterConverter, Collections.singletonList(selectionProperty));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
		builder.withSelectionListener(selectionListener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#renderer(com.vaadin.flow.
	 * data.renderer.Renderer)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> renderer(Renderer<PropertyBox> renderer) {
		builder.renderer(renderer);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> pageSize(int pageSize) {
		builder.pageSize(pageSize);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com.
	 * holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> itemCaptionGenerator(
			ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
		builder.itemCaptionGenerator(itemCaptionGenerator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.PropertySelectInputConfigurator#itemCaptionProperty(com.
	 * holonplatform.core.property.Property)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return itemCaptionGenerator(item -> Objects.toString(item.getValue(property), ""));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
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
	public ValidatablePropertyFilterableSingleSelectInputBuilder<T> validatable() {
		return new DefaultValidatablePropertyFilterableSingleSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
	public PropertyFilterableSingleSelectInputBuilder<T> withValueChangeListener(
			ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		builder.withValueChangeListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> visible(boolean visible) {
		builder.visible(visible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.function.
	 * Consumer)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
		builder.elementConfiguration(element);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> withAttachListener(
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
	public PropertyFilterableSingleSelectInputBuilder<T> withDetachListener(
			ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
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
	public PropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener,
			String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> width(String width) {
		builder.width(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> height(String height) {
		builder.height(height);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> minWidth(String minWidth) {
		builder.minWidth(minWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
		builder.maxWidth(maxWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> minHeight(String minHeight) {
		builder.minHeight(minHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
		builder.maxHeight(maxHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
	public PropertyFilterableSingleSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
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
	public PropertyFilterableSingleSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, String> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasFilterableDataSourceConfigurator#dataSource(com.vaadin.flow.
	 * data.provider.DataProvider, com.vaadin.flow.function.SerializableFunction)
	 */
	@Override
	public <FILTER> PropertyFilterableSingleSelectInputBuilder<T> dataSource(
			DataProvider<PropertyBox, FILTER> dataProvider, SerializableFunction<String, FILTER> filterConverter) {
		builder.dataSource(dataProvider, filterConverter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
		builder.items(items);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> addItem(PropertyBox item) {
		builder.addItem(item);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> label(Localizable label) {
		builder.label(label);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
		builder.placeholder(placeholder);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> pattern(String pattern) {
		builder.pattern(pattern);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> preventInvalidInput(boolean preventInvalidInput) {
		builder.preventInvalidInput(preventInvalidInput);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
		builder.autofocus(autofocus);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
		builder.tabIndex(tabIndex);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> withFocusListener(
			ComponentEventListener<FocusEvent<Component>> listener) {
		builder.withFocusListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyFilterableSingleSelectInputBuilder<T> withBlurListener(
			ComponentEventListener<BlurEvent<Component>> listener) {
		builder.withBlurListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusShortcut(com.vaadin.flow.
	 * component.Key)
	 */
	@Override
	public ShortcutConfigurator<PropertyFilterableSingleSelectInputBuilder<T>> withFocusShortcut(Key key) {
		return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
	}

	// ------- extended builders

	static class DefaultValidatablePropertyFilterableSingleSelectInputBuilder<T>
			implements ValidatablePropertyFilterableSingleSelectInputBuilder<T> {

		private final PropertyFilterableSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatablePropertyFilterableSingleSelectInputBuilder(
				PropertyFilterableSingleSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target, Function<String, QueryFilter> filterConverter) {
			return new DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, filterConverter));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.Renderer)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> renderer(Renderer<PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> pageSize(int pageSize) {
			builder.pageSize(pageSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
			builder.readOnly(readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> elementConfiguration(
				Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withAttachListener(
				ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization
		 * (boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withDeferredLocalization(
				boolean deferredLocalization) {
			builder.withDeferredLocalization(deferredLocalization);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return builder.isDeferredLocalizationEnabled();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> pattern(String pattern) {
			builder.pattern(pattern);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> preventInvalidInput(
				boolean preventInvalidInput) {
			builder.preventInvalidInput(preventInvalidInput);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
			builder.tabIndex(tabIndex);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withFocusListener(
				ComponentEventListener<FocusEvent<Component>> listener) {
			builder.withFocusListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withBlurListener(
				ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusShortcut(com.vaadin.flow.
		 * component.Key)
		 */
		@Override
		public ShortcutConfigurator<ValidatablePropertyFilterableSingleSelectInputBuilder<T>> withFocusShortcut(
				Key key) {
			return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasPropertyBoxDatastoreFilterableDataProviderConfigurator#
		 * dataSource(com.holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
		 * java.util.function.Function, java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Function<String, QueryFilter> filterConverter,
				Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, filterConverter, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasFilterableDataProviderConfigurator#dataSource(com.vaadin
		 * .flow.data.provider.DataProvider)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				DataProvider<PropertyBox, String> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasFilterableDataProviderConfigurator#dataSource(com.vaadin
		 * .flow.data.provider.DataProvider, com.vaadin.flow.function.SerializableFunction)
		 */
		@Override
		public <F> ValidatablePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				DataProvider<PropertyBox, F> dataProvider, SerializableFunction<String, F> filterConverter) {
			builder.dataSource(dataProvider, filterConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> addItem(PropertyBox item) {
			builder.addItem(item);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
			validatableInputConfigurator.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
			validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> validateOnValueChange(
				boolean validateOnValueChange) {
			validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.
		 * core.Validator)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> required(Validator<T> validator) {
			validatableInputConfigurator.required(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyFilterableSingleSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder#build()
		 */
		@Override
		public ValidatableSingleSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableSingleSelect.from(builder.build()));
		}

	}

	static class DefaultDatastorePropertyFilterableSingleSelectInputBuilder<T>
			implements DatastorePropertyFilterableSingleSelectInputBuilder<T> {

		private final DefaultPropertyFilterableSingleSelectInputBuilder<T> builder;
		private final DatastoreDataProvider<PropertyBox, String> datastoreDataProvider;

		public DefaultDatastorePropertyFilterableSingleSelectInputBuilder(
				DefaultPropertyFilterableSingleSelectInputBuilder<T> builder,
				DatastoreDataProvider<PropertyBox, String> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * DatastorePropertySelectModeSingleSelectInputBuilder#filterConverter(java.util.function.Function)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> filterConverter(
				Function<String, QueryFilter> filterConverter) {
			this.datastoreDataProvider.setFilterConverter(filterConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.Renderer)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> renderer(Renderer<PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> pageSize(int pageSize) {
			builder.pageSize(pageSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> validatable() {
			return new DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
			builder.readOnly(readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> required(boolean required) {
			builder.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withAttachListener(
				ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization
		 * (boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withDeferredLocalization(
				boolean deferredLocalization) {
			builder.withDeferredLocalization(deferredLocalization);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return builder.isDeferredLocalizationEnabled();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> pattern(String pattern) {
			builder.pattern(pattern);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> preventInvalidInput(boolean preventInvalidInput) {
			builder.preventInvalidInput(preventInvalidInput);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
			builder.tabIndex(tabIndex);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withFocusListener(
				ComponentEventListener<FocusEvent<Component>> listener) {
			builder.withFocusListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withBlurListener(
				ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusShortcut(com.vaadin.flow.
		 * component.Key)
		 */
		@Override
		public ShortcutConfigurator<DatastorePropertyFilterableSingleSelectInputBuilder<T>> withFocusShortcut(Key key) {
			return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#withDefaultQuerySort(com.
		 * holonplatform.core.query.QuerySort)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
			datastoreDataProvider.setDefaultSort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#itemIdentifierProvider(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> itemIdentifierProvider(
				Function<PropertyBox, Object> itemIdentifierProvider) {
			datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#querySortOrderConverter(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyFilterableSingleSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
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

	}

	static class DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T>
			implements ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> {

		private final DatastorePropertyFilterableSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableDatastorePropertyFilterableSingleSelectInputBuilder(
				DatastorePropertyFilterableSingleSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			builder.withQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#withDefaultQuerySort(com.
		 * holonplatform.core.query.QuerySort)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withDefaultQuerySort(
				QuerySort defaultQuerySort) {
			builder.withDefaultQuerySort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#itemIdentifierProvider(
		 * java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> itemIdentifierProvider(
				Function<PropertyBox, Object> itemIdentifierProvider) {
			builder.itemIdentifierProvider(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#querySortOrderConverter(
		 * java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * DatastorePropertySelectModeSingleSelectInputConfigurator#filterConverter(java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> filterConverter(
				Function<String, QueryFilter> filterConverter) {
			builder.filterConverter(filterConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.Renderer)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> renderer(
				Renderer<PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> pageSize(int pageSize) {
			builder.pageSize(pageSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
			builder.readOnly(readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> elementConfiguration(
				Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withAttachListener(
				ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization
		 * (boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withDeferredLocalization(
				boolean deferredLocalization) {
			builder.withDeferredLocalization(deferredLocalization);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return builder.isDeferredLocalizationEnabled();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> pattern(String pattern) {
			builder.pattern(pattern);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> preventInvalidInput(
				boolean preventInvalidInput) {
			builder.preventInvalidInput(preventInvalidInput);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
			builder.tabIndex(tabIndex);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withFocusListener(
				ComponentEventListener<FocusEvent<Component>> listener) {
			builder.withFocusListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withBlurListener(
				ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusShortcut(com.vaadin.flow.
		 * component.Key)
		 */
		@Override
		public ShortcutConfigurator<ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T>> withFocusShortcut(
				Key key) {
			return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> itemCaptionProperty(
				Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
			validatableInputConfigurator.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
			validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> validateOnValueChange(
				boolean validateOnValueChange) {
			validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.
		 * core.Validator)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> required(Validator<T> validator) {
			validatableInputConfigurator.required(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#required(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyFilterableSingleSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder#build()
		 */
		@Override
		public ValidatableSingleSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableSingleSelect.from(builder.build()));
		}

	}

}
