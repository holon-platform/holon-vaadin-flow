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

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
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
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.PropertyOptionsSingleSelectInputBuilder;
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
 * Default {@link PropertyOptionsSingleSelectInputBuilder} implementation.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultPropertyOptionsSingleSelectInputBuilder<T> implements PropertyOptionsSingleSelectInputBuilder<T> {

	private final OptionsSingleSelectInputBuilder<T, PropertyBox> builder;

	private final Property<T> selectionProperty;

	private PropertyItemConverter<T> propertyItemConverter;

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 */
	public DefaultPropertyOptionsSingleSelectInputBuilder(final Property<T> selectionProperty) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding selection item
	 */
	public DefaultPropertyOptionsSingleSelectInputBuilder(Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		this(selectionProperty, new PropertyItemConverter<>(selectionProperty, itemConverter));
	}

	/**
	 * Constructor.
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The item converter to use (not null)
	 */
	protected DefaultPropertyOptionsSingleSelectInputBuilder(Property<T> selectionProperty,
			ItemConverter<T, PropertyBox> itemConverter) {
		super();
		ObjectUtils.argumentNotNull(selectionProperty, "Selection property must be not null");
		this.selectionProperty = selectionProperty;
		this.builder = new DefaultOptionsSingleSelectInputBuilder<>(selectionProperty.getType(), PropertyBox.class,
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
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
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
	public PropertyOptionsSingleSelectInputBuilder<T> renderer(
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
	public PropertyOptionsSingleSelectInputBuilder<T> itemEnabledProvider(Predicate<PropertyBox> itemEnabledProvider) {
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
	public PropertyOptionsSingleSelectInputBuilder<T> itemCaptionGenerator(
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
	public PropertyOptionsSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return itemCaptionGenerator(item -> Objects.toString(item.getValue(property), ""));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
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
	public ValidatablePropertyOptionsSingleSelectInputBuilder<T> validatable() {
		return new DefaultValidatablePropertyOptionsSingleSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
	public PropertyOptionsSingleSelectInputBuilder<T> withValueChangeListener(
			ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		builder.withValueChangeListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> visible(boolean visible) {
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
	public PropertyOptionsSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
		builder.elementConfiguration(element);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> withAttachListener(ComponentEventListener<AttachEvent> listener) {
		builder.withAttachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> withDetachListener(ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener) {
		builder.withEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType, DomEventListener listener,
			String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
	public PropertyOptionsSingleSelectInputBuilder<T> withDeferredLocalization(boolean deferredLocalization) {
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
	 * com.holonplatform.vaadin.flow.components.builders.HasPropertyBoxDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget, java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastorePropertyOptionsSingleSelectInputBuilder<T> dataSource(Datastore datastore,
			DataTarget<?> target, Iterable<P> properties) {
		final DatastoreDataProvider<PropertyBox, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore,
				target, DatastoreDataProvider.asPropertySet(properties));
		builder.dataSource(datastoreDataProvider);
		setupDatastoreItemConverter(datastore, target, DatastoreDataProvider.asPropertySet(properties));
		return new DefaultDatastorePropertyOptionsSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.
	 * PropertyOptionsModeSingleSelectInputBuilder#dataSource(com.holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastorePropertyOptionsSingleSelectInputBuilder<T> dataSource(Datastore datastore, DataTarget<?> target) {
		return dataSource(datastore, target, Collections.singletonList(selectionProperty));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#dataSource(com.vaadin.flow.data.
	 * provider.DataProvider)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> dataSource(DataProvider<PropertyBox, ?> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Object[])
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> items(PropertyBox... items) {
		return items(Arrays.asList(items));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> dataSource(ListDataProvider<PropertyBox> dataProvider) {
		builder.dataSource(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
		builder.items(items);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> addItem(PropertyBox item) {
		builder.addItem(item);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public PropertyOptionsSingleSelectInputBuilder<T> label(Localizable label) {
		builder.label(label);
		return this;
	}

	// ------- extended builders

	static class DefaultValidatablePropertyOptionsSingleSelectInputBuilder<T>
			implements ValidatablePropertyOptionsSingleSelectInputBuilder<T> {

		private final PropertyOptionsSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatablePropertyOptionsSingleSelectInputBuilder(
				PropertyOptionsSingleSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.
		 * PropertyOptionsModeSingleSelectInputConfigurator#dataSource(com.holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastorePropertyOptionsSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#renderer(com.vaadin.
		 * flow.data.renderer.ComponentRenderer)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com
		 * .vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemCaptionGenerator(
		 * com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> dataSource(
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withAttachListener(
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withThemeName(String themeName) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withDeferredLocalization(
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
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasPropertyBoxDatastoreDataProviderConfigurator#dataSource(
		 * com.holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget, java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> dataSource(
				Datastore datastore, DataTarget<?> target, Iterable<P> properties) {
			return new DefaultValidatableDatastorePropertyOptionsSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#dataSource(com.vaadin.flow.data
		 * .provider.DataProvider)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> dataSource(
				DataProvider<PropertyBox, ?> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Iterable)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> items(Iterable<PropertyBox> items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Object[])
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> items(PropertyBox... items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#addItem(java.lang.Object)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> addItem(PropertyBox item) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
			builder.itemCaptionProperty(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> validationStatusHandler(
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> validateOnValueChange(
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> required(Validator<T> validator) {
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
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatablePropertyOptionsSingleSelectInputBuilder<T> required() {
			return required(true);
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

	static class DefaultDatastorePropertyOptionsSingleSelectInputBuilder<T>
			implements DatastorePropertyOptionsSingleSelectInputBuilder<T> {

		private final DefaultPropertyOptionsSingleSelectInputBuilder<T> builder;
		private final DatastoreDataProvider<PropertyBox, ?> datastoreDataProvider;

		public DefaultDatastorePropertyOptionsSingleSelectInputBuilder(
				DefaultPropertyOptionsSingleSelectInputBuilder<T> builder,
				DatastoreDataProvider<PropertyBox, ?> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#renderer(com.vaadin.
		 * flow.data.renderer.ComponentRenderer)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com
		 * .vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> itemCaptionGenerator(
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> itemCaptionProperty(Property<?> property) {
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> itemCaption(PropertyBox item, Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> dataSource(
				ListDataProvider<PropertyBox> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> validatable() {
			return new DefaultValidatableDatastorePropertyOptionsSingleSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> required(boolean required) {
			builder.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> elementConfiguration(Consumer<Element> element) {
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withAttachListener(
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withThemeName(String themeName) {
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withDeferredLocalization(
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
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withQueryConfigurationProvider(
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> itemIdentifierProvider(
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
		public DatastorePropertyOptionsSingleSelectInputBuilder<T> querySortOrderConverter(
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

	static class DefaultValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T>
			implements ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> {

		private final DatastorePropertyOptionsSingleSelectInputBuilder<T> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableDatastorePropertyOptionsSingleSelectInputBuilder(
				DatastorePropertyOptionsSingleSelectInputBuilder<T> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#renderer(com.vaadin.
		 * flow.data.renderer.ComponentRenderer)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> renderer(
				ComponentRenderer<? extends Component, PropertyBox> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com
		 * .vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> itemEnabledProvider(
				Predicate<PropertyBox> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemCaptionGenerator(
		 * com.holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> itemCaptionGenerator(
				ItemCaptionGenerator<PropertyBox> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> itemCaption(PropertyBox item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> dataSource(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> readOnly(boolean readOnly) {
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> elementConfiguration(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withAttachListener(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withThemeName(String themeName) {
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType,
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> enabled(boolean enabled) {
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withDeferredLocalization(
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
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withQueryConfigurationProvider(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withDefaultQuerySort(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> itemIdentifierProvider(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertySelectInputConfigurator#itemCaptionProperty(com.
		 * holonplatform.core.property.Property)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> itemCaptionProperty(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> withValidator(Validator<T> validator) {
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> validationStatusHandler(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> validateOnValueChange(
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> required(Validator<T> validator) {
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
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableDatastorePropertyOptionsSingleSelectInputBuilder<T> required() {
			return required(true);
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
