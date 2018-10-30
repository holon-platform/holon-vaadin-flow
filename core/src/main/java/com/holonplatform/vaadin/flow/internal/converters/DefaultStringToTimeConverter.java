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
package com.holonplatform.vaadin.flow.internal.converters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.holonplatform.vaadin.flow.components.converters.StringToTimeConverter;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;

/**
 * Default {@link StringToTimeConverter} implementation.
 * 
 * @since 5.2.0
 */
public class DefaultStringToTimeConverter extends AbstractLocaleSupportConverter<String, LocalTime>
		implements StringToTimeConverter {

	private static final long serialVersionUID = 1951514034242175346L;

	public static final char DEFAULT_TIME_SEPARATOR = ':';

	private static final ConcurrentHashMap<Locale, Character> CACHE = new ConcurrentHashMap<>();

	private final Character fixedTimeSeparator;

	/**
	 * Default constructor.
	 */
	public DefaultStringToTimeConverter() {
		this(null, null);
	}

	/**
	 * Constructor with fixed locale.
	 * @param locale The fixed locale to use
	 */
	public DefaultStringToTimeConverter(Locale locale) {
		this(null, locale);
	}

	/**
	 * Constructor with fixed time separator.
	 * @param fixedTimeSeparator The fixed time separator character
	 */
	public DefaultStringToTimeConverter(char fixedTimeSeparator) {
		this(fixedTimeSeparator, null);
	}

	/**
	 * Constructor with fixed locale and time separator.
	 * @param fixedTimeSeparator The fixed time separator character
	 * @param locale The fixed locale to use
	 */
	public DefaultStringToTimeConverter(Character fixedTimeSeparator, Locale locale) {
		super(locale);
		this.fixedTimeSeparator = fixedTimeSeparator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.converter.Converter#convertToModel(java.lang.Object,
	 * com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public Result<LocalTime> convertToModel(String value, ValueContext context) {
		if (value != null && !value.trim().equals("")) {
			try {
				return Result.ok(LocalTime.parse(value, getDateTimeFormatter(context)));
			} catch (Exception e) {
				return Result.error("Could not convert String value [" + value + "] to LocalTime: " + e.getMessage());
			}
		}
		// null value if String value was null or blank
		return Result.ok(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.converter.Converter#convertToPresentation(java.lang.Object,
	 * com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public String convertToPresentation(LocalTime value, ValueContext context) {
		return (value != null) ? getDateTimeFormatter(context).format(value) : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToTimeConverter#getTimeSeparator()
	 */
	@Override
	public char getTimeSeparator() {
		if (getFixedTimeSeparator().isPresent()) {
			return getFixedTimeSeparator().get();
		}
		return getLocale().map(locale -> getTimeSeparator(locale)).orElse(DEFAULT_TIME_SEPARATOR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToTimeConverter#getValidationPattern()
	 */
	@Override
	public String getValidationPattern() {
		final char timeSeparator = getTimeSeparator();
		return "^([01]?[0-9]|2[0-3])(" + TextConverterUtils.escape(timeSeparator) + "|"
				+ TextConverterUtils.escape(timeSeparator) + "[0-5]|" + TextConverterUtils.escape(timeSeparator)
				+ "[0-5][0-9])?";
	}

	/**
	 * Get the time separator to use with given {@link Locale}.
	 * <p>
	 * If a fixed time separator is available, that one is returned.
	 * </p>
	 * @param locale Locale to use
	 * @return the time separator character
	 */
	protected char getTimeSeparator(Locale locale) {
		if (getFixedTimeSeparator().isPresent()) {
			return getFixedTimeSeparator().get();
		}
		return getLocaleTimeSeparator(locale);
	}

	/**
	 * Get the fixed time separator, if available.
	 * @return Optional fixed time separator
	 */
	protected Optional<Character> getFixedTimeSeparator() {
		return Optional.ofNullable(fixedTimeSeparator);
	}

	/**
	 * Get the {@link DateTimeFormatter} to use for value conversions.
	 * @param context The value context (not null)
	 * @return The {@link DateTimeFormatter} to use for value conversions
	 */
	protected DateTimeFormatter getDateTimeFormatter(ValueContext context) {
		char timeSeparator = getTimeSeparator(getLocale(context));
		final StringBuilder sb = new StringBuilder();
		sb.append("H'");
		if ('\'' == timeSeparator) {
			sb.append("'");
		}
		sb.append(timeSeparator);
		sb.append("'mm");
		return DateTimeFormatter.ofPattern(sb.toString());
	}

	/**
	 * Get the time separator character for given locale.
	 * @param locale The locale
	 * @return the time separator character, {@link #DEFAULT_TIME_SEPARATOR} if locale was <code>null</code>.
	 */
	private static char getLocaleTimeSeparator(Locale locale) {
		if (locale != null) {
			return CACHE.computeIfAbsent(locale, DefaultStringToTimeConverter::obtainTimeSeparator);
		}
		// default
		return DEFAULT_TIME_SEPARATOR;
	}

	private static char obtainTimeSeparator(Locale locale) {
		String value = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
				.format(LocalTime.of(0, 0));
		if (value != null) {
			char[] chars = value.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (i > 3) {
					break;
				}
				if (!Character.isDigit(chars[i])) {
					return chars[i];
				}
			}
		}
		return DEFAULT_TIME_SEPARATOR;
	}

}
