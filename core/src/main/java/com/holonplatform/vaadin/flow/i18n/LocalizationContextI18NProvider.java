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
package com.holonplatform.vaadin.flow.i18n;

import java.util.List;
import java.util.Locale;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.internal.i18n.DefaultLocalizationContextI18NProvider;
import com.vaadin.flow.i18n.I18NProvider;

/**
 * A {@link I18NProvider} which uses the current {@link LocalizationContext} to provide the messages localization.
 * <p>
 * A {@link LocalizationContext} reference must be available from {@link LocalizationContext#getCurrent()} in order for
 * the localization to work, otherwise a <code>null</code> translation will be returned for any message key.
 * </p>
 *
 * @since 5.2.0
 */
public interface LocalizationContextI18NProvider extends I18NProvider {

	/**
	 * Create a new {@link LocalizationContextI18NProvider} without any provided {@link Locale}.
	 * @return A new {@link LocalizationContextI18NProvider}
	 * @see I18NProvider#getProvidedLocales()
	 */
	static LocalizationContextI18NProvider create() {
		return new DefaultLocalizationContextI18NProvider();
	}

	/**
	 * Create a new {@link LocalizationContextI18NProvider} using given provided locales.
	 * @return A new {@link LocalizationContextI18NProvider}
	 * @param providedLocales The provided locales
	 * @see I18NProvider#getProvidedLocales()
	 */
	static LocalizationContextI18NProvider create(List<Locale> providedLocales) {
		return new DefaultLocalizationContextI18NProvider(providedLocales);
	}

}
