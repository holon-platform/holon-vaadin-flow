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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator.ListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.SingleSelectInputAdapter;
import com.holonplatform.vaadin.flow.internal.components.support.DeferrableItemLabelGenerator;
import com.holonplatform.vaadin.flow.internal.components.support.ExceptionSwallowingSupplier;
import com.holonplatform.vaadin.flow.internal.converters.ItemConverterConverter;
import com.holonplatform.vaadin.flow.internal.utils.CollectionUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;

/**
 * Default {@link ListSingleSelectInputBuilder} implementation.
 *
 * @param <T>    Value type
 * @param <ITEM> Item type
 *
 * @since 5.4.0
 */
public class DefaultListSingleSelectInputBuilder<T, ITEM>
		extends AbstractInputConfigurator<T, ValueChangeEvent<T>, ListBox<ITEM>, ListSingleSelectInputBuilder<T, ITEM>>
		implements ListSingleSelectInputBuilder<T, ITEM> {

	protected final DefaultHasEnabledConfigurator enabledConfigurator;

	protected final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	private final Class<? extends T> type;
	private final Class<ITEM> itemType;

	protected final ItemConverter<T, ITEM> itemConverter;

	protected Set<ITEM> items = new LinkedHashSet<>();

	protected final Map<ITEM, Localizable> itemCaptions = new HashMap<>();

	protected boolean customItemCaptionGenerator = false;

	protected boolean failWhenItemNotPresent = false;

	/**
	 * Constructor.
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type
	 * @param itemConverter The item converter to use (not null)
	 */
	public DefaultListSingleSelectInputBuilder(Class<? extends T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		super(new ListBox<>(), InputAdaptersContainer.create());
		ObjectUtils.argumentNotNull(type, "Selection value type must be not null");
		ObjectUtils.argumentNotNull(itemType, "Selection item type must be not null");
		ObjectUtils.argumentNotNull(itemConverter, "ItemConverter must be not null");
		this.type = type;
		this.itemType = itemType;
		this.itemConverter = itemConverter;

		enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
	}

	protected Class<? extends T> getType() {
		return type;
	}

	protected Class<ITEM> getItemType() {
		return itemType;
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.empty();
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.
	 * AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ListSingleSelectInputBuilder<T, ITEM> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public SingleSelect<T> build() {
		final ListBox<ITEM> component = getComponent();

		// check DataProvider
		if (!new ExceptionSwallowingSupplier<>(() -> component.getDataProvider()).get().isPresent()) {
			// default data provider
			component.setDataProvider(DataProvider.ofCollection(Collections.emptySet()));
		}

		// configure captions
		if (!customItemCaptionGenerator && !itemCaptions.isEmpty()) {
			component.setRenderer(new TextRenderer<>(
					new DeferrableItemLabelGenerator<>(itemCaptions, component, isDeferredLocalizationEnabled())));
		}

		// items
		if (!items.isEmpty()) {
			component.setItems(items);
		}

		final Input<ITEM> itemInput = Input.builder(component).requiredPropertyHandler((f, c) -> {
			return false;
			// TODO not supported by web component at time of writing
			// return f.isRequiredIndicatorVisible();
		}, (f, c, v) -> {
			// TODO not supported by web component at time of writing
			// f.setRequiredIndicatorVisible(v);
		}).hasEnabledSupplier(f -> f).build();

		final Input<T> input = Input
				.builder(itemInput,
						failWhenItemNotPresent ? new ItemConverterConverter<>(itemConverter)
								: new ListBoxItemConverterAdapter<>(component,
										new ItemConverterConverter<>(itemConverter)))
				.withValueChangeListeners(getValueChangeListeners())
				.withReadonlyChangeListeners(getReadonlyChangeListeners()).withAdapters(getAdapters()).build();

		final SingleSelect<T> select = new SingleSelectInputAdapter<>(input,
				() -> component.getDataProvider().refreshAll());
		selectionListeners.forEach(listener -> select.addSelectionListener(listener));
		return select;
	}

	@SuppressWarnings("serial")
	private static class ListBoxItemConverterAdapter<ITEM, T> implements Converter<ITEM, T> {

		private final ListBox<ITEM> component;
		private final Converter<ITEM, T> converter;

		public ListBoxItemConverterAdapter(ListBox<ITEM> component, Converter<ITEM, T> converter) {
			super();
			this.component = component;
			this.converter = converter;
		}

		@Override
		public Result<T> convertToModel(ITEM value, ValueContext context) {
			return converter.convertToModel(value, context);
		}

		@Override
		public ITEM convertToPresentation(T value, ValueContext context) {
			final ITEM item = converter.convertToPresentation(value, context);
			if (item != null) {
				try {
					component.setValue(item);
				} catch (@SuppressWarnings("unused") IllegalArgumentException e) {
					return null;
				}
			}
			return item;
		}

	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> withSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
		return getConfigurator();
	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> renderer(ComponentRenderer<? extends Component, ITEM> renderer) {
		getComponent().setRenderer(renderer);
		return getConfigurator();
	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		ObjectUtils.argumentNotNull(itemCaptionGenerator, "ItemCaptionGenerator must be not null");
		getComponent().setRenderer(new TextRenderer<>(item -> itemCaptionGenerator.getItemCaption(item)));
		this.customItemCaptionGenerator = true;
		getComponent().setRenderer(new TextRenderer<>(item -> itemCaptionGenerator.getItemCaption(item)));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#itemCaption(java.lang. Object,
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		if (caption == null) {
			itemCaptions.remove(item);
		} else {
			itemCaptions.put(item, caption);
		}
		return getConfigurator();
	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		getComponent().addComponentAsFirst(component);
		return getConfigurator();
	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem, Component component) {
		ObjectUtils.argumentNotNull(beforeItem, "Item must be not null");
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		getComponent().prependComponents(beforeItem, component);
		return getConfigurator();
	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem, Component component) {
		ObjectUtils.argumentNotNull(afterItem, "Item must be not null");
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		getComponent().addComponents(afterItem, component);
		return getConfigurator();
	}

	@Override
	public ListSingleSelectInputBuilder<T, ITEM> failWhenItemNotPresent(boolean failWhenItemNotPresent) {
		this.failWhenItemNotPresent = failWhenItemNotPresent;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#
	 * itemEnabledProvider(java.util. function.Predicate)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(Predicate<ITEM> itemEnabledProvider) {
		ObjectUtils.argumentNotNull(itemEnabledProvider, "Item enabled predicate must be not null");
		getComponent().setItemEnabledProvider(item -> itemEnabledProvider.test(item));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
	 * #items(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> items(ITEM... items) {
		return items(Arrays.asList(items));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
	 * #dataSource(com.vaadin.flow.data. provider.DataProvider)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> dataSource(DataProvider<ITEM, ?> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastoreListSingleSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
			DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter, Iterable<P> properties) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				DatastoreDataProvider.asPropertySet(properties), itemConverter, f -> null);
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreListSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastoreListSingleSelectInputBuilder<T, ITEM> dataSource(Datastore datastore, DataTarget<?> target) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				getItemType());
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreListSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(
	 * java.lang.Iterable)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
		this.items = CollectionUtils.iterableAsSet(items);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#
	 * addItem(java.lang.Object)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> addItem(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		this.items.add(item);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableListSingleSelectInputBuilder<T, ITEM> validatable() {
		return new DefaultValidatableListSingleSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
	 * boolean)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
	 * boolean)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> required(boolean required) {
		getComponent().setRequiredIndicatorVisible(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(
	 * )
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#
	 * enabled(boolean)
	 */
	@Override
	public ListSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	// ------- extended builders

	static class DefaultValidatableListSingleSelectInputBuilder<T, ITEM>
			implements ValidatableListSingleSelectInputBuilder<T, ITEM> {

		private final ListSingleSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableListSingleSelectInputBuilder(ListSingleSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.ComponentRenderer)
		 */
		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> failWhenItemNotPresent(boolean failWhenItemNotPresent) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> dataSource(DataProvider<ITEM, ?> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasBeanDatastoreDataProviderConfigurator#dataSource(com.
		 * holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function,
		 * java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> dataSource(
				Datastore datastore, DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter,
				Iterable<P> properties) {
			return new DefaultValidatableDatastoreListSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, itemConverter, properties));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasBeanDatastoreDataProviderConfigurator#dataSource(com.
		 * holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastoreListSingleSelectInputBuilder<>(builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #items(java.lang.Object[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> items(ITEM... items) {
			builder.items(items);
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withSelectionListener(
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withReadonlyChangeListener(
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> required(boolean required) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> id(String id) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> visible(boolean visible) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> elementConfiguration(Consumer<Element> element) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withDetachListener(
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> styleName(String styleName) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withDeferredLocalization(boolean deferredLocalization) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> width(String width) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> height(String height) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> addItem(ITEM item) {
			builder.addItem(item);
			return this;
		}

		@Override
		public <A> ValidatableListSingleSelectInputBuilder<T, ITEM> withAdapter(Class<A> type,
				Function<Input<T>, A> adapter) {
			builder.withAdapter(type, adapter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#withValidator(com. holonplatform.core.Validator)
		 */
		@Override
		public ValidatableListSingleSelectInputBuilder<T, ITEM> withValidator(Validator<T> validator) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> validationStatusHandler(
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> validateOnValueChange(boolean validateOnValueChange) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> required(Validator<T> validator) {
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
		public ValidatableListSingleSelectInputBuilder<T, ITEM> required(Localizable message) {
			validatableInputConfigurator.required(message);
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

	static class DefaultDatastoreListSingleSelectInputBuilder<T, ITEM>
			implements DatastoreListSingleSelectInputBuilder<T, ITEM> {

		private final ListSingleSelectInputBuilder<T, ITEM> builder;
		private final DatastoreDataProvider<ITEM, ?> datastoreDataProvider;

		public DefaultDatastoreListSingleSelectInputBuilder(ListSingleSelectInputBuilder<T, ITEM> builder,
				DatastoreDataProvider<ITEM, ?> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.ComponentRenderer)
		 */
		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem, Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> failWhenItemNotPresent(boolean failWhenItemNotPresent) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(Predicate<ITEM> itemEnabledProvider) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> validatable() {
			return new DefaultValidatableDatastoreListSingleSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
		 * boolean)
		 */
		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withReadonlyChangeListener(
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> required(boolean required) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public DatastoreListSingleSelectInputBuilder<T, ITEM> id(String id) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> visible(boolean visible) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> elementConfiguration(Consumer<Element> element) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withAttachListener(
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withDetachListener(
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withSelectionListener(
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> styleName(String styleName) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withDeferredLocalization(boolean deferredLocalization) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> width(String width) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> height(String height) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		@Override
		public <A> DatastoreListSingleSelectInputBuilder<T, ITEM> withAdapter(Class<A> type,
				Function<Input<T>, A> adapter) {
			builder.withAdapter(type, adapter);
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> withDefaultQuerySort(QuerySort defaultQuerySort) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> itemIdentifierProvider(
				Function<ITEM, Object> itemIdentifierProvider) {
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
		public DatastoreListSingleSelectInputBuilder<T, ITEM> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
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

	static class DefaultValidatableDatastoreListSingleSelectInputBuilder<T, ITEM>
			implements ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> {

		private final DatastoreListSingleSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableDatastoreListSingleSelectInputBuilder(
				DatastoreListSingleSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#
		 * itemEnabledProvider(java.util. function.Predicate)
		 */
		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.ComponentRenderer)
		 */
		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> failWhenItemNotPresent(
				boolean failWhenItemNotPresent) {
			builder.failWhenItemNotPresent(failWhenItemNotPresent);
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> dataSource(
				ListDataProvider<ITEM> dataProvider) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withSelectionListener(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withReadonlyChangeListener(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> required(boolean required) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> id(String id) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> visible(boolean visible) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> elementConfiguration(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withDetachListener(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> styleName(String styleName) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> width(String width) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> height(String height) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withDefaultQuerySort(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> itemIdentifierProvider(
				Function<ITEM, Object> itemIdentifierProvider) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
			return this;
		}

		@Override
		public <A> ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withAdapter(Class<A> type,
				Function<Input<T>, A> adapter) {
			builder.withAdapter(type, adapter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * ValidatableInputConfigurator#withValidator(com. holonplatform.core.Validator)
		 */
		@Override
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> withValidator(Validator<T> validator) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> validationStatusHandler(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> validateOnValueChange(
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> required(Validator<T> validator) {
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
		public ValidatableDatastoreListSingleSelectInputBuilder<T, ITEM> required(Localizable message) {
			validatableInputConfigurator.required(message);
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
