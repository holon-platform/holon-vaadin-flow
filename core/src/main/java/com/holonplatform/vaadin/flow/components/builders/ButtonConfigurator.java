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
