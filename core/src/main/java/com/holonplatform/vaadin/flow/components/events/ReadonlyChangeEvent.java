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

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultReadonlyChangeEvent;

/**
 * {@link Input} read-only state change event.
 * 
 * @since 5.2.12
 */
public interface ReadonlyChangeEvent extends Event<Input<?>> {

	/**
	 * Returns whether this input component is in read-only mode or not.
	 * @return <code>false</code> if the user can modify the value, <code>true</code> if not
	 */
	boolean isReadOnly();

	/**
	 * Create a new {@link ReadonlyChangeEvent}.
	 * @param input The source input
	 * @param readOnly The read-only state
	 * @return A new event
	 */
	static ReadonlyChangeEvent create(Input<?> input, boolean readOnly) {
		return new DefaultReadonlyChangeEvent(input, readOnly);
	}

}
