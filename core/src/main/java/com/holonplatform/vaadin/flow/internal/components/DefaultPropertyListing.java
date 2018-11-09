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
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.Path;
import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.binder.Setter;
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
	 * @param propertySet Property set (not null)
	 */
	public DefaultPropertyListing(PropertySet<?> propertySet) {
		super();
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
		// add properties as columns
		for (Property<?> property : propertySet) {
			addPropertyColumn(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#isAlwaysReadOnly(java.lang.Object)
	 */
	@Override
	protected boolean isAlwaysReadOnly(Property<?> property) {
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
	@Override
	protected Column<PropertyBox> generateDefaultGridColumn(
			ItemListingColumn<Property<?>, PropertyBox, ?> configuration) {
		final Property<?> property = configuration.getProperty();
		// check component
		if (Component.class.isAssignableFrom(property.getType())) {
			@SuppressWarnings("unchecked")
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
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getDefaultPropertyEditor(java.lang.Object)
	 */
	@Override
	protected Optional<Input<?>> getDefaultPropertyEditor(Property<?> property) {
		return property.renderIfAvailable(Input.class).map(i -> i);
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
	 * @see com.holonplatform.vaadin.flow.components.PropertySetBound#getProperties()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Property<?>> getProperties() {
		return (Iterable<Property<?>>) propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		return propertySet.contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertySetBound#propertyStream()
	 */
	@Override
	public Stream<Property<?>> propertyStream() {
		return propertySet.stream().map(p -> (Property<?>) p);
	}

}
