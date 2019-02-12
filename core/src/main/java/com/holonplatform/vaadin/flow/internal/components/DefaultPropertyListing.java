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
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Path;
import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder.DatastorePropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.components.events.ItemClickEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEvent;
import com.holonplatform.vaadin.flow.components.events.ItemEventListener;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.data.ItemSort;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.EditorCancelListener;
import com.vaadin.flow.component.grid.editor.EditorCloseListener;
import com.vaadin.flow.component.grid.editor.EditorOpenListener;
import com.vaadin.flow.component.grid.editor.EditorSaveListener;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.function.ValueProvider;

/**
 * Default {@link PropertyListing} implementation.
 *
 * @since 5.2.0
 */
public class DefaultPropertyListing extends AbstractItemListing<PropertyBox, Property<?>> implements PropertyListing {

	private static final long serialVersionUID = -1099573388730286182L;

	/**
	 * Property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Constructor.
	 * @param <P> Property type
	 * @param properties Property set (not null)
	 */
	public <P extends Property<?>> DefaultPropertyListing(Iterable<P> properties) {
		super();
		ObjectUtils.argumentNotNull(properties, "Property set must be not null");
		this.propertySet = (properties instanceof PropertySet<?>) ? (PropertySet<?>) properties
				: PropertySet.of(properties);
		// add properties as columns
		for (Property<?> property : propertySet) {
			addPropertyColumn(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemSet#getDataProvider()
	 */
	@Override
	public DataProvider<PropertyBox, ?> getDataProvider() {
		return getGrid().getDataProvider();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasPropertySet#getProperties()
	 */
	@Override
	public Collection<Property<?>> getProperties() {
		return getPropertySet().stream().map(p -> (Property<?>) p).collect(Collectors.toList());
	}

	/**
	 * Get the listing property set.
	 * @return the listing property set
	 */
	protected PropertySet<?> getPropertySet() {
		return propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#isReadOnlyByDefault(java.lang.Object)
	 */
	@Override
	protected boolean isReadOnlyByDefault(Property<?> property) {
		return property.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getColumnKey(java.lang.Object)
	 */
	@Override
	protected String generateColumnKey(Property<?> property) {
		if (Path.class.isAssignableFrom(property.getClass())) {
			return ((Path<?>) property).fullName();
		}
		String name = property.getName();
		if (name != null) {
			return name;
		}
		return String.valueOf(property.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getDefaultColumnHeader(java.lang.Object)
	 */
	@Override
	protected Optional<Localizable> getDefaultColumnHeader(Property<?> property) {
		if (property.getMessage() != null || property.getMessageCode() != null) {
			return Optional.of(Localizable.builder()
					.message((property.getMessage() != null) ? property.getMessage() : property.getName())
					.messageCode(property.getMessageCode()).build());
		}
		if (Path.class.isAssignableFrom(property.getClass()) && property.getName() != null) {
			return Optional.of(Localizable.of(property.getName()));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#preProcessConfiguration(com.holonplatform.
	 * vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@Override
	protected ItemListingColumn<Property<?>, PropertyBox, ?> preProcessConfiguration(
			ItemListingColumn<Property<?>, PropertyBox, ?> configuration) {
		// sort
		if (Path.class.isAssignableFrom(configuration.getProperty().getClass())) {
			if (configuration.getSortProperties().isEmpty()) {
				configuration.setSortProperties(Collections.singletonList(configuration.getProperty()));
			}
			if (configuration.getSortMode() == SortMode.DEFAULT) {
				configuration.setSortMode(SortMode.ENABLED);
			}
		}
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getSortPropertyName(java.lang.Object)
	 */
	@Override
	protected Optional<String> getSortPropertyName(Property<?> property) {
		if (property != null && Path.class.isAssignableFrom(property.getClass())) {
			return Optional.ofNullable(((Path<?>) property).relativeName());
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#generateDefaultGridColumn(com.holonplatform
	 * .vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Column<PropertyBox> generateDefaultGridColumn(
			ItemListingColumn<Property<?>, PropertyBox, ?> configuration) {
		final Property<?> property = configuration.getProperty();
		// check component
		if (Component.class.isAssignableFrom(property.getType())) {
			final Property<? extends Component> componentProperty = (Property<? extends Component>) property;
			return getGrid().addComponentColumn(item -> {
				if (item.contains(property)) {
					return item.getValue(componentProperty);
				}
				return null;
			});
		}
		// default provider using property presenter
		return getGrid().addColumn(item -> {
			if (item.contains(property)) {
				return item.present(property);
			}
			return null;
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getPropertyType(java.lang.Object)
	 */
	@Override
	protected Class<?> getPropertyType(Property<?> property) {
		return property.getType();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getPropertyValueGetter(java.lang.Object)
	 */
	@Override
	protected ValueProvider<PropertyBox, ?> getPropertyValueGetter(Property<?> property) {
		return item -> item.getValue(property);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getPropertyValueSetter(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Optional<Setter<PropertyBox, ?>> getPropertyValueSetter(Property<?> property) {
		if (!property.isReadOnly()) {
			return Optional.of((item, value) -> item.setValue((Property<Object>) property, value));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#buildPropertyEditor(com.holonplatform.
	 * vaadin.flow.internal.components.support.ItemListingColumn)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <V> Optional<Input<V>> buildPropertyEditor(ItemListingColumn<Property<?>, PropertyBox, V> configuration) {
		final Property<V> property = (Property<V>) configuration.getProperty();
		// check custom renderer
		Optional<Input<V>> component = configuration.getEditorInputRenderer().map(r -> r.render(property));
		if (component.isPresent()) {
			return component;
		}
		// check specific registry
		if (getPropertyRendererRegistry().isPresent()) {
			return getPropertyRendererRegistry().get().getRenderer(Input.class, property).map(r -> r.render(property));
		} else {
			// use default
			return property.renderIfAvailable(Input.class).map(c -> (Input<V>) c);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getDefaultPropertyValidators(java.lang.
	 * Object)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Collection<Validator<Object>> getDefaultPropertyValidators(Property<?> property) {
		return ((Property) property).getValidators();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#refreshVirtualProperties()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void refreshVirtualProperties() {
		isEditing().ifPresent(value -> {
			getBindings().filter(b -> b.getProperty() instanceof VirtualProperty).forEach(b -> {
				((Input) b.getElement()).setValue((value != null) ? value.getValue(b.getProperty()) : null);
			});
		});
	}

	// ------- Builder

	/**
	 * Default {@link PropertyListingBuilder} implementation.
	 */
	public static class DefaultPropertyListingBuilder extends
			AbstractItemListingConfigurator<PropertyBox, Property<?>, PropertyListing, DefaultPropertyListing, PropertyListingBuilder>
			implements PropertyListingBuilder {

		public <P extends Property<?>> DefaultPropertyListingBuilder(Iterable<P> properties) {
			super(new DefaultPropertyListing(properties));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.AbstractItemListingConfigurator#
		 * getItemListing()
		 */
		@Override
		protected PropertyListing getItemListing() {
			return getInstance();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.AbstractItemListingConfigurator#
		 * getConfigurator()
		 */
		@Override
		public DefaultPropertyListingBuilder getConfigurator() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withColumn(com.vaadin.flow.function
		 * .ValueProvider)
		 */
		@Override
		public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withColumn(
				ValueProvider<PropertyBox, X> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return withColumn(VirtualProperty.create(Object.class, item -> valueProvider.apply(item)));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#withColumn(com.holonplatform.
		 * core.property.VirtualProperty)
		 */
		@Override
		public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withColumn(
				VirtualProperty<X> property) {
			ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
			getInstance().addPropertyColumn(property).setValueProvider(new VirtualPropertyValueProvider<>(property));
			return new DefaultItemListingColumnBuilder<>(property, getInstance(), getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withComponentColumn(com.vaadin.flow
		 * .function.ValueProvider)
		 */
		@Override
		public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withComponentColumn(
				ValueProvider<PropertyBox, Component> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return withComponentColumn(VirtualProperty.create(Component.class, item -> valueProvider.apply(item)));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#withComponentColumn(com.
		 * holonplatform.core.property.VirtualProperty)
		 */
		@Override
		public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> withComponentColumn(
				VirtualProperty<? extends Component> property) {
			ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
			final ItemListingColumn<Property<?>, PropertyBox, ?> column = getInstance().addPropertyColumn(property);
			column.setRenderer(new ComponentRenderer<>(item -> property.getValueProvider().getPropertyValue(item)));
			return new DefaultItemListingColumnBuilder<>(property, getInstance(), getConfigurator());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#withValidator(com.holonplatform
		 * .core.property.Property, com.holonplatform.core.Validator)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public <V> PropertyListingBuilder withValidator(Property<V> property, Validator<? super V> validator) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(validator, "Validator must be not null");
			getInstance().getColumnConfiguration(property).addValidator((Validator) validator);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#editor(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <T> PropertyListingBuilder editor(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			((ItemListingColumn) getInstance().getColumnConfiguration(property)).setEditorInputRenderer(renderer);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.PropertySetInputGroupConfigurator#
		 * defaultValue(com.holonplatform.core.property.Property, java.util.function.Supplier)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <V> PropertyListingBuilder defaultValue(Property<V> property, Supplier<V> defaultValueProvider) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).setDefaultValueProvider((Supplier) defaultValueProvider);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.PropertySetInputGroupConfigurator#
		 * withValueChangeListener(com.holonplatform.core.property.Property,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <V> PropertyListingBuilder withValueChangeListener(Property<V> property,
				ValueChangeListener<V, GroupValueChangeEvent<V, Property<?>, Input<?>, EditorComponentGroup<Property<?>, PropertyBox>>> listener) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).addValueChangeListener((ValueChangeListener) listener);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasPropertySetDatastoreDataProviderConfigurator#dataSource(
		 * com.holonplatform.core.datastore.Datastore, com.holonplatform.core.datastore.DataTarget)
		 */
		@Override
		public DatastorePropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> target) {
			final DatastoreDataProvider<PropertyBox, QueryFilter> datastoreDataProvider = DatastoreDataProvider
					.create(datastore, target, getInstance().getPropertySet());
			getInstance().getGrid().setDataProvider(datastoreDataProvider);
			return new DefaultDatastorePropertyListingBuilder(this, datastoreDataProvider);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
		 */
		@Override
		public PropertyListing build() {
			return configureAndBuild();
		}

	}

	public static class DefaultDatastorePropertyListingBuilder implements DatastorePropertyListingBuilder {

		private final DefaultPropertyListingBuilder builder;
		private final DatastoreDataProvider<PropertyBox, QueryFilter> datastoreDataProvider;

		public DefaultDatastorePropertyListingBuilder(DefaultPropertyListingBuilder builder,
				DatastoreDataProvider<PropertyBox, QueryFilter> datastoreDataProvider) {
			super();
			this.builder = builder;
			this.datastoreDataProvider = datastoreDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#withColumn(com.holonplatform.
		 * core.property.VirtualProperty)
		 */
		@Override
		public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, DatastorePropertyListingBuilder> withColumn(
				VirtualProperty<X> property) {
			ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
			builder.getInstance().addPropertyColumn(property)
					.setValueProvider(new VirtualPropertyValueProvider<>(property));
			return new DefaultItemListingColumnBuilder<>(property, builder.getInstance(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withColumn(com.vaadin.flow.function
		 * .ValueProvider)
		 */
		@Override
		public <X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, DatastorePropertyListingBuilder> withColumn(
				ValueProvider<PropertyBox, X> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return withColumn(VirtualProperty.create(Object.class, item -> valueProvider.apply(item)));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#withComponentColumn(com.
		 * holonplatform.core.property.VirtualProperty)
		 */
		@Override
		public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, DatastorePropertyListingBuilder> withComponentColumn(
				VirtualProperty<? extends Component> property) {
			ObjectUtils.argumentNotNull(property, "VirtualProperty must be not null");
			final ItemListingColumn<Property<?>, PropertyBox, ?> column = builder.getInstance()
					.addPropertyColumn(property);
			column.setRenderer(new ComponentRenderer<>(item -> property.getValueProvider().getPropertyValue(item)));
			return new DefaultItemListingColumnBuilder<>(property, builder.getInstance(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withComponentColumn(com.vaadin.flow
		 * .function.ValueProvider)
		 */
		@Override
		public ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, DatastorePropertyListingBuilder> withComponentColumn(
				ValueProvider<PropertyBox, Component> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return withComponentColumn(VirtualProperty.create(Component.class, item -> valueProvider.apply(item)));
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#withValidator(com.holonplatform
		 * .core.property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <V> DatastorePropertyListingBuilder withValidator(Property<V> property, Validator<? super V> validator) {
			builder.withValidator(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyListingConfigurator#editor(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> DatastorePropertyListingBuilder editor(Property<T> property,
				PropertyRenderer<Input<T>, T> renderer) {
			builder.editor(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DatastoreDataProviderConfigurator#
		 * withQueryConfigurationProvider(com.holonplatform.core.query.QueryConfigurationProvider)
		 */
		@Override
		public DatastorePropertyListingBuilder withQueryConfigurationProvider(
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
		public DatastorePropertyListingBuilder withDefaultQuerySort(QuerySort defaultQuerySort) {
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
		public DatastorePropertyListingBuilder itemIdentifierProvider(
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
		public DatastorePropertyListingBuilder querySortOrderConverter(
				Function<QuerySortOrder, QuerySort> querySortOrderConverter) {
			datastoreDataProvider.setQuerySortOrderConverter(querySortOrderConverter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#editorComponent(java.lang.Object,
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListingBuilder editorComponent(Property<?> property,
				Function<PropertyBox, ? extends Component> editorComponentProvider) {
			builder.editorComponent(property, editorComponentProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayAsFirst(java.lang.Object)
		 */
		@Override
		public DatastorePropertyListingBuilder displayAsFirst(Property<?> property) {
			builder.displayAsFirst(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayAsLast(java.lang.Object)
		 */
		@Override
		public DatastorePropertyListingBuilder displayAsLast(Property<?> property) {
			builder.displayAsLast(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayBefore(java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public DatastorePropertyListingBuilder displayBefore(Property<?> property, Property<?> beforeProperty) {
			builder.displayBefore(property, beforeProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#displayAfter(java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public DatastorePropertyListingBuilder displayAfter(Property<?> property, Property<?> afterProperty) {
			builder.displayAfter(property, afterProperty);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#visibleColumns(java.util.List)
		 */
		@Override
		public DatastorePropertyListingBuilder visibleColumns(List<? extends Property<?>> visibleColumns) {
			builder.visibleColumns(visibleColumns);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortable(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder sortable(boolean sortable) {
			builder.sortable(sortable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortable(java.lang.Object,
		 * boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder sortable(Property<?> property, boolean sortable) {
			builder.sortable(property, sortable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#resizable(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder resizable(boolean resizable) {
			builder.resizable(resizable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#resizable(java.lang.Object,
		 * boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder resizable(Property<?> property, boolean resizable) {
			builder.resizable(property, resizable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#visible(java.lang.Object,
		 * boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder visible(Property<?> property, boolean visible) {
			builder.visible(property, visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#readOnly(java.lang.Object,
		 * boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder readOnly(Property<?> property, boolean readOnly) {
			builder.readOnly(property, readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#frozen(java.lang.Object,
		 * boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder frozen(Property<?> property, boolean frozen) {
			builder.frozen(property, frozen);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#frozenColumns(int)
		 */
		@Override
		public DatastorePropertyListingBuilder frozenColumns(int frozenColumnsCount) {
			builder.frozenColumns(frozenColumnsCount);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#width(java.lang.Object,
		 * java.lang.String)
		 */
		@Override
		public DatastorePropertyListingBuilder width(Property<?> property, String width) {
			builder.width(property, width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#flexGrow(java.lang.Object,
		 * int)
		 */
		@Override
		public DatastorePropertyListingBuilder flexGrow(Property<?> property, int flexGrow) {
			builder.flexGrow(property, flexGrow);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#expand(java.lang.Object)
		 */
		@Override
		public DatastorePropertyListingBuilder expand(Property<?> property) {
			builder.expand(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#alignment(java.lang.Object,
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment)
		 */
		@Override
		public DatastorePropertyListingBuilder alignment(Property<?> property, ColumnAlignment alignment) {
			builder.alignment(property, alignment);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#renderer(java.lang.Object,
		 * com.vaadin.flow.data.renderer.Renderer)
		 */
		@Override
		public DatastorePropertyListingBuilder renderer(Property<?> property, Renderer<PropertyBox> renderer) {
			builder.renderer(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#valueProvider(java.lang.Object,
		 * com.vaadin.flow.function.ValueProvider)
		 */
		@Override
		public DatastorePropertyListingBuilder valueProvider(Property<?> property,
				ValueProvider<PropertyBox, String> valueProvider) {
			builder.valueProvider(property, valueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortComparator(java.lang.Object,
		 * java.util.Comparator)
		 */
		@Override
		public DatastorePropertyListingBuilder sortComparator(Property<?> property,
				Comparator<PropertyBox> comparator) {
			builder.sortComparator(property, comparator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortUsing(java.lang.Object,
		 * java.util.List)
		 */
		@Override
		public DatastorePropertyListingBuilder sortUsing(Property<?> property, List<Property<?>> sortProperties) {
			builder.sortUsing(property, sortProperties);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#sortProvider(java.lang.Object,
		 * java.util.function.Function)
		 */
		@Override
		public DatastorePropertyListingBuilder sortProvider(Property<?> property,
				Function<SortDirection, Stream<ItemSort<Property<?>>>> sortProvider) {
			builder.sortProvider(property, sortProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#header(java.lang.Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastorePropertyListingBuilder header(Property<?> property, Localizable header) {
			builder.header(property, header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#headerComponent(java.lang.Object,
		 * com.vaadin.flow.component.Component)
		 */
		@Override
		public DatastorePropertyListingBuilder headerComponent(Property<?> property, Component header) {
			builder.headerComponent(property, header);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#footer(java.lang.Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastorePropertyListingBuilder footer(Property<?> property, Localizable footer) {
			builder.footer(property, footer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#footerComponent(java.lang.Object,
		 * com.vaadin.flow.component.Component)
		 */
		@Override
		public DatastorePropertyListingBuilder footerComponent(Property<?> property, Component footer) {
			builder.footerComponent(property, footer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#pageSize(int)
		 */
		@Override
		public DatastorePropertyListingBuilder pageSize(int pageSize) {
			builder.pageSize(pageSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#heightByRows(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder heightByRows(boolean heightByRows) {
			builder.heightByRows(heightByRows);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#columnReorderingAllowed(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder columnReorderingAllowed(boolean columnReorderingAllowed) {
			builder.columnReorderingAllowed(columnReorderingAllowed);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#itemDetailsRenderer(com.vaadin.flow
		 * .data.renderer.Renderer)
		 */
		@Override
		public DatastorePropertyListingBuilder itemDetailsRenderer(Renderer<PropertyBox> renderer) {
			builder.itemDetailsRenderer(renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#itemDetailsVisibleOnClick(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder itemDetailsVisibleOnClick(boolean detailsVisibleOnClick) {
			builder.itemDetailsVisibleOnClick(detailsVisibleOnClick);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#selectionMode(com.holonplatform.
		 * vaadin.flow.components.Selectable.SelectionMode)
		 */
		@Override
		public DatastorePropertyListingBuilder selectionMode(SelectionMode selectionMode) {
			builder.selectionMode(selectionMode);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withSelectionListener(com.
		 * holonplatform.vaadin.flow.components.Selectable.SelectionListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withSelectionListener(SelectionListener<PropertyBox> selectionListener) {
			builder.withSelectionListener(selectionListener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withItemClickListener(com.
		 * holonplatform.vaadin.flow.components.events.ClickEventListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withItemClickListener(
				ClickEventListener<PropertyListing, ItemClickEvent<PropertyListing, PropertyBox>> listener) {
			builder.withItemClickListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withItemRefreshListener(com.
		 * holonplatform.vaadin.flow.components.events.ItemEventListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withItemRefreshListener(
				ItemEventListener<PropertyListing, PropertyBox, ItemEvent<PropertyListing, PropertyBox>> listener) {
			builder.withItemRefreshListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#multiSort(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder multiSort(boolean multiSort) {
			builder.multiSort(multiSort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#verticalScrollingEnabled(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder verticalScrollingEnabled(boolean enabled) {
			builder.verticalScrollingEnabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#contextMenu()
		 */
		@Override
		public ItemListingContextMenuBuilder<PropertyBox, Property<?>, PropertyListing, DatastorePropertyListingBuilder> contextMenu() {
			return new DefaultItemListingContextMenuBuilder<>(builder.getInstance(),
					builder.getInstance().getGrid().addContextMenu(), this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#header(java.util.function.Consumer)
		 */
		@Override
		public DatastorePropertyListingBuilder header(
				Consumer<EditableItemListingSection<Property<?>>> headerConfigurator) {
			builder.header(headerConfigurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#footer(java.util.function.Consumer)
		 */
		@Override
		public DatastorePropertyListingBuilder footer(
				Consumer<EditableItemListingSection<Property<?>>> footerConfigurator) {
			builder.footer(footerConfigurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#editable(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder editable(boolean editable) {
			builder.editable(editable);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#editorBuffered(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder editorBuffered(boolean buffered) {
			builder.editorBuffered(buffered);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorSaveListener(com.vaadin.
		 * flow.component.grid.editor.EditorSaveListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withEditorSaveListener(EditorSaveListener<PropertyBox> listener) {
			builder.withEditorSaveListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorCancelListener(com.vaadin
		 * .flow.component.grid.editor.EditorCancelListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withEditorCancelListener(EditorCancelListener<PropertyBox> listener) {
			builder.withEditorCancelListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorOpenListener(com.vaadin.
		 * flow.component.grid.editor.EditorOpenListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withEditorOpenListener(EditorOpenListener<PropertyBox> listener) {
			builder.withEditorOpenListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withEditorCloseListener(com.vaadin.
		 * flow.component.grid.editor.EditorCloseListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withEditorCloseListener(EditorCloseListener<PropertyBox> listener) {
			builder.withEditorCloseListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withValidator(com.holonplatform.
		 * core.Validator)
		 */
		@Override
		public DatastorePropertyListingBuilder withValidator(Validator<PropertyBox> validator) {
			builder.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.PropertySetInputGroupConfigurator#
		 * defaultValue(com.holonplatform.core.property.Property, java.util.function.Supplier)
		 */
		@Override
		public <V> DatastorePropertyListingBuilder defaultValue(Property<V> property,
				Supplier<V> defaultValueProvider) {
			builder.defaultValue(property, defaultValueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.PropertySetInputGroupConfigurator#
		 * withValueChangeListener(com.holonplatform.core.property.Property,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public <V> DatastorePropertyListingBuilder withValueChangeListener(Property<V> property,
				ValueChangeListener<V, GroupValueChangeEvent<V, Property<?>, Input<?>, EditorComponentGroup<Property<?>, PropertyBox>>> listener) {
			builder.withValueChangeListener(property, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#usePropertyRendererRegistry(com.
		 * holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public DatastorePropertyListingBuilder usePropertyRendererRegistry(
				PropertyRendererRegistry propertyRendererRegistry) {
			builder.usePropertyRendererRegistry(propertyRendererRegistry);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentGroupConfigurator#withValueChangeListener(com.
		 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public DatastorePropertyListingBuilder withValueChangeListener(
				ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, Input<?>, EditorComponentGroup<Property<?>, PropertyBox>>> listener) {
			builder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#required(java.lang.Object)
		 */
		@Override
		public DatastorePropertyListingBuilder required(Property<?> property) {
			builder.required(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#required(java.lang.Object,
		 * com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public DatastorePropertyListingBuilder required(Property<?> property, Localizable message) {
			builder.required(property, message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#withPostProcessor(java.util.function
		 * .BiConsumer)
		 */
		@Override
		public DatastorePropertyListingBuilder withPostProcessor(BiConsumer<Property<?>, Input<?>> postProcessor) {
			builder.withPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public DatastorePropertyListingBuilder validationStatusHandler(
				ValidationStatusHandler<EditorComponentGroup<Property<?>, PropertyBox>> validationStatusHandler) {
			builder.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#groupValidationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.GroupValidationStatusHandler)
		 */
		@Override
		public DatastorePropertyListingBuilder groupValidationStatusHandler(
				GroupValidationStatusHandler<EditorComponentGroup<Property<?>, PropertyBox>, Property<?>, Input<?>> groupValidationStatusHandler) {
			builder.groupValidationStatusHandler(groupValidationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#validationStatusHandler(java.lang.
		 * Object, com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public DatastorePropertyListingBuilder validationStatusHandler(Property<?> property,
				ValidationStatusHandler<Input<?>> validationStatusHandler) {
			builder.validationStatusHandler(property, validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#enableRefreshOnValueChange(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
			builder.enableRefreshOnValueChange(enableRefreshOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public DatastorePropertyListingBuilder id(String id) {
			builder.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder visible(boolean visible) {
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
		public DatastorePropertyListingBuilder withAttachListener(ComponentEventListener<AttachEvent> listener) {
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
		public DatastorePropertyListingBuilder withDetachListener(ComponentEventListener<DetachEvent> listener) {
			builder.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public DatastorePropertyListingBuilder withThemeName(String themeName) {
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
		public DatastorePropertyListingBuilder withEventListener(String eventType, DomEventListener listener) {
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
		public DatastorePropertyListingBuilder withEventListener(String eventType, DomEventListener listener,
				String filter) {
			builder.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
		 */
		@Override
		public DatastorePropertyListingBuilder width(String width) {
			builder.width(width);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
		 */
		@Override
		public DatastorePropertyListingBuilder height(String height) {
			builder.height(height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public DatastorePropertyListingBuilder styleNames(String... styleNames) {
			builder.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public DatastorePropertyListingBuilder styleName(String styleName) {
			builder.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
		 */
		@Override
		public DatastorePropertyListingBuilder enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
		 */
		@Override
		public DatastorePropertyListingBuilder tabIndex(int tabIndex) {
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
		public DatastorePropertyListingBuilder withFocusListener(
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
		public DatastorePropertyListingBuilder withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
			builder.withBlurListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasThemeVariantConfigurator#withThemeVariants(java.lang.
		 * Enum[])
		 */
		@Override
		public DatastorePropertyListingBuilder withThemeVariants(GridVariant... variants) {
			builder.withThemeVariants(variants);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ItemListingBuilder#build()
		 */
		@Override
		public PropertyListing build() {
			return builder.build();
		}

	}

	static class VirtualPropertyValueProvider<T> implements ValueProvider<PropertyBox, String> {

		private static final long serialVersionUID = 582674628237265592L;

		private final VirtualProperty<T> property;

		public VirtualPropertyValueProvider(VirtualProperty<T> property) {
			super();
			this.property = property;
		}

		@Override
		public String apply(PropertyBox source) {
			return property.present(property.getValueProvider().getPropertyValue(source));
		}

	}

}
