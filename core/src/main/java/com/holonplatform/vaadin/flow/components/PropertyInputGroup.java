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
package com.holonplatform.vaadin.flow.components;

import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupBuilder;
import com.holonplatform.vaadin.flow.exceptions.InputGroupValidationException;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyInputGroup;

/**
 * A class to manage a group of {@link Input}s bound to a {@link Property} set, loading and obtaining property values in
 * and from {@link Input}s using the {@link PropertyBox} data container type.
 * <p>
 * Supports overall {@link Validator}s registration to validate all the {@link Input} values, allowing cross input
 * validation, using a {@link PropertyBox} to represent the inputs value set.
 * </p>
 * <p>
 * By default, property {@link Input} components are obtained from the {@link PropertyRenderer}s registered in the
 * context {@link PropertyRendererRegistry}, if available. Custom {@link PropertyRenderer} registration is supported to
 * provide custom input components for specific properties.
 * </p>
 * <p>
 * Default property values are supported using a {@link DefaultValueProvider}.
 * </p>
 * <p>
 * Convenience methods {@link #setEnabled(boolean)} and {@link #setReadOnly(boolean)} can be used to change the enabled
 * / read-only state for all the property bound {@link Input}s.
 * </p>
 * 
 * @since 5.2.0
 */
public interface PropertyInputGroup extends PropertyInputBinder, ValueHolder<PropertyBox>, Validatable {

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set.
	 * <p>
	 * For each property with a bound {@link Input} component, the property value is obtained from the {@link Input}
	 * component through the {@link Input#getValue()} method.
	 * </p>
	 * <p>
	 * If the validation fails, an {@link InputGroupValidationException} is thrown as {@link ValidationException}
	 * extension, allowing to inspect the property / input component source of the validation failure. If no property /
	 * input component is provided, it means it was an overall validation failure.
	 * </p>
	 * @param validate <code>true</code> to check the validity of the property bound {@link Input}s and of this
	 *        {@link PropertyInputGroup} before returing the value, throwing a {@link ValidationException} if the
	 *        validation is not successful.
	 * @return A {@link PropertyBox} containing the property values (never null)
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and an {@link Input} value is not valid
	 */
	PropertyBox getValue(boolean validate);

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set.
	 * <p>
	 * For each property with a bound {@link Input} component, the property value is obtained from the {@link Input}
	 * component through the {@link Input#getValue()} method.
	 * </p>
	 * <p>
	 * The available {@link Input}s and the overall group validation is performed before returning the value, throwing a
	 * {@link ValidationException} if the validation is not successful.
	 * </p>
	 * <p>
	 * If the validation fails, an {@link InputGroupValidationException} is thrown as {@link ValidationException}
	 * extension, allowing to inspect the property / input component source of the validation failure. If no property /
	 * input component is provided, it means it was an overall validation failure.
	 * </p>
	 * @return A {@link PropertyBox} containing the property values (never null)
	 * @throws ValidationException If one or more input value is not valid, providing the validation error messages
	 * @see #getValue(boolean)
	 * @see #getValueIfValid()
	 */
	@Override
	PropertyBox getValue();

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set, only if the property bound {@link Input}s and this {@link PropertyInputGroup} are valid
	 * <p>
	 * For each property with a bound {@link Input} component, the property value is obtained from the {@link Input}
	 * component through the {@link Input#getValue()} method.
	 * </p>
	 * @return A {@link PropertyBox} containing the property values, or an empty Optional if validation failed
	 */
	Optional<PropertyBox> getValueIfValid();

	/**
	 * Set the current property values using a {@link PropertyBox}, loading the values to the available property bound
	 * {@link Input}s through the {@link Input#setValue(Object)} method.
	 * <p>
	 * Only the properties which belong to the group's property set are taken into account.
	 * </p>
	 * <p>
	 * If the validation fails, an {@link InputGroupValidationException} is thrown as {@link ValidationException}
	 * extension, allowing to inspect the property / input component source of the validation failure. If no property /
	 * input component is provided, it means it was an overall validation failure.
	 * </p>
	 * @param value the {@link PropertyBox} which contains the property values to load. If <code>null</code>, all the
	 *        {@link Input} components are cleared.
	 * @param validate <code>true</code> to check the validity of the property bound {@link Input}s and of this
	 *        {@link PropertyInputGroup}, throwing a {@link ValidationException} if the validation is not successful.
	 * @throws ValidationException If <code>validate</code> is <code>true</code> and an {@link Input} value is not valid
	 */
	void setValue(PropertyBox value, boolean validate);

	/**
	 * Set the current property values using a {@link PropertyBox}, loading the values to the available property bound
	 * {@link Input}s through the {@link Input#setValue(Object)} method.
	 * <p>
	 * Only the properties which belong to the group's property set are taken into account.
	 * </p>
	 * <p>
	 * By default, no value validation is performed using this method.
	 * </p>
	 * @param value the {@link PropertyBox} which contains the property values to load. If <code>null</code>, all the
	 *        {@link Input} components are cleared.
	 * @see #setValue(PropertyBox, boolean)
	 */
	@Override
	void setValue(PropertyBox value);

	/**
	 * Set the read-only mode for all the group inputs.
	 * @param readOnly <code>true</code> to set all inputs as read-only, <code>false</code> to unset
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Updates the enabled state of all the group inputs.
	 * @param enabled <code>true</code> to enable all group inputs, <code>false</code> to disable them
	 */
	void setEnabled(boolean enabled);

	/**
	 * Checks the validity of each input component of the group and of the overall group value against every registered
	 * validator, if any.
	 * <p>
	 * If the validation fails, an {@link InputGroupValidationException} is thrown as {@link ValidationException}
	 * extension, allowing to inspect the property / input component source of the validation failure. If no property /
	 * input component is provided, it means it was an overall validation failure.
	 * </p>
	 * @throws ValidationException If a validation error occurred
	 */
	@Override
	void validate() throws ValidationException;

	// ------- Builders

	/**
	 * Get a {@link PropertyInputGroupBuilder} to create and setup a {@link PropertyInputGroup}.
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyInputGroupBuilder}
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyInputGroupBuilder builder(Iterable<P> properties) {
		return new DefaultPropertyInputGroup.DefaultBuilder(properties);
	}

	/**
	 * Get a {@link PropertyInputGroupBuilder} to create and setup a {@link PropertyInputGroup}.
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyInputGroupBuilder}
	 */
	static PropertyInputGroupBuilder builder(Property<?>... properties) {
		return builder(PropertySet.of(properties));
	}

	// -------

	/**
	 * Interface to provide the default value for a {@link Property}.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface DefaultValueProvider<T> {

		/**
		 * Get the property default value
		 * @param property Property (never null)
		 * @return Default value
		 */
		T getDefaultValue(Property<T> property);

	}

}
