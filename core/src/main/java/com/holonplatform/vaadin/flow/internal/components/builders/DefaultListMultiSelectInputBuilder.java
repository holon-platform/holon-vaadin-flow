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
import java.util.stream.Collectors;

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
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.ListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.MultiSelectInputAdapter;
import com.holonplatform.vaadin.flow.internal.components.support.DeferrableItemLabelGenerator;
import com.holonplatform.vaadin.flow.internal.components.support.ExceptionSwallowingSupplier;
import com.holonplatform.vaadin.flow.internal.utils.CollectionUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;

/**
 * Default {@link ListMultiSelectInputBuilder} implementation.
 *
 * @param <T>    Value type
 * @param <ITEM> Item type
 *
 * @since 5.4.0
 */
public class DefaultListMultiSelectInputBuilder<T, ITEM> extends
		AbstractInputConfigurator<Set<T>, ValueChangeEvent<Set<T>>, MultiSelectListBox<ITEM>, ListMultiSelectInputBuilder<T, ITEM>>
		implements ListMultiSelectInputBuilder<T, ITEM> {

	protected final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	private final Class<? extends T> type;
	private final Class<ITEM> itemType;

	protected final ItemConverter<T, ITEM> itemConverter;

	protected Set<ITEM> items = new LinkedHashSet<>();

	protected final Map<ITEM, Localizable> itemCaptions = new HashMap<>();

	protected boolean customItemCaptionGenerator = false;

	/**
	 * Constructor.
	 * @param type          Selection value type (not null)
	 * @param itemType      Selection items type
	 * @param itemConverter The item converter to use (not null)
	 */
	public DefaultListMultiSelectInputBuilder(Class<? extends T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		super(new MultiSelectListBox<>(), InputAdaptersContainer.create());
		ObjectUtils.argumentNotNull(type, "Selection value type must be not null");
		ObjectUtils.argumentNotNull(itemType, "Selection item type must be not null");
		ObjectUtils.argumentNotNull(itemConverter, "ItemConverter must be not null");
		this.type = type;
		this.itemType = itemType;
		this.itemConverter = itemConverter;
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
	protected ListMultiSelectInputBuilder<T, ITEM> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public MultiSelect<T> build() {
		final MultiSelectListBox<ITEM> component = getComponent();

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

		final Input<Set<ITEM>> itemInput = Input.builder(component).requiredPropertyHandler((f, c) -> {
			return false;
			// TODO not supported by web component at time of writing
			// return f.isRequiredIndicatorVisible();
		}, (f, c, v) -> {
			// TODO not supported by web component at time of writing
			// f.setRequiredIndicatorVisible(v);
		}).isEmptySupplier(f -> f.getValue() == null || f.getValue().isEmpty()).build();

		final MultiSelectInputAdapter<T, ITEM, MultiSelectListBox<ITEM>> multiSelect = new MultiSelectInputAdapter<>(
				itemInput, new MultiSelectListBoxItemConverterAdapter<>(component, itemConverter),
				ms -> component.getDataProvider().refreshAll(), () -> {
					if (component.getDataProvider() != null) {
						return component.getDataProvider().fetch(new Query<>()).collect(Collectors.toSet());
					}
					return null;
				});
		selectionListeners.forEach(listener -> multiSelect.addSelectionListener(listener));
		getValueChangeListeners().forEach(listener -> multiSelect.addValueChangeListener(listener));
		getReadonlyChangeListeners().forEach(listener -> multiSelect.addReadonlyChangeListener(listener));
		getAdapters().getAdapters().forEach((t, a) -> multiSelect.setAdapter(t, a));

		return multiSelect;
	}

	private static class MultiSelectListBoxItemConverterAdapter<T, ITEM> implements ItemConverter<T, ITEM> {

		private final MultiSelectListBox<ITEM> component;
		private final ItemConverter<T, ITEM> converter;

		public MultiSelectListBoxItemConverterAdapter(MultiSelectListBox<ITEM> component,
				ItemConverter<T, ITEM> converter) {
			super();
			this.component = component;
			this.converter = converter;
		}

		@Override
		public T getValue(ITEM item) {
			return converter.getValue(item);
		}

		@Override
		public Optional<ITEM> getItem(T value) {
			return converter.getItem(value).filter(i -> component.getItemPosition(i) > -1);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableListMultiSelectInputBuilder<T, ITEM> validatable() {
		return new DefaultValidatableListMultiSelectInputBuilder<>(this);
	}

	@Override
	public ListMultiSelectInputBuilder<T, ITEM> renderer(ComponentRenderer<? extends Component, ITEM> renderer) {
		getComponent().setRenderer(renderer);
		return getConfigurator();
	}

	@Override
	public ListMultiSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		getComponent().addComponentAsFirst(component);
		return getConfigurator();
	}

	@Override
	public ListMultiSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem, Component component) {
		ObjectUtils.argumentNotNull(beforeItem, "Item must be not null");
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		getComponent().prependComponents(beforeItem, component);
		return getConfigurator();
	}

	@Override
	public ListMultiSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem, Component component) {
		ObjectUtils.argumentNotNull(afterItem, "Item must be not null");
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		getComponent().addComponents(afterItem, component);
		return getConfigurator();
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
	public ListMultiSelectInputBuilder<T, ITEM> withSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com.
	 * vaadin.flow.function.SerializablePredicate)
	 */
	@Override
	public ListMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(Predicate<ITEM> itemEnabledProvider) {
		ObjectUtils.argumentNotNull(itemEnabledProvider, "Item enabled predicate must be not null");
		getComponent().setItemEnabledProvider(item -> itemEnabledProvider.test(item));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com.
	 * holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator)
	 */
	@Override
	public ListMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		ObjectUtils.argumentNotNull(itemCaptionGenerator, "ItemCaptionGenerator must be not null");
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
	public ListMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.
	 * HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore,
	 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastoreListMultiSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
			DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter, Iterable<P> properties) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				DatastoreDataProvider.asPropertySet(properties), itemConverter, f -> null);
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreListMultiSelectInputBuilder<>(this, datastoreDataProvider);
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
	public DatastoreListMultiSelectInputBuilder<T, ITEM> dataSource(Datastore datastore, DataTarget<?> target) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				getItemType());
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreListMultiSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
	 * #dataSource(com.vaadin.flow.data. provider.DataProvider)
	 */
	@Override
	public ListMultiSelectInputBuilder<T, ITEM> dataSource(DataProvider<ITEM, ?> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return this;
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
	public ListMultiSelectInputBuilder<T, ITEM> items(ITEM... items) {
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
	public ListMultiSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
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
	public ListMultiSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
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
	public ListMultiSelectInputBuilder<T, ITEM> addItem(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		this.items.add(item);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(
	 * boolean)
	 */
	@Override
	public ListMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
	public ListMultiSelectInputBuilder<T, ITEM> required(boolean required) {
		// TODO not supported at time of writing
		// getComponent().setRequiredIndicatorVisible(required);
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
	public ListMultiSelectInputBuilder<T, ITEM> required() {
		return required(true);
	}

	// ------- extended builders

	static class DefaultValidatableListMultiSelectInputBuilder<T, ITEM>
			implements ValidatableListMultiSelectInputBuilder<T, ITEM> {

		private final ListMultiSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<Set<T>> validatableInputConfigurator;

		public DefaultValidatableListMultiSelectInputBuilder(ListMultiSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> width(String width) {
			builder.width(width);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> height(String height) {
			builder.height(height);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem, Component component) {
			builder.withComponentAfter(afterItem, component);
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withSelectionListener(
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withReadonlyChangeListener(
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> id(String id) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> visible(boolean visible) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> elementConfiguration(Consumer<Element> element) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withDetachListener(
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withDeferredLocalization(boolean deferredLocalization) {
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
		 * HasBeanDatastoreDataProviderConfigurator#dataSource(com.
		 * holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function,
		 * java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> dataSource(
				Datastore datastore, DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter,
				Iterable<P> properties) {
			return new DefaultValidatableDatastoreListMultiSelectInputBuilder<>(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastoreListMultiSelectInputBuilder<>(builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator
		 * #dataSource(com.vaadin.flow.data .provider.DataProvider)
		 */
		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> dataSource(DataProvider<ITEM, ?> dataProvider) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
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
		@SuppressWarnings("unchecked")
		@Override
		public ValidatableListMultiSelectInputBuilder<T, ITEM> items(ITEM... items) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> addItem(ITEM item) {
			builder.addItem(item);
			return this;
		}

		@Override
		public <A> ValidatableListMultiSelectInputBuilder<T, ITEM> withAdapter(Class<A> type,
				Function<Input<Set<T>>, A> adapter) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> withValidator(Validator<Set<T>> validator) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> validationStatusHandler(
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> validateOnValueChange(boolean validateOnValueChange) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> required(Validator<Set<T>> validator) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> required(Localizable message) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> required(boolean required) {
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
		public ValidatableListMultiSelectInputBuilder<T, ITEM> required() {
			return required(true);
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

	static class DefaultDatastoreListMultiSelectInputBuilder<T, ITEM>
			implements DatastoreListMultiSelectInputBuilder<T, ITEM> {

		private final DefaultListMultiSelectInputBuilder<T, ITEM> builder;
		private final DatastoreDataProvider<ITEM, ?> datastoreDataProvider;

		public DefaultDatastoreListMultiSelectInputBuilder(DefaultListMultiSelectInputBuilder<T, ITEM> builder,
				DatastoreDataProvider<ITEM, ?> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> width(String width) {
			builder.width(width);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> height(String height) {
			builder.height(height);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem, Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem, Component component) {
			builder.withComponentAfter(afterItem, component);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> validatable() {
			return new DefaultValidatableDatastoreListMultiSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * OptionsModeSingleSelectInputBuilder#itemEnabledProvider(com
		 * .vaadin.flow.function.SerializablePredicate)
		 */
		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(Predicate<ITEM> itemEnabledProvider) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
			builder.dataSource(dataProvider);
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withReadonlyChangeListener(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> required(boolean required) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> required() {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> id(String id) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> visible(boolean visible) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> elementConfiguration(Consumer<Element> element) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withAttachListener(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withDetachListener(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withSelectionListener(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withDeferredLocalization(boolean deferredLocalization) {
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

		@Override
		public <A> DatastoreListMultiSelectInputBuilder<T, ITEM> withAdapter(Class<A> type,
				Function<Input<Set<T>>, A> adapter) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> withDefaultQuerySort(QuerySort defaultQuerySort) {
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> itemIdentifierProvider(
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
		public DatastoreListMultiSelectInputBuilder<T, ITEM> querySortOrderConverter(
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
		public MultiSelect<T> build() {
			return builder.build();
		}

	}

	static class DefaultValidatableDatastoreListMultiSelectInputBuilder<T, ITEM>
			implements ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> {

		private final DatastoreListMultiSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<Set<T>> validatableInputConfigurator;

		public DefaultValidatableDatastoreListMultiSelectInputBuilder(
				DatastoreListMultiSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> width(String width) {
			builder.width(width);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> height(String height) {
			builder.height(height);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withPrefixComponent(Component component) {
			builder.withPrefixComponent(component);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withComponentBefore(ITEM beforeItem,
				Component component) {
			builder.withComponentBefore(beforeItem, component);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withComponentAfter(ITEM afterItem,
				Component component) {
			builder.withComponentAfter(afterItem, component);
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> itemCaptionGenerator(
				ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> dataSource(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withSelectionListener(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withReadonlyChangeListener(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> id(String id) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> visible(boolean visible) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> elementConfiguration(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withDetachListener(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> styleName(String styleName) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withDefaultQuerySort(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> itemIdentifierProvider(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			builder.querySortOrderConverter(querySortOrderConverter);
			return this;
		}

		@Override
		public <A> ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withAdapter(Class<A> type,
				Function<Input<Set<T>>, A> adapter) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> withValidator(Validator<Set<T>> validator) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> validationStatusHandler(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> validateOnValueChange(
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> required(Validator<Set<T>> validator) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> required(Localizable message) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> required(boolean required) {
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
		public ValidatableDatastoreListMultiSelectInputBuilder<T, ITEM> required() {
			return required(true);
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
