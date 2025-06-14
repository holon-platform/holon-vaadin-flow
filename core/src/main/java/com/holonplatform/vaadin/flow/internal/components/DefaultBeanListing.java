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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder.DatastoreBeanListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.components.events.ColumnReorderListener;
import com.holonplatform.vaadin.flow.components.events.ColumnResizeListener;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.components.events.ItemClickEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEventListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingDnDListener;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragEndEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragStartEvent;
import com.holonplatform.vaadin.flow.components.events.ItemListingDropEvent;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultShortcutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;

/**
 * Default {@link BeanListing} implementation.
 * @param <T> Bean type
 * @since 5.2.0
 */
public class DefaultBeanListing<T> extends AbstractItemListing<T, String> implements BeanListing<T> {

	private static final long serialVersionUID = -4447503694235650581L;

	/**
	 * Bean type
	 */
	private final Class<T> beanType;

	/**
	 * Bean property set
	 */
	private final BeanPropertySet<T> propertySet;

	/**
	 * Virtual columns
	 */
	private final Set<String> virtualColumnProperties = new HashSet<>(4);

	/**
	 * Constructor.
	 * @param beanType Bean type (not null)
	 */
	public DefaultBeanListing(Class<T> beanType) {
		super();
		ObjectUtils.argumentNotNull(beanType, "Bean type must be not null");
		this.beanType = beanType;
		this.propertySet = BeanPropertySet.create(beanType);
		// add properties as columns
		for (PathProperty<?> property : propertySet) {
			addPropertyColumn(property.relativeName());
		}
	}

