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

import java.lang.reflect.Field;

import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.ParameterContainerType;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.URLParameterDefinition;

/**
 * Default {@link URLParameterDefinition} implementation.
 *
 * @since 5.2.0
 */
public class DefaultURLParameterDefinition extends AbstractNavigationParameterDefinition
		implements URLParameterDefinition {

	private static final long serialVersionUID = 5491077165723860355L;

	public DefaultURLParameterDefinition(String name, Class<?> type, ParameterContainerType containerType,
			Field field) {
		super(name, type, containerType, field);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultURLParameterDefinition other = (DefaultURLParameterDefinition) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

}
