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
package com.holonplatform.vaadin.flow.examples;

import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.i18n.MessageProvider;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationContextI18NProvider;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;

@SuppressWarnings("unused")
public class ExampleI18n {

	public void builder1() {
		// tag::builder1[]
		Button button = Components.button().text("Not localizable").build(); // <1>

		button = Components.button().text("Default message", "message.localization.code").build(); // <2>

		button = Components.button().text("Default message", "message.localization.code", "arg1", "arg2").build(); // <3>

		Localizable message = Localizable.of("Default message", "message.localization.code");
		button = Components.button().text(message).build(); // <4>
		// end::builder1[]
	}

	public void localizationProvider1() {
		// tag::localizationProvider1[]
		final Localizable message = Localizable.of("Default message", "message.localization.code");

		Optional<String> localized = LocalizationProvider.getLocalization(Locale.US, message); // <1>
		String localizedOrDefault = LocalizationProvider.getLocalization(Locale.US, "Default message",
				"message.localization.code"); // <2>
		// end::localizationProvider1[]
	}

	public void localizationProvider2() {
		// tag::localizationProvider2[]
		final Localizable message = Localizable.of("Default message", "message.localization.code");

		Optional<Locale> locale = LocalizationProvider.getCurrentLocale(); // <1>

		Optional<String> localized = LocalizationProvider.localize(message); // <2>
		String localizedOrDefault = LocalizationProvider.localize("Default message", "message.localization.code"); // <3>
		// end::localizationProvider2[]
	}

	public void i18nprovider1() {
		// tag::i18nprovider1[]
		LocalizationContext localizationContext = LocalizationContext.builder()
				.withMessageProvider(MessageProvider.fromProperties("messages").build()) // <1>
				.withInitialLocale(Locale.US) // <2>
				.build();

		I18NProvider i18nProvider = LocalizationContextI18NProvider.create(localizationContext); // <3>
		// end::i18nprovider1[]
	}

	public void i18nprovider2() {
		// tag::i18nprovider2[]
		LocalizationContext localizationContext = LocalizationContext.builder()
				.withMessageProvider(MessageProvider.fromProperties("messages").build()).withInitialLocale(Locale.US)
				.build(); // <1>

		VaadinSession.getCurrent().setAttribute(LocalizationContext.class, localizationContext); // <2>

		I18NProvider i18nProvider = LocalizationContextI18NProvider.create(); // <3>
		// end::i18nprovider2[]
	}

	// tag::enum2[]
	enum MyEnum {

		@Caption(value = "The first", messageCode = "first.message.code")
		FIRST,

		@Caption(value = "The second", messageCode = "second.message.code")
		SECOND,

		@Caption(value = "The third", messageCode = "third.message.code")
		THIRD;

	}
	// end::enum2[]

}
