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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

/**
 * A {@link HasValue} component for date and time input.
 *
 * @since 5.2.0
 */
public class DateTimeField extends Composite<HorizontalLayout>
		implements HasValueAndElement<ComponentValueChangeEvent<DateTimeField, LocalDateTime>, LocalDateTime>, HasLabel,
		HasStyle, HasSize, HasValidation, Focusable<DateTimeField> {

	private static final long serialVersionUID = -6960232589608854521L;

	public static final String DEFAULT_TIME_SEPARATOR = ":";

	public static final String DEFAULT_TIME_INPUTS_WIDTH = "3.2em";

	private final DatePicker date;
	private final TextField hours;
	private final TextField minutes;

	private final Span timeSeparator;

	private final List<ValueChangeListener<ComponentValueChangeEvent<DateTimeField, LocalDateTime>>> valueChangeListeners = new LinkedList<>();

	public DateTimeField() {
		super();
		this.date = new DatePicker();
		this.hours = new TextField();
		this.minutes = new TextField();

		this.hours.setMaxLength(2);
		this.hours.setPattern("[01]?[0-9]|2[0-3]");
		this.hours.setPreventInvalidInput(true);
		this.hours.setAutocapitalize(Autocapitalize.NONE);
		this.hours.setAutocomplete(Autocomplete.OFF);
		this.hours.setAutocorrect(false);

		this.timeSeparator = new Span(DEFAULT_TIME_SEPARATOR);
		this.timeSeparator.addClassName("time-separator");

		this.hours.setSuffixComponent(this.timeSeparator);

		this.minutes.setMaxLength(2);
		this.minutes.setPattern("[0-5]?[0-9]");
		this.minutes.setPreventInvalidInput(true);
		this.minutes.setAutocapitalize(Autocapitalize.NONE);
		this.minutes.setAutocomplete(Autocomplete.OFF);
		this.minutes.setAutocorrect(false);

		this.hours.setWidth(DEFAULT_TIME_INPUTS_WIDTH);
		this.minutes.setWidth(DEFAULT_TIME_INPUTS_WIDTH);

		getContent().setSpacing(false);
		getContent().setAlignItems(Alignment.BASELINE);
		getContent().add(date);
		getContent().add(hours);
		getContent().add(minutes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasLabel#getLabel()
	 */
	@Override
	public String getLabel() {
		return this.date.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasLabel#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String label) {
		this.date.setLabel(label);
	}

	/**
	 * Get the time separator text, i.e. the text to show between the <code>hours</code> and <code>minutes</code>
	 * fields.
	 * @return the time separator text
	 */
	public String getTimeSeparator() {
		return timeSeparator.getText();
	}

	/**
	 * Set the time separator text, i.e. the text to show between the <code>hours</code> and <code>minutes</code>
	 * fields.
	 * @param timeSeparator the time separator to set, if <code>null</code> no separator will be shown.
	 */
	public void setTimeSeparator(String timeSeparator) {
		this.timeSeparator.setText((timeSeparator != null) ? timeSeparator : "");
	}

	/**
	 * Set whether to display a space between the <code>date</code>, <code>hours</code> and <code>minutes</code> fields.
	 * @param spacing whether to enable spacing
	 */
	public void setSpacing(boolean spacing) {
		getContent().setSpacing(spacing);
	}

	/**
	 * Get whether to display a space between the <code>date</code>, <code>hours</code> and <code>minutes</code> fields.
	 * @return <code>true</code> if spacing enabled, <code>false</code> otherwise
	 */
	public boolean isSpacing() {
		return getContent().isSpacing();
	}

	/**
	 * Set the time inputs (i.e. the <code>hours</code> and <code>minutes</code> fields) width.
	 * @param timeInputsWidth the time inputs width to set
	 */
	public void setTimeInputsWidth(String timeInputsWidth) {
		this.hours.setWidth(timeInputsWidth);
		this.minutes.setWidth(timeInputsWidth);
	}

	/**
	 * Get the time inputs (i.e. the <code>hours</code> and <code>minutes</code> fields) width.
	 * @return the time inputs width
	 */
	public String getTimeInputsWidth() {
		return this.hours.getWidth();
	}

	/**
	 * Set the input placeholder, i.e. a hint to the user of what can be entered.
	 * <p>
	 * The placeholder will be shown for the <code>date</code> input field. Use {@link #setHoursPlaceholder(String)} and
	 * {@link #setMinutesPlaceholder(String)} to set a placeholder for the time part fields.
	 * </p>
	 * @param placeholder The placeholder to set
	 */
	public void setPlaceholder(String placeholder) {
		this.date.setPlaceholder(placeholder);
	}

	/**
	 * Get the input placeholder, i.e. a hint to the user of what can be entered.
	 * @return The placeholder
	 */
	public String getPlaceholder() {
		return this.date.getPlaceholder();
	}

	/**
	 * Set the <code>hours</code> input placeholder, i.e. a hint to the user of what can be entered.
	 * @param placeholder The placeholder to set
	 */
	public void setHoursPlaceholder(String placeholder) {
		this.hours.setPlaceholder(placeholder);
	}

	/**
	 * Get the <code>hours</code> input placeholder, i.e. a hint to the user of what can be entered.
	 * @return The <code>hours</code> placeholder
	 */
	public String getHoursPlaceholder() {
		return this.hours.getPlaceholder();
	}

	/**
	 * Set the <code>minutes</code> input placeholder, i.e. a hint to the user of what can be entered.
	 * @param placeholder The placeholder to set
	 */
	public void setMinutesPlaceholder(String placeholder) {
		this.minutes.setPlaceholder(placeholder);
	}

	/**
	 * Get the <code>minutes</code> input placeholder, i.e. a hint to the user of what can be entered.
	 * @return The <code>minutes</code> placeholder
	 */
	public String getMinutesPlaceholder() {
		return this.minutes.getPlaceholder();
	}

	/**
	 * Set whether to display ISO-8601 week numbers in the calendar of the <code>date</code> field.
	 * @param weekNumbersVisible the boolean value to set
	 */
	public void setWeekNumbersVisible(boolean weekNumbersVisible) {
		this.date.setWeekNumbersVisible(weekNumbersVisible);
	}

	/**
	 * Get whether to display ISO-8601 week numbers in the calendar of the <code>date</code> field.
	 * @return whether to display ISO-8601 week numbers
	 */
	public boolean isWeekNumbersVisible() {
		return this.date.isWeekNumbersVisible();
	}

	/**
	 * Gets the internationalization settings for the <code>date</code> field.
	 * @return the date internationalization settings
	 */
	public DatePickerI18n getI18n() {
		return this.date.getI18n();
	}

	/**
	 * Sets the internationalization settings for the <code>date</code> field.
	 * @param i18n the internationalized settings, not <code>null</code>
	 */
	public void setI18n(DatePickerI18n i18n) {
		this.date.setI18n(i18n);
	}

	/**
	 * Sets the minimum date in <code>date</code> field. Dates before that will be disabled in the calendar popup.
	 * @param min the minimum date that is allowed to be selected, or <code>null</code> to remove any minimum
	 *        constraints
	 */
	public void setMin(LocalDate min) {
		this.date.setMin(min);
	}

	/**
	 * Gets the minimum date in the <code>date</code> field. Dates before that will be disabled in the calendar popup.
	 * @return the minimum date that is allowed to be selected, or <code>null</code> if there's no minimum
	 */
	public LocalDate getMin() {
		return this.date.getMin();
	}

	/**
	 * Sets the maximum date in the <code>date</code> field. Dates after that will be disabled in the calendar popup.
	 * @param max the maximum date that is allowed to be selected, or <code>null</code> to remove any maximum
	 *        constraints
	 */
	public void setMax(LocalDate max) {
		this.date.setMax(max);
	}

	/**
	 * Gets the maximum date in the <code>date</code> field. Dates after that will be disabled in the calendar popup.
	 * @return the maximum date that is allowed to be selected, or <code>null</code> if there's no maximum
	 */
	public LocalDate getMax() {
		return this.date.getMax();
	}

	/**
	 * Set the date and time which should be visible when there is no value selected.
	 * @param initialPosition the initial value to set
	 */
	public void setInitialPosition(LocalDateTime initialPosition) {
		this.date.setInitialPosition((initialPosition == null) ? null : initialPosition.toLocalDate());
	}

	/**
	 * Get the date and time which should be visible when there is no value selected.
	 * @return the initial date and time
	 */
	public LocalDateTime getInitialPosition() {
		return LocalDateTime.of(date.getInitialPosition(), LocalTime.of(0, 0));
	}

	/**
	 * Set the {@link Locale} to use for the <code>date</code> field. The displayed date will be matched to the format
	 * used in that locale.
	 * @param locale the locale set, not <code>null</code>
	 */
	public void setLocale(Locale locale) {
		this.date.setLocale(locale);
	}

	/**
	 * Gets the {@link Locale} to use for the <code>date</code> field.
	 * @return the <code>date</code> field locale
	 */
	@Override
	public Locale getLocale() {
		return this.date.getLocale();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(LocalDateTime value) {

		final LocalDateTime oldValue = getValue();

		LocalDate date = null;
		Integer hours = null;
		Integer minutes = null;

		if (value != null) {
			date = value.toLocalDate();
			hours = value.getHour();
			minutes = value.getMinute();
		}

		this.date.setValue(date);
		this.hours.setValue((hours == null) ? this.hours.getEmptyValue() : String.valueOf(hours.intValue()));
		this.minutes.setValue((minutes == null) ? this.minutes.getEmptyValue() : String.valueOf(minutes.intValue()));

		fireValueChangeListeners(oldValue);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#getValue()
	 */
	@Override
	public LocalDateTime getValue() {
		return getDateValue().map(date -> LocalDateTime.of(date, getTimeValue())).orElse(null);
	}

	/**
	 * Get the date part value as a {@link LocalDate}.
	 * @return The optional date value
	 */
	protected Optional<LocalDate> getDateValue() {
		return Optional.ofNullable(this.date.getValue());
	}

	/**
	 * Get the time part value as a {@link LocalTime}.
	 * @return The time value, never <code>null</code>
	 */
	protected LocalTime getTimeValue() {
		return LocalTime.of(getHoursValue().orElse(0), getMinutesValue().orElse(0));
	}

	/**
	 * Get the value of the <code>hours</code> field, if available.
	 * @return The <code>hours</code> field value, an empty Optional if the field is empty
	 */
	protected Optional<Integer> getHoursValue() {
		return getHoursValue(this.hours.getValue());
	}

	/**
	 * Get the value of the <code>minutes</code> field, if available.
	 * @return The <code>minutes</code> field value, an empty Optional if the field is empty
	 */
	protected Optional<Integer> getMinutesValue() {
		return getMinutesValue(this.minutes.getValue());
	}

	/**
	 * Get the time part value as a {@link LocalTime}.
	 * @param hours Hours value as text
	 * @param minutes Minutes value as text
	 * @return The time value, never <code>null</code>
	 */
	protected LocalTime getTimeValue(String hours, String minutes) {
		return LocalTime.of(getHoursValue(hours).orElse(0), getMinutesValue(minutes).orElse(0));
	}

	/**
	 * Get the value of the <code>hours</code> field, if available.
	 * @param textValue The text value
	 * @return The <code>hours</code> field value, an empty Optional if the field is empty
	 */
	protected Optional<Integer> getHoursValue(String textValue) {
		if (textValue != null && !textValue.trim().equals("")) {
			return Optional.ofNullable(Integer.valueOf(textValue));
		}
		return Optional.empty();
	}

	/**
	 * Get the value of the <code>minutes</code> field, if available.
	 * @param textValue The text value
	 * @return The <code>minutes</code> field value, an empty Optional if the field is empty
	 */
	protected Optional<Integer> getMinutesValue(String textValue) {
		if (textValue != null && !textValue.trim().equals("")) {
			return Optional.ofNullable(Integer.valueOf(textValue));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.flow.component.HasValue#addValueChangeListener(com.vaadin.flow.component.HasValue.ValueChangeListener)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<DateTimeField, LocalDateTime>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners
				.add((ValueChangeListener<ComponentValueChangeEvent<DateTimeField, LocalDateTime>>) listener);
		final Registration dl = this.date.addValueChangeListener(e -> {
			if (e.isFromClient()) {
				listener.valueChanged(new ComponentValueChangeEvent<>(DateTimeField.this, DateTimeField.this,
						(e.getOldValue() == null) ? null : LocalDateTime.of(e.getOldValue(), getTimeValue()),
						e.isFromClient()));
			}
		});
		final Registration hl = this.hours.addValueChangeListener(e -> {
			if (e.isFromClient()) {
				listener.valueChanged(new ComponentValueChangeEvent<>(DateTimeField.this, DateTimeField.this,
						getDateValue().map(
								date -> LocalDateTime.of(date, getTimeValue(e.getOldValue(), this.minutes.getValue())))
								.orElse(null),
						e.isFromClient()));
			}
		});
		final Registration ml = this.minutes.addValueChangeListener(e -> {
			if (e.isFromClient()) {
				listener.valueChanged(new ComponentValueChangeEvent<>(DateTimeField.this, DateTimeField.this,
						getDateValue().map(
								date -> LocalDateTime.of(date, getTimeValue(this.hours.getValue(), e.getOldValue())))
								.orElse(null),
						e.isFromClient()));
			}
		});
		return () -> {
			dl.remove();
			hl.remove();
			ml.remove();
		};
	}

	/**
	 * Fire value change listeners when value change is triggered from server side.
	 * @param oldValue The old value
	 */
	protected void fireValueChangeListeners(LocalDateTime oldValue) {
		this.valueChangeListeners.forEach(listener -> listener.valueChanged(
				new ComponentValueChangeEvent<>(DateTimeField.this, DateTimeField.this, oldValue, false)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValidation#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String errorMessage) {
		this.date.setErrorMessage(errorMessage);
		this.hours.setErrorMessage(null);
		this.minutes.setErrorMessage(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValidation#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		final StringBuilder sb = new StringBuilder();
		if (this.date.getErrorMessage() != null) {
			sb.append(this.date.getErrorMessage());
		}
		if (this.hours.getErrorMessage() != null && !this.hours.getErrorMessage().equals(this.date.getErrorMessage())) {
			if (sb.length() > 0) {
				sb.append(";");
			}
			sb.append(this.hours.getErrorMessage());
		}
		if (this.minutes.getErrorMessage() != null
				&& !this.minutes.getErrorMessage().equals(this.date.getErrorMessage())) {
			if (sb.length() > 0) {
				sb.append(";");
			}
			sb.append(this.minutes.getErrorMessage());
		}
		return (sb.length() == 0) ? null : sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValidation#setInvalid(boolean)
	 */
	@Override
	public void setInvalid(boolean invalid) {
		this.date.setInvalid(invalid);
		this.hours.setInvalid(invalid);
		this.minutes.setInvalid(invalid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValidation#isInvalid()
	 */
	@Override
	public boolean isInvalid() {
		if (this.date.isInvalid() || this.hours.isInvalid() || this.minutes.isInvalid()) {
			return true;
		}
		return false;
	}

	/**
	 * Get whether the field is required, i.e. the user must fill in a value.
	 * @return whether the field is required
	 */
	public boolean isRequired() {
		return this.date.isRequired();
	}

	/**
	 * Specifies that the user must fill in a value.
	 * @param required whether the field is required
	 */
	public void setRequired(boolean required) {
		this.date.setRequired(required);
		this.hours.setRequired(required);
		this.minutes.setRequired(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValueAndElement#setRequiredIndicatorVisible(boolean)
	 */
	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		this.date.setRequiredIndicatorVisible(requiredIndicatorVisible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValueAndElement#isRequiredIndicatorVisible()
	 */
	@Override
	public boolean isRequiredIndicatorVisible() {
		return this.date.isRequiredIndicatorVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValueAndElement#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		this.date.setReadOnly(readOnly);
		this.hours.setReadOnly(readOnly);
		this.minutes.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValueAndElement#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return this.date.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int tabIndex) {
		this.date.setTabIndex(tabIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.Focusable#getTabIndex()
	 */
	@Override
	public int getTabIndex() {
		return this.date.getTabIndex();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.Focusable#focus()
	 */
	@Override
	public void focus() {
		this.date.focus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.Focusable#blur()
	 */
	@Override
	public void blur() {
		this.date.blur();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.FocusNotifier#addFocusListener(com.vaadin.flow.component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public Registration addFocusListener(ComponentEventListener<FocusEvent<DateTimeField>> listener) {
		return this.date.addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<DatePicker>>() {

			@Override
			public void onComponentEvent(FocusEvent<DatePicker> event) {
				listener.onComponentEvent(new FocusEvent<>(DateTimeField.this, event.isFromClient()));
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.BlurNotifier#addBlurListener(com.vaadin.flow.component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public Registration addBlurListener(ComponentEventListener<BlurEvent<DateTimeField>> listener) {
		return this.date.addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<DatePicker>>() {

			@Override
			public void onComponentEvent(BlurEvent<DatePicker> event) {
				listener.onComponentEvent(new BlurEvent<>(DateTimeField.this, event.isFromClient()));
			}

		});
	}

}
