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
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HtmlContainer;

/**
 * Base {@link LabelConfigurator} implementation.
 * 
 * @param <L> Concrete label type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractLabelConfigurator<L extends HtmlContainer & ClickNotifier, C extends LabelConfigurator<L, C>>
		extends AbstractLocalizableComponentConfigurator<L, C> implements LabelConfigurator<L, C> {

	protected final DefaultHasTextConfigurator textConfigurator;
	protected final DefaultHasHtmlTextConfigurator htmlTextConfigurator;
	protected final DefaultHasTitleConfigurator<L> titleConfigurator;

	public AbstractLabelConfigurator(L component) {
		super(component);
		this.textConfigurator = new DefaultHasTextConfigurator(component, this);
		this.htmlTextConfigurator = new DefaultHasHtmlTextConfigurator(component, this);
		this.titleConfigurator = new DefaultHasTitleConfigurator<>(component, title -> {
			component.setTitle(title);
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
	 * com.holonplatform.vaadin.flow.components.builders.HasHtmlTextConfigurator#htmlText(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C htmlText(Localizable text) {
		htmlTextConfigurator.htmlText(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C text(Localizable text) {
		textConfigurator.text(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickListener(com.holonplatform.
	 * vaadin.flow.components.events.ClickEventListener)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public C withClickListener(ClickEventListener<L, ClickEvent<L>> clickEventListener) {
		getComponent().addClickListener(new ComponentClickListenerAdapter<>(clickEventListener));
		return getConfigurator();
	}

}
