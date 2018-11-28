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
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;

/**
 * Default {@link ValidationStatus} implementation.
 *
 * @since 5.2.0
 */
public class DefaultValidationStatus implements ValidationStatus {

	private final Status status;
	private final List<ValidationException> validationExceptions;

	public DefaultValidationStatus(Status status, List<ValidationException> validationExceptions) {
		super();
		ObjectUtils.argumentNotNull(status, "Status must be not null");
		ObjectUtils.argumentNotNull(validationExceptions, "Validation exceptions must be not null");
		this.status = status;
		this.validationExceptions = validationExceptions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.PropertyValidationStatus#getStatus()
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.PropertyValidationStatus#getValidationExceptions()
	 */
	@Override
	public List<ValidationException> getValidationExceptions() {
		return validationExceptions;
	}

}
