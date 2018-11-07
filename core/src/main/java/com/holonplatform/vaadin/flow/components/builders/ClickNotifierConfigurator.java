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

}
