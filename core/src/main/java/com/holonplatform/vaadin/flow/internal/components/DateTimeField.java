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
import com.holonplatform.vaadin.flow.components.Input;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

	public static final String DEFAULT_TIME_INPUTS_WIDTH = "3.3em";

	private final DatePicker date;
	private Input<LocalTime> time;

	private String timeInputWidth;

	private final List<ValueChangeListener<ComponentValueChangeEvent<DateTimeField, LocalDateTime>>> valueChangeListeners = new LinkedList<>();

	/**
	 * Default constructor.
	 */
	public DateTimeField() {
		this(new DatePicker(), Input.localTime().build());
	}

	/**
	 * Constructor.
	 * @param time The time input (not null)
	 */
	public DateTimeField(Input<LocalTime> time) {
		this(new DatePicker(), time);
	}

	/**
	 * Constructor
	 * @param date The date input (not null)
	 * @param time The time input (not null)
	 */
	public DateTimeField(DatePicker date, Input<LocalTime> time) {
		super();
		ObjectUtils.argumentNotNull(date, "LocalDate input must be not null");
		ObjectUtils.argumentNotNull(time, "LocalTime input must be not null");
		this.date = date;
		this.time = time;
		this.timeInputWidth = time.hasSize().map(s -> s.getWidth()).orElse(null);
		getContent().setSpacing(false);
		getContent().setAlignItems(Alignment.BASELINE);
		getContent().add(this.date);
		getContent().add(this.time.getComponent());
	}

	/**
	 * Get the time input.
	 * @return the time input
	 */
	public Input<LocalTime> getTimeInput() {
		return time;
	}

	/**
	 * Replace the current time input with the given one.
	 * @param time the time input to set (not null)
	 */
	public void setTimeInput(Input<LocalTime> time) {
		ObjectUtils.argumentNotNull(time, "LocalTime input must be not null");
		Input<LocalTime> previous = this.time;
		this.time = time;
		this.time.setRequired(previous.isRequired());
		this.time.setReadOnly(previous.isReadOnly());
		if (this.timeInputWidth != null) {
			this.time.hasSize().ifPresent(s -> s.setWidth(timeInputWidth));
		}
		this.valueChangeListeners.forEach(listener -> {
			this.time.addValueChangeListener(e -> {
				if (e.isUserOriginated()) {
					listener.valueChanged(new ComponentValueChangeEvent<>(DateTimeField.this, DateTimeField.this,
							getDateValue().map(date -> LocalDateTime.of(date, e.getOldValue())).orElse(null),
							e.isUserOriginated()));
				}
			});
		});
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
	 * Set the time input width.
	 * @param timeInputWidth the time input width to set
	 */
	public void setTimeInputWidth(String timeInputWidth) {
		this.timeInputWidth = timeInputWidth;
		this.time.hasSize().ifPresent(s -> s.setWidth(timeInputWidth));
	}

	/**
	 * Get the time input width.
	 * @return the time input width
	 */
	public String getTimeInputWidth() {
		return timeInputWidth;
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
	 * Set the time input placeholder, i.e. a hint to the user of what can be entered.
	 * @param placeholder The placeholder to set
	 */
	public void setTimePlaceholder(String placeholder) {
		this.time.hasPlaceholder().ifPresent(p -> p.setPlaceholder(placeholder));
	}

	/**
	 * Get the time input placeholder, i.e. a hint to the user of what can be entered.
	 * @return The time input placeholder
	 */
	public String getTimePlaceholder() {
		return this.time.hasPlaceholder().map(p -> p.getPlaceholder()).orElse(null);
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

		final LocalDate date = (value != null) ? value.toLocalDate() : null;
		final LocalTime time = (value != null) ? value.toLocalTime() : null;

		this.date.setValue(date);
		this.time.setValue(time);

		fireValueChangeListeners(oldValue);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValue#getValue()
	 */
	@Override
	public LocalDateTime getValue() {
		return getDateValue().map(date -> LocalDateTime.of(date, getTimeValueOrDefault())).orElse(null);
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
	 * @return The time value
	 */
	protected Optional<LocalTime> getTimeValue() {
		return Optional.ofNullable(this.time.getValue());
	}

	/**
	 * Get the time part value as a {@link LocalTime}.
	 * @return The time value, never <code>null</code>
	 */
	protected LocalTime getTimeValueOrDefault() {
		return getTimeValue().orElse(LocalTime.of(0, 0));
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
						(e.getOldValue() == null) ? null : LocalDateTime.of(e.getOldValue(), getTimeValueOrDefault()),
						e.isFromClient()));
			}
		});
		final Registration tl = this.time.addValueChangeListener(e -> {
			if (e.isUserOriginated()) {
				listener.valueChanged(new ComponentValueChangeEvent<>(DateTimeField.this, DateTimeField.this,
						getDateValue().map(date -> LocalDateTime.of(date, e.getOldValue())).orElse(null),
						e.isUserOriginated()));
			}
		});
		return () -> {
			dl.remove();
			tl.remove();
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
		this.time.hasValidation().ifPresent(v -> v.setErrorMessage(null));
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
		final String timeError = this.time.hasValidation().map(v -> v.getErrorMessage()).orElse(null);
		if (timeError != null && !timeError.equals(this.date.getErrorMessage())) {
			if (sb.length() > 0) {
				sb.append(";");
			}
			sb.append(timeError);
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
		this.time.hasValidation().ifPresent(v -> v.setInvalid(invalid));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasValidation#isInvalid()
	 */
	@Override
	public boolean isInvalid() {
		if (this.date.isInvalid() || this.time.hasValidation().map(v -> v.isInvalid()).orElse(false)) {
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
		this.time.setRequired(required);
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
		this.time.setReadOnly(readOnly);
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