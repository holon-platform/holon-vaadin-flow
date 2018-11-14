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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.EnumItemCaptionGenerator;
import com.holonplatform.vaadin.flow.internal.converters.DateToLocalTimeConverter;

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
	public Input render(Property<? extends T> property) {

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
		if (LocalTime.class.isAssignableFrom(propertyType)) {
			// LocalDate
			return renderLocalTime(property);
		}
		if (LocalDateTime.class.isAssignableFrom(propertyType)) {
			// LocalDate
			return renderLocalDateTime(property);
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
	 * Create an {@link Input} to handle given value <code>type</code>, if available.
	 * @param <V> Value type
	 * @param type The value type (not null)
	 * @return Optional {@link Input} component
	 */
	@SuppressWarnings("unchecked")
	public static <V> Optional<Input<V>> createByType(Class<V> type) {
		ObjectUtils.argumentNotNull(type, "Type must be not null");

		if (TypeUtils.isString(type)) {
			// String
			return Optional.of(Input.string().emptyValuesAsNull(true).build()).map(input -> (Input<V>) input);
		}
		if (TypeUtils.isBoolean(type)) {
			// Boolean
			return Optional.of(Input.boolean_().build()).map(input -> (Input<V>) input);
		}
		if (TypeUtils.isEnum(type)) {
			// Enum
			final Class<Enum> enumType = (Class<Enum>) type;
			return Optional.of(Input.singleSelect(enumType).items(enumType.getEnumConstants()).build())
					.map(input -> (Input<V>) input);
		}
		if (LocalDate.class.isAssignableFrom(type)) {
			// LocalDate
			return Optional.of(Input.localDate().build()).map(input -> (Input<V>) input);
		}
		if (LocalTime.class.isAssignableFrom(type)) {
			// LocalDate
			return Optional.of(Input.localTime().build()).map(input -> (Input<V>) input);
		}
		if (LocalDateTime.class.isAssignableFrom(type)) {
			// LocalDate
			return Optional.of(Input.localDateTime().build()).map(input -> (Input<V>) input);
		}
		if (TypeUtils.isDate(type)) {
			// Date
			return Optional.of(Input.date().build()).map(input -> (Input<V>) input);
		}
		if (TypeUtils.isNumber(type)) {
			// Number
			final Class<? extends Number> numberType = (Class<? extends Number>) type;
			return Optional.of(Input.number(numberType)).map(input -> (Input<V>) input);
		}

		return Optional.empty();
	}

	/**
	 * Render the property as a {@link String} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<String> renderString(Property<? extends T> property) {
		return Input.string().emptyValuesAsNull(true).label(property).readOnly(property.isReadOnly()).build();
	}

	/**
	 * Render the property as a {@link Boolean} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<Boolean> renderBoolean(Property<? extends T> property) {
		return Input.boolean_().label(property).readOnly(property.isReadOnly()).build();
	}

	/**
	 * Render the property as a {@link Enum} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	@SuppressWarnings("unchecked")
	protected Input<T> renderEnum(Property<? extends T> property) {
		final Class<Enum> enumType = (Class<Enum>) property.getType();
		return (Input<T>) Input.singleSelect(enumType).items(enumType.getEnumConstants())
				.itemCaptionGenerator(new EnumItemCaptionGenerator<>()).label(property).readOnly(property.isReadOnly())
				.build();
	}

	/**
	 * Render the property as a {@link LocalDate} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<LocalDate> renderLocalDate(Property<? extends T> property) {
		return Input.localDate().label(property).readOnly(property.isReadOnly()).build();
	}

	/**
	 * Render the property as a {@link LocalTime} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<LocalTime> renderLocalTime(Property<? extends T> property) {
		return Input.localTime().label(property).readOnly(property.isReadOnly()).build();
	}

	/**
	 * Render the property as a {@link LocalDateTime} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<LocalDateTime> renderLocalDateTime(Property<? extends T> property) {
		return Input.localDateTime().label(property).readOnly(property.isReadOnly()).build();
	}

	/**
	 * Render the property as a {@link Date} type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	protected Input<Date> renderDate(Property<? extends T> property) {
		final TemporalType type = property.getConfiguration().getTemporalType().orElse(TemporalType.DATE);
		switch (type) {
		case TIME:
			return Input.from(Input.localTime().label(property).readOnly(property.isReadOnly()).build(),
					new DateToLocalTimeConverter());
		case DATE_TIME:
			return Input.dateTime().label(property).readOnly(property.isReadOnly()).build();
		case DATE:
		default:
			return Input.date().label(property).readOnly(property.isReadOnly()).build();
		}
	}

	/**
	 * Render the property as a numeric type {@link Input}.
	 * @param property Property to render
	 * @return The {@link Input} instance
	 */
	@SuppressWarnings("unchecked")
	protected Input<T> renderNumber(Property<? extends T> property) {
		// numeric type
		final Class<? extends Number> type = (Class<? extends Number>) property.getType();
		// configuration
		int decimals = property.getConfiguration().getParameter(StringValuePresenter.DECIMAL_POSITIONS).orElse(-1);
		// build Input
		return (Input<T>) Input.number(type).label(property).readOnly(property.isReadOnly()).maxDecimals(decimals)
				.build();
	}

}
