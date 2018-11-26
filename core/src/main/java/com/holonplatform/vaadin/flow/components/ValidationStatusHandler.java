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
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.internal.components.DefaultValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.LabelValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.NotificationValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValidationStatusEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.notification.Notification;

/**
 * Handler for validation status change events, typically bound to a {@link ValueComponent} source object.
 * 
 * @param <S> Validation source
 * @param <V> Validation value type
 * @param <C> Value component to which the validation event refers
 * 
 * @since 5.2.0
 */
@FunctionalInterface
public interface ValidationStatusHandler<S, V, C extends ValueComponent<V>> {

	/**
	 * Invoked when the validation status has changed.
	 * @param statusChangeEvent the changed status event, providing validation status, error message and the optional
	 *        source component
	 */
	void validationStatusChange(ValidationStatusEvent<S, V, C> statusChangeEvent);

	/**
	 * Create and return the default {@link ValidationStatusHandler}.
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
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @return A new default {@link ValidationStatusHandler} instance
	 */
	static <S, V, C extends ValueComponent<V>> ValidationStatusHandler<S, V, C> getDefault() {
		return new DefaultValidationStatusHandler<>();
	}

	/**
	 * Create and return the default {@link ValidationStatusHandler}.
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
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @param fallback The {@link ValidationStatusHandler} to use when the component on which the validation failed has
	 *        not validation support
	 * @return A new default {@link ValidationStatusHandler} instance
	 */
	static <S, V, C extends ValueComponent<V>> ValidationStatusHandler<S, V, C> getDefault(
			ValidationStatusHandler<S, V, C> fallback) {
		return new DefaultValidationStatusHandler<>(fallback);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which uses a {@link HasText} component to notify validation errors.
	 * <p>
	 * By default, the status label is set to visible only when the validation status is invalid, i.e. a validation
	 * error to display is available.
	 * </p>
	 * @param <S> Validation source
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @param <L> Label component type
	 * @param statusLabel The {@link HasText} component to use to notify validation errors (not null)
	 * @return A new validation status handler instance
	 */
	static <S, V, C extends ValueComponent<V>, L extends Component & HasText> ValidationStatusHandler<S, V, C> label(
			L statusLabel) {
		return label(statusLabel, true);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which uses a {@link HasText} component to notify validation errors.
	 * @param <S> Validation source
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @param <L> Label component type
	 * @param statusLabel The {@link HasText} component to use to notify validation errors (not null)
	 * @param hideWhenValid whether to hide the component when the validation status is not invalid
	 * @return A new validation status handler instance
	 */
	static <S, V, C extends ValueComponent<V>, L extends Component & HasText> ValidationStatusHandler<S, V, C> label(
			L statusLabel, boolean hideWhenValid) {
		return new LabelValidationStatusHandler<>(statusLabel, hideWhenValid);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * <p>
	 * This methods creates a notification validation status handler which displays only the first validation error.
	 * </p>
	 * @param <S> Validation source
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @return A new notification validation status handler instance
	 */
	static <S, V, C extends ValueComponent<V>> ValidationStatusHandler<S, V, C> notification() {
		return new NotificationValidationStatusHandler<>(null, false);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * @param <S> Validation source
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 * @return A new notification validation status handler instance
	 */
	static <S, V, C extends ValueComponent<V>> ValidationStatusHandler<S, V, C> notification(boolean showAllErrors) {
		return new NotificationValidationStatusHandler<>(null, showAllErrors);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * <p>
	 * This methods creates a notification validation status handler which displays only the first validation error.
	 * </p>
	 * @param <S> Validation source
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @return A new notification validation status handler instance
	 */
	static <S, V, C extends ValueComponent<V>> ValidationStatusHandler<S, V, C> notification(
			Notification notification) {
		return new NotificationValidationStatusHandler<>(notification, false);
	}

	/**
	 * Create a {@link ValidationStatusHandler} which shows a {@link Notification} to notify validation errors.
	 * @param notification The notification instance to use, <code>null</code> for default
	 * @param showAllErrors <code>true</code> to display all validation errors, <code>false</code> to show only the
	 *        first
	 * @param <S> Validation source
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 * @return A new notification validation status handler instance
	 */
	static <S, V, C extends ValueComponent<V>> ValidationStatusHandler<S, V, C> notification(Notification notification,
			boolean showAllErrors) {
		return new NotificationValidationStatusHandler<>(notification, showAllErrors);
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
	 * @param <V> Validation value type
	 * @param <C> Value component to which the validation event refers
	 */
	public interface ValidationStatusEvent<S, V, C extends ValueComponent<V>> extends Serializable {

		/**
		 * Ge the validation event source
		 * @return the validation event source.
		 */
		S getSource();

		/**
		 * Get the component against whom the validation was performed, if available.
		 * @return Optional validation event {@link Property}
		 */
		Optional<Property<V>> getProperty();

		/**
		 * Get the component against whom the validation was performed, if available.
		 * @return Optional validation event component
		 */
		Optional<C> getComponent();

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
		 * Get the localized error messages from {@link #getErrors()}, using the context {@link LocalizationContext}, if
		 * available. If a {@link LocalizationContext} is not available, the default message of each localizable error
		 * message is returned.
		 * @return the localized error messages, an empty list if none.
		 */
		default List<String> getErrorMessages() {
			return getErrors().stream().map(e -> LocalizationContext.translate(e, true)).collect(Collectors.toList());
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
		 * Get the first localized error message, using the context {@link LocalizationContext}, if available. If a
		 * {@link LocalizationContext} is not available, the default message of the first localizable error message is
		 * returned.
		 * @return the first localized error message, or <code>null</code> if none
		 */
		default String getErrorMessage() {
			return getError().map(e -> LocalizationContext.translate(e, true)).orElse(null);
		}

		// ------- builders

		/**
		 * Create a new {@link ValidationStatusEvent}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param status Validation status (not null)
		 * @param errors Validation errors
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> create(S source, Status status,
				List<Localizable> errors) {
			return new DefaultValidationStatusEvent<>(source, status, errors);
		}

		/**
		 * Create a new {@link ValidationStatusEvent}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param status Validation status (not null)
		 * @param error Validation error
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> create(S source, Status status,
				Localizable error) {
			return create(source, status, (error != null) ? Collections.singletonList(error) : Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} bound to a property and/or a value component.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param component Optional value component
		 * @param property Optional property
		 * @param status Validation status (not null)
		 * @param errors Validation errors
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> create(S source, C component,
				Property<V> property, Status status, List<Localizable> errors) {
			return new DefaultValidationStatusEvent<>(source, component, property, status, errors);
		}

		/**
		 * Create a new {@link ValidationStatusEvent} bound to a property and/or a value component.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param component Optional value component
		 * @param property Optional property
		 * @param status Validation status (not null)
		 * @param error Validation error
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> create(S source, C component,
				Property<V> property, Status status, Localizable error) {
			return create(source, component, property, status,
					(error != null) ? Collections.singletonList(error) : Collections.emptyList());
		}

		// ------- builders by status

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#UNRESOLVED}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> unresolved(S source) {
			return new DefaultValidationStatusEvent<>(source, Status.UNRESOLVED, Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#UNRESOLVED}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param component Optional value component
		 * @param property Optional property
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> unresolved(S source, C component,
				Property<V> property) {
			return new DefaultValidationStatusEvent<>(source, component, property, Status.UNRESOLVED,
					Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#VALID}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> valid(S source) {
			return new DefaultValidationStatusEvent<>(source, Status.VALID, Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#VALID}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param component Optional value component
		 * @param property Optional property
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> valid(S source, C component,
				Property<V> property) {
			return new DefaultValidationStatusEvent<>(source, component, property, Status.VALID,
					Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#INVALID}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param errors Validation errors
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> invalid(S source,
				List<Localizable> errors) {
			return new DefaultValidationStatusEvent<>(source, Status.INVALID, errors);
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#INVALID}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param error Validation error
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> invalid(S source, Localizable error) {
			return new DefaultValidationStatusEvent<>(source, Status.INVALID,
					(error != null) ? Collections.singletonList(error) : Collections.emptyList());
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#INVALID}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param component Optional value component
		 * @param property Optional property
		 * @param errors Validation errors
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> invalid(S source, C component,
				Property<V> property, List<Localizable> errors) {
			return new DefaultValidationStatusEvent<>(source, component, property, Status.INVALID, errors);
		}

		/**
		 * Create a new {@link ValidationStatusEvent} for {@link Status#INVALID}.
		 * @param <S> Validation source
		 * @param <V> Validation value type
		 * @param <C> Value component to which the validation event refers
		 * @param source Event source (not null)
		 * @param component Optional value component
		 * @param property Optional property
		 * @param error Validation error
		 * @return A new {@link ValidationStatusEvent}
		 */
		static <S, V, C extends ValueComponent<V>> ValidationStatusEvent<S, V, C> invalid(S source, C component,
				Property<V> property, Localizable error) {
			return new DefaultValidationStatusEvent<>(source, component, property, Status.INVALID,
					(error != null) ? Collections.singletonList(error) : Collections.emptyList());
		}

	}

}
