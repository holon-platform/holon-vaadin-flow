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

import java.util.Collection;

import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Represents an object bound to a property set.
 * 
 * @param <P> Property type
 *
 * @since 5.2.0
 */
public interface HasPropertySet<P> {

	/**
	 * Gets the available properties.
	 * @return The property set, empty if no property is available
	 */
	Collection<P> getProperties();

	/**
	 * Gets whether the property set contains the given <code>property</code>.
	 * @param property The property to check (not null)
	 * @return <code>true</code> if the property set contains the given <code>property</code>, <code>false</code>
	 *         otherwise
	 */
	default boolean hasProperty(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return getProperties().contains(property);
	}

}
