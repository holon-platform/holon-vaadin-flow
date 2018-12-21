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

import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.i18n.DefaultLocalizationProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;

/**
 * Message localization provider.
 * <p>
 * A {@link LocalizationProvider} can be used to obtain the current {@link Locale} and for messages localization, using
 * a message localization key and optional localization arguments.
 * </p>
 * <p>
 * A {@link LocalizationProvider} can be backed by a Vaadin {@link I18NProvider} or by the Holon Platform
 * {@link LocalizationContext}. See the {@link #create(I18NProvider)} and {@link #create(LocalizationContext)} methods.
 * </p>
 * <p>
 * A set of <code>static</code> methods are available to obtain the current {@link Locale} and/or perform messages
 * localization using the following strategy:
 * <ul>
 * <li>If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
 * localization.</li>
 * <li>Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context} resource and
 * it is localized.</li>
 * </ul>
 *
 * @since 5.2.0
 * 
 * @see I18NProvider
 * @see LocalizationContext
 */
public interface LocalizationProvider extends Serializable {

	/**
	 * Get the current {@link Locale}, if available.
	 * @return Optional current {@link Locale}
	 */
	Optional<Locale> getLocale();

	/**
	 * Get the message localization for given <code>locale</code>, using the provided {@link Localizable} to obtain the
	 * message localization key ({@link Localizable#getMessageCode()}) and the optional localization arguments.
	 * @param locale The {@link Locale} for which to obtain the message localization (not null)
	 * @param localizable The {@link Localizable} which represents the message to localize (not null)
	 * @return The localized message, if available. If the given <code>localizable</code> provides a default message
	 *         ({@link Localizable#getMessage()}) and a message localization is not available, the default message is
	 *         returned
	 */
	Optional<String> getMessage(Locale locale, Localizable localizable);

