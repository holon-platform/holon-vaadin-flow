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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;

/**
 * Validation status.
 *
 * @since 5.2.0
 */
public interface ValidationStatus {

	/**
	 * Get the validation status.
	 * @return the validation status
	 */
	Status getStatus();

	/**
	 * Get whether the validation status is invalid.
	 * @return whether the validation status is invalid
	 */
	default boolean isInvalid() {
		return getStatus() == Status.INVALID;
	}

	/**
	 * Get the validation excaptions, if any.
	 * @return the validation excaptions, empty if none
	 */
	List<ValidationException> getValidationExceptions();

	/**
	 * Get the validation errors.
	 * @return the validation errors
	 */
	default List<Localizable> getErrors() {
		return getValidationExceptions().stream().map(v -> v.getValidationMessages()).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	/**
	 * Create a new {@link ValidationStatus} with {@link Status#UNRESOLVED}.
	 * @return A new {@link ValidationStatus}
	 */
	static ValidationStatus unresolved() {
		return new DefaultValidationStatus(Status.UNRESOLVED, Collections.emptyList());
	}

	/**
	 * Create a new {@link ValidationStatus} with {@link Status#VALID}.
	 * @return A new {@link ValidationStatus}
	 */
	static ValidationStatus valid() {
		return new DefaultValidationStatus(Status.VALID, Collections.emptyList());
	}

	/**
	 * Create a new {@link ValidationStatus} with {@link Status#INVALID}.
	 * @param validationException Validation exception (not null)
	 * @return A new {@link ValidationStatus}
	 */
	static ValidationStatus invalid(ValidationException validationException) {
		if (validationException == null) {
			throw new IllegalArgumentException("Validation exception must be not null");
		}
		return new DefaultValidationStatus(Status.INVALID, Collections.singletonList(validationException));
	}

	/**
	 * Create a new {@link ValidationStatus} with {@link Status#INVALID}.
	 * @param validationExceptions Validation exceptions (not null and not empty)
	 * @return A new {@link ValidationStatus}
	 */
	static ValidationStatus invalid(List<ValidationException> validationExceptions) {
		if (validationExceptions == null || validationExceptions.isEmpty()) {
			throw new IllegalArgumentException("Validation exceptions must be not null and not empty");
		}
		return new DefaultValidationStatus(Status.INVALID, validationExceptions);
	}

}
