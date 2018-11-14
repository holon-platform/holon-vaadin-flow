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
package com.holonplatform.vaadin.flow.exceptions;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;

/**
 * A {@link ValidationException} which can be used with a {@link PropertyInputGroup} to notify the {@link Property} and
 * the {@link Input} component which caused the validation error, if available.
 * <p>
 * When the {@link Property} and the {@link Input} component which caused the validation error are not available, the
 * validation exception should be intended as an overall (group) validation error.
 * </p>
 *
 * @since 5.2.0
 */
public class InputGroupValidationException extends ValidationException {

	private static final long serialVersionUID = -325010804803882796L;

	private final Property<?> property;
	private final Input<?> input;

	/**
	 * Constructor.
	 * @param cause Actual validation exception cause (not null)
	 */
	public InputGroupValidationException(ValidationException cause) {
		this(null, null, cause.getLocalizableMessage().orElse(null), cause.getCauses());
	}

	/**
	 * Constructor.
	 * @param cause Actual validation exception cause (not null)
	 */
	public InputGroupValidationException(Property<?> property, Input<?> input, ValidationException cause) {
		this(property, input, cause.getLocalizableMessage().orElse(null), cause.getCauses());
	}

	/**
	 * Constructor.
	 * @param causes Validation error causes
	 */
	public InputGroupValidationException(Collection<InputGroupValidationException> causes) {
		this(null, null, null, (causes == null) ? null : causes.stream().map(c -> c).collect(Collectors.toList()));
	}

	/**
	 * Constructor.
	 * @param property The property source of the validation exception (may be null)
	 * @param input The input source of the validation exception (may be null)
	 * @param causes Validation error causes
	 */
	public InputGroupValidationException(Property<?> property, Input<?> input,
			Collection<InputGroupValidationException> causes) {
		this(property, input, null, (causes == null) ? null : causes.stream().map(c -> c).collect(Collectors.toList()));
	}

	/**
	 * Constructor.
	 * @param property The property source of the validation exception (may be null)
	 * @param input The input source of the validation exception (may be null)
	 * @param message Validation error message (may be null)
	 * @param causes Validation error causes
	 */
	public InputGroupValidationException(Property<?> property, Input<?> input, Localizable message,
			Collection<ValidationException> causes) {
		super(message, causes);
		this.property = property;
		this.input = input;
	}

	/**
	 * Get the property source of the validation exception, if available.
	 * @return Optional property source of the validation exception
	 */
	public Optional<Property<?>> getProperty() {
		return Optional.ofNullable(property);
	}

	/**
	 * Get the input source of the validation exception, if available.
	 * @return Optional input source of the validation exception
	 */
	public Optional<Input<?>> getInput() {
		return Optional.ofNullable(input);
	}

	/**
	 * Get the input caption label, if available.
	 * @return Optional input caption label
	 */
	public Optional<String> getInputCaption() {
		Optional<String> caption = getInput().flatMap(i -> i.hasLabel()).map(l -> l.getLabel());
		if (caption.isPresent()) {
			return caption;
		}
		return getProperty().map(p -> LocalizationContext.translate(p, true));
	}

}
