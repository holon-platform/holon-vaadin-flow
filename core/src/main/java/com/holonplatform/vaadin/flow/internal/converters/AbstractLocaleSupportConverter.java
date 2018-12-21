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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * Abstract class for {@link Converter} with {@link Locale} support.
 * 
 * @since 5.2.0
 */
public abstract class AbstractLocaleSupportConverter<PRESENTATION, MODEL> implements Converter<PRESENTATION, MODEL> {

	private static final long serialVersionUID = 403136100747168679L;

	public static final List<Character> REGEX_RESERVED = Arrays.asList('.', '<', '(', '[', '{', '\\', '^', '-', '=',
			'$', '!', '|', ']', '}', ')', '?', '*', '+', '>');

	/**
	 * Locale
	 */
	private final Locale fixedLocale;

	/**
	 * Default constructor.
	 */
	public AbstractLocaleSupportConverter() {
		this(null);
	}

	/**
	 * Constructor with locale.
	 * @param fixedLocale The fixed locale to use
	 */
	public AbstractLocaleSupportConverter(Locale fixedLocale) {
		super();
		this.fixedLocale = fixedLocale;
	}

	/**
	 * Get the {@link Locale} currently bound to this converter
	 * @return the {@link Locale} currently bound to this converter, if available
	 */
	protected Optional<Locale> getLocale() {
		if (getFixedLocale().isPresent()) {
			return getFixedLocale();
		}
		return LocalizationProvider.getCurrentLocale();
	}

	/**
	 * Get the fixed {@link Locale} to use.
	 * @return Optional fixed locale
	 */
	protected Optional<Locale> getFixedLocale() {
		return Optional.ofNullable(fixedLocale);
	}

	/**
	 * Get the {@link Locale} to use for number formatting.
	 * <p>
	 * If a fixed {@link Locale} is configured, it is returned.
	 * </p>
	 * <p>
	 * Otherwise, the {@link Locale} is obtained from the given context, or using
	 * {@link LocalizationProvider#getCurrentLocale()} if not available. The default {@link Locale} is returned if a
	 * current {@link Locale} cannot be detected.
	 * </p>
	 * @param context Value context (not null)
	 * @return The {@link Locale} to use for number formatting
	 */
	protected Locale getLocale(ValueContext context) {
		if (getFixedLocale().isPresent()) {
			return getFixedLocale().get();
		}
		if (context == null) {
			return LocalizationProvider.getCurrentLocale().orElseGet(() -> Locale.getDefault());
		}
		return context.getLocale()
				.orElseGet(() -> LocalizationProvider.getCurrentLocale().orElseGet(() -> Locale.getDefault()));
	}

	/**
	 * Escape given character for regex if required.
	 * @param c Character to escape
	 * @return Escaped character
	 */
	protected static String escape(Character c) {
		if (c == null) {
			return null;
		}
		if (REGEX_RESERVED.contains(c)) {
			return "\\" + c;
		}
		return String.valueOf(c);
	}

}
