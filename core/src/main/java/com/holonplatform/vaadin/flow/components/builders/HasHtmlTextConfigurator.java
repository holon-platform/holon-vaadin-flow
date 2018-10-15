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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.vaadin.flow.component.HasText;

/**
 * Configurator for {@link HasText} type components which can support HTML markup.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasHtmlTextConfigurator<C extends HasHtmlTextConfigurator<C>> {

	/**
	 * Sets the text content using a {@link Localizable} message, with HTML markup support.
	 * <p>
	 * Care should be taken when using HTML text mode to avoid Cross-site Scripting (XSS) issues.
	 * </p>
	 * <p>
	 * A <code>null</code> value is interpreted as an empty text.
	 * </p>
	 * @param text Localizable text content message (may be null)
	 * @return this
	 */
	C htmlText(Localizable text);

	/**
	 * Sets the text content with HTML markup support, replacing any previous content.
	 * <p>
	 * Care should be taken when using HTML text mode to avoid Cross-site Scripting (XSS) issues.
	 * </p>
	 * <p>
	 * A <code>null</code> text value is interpreted as an empty text.
	 * </p>
	 * @param text The text content to set, with HTML markup support
	 * @return this
	 */
	default C htmlText(String text) {
		return htmlText((text == null) ? null : Localizable.builder().message(text).build());
	}

	/**
	 * Sets the text content with HTML markup support, using a localizable <code>messageCode</code>.
	 * <p>
	 * Care should be taken when using HTML text mode to avoid Cross-site Scripting (XSS) issues.
	 * </p>
	 * @param defaultText Default text content if no translation is available for given <code>messageCode</code>.
	 * @param messageCode Text translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default C htmlText(String defaultText, String messageCode, Object... arguments) {
		return htmlText(Localizable.builder().message((defaultText == null) ? "" : defaultText).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

}
