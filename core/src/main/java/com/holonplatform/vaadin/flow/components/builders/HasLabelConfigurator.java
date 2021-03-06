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

import java.util.function.Consumer;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasLabelConfigurator;
import com.vaadin.flow.component.HasElement;

/**
 * Configurator for {@link HasLabel} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasLabelConfigurator<C extends HasLabelConfigurator<C>> {

	/**
	 * Sets the label text using a {@link Localizable} message.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> value is interpreted as an empty text.
	 * </p>
	 * @param label Localizable label text message (may be null)
	 * @return this
	 * @see LocalizationProvider
	 */
	C label(Localizable label);

	/**
	 * Sets the label text, replacing any previous content.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> text value is interpreted as an empty text.
	 * </p>
	 * @param label The label text to set
	 * @return this
	 */
	default C label(String label) {
		return label((label == null) ? null : Localizable.builder().message(label).build());
	}

	/**
	 * Sets the label text using a localizable <code>messageCode</code>.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * @param defaultLabel Default label text if no translation is available for given <code>messageCode</code>
	 * @param messageCode Label text translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationProvider
	 */
	default C label(String defaultLabel, String messageCode, Object... arguments) {
		return label(Localizable.builder().message((defaultLabel == null) ? "" : defaultLabel).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

	/**
	 * Base {@link HasLabelConfigurator}.
	 */
	public interface BaseHasLabelConfigurator extends HasLabelConfigurator<BaseHasLabelConfigurator> {

	}

	/**
	 * Create a new {@link BaseHasLabelConfigurator}.
	 * @param <C> Component type
	 * @param setLabelOperation Operation to use to set the component label (not null)
	 * @param component Component to configure (not null)
	 * @return A new {@link BaseHasLabelConfigurator}
	 */
	static <C extends HasElement> BaseHasLabelConfigurator create(C component, Consumer<String> setLabelOperation) {
		return new DefaultHasLabelConfigurator<>(component, setLabelOperation);
	}

	/**
	 * Create a new {@link BaseHasLabelConfigurator}.
	 * @param <C> Component type
	 * @param setLabelOperation Operation to use to set the component label (not null)
	 * @param component Component to configure (not null)
	 * @param deferrableLocalization Optional {@link HasDeferrableLocalization} reference
	 * @return A new {@link BaseHasLabelConfigurator}
	 */
	static <C extends HasElement> BaseHasLabelConfigurator create(C component, Consumer<String> setLabelOperation,
			HasDeferrableLocalization deferrableLocalization) {
		return new DefaultHasLabelConfigurator<>(component, setLabelOperation, deferrableLocalization);
	}

}
