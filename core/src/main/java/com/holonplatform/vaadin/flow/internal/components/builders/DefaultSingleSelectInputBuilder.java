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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator.SingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.SingleSelectInputAdapter;
import com.holonplatform.vaadin.flow.internal.components.support.DeferrableItemLabelGenerator;
import com.holonplatform.vaadin.flow.internal.components.support.ExceptionSwallowingSupplier;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;

/**
 * Default {@link SingleSelectInputBuilder} implementation.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 *
 * @since 5.2.3
 */
public class DefaultSingleSelectInputBuilder<T, ITEM>
		extends AbstractLocalizableComponentConfigurator<Select<ITEM>, SingleSelectInputBuilder<T, ITEM>>
		implements SingleSelectInputBuilder<T, ITEM> {

	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasLabelConfigurator<Select<ITEM>> labelConfigurator;
	protected final DefaultHasPlaceholderConfigurator<Select<ITEM>> placeholderConfigurator;

	protected final List<ValueChangeListener<T, ValueChangeEvent<T>>> valueChangeListeners = new LinkedList<>();
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
	public DefaultSingleSelectInputBuilder(Class<? extends T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		super(new Select<>());
		ObjectUtils.argumentNotNull(type, "Selection value type must be not null");
		ObjectUtils.argumentNotNull(itemType, "Selection item type must be not null");
		ObjectUtils.argumentNotNull(itemConverter, "ItemConverter must be not null");
		this.type = type;
		this.itemType = itemType;
		this.itemConverter = itemConverter;

		enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), placeholder -> {
			getComponent().setPlaceholder(placeholder);
		}, this);
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
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected SingleSelectInputBuilder<T, ITEM> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public SingleSelect<T> build() {
		final Select<ITEM> component = getComponent();

		// check DataProvider
		if (!new ExceptionSwallowingSupplier<>(() -> component.getDataProvider()).get().isPresent()) {
			// default data provider
			component.setDataProvider(DataProvider.ofCollection(Collections.emptySet()));
		}

		// configure captions
		if (!customItemCaptionGenerator && !itemCaptions.isEmpty()) {
			component.setTextRenderer(
					new DeferrableItemLabelGenerator<>(itemCaptions, component, isDeferredLocalizationEnabled()));
		}

		// items
		if (!items.isEmpty()) {
			component.setItems(items);
		}

		final Input<ITEM> itemInput = Input.builder(component)
				.requiredPropertyHandler((f, c) -> f.isRequiredIndicatorVisible(),
						(f, c, v) -> f.setRequiredIndicatorVisible(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.focusOperation(f -> f.focus()).hasEnabledSupplier(f -> f).build();

		final Input<T> input = Input.from(itemInput, Converter.from(item -> Result.ok(itemConverter.getValue(item)),
				value -> itemConverter.getItem(value).orElse(null)));
		valueChangeListeners.forEach(listener -> input.addValueChangeListener(listener));

		final SingleSelect<T> select = new SingleSelectInputAdapter<>(input,
				() -> component.getDataProvider().refreshAll());
		selectionListeners.forEach(listener -> select.addSelectionListener(listener));
		return select;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectableInputConfigurator#withSelectionListener(com.
	 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> withSelectionListener(
			SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow.
	 * data.renderer.ComponentRenderer)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> renderer(
			ComponentRenderer<? extends Component, ITEM> renderer) {
		getComponent().setRenderer(renderer);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#itemLabelGenerator(java.util
	 * .function.Function)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> itemLabelGenerator(
			Function<ITEM, String> itemLabelGenerator) {
		ObjectUtils.argumentNotNull(itemLabelGenerator, "Item label generator must be not null");
		getComponent().setItemLabelGenerator(item -> itemLabelGenerator.apply(item));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionAllowed(
	 * boolean)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> emptySelectionAllowed(boolean emptySelectionAllowed) {
		getComponent().setEmptySelectionAllowed(emptySelectionAllowed);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionCaption(com.
	 * holonplatform.core.i18n.Localizable)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> emptySelectionCaption(Localizable emptySelectionCaption) {
		if (emptySelectionCaption != null) {
			getComponent().setEmptySelectionCaption(LocalizationProvider.localize(emptySelectionCaption).orElse(""));
		} else {
			getComponent().setEmptySelectionCaption("");
		}
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#itemEnabledProvider(java.util.
	 * function.Predicate)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> itemEnabledProvider(Predicate<ITEM> itemEnabledProvider) {
		ObjectUtils.argumentNotNull(itemEnabledProvider, "Item enabled predicate must be not null");
		getComponent().setItemEnabledProvider(item -> itemEnabledProvider.test(item));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com.
	 * holonplatform.vaadin.flow.components.ItemSet.ItemCaptionGenerator)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
			ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		ObjectUtils.argumentNotNull(itemCaptionGenerator, "ItemCaptionGenerator must be not null");
		getComponent().setTextRenderer(item -> itemCaptionGenerator.getItemCaption(item));
		this.customItemCaptionGenerator = true;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaption(java.lang.
	 * Object, com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SingleSelectInputBuilder<T, ITEM> items(ITEM... items) {
		return items(Arrays.asList(items));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#dataSource(com.vaadin.flow.data.
	 * provider.DataProvider)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> dataSource(DataProvider<ITEM, ?> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget, java.util.function.Function,
	 * java.lang.Iterable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Property> DatastoreSingleSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
			DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter, Iterable<P> properties) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				DatastoreDataProvider.asPropertySet(properties), itemConverter, f -> null);
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreItemNativeModeSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
	 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public DatastoreSingleSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
			DataTarget<?> target) {
		final DatastoreDataProvider<ITEM, ?> datastoreDataProvider = DatastoreDataProvider.create(datastore, target,
				getItemType());
		getComponent().setDataProvider(datastoreDataProvider);
		return new DefaultDatastoreItemNativeModeSingleSelectInputBuilder<>(this, datastoreDataProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#dataSource(com.vaadin.flow.
	 * data.provider.ListDataProvider)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> dataSource(ListDataProvider<ITEM> dataProvider) {
		getComponent().setDataProvider(dataProvider);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
		this.items = (items != null) ? ConversionUtils.iterableAsSet(items) : new HashSet<>();
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> addItem(ITEM item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		this.items.add(item);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableSingleSelectInputBuilder<T, ITEM> validatable() {
		return new DefaultValidatableItemNativeModeSingleSelectInputBuilder<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
	public SingleSelectInputBuilder<T, ITEM> withValueChangeListener(
			ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> required(boolean required) {
		getComponent().setRequiredIndicatorVisible(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> placeholder(Localizable placeholder) {
		placeholderConfigurator.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public SingleSelectInputBuilder<T, ITEM> tabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public SingleSelectInputBuilder<T, ITEM> withFocusListener(
			ComponentEventListener<FocusEvent<Component>> listener) {
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<Select<ITEM>>>() {

			@Override
			public void onComponentEvent(FocusEvent<Select<ITEM>> event) {
				listener.onComponentEvent(new FocusEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public SingleSelectInputBuilder<T, ITEM> withBlurListener(
			ComponentEventListener<BlurEvent<Component>> listener) {
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<Select<ITEM>>>() {

			@Override
			public void onComponentEvent(BlurEvent<Select<ITEM>> event) {
				listener.onComponentEvent(new BlurEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusShortcut(com.vaadin.flow.
	 * component.Key)
	 */
	@Override
	public ShortcutConfigurator<SingleSelectInputBuilder<T, ITEM>> withFocusShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addFocusShortcut(key), getConfigurator());
	}

	// ------- extended builders

	static class DefaultValidatableItemNativeModeSingleSelectInputBuilder<T, ITEM>
			implements ValidatableSingleSelectInputBuilder<T, ITEM> {

		private final SingleSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableItemNativeModeSingleSelectInputBuilder(
				SingleSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.ComponentRenderer)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#itemLabelGenerator(java.
		 * util.function.Function)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> itemLabelGenerator(
				Function<ITEM, String> itemLabelGenerator) {
			builder.itemLabelGenerator(itemLabelGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionAllowed(
		 * boolean)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> emptySelectionAllowed(
				boolean emptySelectionAllowed) {
			builder.emptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionCaption(
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> emptySelectionCaption(
				Localizable emptySelectionCaption) {
			builder.emptySelectionCaption(emptySelectionCaption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#itemEnabledProvider(java.util.
		 * function.Predicate)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#dataSource(com.vaadin.flow.data
		 * .provider.DataProvider)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> dataSource(
				DataProvider<ITEM, ?> dataProvider) {
			builder.dataSource(dataProvider);
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
		public <P extends Property> ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> dataSource(
				Datastore datastore, DataTarget<?> target, Function<PropertyBox, ITEM> itemConverter,
				Iterable<P> properties) {
			return new DefaultValidatableDatastoreItemNativeModeSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target, itemConverter, properties));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasBeanDatastoreDataProviderConfigurator#dataSource(com.
		 * holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> dataSource(Datastore datastore,
				DataTarget<?> target) {
			return new DefaultValidatableDatastoreItemNativeModeSingleSelectInputBuilder<>(
					builder.dataSource(datastore, target));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasDataProviderConfigurator#items(java.lang.Object[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> items(ITEM... items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> dataSource(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> elementConfiguration(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> label(Localizable label) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> tabIndex(int tabIndex) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withFocusListener(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> withBlurListener(
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
		public ShortcutConfigurator<ValidatableSingleSelectInputBuilder<T, ITEM>> withFocusShortcut(
				Key key) {
			return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#items(java.lang.Iterable)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> items(Iterable<ITEM> items) {
			builder.items(items);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasItemsConfigurator#addItem(java.lang.Object)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> addItem(ITEM item) {
			builder.addItem(item);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ValidatableInputConfigurator#withValidator(com.
		 * holonplatform.core.Validator)
		 */
		@Override
		public ValidatableSingleSelectInputBuilder<T, ITEM> withValidator(Validator<T> validator) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> validationStatusHandler(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> validateOnValueChange(
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> required(Validator<T> validator) {
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
		public ValidatableSingleSelectInputBuilder<T, ITEM> required(Localizable message) {
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

	static class DefaultDatastoreItemNativeModeSingleSelectInputBuilder<T, ITEM>
			implements DatastoreSingleSelectInputBuilder<T, ITEM> {

		private final SingleSelectInputBuilder<T, ITEM> builder;
		private final DatastoreDataProvider<ITEM, ?> datastoreDataProvider;

		public DefaultDatastoreItemNativeModeSingleSelectInputBuilder(
				SingleSelectInputBuilder<T, ITEM> builder,
				DatastoreDataProvider<ITEM, ?> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.ComponentRenderer)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#itemLabelGenerator(java.
		 * util.function.Function)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> itemLabelGenerator(
				Function<ITEM, String> itemLabelGenerator) {
			builder.itemLabelGenerator(itemLabelGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionAllowed(
		 * boolean)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> emptySelectionAllowed(
				boolean emptySelectionAllowed) {
			builder.emptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionCaption(
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> emptySelectionCaption(
				Localizable emptySelectionCaption) {
			builder.emptySelectionCaption(emptySelectionCaption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#itemEnabledProvider(java.util.
		 * function.Predicate)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item, Localizable caption) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> dataSource(
				ListDataProvider<ITEM> dataProvider) {
			builder.dataSource(dataProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> validatable() {
			return new DefaultValidatableDatastoreItemNativeModeSingleSelectInputBuilder<>(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> required(boolean required) {
			builder.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> elementConfiguration(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withAttachListener(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> label(Localizable label) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> placeholder(Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> tabIndex(int tabIndex) {
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withFocusListener(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withBlurListener(
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
		public ShortcutConfigurator<DatastoreSingleSelectInputBuilder<T, ITEM>> withFocusShortcut(
				Key key) {
			return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public DatastoreSingleSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> withDefaultQuerySort(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> itemIdentifierProvider(
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
		public DatastoreSingleSelectInputBuilder<T, ITEM> querySortOrderConverter(
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

	static class DefaultValidatableDatastoreItemNativeModeSingleSelectInputBuilder<T, ITEM>
			implements ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> {

		private final DatastoreSingleSelectInputBuilder<T, ITEM> builder;
		private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

		public DefaultValidatableDatastoreItemNativeModeSingleSelectInputBuilder(
				DatastoreSingleSelectInputBuilder<T, ITEM> builder) {
			super();
			this.builder = builder;
			this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasItemEnableConfigurator#itemEnabledProvider(java.util.
		 * function.Predicate)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> itemEnabledProvider(
				Predicate<ITEM> itemEnabledProvider) {
			builder.itemEnabledProvider(itemEnabledProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#renderer(com.vaadin.flow
		 * .data.renderer.ComponentRenderer)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> renderer(
				ComponentRenderer<? extends Component, ITEM> renderer) {
			builder.renderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#itemLabelGenerator(java.
		 * util.function.Function)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> itemLabelGenerator(
				Function<ITEM, String> itemLabelGenerator) {
			builder.itemLabelGenerator(itemLabelGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionAllowed(
		 * boolean)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> emptySelectionAllowed(
				boolean emptySelectionAllowed) {
			builder.emptySelectionAllowed(emptySelectionAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.NativeModeSingleSelectInputBuilder#emptySelectionCaption(
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> emptySelectionCaption(
				Localizable emptySelectionCaption) {
			builder.emptySelectionCaption(emptySelectionCaption);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder#itemCaptionGenerator(com
		 * .holonplatform.vaadin.flow.components.builders.ItemSetConfigurator.ItemCaptionGenerator)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> itemCaptionGenerator(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> itemCaption(ITEM item,
				Localizable caption) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> dataSource(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withSelectionListener(
				SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> readOnly(boolean readOnly) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withValueChangeListener(
				ValueChangeListener<T, ValueChangeEvent<T>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> required(boolean required) {
			validatableInputConfigurator.required(required);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> required() {
			return required(true);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> elementConfiguration(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withAttachListener(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withDetachListener(
				ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withThemeName(String themeName) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withEventListener(String eventType,
				DomEventListener listener, String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> enabled(boolean enabled) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withDeferredLocalization(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> maxHeight(String maxHeight) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> label(Localizable label) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> placeholder(
				Localizable placeholder) {
			builder.placeholder(placeholder);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> autofocus(boolean autofocus) {
			builder.autofocus(autofocus);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> tabIndex(int tabIndex) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withFocusListener(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withBlurListener(
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
		public ShortcutConfigurator<ValidatableDatastoreSingleSelectInputBuilder<T, ITEM>> withFocusShortcut(
				Key key) {
			return new DelegatedShortcutConfigurator<>(builder.withFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withQueryConfigurationProvider(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withDefaultQuerySort(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> itemIdentifierProvider(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> querySortOrderConverter(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> withValidator(
				Validator<T> validator) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> validationStatusHandler(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> validateOnValueChange(
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> required(Validator<T> validator) {
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
		public ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> required(Localizable message) {
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
