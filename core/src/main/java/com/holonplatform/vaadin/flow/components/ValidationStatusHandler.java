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
package com.holonplatform.vaadin.flow.components;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.DefaultHasComponentValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.DialogValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.LabelValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.NotificationValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValidationStatusEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;

/**
 * Handler for validation status change events, typically bound to a {@link ValueComponent} source object.
 * 
 * @param <S> Validation source type
 * 
 * @since 5.2.0
 */
@FunctionalInterface
public interface ValidationStatusHandler<S> extends Serializable {

	/**
	 * Invoked when the validation status has changed.
	 * @param statusChangeEvent the changed status event, providing validation status and error messages
	 */
	void validationStatusChange(ValidationStatusEvent<S> statusChangeEvent);

	/**
	 * Create and return the default {@link ValidationStatusHandler} for {@link HasComponent} types.
	 * <p>
	 * The default validation status handler uses {{@link HasValidation#setErrorMessage(String)} to notify the
	 * validation status on the value component against whom the validation is performed, if the component implements
	 * {@link HasValidation}.
	 * </p>
	 * <p>
	 * If the component against whom the validation is performed does not implement {@link HasValidation}, a warning is
	 * logged.
	 * </p>
	 * @param <S> Validation source
	 * @return A new default {@link ValidationStatusHandler} instance
	 */
	static <S extends HasComponent> ValidationStatusHandler<S> getDefault() {
		return new DefaultHasComponentValidationStatusHandler<>();
	}

