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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ConversionUtils;
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
import com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder.ItemOptionsModeMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.MultiSelectInputAdapter;
import com.holonplatform.vaadin.flow.internal.components.support.DeferrableItemLabelGenerator;
import com.holonplatform.vaadin.flow.internal.components.support.ExceptionSwallowingSupplier;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * Default {@link ItemOptionsModeMultiSelectInputBuilder} implementation.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 *
 * @since 5.2.0
 */
public class DefaultItemOptionsModeMultiSelectInputBuilder<T, ITEM> extends
		AbstractLocalizableComponentConfigurator<CheckboxGroup<ITEM>, ItemOptionsModeMultiSelectInputBuilder<T, ITEM>>
		implements ItemOptionsModeMultiSelectInputBuilder<T, ITEM> {

	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasLabelConfigurator<CheckboxGroup<ITEM>> labelConfigurator;

	protected final List<ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>>> valueChangeListeners = new LinkedList<>();
	protected final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	private final Class<? extends T> type;
	private final Class<ITEM> itemType;

	protected final ItemConverter<T, ITEM> itemConverter;

	protected Set<ITEM> items = new HashSet<>();

	protected final Map<ITEM, Localizable> itemCaptions = new HashMap<>();

	protected boolean customItemCaptionGenerator = false;

	/**
	 * Constructor.
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type
	 * @param itemConverter The item converter to use (not null)
	 */
	public DefaultItemOptionsModeMultiSelectInputBuilder(Class<? extends T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		super(new CheckboxGroup<>());
		ObjectUtils.argumentNotNull(type, "Selection value type must be not null");
		ObjectUtils.argumentNotNull(itemType, "Selection item type must be not null");
		ObjectUtils.argumentNotNull(itemConverter, "ItemConverter must be not null");
		this.type = type;
		this.itemType = itemType;
		this.itemConverter = itemConverter;

		styleConfigurator = new DefaultHasStyleConfigurator(getComponent());
		enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
	}

	protected Class<? extends T> getType() {
		return type;
	}

	protected Class<ITEM> getItemType() {
		return itemType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ItemOptionsModeMultiSelectInputBuilder<T, ITEM> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public MultiSelect<T> build() {
		final CheckboxGroup<ITEM> component = getComponent();

		// check DataProvider
		if (!new ExceptionSwallowingSupplier<>(() -> component.getDataProvider()).get().isPresent()) {
			// default data provider
			component.setDataProvider(DataProvider.ofCollection(Collections.emptySet()));
		}

		// configure captions
		if (!customItemCaptionGenerator && !itemCaptions.isEmpty()) {
			component.setItemLabelGenerator(
					new DeferrableItemLabelGenerator<>(itemCaptions, component, isDeferredLocalizationEnabled()));
		}

		// items
		if (!items.isEmpty()) {
			component.setItems(items);
		}

		final Input<Set<ITEM>> itemInput = Input.builder(component)
				.requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v)).hasEnabledSupplier(f -> f)
				.hasValidationSupplier(f -> f).isEmptySupplier(f -> f.getValue() == null || f.getValue().isEmpty())
				.build();

		final MultiSelect<T> multiSelect = new MultiSelectInputAdapter<>(itemInput, itemConverter,
				ms -> component.getDataProvider().refreshAll(), () -> {
					if (component.getDataProvider() != null) {
						return component.getDataProvider().fetch(new Query<>()).collect(Collectors.toSet());
					}
					return null;
				});
		selectionListeners.forEach(listener -> multiSelect.addSelectionListener(listener));
		valueChangeListeners.forEach(listener -> multiSelect.addValueChangeListener(listener));

		return multiSelect;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> validatable() {
		return new DefaultValidatableItemOptionsModeMultiSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> withSelectionListener(
			SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com.
	 * vaadin.flow.function.SerializablePredicate)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(
			SerializablePredicate<ITEM> itemEnabledProvider) {
		getComponent().setItemEnabledProvider(itemEnabledProvider);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com.
	 * holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
			ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		ObjectUtils.argumentNotNull(itemCaptionGenerator, "ItemCaptionGenerator must be not null");
		getComponent().setItemLabelGenerator(item -> itemCaptionGenerator.getItemCaption(item));
		this.customItemCaptionGenerator = true;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		if (caption == null) {
			itemCaptions.remove(item);
		} else {
			itemCaptions.put(item, caption);
		}
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget, java.util.function.Function,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
			DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter, Iterable<P> properties) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				DatastoreDataProvider.asPropertySet(properties), itemConverter, f -> null);
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreItemOptionsModeMultiSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
			DataTarget<?> target) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				getItemType());
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreItemOptionsModeMultiSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#dataSource(com.vaadin.flow.data.
	 * provider.DataProvider)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(DataProvider<ITEM, ?> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> items(ITEM... items) {
		return items(Arrays.asList(items));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
		this.items = (items != null) ? ConversionUtils.iterableAsSet(items) : new HashSet<>();
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> addItem(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		this.items.add(item);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
			ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public ItemOptionsModeMultiSelectInputBuilder<T, ITEM> label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	// ------- extended builders

	static class DefaultValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM>
			implements ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> {

		private final ItemOptionsModeMultiSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<Set<T>> validatableInputConfigurator;

		public DefaultValidatableItemOptionsModeMultiSelectInputBuilder(
				ItemOptionsModeMultiSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#itemEnabledProvider(com.
		 * vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(
				SerializablePredicate<ITEM> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(
				ListDataProvider<ITEM> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
		 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget,
		 * java.util.function.Function, java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(
				Datastore datastore, DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter,
				Iterable<P> properties) {
			return new DefaultValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<>(
					builder.dataSource(datastore, target, itemConverter, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
		 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#dataSource(com.vaadin.flow.data
		 * .provider.DataProvider)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(
				DataProvider<ITEM, ?> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Iterable)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Object[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> items(ITEM... items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#addItem(java.lang.Object)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> addItem(ITEM item) {
			builder.addItem(item);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> withValidator(Validator<Set<T>> validator) {
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<Set<T>>> validationStatusHandler) {
			validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> validateOnValueChange(
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(Validator<Set<T>> validator) {
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
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableItemOptionsModeMultiSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder#build()
		 */
		@Override
		public ValidatableMultiSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableMultiSelect.from(builder.build()));
		}

	}

	static class DefaultDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM>
			implements DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> {

		private final DefaultItemOptionsModeMultiSelectInputBuilder<T, ITEM> builder;
		private final DatastoreDataProvider<ITEM, ?> datastoreDataProvider;

		public DefaultDatastoreItemOptionsModeMultiSelectInputBuilder(
				DefaultItemOptionsModeMultiSelectInputBuilder<T, ITEM> builder,
				DatastoreDataProvider<ITEM, ?> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> validatable() {
			return new DefaultValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com
		 * .vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(
				SerializablePredicate<ITEM> itemEnabledProvider) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(
				ListDataProvider<ITEM> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(boolean required) {
			builder.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withAttachListener(
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDefaultQuerySort(
				QuerySort defaultQuerySort) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemIdentifierProvider(
				Function<ITEM, Object> itemIdentifierProvider) {
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
		public DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
		 */
		@Override
		public MultiSelect<T> build() {
			return builder.build();
		}

	}

	static class DefaultValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM>
			implements ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> {

		private final DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<Set<T>> validatableInputConfigurator;

		public DefaultValidatableDatastoreItemOptionsModeMultiSelectInputBuilder(
				DatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#itemEnabledProvider(com.
		 * vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(
				SerializablePredicate<ITEM> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
			builder.itemCaptionGenerator(itemCaptionGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#itemCaption(java.lang.
		 * Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item,
				Localizable caption) {
			builder.itemCaption(item, caption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.OptionsModeMultiSelectInputBuilder#dataSource(com.vaadin.
		 * flow.data.provider.ListDataProvider)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> dataSource(
				ListDataProvider<ITEM> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> label(Localizable label) {
			builder.label(label);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withDefaultQuerySort(
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> itemIdentifierProvider(
				Function<ITEM, Object> itemIdentifierProvider) {
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> withValidator(
				Validator<Set<T>> validator) {
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> validationStatusHandler(
				ValidationStatusHandler<ValidatableInput<Set<T>>> validationStatusHandler) {
			validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#validateOnValueChange(boolean)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> validateOnValueChange(
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(
				Validator<Set<T>> validator) {
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
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(Localizable message) {
			validatableInputConfigurator.required(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableDatastoreItemOptionsModeMultiSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BaseValidatableInputBuilder#build()
		 */
		@Override
		public ValidatableMultiSelect<T> build() {
			return validatableInputConfigurator.configure(ValidatableMultiSelect.from(builder.build()));
		}

	}

}
