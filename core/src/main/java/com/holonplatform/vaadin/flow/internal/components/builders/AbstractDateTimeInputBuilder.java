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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractLocalDateTimeInputBuilder.DefaultCalendarLocalizationBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.flow.dom.DomEventListener;

/**
 * Base {@link DateTimeInputConfigurator}.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public abstract class AbstractDateTimeInputBuilder<C extends DateTimeInputConfigurator<C>>
		implements DateTimeInputConfigurator<C> {

	private final List<ValueChangeListener<Date, ValueChangeEvent<Date>>> valueChangeListeners = new LinkedList<>();

	private final DefaultLocalDateTimeInputBuilder localDateTimeInputBuilder;

	private ZoneId timeZone;

	public AbstractDateTimeInputBuilder() {
		this(new DefaultLocalDateTimeInputBuilder(), null, Collections.emptyList());
	}

	public AbstractDateTimeInputBuilder(DefaultLocalDateTimeInputBuilder localDateTimeInputBuilder, ZoneId timeZone,
			List<ValueChangeListener<Date, ValueChangeEvent<Date>>> valueChangeListeners) {
		super();
		this.localDateTimeInputBuilder = localDateTimeInputBuilder;
		this.timeZone = timeZone;
		valueChangeListeners.forEach(l -> this.valueChangeListeners.add(l));
	}

	/**
	 * Get the actual configurator.
	 * @return the actual configurator
	 */
	protected abstract C getConfigurator();

	protected DefaultLocalDateTimeInputBuilder getLocalDateTimeInputBuilder() {
		return localDateTimeInputBuilder;
	}

	protected List<ValueChangeListener<Date, ValueChangeEvent<Date>>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	protected ZoneId getTimeZone() {
		return timeZone;
	}

	protected LocalDateTime asLocalDateTime(Date date) {
		if (date != null) {
			final LocalDateTimeToDateConverter converter = (timeZone != null)
					? new LocalDateTimeToDateConverter(timeZone)
					: new LocalDateTimeToDateConverter(ZoneId.systemDefault());
			return converter.convertToPresentation(date, new ValueContext());
		}
		return null;
	}

	/**
	 * Build the component as an {@link Input}.
	 * @return The {@link Input} instance
	 */
	protected Input<Date> buildAsInput() {
		final LocalDateTimeToDateConverter converter = (timeZone != null) ? new LocalDateTimeToDateConverter(timeZone)
				: new LocalDateTimeToDateConverter(ZoneId.systemDefault());
		final Input<Date> input = Input.from(localDateTimeInputBuilder.build(), converter);
		valueChangeListeners.forEach(l -> input.addValueChangeListener(l));
		return input;
	}

	/**
	 * Build the component as a {@link ValidatableInput}.
	 * @return The {@link ValidatableInput} instance
	 */
	protected ValidatableInput<Date> buildAsValidatableInput() {
		return ValidatableInput.from(buildAsInput());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization(
	 * boolean)
	 */
	@Override
	public C withDeferredLocalization(boolean deferredLocalization) {
		localDateTimeInputBuilder.withDeferredLocalization(deferredLocalization);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
	 */
	@Override
	public boolean isDeferredLocalizationEnabled() {
		return localDateTimeInputBuilder.isDeferredLocalizationEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder#timeZone(java.time.ZoneId)
	 */
	@Override
	public C timeZone(ZoneId zone) {
		this.timeZone = zone;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#spacing(boolean)
	 */
	@Override
	public C spacing(boolean spacing) {
		localDateTimeInputBuilder.spacing(spacing);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#timeSeparator(char)
	 */
	@Override
	public C timeSeparator(char timeSeparator) {
		localDateTimeInputBuilder.timeSeparator(timeSeparator);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#timeInputWidth(java.lang.String)
	 */
	@Override
	public C timeInputWidth(String timeInputWidth) {
		localDateTimeInputBuilder.timeInputWidth(timeInputWidth);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#timePlaceholder(java.util.function.
	 * Function)
	 */
	@Override
	public C timePlaceholder(Function<Character, String> timePlaceholder) {
		localDateTimeInputBuilder.timePlaceholder(timePlaceholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public C locale(Locale locale) {
		localDateTimeInputBuilder.locale(locale);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#updateLocaleOnAttach(boolean)
	 */
	@Override
	public C updateLocaleOnAttach(boolean updateLocaleOnAttach) {
		localDateTimeInputBuilder.updateLocaleOnAttach(updateLocaleOnAttach);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#min(java.time.LocalDate)
	 */
	@Override
	public C min(Date min) {
		localDateTimeInputBuilder.min(asLocalDateTime(min));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#max(java.time.LocalDate)
	 */
	@Override
	public C max(Date max) {
		localDateTimeInputBuilder.max(asLocalDateTime(max));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#initialPosition(java.time.LocalDate)
	 */
	@Override
	public C initialPosition(Date initialPosition) {
		localDateTimeInputBuilder.initialPosition(asLocalDateTime(initialPosition));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#weekNumbersVisible(boolean)
	 */
	@Override
	public C weekNumbersVisible(boolean weekNumbersVisible) {
		localDateTimeInputBuilder.weekNumbersVisible(weekNumbersVisible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#localization(com.holonplatform.vaadin.
	 * flow .components.builders.BaseDateTimeInputlocalDateTimeInputBuilder.CalendarLocalization)
	 */
	@Override
	public C localization(CalendarLocalization localization) {
		localDateTimeInputBuilder.localization(localization);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public C readOnly(boolean readOnly) {
		localDateTimeInputBuilder.readOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public C withValue(Date value) {
		localDateTimeInputBuilder.withValue(asLocalDateTime(value));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public C withValueChangeListener(ValueChangeListener<Date, ValueChangeEvent<Date>> listener) {
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
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public C id(String id) {
		localDateTimeInputBuilder.id(id);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public C visible(boolean visible) {
		localDateTimeInputBuilder.visible(visible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withAttachListener(ComponentEventListener<AttachEvent> listener) {
		localDateTimeInputBuilder.withAttachListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withDetachListener(ComponentEventListener<DetachEvent> listener) {
		localDateTimeInputBuilder.withDetachListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public C withThemeName(String themeName) {
		localDateTimeInputBuilder.withThemeName(themeName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public C withEventListener(String eventType, DomEventListener listener) {
		localDateTimeInputBuilder.withEventListener(eventType, listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public C withEventListener(String eventType, DomEventListener listener, String filter) {
		localDateTimeInputBuilder.withEventListener(eventType, listener, filter);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public C width(String width) {
		localDateTimeInputBuilder.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public C height(String height) {
		localDateTimeInputBuilder.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
	 */
	@Override
	public C minWidth(String minWidth) {
		localDateTimeInputBuilder.minWidth(minWidth);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
	 */
	@Override
	public C maxWidth(String maxWidth) {
		localDateTimeInputBuilder.maxWidth(maxWidth);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
	 */
	@Override
	public C minHeight(String minHeight) {
		localDateTimeInputBuilder.minHeight(minHeight);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
	 */
	@Override
	public C maxHeight(String maxHeight) {
		localDateTimeInputBuilder.maxHeight(maxHeight);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		localDateTimeInputBuilder.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		localDateTimeInputBuilder.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public C enabled(boolean enabled) {
		localDateTimeInputBuilder.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public C tabIndex(int tabIndex) {
		localDateTimeInputBuilder.tabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		localDateTimeInputBuilder.withFocusListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		localDateTimeInputBuilder.withBlurListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public C placeholder(Localizable placeholder) {
		localDateTimeInputBuilder.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C label(Localizable label) {
		localDateTimeInputBuilder.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#localization()
	 */
	@Override
	public CalendarLocalizationBuilder<Date, C> localization() {
		return new DefaultCalendarLocalizationBuilder<>(getConfigurator());
	}

}
