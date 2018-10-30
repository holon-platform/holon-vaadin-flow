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
package com.holonplatform.vaadin.flow.components.converters;

import java.time.LocalTime;
import java.util.Locale;

import com.holonplatform.vaadin.flow.internal.converters.DefaultStringToTimeConverter;
import com.vaadin.flow.data.converter.Converter;

/**
 * A {@link Converter}s that convert from {@link LocalTime} types to {@link String} and back.
 *
 * @since 5.2.0
 */
public interface StringToTimeConverter extends Converter<String, LocalTime> {

	/**
	 * Get the time separator character which corresponds to the converter configuration.
	 * @return the time separator character
	 */
	char getTimeSeparator();

	/**
	 * Get the regex validation pattern which corresponds to the converter configuration.
	 * @return the regex validation pattern
	 */
	String getValidationPattern();

	// builders

	/**
	 * Create a new {@link StringToTimeConverter}.
	 * @return A new {@link StringToTimeConverter}
	 */
	static StringToTimeConverter create() {
		return new DefaultStringToTimeConverter();
	}

	/**
	 * Create a new {@link StringToTimeConverter}.
	 * @param timeSeparator The fixed time separator character to use
	 * @return A new {@link StringToTimeConverter}
	 */
	static StringToTimeConverter create(char timeSeparator) {
		return new DefaultStringToTimeConverter(timeSeparator);
	}

	/**
	 * Create a new {@link StringToTimeConverter}.
	 * @param locale The fixed {@link Locale} to use
	 * @return A new {@link StringToTimeConverter}
	 */
	static StringToTimeConverter create(Locale locale) {
		return new DefaultStringToTimeConverter(locale);
	}

	/**
	 * Create a new {@link StringToTimeConverter}.
	 * @param timeSeparator The fixed time separator character to use
	 * @param locale The fixed {@link Locale} to use
	 * @return A new {@link StringToTimeConverter}
	 */
	static StringToTimeConverter create(char timeSeparator, Locale locale) {
		return new DefaultStringToTimeConverter(timeSeparator, locale);
	}

}
