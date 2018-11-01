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

import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup.DefaultValueProvider;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;

/**
 * Configuration for a {@link Property} bound to a {@link Input}.
 * 
 * @param <T> Property type
 *
 * @since 5.2.0
 */
public interface InputPropertyConfiguration<T> extends ValueComponentPropertyConfiguration<T, Input<T>> {

	/**
	 * Get whether the property is read only.
	 * @return whether the property is read only
	 */
	boolean isReadOnly();

	/**
	 * Set whether the property is read only.
	 * @param readOnly whether the property is read only
	 */
	public void setReadOnly(boolean readOnly);

	/**
	 * Get whether the property is required.
	 * @return whether the property is required
	 */
	boolean isRequired();

	/**
	 * Set whether the property is required.
	 * @param required whether the property is required
	 */
	public void setRequired(boolean required);

	/**
	 * Get the default value provider, if available.
	 * @return Optional default value provider
	 */
	Optional<DefaultValueProvider<T>> getDefaultValueProvider();

	/**
	 * Set the default value provider
	 * @param defaultValueProvider The default value provider to set
	 */
	void setDefaultValueProvider(DefaultValueProvider<T> defaultValueProvider);

	/**
	 * Get the property {@link Validator}s.
	 * @return the property validators
	 */
	List<Validator<T>> getValidators();

	/**
	 * Add a property validator.
	 * @param validator property validator (not null)
	 */
	void addValidator(Validator<T> validator);

	/**
	 * Get the required validation message to use when property {@link #isRequired()}.
	 * @return the required validation message
	 */
	Optional<Localizable> getRequiredMessage();

	/**
	 * Set the required validation message to use when property {@link #isRequired()}.
	 * @param requiredMessage the required validation message to set
	 */
	void setRequiredMessage(Localizable requiredMessage);

	/**
	 * Get the {@link ValidationStatusHandler} to use.
	 * @return Optional {@link ValidationStatusHandler}
	 */
	Optional<ValidationStatusHandler<T>> getValidationStatusHandler();

	/**
	 * Set the {@link ValidationStatusHandler} to use.
	 * @param validationStatusHandler The {@link ValidationStatusHandler} to set
	 */
	void setValidationStatusHandler(ValidationStatusHandler<T> validationStatusHandler);

	/**
	 * Get the property {@link ValueChangeListener}s.
	 * @return the property value change listeners
	 */
	List<ValueChangeListener<T>> getValueChangeListeners();

	/**
	 * Add a property {@link ValueChangeListener}.
	 * @param valueChangeListener property value change listener (not null)
	 */
	void addValueChangeListener(ValueChangeListener<T> valueChangeListener);

	/**
	 * Create a new {@link InputPropertyConfiguration} for given property.
	 * @param <T> Property type
	 * @param property The property (not null)
	 * @return A new {@link InputPropertyConfiguration}
	 */
	static <T> InputPropertyConfiguration<T> create(Property<T> property) {
		return new DefaultInputPropertyConfiguration<>(property);
	}

}
