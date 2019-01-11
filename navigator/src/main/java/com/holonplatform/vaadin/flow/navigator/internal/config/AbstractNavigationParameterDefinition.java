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
package com.holonplatform.vaadin.flow.navigator.internal.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration.NavigationParameterDefinition;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration.ParameterContainerType;

/**
 * Abstract {@link NavigationParameterDefinition} implementation.
 *
 * @since 5.2.0
 */
public abstract class AbstractNavigationParameterDefinition implements NavigationParameterDefinition {

	private static final long serialVersionUID = 4917120934888696477L;

	private final Class<?> type;
	private final ParameterContainerType containerType;
	private final Field field;

	private boolean required = false;
	private Object defaultValue = null;
	private Method writeMethod = null;

	public AbstractNavigationParameterDefinition(Class<?> type, ParameterContainerType containerType, Field field) {
		super();
		ObjectUtils.argumentNotNull(type, "Parameter type must be not null");
		ObjectUtils.argumentNotNull(containerType, "Parameter container type must be not null");
		ObjectUtils.argumentNotNull(field, "Parameter field must be not null");
		this.type = type;
		this.containerType = containerType;
		this.field = field;
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
	 * Set the parameter write method.
	 * @param writeMethod the method to set
	 */
	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

}
