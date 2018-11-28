/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.HasValidation;

/**
 * Default {@link ValidationStatusHandler} implementation using {@link HasValidation#setErrorMessage(String)} to notify
 * the validation status when supported by the component.
 * 
 * @param <S> Validation source
 * 
 * @since 5.2.0
 */
public class DefaultHasComponentValidationStatusHandler<S extends HasComponent> implements ValidationStatusHandler<S> {

	private static final long serialVersionUID = 7495988011545023652L;

	private final static Logger LOGGER = VaadinLogger.create();

	private final ValidationStatusHandler<S> fallback;

	/**
	 * Default constructor.
	 */
	public DefaultHasComponentValidationStatusHandler() {
		this(null);
	}

	/**
	 * Constructor with fallback {@link ValidationStatusHandler}.
	 * @param fallback The ValidationStatusHandler to use when the component has not validation support
	 */
	public DefaultHasComponentValidationStatusHandler(ValidationStatusHandler<S> fallback) {
		super();
		this.fallback = fallback;
	}

	@Override
	public void validationStatusChange(final ValidationStatusEvent<S> statusChangeEvent) {
		if (statusChangeEvent.isInvalid()) {
			if (!statusChangeEvent.getSource().hasValidation().isPresent()) {
				if (fallback != null) {
					fallback.validationStatusChange(statusChangeEvent);
				} else {
					LOGGER.warn("Cannot notify validation error [" + statusChangeEvent.getErrorMessage()
							+ "] on component [" + statusChangeEvent.getSource()
							+ "]: the component does not implement com.vaadin.flow.component.HasValidation. "
							+ "Provide a suitable ValidationStatusHandler to handle the validation error notification.");
				}
			} else {
				statusChangeEvent.getSource().hasValidation().ifPresent(v -> {
					v.setInvalid(true);
					v.setErrorMessage(statusChangeEvent.getErrorMessage());
				});
			}
		} else {
			statusChangeEvent.getSource().hasValidation().ifPresent(v -> {
				v.setInvalid(false);
				v.setErrorMessage(null);
			});
		}
	}

}
