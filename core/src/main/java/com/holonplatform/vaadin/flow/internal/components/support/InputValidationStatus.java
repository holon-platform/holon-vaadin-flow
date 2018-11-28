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

import java.util.Collections;
import java.util.List;

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;

/**
 * Input validation status.
 *
 * @since 5.2.0
 */
public interface InputValidationStatus extends ValidationStatus {

	/**
	 * Get the validated input.
	 * @return the validated input
	 */
	Input<?> getInput();

	/**
	 * Create a new {@link InputValidationStatus} with {@link Status#UNRESOLVED}.
	 * @param input The validated input (not null)
	 * @return A new {@link InputValidationStatus}
	 */
	static InputValidationStatus unresolved(Input<?> input) {
		return new DefaultInputValidationStatus(input, Status.UNRESOLVED, Collections.emptyList());
	}

	/**
	 * Create a new {@link InputValidationStatus} with {@link Status#VALID}.
	 * @param input The validated input (not null)
	 * @return A new {@link InputValidationStatus}
	 */
	static InputValidationStatus valid(Input<?> input) {
		return new DefaultInputValidationStatus(input, Status.VALID, Collections.emptyList());
	}

	/**
	 * Create a new {@link InputValidationStatus} with {@link Status#INVALID}.
	 * @param input The validated input (not null)
	 * @param validationException Validation exception (not null)
	 * @return A new {@link InputValidationStatus}
	 */
	static InputValidationStatus invalid(Input<?> input, ValidationException validationException) {
		if (validationException == null) {
			throw new IllegalArgumentException("Validation exception must be not null");
		}
		return new DefaultInputValidationStatus(input, Status.INVALID, Collections.singletonList(validationException));
	}

	/**
	 * Create a new {@link InputValidationStatus} with {@link Status#INVALID}.
	 * @param input The validated input (not null)
	 * @param validationExceptions Validation exceptions (not null and not empty)
	 * @return A new {@link InputValidationStatus}
	 */
	static InputValidationStatus invalid(Input<?> input, List<ValidationException> validationExceptions) {
		if (validationExceptions == null || validationExceptions.isEmpty()) {
			throw new IllegalArgumentException("Validation exceptions must be not null and not empty");
		}
		return new DefaultInputValidationStatus(input, Status.INVALID, validationExceptions);
	}

}
