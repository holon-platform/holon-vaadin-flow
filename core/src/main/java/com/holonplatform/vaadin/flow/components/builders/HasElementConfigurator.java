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

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.dom.DomEventListener;

/**
 * Configurator for {@link HasElement} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasElementConfigurator<C extends HasElementConfigurator<C>> {

	/**
	 * Adds an event listener for the given event type.
	 * <p>
	 * Event listeners are triggered in the order they are registered.
	 * </p>
	 * @param eventType the type of event to listen to, not <code>null</code>
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withEventListener(String eventType, DomEventListener listener);

	/**
	 * Adds an event listener for the given event type and configure a filter.
	 * <p>
	 * A filter is JavaScript expression that is used for filtering events to this listener. When an event is fired in
	 * the browser, the expression is evaluated and an event is sent to the server only if the expression value is
	 * <code>true</code>-ish according to JavaScript type coercion rules. The expression is evaluated in a context where
	 * <code>element</code> refers to this element and <code>event</code> refers to the fired event. An expression might
	 * be e.g. <code>event.button === 0</code> to only forward events triggered by the primary mouse button.
	 * </p>
	 * <p>
	 * Event listeners are triggered in the order they are registered.
	 * </p>
	 * @param eventType the type of event to listen to, not <code>null</code>
	 * @param listener the listener to add, not <code>null</code>
	 * @param filter the JavaScript filter expression
	 * @return this
	 */
	C withEventListener(String eventType, DomEventListener listener, String filter);

}
