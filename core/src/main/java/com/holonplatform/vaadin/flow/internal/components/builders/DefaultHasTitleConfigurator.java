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
import com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.HasElement;

/**
 * Default {@link HasTitleConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasTitleConfigurator<C extends HasElement> extends AbstractLocalizationSupportConfigurator<C>
		implements HasTitleConfigurator<DefaultHasTitleConfigurator<C>> {

	private static final Logger LOGGER = VaadinLogger.create();

	private final C component;

	/**
	 * Constructor.
	 * @param component The component to configure (not null)
	 * @param setTitleOperation Actual operation to set the title (not null)
	 */
	public DefaultHasTitleConfigurator(C component, Consumer<String> setTitleOperation) {
		this(component, setTitleOperation, null);
	}

	/**
	 * Constructor.
	 * @param component The component to configure (not null)
	 * @param setTitleOperation Actual operation to set the title (not null)
	 * @param deferrableLocalization Optional {@link HasDeferrableLocalization} reference
	 */
	public DefaultHasTitleConfigurator(C component, Consumer<String> setTitleOperation,
			HasDeferrableLocalization deferrableLocalization) {
		super(text -> setTitleOperation.accept(text), deferrableLocalization);
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public DefaultHasTitleConfigurator<C> title(Localizable title) {
		if (localize(component, title)) {
			LOGGER.debug(() -> "Component [" + component + "] title localization was deferred");
		}
		return this;
	}

}
