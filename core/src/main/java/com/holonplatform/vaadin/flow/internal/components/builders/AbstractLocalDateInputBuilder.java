/*
 * Copyright 2016-2019 Axioma srl.
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.BaseTemporalInputConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultInvalidChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.support.RegistrationAdapter;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.shared.Registration;

/**
 * Base {@link LocalDateInputConfigurator} implementation using a {@link DatePicker} as concrete component.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public abstract class AbstractLocalDateInputBuilder<C extends LocalDateInputConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<DatePicker, C> implements LocalDateInputConfigurator<C> {

	private final List<ValueChangeListener<LocalDate, ValueChangeEvent<LocalDate>>> valueChangeListeners = new LinkedList<>();

	protected final DefaultHasLabelConfigurator<DatePicker> labelConfigurator;
	protected final DefaultHasPlaceholderConfigurator<DatePicker> placeholderConfigurator;

	private Registration contextLocaleOnAttachRegistration;

	private CalendarLocalization localization;

	public AbstractLocalDateInputBuilder() {
		this(new DatePicker(), null, null, Collections.emptyList());
	}

	public AbstractLocalDateInputBuilder(DatePicker component, Registration contextLocaleOnAttachRegistration,
			CalendarLocalization localization,
			List<ValueChangeListener<LocalDate, ValueChangeEvent<LocalDate>>> valueChangeListeners) {
		super(component);

		this.contextLocaleOnAttachRegistration = contextLocaleOnAttachRegistration;
		this.localization = localization;
		valueChangeListeners.forEach(l -> this.valueChangeListeners.add(l));

		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), placeholder -> {
			getComponent().setPlaceholder(placeholder);
		}, this);
	}

	protected List<ValueChangeListener<LocalDate, ValueChangeEvent<LocalDate>>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	protected Registration getContextLocaleOnAttachRegistration() {
		return contextLocaleOnAttachRegistration;
	}

	protected CalendarLocalization getLocalization() {
		return localization;
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	/**
	 * Build the component as an {@link Input}.
	 * @return The {@link Input} instance
	 */
	protected Input<LocalDate> buildAsInput() {
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
				.invalidChangeEventNotifierSupplier(
						f -> listener -> RegistrationAdapter.adapt(f.addInvalidChangeListener(e -> listener
								.onInvalidChangeEvent(new DefaultInvalidChangeEvent(e.isFromClient(), f)))))
				.withValueChangeListeners(valueChangeListeners).build();
	}

	/**
	 * Build the component as a {@link ValidatableInput}.
	 * @return The {@link ValidatableInput} instance
	 */
	protected ValidatableInput<LocalDate> buildAsValidatableInput() {
		return ValidatableInput.from(buildAsInput());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public C readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public C withValue(LocalDate value) {
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
	public C withValueChangeListener(ValueChangeListener<LocalDate, ValueChangeEvent<LocalDate>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public C required() {
		return required(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public C tabIndex(int tabIndex) {
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
	public C withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
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
	public C withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
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
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusShortcut(com.vaadin.flow.
	 * component.Key)
	 */
	@Override
	public ShortcutConfigurator<C> withFocusShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addFocusShortcut(key), getConfigurator());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public C placeholder(Localizable placeholder) {
		placeholderConfigurator.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public C locale(Locale locale) {
		getComponent().setLocale(locale);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#updateLocaleOnAttach(boolean)
	 */
	@Override
	public C updateLocaleOnAttach(boolean updateLocaleOnAttach) {
		// unregister previous
		if (contextLocaleOnAttachRegistration != null) {
			contextLocaleOnAttachRegistration.remove();
			contextLocaleOnAttachRegistration = null;
		}
		contextLocaleOnAttachRegistration = getComponent().addAttachListener(e -> {
			if (e.isInitialAttach()) {
				Locale componentLocale = getComponent().getLocale();
				Locale currentLocale = LocalizationProvider.getCurrentLocale().orElse(null);
				if (currentLocale != null && !Objects.equals(currentLocale, componentLocale)) {
					getComponent().setLocale(currentLocale);
				}
			}
		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#min(java.time.LocalDate)
	 */
	@Override
	public C min(LocalDate min) {
		getComponent().setMin(min);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#max(java.time.LocalDate)
	 */
	@Override
	public C max(LocalDate max) {
		getComponent().setMax(max);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#initialPosition(java.time.LocalDate)
	 */
	@Override
	public C initialPosition(LocalDate initialPosition) {
		getComponent().setInitialPosition(initialPosition);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#weekNumbersVisible(boolean)
	 */
	@Override
	public C weekNumbersVisible(boolean weekNumbersVisible) {
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
	public C localization(CalendarLocalization localization) {
		this.localization = localization;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#localization()
	 */
	@Override
	public CalendarLocalizationBuilder<LocalDate, C> localization() {
		return new DefaultCalendarLocalizationBuilder<>(getConfigurator());
	}

	private static DatePickerI18n localize(CalendarLocalization localization) {
		DatePickerI18n dpi = new DatePickerI18n();
		if (!localization.getMonthNames().isEmpty()) {
			dpi.setMonthNames(localization.getMonthNames().stream()
					.map(m -> LocalizationProvider.localize(m).orElse("")).collect(Collectors.toList()));
		}
		if (!localization.getWeekdays().isEmpty()) {
			dpi.setWeekdays(localization.getWeekdays().stream().map(m -> LocalizationProvider.localize(m).orElse(""))
					.collect(Collectors.toList()));
		}
		if (!localization.getWeekdaysShort().isEmpty()) {
			dpi.setWeekdaysShort(localization.getWeekdaysShort().stream()
					.map(m -> LocalizationProvider.localize(m).orElse("")).collect(Collectors.toList()));
		}
		if (localization.getFirstDayOfWeek() != null) {
			dpi.setFirstDayOfWeek(localization.getFirstDayOfWeek().intValue());
		}
		localization.getWeek().ifPresent(m -> dpi.setWeek(LocalizationProvider.localize(m).orElse("")));
		localization.getCalendar().ifPresent(m -> dpi.setCalendar(LocalizationProvider.localize(m).orElse("")));
		localization.getClear().ifPresent(m -> dpi.setClear(LocalizationProvider.localize(m).orElse("")));
		localization.getToday().ifPresent(m -> dpi.setToday(LocalizationProvider.localize(m).orElse("")));
		localization.getCancel().ifPresent(m -> dpi.setCancel(LocalizationProvider.localize(m).orElse("")));
		return dpi;
	}

	static class DefaultCalendarLocalizationBuilder<D, B extends BaseTemporalInputConfigurator<D, B>>
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
