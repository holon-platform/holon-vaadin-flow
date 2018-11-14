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
 * A registry to handle {@link InputPropertyConfiguration}s.
 * 
 * @since 5.2.0
 */
public interface InputPropertyConfigurationRegistry {

	/**
	 * Get the {@link InputPropertyConfiguration} bound to given property.
	 * @param <T> Property type
	 * @param property The property for which to obtain the configuration (not null)
	 * @return The {@link InputPropertyConfiguration} bound to given property
	 */
	<T> InputPropertyConfiguration<T> get(Property<T> property);

	/**
	 * Create a new {@link InputPropertyConfigurationRegistry}.
	 * @return A new {@link InputPropertyConfigurationRegistry}
	 */
	static InputPropertyConfigurationRegistry create() {
		return new DefaultInputPropertyConfigurationRegistry();
	}

}
