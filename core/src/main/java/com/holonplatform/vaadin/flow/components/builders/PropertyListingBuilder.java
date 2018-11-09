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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;

/**
 * {@link PropertyListing} builder.
 *
 * @since 5.2.0
 */
public interface PropertyListingBuilder
		extends ItemListingBuilder<PropertyBox, Property<?>, PropertyListing, PropertyListingBuilder>,
		HasPropertyDataSourceConfigurator<PropertyListingBuilder> {

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

}
