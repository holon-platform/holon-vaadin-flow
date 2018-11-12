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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.core.Validator;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn;
import com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn.SortMode;
import com.vaadin.flow.component.Component;
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
	 * Component columns
	 */
	private final Set<String> componentColumnProperties = new HashSet<>(4);

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

	/**
	 * Add a component type virtual column.
	 * @return The property id
	 */
	protected String addComponentColumnProperty() {
		final String id = "_component" + componentColumnProperties.size();
		addPropertyColumn(id);
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing#isAlwaysReadOnly(java.lang.Object)
	 */
	@Override
	protected boolean isAlwaysReadOnly(String property) {
		return property != null && componentColumnProperties.contains(property);
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

	// ------- Builder

	/**
	 * Default {@link BeanListingBuilder} implementation.
	 * 
	 * @param <T> Bean type
	 */
	public static class DefaultBeanListingBuilder<T>
			extends AbstractItemListingConfigurator<T, String, DefaultBeanListing<T>, BeanListingBuilder<T>>
			implements BeanListingBuilder<T> {

		public DefaultBeanListingBuilder(Class<T> beanType) {
			super(new DefaultBeanListing<>(beanType));
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder#withValidator(java.lang.String,
		 * com.holonplatform.core.Validator)
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
		 * @see com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder#editor(java.lang.String,
		 * com.holonplatform.vaadin.flow.components.Input)
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public BeanListingBuilder<T> editor(String property, Input<?> editor) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			getInstance().getColumnConfiguration(property).setEditor((Input) editor);
			return getConfigurator();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator#withComponentColumn(com.vaadin.flow
		 * .function.ValueProvider)
		 */
		@Override
		public ItemListingColumnBuilder<T, String, BeanListingBuilder<T>> withComponentColumn(
				ValueProvider<T, Component> valueProvider) {
			ObjectUtils.argumentNotNull(valueProvider, "ValueProvider must be not null");
			return new DefaultItemListingColumnBuilder<>(getInstance().addComponentColumnProperty(), getInstance(),
					this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.internal.components.AbstractItemListing.AbstractItemListingConfigurator#
		 * getConfigurator()
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

}
