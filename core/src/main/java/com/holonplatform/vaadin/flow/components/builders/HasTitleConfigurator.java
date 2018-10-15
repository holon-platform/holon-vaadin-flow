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

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;

/**
 * Configurator for components which supports a title.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasTitleConfigurator<C extends HasTitleConfigurator<C>> {

	/**
	 * Sets the title text using a {@link Localizable} message. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource.
	 * <p>
	 * Browsers typically use the title to show a tooltip when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * A <code>null</code> value will remove the title.
	 * </p>
	 * @param text Localizable title message (may be null)
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	C title(Localizable title);

	/**
	 * Sets the title text.
	 * <p>
	 * Browsers typically use the title to show a tooltip when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * A <code>null</code> value will remove the title.
	 * </p>
	 * @param title The title to set (may be null)
	 * @return this
	 */
	default C title(String title) {
		return title((title == null) ? null : Localizable.builder().message(title).build());
	}

	/**
	 * Sets the title text using a localizable <code>messageCode</code>. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource.
	 * <p>
	 * Browsers typically use the title to show a tooltip when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * @param defaultTitle Default title if no translation is available for given <code>messageCode</code>.
	 * @param messageCode Title translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	default C title(String defaultTitle, String messageCode, Object... arguments) {
		return title(Localizable.builder().message((defaultTitle == null) ? "" : defaultTitle).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Sets the description text using a {@link Localizable} message. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource. This is an alias for
	 * {@link #title(Localizable)}.
	 * <p>
	 * The description is set using the <code>title</code> attribute. Browsers typically use the title to show a tooltip
	 * when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * A <code>null</code> value will remove the description.
	 * </p>
	 * @param description Localizable description message (may be null)
	 * @return this
	 */
	default C description(Localizable description) {
		return title(description);
	}

	/**
	 * Sets the description text. This is an alias for {@link #title(String)}.
	 * <p>
	 * The description is set using the <code>title</code> attribute. Browsers typically use the title to show a tooltip
	 * when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * A <code>null</code> value will remove the title.
	 * </p>
	 * @param description The description to set (may be null)
	 * @return this
	 */
	default C description(String description) {
		return title(description);
	}

	/**
	 * Sets the description using a localizable <code>messageCode</code>. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource. This is an alias for
	 * {@link #title(String, String, Object...)}.
	 * <p>
	 * The description is set using the <code>title</code> attribute. Browsers typically use the title to show a tooltip
	 * when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * @param defaultDescription Default description if no translation is available for given <code>messageCode</code>.
	 * @param messageCode Description translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default C description(String defaultDescription, String messageCode, Object... arguments) {
		return title(defaultDescription, messageCode, arguments);
	}

}
