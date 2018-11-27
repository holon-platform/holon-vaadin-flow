/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyListing;

/**
 * An {@link ItemListing} component using {@link Property}s as item properties and {@link PropertyBox} as item data
 * type.
 * 
 * @since 5.2.0
 */
public interface PropertyListing extends ItemListing<PropertyBox, Property<?>>, HasPropertySet<Property<?>> {

	/**
	 * Get a {@link PropertyListingBuilder} to create and setup a {@link PropertyListing}.
	 * @param <P> Property type
	 * @param properties The listing property set (not null)
	 * @return A new {@link PropertyListingBuilder}
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyListingBuilder builder(Iterable<P> properties) {
		return new DefaultPropertyListing.DefaultPropertyListingBuilder(properties);
	}

	/**
	 * Get a {@link PropertyListingBuilder} to create and setup a {@link PropertyListing}.
	 * @param properties The listing property set (not null)
	 * @return A new {@link PropertyListingBuilder}
	 */
	static PropertyListingBuilder builder(Property<?>... properties) {
		return builder(PropertySet.of(properties));
	}

}
