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
import java.lang.reflect.Method;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.annotations.ViewParameterType;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.ParameterContainerType;

/**
 * Abstract {@link NavigationParameterDefinition} implementation.
 *
 * @since 5.2.0
 */
public class DefaultNavigationParameterDefinition implements NavigationParameterDefinition {

	private static final long serialVersionUID = 4917120934888696477L;

	private final String name;
	private final ViewParameterType viewParameterType;
	private final Class<?> type;
	private final ParameterContainerType containerType;
	private final Field field;

	private boolean required = false;
	private Object defaultValue = null;
	private Method readMethod = null;
	private Method writeMethod = null;

	public DefaultNavigationParameterDefinition(String name, ViewParameterType viewParameterType, Class<?> type,
			ParameterContainerType containerType, Field field) {
		super();
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		ObjectUtils.argumentNotNull(type, "Parameter type must be not null");
		ObjectUtils.argumentNotNull(containerType, "Parameter container type must be not null");
		ObjectUtils.argumentNotNull(field, "Parameter field must be not null");
		this.name = name;
		this.viewParameterType = (viewParameterType == null) ? ViewParameterType.QUERY : viewParameterType;
		this.type = type;
		this.containerType = containerType;
		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getViewParameterType()
	 */
	@Override
	public ViewParameterType getViewParameterType() {
		return viewParameterType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getType()
	 */
	@Override
	public Class<?> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getParameterContainerType()
	 */
	@Override
	public ParameterContainerType getParameterContainerType() {
		return containerType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getField()
	 */
	@Override
	public Field getField() {
		return field;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getDefaultValue()
	 */
	@Override
	public Optional<Object> getDefaultValue() {
		return Optional.ofNullable(defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getReadMethod()
	 */
	@Override
	public Optional<Method> getReadMethod() {
		return Optional.ofNullable(readMethod);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration.NavigationParameterDefinition#
	 * getWriteMethod()
	 */
	@Override
	public Optional<Method> getWriteMethod() {
		return Optional.ofNullable(writeMethod);
	}

	/**
	 * Set whether the parameter is required.
	 * @param required whether the parameter is required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Set the parameter default value.
	 * @param defaultValue the default value to set
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Set the parameter read method.
	 * @param readMethod the method to set
	 */
	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	/**
	 * Set the parameter write method.
	 * @param writeMethod the method to set
	 */
	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NavigationParameterDefinition [name=" + name + ", type=" + type + ", containerType=" + containerType
				+ ", required=" + required + ", defaultValue=" + defaultValue + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((viewParameterType == null) ? 0 : viewParameterType.hashCode());
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
		DefaultNavigationParameterDefinition other = (DefaultNavigationParameterDefinition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (viewParameterType != other.viewParameterType)
			return false;
		return true;
	}

}
