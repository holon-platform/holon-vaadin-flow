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
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueComponent;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;

/**
 * Default {@link ValidationStatusHandler} implementation using {@link HasValidation#setErrorMessage(String)} to notify
 * the validation status when supported by the component.
 * 
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class DefaultValidationStatusHandler<T> implements ValidationStatusHandler<T> {

	private final static Logger LOGGER = VaadinLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler#validationStatusChange(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler.ValidationStatusEvent)
	 */
	@Override
	public void validationStatusChange(final ValidationStatusEvent<T> statusChangeEvent) {
		statusChangeEvent.getSource().ifPresent(c -> {
			setComponentError(c, statusChangeEvent.isInvalid() ? statusChangeEvent.getErrorMessage() : null);
		});
	}

	private static void setComponentError(ValueComponent<?> valueComponent, String error) {
		final Component c = valueComponent.getComponent();
		if (c instanceof HasValidation) {
			((HasValidation) c).setErrorMessage(error);
		} else {
			LOGGER.warn("Cannot notify validation error [" + error + "] on component [" + c
					+ "]: the component does not implement com.vaadin.flow.component.HasValidation. "
					+ "Provide a suitable ValidationStatusHandler to handle the validation error notification.");
		}
	}

}
