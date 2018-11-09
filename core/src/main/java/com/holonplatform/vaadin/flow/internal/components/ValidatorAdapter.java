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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.function.Supplier;

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

/**
 * Vaadin validator adapter.
 * 
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class ValidatorAdapter<T> implements Validator<T> {

	private static final long serialVersionUID = 3664165755542688523L;

	private final com.vaadin.flow.data.binder.Validator<T> validator;
	private final Supplier<ValueContext> contextSupplier;

	/**
	 * Constructor
	 * @param validator Vaadin validator (not null)
	 * @param contextSupplier Validation context supplier (not null)
	 */
	public ValidatorAdapter(com.vaadin.flow.data.binder.Validator<T> validator,
			Supplier<ValueContext> contextSupplier) {
		super();
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		ObjectUtils.argumentNotNull(contextSupplier, "Context supplier must be not null");
		this.validator = validator;
		this.contextSupplier = contextSupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(T value) throws ValidationException {
		ValidationResult result = validator.apply(value, contextSupplier.get());
		if (result.isError()) {
			throw new ValidationException(result.getErrorMessage());
		}
	}

}
