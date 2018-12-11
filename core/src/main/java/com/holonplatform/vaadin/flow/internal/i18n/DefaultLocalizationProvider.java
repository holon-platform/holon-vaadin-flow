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

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;

/**
 * Default {@link LocalizationProvider} implementation.
 *
 * @since 5.2.0
 */
public class DefaultLocalizationProvider implements LocalizationProvider {

	private static final long serialVersionUID = -2638646173588685927L;

	private final I18NProvider i18nProvider;
	private final LocalizationContext localizationContext;

	/**
	 * Constructor using a {@link I18NProvider}.
	 * @param i18nProvider The {@link I18NProvider} to use (not null)
	 */
	public DefaultLocalizationProvider(I18NProvider i18nProvider) {
		super();
		ObjectUtils.argumentNotNull(i18nProvider, "I18NProvider must be not null");
		this.i18nProvider = i18nProvider;
		this.localizationContext = null;
	}

	/**
	 * Constructor using a {@link LocalizationContext}.
	 * @param localizationContext The {@link LocalizationContext} to use (not null)
	 */
	public DefaultLocalizationProvider(LocalizationContext localizationContext) {
		super();
		ObjectUtils.argumentNotNull(localizationContext, "LocalizationContext must be not null");
		this.i18nProvider = null;
		this.localizationContext = localizationContext;
	}

	/**
	 * Get the {@link I18NProvider}, if available.
	 * @return Optional I18NProvider
	 */
	protected Optional<I18NProvider> getI18nProvider() {
		return Optional.ofNullable(i18nProvider);
	}

