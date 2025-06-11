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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator;
import com.holonplatform.vaadin.flow.internal.components.DefaultDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dialog.Dialog.DialogResizeEvent;
import com.vaadin.flow.component.dialog.Dialog.OpenedChangeEvent;

/**
 * Abstract {@link DialogConfigurator}.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public abstract class AbstractDialogConfigurator<C extends DialogConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<DefaultDialog, C> implements DialogConfigurator<C> {

	private final MessageLocalizationSupportConfigurator<DefaultDialog> messageConfigurator;

	public AbstractDialogConfigurator() {
		super(new DefaultDialog());

		this.messageConfigurator = new MessageLocalizationSupportConfigurator<>(getComponent(),
				text -> getComponent().setMessage(text), this);
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
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(
	 * com.holonplatform.core.i18n. Localizable)
	 */
	@Override
	public C text(Localizable text) {
		messageConfigurator.setMessage(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#
	 * withComponent(com.vaadin.flow.component. Component)
	 */
	@Override
	public C withComponent(Component component) {
		getComponent().addContentComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#
	 * withToolbarComponent(com.vaadin.flow. component.Component)
	 */
	@Override
	public C withToolbarComponent(Component component) {
		getComponent().addToolbarComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#
	 * withOpenedChangeListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
		ObjectUtils.argumentNotNull(listener, "Event listener must be not null");
		getComponent().addOpenedChangeListener(listener);
		return getConfigurator();
	}

	@Override
	public C withResizeListener(ComponentEventListener<DialogResizeEvent> listener) {
		ObjectUtils.argumentNotNull(listener, "Event listener must be not null");
		getComponent().addResizeListener(listener);
		return getConfigurator();
	}

	@Override
	public C resizable(boolean resizable) {
		getComponent().setResizable(resizable);
		return getConfigurator();
	}

	@Override
	public C draggable(boolean draggable) {
		getComponent().setDraggable(draggable);
		return getConfigurator();
	}

	@Override
	public C modal(boolean modal) {
		getComponent().setModal(modal);
		return getConfigurator();
	}

}
