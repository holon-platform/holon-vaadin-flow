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
package com.holonplatform.vaadin.flow.internal.i18n;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.i18n.LocalizationContextI18NProvider;

/**
 * Default {@link LocalizationContextI18NProvider} implementation.
 *
 * @since 5.2.0
 */
public class DefaultLocalizationContextI18NProvider implements LocalizationContextI18NProvider {

	private static final long serialVersionUID = 820749775814684825L;

	private final LocalizationContext localizationContext;

	private final List<Locale> providedLocales;

	/**
	 * Constructor without provided {@link Locale}s.
	 */
	public DefaultLocalizationContextI18NProvider() {
		this(null, Collections.emptyList());
	}

	/**
	 * Constructor with provided {@link Locale}s.
	 * @param providedLocales The provided {@link Locale}s
	 */
	public DefaultLocalizationContextI18NProvider(List<Locale> providedLocales) {
		this(null, providedLocales);
	}

	/**
	 * Constructor without provided {@link Locale}s.
	 * @param localizationContext The {@link LocalizationContext} to use
	 */
	public DefaultLocalizationContextI18NProvider(LocalizationContext localizationContext) {
		this(localizationContext, Collections.emptyList());
	}

	/**
	 * Constructor with provided {@link Locale}s.
	 * @param localizationContext The {@link LocalizationContext} to use
	 * @param providedLocales The provided {@link Locale}s
	 */
	public DefaultLocalizationContextI18NProvider(LocalizationContext localizationContext,
			List<Locale> providedLocales) {
		super();
		this.localizationContext = localizationContext;
		this.providedLocales = (providedLocales != null) ? providedLocales : Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.i18n.I18NProvider#getProvidedLocales()
	 */
	@Override
	public List<Locale> getProvidedLocales() {
		return providedLocales;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.i18n.I18NProvider#getTranslation(java.lang.String, java.util.Locale, java.lang.Object[])
	 */
	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		return getLocalizationContext().map(c -> c.asMessageResolver()).flatMap(r -> r.getMessage(locale, key, params))
				.orElse(null);
	}

	/**
	 * Get the {@link LocalizationContext} context to use.
	 * @return The configured {@link LocalizationContext} or the context {@link LocalizationContext}, if available
	 */
	protected Optional<LocalizationContext> getLocalizationContext() {
		if (localizationContext != null) {
			return Optional.of(localizationContext);
		}
		return LocalizationContext.getCurrent();
	}

}
