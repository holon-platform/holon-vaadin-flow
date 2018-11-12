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
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;

/**
 * {@link BeanListing} builder.
 * 
 * @param <T> Bean type
 *
 * @since 5.2.0
 */
public interface BeanListingBuilder<T> extends ItemListingBuilder<T, String, BeanListing<T>, BeanListingBuilder<T>> {

	/**
	 * Add a property {@link Validator} to be used when the property value is edited using the item editor.
	 * @param property The property for which to add the validator (not null)
	 * @param validator The validator to add (not null)
	 * @return this
	 */
	BeanListingBuilder<T> withValidator(String property, Validator<?> validator);

	/**
	 * Set the {@link Input} to use as given property editor.
	 * @param property The property for which to set the editor (not null)
	 * @param editor The property editor (not null)
	 * @return this
	 */
	BeanListingBuilder<T> editor(String property, Input<?> editor);

}
