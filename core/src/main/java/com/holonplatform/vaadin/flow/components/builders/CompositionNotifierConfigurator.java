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
import com.vaadin.flow.component.CompositionEndEvent;
import com.vaadin.flow.component.CompositionNotifier;
import com.vaadin.flow.component.CompositionStartEvent;
import com.vaadin.flow.component.CompositionUpdateEvent;

/**
 * Configurator for {@link CompositionNotifier} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface CompositionNotifierConfigurator<C extends CompositionNotifierConfigurator<C>> {

	/**
	 * Adds a <code>compositionstart</code> listener to this component.
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener);

	/**
	 * Adds a <code>compositionupdate</code> listener to this component.
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withCompositionUpdateListener(ComponentEventListener<CompositionUpdateEvent> listener);

	/**
	 * Adds a <code>compositionend</code> listener to this component.
	 * @param listener the listener to add, not <code>null</code>
	 * @return this
	 */
	C withCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener);

}
