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

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.components.ValidatableSingleSelectAdapter;

/**
 * An {@link SingleSelect} component with validation support using {@link Validator}s.
 * 
 * @param <T> Selection item type
 *
 * @since 5.2.2
 */
public interface ValidatableSingleSelect<T> extends SingleSelect<T>, ValidatableInput<T> {

	/**
	 * Create a {@link ValidatableSingleSelect} from given {@link SingleSelect} instance.
	 * @param <T> Selection item type
	 * @param input The {@link SingleSelect} instance (not null)
	 * @return A new {@link ValidatableSingleSelect} component which wraps the given <code>input</code>
	 */
	static <T> ValidatableSingleSelect<T> from(SingleSelect<T> input) {
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		return (input instanceof ValidatableSingleSelect) ? (ValidatableSingleSelect<T>) input
				: new ValidatableSingleSelectAdapter<>(input);
	}

}
