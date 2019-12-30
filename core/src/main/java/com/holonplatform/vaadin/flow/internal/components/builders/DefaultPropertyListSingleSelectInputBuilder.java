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

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator.PropertyListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.data.PropertyItemConverter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;

/**
 * Default {@link PropertyListSingleSelectInputBuilder} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.4.0
 */
public class DefaultPropertyListSingleSelectInputBuilder<T> implements PropertyListSingleSelectInputBuilder<T> {

	private final ListSingleSelectInputBuilder<T, PropertyBox> builder;

	private final Property<T> selectionProperty;

	private PropertyItemConverter<T> propertyItemConverter;

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 */
	public DefaultPropertyListSingleSelectInputBuilder(final Property<T> selectionProperty) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding selection item
	 */
	public DefaultPropertyListSingleSelectInputBuilder(Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty, itemConverter));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     the item converter to use (not null)
	 */
	protected DefaultPropertyListSingleSelectInputBuilder(Property<T> selectionProperty,
			ItemConverter<T, PropertyBox> itemConverter) {
		super();
		ObjectUtils.argumentNotNull(selectionProperty, "Selection property must be not null");
		this.selectionProperty = selectionProperty;
		this.builder = new DefaultListSingleSelectInputBuilder<>(selectionProperty.getType(), PropertyBox.class,
				itemConverter);
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
	 * Setup a item converter function if the current item converter is a
	 * {@link PropertyItemConverter}, using the selection property to retrieve an
	 * item.
	 * @param datastore   The datastore
	 * @param target      The query target
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
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * HasPropertyBoxDatastoreFilterableDataProviderConfigurator#
	 * dataSource(com.holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget, java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastorePropertyListSingleSelectInputBuilder<T> dataSource(Datastore datastore,
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
		return new DefaultDatastorePropertyListSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder.
	 * PropertySelectModeSingleSelectInputBuilder#dataSource(com.holonplatform.core.
	 * datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastorePropertyListSingleSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target) {
		return dataSource(datastore, target, Collections.singletonList(selectionProperty));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
	 * #items(java.lang.Object[])
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> items(PropertyBox... items) {
		return items(Arrays.asList(items));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator
	 * #withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
		builder.withSelectionListener(selectionListener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com.
	 * holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> itemCaptionGenerator(
			ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
		builder.itemCaptionGenerator(itemCaptionGenerator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * PropertySelectInputConfigurator#itemCaptionProperty(com.
	 * holonplatform.core.property.Property)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return itemCaptionGenerator(item -> Objects.toString(item.getValue(property), ""));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#itemCaption(java.lang. Object,
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
		builder.itemCaption(item, caption);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public SingleSelect<T> build() {
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatablePropertyListSingleSelectInputBuilder<T> validatable() {
		return new DefaultValidatablePropertyListSingleSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
	 * boolean)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
		builder.readOnly(readOnly);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#
	 * withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withValueChangeListener(
			ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		builder.withValueChangeListener(listener);
		return this;
	}

	@Override
	public PropertyListSingleSelectInputBuilder<T> withReadonlyChangeListener(ReadonlyChangeListener listener) {
		builder.withReadonlyChangeListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
	 * boolean)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
	 * )
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> required() {
		return required(true);
	}

	@Override
	public <A> PropertyListSingleSelectInputBuilder<T> withAdapter(Class<A> type, Function<Input<T>, A> adapter) {
		builder.withAdapter(type, adapter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
	 * java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
	 * visible(boolean)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> visible(boolean visible) {
		builder.visible(visible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
	 * elementConfiguration(java.util.function. Consumer)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
		builder.elementConfiguration(element);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
	 * withAttachListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withAttachListener(ComponentEventListener<AttachEvent> listener) {
		builder.withAttachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
	 * withDetachListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withDetachListener(ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
	 * withThemeName(java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
	 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener) {
		builder.withEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
	 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener,
	 * java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener,
			String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
	 * java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> width(String width) {
		builder.width(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
	 * java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> height(String height) {
		builder.height(height);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
	 * minWidth(java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> minWidth(String minWidth) {
		builder.minWidth(minWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
	 * maxWidth(java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
		builder.maxWidth(maxWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
	 * minHeight(java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> minHeight(String minHeight) {
		builder.minHeight(minHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
	 * maxHeight(java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
		builder.maxHeight(maxHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
	 * styleNames(java.lang.String[])
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
	 * styleName(java.lang.String)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
	 * enabled(boolean)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> enabled(boolean enabled) {
		builder.enabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * DeferrableLocalizationConfigurator#withDeferredLocalization( boolean)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
		builder.withDeferredLocalization(deferredLocalization);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#
	 * isDeferredLocalizationEnabled()
	 */
	@Override
	public boolean isDeferredLocalizationEnabled() {
		return builder.isDeferredLocalizationEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
	 * #dataSource(com.vaadin.flow.data. provider.DataProvider)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, ?> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(
	 * java.lang.Iterable)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
		builder.items(items);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator#
	 * renderer(com.vaadin.flow.data.renderer .ComponentRenderer)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> renderer(
			ComponentRenderer<? extends Component, PropertyBox> renderer) {
		builder.renderer(renderer);
		return this;
	}

	@Override
	public PropertyListSingleSelectInputBuilder<T> withPrefixComponent(Component component) {
		builder.withPrefixComponent(component);
		return this;
	}

	@Override
	public PropertyListSingleSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem, Component component) {
		builder.withComponentBefore(beforeItem, component);
		return this;
	}

	@Override
	public PropertyListSingleSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem, Component component) {
		builder.withComponentAfter(afterItem, component);
		return this;
	}

	@Override
	public PropertyListSingleSelectInputBuilder<T> failWhenItemNotPresent(boolean failWhenItemNotPresent) {
		builder.failWhenItemNotPresent(failWhenItemNotPresent);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#
	 * itemEnabledProvider(java.util. function.Predicate)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> itemEnabledProvider(Predicate<PropertyBox> itemEnabledProvider) {
		builder.itemEnabledProvider(itemEnabledProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#
	 * addItem(java.lang.Object)
	 */
	@Override
	public PropertyListSingleSelectInputBuilder<T> addItem(PropertyBox item) {
		builder.addItem(item);
		return this;
	}

	// ------- extended builders

	static class DefaultValidatablePropertyListSingleSelectInputBuilder<T>
			implements ValidatablePropertyListSingleSelectInputBuilder<T> {

		private final PropertyListSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatablePropertyListSingleSelectInputBuilder(PropertyListSingleSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.
		 * core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
		 * java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastorePropertyListSingleSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertyListSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, properties));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder.
		 * PropertySelectModeSingleSelectInputConfigurator#dataSource(com.holonplatform.
		 * core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastorePropertyListSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.
		 * ItemCaptionGenerator)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#itemCaption(java.lang. Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator#
		 * renderer(com.vaadin.flow.data. renderer.ComponentRenderer)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #dataSource(com.vaadin.flow.data .provider.DataProvider)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> dataSource(
				DataProvider<PropertyBox, ?> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #items(java.lang.Object[])
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> items(PropertyBox... items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#
		 * itemEnabledProvider(java.util. function.Predicate)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator
		 * #withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
		 * boolean)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
			builder.readOnly(readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#
		 * withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withReadonlyChangeListener(
				ReadonlyChangeListener listener) {
			builder.withReadonlyChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
		 * boolean)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
		 * )
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		@Override
		public <A> ValidatablePropertyListSingleSelectInputBuilder<T> withAdapter(Class<A> type,
				Function<Input<T>, A> adapter) {
			builder.withAdapter(type, adapter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * visible(boolean)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * elementConfiguration(java.util. function.Consumer)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withAttachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withAttachListener(
				ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withDetachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withThemeName(java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener,
		 * java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleNames(java.lang.String[])
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleName(java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
		 * enabled(boolean)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DeferrableLocalizationConfigurator#withDeferredLocalization (boolean)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withDeferredLocalization(
				boolean deferredLocalization) {
			builder.withDeferredLocalization(deferredLocalization);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#
		 * isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return builder.isDeferredLocalizationEnabled();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
		 * java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
		 * java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minWidth(java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxWidth(java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minHeight(java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxHeight(java.lang.String)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(
		 * java.lang.Iterable)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#
		 * addItem(java.lang.Object)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> addItem(PropertyBox item) {
			builder.addItem(item);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#withValidator(com. holonplatform.core.Validator)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
			validatableInputConfigurator.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
			validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#validateOnValueChange(boolean)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> validateOnValueChange(boolean validateOnValueChange) {
			validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#required(com.holonplatform. core.Validator)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> required(Validator<T> validator) {
			validatableInputConfigurator.required(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#required(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public ValidatablePropertyListSingleSelectInputBuilder<T> failWhenItemNotPresent(
				boolean failWhenItemNotPresent) {
			builder.failWhenItemNotPresent(failWhenItemNotPresent);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder
		 * #build()
		 */
		@Override
		public ValidatableSingleSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableSingleSelect.from(builder.build()));
		}

	}

	static class DefaultDatastorePropertyListSingleSelectInputBuilder<T>
			implements DatastorePropertyListSingleSelectInputBuilder<T> {

		private final DefaultPropertyListSingleSelectInputBuilder<T> builder;
		private final DatastoreDataProvider<PropertyBox, String> datastoreDataProvider;

		public DefaultDatastorePropertyListSingleSelectInputBuilder(
				DefaultPropertyListSingleSelectInputBuilder<T> builder,
				DatastoreDataProvider<PropertyBox, String> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.
		 * ItemCaptionGenerator)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#itemCaption(java.lang. Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> validatable() {
			return new DefaultValidatableDatastorePropertyListSingleSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
		 * boolean)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
			builder.readOnly(readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#
		 * withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withReadonlyChangeListener(
				ReadonlyChangeListener listener) {
			builder.withReadonlyChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
		 * boolean)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> required(boolean required) {
			builder.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
		 * )
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		@Override
		public <A> DatastorePropertyListSingleSelectInputBuilder<T> withAdapter(Class<A> type,
				Function<Input<T>, A> adapter) {
			builder.withAdapter(type, adapter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * visible(boolean)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * elementConfiguration(java.util. function.Consumer)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withAttachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withAttachListener(
				ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withDetachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withThemeName(java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener,
		 * java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator
		 * #withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleNames(java.lang.String[])
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleName(java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
		 * enabled(boolean)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DeferrableLocalizationConfigurator#withDeferredLocalization (boolean)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
			builder.withDeferredLocalization(deferredLocalization);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#
		 * isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return builder.isDeferredLocalizationEnabled();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
		 * java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
		 * java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minWidth(java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxWidth(java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minHeight(java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxHeight(java.lang.String)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.
		 * QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#withDefaultQuerySort(com.
		 * holonplatform.core.query.QuerySort)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
			datastoreDataProvider.setDefaultSort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#itemIdentifierProvider(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> itemIdentifierProvider(
				Function<PropertyBox, Object> itemIdentifierProvider) {
			datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#querySortOrderConverter(
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator#
		 * renderer(com.vaadin.flow.data. renderer.ComponentRenderer)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#
		 * itemEnabledProvider(java.util. function.Predicate)
		 */
		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public DatastorePropertyListSingleSelectInputBuilder<T> failWhenItemNotPresent(boolean failWhenItemNotPresent) {
			builder.failWhenItemNotPresent(failWhenItemNotPresent);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
		 */
		@Override
		public SingleSelect<T> build() {
			return builder.build();
		}

	}

	static class DefaultValidatableDatastorePropertyListSingleSelectInputBuilder<T>
			implements ValidatableDatastorePropertyListSingleSelectInputBuilder<T> {

		private final DatastorePropertyListSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableDatastorePropertyListSingleSelectInputBuilder(
				DatastorePropertyListSingleSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.
		 * QueryConfigurationProvider)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			builder.withQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#withDefaultQuerySort(com.
		 * holonplatform.core.query.QuerySort)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withDefaultQuerySort(
				QuerySort defaultQuerySort) {
			builder.withDefaultQuerySort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#itemIdentifierProvider(
		 * java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> itemIdentifierProvider(
				Function<PropertyBox, Object> itemIdentifierProvider) {
			builder.itemIdentifierProvider(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#querySortOrderConverter(
		 * java.util.function.Function)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.
		 * ItemCaptionGenerator)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#itemCaption(java.lang. Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator
		 * #withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
		 * boolean)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
			builder.readOnly(readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#
		 * withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withReadonlyChangeListener(
				ReadonlyChangeListener listener) {
			builder.withReadonlyChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
		 * boolean)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
		 * )
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		@Override
		public <A> ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withAdapter(Class<A> type,
				Function<Input<T>, A> adapter) {
			builder.withAdapter(type, adapter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * visible(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * elementConfiguration(java.util. function.Consumer)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> elementConfiguration(
				Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withAttachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withAttachListener(
				ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withDetachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener,
		 * java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
		 * enabled(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DeferrableLocalizationConfigurator#withDeferredLocalization (boolean)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withDeferredLocalization(
				boolean deferredLocalization) {
			builder.withDeferredLocalization(deferredLocalization);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#
		 * isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return builder.isDeferredLocalizationEnabled();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
		 * java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
		 * java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator#
		 * renderer(com.vaadin.flow.data. renderer.ComponentRenderer)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#
		 * itemEnabledProvider(java.util. function.Predicate)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#withValidator(com. holonplatform.core.Validator)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
			validatableInputConfigurator.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
			validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#validateOnValueChange(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> validateOnValueChange(
				boolean validateOnValueChange) {
			validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#required(com.holonplatform. core.Validator)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> required(Validator<T> validator) {
			validatableInputConfigurator.required(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#required(com.holonplatform.
		 * core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListSingleSelectInputBuilder<T> failWhenItemNotPresent(
				boolean failWhenItemNotPresent) {
			builder.failWhenItemNotPresent(failWhenItemNotPresent);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder
		 * #build()
		 */
		@Override
		public ValidatableSingleSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableSingleSelect.from(builder.build()));
		}

	}

}
