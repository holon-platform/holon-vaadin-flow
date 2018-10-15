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

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.vaadin.flow.component.Component;

/**
 * Base localizable {@link ComponentConfigurator}.
 * 
 * @param <C> Concrete component type
 * @param <B> Concrete builder type
 */
public abstract class AbstractLocalizableComponentConfigurator<C extends Component, B extends ComponentConfigurator<B> & DeferrableLocalizationConfigurator<B>>
		extends AbstractComponentConfigurator<C, B> implements DeferrableLocalizationConfigurator<B> {

	private boolean deferredLocalizationEnabled;

	/**
	 * Constructor.
	 * @param component The component instance (not null)
	 */
	public AbstractLocalizableComponentConfigurator(C component) {
		super(component);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
	 */
	@Override
	public boolean isDeferredLocalizationEnabled() {
		return deferredLocalizationEnabled;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization(
	 * boolean)
	 */
	@Override
	public B withDeferredLocalization(boolean deferredLocalization) {
		this.deferredLocalizationEnabled = deferredLocalization;
		return getConfigurator();
	}

}
