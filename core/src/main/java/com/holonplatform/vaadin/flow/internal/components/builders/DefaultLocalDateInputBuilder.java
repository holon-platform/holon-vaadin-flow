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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultInvalidChangeEvent;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.shared.Registration;

/**
 * Default {@link LocalDateInputBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultLocalDateInputBuilder extends
		AbstractLocalizableComponentConfigurator<DatePicker, LocalDateInputBuilder> implements LocalDateInputBuilder {

	private final List<ValueChangeListener<LocalDate>> valueChangeListeners = new LinkedList<>();

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasLabelConfigurator<DatePicker> labelConfigurator;
	protected final DefaultHasPlaceholderConfigurator<DatePicker> placeholderConfigurator;

	private Registration contextLocaleOnAttachRegistration;

	private CalendarLocalization localization;

	public DefaultLocalDateInputBuilder() {
		super(new DatePicker());

		sizeConfigurator = new DefaultHasSizeConfigurator(getComponent());
		styleConfigurator = new DefaultHasStyleConfigurator(getComponent());
		enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), placeholder -> {
			getComponent().setPlaceholder(placeholder);
		}, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected LocalDateInputBuilder getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<LocalDate> build() {
		final DatePicker component = getComponent();

		// calendar localization
		if (localization != null) {
			if (isDeferredLocalizationEnabled()) {
				final CalendarLocalization localizationOnAttach = localization;
				component.addAttachListener(e -> {
					if (e.isInitialAttach()) {
						component.setI18n(localize(localizationOnAttach));
					}
				});
			} else {
				component.setI18n(localize(localization));
			}
		}

		return Input.builder(component).requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.focusOperation(f -> f.focus()).hasEnabledSupplier(f -> f)
				.invalidChangeEventNotifierSupplier(f -> listener -> f.addInvalidChangeListener(
						e -> listener.onInvalidChangeEvent(new DefaultInvalidChangeEvent(e.isFromClient(), f))))
				.withValueChangeListeners(valueChangeListeners).build();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<LocalDate, ValidatableInput<LocalDate>> validatable() {
		return ValidatableInputBuilder.create(build());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public LocalDateInputBuilder readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public LocalDateInputBuilder withValue(LocalDate value) {
		getComponent().setValue(value);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public LocalDateInputBuilder withValueChangeListener(ValueChangeListener<LocalDate> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public LocalDateInputBuilder required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public LocalDateInputBuilder width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public LocalDateInputBuilder height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public LocalDateInputBuilder styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public LocalDateInputBuilder styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public LocalDateInputBuilder enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public LocalDateInputBuilder tabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public LocalDateInputBuilder withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<DatePicker>>() {

			@Override
			public void onComponentEvent(FocusEvent<DatePicker> event) {
				listener.onComponentEvent(new FocusEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public LocalDateInputBuilder withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<DatePicker>>() {

			@Override
			public void onComponentEvent(BlurEvent<DatePicker> event) {
				listener.onComponentEvent(new BlurEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public LocalDateInputBuilder placeholder(Localizable placeholder) {
		placeholderConfigurator.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public LocalDateInputBuilder label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public LocalDateInputBuilder locale(Locale locale) {
		getComponent().setLocale(locale);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#useContextLocale(boolean)
	 */
	@Override
	public LocalDateInputBuilder useContextLocale(boolean useContextLocale) {
		// unregister previous
		if (contextLocaleOnAttachRegistration != null) {
			contextLocaleOnAttachRegistration.remove();
			contextLocaleOnAttachRegistration = null;
		}
		contextLocaleOnAttachRegistration = getComponent().addAttachListener(e -> {
			if (e.isInitialAttach()) {
				LocalizationContext.getCurrent().flatMap(ctx -> ctx.getLocale())
						.ifPresent(locale -> getComponent().setLocale(locale));
			}
		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#min(java.time.LocalDate)
	 */
	@Override
	public LocalDateInputBuilder min(LocalDate min) {
		getComponent().setMin(min);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#max(java.time.LocalDate)
	 */
	@Override
	public LocalDateInputBuilder max(LocalDate max) {
		getComponent().setMax(max);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#initialPosition(java.time.LocalDate)
	 */
	@Override
	public LocalDateInputBuilder initialPosition(LocalDate initialPosition) {
		getComponent().setInitialPosition(initialPosition);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#weekNumbersVisible(boolean)
	 */
	@Override
	public LocalDateInputBuilder weekNumbersVisible(boolean weekNumbersVisible) {
		getComponent().setWeekNumbersVisible(weekNumbersVisible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#localization(com.holonplatform.vaadin.flow.
	 * components.builders.DateInputBuilder.CalendarLocalization)
	 */
	@Override
	public LocalDateInputBuilder localization(CalendarLocalization localization) {
		this.localization = localization;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#localization()
	 */
	@Override
	public CalendarLocalizationBuilder<LocalDate, LocalDateInputBuilder> localization() {
		return new DefaultCalendarLocalizationBuilder<>(this);
	}

	private static DatePickerI18n localize(CalendarLocalization localization) {
		DatePickerI18n dpi = new DatePickerI18n();
		if (!localization.getMonthNames().isEmpty()) {
			dpi.setMonthNames(localization.getMonthNames().stream().map(m -> LocalizationContext.translate(m, true))
					.collect(Collectors.toList()));
		}
		if (!localization.getWeekdays().isEmpty()) {
			dpi.setWeekdays(localization.getWeekdays().stream().map(m -> LocalizationContext.translate(m, true))
					.collect(Collectors.toList()));
		}
		if (!localization.getWeekdaysShort().isEmpty()) {
			dpi.setWeekdaysShort(localization.getWeekdaysShort().stream()
					.map(m -> LocalizationContext.translate(m, true)).collect(Collectors.toList()));
		}
		if (localization.getFirstDayOfWeek() != null) {
			dpi.setFirstDayOfWeek(localization.getFirstDayOfWeek().intValue());
		}
		localization.getWeek().ifPresent(m -> dpi.setWeek(LocalizationContext.translate(m, true)));
		localization.getCalendar().ifPresent(m -> dpi.setCalendar(LocalizationContext.translate(m, true)));
		localization.getClear().ifPresent(m -> dpi.setClear(LocalizationContext.translate(m, true)));
		localization.getToday().ifPresent(m -> dpi.setToday(LocalizationContext.translate(m, true)));
		localization.getCancel().ifPresent(m -> dpi.setCancel(LocalizationContext.translate(m, true)));
		return dpi;
	}

	static class DefaultCalendarLocalizationBuilder<D, B extends BaseDateInputBuilder<D, B>>
			implements CalendarLocalizationBuilder<D, B> {

		private final B parent;
		private final DefaultCalendarLocalization localization = new DefaultCalendarLocalization();

		public DefaultCalendarLocalizationBuilder(B parent) {
			super();
			ObjectUtils.argumentNotNull(parent, "Parent builder mujst be not null");
			this.parent = parent;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#monthNames(
		 * java.util.List)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> monthNames(List<Localizable> monthNames) {
			localization.setMonthNames(monthNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#weekDays(java.
		 * util.List)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> weekDays(List<Localizable> weekDays) {
			localization.setWeekdays(weekDays);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#weekDaysShort(
		 * java.util.List)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> weekDaysShort(List<Localizable> weekDaysShort) {
			localization.setWeekdaysShort(weekDaysShort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#firstDayOfWeek
		 * (int)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> firstDayOfWeek(int firstDayOfWeek) {
			localization.setFirstDayOfWeek(firstDayOfWeek);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#week(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> week(Localizable message) {
			localization.setWeek(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#calendar(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> calendar(Localizable message) {
			localization.setCalendar(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#clear(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> clear(Localizable message) {
			localization.setClear(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#today(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> today(Localizable message) {
			localization.setToday(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#cancel(com.
		 * holonplatform.core.i18n.Localizable)
		 */
		@Override
		public CalendarLocalizationBuilder<D, B> cancel(Localizable message) {
			localization.setCancel(message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalizationBuilder#set()
		 */
		@Override
		public B set() {
			return parent.localization(localization);
		}

	}

	static class DefaultCalendarLocalization implements CalendarLocalization {

		private List<Localizable> monthNames;
		private List<Localizable> weekdays;
		private List<Localizable> weekdaysShort;
		private Integer firstDayOfWeek;
		private Localizable week;
		private Localizable calendar;
		private Localizable clear;
		private Localizable today;
		private Localizable cancel;

		public void setMonthNames(List<Localizable> monthNames) {
			this.monthNames = monthNames;
		}

		public void setWeekdays(List<Localizable> weekdays) {
			this.weekdays = weekdays;
		}

		public void setWeekdaysShort(List<Localizable> weekdaysShort) {
			this.weekdaysShort = weekdaysShort;
		}

		public void setFirstDayOfWeek(Integer firstDayOfWeek) {
			this.firstDayOfWeek = firstDayOfWeek;
		}

		public void setWeek(Localizable week) {
			this.week = week;
		}

		public void setCalendar(Localizable calendar) {
			this.calendar = calendar;
		}

		public void setClear(Localizable clear) {
			this.clear = clear;
		}

		public void setToday(Localizable today) {
			this.today = today;
		}

		public void setCancel(Localizable cancel) {
			this.cancel = cancel;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getMonthNames()
		 */
		@Override
		public List<Localizable> getMonthNames() {
			return (monthNames != null) ? monthNames : Collections.emptyList();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getWeekdays()
		 */
		@Override
		public List<Localizable> getWeekdays() {
			return (weekdays != null) ? weekdays : Collections.emptyList();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getWeekdaysShort()
		 */
		@Override
		public List<Localizable> getWeekdaysShort() {
			return (weekdaysShort != null) ? weekdaysShort : Collections.emptyList();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getFirstDayOfWeek()
		 */
		@Override
		public Integer getFirstDayOfWeek() {
			return firstDayOfWeek;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getWeek()
		 */
		@Override
		public Optional<Localizable> getWeek() {
			return Optional.ofNullable(week);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getCalendar()
		 */
		@Override
		public Optional<Localizable> getCalendar() {
			return Optional.ofNullable(calendar);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getClear()
		 */
		@Override
		public Optional<Localizable> getClear() {
			return Optional.ofNullable(clear);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getToday()
		 */
		@Override
		public Optional<Localizable> getToday() {
			return Optional.ofNullable(today);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder.CalendarLocalization#getCancel()
		 */
		@Override
		public Optional<Localizable> getCancel() {
			return Optional.ofNullable(cancel);
		}

	}

}
