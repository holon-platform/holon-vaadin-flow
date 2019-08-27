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

import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.events.CustomValueSetEvent;

/**
 * Default {@link CustomValueSetEvent} implementation.
 *
 * @param <T> Select value type
 */
public class DefaultCustomValueSetEvent<T> implements CustomValueSetEvent<T> {

	private static final long serialVersionUID = 8626765674836153959L;
	
	private final SingleSelect<T> source;
	private final boolean fromClient;
	private final String customValue;

	public DefaultCustomValueSetEvent(SingleSelect<T> source, boolean fromClient, String customValue) {
		super();
		this.source = source;
		this.fromClient = fromClient;
		this.customValue = customValue;
	}

	@Override
	public SingleSelect<T> getSource() {
		return source;
	}

	@Override
	public boolean isFromClient() {
		return fromClient;
	}

	@Override
	public String getCustomValue() {
		return customValue;
	}

}
