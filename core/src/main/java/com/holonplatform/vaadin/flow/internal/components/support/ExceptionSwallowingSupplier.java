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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.Optional;
import java.util.function.Supplier;

import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Optional value supplier which swallows any exception thrown by a concrete {@link Supplier}.
 *
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class ExceptionSwallowingSupplier<T> {

	private Supplier<T> supplier;

	/**
	 * Constructor
	 * @param supplier Concrete supplier (not null)
	 */
	public ExceptionSwallowingSupplier(Supplier<T> supplier) {
		super();
		ObjectUtils.argumentNotNull(supplier, "Supplier must be not null");
		this.supplier = supplier;
	}

	/**
	 * Get the value, swallowing any exception.
	 * @return Optional value
	 */
	public Optional<T> get() {
		try {
			return Optional.ofNullable(supplier.get());
		} catch (@SuppressWarnings("unused") Exception e) {
			// swallow
			return Optional.empty();
		}
	}

}
