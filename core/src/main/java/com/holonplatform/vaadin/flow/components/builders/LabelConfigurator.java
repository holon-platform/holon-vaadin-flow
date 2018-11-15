/*
 * Copyright 2016-2017 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLabelConfigurator;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Interface to configure a <em>label</em>, i.e. a component to display a text.
 * 
 * @param <L> Concrete label component type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public interface LabelConfigurator<L extends HtmlContainer & ClickNotifier, C extends LabelConfigurator<L, C>> extends
		HtmlComponentConfigurator<C>, HasEnabledConfigurator<C>, HasTextConfigurator<C>, HasHtmlTextConfigurator<C>,
		ClickNotifierConfigurator<L, ClickEvent<L>, C>, DeferrableLocalizationConfigurator<C> {

	/**
	 * Base Label configurator.
	 */
	public interface BaseLabelConfigurator<L extends HtmlContainer & ClickNotifier>
			extends LabelConfigurator<L, BaseLabelConfigurator<L>> {

	}

	/**
	 * Obtain a {@link LabelConfigurator} to configure given label component.
	 * <p>
	 * The component must be a {@link HtmlContainer} and {@link ClickNotifier}, such as {@link Span} or {@link Div}.
	 * </p>
	 * @param <L> Label component type
	 * @param component The component to configure (not null)
	 * @return A {@link LabelConfigurator} to configure given component
	 */
	static <L extends HtmlContainer & ClickNotifier> BaseLabelConfigurator<L> configure(L component) {
		return new DefaultLabelConfigurator<>(component);
	}

}
