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

import java.util.List;

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;

/**
 * Default {@link InputValidationStatus} implementation.
 *
 * @since 5.2.0
 */
public class DefaultInputValidationStatus extends DefaultValidationStatus implements InputValidationStatus {

	private final Input<?> input;

	public DefaultInputValidationStatus(Input<?> input, Status status, List<ValidationException> validationExceptions) {
		super(status, validationExceptions);
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		this.input = input;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.InputValidationStatus#getInput()
	 */
	@Override
	public Input<?> getInput() {
		return input;
	}

}
