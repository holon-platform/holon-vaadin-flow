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

import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;

/**
 * Configurator for components which support {@link ClickEventListener} registration.
 * 
 * @param <S> Event source type
 * @param <E> Click event type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ClickNotifierConfigurator<S, E extends ClickEvent<S>, C extends ClickNotifierConfigurator<S, E, C>> {

	/**
	 * Register a click event listener.
	 * @param clickEventListener The listener to add (not null)
	 * @return this
	 */
	C withClickListener(ClickEventListener<S, E> clickEventListener);

	/**
	 * Register a click event listener.
	 * <p>
	 * Alias for {@link #withClickListener(ClickEventListener)}.
	 * </p>
	 * @param clickEventListener The listener to add (not null)
	 * @return this
	 */
	default C onClick(ClickEventListener<S, E> clickEventListener) {
		return withClickListener(clickEventListener);
	}

	/**
	 * Adds a shortcut to perform a <em>click</em> action for this component when the provided key is pressed.
	 * @param key Primary {@link Key} used to trigger the shortcut (not null)
	 * @param keyModifiers Optional key modifier(s) that need to be pressed along with the <code>key</code> for the
	 *        shortcut to trigger
	 * @return this
	 * @since 5.2.3
	 * @see #withClickShortcut(Key)
	 */
	C withClickShortcutKey(Key key, KeyModifier... keyModifiers);

	/**
	 * Adds a shortcut to perform a <em>click</em> action for this component when the provided key is pressed.
	 * <p>
	 * A {@link ShortcutConfigurator} API is returned to further configure the shortcut listener. The
	 * {@link ShortcutConfigurator#add()} method should be used to add the shortcut listener and go back to the parent
	 * builder.
	 * </p>
	 * @param key Primary {@link Key} used to trigger the shortcut (not null)
	 * @return A {@link ShortcutConfigurator}
	 * @since 5.2.3
	 */
	ShortcutConfigurator<C> withClickShortcut(Key key);

}
