/*
 * Copyright 2000-2017 Holon TDCN.
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

/**
 * An {@link ItemListing} component using a bean type as item type and the bean property names as property set.
 * 
 * @param <T> Bean type
 * 
 * @since 5.2.0
 */
public interface BeanListing<T> extends ItemListing<T, String> {

	// TODO
	// static <T> BeanListingBuilder<T> builder(Class<T> itemType) {
	// return new DefaultBeanListingBuilder<>(itemType);
	// }

}
