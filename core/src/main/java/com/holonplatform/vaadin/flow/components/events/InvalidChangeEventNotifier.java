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
package com.holonplatform.vaadin.flow.components.events;

import com.vaadin.flow.shared.Registration;

/**
 * {@link InvalidChangeEvent} notifier.
 * 
 * @since 5.2.0
 */
@FunctionalInterface
public interface InvalidChangeEventNotifier {

	/**
	 * Add an invalid change event listener.
	 * @param listener The listener to add (not null)
	 * @return An handler to remove the registered listener
	 */
	Registration addInvalidChangeListener(InvalidChangeEventListener listener);

}
