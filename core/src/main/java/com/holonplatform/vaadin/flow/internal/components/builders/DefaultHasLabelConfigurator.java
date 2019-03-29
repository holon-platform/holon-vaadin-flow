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
import com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator.BaseHasLabelConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.HasElement;

/**
 * Default {@link HasLabelConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasLabelConfigurator<C extends HasElement> extends AbstractLocalizationSupportConfigurator<C>
		implements BaseHasLabelConfigurator {

	private static final Logger LOGGER = VaadinLogger.create();

	private final C component;

	/**
	 * Constructor.
	 * @param component The component to configure (not null)
	 * @param setLabelOperation Actual operation to set the label (not null)
	 */
	public DefaultHasLabelConfigurator(C component, Consumer<String> setLabelOperation) {
		this(component, setLabelOperation, null);
	}

	/**
	 * Constructor.
	 * @param component The component to configure (not null)
	 * @param setLabelOperation Actual operation to set the label (not null)
	 * @param deferrableLocalization Optional {@link HasDeferrableLocalization} reference
	 */
	public DefaultHasLabelConfigurator(C component, Consumer<String> setLabelOperation,
			HasDeferrableLocalization deferrableLocalization) {
		super(text -> setLabelOperation.accept(text), deferrableLocalization);
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public DefaultHasLabelConfigurator<C> label(Localizable label) {
		if (localize(component, label)) {
			LOGGER.debug(() -> "Component [" + component + "] label localization was deferred");
		}
		return this;
	}

}
