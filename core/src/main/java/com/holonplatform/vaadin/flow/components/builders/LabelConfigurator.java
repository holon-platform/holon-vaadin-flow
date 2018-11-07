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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.support.ContentMode;
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
	 * Sets the label text content using a {@link Localizable} message.
	 * @param content Localizable text content message
	 * @return this
	 * @deprecated Use {@link #text(Localizable)} or {@link #htmlText(Localizable)}
	 */
	@Deprecated
	C content(Localizable content);

	/**
	 * Sets the label text content.
	 * @param content The text content to set
	 * @return this
	 * @deprecated Use {@link #text(String)} or {@link #htmlText(String)}
	 */
	@Deprecated
	default C content(String content) {
		return content((content == null) ? null : Localizable.builder().message(content).build());
	}

	/**
	 * Sets the label text content using a localizable <code>messageCode</code>.
	 * @param defaultContent Default text content if no translation is available for given <code>messageCode</code> for
	 *        current Locale.
	 * @param messageCode Text translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @deprecated Use {@link #text(String, String, Object...)} or {@link #htmlText(String, String, Object...)}
	 */
	@Deprecated
	default C content(String defaultContent, String messageCode, Object... arguments) {
		return content(Localizable.builder().message((defaultContent == null) ? "" : defaultContent)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Sets the content mode of the Label.
	 * @param contentMode The content mode to set
	 * @return this
	 * @deprecated Use <code>text(...)</code> or <code>htmlText(...)</code> methods to set the text content either as
	 *             plain or HTML text
	 */
	@Deprecated
	C contentMode(ContentMode contentMode);

	/**
	 * A shortcut to set the content mode to {@link ContentMode#HTML}
	 * @return this
	 * @deprecated Use <code>text(...)</code> or <code>htmlText(...)</code> methods to set the text content either as
	 *             plain or HTML text
	 */
	@Deprecated
	default C html() {
		return contentMode(ContentMode.HTML);
	}

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
