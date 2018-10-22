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
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLocalDateInputBuilder.DefaultCalendarLocalizationBuilder;
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
 * Default {@link DateInputBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultDateInputBuilder implements DateInputBuilder {

	private final DefaultLocalDateInputBuilder builder = new DefaultLocalDateInputBuilder();

	private final List<ValueChangeListener<Date>> valueChangeListeners = new LinkedList<>();

	private ZoneId timeZone;

	public DefaultDateInputBuilder() {
		super();
	}

	protected LocalDate asLocalDate(Date date) {
		if (date != null) {
			final LocalDateToDateConverter converter = (timeZone != null) ? new LocalDateToDateConverter(timeZone)
					: new LocalDateToDateConverter();
			return converter.convertToPresentation(date, new ValueContext());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<Date> build() {
		final Input<Date> input = Input.from(builder.build(),
				(timeZone != null) ? new LocalDateToDateConverter(timeZone) : new LocalDateToDateConverter());
		valueChangeListeners.forEach(l -> input.addValueChangeListener(l));
		return input;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<Date, ValidatableInput<Date>> validatable() {
		return ValidatableInputBuilder.create(build());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization(
	 * boolean)
	 */
	@Override
	public DateInputBuilder withDeferredLocalization(boolean deferredLocalization) {
		builder.withDeferredLocalization(deferredLocalization);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
	 */
	@Override
	public boolean isDeferredLocalizationEnabled() {
		return builder.isDeferredLocalizationEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DateInputBuilder#timeZone(java.time.ZoneId)
	 */
	@Override
	public DateInputBuilder timeZone(ZoneId zone) {
		this.timeZone = zone;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public DateInputBuilder locale(Locale locale) {
		builder.locale(locale);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#useContextLocale(boolean)
	 */
	@Override
	public DateInputBuilder useContextLocale(boolean useContextLocale) {
		builder.useContextLocale(useContextLocale);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#min(java.time.LocalDate)
	 */
	@Override
	public DateInputBuilder min(Date min) {
		builder.min(asLocalDate(min));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#max(java.time.LocalDate)
	 */
	@Override
	public DateInputBuilder max(Date max) {
		builder.max(asLocalDate(max));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#initialPosition(java.time.LocalDate)
	 */
	@Override
	public DateInputBuilder initialPosition(Date initialPosition) {
		builder.initialPosition(asLocalDate(initialPosition));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#weekNumbersVisible(boolean)
	 */
	@Override
	public DateInputBuilder weekNumbersVisible(boolean weekNumbersVisible) {
		builder.weekNumbersVisible(weekNumbersVisible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#localization(com.holonplatform.vaadin.flow
	 * .components.builders.BaseDateInputBuilder.CalendarLocalization)
	 */
	@Override
	public DateInputBuilder localization(CalendarLocalization localization) {
		builder.localization(localization);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public DateInputBuilder readOnly(boolean readOnly) {
		builder.readOnly(readOnly);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public DateInputBuilder withValue(Date value) {
		builder.withValue(asLocalDate(value));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public DateInputBuilder withValueChangeListener(ValueChangeListener<Date> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public DateInputBuilder required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public DateInputBuilder id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public DateInputBuilder visible(boolean visible) {
		builder.visible(visible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateInputBuilder withAttachListener(ComponentEventListener<AttachEvent> listener) {
		builder.withAttachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateInputBuilder withDetachListener(ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public DateInputBuilder withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public DateInputBuilder withEventListener(String eventType, DomEventListener listener) {
		builder.withEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public DateInputBuilder withEventListener(String eventType, DomEventListener listener, String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public DateInputBuilder width(String width) {
		builder.width(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public DateInputBuilder height(String height) {
		builder.height(height);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public DateInputBuilder styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public DateInputBuilder styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public DateInputBuilder removeStyleName(String styleName) {
		builder.removeStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public DateInputBuilder replaceStyleName(String styleName) {
		builder.replaceStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public DateInputBuilder enabled(boolean enabled) {
		builder.enabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public DateInputBuilder tabIndex(int tabIndex) {
		builder.tabIndex(tabIndex);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateInputBuilder withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		builder.withFocusListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateInputBuilder withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		builder.withBlurListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public DateInputBuilder placeholder(Localizable placeholder) {
		builder.placeholder(placeholder);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public DateInputBuilder label(Localizable label) {
		builder.label(label);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateInputBuilder#localization()
	 */
	@Override
	public CalendarLocalizationBuilder<Date, DateInputBuilder> localization() {
		return new DefaultCalendarLocalizationBuilder<>(this);
	}

}
