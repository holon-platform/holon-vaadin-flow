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

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.KeyUpEvent;

/**
 * Configurator for {@link KeyNotifier} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface KeyNotifierConfigurator<C extends KeyNotifierConfigurator<C>> {

	/**
	 * Adds a <code>keydown</code> listener to this component.
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withKeyDownListener(ComponentEventListener<KeyDownEvent> listener);

	/**
	 * Adds a <code>keypress</code> listener to this component.
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withKeyPressListener(ComponentEventListener<KeyPressEvent> listener);

	/**
	 * Adds a <code>keyup</code> listener to this component.
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withKeyUpListener(ComponentEventListener<KeyUpEvent> listener);

	/**
	 * Adds a <code>keydown</code> listener to this component, which will trigger only if the keys involved in the event
	 * match the <code>key</code> and <code>modifiers</code> parameters.
	 * <p>
	 * See {@link Key} for common static instances or use {@link Key#of(String, String...)} to get an instance from an
	 * arbitrary value.
	 * @param key the key to match
	 * @param listener the listener to add, not <code>null</code>
	 * @param modifiers the optional modifiers to match
	 * @return this
	 */
	C withKeyDownListener(Key key, ComponentEventListener<KeyDownEvent> listener, KeyModifier... modifiers);

	/**
	 * Adds a <code>keypress</code> listener to this component, which will trigger only if the keys involved in the
	 * event match the <code>key</code> and <code>modifiers</code> parameters.
	 * <p>
	 * See {@link Key} for common static instances or use {@link Key#of(String, String...)} to get an instance from an
	 * arbitrary value.
	 * @param key the key to match
	 * @param listener the listener to add, not <code>null</code>
	 * @param modifiers the optional modifiers to match
	 * @return this
	 */
	C withKeyPressListener(Key key, ComponentEventListener<KeyPressEvent> listener, KeyModifier... modifiers);

	/**
	 * Adds a <code>keyup</code> listener to this component, which will trigger only if the keys involved in the event
	 * match the <code>key</code> and <code>modifiers</code> parameters.
	 * <p>
	 * See {@link Key} for common static instances or use {@link Key#of(String, String...)} to get an instance from an
	 * arbitrary value.
	 * @param key the key to match
	 * @param listener the listener to add, not <code>null</code>
	 * @param modifiers the optional modifiers to match
	 * @return this
	 */
	C withKeyUpListener(Key key, ComponentEventListener<KeyUpEvent> listener, KeyModifier... modifiers);

}
