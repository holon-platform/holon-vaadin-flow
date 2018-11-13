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
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.Component;

/**
 * Generic localizable message configurator.
 *
 * @since 5.2.0
 */
public class MessageLocalizationSupportConfigurator<C extends Component>
		extends AbstractLocalizationSupportConfigurator<C> {

	private static final Logger LOGGER = VaadinLogger.create();

	private final C component;

	public MessageLocalizationSupportConfigurator(C component, Consumer<String> operation,
			HasDeferrableLocalization deferrableLocalization) {
		super(operation, deferrableLocalization);
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	public void setMessage(Localizable message) {
		if (localize(component, message)) {
			LOGGER.debug(() -> "Component [" + component + "] title localization was deferred");
		}
	}

}
