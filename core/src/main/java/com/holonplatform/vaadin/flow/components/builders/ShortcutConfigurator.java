/*
 * Copyright 2016-2019 Axioma srl.
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

import com.holonplatform.vaadin.flow.internal.components.builders.DelegatedShortcutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.KeyModifier;

/**
 * Configurator for component shortcut registration.
 * 
 * @param <P> Parent configurator
 *
 * @since 5.2.3
 */
public interface ShortcutConfigurator<P> {

	/**
	 * Configures {@link KeyModifier KeyModifiers} for the shortcut.
	 * <p>
	 * Calling this method will overwrite any previously set modifier keys.
	 * </p>
	 * @param keyModifiers The key modifiers to set (can be empty)
	 * @return this
	 */
	ShortcutConfigurator<P> modifiers(KeyModifier... keyModifiers);

	/**
	 * Adds {@link KeyModifier#ALT} to the shortcut's modifiers.
	 * @return this
	 */
	ShortcutConfigurator<P> withAlt();

	/**
	 * Adds {@link KeyModifier#CONTROL} to the shortcut's modifiers.
	 * @return this
	 */
	ShortcutConfigurator<P> withCtrl();

	/**
	 * Adds {@link KeyModifier#SHIFT} to the shortcut's modifiers.
	 * @return this
	 */
	ShortcutConfigurator<P> withShift();

	/**
	 * Adds {@link KeyModifier#META} to the shortcut's modifiers.
	 * @return this
	 */
	ShortcutConfigurator<P> withMeta();

	/**
	 * Allows the default keyboard event handling when the shortcut is invoked.
	 * @return this
	 */
	ShortcutConfigurator<P> allowBrowserDefault();

	/**
	 * Allow the event to propagate upwards in the DOM tree, when the shortcut is invoked.
	 * @return this
	 */
	ShortcutConfigurator<P> allowEventPropagation();

	/**
	 * Binds the shortcut's life cycle to that of the given {@link Component}. When the given <code>component</code> is
	 * attached, the shortcut's listener is attached to the Component that owns the shortcut. When the given
	 * <code>component</code> is detached, so is the listener.
	 * @param component New lifecycle owner of the shortcut
	 * @return this
	 */
	ShortcutConfigurator<P> bindLifecycleTo(Component component);

	/**
	 * Define the {@link Component} onto which the shortcut's listener is bound. Calling this method will remove the
	 * previous listener from the component it was bound to.
	 * @param listenOnComponent Component onto which the shortcut listener is bound.
	 * @return this
	 */
	ShortcutConfigurator<P> listenOn(Component listenOnComponent);

	/**
	 * Add the configured shortcut listener to the component.
	 * @return The parent configurator/builder
	 */
	P add();

	/**
	 * Create a {@link ShortcutConfigurator} which delegates actual configuration to another one.
	 * @param <P> Parent configurator type
	 * @param delegate Delegate configurator (not null)
	 * @param parent Parent configurator (not null)
	 * @return A new delegated {@link ShortcutConfigurator}
	 */
	static <P> ShortcutConfigurator<P> delegated(ShortcutConfigurator<?> delegate, P parent) {
		return new DelegatedShortcutConfigurator<>(delegate, parent);
	}

}