	/**
	 * Get the bean class.
	 * @return the bean type
	 */
	protected Class<T> getBeanType() {
		return beanType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasPropertySet#getProperties()
	 */
	@Override
	public Collection<String> getProperties() {
		return propertySet.stream().map(p -> p.relativeName()).collect(Collectors.toList());
	}

	/**
	 * Add a virtual column.
	 * @return The property id
	 */
	protected String addColumnProperty() {
		final String id = "_!virtual#" + virtualColumnProperties.size();
		virtualColumnProperties.add(id);
		addPropertyColumn(id);
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * isReadOnlyByDefault(java.lang.Object)
	 */
	@Override
	protected boolean isReadOnlyByDefault(String property) {
		return property != null && virtualColumnProperties.contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * generateColumnKey(java.lang.Object)
	 */
	@Override
	protected String generateColumnKey(String property) {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * generateDefaultGridColumn(com.holonplatform
	 * .vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@Override
	protected Column<T> generateDefaultGridColumn(ItemListingColumn<String, T, ?> configuration) {
		final String property = configuration.getProperty();
		return getGrid().addColumn(item -> {
			return propertySet.getProperty(property).map(p -> p.present(propertySet.read(p, item))).orElse(null);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * preProcessConfiguration(com.holonplatform.
	 * vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@Override
	protected ItemListingColumn<String, T, ?> preProcessConfiguration(ItemListingColumn<String, T, ?> configuration) {
		if (configuration.getSortProperties().isEmpty()) {
			configuration.setSortProperties(Collections.singletonList(configuration.getProperty()));
		}
		if (configuration.getSortMode() == SortMode.DEFAULT) {
			configuration.setSortMode(SortMode.ENABLED);
		}
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getSortPropertyName(java.lang.Object)
	 */
	@Override
	protected Optional<String> getSortPropertyName(String property) {
		return Optional.of(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getDefaultColumnHeader(java.lang.Object)
	 */
	@Override
	protected Optional<Localizable> getDefaultColumnHeader(String property) {
		return propertySet.getProperty(property).map(p -> {
			if (p.getMessage() != null || p.getMessageCode() != null) {
				return Localizable.builder().message((p.getMessage() != null) ? p.getMessage() : p.getName())
						.messageCode(p.getMessageCode()).build();
			}
			return Localizable.of(p.getName());
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getPropertyType(java.lang.Object)
	 */
	@Override
	protected Class<?> getPropertyType(String property) {
		return propertySet.property(property).getType();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getPropertyValueGetter(java.lang.Object)
	 */
	@Override
	protected ValueProvider<T, ?> getPropertyValueGetter(String property) {
		return item -> propertySet.read(property, item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getPropertyValueSetter(java.lang.Object)
	 */
	@Override
	protected Optional<Setter<T, ?>> getPropertyValueSetter(String property) {
		return Optional.of((item, value) -> propertySet.write(property, value, item));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * buildPropertyEditor(com.holonplatform. vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <V> Optional<Input<V>> buildPropertyEditor(ItemListingColumn<String, T, V> configuration) {
		return propertySet.getProperty(configuration.getProperty()).flatMap(p -> {
			final Property<V> property = (Property<V>) p;
			// check custom renderer
			Optional<Input<V>> component = configuration.getEditorInputRenderer().map(r -> r.render(property));
			if (component.isPresent()) {
				return component;
			}
			// check specific registry
			if (getPropertyRendererRegistry().isPresent()) {
				return getPropertyRendererRegistry().get().getRenderer(Input.class, property)
						.map(r -> (Input<V>) r.render(property));
			} else {
				// use default
				return property.renderIfAvailable(Input.class).map(c -> (Input<V>) c);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * getDefaultPropertyValidators(java.lang. Object)
	 */
	@Override
	protected Collection<Validator<Object>> getDefaultPropertyValidators(String property) {
		return propertySet.getProperty(property).map(p -> p.getValidators()).orElse(Collections.emptyList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#
	 * refreshVirtualProperties()
	 */
	@Override
	protected void refreshVirtualProperties() {
		// noop
	}

	// ------- Builder

	/**
	 * Default {@link BeanListingBuilder} implementation.
	 * @param <T> Bean type
	 */
	public static class DefaultBeanListingBuilder<T> extends
			AbstractItemListingConfigurator<T, String, BeanListing<T>, DefaultBeanListing<T>, BeanListingBuilder<T>>
			implements BeanListingBuilder<T> {

		public DefaultBeanListingBuilder(Class<T> beanType) {
			super(new DefaultBeanListing<>(beanType));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.
		 * AbstractItemListingConfigurator# getItemListing()
		 */
		@Override
		protected BeanListing<T> getItemListing() {
			return getInstance();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder#
		 * withValidator(java.lang.String, com.holonplatform.core.Validator)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public BeanListingBuilder<T> withValidator(String property, Validator<?> validator) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validator, "Validator must be not null");
			getInstance().getColumnConfiguration(property).addValidator((Validator) validator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder#editor(
		 * java.lang.String, com.holonplatform.vaadin.flow.components.Input)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public BeanListingBuilder<T> editor(String property, Input<?> editor) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property)
					.setEditorInputRenderer(InputPropertyRenderer.create(p -> (Input) editor));
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
		 * BeanInputGroupConfigurator# defaultValue(java.lang.String, java.util.function.Function)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public BeanListingBuilder<T> defaultValue(String property, Supplier<Object> defaultValueProvider) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).setDefaultValueProvider((Supplier) defaultValueProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
		 * BeanInputGroupConfigurator# withValueChangeListener(java.lang.String,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public BeanListingBuilder<T> withValueChangeListener(String property,
				ValueChangeListener<?, GroupValueChangeEvent<?, String, Input<?>, EditorComponentGroup<String, T>>> listener) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).addValueChangeListener((ValueChangeListener) listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withComponentColumn(com.vaadin.flow .function.ValueProvider)
		 */
		@Override
		public ItemListingColumnBuilder<T, String, BeanListing<T>, BeanListingBuilder<T>> withComponentColumn(
				ValueProvider<T, Component> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			final String columnId = getInstance().addColumnProperty();
			getInstance().getColumnConfiguration(columnId).setRenderer(new ComponentRenderer<>(valueProvider));
			return new DefaultItemListingColumnBuilder<>(columnId, getInstance(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withColumn(com.vaadin.flow.function .ValueProvider)
		 */
		@Override
		public <X> ItemListingColumnBuilder<T, String, BeanListing<T>, BeanListingBuilder<T>> withColumn(
				ValueProvider<T, X> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			final String columnId = getInstance().addColumnProperty();
			getInstance().getColumnConfiguration(columnId).setValueProvider(new ValueProviderAdapter<>(valueProvider));
			return new DefaultItemListingColumnBuilder<>(columnId, getInstance(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasDatastoreDataProviderConfigurator#dataSource(com. holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.util.function.Function, java.lang.Iterable)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> DatastoreBeanListingBuilder<T> dataSource(Datastore datastore, DataTarget<?> target,
				Function<PropertyBox, T> itemConverter, Iterable<P> properties) {
			final DatastoreDataProvider<T, QueryFilter> datastoreDataProvider = DatastoreDataProvider.create(datastore,
					target, DatastoreDataProvider.asPropertySet(properties), itemConverter, Function.identity());
			getInstance().setDataProvider(datastoreDataProvider);
			return new DefaultDatastoreBeanListingBuilder<>(this, datastoreDataProvider);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * HasDatastoreDataProviderConfigurator#dataSource(com. holonplatform.core.datastore.Datastore,
		 * com.holonplatform.core.datastore.DataTarget, java.lang.Class)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> dataSource(Datastore datastore, DataTarget<?> target) {
			final DatastoreDataProvider<T, QueryFilter> datastoreDataProvider = DatastoreDataProvider.create(datastore,
					target, getInstance().getBeanType());
			getInstance().setDataProvider(datastoreDataProvider);
			return new DefaultDatastoreBeanListingBuilder<>(this, datastoreDataProvider);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.
		 * AbstractItemListingConfigurator# getConfigurator()
		 */
		@Override
		public BeanListingBuilder<T> getConfigurator() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
		 */
		@Override
		public BeanListing<T> build() {
			return configureAndBuild();
		}

	}

	public static class DefaultDatastoreBeanListingBuilder<T> implements DatastoreBeanListingBuilder<T> {

		private final DefaultBeanListingBuilder<T> builder;
		private final DatastoreDataProvider<T, QueryFilter> datastoreDataProvider;

		public DefaultDatastoreBeanListingBuilder(DefaultBeanListingBuilder<T> builder,
				DatastoreDataProvider<T, QueryFilter> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BeanListingConfigurator#
		 * withValidator(java.lang.String, com.holonplatform.core.Validator)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withValidator(String property, Validator<?> validator) {
			builder.withValidator(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BeanListingConfigurator#
		 * editor(java.lang.String, com.holonplatform.vaadin.flow.components.Input)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> editor(String property, Input<?> editor) {
			builder.editor(property, editor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders. DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query. QueryConfigurationProvider)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withQueryConfigurationProvider(
				QueryConfigurationProvider queryConfigurationProvider) {
			datastoreDataProvider.addQueryConfigurationProvider(queryConfigurationProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#withDefaultQuerySort(com. holonplatform.core.query.QuerySort)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withDefaultQuerySort(QuerySort defaultQuerySort) {
			datastoreDataProvider.setDefaultSort(defaultQuerySort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#itemIdentifierProvider( java.util.function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> itemIdentifierProvider(Function<T, Object> itemIdentifierProvider) {
			datastoreDataProvider.setItemIdentifier(itemIdentifierProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.
		 * DatastoreDataProviderConfigurator#querySortOrderConverter( java.util.function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * editorComponent(java.lang.Object, java.util.function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> editorComponent(String property,
				Function<T, ? extends Component> editorComponentProvider) {
			builder.editorComponent(property, editorComponentProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayAsFirst(java.lang.Object)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> displayAsFirst(String property) {
			builder.displayAsFirst(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayAsLast(java.lang.Object)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> displayAsLast(String property) {
			builder.displayAsLast(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayBefore(java.lang.Object, java.lang.Object)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> displayBefore(String property, String beforeProperty) {
			builder.displayBefore(property, beforeProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * displayAfter(java.lang.Object, java.lang.Object)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> displayAfter(String property, String afterProperty) {
			builder.displayAfter(property, afterProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * visibleColumns(java.util.List)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> visibleColumns(List<? extends String> visibleColumns) {
			builder.visibleColumns(visibleColumns);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# sortable(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> sortable(boolean sortable) {
			builder.sortable(sortable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortable(java.lang.Object, boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> sortable(String property, boolean sortable) {
			builder.sortable(property, sortable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * resizable(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> resizable(boolean resizable) {
			builder.resizable(resizable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * resizable(java.lang.Object, boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> resizable(String property, boolean resizable) {
			builder.resizable(property, resizable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * visible(java.lang.Object, boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> visible(String property, boolean visible) {
			builder.visible(property, visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * readOnly(java.lang.Object, boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> readOnly(String property, boolean readOnly) {
			builder.readOnly(property, readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * frozen(java.lang.Object, boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> frozen(String property, boolean frozen) {
			builder.frozen(property, frozen);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * frozenColumns(int)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> frozenColumns(int frozenColumnsCount) {
			builder.frozenColumns(frozenColumnsCount);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * width(java.lang.Object, java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> width(String property, String width) {
			builder.width(property, width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * flexGrow(java.lang.Object, int)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> flexGrow(String property, int flexGrow) {
			builder.flexGrow(property, flexGrow);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * styleNameGenerator(java.util. function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> styleNameGenerator(Function<T, String> styleNameGenerator) {
			builder.styleNameGenerator(styleNameGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * styleNameGenerator(java.lang. Object, java.util.function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> styleNameGenerator(String property,
				Function<T, String> styleNameGenerator) {
			builder.styleNameGenerator(property, styleNameGenerator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * expand(java.lang.Object)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> expand(String property) {
			builder.expand(property);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> autoWidth(String property, boolean autoWidth) {
			builder.autoWidth(property, autoWidth);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> columnsAutoWidth() {
			builder.columnsAutoWidth();
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * alignment(java.lang.Object,
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator. ColumnAlignment)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> alignment(String property, ColumnAlignment alignment) {
			builder.alignment(property, alignment);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * renderer(java.lang.Object, com.vaadin.flow.data.renderer.Renderer)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> renderer(String property, Renderer<T> renderer) {
			builder.renderer(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * valueProvider(java.lang.Object, com.vaadin.flow.function.ValueProvider)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> valueProvider(String property, ValueProvider<T, String> valueProvider) {
			builder.valueProvider(property, valueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortComparator(java.lang.Object, java.util.Comparator)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> sortComparator(String property, Comparator<T> comparator) {
			builder.sortComparator(property, comparator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortUsing(java.lang.Object, java.util.List)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> sortUsing(String property, List<String> sortProperties) {
			builder.sortUsing(property, sortProperties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * sortProvider(java.lang.Object, java.util.function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> sortProvider(String property,
				Function<SortDirection, Stream<ItemSort<String>>> sortProvider) {
			builder.sortProvider(property, sortProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * header(java.lang.Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> header(String property, Localizable header) {
			builder.header(property, header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * headerComponent(java.lang.Object, com.vaadin.flow.component.Component)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> headerComponent(String property, Component header) {
			builder.headerComponent(property, header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * footer(java.lang.Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> footer(String property, Localizable footer) {
			builder.footer(property, footer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * footerComponent(java.lang.Object, com.vaadin.flow.component.Component)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> footerComponent(String property, Component footer) {
			builder.footerComponent(property, footer);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> withColumnPostProcessor(ColumnPostProcessor<String> columnPostProcessor) {
			builder.withColumnPostProcessor(columnPostProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# pageSize(int)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> pageSize(int pageSize) {
			builder.pageSize(pageSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * allRowsVisible(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> allRowsVisible(boolean allRowsVisible) {
			builder.allRowsVisible(allRowsVisible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * columnReorderingAllowed(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> columnReorderingAllowed(boolean columnReorderingAllowed) {
			builder.columnReorderingAllowed(columnReorderingAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * itemDetailsRenderer(com.vaadin.flow .data.renderer.Renderer)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> itemDetailsRenderer(Renderer<T> renderer) {
			builder.itemDetailsRenderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * itemDetailsVisibleOnClick(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> itemDetailsVisibleOnClick(boolean detailsVisibleOnClick) {
			builder.itemDetailsVisibleOnClick(detailsVisibleOnClick);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * selectionMode(com.holonplatform. vaadin.flow.components.Selectable.SelectionMode)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> selectionMode(SelectionMode selectionMode) {
			builder.selectionMode(selectionMode);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> selectAllCheckboxVisibility(
				SelectAllCheckboxVisibility selectAllCheckBoxVisibility) {
			builder.selectAllCheckboxVisibility(selectAllCheckBoxVisibility);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withSelectionListener(com. holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withSelectionListener(SelectionListener<T> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * multiSort(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> multiSort(boolean multiSort) {
			builder.multiSort(multiSort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# contextMenu()
		 */
		@Override
		public ItemListingContextMenuBuilder<T, String, BeanListing<T>, DatastoreBeanListingBuilder<T>> contextMenu() {
			return new DefaultItemListingContextMenuBuilder<>(builder.getInstance(),
					builder.getInstance().getGrid().addContextMenu(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * header(java.util.function.Consumer)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> header(Consumer<EditableItemListingSection<String>> headerConfigurator) {
			builder.header(headerConfigurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * footer(java.util.function.Consumer)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> footer(Consumer<EditableItemListingSection<String>> footerConfigurator) {
			builder.footer(footerConfigurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# editable(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> editable(boolean editable) {
			builder.editable(editable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * editorBuffered(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> editorBuffered(boolean buffered) {
			builder.editorBuffered(buffered);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorSaveListener(com.vaadin. flow.component.grid.editor.EditorSaveListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withEditorSaveListener(EditorSaveListener<T, String> listener) {
			builder.withEditorSaveListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorCancelListener(com.vaadin .flow.component.grid.editor.EditorCancelListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withEditorCancelListener(EditorCancelListener<T, String> listener) {
			builder.withEditorCancelListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorOpenListener(com.vaadin. flow.component.grid.editor.EditorOpenListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withEditorOpenListener(EditorOpenListener<T, String> listener) {
			builder.withEditorOpenListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withEditorCloseListener(com.vaadin. flow.component.grid.editor.EditorCloseListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withEditorCloseListener(EditorCloseListener<T, String> listener) {
			builder.withEditorCloseListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withValidator(com.holonplatform. core.Validator)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withValidator(Validator<T> validator) {
			builder.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
		 * BeanInputGroupConfigurator# defaultValue(java.lang.String, java.util.function.Function)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> defaultValue(String property, Supplier<Object> defaultValueProvider) {
			builder.defaultValue(property, defaultValueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.
		 * BeanInputGroupConfigurator# withValueChangeListener(java.lang.String,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withValueChangeListener(String property,
				ValueChangeListener<?, GroupValueChangeEvent<?, String, Input<?>, EditorComponentGroup<String, T>>> listener) {
			builder.withValueChangeListener(property, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * required(java.lang.Object)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> required(String property) {
			builder.required(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * required(java.lang.Object, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> required(String property, Localizable message) {
			builder.required(property, message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * withPostProcessor(java.util.function .BiConsumer)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withPostProcessor(BiConsumer<String, Input<?>> postProcessor) {
			builder.withPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * validationStatusHandler(com. holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> validationStatusHandler(
				ValidationStatusHandler<EditorComponentGroup<String, T>> validationStatusHandler) {
			builder.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * groupValidationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.GroupValidationStatusHandler)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> groupValidationStatusHandler(
				GroupValidationStatusHandler<EditorComponentGroup<String, T>, String, Input<?>> groupValidationStatusHandler) {
			builder.groupValidationStatusHandler(groupValidationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * validationStatusHandler(java.lang. Object,
		 * com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> validationStatusHandler(String property,
				ValidationStatusHandler<Input<?>> validationStatusHandler) {
			builder.validationStatusHandler(property, validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#
		 * enableRefreshOnValueChange(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
			builder.enableRefreshOnValueChange(enableRefreshOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#
		 * usePropertyRendererRegistry(com. holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> usePropertyRendererRegistry(
				PropertyRendererRegistry propertyRendererRegistry) {
			builder.usePropertyRendererRegistry(propertyRendererRegistry);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#
		 * withValueChangeListener(com.
		 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withValueChangeListener(
				ValueChangeListener<T, GroupValueChangeEvent<T, String, Input<?>, EditorComponentGroup<String, T>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withComponentColumn(com.vaadin.flow .function.ValueProvider)
		 */
		@Override
		public ItemListingColumnBuilder<T, String, BeanListing<T>, DatastoreBeanListingBuilder<T>> withComponentColumn(
				ValueProvider<T, Component> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			final String columnId = builder.getInstance().addColumnProperty();
			builder.getInstance().getColumnConfiguration(columnId).setRenderer(new ComponentRenderer<>(valueProvider));
			return new DefaultItemListingColumnBuilder<>(columnId, builder.getInstance(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withColumn(com.vaadin.flow.function .ValueProvider)
		 */
		@Override
		public <X> ItemListingColumnBuilder<T, String, BeanListing<T>, DatastoreBeanListingBuilder<T>> withColumn(
				ValueProvider<T, X> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			final String columnId = builder.getInstance().addColumnProperty();
			builder.getInstance().getColumnConfiguration(columnId)
					.setValueProvider(new ValueProviderAdapter<>(valueProvider));
			return new DefaultItemListingColumnBuilder<>(columnId, builder.getInstance(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withItemClickListener(com. holonplatform.vaadin.flow.components.events.ClickEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withItemClickListener(
				ClickEventListener<BeanListing<T>, ItemClickEvent<BeanListing<T>, T>> listener) {
			builder.withItemClickListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#
		 * withItemRefreshListener(com. holonplatform.vaadin.flow.components.events.ItemEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withItemRefreshListener(
				ItemEventListener<BeanListing<T>, T, ItemEvent<BeanListing<T>, T>> listener) {
			builder.withItemRefreshListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(
		 * java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator# visible(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> visible(boolean visible) {
			builder.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * elementConfiguration(java.util. function.Consumer)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> elementConfiguration(Consumer<Element> element) {
			builder.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withAttachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withAttachListener(ComponentEventListener<AttachEvent> listener) {
			builder.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#
		 * withDetachListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withDetachListener(ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withThemeName(java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withThemeName(String themeName) {
			builder.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withEventListener(String eventType, DomEventListener listener) {
			builder.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#
		 * withEventListener(java.lang.String, com.vaadin.flow.dom.DomEventListener, java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withEventListener(String eventType, DomEventListener listener,
				String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(
		 * java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(
		 * java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minWidth(java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> minWidth(String minWidth) {
			builder.minWidth(minWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxWidth(java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> maxWidth(String maxWidth) {
			builder.maxWidth(maxWidth);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * minHeight(java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> minHeight(String minHeight) {
			builder.minHeight(minHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#
		 * maxHeight(java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> maxHeight(String maxHeight) {
			builder.maxHeight(maxHeight);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleNames(java.lang.String[])
		 */
		@Override
		public DatastoreBeanListingBuilder<T> styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#
		 * styleName(java.lang.String)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator# enabled(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator# tabIndex(int)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> tabIndex(int tabIndex) {
			builder.tabIndex(tabIndex);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * withFocusListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withFocusListener(
				ComponentEventListener<FocusEvent<Component>> listener) {
			builder.withFocusListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * withBlurListener(com.vaadin.flow. component.ComponentEventListener)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
		 * withFocusShortcut(com.vaadin.flow. component.Key)
		 */
		@Override
		public ShortcutConfigurator<DatastoreBeanListingBuilder<T>> withFocusShortcut(Key key) {
			return new DefaultShortcutConfigurator<>(builder.getInstance().getGrid().addFocusShortcut(key), this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasThemeVariantConfigurator
		 * #withThemeVariants(java.lang. Enum[])
		 */
		@Override
		public DatastoreBeanListingBuilder<T> withThemeVariants(GridVariant... variants) {
			builder.withThemeVariants(variants);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator# frozen(boolean)
		 */
		@Override
		public DatastoreBeanListingBuilder<T> frozen(boolean frozen) {
			builder.frozen(frozen);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> rowsDraggable(boolean rowsDraggable) {
			builder.rowsDraggable(rowsDraggable);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> dragFilter(Predicate<T> dragFilter) {
			builder.dragFilter(dragFilter);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> dragDataGenerator(String type, Function<T, String> dragDataGenerator) {
			builder.dragDataGenerator(type, dragDataGenerator);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> withDragStartListener(
				ItemListingDnDListener<T, String, ItemListingDragStartEvent<T, String>> listener) {
			builder.withDragStartListener(listener);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> withDragEndListener(
				ItemListingDnDListener<T, String, ItemListingDragEndEvent<T, String>> listener) {
			builder.withDragEndListener(listener);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> dropMode(GridDropMode dropMode) {
			builder.dropMode(dropMode);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> dropFilter(Predicate<T> dropFilter) {
			builder.dropFilter(dropFilter);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> withDropListener(
				ItemListingDnDListener<T, String, ItemListingDropEvent<T, String>> listener) {
			builder.withDropListener(listener);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> withColumnResizeListener(ColumnResizeListener<T, String> listener) {
			builder.withColumnResizeListener(listener);
			return this;
		}

		@Override
		public DatastoreBeanListingBuilder<T> withColumnReorderListener(ColumnReorderListener<T, String> listener) {
			builder.withColumnReorderListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
		 */
		@Override
		public BeanListing<T> build() {
			return builder.build();
		}

	}

	static class ValueProviderAdapter<T, V> implements ValueProvider<T, String> {

		private static final long serialVersionUID = -3231386190085166260L;

		private final ValueProvider<T, V> provider;

		public ValueProviderAdapter(ValueProvider<T, V> provider) {
			super();
			this.provider = provider;
		}

		@Override
		public String apply(T source) {
			return StringValuePresenter.getDefault().present(provider.apply(source));
		}

	}

}
