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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyValueProvider;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.vaadin.flow.component.Component;

/**
 * {@link PropertyListing} builder.
 *
 * @since 5.2.0
 */
public interface PropertyListingBuilder
		extends ItemListingBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder>,
		HasPropertyDataSourceConfigurator<PropertyListingBuilder> {

	/**
	 * Add a column which contents will be rendered as a {@link Component} using given {@link VirtualProperty}.
	 * <p>
	 * The virtual property {@link PropertyValueProvider} will be invoked for each listing item to obtain the Component
	 * to display.
	 * </p>
	 * @param property The virtual property which represent the column.
	 * @return An {@link ItemListingColumnBuilder} which allow further column configuration and provides the
	 *         {@link ItemListingColumnBuilder#add()} method to add the column to the listing
	 */
	ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListingBuilder> withComponentColumn(
			VirtualProperty<Component> property);

	/**
	 * Add a property {@link Validator} to be used when the property value is edited using the item editor.
	 * @param <V> Property value type
	 * @param property The property for which to add the validator (not null)
	 * @param validator The validator to add (not null)
	 * @return this
	 */
	<V> PropertyListingBuilder withValidator(Property<V> property, Validator<? super V> validator);

	/**
	 * Set the {@link Input} to use as given property editor.
	 * @param <V> Property value type
	 * @param property The property for which to set the editor (not null)
	 * @param editor The property editor (not null)
	 * @return this
	 */
	<V> PropertyListingBuilder editor(Property<V> property, Input<V> editor);

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source. The listing property set
	 * will be used as query projection.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @return this
	 */
	default PropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> dataTarget) {
		return dataSource(datastore, dataTarget, new QueryConfigurationProvider[0]);
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source. The listing property set
	 * will be used as query projection.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param filter Fixed {@link QueryFilter} to use (may be null)
	 * @return this
	 */
	default PropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> dataTarget, QueryFilter filter) {
		return dataSource(datastore, dataTarget, QueryConfigurationProvider.create(filter, null));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source. The listing property set
	 * will be used as query projection.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param sort Fixed {@link QuerySort} to use (may be null)
	 * @return this
	 */
	default PropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> dataTarget, QuerySort sort) {
		return dataSource(datastore, dataTarget, QueryConfigurationProvider.create(null, sort));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source. The listing property set
	 * will be used as query projection.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param filter Fixed {@link QueryFilter} to use (may be null)
	 * @param sort Fixed {@link QuerySort} to use (may be null)
	 * @return this
	 */
	default PropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> dataTarget, QueryFilter filter,
			QuerySort sort) {
		return dataSource(datastore, dataTarget, QueryConfigurationProvider.create(filter, sort));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source. The listing property set
	 * will be used as query projection.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return this
	 */
	PropertyListingBuilder dataSource(Datastore datastore, DataTarget<?> dataTarget,
			QueryConfigurationProvider... queryConfigurationProviders);

}
