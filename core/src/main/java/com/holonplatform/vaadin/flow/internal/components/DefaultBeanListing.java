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

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;

/**
 * Default {@link BeanListing} implementation.
 *
 * @param <T> Bean type
 * 
 * @since 5.2.0
 */
public class DefaultBeanListing<T> extends AbstractItemListing<T, String> implements BeanListing<T> {

	private static final long serialVersionUID = -4447503694235650581L;

	/**
	 * Bean property set
	 */
	private final BeanPropertySet<T> propertySet;

	/**
	 * Constructor.
	 * @param beanType Bean type (not null)
	 */
	public DefaultBeanListing(Class<T> beanType) {
		super();
		ObjectUtils.argumentNotNull(beanType, "Bean type must be not null");
		this.propertySet = BeanPropertySet.create(beanType);
		// add properties as columns
		for (PathProperty<?> property : propertySet) {
			addPropertyColumn(property.relativeName());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#isAlwaysReadOnly(java.lang.Object)
	 */
	@Override
	protected boolean isAlwaysReadOnly(String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#generateColumnKey(java.lang.Object)
	 */
	@Override
	protected String generateColumnKey(String property) {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#generateDefaultGridColumn(com.holonplatform
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
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#preProcessConfiguration(com.holonplatform.
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
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getSortPropertyName(java.lang.Object)
	 */
	@Override
	protected Optional<String> getSortPropertyName(String property) {
		return Optional.of(property);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getDefaultColumnHeader(java.lang.Object)
	 */
	@Override
	protected Optional<Localizable> getDefaultColumnHeader(String property) {
		return propertySet.getProperty(property).map(p -> {
			if (p.getMessage() != null || p.getMessageCode() != null) {
				return Localizable.builder().message((p.getMessage() != null) ? p.getMessage() : p.getName())
						.messageCode(p.getMessageCode()).build();
			}
			return null;
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getPropertyType(java.lang.Object)
	 */
	@Override
	protected Class<?> getPropertyType(String property) {
		return propertySet.property(property).getType();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getPropertyValueGetter(java.lang.Object)
	 */
	@Override
	protected ValueProvider<T, ?> getPropertyValueGetter(String property) {
		return item -> propertySet.read(property, item);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getPropertyValueSetter(java.lang.Object)
	 */
	@Override
	protected Optional<Setter<T, ?>> getPropertyValueSetter(String property) {
		return Optional.of((item, value) -> propertySet.write(property, value, item));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getDefaultPropertyEditor(java.lang.Object)
	 */
	@Override
	protected Optional<Input<?>> getDefaultPropertyEditor(String property) {
		return propertySet.getProperty(property).flatMap(p -> p.renderIfAvailable(Input.class)).map(i -> i);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#getDefaultPropertyValidators(java.lang.
	 * Object)
	 */
	@Override
	protected Collection<Validator<Object>> getDefaultPropertyValidators(String property) {
		return propertySet.getProperty(property).map(p -> p.getValidators()).orElse(Collections.emptyList());
	}

}
