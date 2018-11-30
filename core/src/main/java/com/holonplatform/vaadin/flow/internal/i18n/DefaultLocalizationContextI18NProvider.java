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

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.i18n.LocalizationContextI18NProvider;

/**
 * Default {@link LocalizationContextI18NProvider} implementation.
 *
 * @since 5.2.0
 */
public class DefaultLocalizationContextI18NProvider implements LocalizationContextI18NProvider {

	private static final long serialVersionUID = 820749775814684825L;

	private final List<Locale> providedLocales;

	/**
	 * Constructor without provided {@link Locale}s.
	 */
	public DefaultLocalizationContextI18NProvider() {
		this(Collections.emptyList());
	}

	/**
	 * Constructor with provided {@link Locale}s.
	 * @param providedLocales The provided {@link Locale}s
	 */
	public DefaultLocalizationContextI18NProvider(List<Locale> providedLocales) {
		super();
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
		return LocalizationContext.getCurrent().map(c -> c.asMessageResolver())
				.flatMap(r -> r.getMessage(locale, key, params)).orElse(null);
	}

}