	/**
	 * Get the message localization for given <code>locale</code>, using the provided <code>messageCode</code> as
	 * message localization key and the optional localization arguments.
	 * @param locale The {@link Locale} for which to obtain the message localization (not null)
	 * @param messageCode The message localization key (not null)
	 * @param arguments Optional message localization arguments
	 * @return The localized message, if available
	 */
	default Optional<String> getMessage(Locale locale, String messageCode, Object... arguments) {
		return getMessage(locale, Localizable.builder().messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Get the message localization for given <code>locale</code>, using the provided <code>messageCode</code> as
	 * message localization key and the optional localization arguments.
	 * @param locale The {@link Locale} for which to obtain the message localization (not null)
	 * @param defaultMessage The default message to use when a message localization is not available for the provided
	 *        {@link Locale} and message code
	 * @param messageCode The message localization key (not null)
	 * @param arguments Optional message localization arguments
	 * @return The localized message, or the <code>defaultMessage</code> if not available
	 */
	default String getMessage(Locale locale, String defaultMessage, String messageCode, Object... arguments) {
		return getMessage(locale, Localizable.builder().message(defaultMessage).messageCode(messageCode)
				.messageArguments(arguments).build()).orElse(defaultMessage);
	}

	// ------- builders

	/**
	 * Create a new {@link LocalizationProvider} using given {@link I18NProvider}.
	 * @param i18nProvider The {@link I18NProvider} to use for messages localization (not null)
	 * @return A new {@link LocalizationProvider} using given {@link I18NProvider}
	 */
	static LocalizationProvider create(I18NProvider i18nProvider) {
		return new DefaultLocalizationProvider(i18nProvider);
	}

	/**
	 * Create a new {@link LocalizationProvider} using given {@link LocalizationContext}.
	 * @param localizationContext The {@link LocalizationContext} to use for messages localization (not null)
	 * @return A new {@link LocalizationProvider} using given {@link LocalizationContext}
	 */
	static LocalizationProvider create(LocalizationContext localizationContext) {
		return new DefaultLocalizationProvider(localizationContext);
	}

	// ------- static

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
	static Optional<Locale> getCurrentLocale() {
		return DefaultLocalizationProvider.getCurrentLocale();
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
	static Optional<String> getLocalization(Locale locale, Localizable localizable) {
		return DefaultLocalizationProvider.getLocalization(locale, localizable);
	}

	/**
	 * Get the message localization for given <code>locale</code>, using the provided <code>messageCode</code> as
	 * message localization key and the optional localization arguments.
	 * <p>
	 * If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
	 * localization. Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context}
	 * resource and it is localized.
	 * </p>
	 * @param locale The {@link Locale} for which to obtain the message localization (not null)
	 * @param messageCode The message localization key (not null)
	 * @param arguments Optional message localization arguments
	 * @return The localized message, if available
	 * @see LocalizationContext#getCurrent()
	 */
	static Optional<String> getLocalization(Locale locale, String messageCode, Object... arguments) {
		ObjectUtils.argumentNotNull(messageCode, "Message code must be not null");
		return getLocalization(locale,
				Localizable.builder().messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Get the message localization for given <code>locale</code>, using the provided <code>messageCode</code> as
	 * message localization key and the optional localization arguments.
	 * <p>
	 * If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
	 * localization. Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context}
	 * resource and it is localized.
	 * </p>
	 * @param locale The {@link Locale} for which to obtain the message localization (not null)
	 * @param defaultMessage The default message to use when a message localization is not available for the provided
	 *        {@link Locale} and message code
	 * @param messageCode The message localization key (not null)
	 * @param arguments Optional message localization arguments
	 * @return The localized message, or the <code>defaultMessage</code> if not available
	 * @see LocalizationContext#getCurrent()
	 */
	static String getLocalization(Locale locale, String defaultMessage, String messageCode, Object... arguments) {
		return getLocalization(locale, Localizable.builder().message(defaultMessage).messageCode(messageCode)
				.messageArguments(arguments).build()).orElse(defaultMessage);
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
	 * The message localization will be performed only if a current {@link Locale} is available.
	 * </p>
	 * @param localizable The {@link Localizable} which represents the message to localize (not null)
	 * @return The localized message, if available. If the given <code>localizable</code> provides a default message
	 *         ({@link Localizable#getMessage()}) and a message localization is not available, the default message is
	 *         returned
	 * @see #getCurrentLocale()
	 */
	static Optional<String> localize(Localizable localizable) {
		return DefaultLocalizationProvider.localize(localizable);
	}

	/**
	 * Get the message localization for the current {@link Locale}, using the provided <code>messageCode</code> as
	 * message localization key and the optional localization arguments.
	 * <p>
	 * If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
	 * localization. Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context}
	 * resource and it is localized.
	 * </p>
	 * <p>
	 * The message localization will be performed only if a current {@link Locale} is available.
	 * </p>
	 * @param messageCode The message localization key (not null)
	 * @param arguments Optional message localization arguments
	 * @return The localized message, if available
	 * @see #getCurrentLocale()
	 */
	static Optional<String> localize(String messageCode, Object... arguments) {
		ObjectUtils.argumentNotNull(messageCode, "Message code must be not null");
		return localize(Localizable.builder().messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Get the message localization for the current {@link Locale}, using the provided <code>messageCode</code> as
	 * message localization key and the optional localization arguments.
	 * <p>
	 * If a {@link I18NProvider} is available from the current {@link VaadinService}, it is used for message
	 * localization. Otherwise, the current {@link LocalizationContext} is used, if it is available as a {@link Context}
	 * resource and it is localized.
	 * </p>
	 * <p>
	 * The message localization will be performed only if a current {@link Locale} is available.
	 * </p>
	 * @param defaultMessage The default message to use when a message localization is not available for the provided
	 *        {@link Locale} and message code
	 * @param messageCode The message localization key (not null)
	 * @param arguments Optional message localization arguments
	 * @return The localized message, or the <code>defaultMessage</code> if not available
	 * @see #getCurrentLocale()
	 */
	static String localize(String defaultMessage, String messageCode, Object... arguments) {
		return localize(Localizable.builder().message(defaultMessage).messageCode(messageCode)
				.messageArguments(arguments).build()).orElse(defaultMessage);
	}

}
