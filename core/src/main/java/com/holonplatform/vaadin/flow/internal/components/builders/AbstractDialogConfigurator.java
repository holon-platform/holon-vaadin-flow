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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.DialogConfigurator;
import com.holonplatform.vaadin.flow.internal.components.DefaultDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;

/**
 * Abstract {@link DialogConfigurator}.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public abstract class AbstractDialogConfigurator<C extends DialogConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<DefaultDialog, C> implements DialogConfigurator<C> {

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	private final MessageLocalizationSupportConfigurator<DefaultDialog> messageConfigurator;

	public AbstractDialogConfigurator() {
		super(new DefaultDialog());

		this.sizeConfigurator = new DefaultHasSizeConfigurator(getComponent());
		this.styleConfigurator = new DefaultHasStyleConfigurator(getComponent());
		this.messageConfigurator = new MessageLocalizationSupportConfigurator<>(getComponent(),
				text -> getComponent().setMessage(text), this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public C width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public C height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#message(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C message(Localizable message) {
		messageConfigurator.setMessage(message);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#withComponent(com.vaadin.flow.component.
	 * Component)
	 */
	@Override
	public C withComponent(Component component) {
		getComponent().addContentComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#withToolbarComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public C withToolbarComponent(Component component) {
		getComponent().addToolbarComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#withOpenedChangeListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent<Dialog>> listener) {
		ObjectUtils.argumentNotNull(listener, "Event listener must be not null");
		return getConfigurator();
	}

}
