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
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.DateInputConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractLocalDateInputBuilder.DefaultCalendarLocalizationBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.dom.DomEventListener;

/**
 * Base {@link DateInputConfigurator}.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public abstract class AbstractDateInputBuilder<C extends DateInputConfigurator<C>> implements DateInputConfigurator<C> {

	private final List<ValueChangeListener<Date, ValueChangeEvent<Date>>> valueChangeListeners = new LinkedList<>();

	private final DefaultLocalDateInputBuilder localDateInputBuilder;

	private ZoneId timeZone;

	public AbstractDateInputBuilder() {
		this(new DefaultLocalDateInputBuilder(), null, Collections.emptyList());
	}

	public AbstractDateInputBuilder(DefaultLocalDateInputBuilder localDateInputBuilder, ZoneId timeZone,
			List<ValueChangeListener<Date, ValueChangeEvent<Date>>> valueChangeListeners) {
		super();
		this.localDateInputBuilder = localDateInputBuilder;
		this.timeZone = timeZone;
		valueChangeListeners.forEach(l -> this.valueChangeListeners.add(l));
	}

	/**
	 * Get the actual configurator.
	 * @return the actual configurator
	 */
	protected abstract C getConfigurator();

	protected DefaultLocalDateInputBuilder getLocalDateInputBuilder() {
		return localDateInputBuilder;
	}

	protected List<ValueChangeListener<Date, ValueChangeEvent<Date>>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	protected ZoneId getTimeZone() {
		return timeZone;
	}

	protected LocalDate asLocalDate(Date date) {
		if (date != null) {
			final LocalDateToDateConverter converter = (timeZone != null) ? new LocalDateToDateConverter(timeZone)
					: new LocalDateToDateConverter();
			return converter.convertToPresentation(date, new ValueContext());
		}
		return null;
	}

	/**
	 * Build the component as an {@link Input}.
	 * @return The {@link Input} instance
	 */
	protected Input<Date> buildAsInput() {
		final Input<Date> input = Input.from(localDateInputBuilder.build(),
				(timeZone != null) ? new LocalDateToDateConverter(timeZone) : new LocalDateToDateConverter());
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
		localDateInputBuilder.withDeferredLocalization(deferredLocalization);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
	 */
	@Override
	public boolean isDeferredLocalizationEnabled() {
		return localDateInputBuilder.isDeferredLocalizationEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#timeZone(java.time.ZoneId)
	 */
	@Override
	public C timeZone(ZoneId zone) {
		this.timeZone = zone;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public C locale(Locale locale) {
		localDateInputBuilder.locale(locale);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#updateLocaleOnAttach(boolean)
	 */
	@Override
	public C updateLocaleOnAttach(boolean updateLocaleOnAttach) {
		localDateInputBuilder.updateLocaleOnAttach(updateLocaleOnAttach);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#min(java.time.LocalDate)
	 */
	@Override
	public C min(Date min) {
		localDateInputBuilder.min(asLocalDate(min));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#max(java.time.LocalDate)
	 */
	@Override
	public C max(Date max) {
		localDateInputBuilder.max(asLocalDate(max));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#initialPosition(java.time.LocalDate)
	 */
	@Override
	public C initialPosition(Date initialPosition) {
		localDateInputBuilder.initialPosition(asLocalDate(initialPosition));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#weekNumbersVisible(boolean)
	 */
	@Override
	public C weekNumbersVisible(boolean weekNumbersVisible) {
		localDateInputBuilder.weekNumbersVisible(weekNumbersVisible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#localization(com.holonplatform.vaadin.flow
	 * .components.builders.BaseDateInputlocalDateInputBuilder.CalendarLocalization)
	 */
	@Override
	public C localization(CalendarLocalization localization) {
		localDateInputBuilder.localization(localization);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public C readOnly(boolean readOnly) {
		localDateInputBuilder.readOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public C withValue(Date value) {
		localDateInputBuilder.withValue(asLocalDate(value));
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
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public C id(String id) {
		localDateInputBuilder.id(id);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public C visible(boolean visible) {
		localDateInputBuilder.visible(visible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withAttachListener(ComponentEventListener<AttachEvent> listener) {
		localDateInputBuilder.withAttachListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withDetachListener(ComponentEventListener<DetachEvent> listener) {
		localDateInputBuilder.withDetachListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public C withThemeName(String themeName) {
		localDateInputBuilder.withThemeName(themeName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public C withEventListener(String eventType, DomEventListener listener) {
		localDateInputBuilder.withEventListener(eventType, listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public C withEventListener(String eventType, DomEventListener listener, String filter) {
		localDateInputBuilder.withEventListener(eventType, listener, filter);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public C width(String width) {
		localDateInputBuilder.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public C height(String height) {
		localDateInputBuilder.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		localDateInputBuilder.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		localDateInputBuilder.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public C enabled(boolean enabled) {
		localDateInputBuilder.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public C tabIndex(int tabIndex) {
		localDateInputBuilder.tabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		localDateInputBuilder.withFocusListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		localDateInputBuilder.withBlurListener(listener);
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
		localDateInputBuilder.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C label(Localizable label) {
		localDateInputBuilder.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#localization()
	 */
	@Override
	public CalendarLocalizationBuilder<Date, C> localization() {
		return new DefaultCalendarLocalizationBuilder<>(getConfigurator());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required()
	 */
	@Override
	public C required() {
		return required(true);
	}

}
