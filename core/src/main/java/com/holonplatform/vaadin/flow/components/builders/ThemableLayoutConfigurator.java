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

import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;

/**
 * Configurator for {@link ThemableLayout} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ThemableLayoutConfigurator<C extends ThemableLayoutConfigurator<C>> extends HasElementConfigurator<C> {

	/**
	 * Enable or disable layout margins, i.e. toggles <code>margin</code> theme setting for the element. If a theme
	 * supports this attribute, it will apply or remove margin to the element.
	 * @param margin adds <code>margin</code> theme setting if <code>true</code> or removes it if <code>false</code>
	 * @return this
	 */
	C margin(boolean margin);

	/**
	 * Enable layout margins, i.e. toggles {@code margin} theme setting for the element. If a theme supports this
	 * attribute, it will apply margin to the element.
	 * @return this
	 */
	default C margin() {
		return margin(true);
	}

	/**
	 * Disable layout margins, i.e. toggles {@code margin} theme setting for the element. If a theme supports this
	 * attribute, it will remove margin to the element.
	 * @return this
	 */
	default C withoutMargin() {
		return margin(false);
	}

	/**
	 * Toggles <code>padding</code> theme setting for the element. If a theme supports this attribute, it will apply or
	 * remove padding to the element.
	 * @param padding adds <code>padding</code> theme setting if <code>true</code> or removes it if <code>false</code>
	 * @return this
	 */
	C padding(boolean padding);

	/**
	 * Add the <code>padding</code> theme setting for the element. If a theme supports this attribute, it will apply
	 * padding to the element.
	 * @return this
	 */
	default C padding() {
		return padding(true);
	}

	/**
	 * Remove the <code>padding</code> theme setting for the element. If a theme supports this attribute, it will remove
	 * padding to the element.
	 * @return this
	 */
	default C withoutPadding() {
		return padding(false);
	}

	/**
	 * Toggles <code>spacing</code> theme setting for the element. If a theme supports this attribute, it will apply or
	 * remove spacing to the element.
	 * <p>
	 * This method adds medium spacing to the component theme, to set other options, use {@link #withThemeName(String)}.
	 * List of options possible:
	 * <ul>
	 * <li>spacing-xs
	 * <li>spacing-s
	 * <li>spacing
	 * <li>spacing-l
	 * <li>spacing-xl
	 * </ul>
	 * @param spacing adds <code>spacing</code> theme setting if <code>true</code> or removes it if <code>false</code>
	 * @return this
	 */
	C spacing(boolean spacing);

	/**
	 * Apply <code>spacing</code> theme setting for the element. If a theme supports this attribute, it will apply
	 * spacing to the element.
	 * @return this
	 * @see #spacing(boolean)
	 */
	default C spacing() {
		return spacing(true);
	}

	/**
	 * Remove <code>spacing</code> theme setting for the element. If a theme supports this attribute, it will remove
	 * spacing to the element.
	 * @return this
	 * @see #spacing(boolean)
	 */
	default C withoutSpacing() {
		return spacing(false);
	}

	/**
	 * Sets the {@code box-sizing} CSS property of the layout.
	 * @param boxSizing the box-sizing of the layout. <code>null</code> is interpreted as {@link BoxSizing#UNDEFINED}
	 * @return this
	 */
	C boxSizing(BoxSizing boxSizing);

}
