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
package com.holonplatform.vaadin.flow.data;

import java.io.Serializable;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.data.DefaultItemSort;

/**
 * Item sort directive.
 * 
 * @param <PROPERTY> Property type
 * 
 * @since 5.2.0
 *
 */
public interface ItemSort<PROPERTY> extends Serializable {

	/**
	 * Item property to sort
	 * @return Item property to sort
	 */
	PROPERTY getProperty();

	/**
	 * Sort direction
	 * @return <code>true</code> if ascending, <code>false</code> if descending
	 */
	boolean isAscending();

	/**
	 * Create an {@link ItemSort} using given property and sort direction.
	 * @param <PROPERTY> Property type
	 * @param property Property to sort (not null)
	 * @param ascending <code>true</code> to sort ascending, <code>false</code> for descending
	 * @return Item sort
	 */
	static <PROPERTY> ItemSort<PROPERTY> of(PROPERTY property, boolean ascending) {
		ObjectUtils.argumentNotNull(property, "Sort property must be not null");
		return new DefaultItemSort<>(property, ascending);
	}

	/**
	 * Create an ascending {@link ItemSort} using given property.
	 * @param <PROPERTY> Property type
	 * @param property Property to sort (not null)
	 * @return Item sort
	 */
	static <PROPERTY> ItemSort<PROPERTY> asc(PROPERTY property) {
		return of(property, true);
	}

	/**
	 * Create an descending {@link ItemSort} using given property.
	 * @param <PROPERTY> Property type
	 * @param property Property to sort (not null)
	 * @return Item sort
	 */
	static <PROPERTY> ItemSort<PROPERTY> desc(PROPERTY property) {
		return of(property, false);
	}
}
