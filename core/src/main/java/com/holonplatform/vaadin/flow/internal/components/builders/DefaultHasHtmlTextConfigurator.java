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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization;
import com.holonplatform.vaadin.flow.components.builders.HasHtmlTextConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.HasElement;

/**
 * Default {@link HasHtmlTextConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasHtmlTextConfigurator extends AbstractLocalizationSupportConfigurator<HasElement>
		implements HasHtmlTextConfigurator<DefaultHasHtmlTextConfigurator> {

	private static final Logger LOGGER = VaadinLogger.create();

	private static final String INNER_HTML_PROPERTY = "innerHTML";

	private final HasElement component;

	/**
	 * Constructor.
	 * @param component Component to configure (not null)
	 */
	public DefaultHasHtmlTextConfigurator(HasElement component) {
		this(component, null);
	}

	/**
	 * Constructor.
	 * @param component Component to configure (not null)
	 * @param deferrableLocalization Optional {@link HasDeferrableLocalization} reference
	 */
	public DefaultHasHtmlTextConfigurator(HasElement component, HasDeferrableLocalization deferrableLocalization) {
		super(text -> component.getElement().setProperty(INNER_HTML_PROPERTY, text), deferrableLocalization);
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasHtmlTextConfigurator#htmlText(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public DefaultHasHtmlTextConfigurator htmlText(Localizable text) {
		if (localize(component, text)) {
			LOGGER.debug(() -> "Component [" + component + "] html text localization was deferred");
		}
		return this;
	}

}
