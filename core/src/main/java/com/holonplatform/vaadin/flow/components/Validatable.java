/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.flow.components;

import java.util.function.Supplier;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.BinderValidatorAdapter;
import com.holonplatform.vaadin.flow.internal.components.ValidatorAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.ValueContext;

/**
 * Interface to provide value validation support for a component, using the current component value as the value to
 * validate.
 * 
 * @since 5.0.0
 */
public interface Validatable {

	/**
	 * Checks the validity of the current component value against every registered validator, if any. If the value is
	 * not valid, an {@link ValidationException} is thrown.
	 * <p>
	 * The {@link ValidationException} is {@link Localizable}, providing optional message code and arguments for
	 * validation message localization.
	 * </p>
	 * @throws ValidationException If the value is not valid
	 */
	void validate() throws ValidationException;

	/**
	 * Check if the current value is valid, swallowing any validation exception.
	 * @return <code>true</code> if the current value is valid, <code>false</code> otherwise
	 * @see #validate()
	 */
	default boolean isValid() {
		try {
			validate();
		} catch (@SuppressWarnings("unused") ValidationException e) {
			return false;
		}
		return true;
	}

	// ------- adapters

	/**
	 * Adapt given {@link com.vaadin.flow.data.binder.Validator} to be used as a {@link Validator}.
	 * @param <T> Validator value type
	 * @param validator The validator to adapt (not null)
	 * @return Adapted validator
	 */
	static <T> Validator<T> adapt(com.vaadin.flow.data.binder.Validator<T> validator) {
		return adapt(validator, () -> new ValueContext());
	}

	/**
	 * Adapt given {@link com.vaadin.flow.data.binder.Validator} to be used as a {@link Validator}.
	 * @param <T> Validator value type
	 * @param validator The validator to adapt (not null)
	 * @param input The input to validate
	 * @return Adapted validator
	 */
	static <T> Validator<T> adapt(com.vaadin.flow.data.binder.Validator<T> validator, Input<T> input) {
		return adapt(validator, () -> new ValueContext(input.getComponent()));
	}

	/**
	 * Adapt given {@link com.vaadin.flow.data.binder.Validator} to be used as a {@link Validator}.
	 * @param <T> Validator value type
	 * @param validator The validator to adapt (not null)
	 * @param component The component to validate
	 * @return Adapted validator
	 */
	static <T> Validator<T> adapt(com.vaadin.flow.data.binder.Validator<T> validator, Component component) {
		return adapt(validator, () -> new ValueContext(component));
	}

	/**
	 * Adapt given {@link com.vaadin.flow.data.binder.Validator} to be used as a {@link Validator}.
	 * @param <T> Validator value type
	 * @param validator The validator to adapt (not null)
	 * @param contextSupplier Value context supplier (not null)
	 * @return Adapted validator
	 */
	static <T> Validator<T> adapt(com.vaadin.flow.data.binder.Validator<T> validator,
			Supplier<ValueContext> contextSupplier) {
		return new ValidatorAdapter<>(validator, contextSupplier);
	}

	// ------- binder adapters

	/**
	 * Adapt given {@link Validator} to be used as a {@link com.vaadin.flow.data.binder.Validator}.
	 * @param <T> Validator value type
	 * @param validator The validator to adapt (not null)
	 * @return Adapted validator
	 */
	static <T> com.vaadin.flow.data.binder.Validator<T> adapt(Validator<T> validator) {
		return new BinderValidatorAdapter<>(validator);
	}

}
