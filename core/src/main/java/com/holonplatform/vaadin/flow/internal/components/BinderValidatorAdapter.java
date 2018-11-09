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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

/**
 * Binder {@link Validator} adapter.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class BinderValidatorAdapter<T> implements Validator<T> {

	private static final long serialVersionUID = -7783005054676508541L;

	private final com.holonplatform.core.Validator<T> validator;

	/**
	 * Constructor.
	 * @param validator The validator to adapt (not null)
	 */
	public BinderValidatorAdapter(com.holonplatform.core.Validator<T> validator) {
		super();
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		this.validator = validator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.binder.Validator#apply(java.lang.Object, com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public ValidationResult apply(T value, ValueContext context) {
		try {
			validator.validate(value);
		} catch (ValidationException e) {
			return ValidationResult.error(e.getLocalizedMessage());
		}
		return ValidationResult.ok();
	}

}
