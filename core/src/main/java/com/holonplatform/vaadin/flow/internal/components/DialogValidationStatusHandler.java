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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.List;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.ConfirmDialogBuilder;
import com.vaadin.flow.component.html.Div;

/**
 * A {@link ValidationStatusHandler} which opens a dialog when validation fails.
 *
 * @param <S> Source type
 * 
 * @since 5.2.0
 */
public class DialogValidationStatusHandler<S> implements ValidationStatusHandler<S> {

	private static final long serialVersionUID = 4676321742147773517L;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.ValidationStatusHandler#validationStatusChange(com.holonplatform.vaadin.
	 * flow.components.ValidationStatusHandler.ValidationStatusEvent)
	 */
	@Override
	public void validationStatusChange(ValidationStatusEvent<S> statusChangeEvent) {
		if (statusChangeEvent.isInvalid()) {

			final ConfirmDialogBuilder builder = Components.dialog.confirm()
					.styleName("dialog-validation-status-error");

			final List<String> messages = statusChangeEvent.getErrorMessages();
			if (messages.isEmpty()) {
				builder.text("Validation failed");
			} else {
				builder.text(messages.get(0));
				if (messages.size() > 1) {
					for (int i = 1; i < messages.size(); i++) {
						final String text = messages.get(i);
						final Div message = new Div();
						message.addClassName("message");
						message.setText((text != null) ? text : "");
						builder.withComponent(message);
					}
				}
			}

			builder.open();
		}
	}

}
