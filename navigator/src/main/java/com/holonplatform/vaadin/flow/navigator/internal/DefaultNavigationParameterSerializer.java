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
package com.holonplatform.vaadin.flow.navigator.internal;

/**
 * Default {@link NavigationParameterSerializer} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultNavigationParameterSerializer implements NavigationParameterSerializer {

	INSTANCE;

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterSerializer#serialize(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> String serialize(Class<T> type, T value) {
		// TODO 
		return null;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterSerializer#deserialize(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T deserialize(Class<T> type, String value) {
		// TODO 
		return null;
	}
	
}
