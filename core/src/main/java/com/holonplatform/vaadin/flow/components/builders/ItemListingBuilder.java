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

import com.holonplatform.vaadin.flow.components.ItemListing;

/**
 * {@link ItemListing} builder.
 *
 * @param <T> Item type
 * @param <P> Item property type
 * @param <L> Item listing type
 * @param <B> Concrete builder type
 * 
 * @since 5.2.0
 */
public interface ItemListingBuilder<T, P, L extends ItemListing<T, P>, B extends ItemListingBuilder<T, P, L, B>>
		extends ItemListingConfigurator<T, P, B> {

	/**
	 * Build the {@link ItemListing}, displaying all the columns which corresponds to the configured properties.
	 * @return The item listing instance
	 */
	L build();

}
