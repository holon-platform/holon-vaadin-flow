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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.function.Consumer;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.shared.Registration;

/**
 * Base configurator with component localization support.
 *
 * @param <C> Component type
 *
 * @since 5.2.0
 */
public abstract class AbstractLocalizationSupportConfigurator<C extends HasElement> {

	private final HasDeferrableLocalization deferrableLocalization;

	private final Consumer<String> operation;

	private Registration localizatorOnAttachRegistration;

	public AbstractLocalizationSupportConfigurator(Consumer<String> operation,
			HasDeferrableLocalization deferrableLocalization) {
		super();
		ObjectUtils.argumentNotNull(operation, "Localization operation must be not null");
		this.operation = operation;
		this.deferrableLocalization = deferrableLocalization;
	}

	/**
	 * Localize given component using given localization.
	 * @param component Component to localize
	 * @param localization Localization to use (may be null)
	 * @return <code>true</code> if the localization was set up in deferred mode
	 */
	protected boolean localize(final C component, final Localizable localization) {
		// unregister deferred localization
		if (localizatorOnAttachRegistration != null) {
			localizatorOnAttachRegistration.remove();
			localizatorOnAttachRegistration = null;
		}
		if (localization == null) {
			operation.accept(null);
		} else {
			// check deferrable localization
			if (deferrableLocalization != null && deferrableLocalization.isDeferredLocalizationEnabled()
					&& component instanceof AttachNotifier) {
				// set default message code
				operation.accept(localization.getMessage());
				// register attach listener
				localizatorOnAttachRegistration = ((AttachNotifier) component).addAttachListener(event -> {
					if (event.isInitialAttach()) {
						LocalizationProvider.localize(localization).ifPresent(m -> operation.accept(m));
					}
				});
				return true;
			} else {
				LocalizationProvider.localize(localization).ifPresent(m -> operation.accept(m));
			}
		}
		return false;
	}

}
