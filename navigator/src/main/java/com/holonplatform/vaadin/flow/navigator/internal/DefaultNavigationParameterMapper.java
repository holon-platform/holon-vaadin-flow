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

import java.security.AccessController;
import java.security.PrivilegedAction;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
import com.holonplatform.vaadin.flow.navigator.NavigationParameterTypeMapper;
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

	/**
	 * Type mappers
	 */
	private static final Map<Class<?>, NavigationParameterTypeMapper<?>> typeMappers = new HashMap<>(8);

	@SuppressWarnings("rawtypes")
	private DefaultNavigationParameterMapper() {
		final Logger LOGGER = VaadinLogger.create();
		LOGGER.debug(() -> "Load NavigationParameterTypeMappers using ServiceLoader with service name: "
				+ NavigationParameterTypeMapper.class.getName());
		Iterable<NavigationParameterTypeMapper> mappers = AccessController
				.doPrivileged(new PrivilegedAction<Iterable<NavigationParameterTypeMapper>>() {
					@Override
					public Iterable<NavigationParameterTypeMapper> run() {
						return ServiceLoader.load(NavigationParameterTypeMapper.class);
					}
				});
		mappers.forEach(mapper -> {
			final Class<?> type = mapper.getParameterType();
			if (type == null) {
				LOGGER.error("The NavigationParameterTypeMapper [" + mapper.getClass().getName()
						+ "] returned null from getParameterType() and will be ignored");
			} else {
				typeMappers.put(type, mapper);
				LOGGER.error("NavigationParameterTypeMapper [" + mapper.getClass().getName()
						+ "] registered and bound to the [" + type.getName() + "] parameter type");
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterMapper#serialize(java.lang.Object)
	 */
	@Override
	public List<String> serialize(Object value) throws InvalidNavigationParameterException {
		if (value != null) {
			if (Collection.class.isAssignableFrom(value.getClass())) {
				return ((Collection<?>) value).stream().filter(v -> v != null).map(v -> serializeParameterValue(v))
						.collect(Collectors.toList());
			}
			return Collections.singletonList(serializeParameterValue(value));
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationParameterMapper#deserialize(java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	public <T> List<T> deserialize(Class<T> type, List<String> values) throws InvalidNavigationParameterException {
		ObjectUtils.argumentNotNull(type, "Type must be not null");
		if (values != null && !values.isEmpty()) {
			return values.stream().filter(v -> v != null).filter(v -> !v.trim().equals(""))
					.map(v -> deserializeParameterValue(type, v)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Get the {@link NavigationParameterTypeMapper} to use to handle the given parameter value <code>type</code>, if
	 * available.
	 * @param <T> Parameter type
	 * @param type the parameter value type
	 * @return Optional {@link NavigationParameterTypeMapper}
	 */
	@SuppressWarnings("unchecked")
	private static <T> Optional<NavigationParameterTypeMapper<T>> getTypeMapper(Class<T> type) {
		return typeMappers.entrySet().stream().filter(e -> TypeUtils.isAssignable(type, e.getKey()))
				.map(e -> e.getValue()).map(m -> (NavigationParameterTypeMapper<T>) m).findFirst();
	}

	/**
	 * Serialize given value as a {@link String}.
	 * @param value The value to serialize (not null)
	 * @return The serialized value
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	private static String serializeParameterValue(Object value) throws InvalidNavigationParameterException {
		// check type mapper
		@SuppressWarnings("rawtypes")
		NavigationParameterTypeMapper mapper = getTypeMapper(value.getClass()).orElse(null);
		if (mapper != null) {
			@SuppressWarnings("unchecked")
			String serialized = mapper.serialize(value);
			if (serialized == null) {
				throw new InvalidNavigationParameterException(
						"The NavigationParameterTypeMapper [" + mapper.getClass().getName()
								+ "] returned a null serialized value for the parameter value [" + value + "]");
			}
			return serialized;
		}
		// default strategy
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
	 * @throws InvalidNavigationParameterException If an error occurred
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T deserializeParameterValue(Class<T> type, String value)
			throws InvalidNavigationParameterException {
		// check type mapper
		NavigationParameterTypeMapper<T> mapper = getTypeMapper(type).orElse(null);
		if (mapper != null) {
			T deserialized = mapper.deserialize(value);
			if (deserialized == null) {
				throw new InvalidNavigationParameterException(
						"The NavigationParameterTypeMapper [" + mapper.getClass().getName()
								+ "] returned a null deserialized value for the parameter value [" + value + "]");
			}
			return deserialized;
		}
		// default strategy

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
