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
import com.holonplatform.core.property.Property.PropertyNotFoundException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupBuilder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.exceptions.InputGroupValidationException;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyInputGroup;

/**
 * A {@link BoundComponentGroup} which handles {@link Input} elements type and uses a {@link Property} set to bind and
 * identify the elements within the group.
 * <p>
 * As a {@link ValueHolder}, allows to manage the overall group elements value, represented by a {@link PropertyBox}
 * instance.
 * </p>
 * <p>
 * The {@link #refresh()} and {@link #refresh(Property)} methods can be used to refresh the group elements values, for
 * example to recalculate any <em>virtual</em> group element value which depends on other elements value.
 * </p>
 * <p>
 * The {@link PropertyInputGroup} is {@link Validatable} and supports {@link Validator} registration both for the single
 * group element and for the overall group value. See the
 * {@link PropertyInputGroupBuilder#withValidator(Property, Validator)} and
 * {@link PropertyInputGroupBuilder#withValidator(Validator)} methods.
 * </p>
 * <p>
 * By default, the {@link Input} components to bind to each property are obtained using the {@link PropertyRenderer}s
 * registered in the context {@link PropertyRendererRegistry}, if available. The <code>bind(...)</code> methods of the
 * {@link PropertyInputGroupBuilder} can be used to provided a specific renderer or {@link Input} of one or more group
 * property.
 * </p>
 * <p>
 * The convenience methods {@link #setEnabled(boolean)} and {@link #setReadOnly(boolean)} can be used to change the
 * enabled / read-only state for all the property bound {@link Input}s.
 * </p>
 * 
 * @since 5.2.0
 */
public interface PropertyInputGroup extends BoundComponentGroup<Property<?>, Input<?>>,
		ValueHolder<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, Input<?>, PropertyInputGroup>>,
		Validatable {

	/**
	 * Get the {@link Input} bound to the given <code>property</code>, if available.
	 * @param <T> Property type
	 * @param property The property which identifies the {@link Input} within the group (not null)
	 * @return Optional {@link Input} bound to the given <code>property</code>
	 */
	<T> Optional<Input<T>> getInput(Property<T> property);

	/**
	 * Get the {@link Input} bound to the given <code>property</code>, throwing a {@link PropertyNotFoundException} if
	 * not available.
	 * @param <T> Property type
	 * @param property The property which identifies the {@link Input} within the group (not null)
	 * @return The {@link Input} bound to the given <code>property</code>
	 * @throws PropertyNotFoundException If no {@link Input} is bound to given property
	 */
	default <T> Input<T> requireInput(Property<T> property) {
		return getInput(property).orElseThrow(
				() -> new PropertyNotFoundException(property, "No Input available for property [" + property + "]"));
	}

	/**
	 * Refresh the value of all available {@link Input}s, using current value.
	 * <p>
	 * Each input value will be replaced by the value of the {@link Property} to which the input is bound, obtained from
	 * the current {@link PropertyBox} value.
	 * </p>
	 */
	void refresh();

	/**
	 * Refresh the value of the {@link Input} bound to given <code>property</code>, using current value.
	 * <p>
	 * The input value will be replaced by the value of the {@link Property} to which the input is bound, obtained from
	 * the current {@link PropertyBox} value.
	 * </p>
	 * @param <T> Property type
	 * @param property The property for which to refresh the bound {@link Input} (not null)
	 * @return <code>true</code> if an {@link Input} bound to given property is available, <code>false</code> otherwise
	 */
	<T> boolean refresh(Property<T> property);

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

}
