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
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidatableMultiSelect;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.PropertyListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.data.AdditionalItemsProvider;
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
 * Default {@link PropertyListMultiSelectInputBuilder} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.4.0
 */
public class DefaultPropertyListMultiSelectInputBuilder<T> extends AbstractPropertySelectInputBuilder<T>
		implements PropertyListMultiSelectInputBuilder<T> {

	private final ListMultiSelectInputBuilder<T, PropertyBox> builder;

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 */
	public DefaultPropertyListMultiSelectInputBuilder(final Property<T> selectionProperty) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The function to use to convert a selection value
	 *                          into the corresponding selection item
	 */
	public DefaultPropertyListMultiSelectInputBuilder(Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty, itemConverter));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value
	 *                          (not null)
	 * @param itemConverter     The item converter to use (not null)
	 */
	protected DefaultPropertyListMultiSelectInputBuilder(Property<T> selectionProperty,
			ItemConverter<T, PropertyBox> itemConverter) {
		super(selectionProperty);
		this.builder = new DefaultListMultiSelectInputBuilder<>(selectionProperty.getType(), PropertyBox.class,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator
	 * #withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public PropertyListMultiSelectInputBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
		builder.withSelectionListener(selectionListener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com.
	 * vaadin.flow.function.SerializablePredicate)
	 */
	@Override
	public PropertyListMultiSelectInputBuilder<T> itemEnabledProvider(Predicate<PropertyBox> itemEnabledProvider) {
		builder.itemEnabledProvider(itemEnabledProvider);
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
	public PropertyListMultiSelectInputBuilder<T> itemCaptionGenerator(
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
	public PropertyListMultiSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
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
	public PropertyListMultiSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
		builder.itemCaption(item, caption);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public MultiSelect<T> build() {
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatablePropertyListMultiSelectInputBuilder<T> validatable() {
		return new DefaultValidatablePropertyListMultiSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
	 * boolean)
	 */
	@Override
	public PropertyListMultiSelectInputBuilder<T> readOnly(boolean readOnly) {
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
	public PropertyListMultiSelectInputBuilder<T> withValueChangeListener(
			ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
		builder.withValueChangeListener(listener);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> withReadonlyChangeListener(ReadonlyChangeListener listener) {
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
	public PropertyListMultiSelectInputBuilder<T> required(boolean required) {
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
	public PropertyListMultiSelectInputBuilder<T> required() {
		return required(true);
	}

	@Override
	public <A> PropertyListMultiSelectInputBuilder<T> withAdapter(Class<A> type, Function<Input<Set<T>>, A> adapter) {
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
	public PropertyListMultiSelectInputBuilder<T> id(String id) {
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
	public PropertyListMultiSelectInputBuilder<T> visible(boolean visible) {
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
	public PropertyListMultiSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
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
	public PropertyListMultiSelectInputBuilder<T> withAttachListener(ComponentEventListener<AttachEvent> listener) {
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
	public PropertyListMultiSelectInputBuilder<T> withDetachListener(ComponentEventListener<DetachEvent> listener) {
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
	public PropertyListMultiSelectInputBuilder<T> withThemeName(String themeName) {
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
	public PropertyListMultiSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener) {
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
	public PropertyListMultiSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener,
			String filter) {
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
	public PropertyListMultiSelectInputBuilder<T> styleNames(String... styleNames) {
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
	public PropertyListMultiSelectInputBuilder<T> styleName(String styleName) {
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
	public PropertyListMultiSelectInputBuilder<T> enabled(boolean enabled) {
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
	public PropertyListMultiSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
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
	 * HasPropertyBoxDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget, java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastorePropertyListMultiSelectInputBuilder<T> dataSource(Datastore datastore,
			DataTarget<?> target, Iterable<P> properties) {
		final DatastoreDataProvider<PropertyBox, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore,
				target, DatastoreDataProvider.asPropertySet(properties));
		builder.dataSource(datastoreDataProvider);
		setupDatastoreItemConverter(datastoreDataProvider, datastore, target,
				DatastoreDataProvider.asPropertySet(properties));
		return new DefaultDatastorePropertyListMultiSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * OptionsModeSingleSelectInputBuilder.
	 * PropertyOptionsModeSingleSelectInputBuilder#dataSource(com.holonplatform.core
	 * .datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastorePropertyListMultiSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target) {
		return dataSource(datastore, target, Collections.singletonList(selectionProperty));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
	 * #dataSource(com.vaadin.flow.data. provider.DataProvider)
	 */
	@Override
	public PropertyListMultiSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, ?> dataProvider) {
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
	public PropertyListMultiSelectInputBuilder<T> items(PropertyBox... items) {
		return items(Arrays.asList(items));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public PropertyListMultiSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
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
	public PropertyListMultiSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
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
	public PropertyListMultiSelectInputBuilder<T> addItem(PropertyBox item) {
		builder.addItem(item);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> renderer(
			ComponentRenderer<? extends Component, PropertyBox> renderer) {
		builder.renderer(renderer);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> withPrefixComponent(Component component) {
		builder.withPrefixComponent(component);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem, Component component) {
		builder.withComponentBefore(beforeItem, component);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem, Component component) {
		builder.withComponentAfter(afterItem, component);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> width(String width) {
		builder.width(width);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> height(String height) {
		builder.height(height);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> minWidth(String minWidth) {
		builder.minWidth(minWidth);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> maxWidth(String maxWidth) {
		builder.maxWidth(maxWidth);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> minHeight(String minHeight) {
		builder.minHeight(minHeight);
		return this;
	}

	@Override
	public PropertyListMultiSelectInputBuilder<T> maxHeight(String maxHeight) {
		builder.maxHeight(maxHeight);
		return this;
	}

	// ------- extended builders

	static class DefaultValidatablePropertyListMultiSelectInputBuilder<T>
			implements ValidatablePropertyListMultiSelectInputBuilder<T> {

		private final PropertyListMultiSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<Set<T>> validatableInputConfigurator;

		public DefaultValidatablePropertyListMultiSelectInputBuilder(PropertyListMultiSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder.
		 * PropertyOptionsModeMultiSelectInputConfigurator#dataSource(com.holonplatform.
		 * core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastorePropertyListMultiSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#itemEnabledProvider(com.
		 * vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.
		 * ItemCaptionGenerator)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#itemCaption(java.lang. Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> dataSource(
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withSelectionListener(
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> withReadonlyChangeListener(
				ReadonlyChangeListener listener) {
			builder.withReadonlyChangeListener(listener);
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> id(String id) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> visible(boolean visible) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withAttachListener(
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withDetachListener(
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withThemeName(String themeName) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> styleNames(String... styleNames) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> styleName(String styleName) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> enabled(boolean enabled) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withDeferredLocalization(
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
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasPropertyBoxDatastoreDataProviderConfigurator#dataSource(
		 * com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastorePropertyListMultiSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertyListMultiSelectInputBuilder<>(
					builder.dataSource(datastore, target, properties));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #dataSource(com.vaadin.flow.data .provider.DataProvider)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, ?> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #items(java.lang.Iterable)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
			builder.items(items);
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> items(PropertyBox... items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #addItem(java.lang.Object)
		 */
		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> addItem(PropertyBox item) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> withValidator(Validator<Set<T>> validator) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<Set<T>>> validationStatusHandler) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> validateOnValueChange(boolean validateOnValueChange) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> required(Validator<Set<T>> validator) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> required(boolean required) {
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
		public ValidatablePropertyListMultiSelectInputBuilder<T> required() {
			return required(true);
		}

		@Override
		public <A> ValidatablePropertyListMultiSelectInputBuilder<T> withAdapter(Class<A> type,
				Function<Input<Set<T>>, A> adapter) {
			builder.withAdapter(type, adapter);
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
		public ValidatableMultiSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableMultiSelect.from(builder.build()));
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		@Override
		public ValidatablePropertyListMultiSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

	}

	static class DefaultDatastorePropertyListMultiSelectInputBuilder<T>
			implements DatastorePropertyListMultiSelectInputBuilder<T> {

		private final DefaultPropertyListMultiSelectInputBuilder<T> builder;
		private final DatastoreDataProvider<PropertyBox, ?> datastoreDataProvider;

		public DefaultDatastorePropertyListMultiSelectInputBuilder(
				DefaultPropertyListMultiSelectInputBuilder<T> builder,
				DatastoreDataProvider<PropertyBox, ?> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> additionalItemsProvider(
				AdditionalItemsProvider<PropertyBox> additionalItemsProvider) {
			this.datastoreDataProvider.setAdditionalItemsProvider(additionalItemsProvider);
			return withAttachListener(e -> this.datastoreDataProvider.refreshAll());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com
		 * .vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
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
		public DatastorePropertyListMultiSelectInputBuilder<T> itemCaptionGenerator(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> validatable() {
			return new DefaultValidatableDatastorePropertyListMultiSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
		 * boolean)
		 */
		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> withReadonlyChangeListener(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> required(boolean required) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> required() {
			return required(true);
		}

		@Override
		public <A> DatastorePropertyListMultiSelectInputBuilder<T> withAdapter(Class<A> type,
				Function<Input<Set<T>>, A> adapter) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> id(String id) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> visible(boolean visible) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withAttachListener(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withDetachListener(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withThemeName(String themeName) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withEventListener(String eventType,
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withEventListener(String eventType,
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withSelectionListener(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> styleNames(String... styleNames) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> styleName(String styleName) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> enabled(boolean enabled) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
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
		 * DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.
		 * QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> withQueryConfigurationProvider(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
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
		public DatastorePropertyListMultiSelectInputBuilder<T> itemIdentifierProvider(
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
		public DatastorePropertyListMultiSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		@Override
		public DatastorePropertyListMultiSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
		 */
		@Override
		public MultiSelect<T> build() {
			return builder.build();
		}

	}

	static class DefaultValidatableDatastorePropertyListMultiSelectInputBuilder<T>
			implements ValidatableDatastorePropertyListMultiSelectInputBuilder<T> {

		private final DatastorePropertyListMultiSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<Set<T>> validatableInputConfigurator;

		public DefaultValidatableDatastorePropertyListMultiSelectInputBuilder(
				DatastorePropertyListMultiSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> additionalItemsProvider(
				AdditionalItemsProvider<PropertyBox> additionalItemsProvider) {
			builder.additionalItemsProvider(additionalItemsProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#itemEnabledProvider(com.
		 * vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.
		 * ItemCaptionGenerator)
		 */
		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#itemCaption(java.lang. Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeMultiSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> dataSource(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withSelectionListener(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withReadonlyChangeListener(
				ReadonlyChangeListener listener) {
			builder.withReadonlyChangeListener(listener);
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> id(String id) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> visible(boolean visible) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> elementConfiguration(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withAttachListener(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withDetachListener(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withThemeName(String themeName) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> styleNames(String... styleNames) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> styleName(String styleName) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> enabled(boolean enabled) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withDeferredLocalization(
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
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.
		 * QueryConfigurationProvider)
		 */
		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withQueryConfigurationProvider(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withDefaultQuerySort(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> itemIdentifierProvider(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withValidator(Validator<Set<T>> validator) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<Set<T>>> validationStatusHandler) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> validateOnValueChange(
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> required(Validator<Set<T>> validator) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> required(boolean required) {
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
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> required() {
			return required(true);
		}

		@Override
		public <A> ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withAdapter(Class<A> type,
				Function<Input<Set<T>>, A> adapter) {
			builder.withAdapter(type, adapter);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withComponentBefore(PropertyBox beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> withComponentAfter(PropertyBox afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		@Override
		public ValidatableDatastorePropertyListMultiSelectInputBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
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
		public ValidatableMultiSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableMultiSelect.from(builder.build()));
		}

	}

}
