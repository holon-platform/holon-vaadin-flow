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

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.NumberInputConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.CompositionEndEvent;
import com.vaadin.flow.component.CompositionStartEvent;
import com.vaadin.flow.component.CompositionUpdateEvent;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.InputEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * Base {@link NumberInputConfigurator} implementation using a {@link TextField} as concrete component.
 *
 * @param <T> Number type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public abstract class AbstractNumberInputBuilder<T extends Number, C extends NumberInputConfigurator<T, C>> extends
		AbstractInputConfigurator<T, ValueChangeEvent<T>, TextField, C> implements NumberInputConfigurator<T, C> {

	private final Class<T> numberType;

	private T initialValue;

	private StringToNumberConverter<T> converter;

	protected final DefaultHasAutocompleteConfigurator autocompleteConfigurator;
	protected final DefaultHasPrefixAndSuffixConfigurator prefixAndSuffixConfigurator;
	protected final DefaultCompositionNotifierConfigurator compositionNotifierConfigurator;
	protected final DefaultInputNotifierConfigurator inputNotifierConfigurator;
	protected final DefaultKeyNotifierConfigurator keyNotifierConfigurator;
	protected final DefaultHasValueChangeModeConfigurator valueChangeModeConfigurator;
	protected final DefaultHasLabelConfigurator<TextField> labelConfigurator;
	protected final DefaultHasTitleConfigurator<TextField> titleConfigurator;
	protected final DefaultHasPlaceholderConfigurator<TextField> placeholderConfigurator;

	public AbstractNumberInputBuilder(Class<T> numberType) {
		this(numberType, new TextField(), null, StringToNumberConverter.create(numberType), Collections.emptyList(),
				Collections.emptyList(), InputAdaptersContainer.create());
	}

	public AbstractNumberInputBuilder(Class<T> numberType, TextField component, T initialValue,
			StringToNumberConverter<T> converter,
			List<ValueChangeListener<T, ValueChangeEvent<T>>> valueChangeListeners,
			List<ReadonlyChangeListener> readonlyChangeListeners, InputAdaptersContainer<T> adapters) {
		super(component, adapters);
		ObjectUtils.argumentNotNull(numberType, "Number type must be not null");
		this.numberType = numberType;
		this.initialValue = initialValue;
		this.converter = converter;
		initValueChangeListeners(valueChangeListeners);
		initReadonlyChangeListeners(readonlyChangeListeners);

		getComponent().setAutocapitalize(Autocapitalize.NONE);
		getComponent().setAutocorrect(false);
		getComponent().setAutocomplete(Autocomplete.OFF);

		autocompleteConfigurator = new DefaultHasAutocompleteConfigurator(getComponent());
		prefixAndSuffixConfigurator = new DefaultHasPrefixAndSuffixConfigurator(getComponent());
		compositionNotifierConfigurator = new DefaultCompositionNotifierConfigurator(getComponent());
		inputNotifierConfigurator = new DefaultInputNotifierConfigurator(getComponent());
		keyNotifierConfigurator = new DefaultKeyNotifierConfigurator(getComponent());
		valueChangeModeConfigurator = new DefaultHasValueChangeModeConfigurator(getComponent());

		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
		titleConfigurator = new DefaultHasTitleConfigurator<>(getComponent(), title -> {
			getComponent().setTitle(title);
		}, this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), placeholder -> {
			getComponent().setPlaceholder(placeholder);
		}, this);
	}

	protected T getInitialValue() {
		return initialValue;
	}

	protected Class<T> getNumberType() {
		return numberType;
	}

	/**
	 * Get the {@link StringToNumberConverter} instance.
	 * @return the converter
	 */
	protected StringToNumberConverter<T> getConverter() {
		if (converter == null) {
			converter = StringToNumberConverter.create(numberType);
		}
		return converter;
	}

	protected void replaceConverter(StringToNumberConverter<T> converter) {
		final StringToNumberConverter<T> oldConverter = this.converter;
		this.converter = converter;
		if (oldConverter != null) {
			this.converter.setAllowNegatives(oldConverter.isAllowNegatives());
			this.converter.setMinDecimals(oldConverter.getMinDecimals());
			this.converter.setMaxDecimals(oldConverter.getMaxDecimals());
		}
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
	protected Input<T> buildAsInput() {
		final TextField component = getComponent();

		// pattern
		component.setPattern(getConverter().getValidationPattern());
		component.setPreventInvalidInput(true);

		final Input<String> input = Input.builder(component).emptyValueSupplier(field -> null)
				.requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.titlePropertyHandler((f, c) -> c.getTitle(), (f, c, v) -> c.setTitle(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.focusOperation(f -> f.focus()).hasEnabledSupplier(f -> f).build();

		// conversion
		final Input<T> numberInput = Input.builder(input, getConverter())
				.withValueChangeListeners(getValueChangeListeners())
				.withReadonlyChangeListeners(getReadonlyChangeListeners()).withAdapters(getAdapters()).build();
		if (initialValue != null) {
			numberInput.setValue(initialValue);
		}
		return numberInput;
	}

	/**
	 * Build the component as a {@link ValidatableInput}.
	 * @return The {@link ValidatableInput} instance
	 */
	protected ValidatableInput<T> buildAsValidatableInput() {
		return ValidatableInput.from(buildAsInput());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public C locale(Locale locale) {
		replaceConverter(StringToNumberConverter.create(numberType, locale));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#numberFormat(java.text.NumberFormat)
	 */
	@Override
	public C numberFormat(NumberFormat numberFormat) {
		replaceConverter(StringToNumberConverter.create(numberType, numberFormat));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#numberFormatPattern(java.lang.String)
	 */
	@Override
	public C numberFormatPattern(String numberFormatPattern) {
		replaceConverter(StringToNumberConverter.create(numberType, numberFormatPattern));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#allowNegative(boolean)
	 */
	@Override
	public C allowNegative(boolean allowNegative) {
		getConverter().setAllowNegatives(allowNegative);
		return getConfigurator();
	}

	@Override
	public C useGrouping(boolean useGrouping) {
		getConverter().setUseGrouping(useGrouping);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#minDecimals(int)
	 */
	@Override
	public C minDecimals(int minDecimals) {
		getConverter().setMinDecimals(minDecimals);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#maxDecimals(int)
	 */
	@Override
	public C maxDecimals(int maxDecimals) {
		getConverter().setMaxDecimals(maxDecimals);
		return getConfigurator();
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
	public C withValue(T value) {
		this.initialValue = value;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#pattern(java.lang.String)
	 */
	@Override
	public C pattern(String pattern) {
		getComponent().setPattern(pattern);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#preventInvalidInput(boolean)
	 */
	@Override
	public C preventInvalidInput(boolean preventInvalidInput) {
		getComponent().setPreventInvalidInput(preventInvalidInput);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutocompleteConfigurator#autocomplete(com.vaadin.flow.
	 * component.textfield.Autocomplete)
	 */
	@Override
	public C autocomplete(Autocomplete autocomplete) {
		autocompleteConfigurator.autocomplete(autocomplete);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputNotifierConfigurator#withInputListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withInputListener(ComponentEventListener<InputEvent> listener) {
		inputNotifierConfigurator.withInputListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyDownListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withKeyDownListener(ComponentEventListener<KeyDownEvent> listener) {
		keyNotifierConfigurator.withKeyDownListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyPressListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withKeyPressListener(ComponentEventListener<KeyPressEvent> listener) {
		keyNotifierConfigurator.withKeyPressListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyUpListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withKeyUpListener(ComponentEventListener<KeyUpEvent> listener) {
		keyNotifierConfigurator.withKeyUpListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyDownListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public C withKeyDownListener(Key key, ComponentEventListener<KeyDownEvent> listener, KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyDownListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyPressListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public C withKeyPressListener(Key key, ComponentEventListener<KeyPressEvent> listener, KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyPressListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyUpListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public C withKeyUpListener(Key key, ComponentEventListener<KeyUpEvent> listener, KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyUpListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueChangeModeConfigurator#valueChangeMode(com.vaadin.flow.
	 * data.value.ValueChangeMode)
	 */
	@Override
	public C valueChangeMode(ValueChangeMode valueChangeMode) {
		valueChangeModeConfigurator.valueChangeMode(valueChangeMode);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
	 */
	@Override
	public C autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
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
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<TextField>>() {

			@Override
			public void onComponentEvent(FocusEvent<TextField> event) {
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
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<TextField>>() {

			@Override
			public void onComponentEvent(BlurEvent<TextField> event) {
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
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#prefixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public C prefixComponent(Component component) {
		prefixAndSuffixConfigurator.prefixComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#suffixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public C suffixComponent(Component component) {
		prefixAndSuffixConfigurator.suffixComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.CompositionNotifierConfigurator#withCompositionStartListener(
	 * com.vaadin.flow.component.ComponentEventListener)
	 */
	@Override
	public C withCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener) {
		compositionNotifierConfigurator.withCompositionStartListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.CompositionNotifierConfigurator#withCompositionUpdateListener(
	 * com.vaadin.flow.component.ComponentEventListener)
	 */
	@Override
	public C withCompositionUpdateListener(ComponentEventListener<CompositionUpdateEvent> listener) {
		compositionNotifierConfigurator.withCompositionUpdateListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.CompositionNotifierConfigurator#withCompositionEndListener(com.
	 * vaadin.flow.component.ComponentEventListener)
	 */
	@Override
	public C withCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener) {
		compositionNotifierConfigurator.withCompositionEndListener(listener);
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
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasThemeVariantConfigurator#withThemeVariants(java.lang.Enum[])
	 */
	@Override
	public C withThemeVariants(TextFieldVariant... variants) {
		getComponent().addThemeVariants(variants);
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
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputConfigurator#autoselect(boolean)
	 */
	@Override
	public C autoselect(boolean autoselect) {
		getComponent().setAutoselect(autoselect);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputConfigurator#clearButtonVisible(boolean)
	 */
	@Override
	public C clearButtonVisible(boolean clearButtonVisible) {
		getComponent().setClearButtonVisible(clearButtonVisible);
		return getConfigurator();
	}

}
