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

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.builders.BeanListingBuilder.DatastoreBeanListingBuilder;

/**
 * {@link BeanListing} builder.
 * 
 * @param <T> Bean type
 *
 * @since 5.2.0
 */
public interface BeanListingBuilder<T> extends BeanListingConfigurator<T, BeanListingBuilder<T>>,
		HasBeanDatastoreDataProviderConfigurator<T, DatastoreBeanListingBuilder<T>, BeanListingBuilder<T>> {

	/**
	 * {@link BeanListing} builder with {@link DatastoreDataProviderConfigurator} support.
	 * 
	 * @param <T> Bean type
	 * 
	 * @since 5.2.0
	 */
	public interface DatastoreBeanListingBuilder<T> extends BeanListingConfigurator<T, DatastoreBeanListingBuilder<T>>,
			DatastoreDataProviderConfigurator<T, DatastoreBeanListingBuilder<T>> {

	}

}
