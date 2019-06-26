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
package com.holonplatform.vaadin.flow.internal.components.events;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeEvent;

/**
 * Default {@link ReadonlyChangeEvent} implementation.
 *
 * @since 5.2.12
 */
public class DefaultReadonlyChangeEvent implements ReadonlyChangeEvent {

	private static final long serialVersionUID = -6549504107493604726L;
	
	private final Input<?> source;
	private final boolean readOnly;
	
	public DefaultReadonlyChangeEvent(Input<?> source, boolean readOnly) {
		super();
		this.source = source;
		this.readOnly = readOnly;
	}

	@Override
	public Input<?> getSource() {
		return source;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

}