	/**
	 * Get the {@link LocalizationContext}, if available.
	 * @return Optional LocalizationContext
	 */
	protected Optional<LocalizationContext> getLocalizationContext() {
		return Optional.ofNullable(localizationContext);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider#getLocale()
	 */
	@Override
	public Optional<Locale> getLocale() {
		// check UI
		final UI currentUi = UI.getCurrent();
		Locale locale = currentUi == null ? null : currentUi.getLocale();
		if (locale == null) {
			// check LocalizationContext
			locale = getLocalizationContext().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale()).orElse(null);
		}
		if (locale == null) {
			// check I18NProvider
			locale = getI18nProvider().map(p -> {
				List<Locale> locales = p.getProvidedLocales();
				if (locales != null && !locales.isEmpty()) {
					return locales.get(0);
				}
				return null;
			}).orElse(null);
		}
		return Optional.ofNullable(locale);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider#getMessage(java.util.Locale,
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public Optional<String> getMessage(Locale locale, Localizable localizable) {
		ObjectUtils.argumentNotNull(locale, "Locale must be not null");
		ObjectUtils.argumentNotNull(localizable, "Localizable must be not null");
		// check message code
		if (localizable.getMessageCode() == null) {
			return Optional.ofNullable(localizable.getMessage());
		}
		// check I18nProvider
		if (getI18nProvider().isPresent()) {
			return Optional.ofNullable(getI18nProvider()
					.map(p -> p.getTranslation(localizable.getMessageCode(), locale, localizable.getMessageArguments()))
					.orElseGet(() -> localizable.getMessage()));
		}
		// check LocalizationContext
		return Optional.ofNullable(getLocalizationContext().flatMap(l -> l.asMessageResolver().getMessage(locale,
				localizable.getMessageCode(), localizable.getMessageArguments())).orElse(localizable.getMessage()));
	}

	/**
	 * Get the current {@link I18NProvider}, if available.
	 * @return Optional current {@link I18NProvider}
	 */
	private static Optional<I18NProvider> getCurrentI18nProvider() {
		final VaadinService service = VaadinService.getCurrent();
		if (service != null) {
			return Optional.ofNullable(VaadinService.getCurrent().getInstantiator().getI18NProvider());
		}
		return Optional.empty();
	}

	/**
	 * Get the current {@link Locale}, if available.
	 * <p>
	 * The current {@link Locale} retrieving strategy is:
	 * <ul>
	 * <li>If a current {@link UI} is available and a UI {@link Locale} is configured, the UI locale is returned.</li>
	 * <li>If a {@link LocalizationContext} is available as a {@link Context} resource and it is localized, the
	 * {@link LocalizationContext} {@link Locale} is returned.</li>
	 * <li>If a {@link I18NProvider} is available from the {@link VaadinService}, the first {@link Locale} from
	 * {@link I18NProvider#getProvidedLocales()} is returned, if available.</li>
	 * </ul>
	 * @return Optional current {@link Locale}
	 * @see LocalizationContext#getCurrent()
	 */
	public static Optional<Locale> getCurrentLocale() {
		// check UI
		final UI currentUi = UI.getCurrent();
		Locale locale = currentUi == null ? null : currentUi.getLocale();
		if (locale == null) {
			// check LocalizationContext
			locale = LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
					.orElse(null);
		}
		if (locale == null) {
			// check I18NProvider
			locale = getCurrentI18nProvider().map(p -> {
				List<Locale> locales = p.getProvidedLocales();
				if (locales != null && !locales.isEmpty()) {
					return locales.get(0);
				}
				return null;
			}).orElse(null);
		}
		return Optional.ofNullable(locale);
	}

	/**
	 * Get the message localization for given <code>locale</code>, using the provided {@link Localizable} to obtain the
	 * message localization key ({@link Localizable#getMessageCode()}) and the optional localization arguments.
	 * <p>
	 * If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
	 * localization. Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context}
	 * resource and it is localized.
	 * </p>
	 * @param locale The {@link Locale} for which to obtain the message localization (not null)
	 * @param localizable The {@link Localizable} which represents the message to localize (not null)
	 * @return The localized message, if available. If the given <code>localizable</code> provides a default message
	 *         ({@link Localizable#getMessage()}) and a message localization is not available, the default message is
	 *         returned
	 * @see LocalizationContext#getCurrent()
	 */
	public static Optional<String> getLocalization(Locale locale, Localizable localizable) {
		ObjectUtils.argumentNotNull(locale, "Locale must be not null");
		ObjectUtils.argumentNotNull(localizable, "Localizable must be not null");
		// check message code
		if (localizable.getMessageCode() == null) {
			return Optional.ofNullable(localizable.getMessage());
		}
		// check I18nProvider
		if (getCurrentI18nProvider().isPresent()) {
			return Optional.ofNullable(getCurrentI18nProvider()
					.map(p -> p.getTranslation(localizable.getMessageCode(), locale, localizable.getMessageArguments()))
					.orElseGet(() -> localizable.getMessage()));
		}
		// check LocalizationContext
		return Optional
				.ofNullable(LocalizationContext
						.getCurrent().flatMap(l -> l.asMessageResolver().getMessage(locale,
								localizable.getMessageCode(), localizable.getMessageArguments()))
						.orElse(localizable.getMessage()));
	}

	/**
	 * Get the message localization for the current {@link Locale}, using the provided {@link Localizable} to obtain the
	 * message localization key ({@link Localizable#getMessageCode()}) and the optional localization arguments.
	 * <p>
	 * If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
	 * localization. Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context}
	 * resource and it is localized.
	 * </p>
	 * <p>
	 * The message localization will be performed only if current {@link Locale} is available.
	 * </p>
	 * @param localizable The {@link Localizable} which represents the message to localize (not null)
	 * @return The localized message, if available. If the given <code>localizable</code> provides a default message
	 *         ({@link Localizable#getMessage()}) and a message localization is not available, the default message is
	 *         returned
	 * @see #getCurrentLocale()
	 */
	public static Optional<String> localize(Localizable localizable) {
		ObjectUtils.argumentNotNull(localizable, "Localizable must be not null");
		// check message code
		if (localizable.getMessageCode() == null) {
			return Optional.ofNullable(localizable.getMessage());
		}
		// check I18nProvider
		final Locale currentLocale = getCurrentLocale().orElse(null);
		if (currentLocale != null && getCurrentI18nProvider().isPresent()) {
			return Optional
					.ofNullable(
							getCurrentI18nProvider()
									.map(p -> p.getTranslation(localizable.getMessageCode(), currentLocale,
											localizable.getMessageArguments()))
									.orElseGet(() -> localizable.getMessage()));
		}
		// check LocalizationContext
		return Optional.ofNullable(LocalizationContext.translate(localizable, true));
	}

}
