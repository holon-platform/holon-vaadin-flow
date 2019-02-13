/*
 * Copyright 2016-2019 Axioma srl.
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

import java.util.Set;

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.components.ValidatableMultiSelectAdapter;

/**
 * An {@link MultiSelect} component with validation support using {@link Validator}s.
 * 
 * @param <T> Selection item type
 *
 * @since 5.2.2
 */
public interface ValidatableMultiSelect<T> extends MultiSelect<T>, ValidatableInput<Set<T>> {

	/**
	 * Create a {@link ValidatableMultiSelect} from given {@link MultiSelect} instance.
	 * @param <T> Selection item type
	 * @param input The {@link MultiSelect} instance (not null)
	 * @return A new {@link ValidatableMultiSelect} component which wraps the given <code>input</code>
	 */
	static <T> ValidatableMultiSelect<T> from(MultiSelect<T> input) {
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		return (input instanceof ValidatableMultiSelect) ? (ValidatableMultiSelect<T>) input
				: new ValidatableMultiSelectAdapter<>(input);
	}

}
