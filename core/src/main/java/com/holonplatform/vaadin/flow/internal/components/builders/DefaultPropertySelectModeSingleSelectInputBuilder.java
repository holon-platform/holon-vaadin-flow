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
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.PropertySelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.data.PropertyItemConverter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Default {@link PropertySelectModeSingleSelectInputBuilder} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultPropertySelectModeSingleSelectInputBuilder<T>
		implements PropertySelectModeSingleSelectInputBuilder<T> {

	private final ItemSelectModeSingleSelectInputBuilder<T, PropertyBox> builder;

	private final Property<T> selectionProperty;

	private PropertyItemConverter<T> propertyItemConverter;

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 */
	public DefaultPropertySelectModeSingleSelectInputBuilder(final Property<T> selectionProperty) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding selection item
	 */
	public DefaultPropertySelectModeSingleSelectInputBuilder(Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty, itemConverter));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter the item converter to use (not null)
	 */
	protected DefaultPropertySelectModeSingleSelectInputBuilder(Property<T> selectionProperty,
			ItemConverter<T, PropertyBox> itemConverter) {
		super();
		ObjectUtils.argumentNotNull(selectionProperty, "Selection property must be not null");
		this.selectionProperty = selectionProperty;
		this.builder = new DefaultItemSelectModeSingleSelectInputBuilder<>(selectionProperty.getType(),
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
	public <P extends Property> DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
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
		return new DefaultDatastorePropertySelectModeSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPropertyBoxDatastoreFilterableDataProviderConfigurator#
	 * dataSource(com.holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
	 * java.util.function.Function, java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
			DataTarget<?> target, Function<String, QueryFilter> filterConverter, Iterable<P> properties) {
		final DatastoreDataProvider<PropertyBox, String> datastoreDataProvider = DatastoreDataProvider.create(datastore,
				target, DatastoreDataProvider.asPropertySet(properties), filterConverter);
		builder.dataSource(datastoreDataProvider);
		setupDatastoreItemConverter(datastore, target, DatastoreDataProvider.asPropertySet(properties));
		return new DefaultDatastorePropertySelectModeSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
	 * PropertySelectModeSingleSelectInputBuilder#dataSource(com.holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
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
	public DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target,
			Function<String, QueryFilter> filterConverter) {
		return dataSource(datastore, target, filterConverter, Collections.singletonList(selectionProperty));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> renderer(Renderer<PropertyBox> renderer) {
		builder.renderer(renderer);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> pageSize(int pageSize) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> itemCaptionGenerator(
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
	public PropertySelectModeSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return itemCaptionGenerator(item -> Objects.toString(item.getValue(property), ""));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
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
	public ValidatablePropertySelectModeSingleSelectInputBuilder<T> validatable() {
		return new DefaultValidatablePropertySelectModeSingleSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> withValueChangeListener(
			ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		builder.withValueChangeListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> visible(boolean visible) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
		builder.elementConfiguration(element);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> withAttachListener(
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
	public PropertySelectModeSingleSelectInputBuilder<T> withDetachListener(
			ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
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
	public PropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener,
			String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> width(String width) {
		builder.width(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> height(String height) {
		builder.height(height);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> minWidth(String minWidth) {
		builder.minWidth(minWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
		builder.maxWidth(maxWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> minHeight(String minHeight) {
		builder.minHeight(minHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
		builder.maxHeight(maxHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, String> dataProvider) {
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
	public <FILTER> PropertySelectModeSingleSelectInputBuilder<T> dataSource(
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
	public PropertySelectModeSingleSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
		builder.items(items);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> addItem(PropertyBox item) {
		builder.addItem(item);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> label(Localizable label) {
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
	public PropertySelectModeSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
		builder.placeholder(placeholder);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> pattern(String pattern) {
		builder.pattern(pattern);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> preventInvalidInput(boolean preventInvalidInput) {
		builder.preventInvalidInput(preventInvalidInput);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
		builder.autofocus(autofocus);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
		builder.tabIndex(tabIndex);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertySelectModeSingleSelectInputBuilder<T> withFocusListener(
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
	public PropertySelectModeSingleSelectInputBuilder<T> withBlurListener(
			ComponentEventListener<BlurEvent<Component>> listener) {
		builder.withBlurListener(listener);
		return this;
	}

	// ------- extended builders

	static class DefaultValidatablePropertySelectModeSingleSelectInputBuilder<T>
			implements ValidatablePropertySelectModeSingleSelectInputBuilder<T> {

		private final PropertySelectModeSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatablePropertySelectModeSingleSelectInputBuilder(
				PropertySelectModeSingleSelectInputBuilder<T> builder) {
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
		public <P extends Property> ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target, Function<String, QueryFilter> filterConverter) {
			return new DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, filterConverter));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.Renderer)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> renderer(Renderer<PropertyBox> renderer) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> itemCaptionGenerator(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> pageSize(int pageSize) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> dataSource(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> elementConfiguration(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withAttachListener(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withThemeName(String themeName) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withDeferredLocalization(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> label(Localizable label) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> pattern(String pattern) {
			builder.pattern(pattern);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> preventInvalidInput(
				boolean preventInvalidInput) {
			builder.preventInvalidInput(preventInvalidInput);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withFocusListener(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withBlurListener(
				ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
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
		public <P extends Property> ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Function<String, QueryFilter> filterConverter,
				Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, filterConverter, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasFilterableDataProviderConfigurator#dataSource(com.vaadin
		 * .flow.data.provider.DataProvider)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> dataSource(
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
		public <F> ValidatablePropertySelectModeSingleSelectInputBuilder<T> dataSource(
				DataProvider<PropertyBox, F> dataProvider, SerializableFunction<String, F> filterConverter) {
			builder.dataSource(dataProvider, filterConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> addItem(PropertyBox item) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> validationStatusHandler(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> validateOnValueChange(
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> required(Validator<T> validator) {
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
		public ValidatablePropertySelectModeSingleSelectInputBuilder<T> required(Localizable message) {
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

	static class DefaultDatastorePropertySelectModeSingleSelectInputBuilder<T>
			implements DatastorePropertySelectModeSingleSelectInputBuilder<T> {

		private final DefaultPropertySelectModeSingleSelectInputBuilder<T> builder;
		private final DatastoreDataProvider<PropertyBox, String> datastoreDataProvider;

		public DefaultDatastorePropertySelectModeSingleSelectInputBuilder(
				DefaultPropertySelectModeSingleSelectInputBuilder<T> builder,
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> filterConverter(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> renderer(Renderer<PropertyBox> renderer) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> itemCaptionGenerator(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> pageSize(int pageSize) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> validatable() {
			return new DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> required(boolean required) {
			builder.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withAttachListener(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withThemeName(String themeName) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withDeferredLocalization(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> label(Localizable label) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> pattern(String pattern) {
			builder.pattern(pattern);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> preventInvalidInput(boolean preventInvalidInput) {
			builder.preventInvalidInput(preventInvalidInput);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withFocusListener(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withBlurListener(
				ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withQueryConfigurationProvider(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> itemIdentifierProvider(
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
		public DatastorePropertySelectModeSingleSelectInputBuilder<T> querySortOrderConverter(
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

	static class DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T>
			implements ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> {

		private final DatastorePropertySelectModeSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableDatastorePropertySelectModeSingleSelectInputBuilder(
				DatastorePropertySelectModeSingleSelectInputBuilder<T> builder) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withQueryConfigurationProvider(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withDefaultQuerySort(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> itemIdentifierProvider(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> querySortOrderConverter(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> filterConverter(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> renderer(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> itemCaptionGenerator(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#pageSize(int)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> pageSize(int pageSize) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> dataSource(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> elementConfiguration(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withAttachListener(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withThemeName(String themeName) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withDeferredLocalization(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> label(Localizable label) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#pattern(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> pattern(String pattern) {
			builder.pattern(pattern);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasPatternConfigurator#preventInvalidInput(boolean)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> preventInvalidInput(
				boolean preventInvalidInput) {
			builder.preventInvalidInput(preventInvalidInput);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> tabIndex(int tabIndex) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withFocusListener(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withBlurListener(
				ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> itemCaptionProperty(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> validationStatusHandler(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> validateOnValueChange(
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> required(Validator<T> validator) {
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
		public ValidatableDatastorePropertySelectModeSingleSelectInputBuilder<T> required(Localizable message) {
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
