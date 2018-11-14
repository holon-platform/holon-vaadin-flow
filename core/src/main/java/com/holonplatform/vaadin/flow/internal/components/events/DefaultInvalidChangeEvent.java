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
package com.holonplatform.vaadin.flow.internal.components.events;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEvent;
import com.vaadin.flow.component.HasValidation;

/**
 * Default {@link InvalidChangeEvent} implementation.
 *
 * @since 5.2.0
 */
public class DefaultInvalidChangeEvent implements InvalidChangeEvent {

	private static final long serialVersionUID = -1331658541535165306L;

	private final boolean fromClient;
	private final HasValidation hasValidation;

	public DefaultInvalidChangeEvent(boolean fromClient, HasValidation hasValidation) {
		super();
		ObjectUtils.argumentNotNull(hasValidation, "HasValidation must be not null");
		this.fromClient = fromClient;
		this.hasValidation = hasValidation;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.InvalidChangeEvent#isFromClient()
	 */
	@Override
	public boolean isFromClient() {
		return fromClient;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.InvalidChangeEvent#isInvalid()
	 */
	@Override
	public boolean isInvalid() {
		return hasValidation.isInvalid();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.InvalidChangeEvent#getErrorMessage()
	 */
	@Override
	public Optional<String> getErrorMessage() {
		if (hasValidation.getErrorMessage() != null && hasValidation.getErrorMessage().trim().equals("")) {
			return Optional.empty();
		}
		return Optional.ofNullable(hasValidation.getErrorMessage());
	}

}
