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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultButtonConfigurator;
import com.vaadin.flow.component.button.Button;

/**
 * {@link Button} component configurator.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ButtonConfigurator<C extends ButtonConfigurator<C>>
		extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasIconConfigurator<C>,
		HasTextConfigurator<C>, HasEnabledConfigurator<C>, HasTitleConfigurator<C>,
		ClickNotifierConfigurator<Button, C>, FocusableConfigurator<Button, C>, DeferrableLocalizationConfigurator<C> {

	// TODO APICHG removed
	// B iconAlternateText(String iconAltText);

	/**
	 * Sets whether this button's icon should be positioned after it's text content or the other way around.
	 * @param iconAfterText Whether the icon should be positioned after the button text
	 * @return this
	 */
	C iconAfterText(boolean iconAfterText);

	/**
	 * Set the button to be input focused when the page loads.
	 * @param autofocus Whether the button to be focused when the page loads
	 * @return this
	 */
	C autofocus(boolean autofocus);

	/**
	 * Set the button to be input focused when the page loads.
	 * @return this
	 */
	default C autofocus() {
		return autofocus(true);
	}

	/**
	 * Automatically disables button when clicked, typically to prevent (accidental) extra clicks on a button.
	 * <p>
	 * Note that this is only used when the click comes from the user, not when calling {@link Button#click()} method
	 * programmatically. Also, if developer wants to re-enable the button, it needs to be done programmatically.
	 * </p>
	 * @return this
	 */
	// TODO re-enable when available in Vaadin
	// B disableOnClick();

	/**
	 * Makes it possible to invoke a click on this button by pressing the given {@link KeyCode} and (optional)
	 * modifiers. The shortcut is global.
	 * @param keyCode the keycode for invoking the shortcut
	 * @param modifiers the (optional) modifiers for invoking the shortcut, null for none
	 * @return this
	 */
	// TODO: APICHG: removed
	// B clickShortcut(int keyCode, int... modifiers);

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
