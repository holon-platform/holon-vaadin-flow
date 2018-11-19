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
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.PathParameterDefinition;

/**
 * @author BODSI08
 *
 */
public class DefaultPathParameterDefinition extends AbstractNavigationParameterDefinition
		implements PathParameterDefinition {

	private static final long serialVersionUID = 591184442885374074L;

	private final int segment;

	public DefaultPathParameterDefinition(int segment, Class<?> type, ParameterContainerType containerType,
			Field field) {
		super(type, containerType, field);
		this.segment = segment;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.PathParameterDefinition#
	 * getPathSegment()
	 */
	@Override
	public int getPathSegment() {
		return segment;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Path parameter [segment=" + segment + ", type=" + getType().getName() + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + segment;
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
		DefaultPathParameterDefinition other = (DefaultPathParameterDefinition) obj;
		if (segment != other.segment)
			return false;
		return true;
	}

}
