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
 * Configurator for input component which supports a date with time.
 *
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasTimeInputConfigurator<C extends HasTimeInputConfigurator<C>> {

	/**
	 * Set the time separator text, i.e. the text to show between the <code>hours</code> and <code>minutes</code>
	 * fields.
	 * @param timeSeparator the time separator to set, if <code>null</code> no separator will be shown.
	 * @return this
	 */
	C timeSeparator(String timeSeparator);

	/**
	 * Set the time inputs (i.e. the <code>hours</code> and <code>minutes</code> fields) width.
	 * @param timeInputsWidth the time inputs width to set
	 * @return this
	 */
	C timeInputsWidth(String timeInputsWidth);

	/**
	 * Set whether to display a space between the <code>date</code>, <code>hours</code> and <code>minutes</code> fields.
	 * @param spacing whether to enable spacing
	 * @return this
	 */
	C spacing(boolean spacing);

	/**
	 * Set the <code>hours</code> input placeholder using a {@link Localizable} message. In order for the localization
	 * to work, a {@link LocalizationContext} must be valid (localized) and available as a {@link Context} resource.
	 * @param placeholder Localizable placeholder text (may be null)
	 * @return this
	 */
	C hoursPlaceholder(Localizable placeholder);

	/**
	 * Set the <code>hours</code> input placeholder, i.e. a hint to the user of what can be entered in the control.
	 * @param placeholder the value to set
	 */
	default C hoursPlaceholder(String placeholder) {
		return hoursPlaceholder((placeholder == null) ? null : Localizable.builder().message(placeholder).build());
	}

	/**
	 * Set the <code>hours</code> input placeholder using a localizable <code>messageCode</code>. In order for the
	 * localization to work, a {@link LocalizationContext} must be valid (localized) and available as a {@link Context}
	 * resource.
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
	default C hoursPlaceholder(String defaultPlaceholder, String messageCode, Object... arguments) {
		return hoursPlaceholder(Localizable.builder().message((defaultPlaceholder == null) ? "" : defaultPlaceholder)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Set the <code>minutes</code> input placeholder using a {@link Localizable} message. In order for the localization
	 * to work, a {@link LocalizationContext} must be valid (localized) and available as a {@link Context} resource.
	 * @param placeholder Localizable placeholder text (may be null)
	 * @return this
	 */
	C minutesPlaceholder(Localizable placeholder);

	/**
	 * Set the <code>minutes</code> input placeholder, i.e. a hint to the user of what can be entered in the control.
	 * @param placeholder the value to set
	 */
	default C minutesPlaceholder(String placeholder) {
		return minutesPlaceholder((placeholder == null) ? null : Localizable.builder().message(placeholder).build());
	}

	/**
	 * Set the <code>minutes</code> input placeholder using a localizable <code>messageCode</code>. In order for the
	 * localization to work, a {@link LocalizationContext} must be valid (localized) and available as a {@link Context}
	 * resource.
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
	default C minutesPlaceholder(String defaultPlaceholder, String messageCode, Object... arguments) {
		return minutesPlaceholder(Localizable.builder().message((defaultPlaceholder == null) ? "" : defaultPlaceholder)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

}
