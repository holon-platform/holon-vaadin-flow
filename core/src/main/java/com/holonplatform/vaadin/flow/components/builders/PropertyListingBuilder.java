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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder.DatastorePropertyListingBuilder;

/**
 * {@link PropertyListing} builder.
 *
 * @since 5.2.0
 */
public interface PropertyListingBuilder extends PropertyListingConfigurator<PropertyListingBuilder>,
		HasPropertySetDatastoreDataProviderConfigurator<DatastorePropertyListingBuilder, PropertyListingBuilder>,
		ItemListingBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder> {

	/**
	 * {@link PropertyListing} builder with {@link DatastoreDataProviderConfigurator} support.
	 * 
	 * @since 5.2.0
	 */
	public interface DatastorePropertyListingBuilder
			extends PropertyListingConfigurator<DatastorePropertyListingBuilder>,
			DatastoreDataProviderConfigurator<PropertyBox, DatastorePropertyListingBuilder>,
			ItemListingBuilder<PropertyBox, Property<?>, PropertyListing, DatastorePropertyListingBuilder> {

	}

}
