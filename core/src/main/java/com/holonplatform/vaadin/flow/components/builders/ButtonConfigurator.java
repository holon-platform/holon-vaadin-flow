/*
 * Copyright 2016-2017 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultButtonConfigurator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

/**
 * {@link Button} component configurator.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ButtonConfigurator<C extends ButtonConfigurator<C>> extends ComponentConfigurator<C>,
		HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasIconConfigurator<C>, HasTextConfigurator<C>,
		HasEnabledConfigurator<C>, HasTitleConfigurator<C>, ClickNotifierConfigurator<Button, ClickEvent<Button>, C>,
		FocusableConfigurator<Button, C>, HasAutofocusConfigurator<C>, HasThemeVariantConfigurator<ButtonVariant, C>,
		DeferrableLocalizationConfigurator<C> {

	/**
	 * Sets whether this button's icon should be positioned after it's text content or the other way around.
	 * @param iconAfterText Whether the icon should be positioned after the button text
	 * @return this
	 */
	C iconAfterText(boolean iconAfterText);

	/**
	 * Set the button to be input focused when the page loads.
	 * @return this
	 */
	default C autofocus() {
		return autofocus(true);
	}

	/**
	 * Automatically disables button when clicked, typically to prevent (accidental) extra clicks on a button.
	 * @return this
	 */
	C disableOnClick();

	/**
	 * Sets the button caption using a {@link Localizable} message. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> value is interpreted as an empty text.
	 * </p>
	 * @param text Localizable button caption message (may be null)
	 * @return this
	 * @deprecated Use {@link #text(Localizable)}
	 */
	@Deprecated
	default C caption(Localizable text) {
		return text(text);
	}

	/**
	 * Sets the button caption, replacing any previous content.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> text value is interpreted as an empty text.
	 * </p>
	 * @param text The button caption to set
	 * @return this
	 * @deprecated Use {@link #text(String)}
	 */
	@Deprecated
	default C caption(String text) {
		return text(text);
	}

	/**
	 * Sets the button caption using a localizable <code>messageCode</code>. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * @param defaultText Default button caption if no translation is available for given <code>messageCode</code>.
	 * @param messageCode Caption translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @deprecated Use {@link #text(String, String, Object...)}
	 */
	@Deprecated
	default C caption(String defaultText, String messageCode, Object... arguments) {
		return text(defaultText, messageCode, arguments);
	}

	/**
	 * Base button configurator.
	 */
	public interface BaseButtonConfigurator extends ButtonConfigurator<BaseButtonConfigurator> {

	}

	/**
	 * Obtain a {@link ButtonConfigurator} to configure given {@link Button} component.
	 * @param button The component to configure (not null)
	 * @return A {@link ButtonConfigurator} to configure given component
	 */
	static BaseButtonConfigurator configure(Button button) {
		return new DefaultButtonConfigurator(button);
	}

}
