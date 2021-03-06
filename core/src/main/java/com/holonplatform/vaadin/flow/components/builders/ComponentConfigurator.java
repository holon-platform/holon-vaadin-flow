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

import java.util.function.Consumer;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultComponentConfigurator;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.dom.Element;

/**
 * Interface to configure a {@link Component}.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ComponentConfigurator<C extends ComponentConfigurator<C>> extends HasElementConfigurator<C> {

	/**
	 * Sets the id of the root element of this component. The id is used with various APIs to identify the element, and
	 * it should be unique on the page.
	 * @param id the id to set, or <code>null</code> to remove any previously set id
	 * @return this
	 */
	C id(String id);

	/**
	 * Sets the component visibility.
	 * <p>
	 * When a component is set as invisible, all the updates of the component from the server to the client are blocked
	 * until the component is set as visible again.
	 * </p>
	 * <p>
	 * Invisible components don't receive any updates from the client-side.
	 * </p>
	 * @param visible Whether the component is visible
	 * @return this
	 */
	C visible(boolean visible);

	/**
	 * Set the component as not visible.
	 * <p>
	 * When a component is set as invisible, all the updates of the component from the server to the client are blocked
	 * until the component is set as visible again.
	 * </p>
	 * <p>
	 * Invisible components don't receive any updates from the client-side.
	 * </p>
	 * @return this
	 * @see #visible(boolean)
	 */
	default C hidden() {
		return visible(false);
	}

	/**
	 * Configure the component {@link Element}.
	 * @param element The consumer to use to configure the component {@link Element} (not null)
	 * @return this
	 * @since 5.2.3
	 */
	C elementConfiguration(Consumer<Element> element);

	/**
	 * Add an {@link AttachEvent} {@link ComponentEventListener} to the component, called after the component is
	 * attached to the application.
	 * @param listener Listener to add
	 * @return this
	 */
	C withAttachListener(ComponentEventListener<AttachEvent> listener);

	/**
	 * Add an {@link DetachEvent} {@link ComponentEventListener} to the component, called before the component is
	 * detached from the application.
	 * @param listener Listener to add
	 * @return this
	 */
	C withDetachListener(ComponentEventListener<DetachEvent> listener);

	/**
	 * Base component configurator.
	 */
	public interface BaseComponentConfigurator extends ComponentConfigurator<BaseComponentConfigurator> {

	}

	/**
	 * Create a new {@link BaseComponentConfigurator} on given <code>component</code>.
	 * @param component Component to configure (not null)
	 * @return A new {@link BaseComponentConfigurator} to configure given component
	 */
	static BaseComponentConfigurator create(Component component) {
		return new DefaultComponentConfigurator(component);
	}

}
