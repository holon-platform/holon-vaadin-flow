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
package com.holonplatform.vaadin.flow.components.events;

import java.io.Serializable;
import java.util.EventListener;

import com.holonplatform.vaadin.flow.components.SingleSelect;

/**
 * Listener for {@link SingleSelect} custom value set events.
 *
 * @param <T> Select value type
 */
@FunctionalInterface
public interface CustomValueSetListener<T> extends EventListener, Serializable {

	/**
	 * Invoked when a custom value set event has been fired.
	 * @param event The custom value set event
	 */
	void onCustomValueSetEvent(CustomValueSetEvent<T> event);

}
