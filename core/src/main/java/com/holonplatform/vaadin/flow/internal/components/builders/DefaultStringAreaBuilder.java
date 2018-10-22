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

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.StringAreaBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.HasValueStringInput;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.CompositionEndEvent;
import com.vaadin.flow.component.CompositionStartEvent;
import com.vaadin.flow.component.CompositionUpdateEvent;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.InputEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * Default {@link StringAreaBuilder} implementation using a {@link TextArea} as concrete component.
 *
 * @since 5.2.0
 */
public class DefaultStringAreaBuilder extends AbstractLocalizableComponentConfigurator<TextArea, StringAreaBuilder>
		implements StringAreaBuilder {

	private final List<ValueChangeListener<String>> valueChangeListeners = new LinkedList<>();

	private boolean emptyValuesAsNull = true;

	private boolean blankValuesAsNull = false;

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasAutocompleteConfigurator autocompleteConfigurator;
	protected final DefaultHasAutocapitalizeConfigurator autocapitalizeConfigurator;
	protected final DefaultHasAutocorrectConfigurator autocorrectConfigurator;
	protected final DefaultHasPrefixAndSuffixConfigurator prefixAndSuffixConfigurator;
	protected final DefaultCompositionNotifierConfigurator compositionNotifierConfigurator;
	protected final DefaultInputNotifierConfigurator inputNotifierConfigurator;
	protected final DefaultKeyNotifierConfigurator keyNotifierConfigurator;
	protected final DefaultHasValueChangeModeConfigurator valueChangeModeConfigurator;
	protected final DefaultHasLabelConfigurator<TextArea> labelConfigurator;
	protected final DefaultHasTitleConfigurator<TextArea> titleConfigurator;
	protected final DefaultHasPlaceholderConfigurator<TextArea> placeholderConfigurator;

	public DefaultStringAreaBuilder() {
		super(new TextArea());

		sizeConfigurator = new DefaultHasSizeConfigurator(getComponent());
		styleConfigurator = new DefaultHasStyleConfigurator(getComponent());
		enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
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
			getComponent().getElement().setAttribute("title", (title != null) ? title : "");
		}, this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), placeholder -> {
			getComponent().setPlaceholder(placeholder);
		}, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<String> build() {
		final TextArea component = getComponent();
		final HasValueStringInput input = new HasValueStringInput(component);
		input.setRequiredPropertyHandler(() -> component.isRequired(), required -> component.setRequired(required));
		input.setLabelPropertyHandler(() -> component.getLabel(), label -> component.setLabel(label));
		input.setEmptyValuesAsNull(emptyValuesAsNull);
		input.setBlankValuesAsNull(blankValuesAsNull);
		valueChangeListeners.forEach(l -> input.addValueChangeListener(l));
		return input;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<String, ValidatableInput<String>> validatable() {
		return ValidatableInputBuilder.create(build());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#emptyValuesAsNull(boolean)
	 */
	@Override
	public StringAreaBuilder emptyValuesAsNull(boolean emptyValuesAsNull) {
		this.emptyValuesAsNull = emptyValuesAsNull;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#blankValuesAsNull(boolean)
	 */
	@Override
	public StringAreaBuilder blankValuesAsNull(boolean blankValuesAsNull) {
		this.blankValuesAsNull = blankValuesAsNull;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public StringAreaBuilder readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public StringAreaBuilder withValue(String value) {
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
	public StringAreaBuilder withValueChangeListener(ValueChangeListener<String> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public StringAreaBuilder required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#maxLength(int)
	 */
	@Override
	public StringAreaBuilder maxLength(int maxLength) {
		getComponent().setMaxLength(maxLength);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#minLength(int)
	 */
	@Override
	public StringAreaBuilder minLength(int minLength) {
		getComponent().setMinLength(minLength);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public StringAreaBuilder enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutocompleteConfigurator#autocomplete(com.vaadin.flow.
	 * component.textfield.Autocomplete)
	 */
	@Override
	public StringAreaBuilder autocomplete(Autocomplete autocomplete) {
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
	public StringAreaBuilder autocapitalize(Autocapitalize autocapitalize) {
		autocapitalizeConfigurator.autocapitalize(autocapitalize);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutocorrectConfigurator#autocorrect(boolean)
	 */
	@Override
	public StringAreaBuilder autocorrect(boolean autocorrect) {
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
	public StringAreaBuilder withInputListener(ComponentEventListener<InputEvent> listener) {
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
	public StringAreaBuilder withKeyDownListener(ComponentEventListener<KeyDownEvent> listener) {
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
	public StringAreaBuilder withKeyPressListener(ComponentEventListener<KeyPressEvent> listener) {
		keyNotifierConfigurator.withKeyPressListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyUpListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public StringAreaBuilder withKeyUpListener(ComponentEventListener<KeyUpEvent> listener) {
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
	public StringAreaBuilder withKeyDownListener(Key key, ComponentEventListener<KeyDownEvent> listener,
			KeyModifier... modifiers) {
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
	public StringAreaBuilder withKeyPressListener(Key key, ComponentEventListener<KeyPressEvent> listener,
			KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyPressListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyUpListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public StringAreaBuilder withKeyUpListener(Key key, ComponentEventListener<KeyUpEvent> listener,
			KeyModifier... modifiers) {
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
	public StringAreaBuilder valueChangeMode(ValueChangeMode valueChangeMode) {
		valueChangeModeConfigurator.valueChangeMode(valueChangeMode);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public StringAreaBuilder width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public StringAreaBuilder height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public StringAreaBuilder styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public StringAreaBuilder styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public StringAreaBuilder removeStyleName(String styleName) {
		styleConfigurator.removeStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public StringAreaBuilder replaceStyleName(String styleName) {
		styleConfigurator.replaceStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
	 */
	@Override
	public StringAreaBuilder autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public StringAreaBuilder tabIndex(int tabIndex) {
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
	public StringAreaBuilder withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<TextArea>>() {

			@Override
			public void onComponentEvent(FocusEvent<TextArea> event) {
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
	public StringAreaBuilder withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<TextArea>>() {

			@Override
			public void onComponentEvent(BlurEvent<TextArea> event) {
				listener.onComponentEvent(new BlurEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#prefixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public StringAreaBuilder prefixComponent(Component component) {
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
	public StringAreaBuilder suffixComponent(Component component) {
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
	public StringAreaBuilder withCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener) {
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
	public StringAreaBuilder withCompositionUpdateListener(ComponentEventListener<CompositionUpdateEvent> listener) {
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
	public StringAreaBuilder withCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener) {
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
	public StringAreaBuilder placeholder(Localizable placeholder) {
		placeholderConfigurator.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public StringAreaBuilder label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public StringAreaBuilder title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected StringAreaBuilder getConfigurator() {
		return this;
	}

}
