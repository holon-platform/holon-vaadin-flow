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
package com.holonplatform.vaadin.flow.internal.components.support;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEvent;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEventListener;

public class DefaultUserInputValidator<T> implements Validator<T>, InvalidChangeEventListener {

	private static final long serialVersionUID = 8168322550502915508L;

	private static final Localizable DEFAULT_ERROR = Localizable.of("Invalid user input",
			"com.holonplatform.vaadin.flow.validator.invalid.user.input");

	private String error;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.events.InvalidChangeEventListener#onInvalidChangeEvent(com.holonplatform
	 * .vaadin.flow.components.events.InvalidChangeEvent)
	 */
	@Override
	public void onInvalidChangeEvent(InvalidChangeEvent event) {
		if (event.isInvalid() && event.isFromClient()) {
			error = event.getErrorMessage().orElseGet(() -> LocalizationContext.translate(DEFAULT_ERROR, true));
		} else {
			error = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(T value) throws ValidationException {
		if (error != null) {
			throw new ValidationException(error);
		}
	}

}
