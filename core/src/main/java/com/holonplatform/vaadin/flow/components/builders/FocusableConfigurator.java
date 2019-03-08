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

import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;

/**
 * Configurator for {@link Focusable} type components.
 * 
 * @param <T> Concrete component type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface FocusableConfigurator<T extends Component, C extends FocusableConfigurator<T, C>> {

	/**
	 * Sets the <code>tabindex</code> attribute in the component. The tabIndex indicates if its element can be focused,
	 * and if/where it participates in sequential keyboard navigation:
	 * <ul>
	 * <li>A negative value (usually <code>tabindex = -1</code> means that the component should be focusable, but should
	 * not be reachable via sequential keyboard navigation.</li>
	 *
	 * <li><code>tabindex = 0</code> means that the component should be focusable in sequential keyboard navigation, but
	 * its order is defined by the document's source order.</li>
	 *
	 * <li>A positive value means the component should be focusable in sequential keyboard navigation, with its order
	 * defined by the value of the number. That is, <code>tabindex = 4</code> would be focused before
	 * <code>tabindex = 5</code>, but after <code>tabindex = 3</code>. If multiple components share the same positive
	 * tabindex value, their order relative to each other follows their position in the document source.</li>
	 * </ul>
	 * @param tabIndex The tab index to set
	 * @return this
	 */
	C tabIndex(int tabIndex);

	/**
	 * Adds a listener which gets fired when the component receives focus.
	 * @param listener The listener to add
	 * @return this
	 */
	C withFocusListener(ComponentEventListener<FocusEvent<T>> listener);

	/**
	 * Adds a listener which gets fired when the the component focus is lost.
	 * @param listener The listener to add
	 * @return this
	 */
	C withBlurListener(ComponentEventListener<BlurEvent<T>> listener);

	/**
	 * Adds a shortcut which focuses the component when the provided key is pressed.
	 * @param key Primary {@link Key} used to trigger the shortcut (not null)
	 * @param keyModifiers Optional key modifier(s) that need to be pressed along with the <code>key</code> for the
	 *        shortcut to trigger
	 * @return this
	 * @since 5.2.3
	 * @see #withFocusShortcut(Key)
	 */
	default C withFocusShortcutKey(Key key, KeyModifier... keyModifiers) {
		return withFocusShortcut(key).modifiers(keyModifiers).add();
	}

	/**
	 * Adds a shortcut which focuses the component when the provided key is pressed.
	 * <p>
	 * A {@link ShortcutConfigurator} API is returned to further configure the shortcut listener. The
	 * {@link ShortcutConfigurator#add()} method should be used to add the shortcut listener and go back to the parent
	 * builder.
	 * </p>
	 * @param key Primary {@link Key} used to trigger the shortcut (not null)
	 * @return A {@link ShortcutConfigurator}
	 * @since 5.2.3
	 */
	ShortcutConfigurator<C> withFocusShortcut(Key key);

}
