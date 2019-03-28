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

import java.time.LocalDateTime;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.LocalDateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableLocalDateTimeInputBuilder;

/**
 * Default {@link LocalDateTimeInputBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultLocalDateTimeInputBuilder extends AbstractLocalDateTimeInputBuilder<LocalDateTimeInputBuilder>
		implements LocalDateTimeInputBuilder {

	public DefaultLocalDateTimeInputBuilder() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected LocalDateTimeInputBuilder getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public LocalDateTimeInputBuilder required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<LocalDateTime> build() {
		return buildAsInput();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableLocalDateTimeInputBuilder validatable() {
		return new DefaultValidatableLocalDateTimeInputBuilder(getComponent(), getContextLocaleOnAttachRegistration(),
				getLocalization(), getInitialValue(), getValueChangeListeners(), getAdapters());
	}

}
