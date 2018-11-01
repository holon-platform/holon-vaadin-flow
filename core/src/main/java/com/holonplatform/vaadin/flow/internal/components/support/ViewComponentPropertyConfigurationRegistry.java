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
package com.holonplatform.vaadin.flow.internal.components.support;

import com.holonplatform.core.property.Property;

/**
 * A registry to handle {@link ViewComponentPropertyConfiguration}s.
 * 
 * @since 5.2.0
 */
public interface ViewComponentPropertyConfigurationRegistry {

	/**
	 * Get the {@link ViewComponentPropertyConfiguration} bound to given property.
	 * @param <T> Property type
	 * @param property The property for which to obtain the configuration (not null)
	 * @return The {@link ViewComponentPropertyConfiguration} bound to given property
	 */
	<T> ViewComponentPropertyConfiguration<T> get(Property<T> property);

	static ViewComponentPropertyConfigurationRegistry create() {
		return new DefaultViewComponentPropertyConfigurationRegistry();
	}

}
