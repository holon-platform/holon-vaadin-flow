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

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableNumberInputBuilder;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Default {@link NumberInputBuilder} implementation using a {@link TextField} as concrete component.
 *
 * @param <T> Number type
 *
 * @since 5.2.0
 */
public class DefaultNumberInputBuilder<T extends Number> extends AbstractNumberInputBuilder<T, NumberInputBuilder<T>>
		implements NumberInputBuilder<T> {

	public DefaultNumberInputBuilder(Class<T> numberType) {
		super(numberType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected NumberInputBuilder<T> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public NumberInputBuilder<T> required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<T> build() {
		return buildAsInput();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableNumberInputBuilder<T> validatable() {
		return new DefaultValidatableNumberInputBuilder<>(getNumberType(), getComponent(), getInitialValue(),
				getConverter(), getValueChangeListeners());
	}

}
