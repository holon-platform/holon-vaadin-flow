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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.Dialog.DialogResizeEvent;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;

/**
 * {@link Dialog} component configurator.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface DialogConfigurator<C extends DialogConfigurator<C>> extends ComponentConfigurator<C>,
		HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasTextConfigurator<C>, DeferrableLocalizationConfigurator<C> {

	/**
	 * Add given component to the dialog content.
	 * <p>
	 * If a dialog message is configured, the component will be placed after the
	 * dialog message element.
	 * </p>
	 * @param component The component to add (not null)
	 * @return this
	 */
	C withComponent(Component component);

	/**
	 * Add given {@link HasComponent} component to the dialog content.
	 * <p>
	 * If a dialog message is configured, the component will be placed after the
	 * dialog message element.
	 * </p>
	 * @param component The component to add (not null)
	 * @return this
	 */
	default C withComponent(HasComponent component) {
		ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
		return withComponent(component.getComponent());
	}

	/**
	 * Add given component to the dialog toolbar, shown at the bottom of the dialog
	 * area.
	 * @param component The toolbar component to add (not null)
	 * @return this
	 */
	C withToolbarComponent(Component component);

	/**
	 * Add given {@link HasComponent} component to the dialog toolbar, shown at the
	 * bottom of the dialog area.
	 * @param component The toolbar component to add (not null)
	 * @return this
	 */
	default C withToolbarComponent(HasComponent component) {
		ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
		return withToolbarComponent(component.getComponent());
	}

	/**
	 * Add a lister for event fired by the <code>opened-changed</code> events.
	 * @param listener the listener to add (not null)
	 * @return this
	 */
	C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent<Dialog>> listener);

	/**
	 * Adds a listener that is called after user finishes resizing the overlay. It
	 * is called only if resizing is enabled.
	 * @param listener the listener to add (not null)
	 * @return this
	 * @since 5.5.0
	 */
	C withResizeListener(ComponentEventListener<DialogResizeEvent> listener);

	/**
	 * Sets whether dialog can be resized by user or not.
	 * @param resizable <code>true</code> to enabled resizing of the dialog,
	 *                  <code>false</code> otherwise.
	 * @return this
	 * @since 5.5.0
	 */
	C resizable(boolean resizable);

	/**
	 * Sets whether dialog is enabled to be dragged by the user or not.
	 * <p>
	 * To allow an element inside the dialog to be dragged by the user (for
	 * instance, a header inside the dialog), a class {@code "draggable"} can be
	 * added to it.
	 * @param draggable <code>true</code> to enable dragging of the dialog,
	 *                  <code>false</code> otherwise
	 * @return this
	 * @since 5.5.0
	 */
	C draggable(boolean draggable);

	/**
	 * Sets whether component will open modal or modeless dialog.
	 * <p>
	 * Note: When dialog is set to be modeless, then it's up to you to provide means
	 * for it to be closed (eg. a button). The reason being that a modeless dialog
	 * allows user to interact with the interface under it and won't be closed by
	 * clicking outside or the ESC key.
	 * @param modal <code>false</code> to enable dialog to open as modeless modal,
	 *              <code>true</code> otherwise.
	 * @return this
	 * @since 5.5.0
	 */
	C modal(boolean modal);

	/**
	 * A {@link DialogConfigurator} with dialog closing options configuration.
	 *
	 * @param <C> Concrete configurator type
	 * 
	 * @since 5.2.0
	 */
	public interface ClosableDialogConfigurator<C extends ClosableDialogConfigurator<C>> extends DialogConfigurator<C> {

		/**
		 * Sets whether the dialog can be closed by hitting the esc-key or not.
		 * <p>
		 * By default, the dialog is closable with esc.
		 * </p>
		 * @param closeOnEsc <code>true</code> to enable closing this dialog with the
		 *                   esc-key, <code>false</code> to disable it
		 * @return this
		 */
		C closeOnEsc(boolean closeOnEsc);

		/**
		 * Sets whether this dialog can be closed by clicking outside of it or not.
		 * <p>
		 * By default, the dialog is closable with an outside click.
		 * </p>
		 * @param closeOnOutsideClick <code>true</code> to enable closing this dialog
		 *                            with an outside click, <code>false</code> to
		 *                            disable it
		 * @return this
		 */
		C closeOnOutsideClick(boolean closeOnOutsideClick);

	}

}
