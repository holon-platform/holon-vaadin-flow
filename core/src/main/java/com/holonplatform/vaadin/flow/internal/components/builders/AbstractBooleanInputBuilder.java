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
import com.holonplatform.vaadin.flow.components.builders.BooleanInputConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
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
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.checkbox.Checkbox;

/**
 * Base {@link BooleanInputConfigurator} implementation using a {@link Checkbox} as concrete component.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.2
 */
public abstract class AbstractBooleanInputBuilder<C extends BooleanInputConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<Checkbox, C> implements BooleanInputConfigurator<C> {

	private final List<ValueChangeListener<Boolean, ValueChangeEvent<Boolean>>> valueChangeListeners = new LinkedList<>();

	protected final DefaultHasLabelConfigurator<Checkbox> labelConfigurator;

	public AbstractBooleanInputBuilder() {
		this(new Checkbox(), Collections.emptyList());
	}

	public AbstractBooleanInputBuilder(Checkbox component,
			List<ValueChangeListener<Boolean, ValueChangeEvent<Boolean>>> valueChangeListeners) {
		super(component);

		valueChangeListeners.forEach(l -> this.valueChangeListeners.add(l));

		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
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

	protected List<ValueChangeListener<Boolean, ValueChangeEvent<Boolean>>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	/**
	 * Build the component as an {@link Input}.
	 * @return The {@link Input} instance
	 */
	protected Input<Boolean> buildAsInput() {
		return Input.builder(getComponent()).labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.withValueChangeListeners(valueChangeListeners).focusOperation(f -> f.focus())
				.hasEnabledSupplier(f -> f).build();
	}

	/**
	 * Build the component as a {@link ValidatableInput}.
	 * @return The {@link ValidatableInput} instance
	 */
	protected ValidatableInput<Boolean> buildAsValidatableInput() {
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
	public C withValue(Boolean value) {
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
	public C withValueChangeListener(ValueChangeListener<Boolean, ValueChangeEvent<Boolean>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
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
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<Checkbox>>() {

			@Override
			public void onComponentEvent(FocusEvent<Checkbox> event) {
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
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<Checkbox>>() {

			@Override
			public void onComponentEvent(BlurEvent<Checkbox> event) {
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
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickListener(com.holonplatform.
	 * vaadin.flow.components.events.ClickEventListener)
	 */
	@Override
	public C withClickListener(ClickEventListener<Checkbox, ClickEvent<Checkbox>> clickEventListener) {
		getComponent().addClickListener(new ComponentClickListenerAdapter<>(clickEventListener));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickShortcut(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public C withClickShortcutKey(Key key, KeyModifier... keyModifiers) {
		getComponent().addClickShortcut(key, keyModifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickShortcut(com.vaadin.flow.
	 * component.Key)
	 */
	@Override
	public ShortcutConfigurator<C> withClickShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addClickShortcut(key), getConfigurator());
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
