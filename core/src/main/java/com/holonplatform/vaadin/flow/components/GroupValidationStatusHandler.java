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
package com.holonplatform.vaadin.flow.components;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent;

/**
 * Handler for validation status change events for a group of input fields.
 *
 * @param <S> Validation source type
 *
 * @since 5.2.0
 */
@FunctionalInterface
public interface GroupValidationStatusHandler<S> extends Serializable {

	/**
	 * Invoked when the group validation status has changed.
	 * @param statusChangeEvent the changed status event, providing validation status and error messages
	 */
	void validationStatusChange(GroupValidationStatusEvent<S> statusChangeEvent);

	/**
	 * A group validation status event.
	 * 
	 * @param <S> Validation source type
	 */
	public interface GroupValidationStatusEvent<S> extends Serializable {

		/**
		 * Get the overall validation status.
		 * <p>
		 * This includes the item validation status and the inputs validation status. If any of the item or inputs
		 * validation status is invalid, the returned status will be invalid.
		 * </p>
		 * @return the overall validation status (never null)
		 */
		Status getStatus();

		/**
		 * Gets whether the overall validation failed or not.
		 * <p>
		 * This includes the item validation status and the inputs validation status. If any of the item or inputs
		 * validation status is invalid, the returned status will be invalid.
		 * </p>
		 * @return <code>true</code> if validation failed (i.e. the validation status is {@link Status#INVALID}),
		 *         <code>false</code> otherwise
		 */
		default boolean isInvalid() {
			return isGroupInvalid() || isAnyInputInvalid();
		}

		/**
		 * Get the group validation status, i.e. the validation status according to any registered group-level
		 * validator.
		 * @return the group validation status (never null)
		 */
		Status getGroupStatus();

		/**
		 * Gets whether the group validation failed or not.
		 * @return <code>true</code> if validation failed (i.e. the validation status is {@link Status#INVALID}),
		 *         <code>false</code> otherwise
		 */
		default boolean isGroupInvalid() {
			return getGroupStatus() == Status.INVALID;
		}

		/**
		 * Get the {@link Localizable} group validation error messages, if the group validation status is
		 * {@link Status#INVALID}.
		 * @return the group validation error messages when in invalid state, an empty list if none
		 */
		List<Localizable> getGroupErrors();

		/**
		 * Get the localized group error messages from {@link #getErrors()}, using the context
		 * {@link LocalizationContext}, if available. If a {@link LocalizationContext} is not available, the default
		 * message of each localizable error message is returned.
		 * @return the localized group error messages, an empty list if none.
		 */
		default List<String> getGroupErrorMessages() {
			return getGroupErrors().stream().map(e -> LocalizationContext.translate(e, true))
					.collect(Collectors.toList());
		}

		/**
		 * Get the first {@link Localizable} group validation error message, if the validation status is
		 * {@link Status#INVALID}.
		 * @return the optional first {@link Localizable} group validation error message when in invalid state
		 */
		default Optional<Localizable> getGroupError() {
			return getGroupErrors().stream().findFirst();
		}

		/**
		 * Get the first localized group error message, using the context {@link LocalizationContext}, if available. If
		 * a {@link LocalizationContext} is not available, the default message of the first localizable error message is
		 * returned.
		 * @return the first localized group error message, or <code>null</code> if none
		 */
		default String getGroupErrorMessage() {
			return getGroupError().map(e -> LocalizationContext.translate(e, true)).orElse(null);
		}

		/**
		 * Get the group inputs validation status events.
		 * @return the group inputs validation status events, an empty list if nine
		 */
		List<ValidationStatusEvent<S, ?, ?>> getInputsValidationStatus();

		/**
		 * Gets whether any of the group input is in an invalid status.
		 * @return <code>true</code> at least one input validation failed (i.e. the validation status is
		 *         {@link Status#INVALID}), <code>false</code> otherwise
		 */
		default boolean isAnyInputInvalid() {
			return getInputsValidationStatus().stream().anyMatch(s -> s.getStatus() == Status.INVALID);
		}

	}

}
