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

import java.util.function.Function;

import com.vaadin.flow.component.HasValue;

/**
 * An is empty supplier for {@link String} type Inputs with empty and blank value support.
 *
 * @param <V> HasValue type
 *
 * @since 5.2.0
 */
public class StringInputIsEmptySupplier<V extends HasValue<?, String>> implements Function<V, Boolean> {

	private final boolean emptyValuesAsNull;
	private final boolean blankValuesAsNull;

	public StringInputIsEmptySupplier(boolean emptyValuesAsNull, boolean blankValuesAsNull) {
		super();
		this.emptyValuesAsNull = emptyValuesAsNull;
		this.blankValuesAsNull = blankValuesAsNull;
	}

	@Override
	public Boolean apply(V t) {
		final String value = t.getValue();
		if (value == null) {
			return true;
		}
		if (value.trim().length() == 0 && blankValuesAsNull) {
			return true;
		}
		if (value.length() == 0 && emptyValuesAsNull) {
			return true;
		}
		return t.isEmpty();
	}

}
