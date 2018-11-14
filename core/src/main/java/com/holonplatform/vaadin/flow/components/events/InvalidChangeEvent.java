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

import java.io.Serializable;
import java.util.Optional;

/**
 * Invalid status change event.
 *
 * @since 5.2.0
 */
public interface InvalidChangeEvent extends Serializable {

	/**
	 * Get whether the event was originated from the client.
	 * @return whether the event was originated from the client
	 */
	boolean isFromClient();

	/**
	 * Get whether the event source component is not valid.
	 * @return whether the event source component is not valid
	 */
	boolean isInvalid();

	/**
	 * Get the event source component error message, if available.
	 * @return Optional event source component error message
	 */
	Optional<String> getErrorMessage();

}
