/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal;

import java.time.LocalDate;
import java.util.Date;

import javax.annotation.Priority;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.Input;

/**
 * Default {@link PropertyRenderer} to create {@link Input} type {@link Property} representations.
 * 
 * @param <T> Property type
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public class DefaultInputPropertyRenderer<T> implements PropertyRenderer<Input, T> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
	 */
	@Override
	public Class<? extends Input> getRenderType() {
		return Input.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
	 */
	@Override
	public Input render(Property<T> property) {

		ObjectUtils.argumentNotNull(property, "Property must be not null");

		// Try to render the property according to the property type
		final Class<?> propertyType = property.getType();

		if (TypeUtils.isString(propertyType)) {
			// String
			return renderString(property);
		}
		if (TypeUtils.isBoolean(propertyType)) {
			// Boolean
			return renderBoolean(property);
		}
		if (TypeUtils.isEnum(propertyType)) {
			// Enum
			return renderEnum(property);
		}
		if (LocalDate.class.isAssignableFrom(propertyType)) {
			// LocalDate
			return renderLocalDate(property);
		}
		if (TypeUtils.isDate(propertyType)) {
			// Date
			return renderDate(property);
		}
		if (TypeUtils.isNumber(propertyType)) {
			// Number
			return renderNumber(property);
		}

		return null;
	}

	/**
	 * Render the property as a {@link String} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<String> renderString(Property<T> property) {
		return Input.string().emptyValuesAsNull(true).label(property).build();
	}

	/**
	 * Render the property as a {@link Boolean} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<Boolean> renderBoolean(Property<T> property) {
		return Input.boolean_().label(property).build();
	}

	/**
	 * Render the property as a {@link Enum} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	@SuppressWarnings("unchecked")
	protected Input<T> renderEnum(Property<T> property) {
		final Class<Enum> enumType = (Class<Enum>) property.getType();
		return (Input<T>) Input.singleSelect(enumType).items(enumType.getEnumConstants()).label(property).build();
	}

	/**
	 * Render the property as a {@link LocalDate} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<LocalDate> renderLocalDate(Property<T> property) {
		return Input.localDate().label(property).build();
	}

	/**
	 * Render the property as a {@link Date} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<Date> renderDate(Property<T> property) {
		return Input.date().label(property).build();
	}

	/**
	 * Render the property as a numeric type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	@SuppressWarnings("unchecked")
	protected Input<T> renderNumber(Property<T> property) {
		// numeric type
		final Class<? extends Number> type = (Class<? extends Number>) property.getType();
		// configuration
		int decimals = property.getConfiguration().getParameter(StringValuePresenter.DECIMAL_POSITIONS).orElse(-1);
		boolean useGrouping = !property.getConfiguration().getParameter(StringValuePresenter.DISABLE_GROUPING)
				.orElse(Boolean.FALSE);

		return (Input<T>) Input.number(type).label(property).useGrouping(useGrouping).maxDecimals(decimals).build();
	}

}
