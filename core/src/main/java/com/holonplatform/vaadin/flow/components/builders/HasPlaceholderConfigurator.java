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
 * Configurator for components which support a placeholder.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasPlaceholderConfigurator<C extends HasPlaceholderConfigurator<C>> {

	/**
	 * Sets the placeholder text using a {@link Localizable} message. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and available as a {@link Context} resource.
	 * <p>
	 * The placeholder is a hint to the user of what can be entered in the control.
	 * </p>
	 * @param placeholder Localizable placeholder text (may be null)
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	C placeholder(Localizable placeholder);

	/**
	 * Set the component placeholder, i.e. a hint to the user of what can be entered in the control.
	 * @param placeholder the value to set
	 * @return this
	 */
	default C placeholder(String placeholder) {
		return placeholder((placeholder == null) ? null : Localizable.builder().message(placeholder).build());
	}

	/**
	 * Sets the placeholder text using a localizable <code>messageCode</code>. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and available as a {@link Context} resource.
	 * <p>
	 * The placeholder is a hint to the user of what can be entered in the control.
	 * </p>
	 * @param defaultPlaceholder Default placeholder text if no translation is available for given
	 *        <code>messageCode</code>.
	 * @param messageCode Placeholder text translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	default C placeholder(String defaultPlaceholder, String messageCode, Object... arguments) {
		return placeholder(Localizable.builder().message((defaultPlaceholder == null) ? "" : defaultPlaceholder)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

}
