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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.StringInputIsEmptySupplier;
import com.holonplatform.vaadin.flow.internal.components.support.StringInputValueSupplier;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * Base {@link PasswordInputConfigurator} implementation using a {@link PasswordField} as concrete component.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public abstract class AbstractPasswordInputBuilder<C extends PasswordInputConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<PasswordField, C> implements PasswordInputConfigurator<C> {

	private final List<ValueChangeListener<String, ValueChangeEvent<String>>> valueChangeListeners = new LinkedList<>();

	private boolean emptyValuesAsNull = true;

	private boolean blankValuesAsNull = false;

	protected final DefaultHasAutocompleteConfigurator autocompleteConfigurator;
	protected final DefaultHasAutocapitalizeConfigurator autocapitalizeConfigurator;
	protected final DefaultHasAutocorrectConfigurator autocorrectConfigurator;
	protected final DefaultHasPrefixAndSuffixConfigurator prefixAndSuffixConfigurator;
	protected final DefaultCompositionNotifierConfigurator compositionNotifierConfigurator;
	protected final DefaultInputNotifierConfigurator inputNotifierConfigurator;
	protected final DefaultKeyNotifierConfigurator keyNotifierConfigurator;
	protected final DefaultHasValueChangeModeConfigurator valueChangeModeConfigurator;
	protected final DefaultHasLabelConfigurator<PasswordField> labelConfigurator;
	protected final DefaultHasTitleConfigurator<PasswordField> titleConfigurator;
	protected final DefaultHasPlaceholderConfigurator<PasswordField> placeholderConfigurator;

	public AbstractPasswordInputBuilder() {
		this(new PasswordField(), true, false, Collections.emptyList());
	}

	public AbstractPasswordInputBuilder(PasswordField component, boolean emptyValuesAsNull, boolean blankValuesAsNull,
			List<ValueChangeListener<String, ValueChangeEvent<String>>> valueChangeListeners) {
		super(component);

		this.emptyValuesAsNull = emptyValuesAsNull;
		this.blankValuesAsNull = blankValuesAsNull;
		valueChangeListeners.forEach(l -> this.valueChangeListeners.add(l));

		autocompleteConfigurator = new DefaultHasAutocompleteConfigurator(getComponent());
		autocapitalizeConfigurator = new DefaultHasAutocapitalizeConfigurator(getComponent());
		autocorrectConfigurator = new DefaultHasAutocorrectConfigurator(getComponent());
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

	protected boolean isEmptyValuesAsNull() {
		return emptyValuesAsNull;
	}

	protected boolean isBlankValuesAsNull() {
		return blankValuesAsNull;
	}

	protected List<ValueChangeListener<String, ValueChangeEvent<String>>> getValueChangeListeners() {
		return valueChangeListeners;
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
	protected Input<String> buildAsInput() {
		return Input.builder(getComponent())
				.requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.titlePropertyHandler((f, c) -> c.getTitle(), (f, c, v) -> c.setTitle(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.isEmptySupplier(new StringInputIsEmptySupplier<>(emptyValuesAsNull, blankValuesAsNull))
				.valueSupplier(new StringInputValueSupplier<>(emptyValuesAsNull, blankValuesAsNull))
				.focusOperation(f -> f.focus()).hasEnabledSupplier(f -> f)
				.withValueChangeListeners(valueChangeListeners).build();
	}

	/**
	 * Build the component as a {@link ValidatableInput}.
	 * @return The {@link ValidatableInput} instance
	 */
	protected ValidatableInput<String> buildAsValidatableInput() {
		return ValidatableInput.from(buildAsInput());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#emptyValuesAsNull(boolean)
	 */
	@Override
	public C emptyValuesAsNull(boolean emptyValuesAsNull) {
		this.emptyValuesAsNull = emptyValuesAsNull;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#blankValuesAsNull(boolean)
	 */
	@Override
	public C blankValuesAsNull(boolean blankValuesAsNull) {
		this.blankValuesAsNull = blankValuesAsNull;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#autoselect(boolean)
	 */
	@Override
	public C autoselect(boolean autoselect) {
		getComponent().setAutoselect(autoselect);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#clearButtonVisible(boolean)
	 */
	@Override
	public C clearButtonVisible(boolean clearButtonVisible) {
		getComponent().setClearButtonVisible(clearButtonVisible);
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
	public C withValue(String value) {
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
	public C withValueChangeListener(ValueChangeListener<String, ValueChangeEvent<String>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#maxLength(int)
	 */
	@Override
	public C maxLength(int maxLength) {
		getComponent().setMaxLength(maxLength);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#minLength(int)
	 */
	@Override
	public C minLength(int minLength) {
		getComponent().setMinLength(minLength);
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
	 * com.holonplatform.vaadin.flow.components.builders.HasAutocapitalizeConfigurator#autocapitalize(com.vaadin.flow.
	 * component.textfield.Autocapitalize)
	 */
	@Override
	public C autocapitalize(Autocapitalize autocapitalize) {
		autocapitalizeConfigurator.autocapitalize(autocapitalize);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutocorrectConfigurator#autocorrect(boolean)
	 */
	@Override
	public C autocorrect(boolean autocorrect) {
		autocorrectConfigurator.autocorrect(autocorrect);
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
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<PasswordField>>() {

			@Override
			public void onComponentEvent(FocusEvent<PasswordField> event) {
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
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<PasswordField>>() {

			@Override
			public void onComponentEvent(BlurEvent<PasswordField> event) {
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
	 * @see com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder#revealButtonVisible(boolean)
	 */
	@Override
	public C revealButtonVisible(boolean revealButtonVisible) {
		getComponent().setRevealButtonVisible(revealButtonVisible);
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

}
