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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.Input;
import com.vaadin.flow.component.Component;

/**
 * Base builder to create date type {@link Input} components.
 * 
 * @param <D> Actual date value type
 * @param <B> Concrete builder type
 * 
 * @since 5.2.0
 */
public interface BaseDateInputBuilder<D, B extends BaseDateInputBuilder<D, B>>
		extends InputBuilder<D, Input<D>, B>, InputValueConfigurator<D, B>, HasSizeConfigurator<B>,
		HasStyleConfigurator<B>, HasEnabledConfigurator<B>, FocusableConfigurator<Component, B>,
		HasPlaceholderConfigurator<B>, HasLabelConfigurator<B>, DeferrableLocalizationConfigurator<B> {

	/**
	 * Set the {@link Locale} to use. The displayed date will be matched to the format used in that locale.
	 * @param locale The locale to set (not null)
	 * @return this
	 */
	B locale(Locale locale);

	/**
	 * Set whether to use the current {@link LocalizationContext} {@link Locale}, if available. The context
	 * {@link Locale} is configured when the input component is attached to a parent layout.
	 * <p>
	 * To obtain the context {@link Locale}, a {@link LocalizationContext} must be valid (localized) and available as a
	 * {@link Context} resource.
	 * </p>
	 * @param useContextLocale Whether to use the current {@link LocalizationContext} {@link Locale}, if available
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	B useContextLocale(boolean useContextLocale);

	/**
	 * Sets the minimum date that is allowed to be selected.
	 * @param min the minimum date that is allowed to be selected, or <code>null</code> to remove any minimum
	 *        constraints
	 * @return this
	 */
	B min(D min);

	/**
	 * Sets the maximum date that is allowed to be selected.
	 * @param max the maximum date that is allowed to be selected, or <code>null</code> to remove any maximum
	 *        constraints
	 * @return this
	 */
	B max(D max);

	/**
	 * Set the date which should be visible when there is no value selected.
	 * @param initialPosition the initial position to set
	 * @return this
	 */
	B initialPosition(D initialPosition);

	/**
	 * Set the visible week numbers.
	 * <p>
	 * Set <code>true</code> to display ISO-8601 week numbers in the calendar.
	 * </p>
	 * @param weekNumbersVisible Whether the week numbers are visible
	 * @return this
	 */
	B weekNumbersVisible(boolean weekNumbersVisible);

	/**
	 * Set the Date Input calendar localization messages.
	 * <p>
	 * Use {@link #localization()} to obtain a suitable builder to configure the calendar localization messages.
	 * </p>
	 * @param localization Calendar localization messages
	 * @return this
	 */
	B localization(CalendarLocalization localization);

	/**
	 * Get a builder to setup the Date Input calendar localization messages.
	 * <p>
	 * The {@link CalendarLocalizationBuilder#set()} method can be used to configure the localization and go back to the
	 * parent Date Input builder.
	 * </p>
	 * @return A {@link CalendarLocalizationBuilder} to setup the Date Input calendar localization messages
	 */
	CalendarLocalizationBuilder<D, B> localization();

	// calendar localization

	/**
	 * Date input calendar popup localization settings.
	 */
	public interface CalendarLocalization {

		/**
		 * Gets {@link Localizable} messages for the name of the months.
		 * @return the month names
		 */
		List<Localizable> getMonthNames();

		/**
		 * Gets the {@link Localizable} messages for the name of the week days.
		 * @return the week days names
		 */
		List<Localizable> getWeekdays();

		/**
		 * Gets the {@link Localizable} messages for the short names of the week days.
		 * @return the short names of the week days
		 */
		List<Localizable> getWeekdaysShort();

		/**
		 * Gets the first day of the week.
		 * <p>
		 * 0 for Sunday, 1 for Monday, 2 for Tuesday, 3 for Wednesday, 4 for Thursday, 5 for Friday, 6 for Saturday.
		 * </p>
		 * @return the index of the first day of the week
		 */
		Integer getFirstDayOfWeek();

		/**
		 * Gets the {@link Localizable} message for <code>week</code>.
		 * @return the <code>week</code> text message
		 */
		Optional<Localizable> getWeek();

		/**
		 * Gets the {@link Localizable} message for <code>calendar</code>.
		 * @return the <code>calendar</code> text message
		 */
		Optional<Localizable> getCalendar();

		/**
		 * Gets the {@link Localizable} message for the <code>clear</code> button.
		 * @return the <code>clear</code> text message
		 */
		Optional<Localizable> getClear();

		/**
		 * Gets the {@link Localizable} message for the <code>today</code> button.
		 * @return the <code>today</code> text message
		 */
		Optional<Localizable> getToday();

		/**
		 * Gets the {@link Localizable} message for the <code>cancel</code> button.
		 * @return the <code>cancel</code> text message
		 */
		Optional<Localizable> getCancel();

	}

	public interface CalendarLocalizationBuilder<D, B extends BaseDateInputBuilder<D, B>> {

		/**
		 * Set the {@link Localizable} messages to use to display the month names, starting from January and ending on
		 * December.
		 * @param monthNames The month names
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> monthNames(List<Localizable> monthNames);

		/**
		 * Set the {@link Localizable} messages to use to display the month names, starting from January and ending on
		 * December.
		 * @param monthNames The month names
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> monthNames(Localizable... monthNames) {
			return monthNames(Arrays.asList(monthNames));
		}

		/**
		 * Sets the name of the months, starting from January and ending on December.
		 * @param monthNames The month names
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> monthNames(String... monthNames) {
			return monthNames(Arrays.asList(monthNames).stream().map(n -> Localizable.builder().message(n).build())
					.collect(Collectors.toList()));
		}

		/**
		 * Set the {@link Localizable} messages to use to display the week days names, starting from Sunday and ending
		 * on Saturday.
		 * @param weekDays The week day names
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> weekDays(List<Localizable> weekDays);

		/**
		 * Set the {@link Localizable} messages to use to display the week days names, starting from Sunday and ending
		 * on Saturday.
		 * @param weekDays The week day names
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> weekDays(Localizable... weekDays) {
			return weekDays(Arrays.asList(weekDays));
		}

		/**
		 * Sets the name of the week days, starting from Sunday and ending on Saturday.
		 * @param weekDays The week day names
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> weekDays(String... weekDays) {
			return weekDays(Arrays.asList(weekDays).stream().map(n -> Localizable.builder().message(n).build())
					.collect(Collectors.toList()));
		}

		/**
		 * Set the {@link Localizable} messages to use to display the short week days names, starting from Sunday and
		 * ending on Saturday.
		 * @param weekDaysShort The short week day names
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> weekDaysShort(List<Localizable> weekDaysShort);

		/**
		 * Set the {@link Localizable} messages to use to display the short week days names, starting from Sunday and
		 * ending on Saturday.
		 * @param weekDaysShort The short week day names
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> weekDaysShort(Localizable... weekDaysShort) {
			return weekDaysShort(Arrays.asList(weekDaysShort));
		}

		/**
		 * Sets the short name of the week days, starting from Sunday and ending on Saturday.
		 * @param weekDaysShort The short week day names
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> weekDaysShort(String... weekDaysShort) {
			return weekDaysShort(Arrays.asList(weekDaysShort).stream()
					.map(n -> Localizable.builder().message(n).build()).collect(Collectors.toList()));
		}

		/**
		 * Sets the first day of the week.
		 * <p>
		 * 0 for Sunday, 1 for Monday, 2 for Tuesday, 3 for Wednesday, 4 for Thursday, 5 for Friday, 6 for Saturday.
		 * </p>
		 * @param firstDayOfWeek the index of the first day of the week
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> firstDayOfWeek(int firstDayOfWeek);

		/**
		 * Set the {@link Localizable} message to use for the word <code>week</code>.
		 * @param message the {@link Localizable} message to use
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> week(Localizable message);

		/**
		 * Set the message to use for the word <code>week</code>.
		 * @param message the message to use
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> week(String message) {
			return week(Localizable.builder().message(message).build());
		}

		/**
		 * Set the localizable message to use for the word <code>week</code>.
		 * @param defaultMessage Default message
		 * @param messageCode Message translation code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> week(String defaultMessage, String messageCode, Object... arguments) {
			return week(Localizable.builder().message(defaultMessage).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the {@link Localizable} message to use for the word <code>calendar</code>.
		 * @param message the {@link Localizable} message to use
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> calendar(Localizable message);

		/**
		 * Set the message to use for the word <code>calendar</code>.
		 * @param message the message to use
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> calendar(String message) {
			return calendar(Localizable.builder().message(message).build());
		}

		/**
		 * Set the localizable message to use for the word <code>week</code>.
		 * @param defaultMessage Default message
		 * @param messageCode Message translation code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> calendar(String defaultMessage, String messageCode,
				Object... arguments) {
			return calendar(Localizable.builder().message(defaultMessage).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the {@link Localizable} message to use for the word <code>clear</code>.
		 * @param message the {@link Localizable} message to use
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> clear(Localizable message);

		/**
		 * Set the message to use for the word <code>clear</code>.
		 * @param message the message to use
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> clear(String message) {
			return clear(Localizable.builder().message(message).build());
		}

		/**
		 * Set the localizable message to use for the word <code>clear</code>.
		 * @param defaultMessage Default message
		 * @param messageCode Message translation code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> clear(String defaultMessage, String messageCode,
				Object... arguments) {
			return clear(Localizable.builder().message(defaultMessage).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the {@link Localizable} message to use for the word <code>today</code>.
		 * @param message the {@link Localizable} message to use
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> today(Localizable message);

		/**
		 * Set the message to use for the word <code>today</code>.
		 * @param message the message to use
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> today(String message) {
			return today(Localizable.builder().message(message).build());
		}

		/**
		 * Set the localizable message to use for the word <code>today</code>.
		 * @param defaultMessage Default message
		 * @param messageCode Message translation code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> today(String defaultMessage, String messageCode,
				Object... arguments) {
			return today(Localizable.builder().message(defaultMessage).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the {@link Localizable} message to use for the word <code>cancel</code>.
		 * @param message the {@link Localizable} message to use
		 * @return this
		 */
		CalendarLocalizationBuilder<D, B> cancel(Localizable message);

		/**
		 * Set the message to use for the word <code>cancel</code>.
		 * @param message the message to use
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> cancel(String message) {
			return cancel(Localizable.builder().message(message).build());
		}

		/**
		 * Set the localizable message to use for the word <code>cancel</code>.
		 * @param defaultMessage Default message
		 * @param messageCode Message translation code
		 * @param arguments Optional message translation arguments
		 * @return this
		 */
		default CalendarLocalizationBuilder<D, B> cancel(String defaultMessage, String messageCode,
				Object... arguments) {
			return cancel(Localizable.builder().message(defaultMessage).messageCode(messageCode)
					.messageArguments(arguments).build());
		}

		/**
		 * Set the calendar localization settings for the Date Input.
		 * @return The parent Input builder
		 */
		B set();

	}

}
