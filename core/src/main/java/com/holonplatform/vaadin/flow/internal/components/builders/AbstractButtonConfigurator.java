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

import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IronIcon;

/**
 * Base {@link ButtonConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public abstract class AbstractButtonConfigurator<C extends ButtonConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<Button, C> implements ButtonConfigurator<C> {

	protected final DefaultHasTextConfigurator textConfigurator;
	protected final DefaultHasTitleConfigurator<Button> titleConfigurator;

	public AbstractButtonConfigurator(Button component) {
		super(component);
		this.textConfigurator = new DefaultHasTextConfigurator(component, this);
		this.titleConfigurator = new DefaultHasTitleConfigurator<>(component, title -> {
			component.getElement().setAttribute("title", (title != null) ? title : "");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasThemeVariantConfigurator
	 * #withThemeVariants(java.lang.Enum[])
	 */
	@Override
	public C withThemeVariants(ButtonVariant... variants) {
		getComponent().addThemeVariants(variants);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator#icon(
	 * com.vaadin.flow.component.Component)
	 */
	@Override
	public C icon(Component icon) {
		getComponent().setIcon(icon);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator#
	 * iconConfigurator(com.vaadin.flow.component. icon.Icon)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IconConfigurator<C> iconConfigurator(Icon icon) {
		return new DefaultIconConfigurator(this, icon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.HasIconConfigurator#
	 * iconConfigurator(com.vaadin.flow.component. icon.IronIcon)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IconConfigurator<C> iconConfigurator(IronIcon icon) {
		return new DefaultIronIconConfigurator(this, icon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(
	 * com.holonplatform.core.i18n. Localizable)
	 */
	@Override
	public C text(Localizable text) {
		textConfigurator.text(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(
	 * com.holonplatform.core.i18n. Localizable)
	 */
	@Override
	public C title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator#
	 * disableOnClick()
	 */
	@Override
	public C disableOnClick() {
		getComponent().setDisableOnClick(true);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#
	 * withClickListener(com.holonplatform.
	 * vaadin.flow.components.events.ClickEventListener)
	 */
	@Override
	public C withClickListener(ClickEventListener<Button, ClickEvent<Button>> clickEventListener) {
		getComponent().addClickListener(new ComponentClickListenerAdapter<>(clickEventListener));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#
	 * withClickShortcut(com.vaadin.flow. component.Key,
	 * com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public C withClickShortcutKey(Key key, KeyModifier... keyModifiers) {
		getComponent().addClickShortcut(key, keyModifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#
	 * withClickShortcut(com.vaadin.flow. component.Key)
	 */
	@Override
	public ShortcutConfigurator<C> withClickShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addClickShortcut(key), getConfigurator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * tabIndex(int)
	 */
	@Override
	public C tabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * withFocusListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public C withFocusListener(ComponentEventListener<FocusEvent<Button>> listener) {
		getComponent().addFocusListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * withBlurListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public C withBlurListener(ComponentEventListener<BlurEvent<Button>> listener) {
		getComponent().addBlurListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * withFocusShortcut(com.vaadin.flow. component.Key)
	 */
	@Override
	public ShortcutConfigurator<C> withFocusShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addFocusShortcut(key), getConfigurator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator#
	 * iconAfterText(boolean)
	 */
	@Override
	public C iconAfterText(boolean iconAfterText) {
		getComponent().setIconAfterText(iconAfterText);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator#
	 * autofocus(boolean)
	 */
	@Override
	public C autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
	}

}
