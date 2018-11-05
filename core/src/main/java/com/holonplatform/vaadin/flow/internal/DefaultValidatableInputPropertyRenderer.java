/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal;

import javax.annotation.Priority;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;

/**
 * Default {@link PropertyRenderer} to create {@link ValidatableInput} components.
 * 
 * @param <T> Property type
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public class DefaultValidatableInputPropertyRenderer<T> implements PropertyRenderer<ValidatableInput, T> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
	 */
	@Override
	public Class<? extends ValidatableInput> getRenderType() {
		return ValidatableInput.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ValidatableInput render(Property<? extends T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		// render as Input and convert to ValidatableInput
		return property.renderIfAvailable(Input.class).map(input -> ValidatableInput.from(input)).map(input -> {
			final ValidatableInput validatableInput = input;
			// property validators
			property.getValidators().forEach(validator -> validatableInput.addValidator(validator));
			return input;
		}).orElse(null);
	}

}
