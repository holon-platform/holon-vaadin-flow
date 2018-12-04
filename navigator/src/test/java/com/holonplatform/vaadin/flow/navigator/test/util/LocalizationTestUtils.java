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
package com.holonplatform.vaadin.flow.navigator.test.util;

import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.internal.components.RequiredInputValidator;

public final class LocalizationTestUtils {

	private LocalizationTestUtils() {
	}

	public static LocalizationContext getTestLocalizationContext() {
		return LocalizationContext.builder().withInitialLocale(Locale.US).messageProvider((locale, code) -> {
			if (Locale.US.equals(locale)) {
				if ("test.code".equals(code)) {
					return Optional.of("TestUS");
				}
			}
			return Optional.empty();
		}).build();
	}

	public static void withTestLocalizationContext(Runnable operation) {
		Context.get().executeThreadBound(LocalizationContext.CONTEXT_KEY, getTestLocalizationContext(), operation);
	}

	public static final String TEST_LOCALIZED_REQUIRED_ERROR = "Localized required error";

	public static LocalizationContext getInputValidationLocalizationContext() {
		return LocalizationContext.builder().withInitialLocale(Locale.US).messageProvider((locale, code) -> {
			if (Locale.US.equals(locale)) {
				if (RequiredInputValidator.DEFAULT_REQUIRED_ERROR.getMessageCode().equals(code)) {
					return Optional.of(TEST_LOCALIZED_REQUIRED_ERROR);
				}
			}
			return Optional.empty();
		}).build();
	}

	public static void withInputValidationLocalizationContext(Runnable operation) {
		Context.get().executeThreadBound(LocalizationContext.CONTEXT_KEY, getInputValidationLocalizationContext(),
				operation);
	}

}
