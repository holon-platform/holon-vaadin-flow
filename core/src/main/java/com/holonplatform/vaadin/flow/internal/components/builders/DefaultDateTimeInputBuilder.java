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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLocalDateTimeInputBuilder.DefaultCalendarLocalizationBuilder;
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
 * Default {@link DateTimeInputBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultDateTimeInputBuilder implements DateTimeInputBuilder {

	private final DefaultLocalDateTimeInputBuilder builder = new DefaultLocalDateTimeInputBuilder();

	private final List<ValueChangeListener<Date>> valueChangeListeners = new LinkedList<>();

	private ZoneId timeZone;

	public DefaultDateTimeInputBuilder() {
		super();
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<Date> build() {
		final LocalDateTimeToDateConverter converter = (timeZone != null) ? new LocalDateTimeToDateConverter(timeZone)
				: new LocalDateTimeToDateConverter(ZoneId.systemDefault());
		final Input<Date> input = Input.from(builder.build(), converter);
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
	public DateTimeInputBuilder withDeferredLocalization(boolean deferredLocalization) {
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
	 * @see com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder#timeZone(java.time.ZoneId)
	 */
	@Override
	public DateTimeInputBuilder timeZone(ZoneId zone) {
		this.timeZone = zone;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#spacing(boolean)
	 */
	@Override
	public DateTimeInputBuilder spacing(boolean spacing) {
		builder.spacing(spacing);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#timeSeparator(char)
	 */
	@Override
	public DateTimeInputBuilder timeSeparator(char timeSeparator) {
		builder.timeSeparator(timeSeparator);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#timeInputWidth(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder timeInputWidth(String timeInputWidth) {
		builder.timeInputWidth(timeInputWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasTimeInputConfigurator#timePlaceholder(java.util.function.
	 * Function)
	 */
	@Override
	public DateTimeInputBuilder timePlaceholder(Function<Character, String> timePlaceholder) {
		builder.timePlaceholder(timePlaceholder);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public DateTimeInputBuilder locale(Locale locale) {
		builder.locale(locale);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#useContextLocale(boolean)
	 */
	@Override
	public DateTimeInputBuilder useContextLocale(boolean useContextLocale) {
		builder.useContextLocale(useContextLocale);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#min(java.time.LocalDate)
	 */
	@Override
	public DateTimeInputBuilder min(Date min) {
		builder.min(asLocalDateTime(min));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#max(java.time.LocalDate)
	 */
	@Override
	public DateTimeInputBuilder max(Date max) {
		builder.max(asLocalDateTime(max));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#initialPosition(java.time.LocalDate)
	 */
	@Override
	public DateTimeInputBuilder initialPosition(Date initialPosition) {
		builder.initialPosition(asLocalDateTime(initialPosition));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#weekNumbersVisible(boolean)
	 */
	@Override
	public DateTimeInputBuilder weekNumbersVisible(boolean weekNumbersVisible) {
		builder.weekNumbersVisible(weekNumbersVisible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#localization(com.holonplatform.vaadin.
	 * flow .components.builders.BaseDateTimeInputBuilder.CalendarLocalization)
	 */
	@Override
	public DateTimeInputBuilder localization(CalendarLocalization localization) {
		builder.localization(localization);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public DateTimeInputBuilder readOnly(boolean readOnly) {
		builder.readOnly(readOnly);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public DateTimeInputBuilder withValue(Date value) {
		builder.withValue(asLocalDateTime(value));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public DateTimeInputBuilder withValueChangeListener(ValueChangeListener<Date> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public DateTimeInputBuilder required(boolean required) {
		builder.required(required);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder id(String id) {
		builder.id(id);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public DateTimeInputBuilder visible(boolean visible) {
		builder.visible(visible);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateTimeInputBuilder withAttachListener(ComponentEventListener<AttachEvent> listener) {
		builder.withAttachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateTimeInputBuilder withDetachListener(ComponentEventListener<DetachEvent> listener) {
		builder.withDetachListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder withThemeName(String themeName) {
		builder.withThemeName(themeName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public DateTimeInputBuilder withEventListener(String eventType, DomEventListener listener) {
		builder.withEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder withEventListener(String eventType, DomEventListener listener, String filter) {
		builder.withEventListener(eventType, listener, filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder width(String width) {
		builder.width(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder height(String height) {
		builder.height(height);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public DateTimeInputBuilder styleNames(String... styleNames) {
		builder.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder styleName(String styleName) {
		builder.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder removeStyleName(String styleName) {
		builder.removeStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public DateTimeInputBuilder replaceStyleName(String styleName) {
		builder.replaceStyleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public DateTimeInputBuilder enabled(boolean enabled) {
		builder.enabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public DateTimeInputBuilder tabIndex(int tabIndex) {
		builder.tabIndex(tabIndex);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateTimeInputBuilder withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		builder.withFocusListener(listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public DateTimeInputBuilder withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
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
	public DateTimeInputBuilder placeholder(Localizable placeholder) {
		builder.placeholder(placeholder);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public DateTimeInputBuilder label(Localizable label) {
		builder.label(label);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.BaseDateTimeInputBuilder#localization()
	 */
	@Override
	public CalendarLocalizationBuilder<Date, DateTimeInputBuilder> localization() {
		return new DefaultCalendarLocalizationBuilder<>(this);
	}

}
