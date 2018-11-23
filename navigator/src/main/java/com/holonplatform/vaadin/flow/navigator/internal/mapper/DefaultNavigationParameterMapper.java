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
package com.holonplatform.vaadin.flow.navigator.internal.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

/**
 * Default {@link NavigationParameterMapper} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultNavigationParameterMapper implements NavigationParameterMapper {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
	private static final DateTimeFormatter OFFSET_DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
	private static final DateTimeFormatter OFFSET_TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

	private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterMapper#serialize(java.lang.Object)
	 */
	@Override
	public Collection<String> serialize(Object value) throws InvalidNavigationParameterException {
		if (value != null) {
			if (Collection.class.isAssignableFrom(value.getClass())) {
				return ((Collection<?>) value).stream().map(v -> serializeSimpleValue(v)).collect(Collectors.toSet());
			}
			return Collections.singleton(serializeSimpleValue(value));
		}
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterMapper#deserialize(java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	public <T> Collection<T> deserialize(Class<T> type, List<String> values)
			throws InvalidNavigationParameterException {
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		if (values != null && !values.isEmpty()) {
			return values.stream().filter(v -> v != null).filter(v -> !v.trim().equals(""))
					.map(v -> deserializeValue(type, v)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Serialize given value as a {@link String}.
	 * @param value The value to serialize (not null)
	 * @return The serialized value
	 */
	private static String serializeSimpleValue(Object value) {
		if (TypeUtils.isString(value.getClass())) {
			return (String) value;
		}
		if (TypeUtils.isBoolean(value.getClass())) {
			return ((boolean) value) ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
		}
		if (TypeUtils.isEnum(value.getClass())) {
			return ((Enum<?>) value).name();
		}
		if (LocalDate.class.isAssignableFrom(value.getClass())) {
			return LOCAL_DATE_FORMATTER.format((LocalDate) value);
		}
		if (LocalTime.class.isAssignableFrom(value.getClass())) {
			return LOCAL_TIME_FORMATTER.format((LocalTime) value);
		}
		if (LocalDateTime.class.isAssignableFrom(value.getClass())) {
			return LOCAL_DATETIME_FORMATTER.format((LocalDateTime) value);
		}
		if (OffsetDateTime.class.isAssignableFrom(value.getClass())) {
			return OFFSET_DATETIME_FORMATTER.format((OffsetDateTime) value);
		}
		if (OffsetTime.class.isAssignableFrom(value.getClass())) {
			return OFFSET_TIME_FORMATTER.format((OffsetTime) value);
		}
		if (Date.class.isAssignableFrom(value.getClass())) {
			return LOCAL_DATETIME_FORMATTER
					.format(Instant.ofEpochMilli(((Date) value).getTime()).atZone(DEFAULT_ZONE_ID).toLocalDateTime());
		}
		return String.valueOf(value);
	}

	/**
	 * Deserialize given parameter value using given type.
	 * @param <T> Value type
	 * @param type Value type (not null)
	 * @param value Parameter value (not null)
	 * @return Deserialized value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T deserializeValue(Class<T> type, String value) {
		if (TypeUtils.isString(type)) {
			return (T) value;
		}
		try {
			if (TypeUtils.isBoolean(value.getClass())) {
				return (T) Boolean.valueOf(value);
			}
			if (TypeUtils.isEnum(type)) {
				return (T) Enum.valueOf((Class<Enum>) type, value);
			}
			if (TypeUtils.isNumber(value.getClass())) {
				return (T) ConversionUtils.parseNumber(value, (Class<Number>) type);
			}
			if (LocalDate.class.isAssignableFrom(type)) {
				return (T) LocalDate.parse(value, LOCAL_DATE_FORMATTER);
			}
			if (LocalTime.class.isAssignableFrom(type)) {
				return (T) LocalTime.parse(value, LOCAL_TIME_FORMATTER);
			}
			if (LocalDateTime.class.isAssignableFrom(type)) {
				return (T) LocalDateTime.parse(value, LOCAL_DATETIME_FORMATTER);
			}
			if (OffsetDateTime.class.isAssignableFrom(type)) {
				return (T) OffsetDateTime.parse(value, OFFSET_DATETIME_FORMATTER);
			}
			if (OffsetTime.class.isAssignableFrom(type)) {
				return (T) OffsetTime.parse(value, OFFSET_TIME_FORMATTER);
			}
			if (Date.class.isAssignableFrom(type)) {
				if (value.contains("T")) {
					return (T) Date.from(
							LocalDateTime.parse(value, LOCAL_DATETIME_FORMATTER).atZone(DEFAULT_ZONE_ID).toInstant());
				} else {
					return (T) Date.from(
							LocalDate.parse(value, LOCAL_DATE_FORMATTER).atStartOfDay(DEFAULT_ZONE_ID).toInstant());
				}
			}
		} catch (Exception e) {
			throw new InvalidNavigationParameterException(
					"Failed to deserialize parameter value [" + value + "] using type [" + type.getName() + "]", e);
		}
		throw new InvalidNavigationParameterException("Failed to deserialize parameter value [" + value
				+ "] using type [" + type.getName() + "]: unsupported value type");
	}

}