	/**
	 * Create and return the default {@link ValidationStatusHandler} for {@link HasComponent} types.
	 * <p>
	 * The default validation status handler uses {{@link HasValidation#setErrorMessage(String)} to notify the
	 * validation status on the value component against whom the validation is performed, if the component implements
	 * {@link HasValidation}.
	 * </p>
	 * <p>
	 * If the component against whom the validation is performed does not implement {@link HasValidation}, a warning is
	 * logged.
	 * </p>
	 * @param <S> Validation source
	 * @param fallback The {@link ValidationStatusHandler} to use when the component on which the validation failed has
	 *        not validation support
	 * @return A new default {@link ValidationStatusHandler} instance
	 */
	static <S extends HasComponent> ValidationStatusHandler<S> getDefault(ValidationStatusHandler<S> fallback) {
		return new DefaultHasComponentValidationStatusHandler<>(fallback);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which uses a {@link HasText} component to notify validation errors.
	 * <p>
	 * By default, the status label is set to visible only when the validation status is invalid, i.e. a validation
	 * error to display is available.
	 * </p>
	 * @param <S> Validation source
	 * @param <L> Label component type
	 * @param statusLabel The {@link HasText} component to use to notify validation errors (not null)
	 * @return A new validation status handler instance
	 */
	static <S, L extends Component & HasText> ValidationStatusHandler<S> label(L statusLabel) {
		return label(statusLabel, true);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which uses a {@link HasText} component to notify validation errors.
	 * @param <S> Validation source
	 * @param <L> Label component type
	 * @param statusLabel The {@link HasText} component to use to notify validation errors (not null)
	 * @param hideWhenValid whether to hide the component when the validation status is not invalid
	 * @return A new validation status handler instance
	 */
	static <S, L extends Component & HasText> ValidationStatusHandler<S> label(L statusLabel, boolean hideWhenValid) {
		return new LabelValidationStatusHandler<>(statusLabel, hideWhenValid);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * <p>
	 * This methods creates a notification validation status handler which displays only the first validation error.
	 * </p>
	 * @param <S> Validation source
	 * @return A new notification validation status handler instance
	 */
	static <S> ValidationStatusHandler<S> notification() {
		return new NotificationValidationStatusHandler<>(null, false);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * @param <S> Validation source
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 * @return A new notification validation status handler instance
	 */
	static <S> ValidationStatusHandler<S> notification(boolean showAllErrors) {
		return new NotificationValidationStatusHandler<>(null, showAllErrors);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * <p>
	 * This methods creates a notification validation status handler which displays only the first validation error.
	 * </p>
	 * @param <S> Validation source
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @return A new notification validation status handler instance
	 */
	static <S> ValidationStatusHandler<S> notification(Notification notification) {
		return new NotificationValidationStatusHandler<>(notification, false);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 * @param <S> Validation source
	 * @return A new notification validation status handler instance
	 */
	static <S> ValidationStatusHandler<S> notification(Notification notification, boolean showAllErrors) {
		return new NotificationValidationStatusHandler<>(notification, showAllErrors);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Dialog} to notify validation errors.
	 * @param <S> Validation source
	 * @return A new {@link Dialog} notification validation status handler instance
	 */
	static <S> ValidationStatusHandler<S> dialog() {
		return new DialogValidationStatusHandler<>();
	}

	/**
	 * Validation status.
	 */
	public enum Status {

		/**
		 * Unresolved status, e.g. no value validation has been made yet.
		 */
		UNRESOLVED,

		/**
		 * Validation passed.
		 */
		VALID,

		/**
		 * Validation failed.
		 */
		INVALID;

	}

	/**
	 * A validation status event.
	 * 
	 * @param <S> Validation source
	 */
	public interface ValidationStatusEvent<S> extends Serializable {

		/**
		 * Ge the validation event source
		 * @return the validation event source.
		 */
		S getSource();

		/**
		 * Get the validation status.
		 * @return the validation status (never null)
		 */
		Status getStatus();

		/**
		 * Gets whether the validation failed or not.
		 * @return <code>true</code> if validation failed (i.e. the validation status is {@link Status#INVALID}),
		 *         <code>false</code> otherwise
		 */
		default boolean isInvalid() {
			return getStatus() == Status.INVALID;
		}

		/**
		 * Get the {@link Localizable} validation error messages, if the validation status is {@link Status#INVALID}.
		 * @return the validation error messages when in invalid state, an empty list if none
		 */
		List<Localizable> getErrors();

		/**
		 * Get the localized error messages from {@link #getErrors()}.
		 * @return the localized error messages, an empty list if none.
		 * @see LocalizationProvider
		 */
		default List<String> getErrorMessages() {
			return getErrors().stream().map(e -> LocalizationProvider.localize(e).orElse(e.getMessage()))
					.collect(Collectors.toList());
		}

		/**
		 * Get the first {@link Localizable} validation error message, if the validation status is
		 * {@link Status#INVALID}.
		 * @return the optional first {@link Localizable} validation error message when in invalid state
		 */
		default Optional<Localizable> getError() {
			return Optional.ofNullable(getErrors().size() > 0 ? getErrors().get(0) : null);
		}

		/**
		 * Get the first localized error message.
		 * @return the first localized error message, or <code>null</code> if none
		 * @see LocalizationProvider
		 */
		default String getErrorMessage() {
			return getError().map(e -> LocalizationProvider.localize(e).orElse(e.getMessage())).orElse(null);
		}

		// ------- builders

		/**
		 * Create a new {@link ValidationStatusEvent}.
		 * @param <S> Validation source
		 * @param source Event source (not null)
		 * @param status Validation status (not null)
		 * @param errors Validation errors
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S> ValidationStatusEvent<S> create(S source, Status status, List<Localizable> errors) {
			return new DefaultValidationStatusEvent<>(source, status, errors);
		}

		/**
		 * Create a new {@link ValidationStatusEvent}.
		 * @param <S> Validation source
		 * @param source Event source (not null)
		 * @param status Validation status (not null)
		 * @param error Validation error
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S> ValidationStatusEvent<S> create(S source, Status status, Localizable error) {
			return create(source, status, (error != null) ? Collections.singletonList(error) : Collections.emptyList());
		}

		// ------- builders by status

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#UNRESOLVED}.
		 * @param <S> Validation source
		 * @param source Event source (not null)
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S> ValidationStatusEvent<S> unresolved(S source) {
			return new DefaultValidationStatusEvent<>(source, Status.UNRESOLVED, Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#VALID}.
		 * @param <S> Validation source
		 * @param source Event source (not null)
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S> ValidationStatusEvent<S> valid(S source) {
			return new DefaultValidationStatusEvent<>(source, Status.VALID, Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#INVALID}.
		 * @param <S> Validation source
		 * @param source Event source (not null)
		 * @param errors Validation errors
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S> ValidationStatusEvent<S> invalid(S source, List<Localizable> errors) {
			return new DefaultValidationStatusEvent<>(source, Status.INVALID, errors);
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#INVALID}.
		 * @param <S> Validation source
		 * @param source Event source (not null)
		 * @param error Validation error
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S> ValidationStatusEvent<S> invalid(S source, Localizable error) {
			return new DefaultValidationStatusEvent<>(source, Status.INVALID,
					(error != null) ? Collections.singletonList(error) : Collections.emptyList());
		}

	}

}
