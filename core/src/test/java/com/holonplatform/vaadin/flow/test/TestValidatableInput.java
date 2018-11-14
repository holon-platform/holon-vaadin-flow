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
package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Validatable;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.ValidationStatusEvent;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class TestValidatableInput {

	@Test
	public void testBuilders() {

		ValidatableInput<String> input = ValidatableInput.from(Input.string().build());
		assertNotNull(input);

		input = ValidatableInput.from(new TextField());
		assertNotNull(input);

		input = ValidatableInput.builder(Input.string().build()).build();
		assertNotNull(input);

		input = Input.string().validatable().build();
		assertNotNull(input);

	}

	@Test
	public void testValidators() {

		final ValidatableInput<String> input = ValidatableInput.from(Input.string().build());
		input.addValidator(Validator.max(2));

		assertTrue(input.isValid());
		Assertions.assertDoesNotThrow(() -> input.validate());

		input.setValue("aa");
		assertTrue(input.isValid());
		Assertions.assertDoesNotThrow(() -> input.validate());

		input.setValue("abc");
		assertFalse(input.isValid());
		Assertions.assertThrows(ValidationException.class, () -> input.validate());

		final ValidatableInput<String> input2 = ValidatableInput.from(Input.string().build());
		input2.addValidator(Validator.notBlank());
		assertFalse(input2.isValid());

		Assertions.assertDoesNotThrow(() -> input2.clear());

		final ValidatableInput<String> input3 = ValidatableInput.builder(Input.string().build())
				.withValidator(Validator.max(2)).build();
		assertTrue(input3.isValid());
		Assertions.assertDoesNotThrow(() -> input3.validate());

		input3.setValue("abc");
		assertFalse(input3.isValid());
		Assertions.assertThrows(ValidationException.class, () -> input3.validate());

		final ValidatableInput<String> input4 = ValidatableInput.builder(Input.string().build())
				.withValidator(Validatable
						.adapt(com.vaadin.flow.data.binder.Validator.from(v -> v == null || v.length() <= 1, "Max 1")))
				.build();
		assertTrue(input4.isValid());
		Assertions.assertDoesNotThrow(() -> input4.validate());

		input4.setValue("ab");
		assertFalse(input4.isValid());
		Assertions.assertThrows(ValidationException.class, () -> input4.validate());

		Assertions.assertThrows(ValidationException.class, () -> input4.getValueIfValid());

		input4.setValue("X");
		assertEquals("X", input4.getValueIfValid());

	}

	@Test
	public void testValidateOnValueChange() {

		final ValidatableInput<String> input = Input.string().validatable().withValidator(Validator.max(2)).build();
		assertFalse(input.isValidateOnValueChange());
		assertTrue(input.getComponent() instanceof HasValidation);
		final HasValidation hv1 = (HasValidation) input.getComponent();

		assertTrue(input.isValid());
		Assertions.assertDoesNotThrow(() -> input.validate());
		assertNull(getAsNull(hv1.getErrorMessage()));

		Assertions.assertDoesNotThrow(() -> {
			input.setValue("aaa");
		});
		assertNull(getAsNull(hv1.getErrorMessage()));

		assertFalse(input.isValid());
		assertNotNull(getAsNull(hv1.getErrorMessage()));
		Assertions.assertThrows(ValidationException.class, () -> input.validate());
		assertNotNull(getAsNull(hv1.getErrorMessage()));

		final ValidatableInput<String> input2 = Input.string().validatable().withValidator(Validator.max(2))
				.validateOnValueChange(true).build();
		final HasValidation hv2 = (HasValidation) input2.getComponent();

		assertTrue(input2.isValidateOnValueChange());
		assertTrue(input2.isValid());
		Assertions.assertDoesNotThrow(() -> input2.validate());
		assertNull(getAsNull(hv2.getErrorMessage()));

		input2.setValue("abc");
		assertNotNull(getAsNull(hv2.getErrorMessage()));

	}

	private static String getAsNull(String value) {
		if (value != null && value.length() == 0) {
			return null;
		}
		return value;
	}

	@Test
	public void testHasValidation() {

		TextField tf = new TextField();
		tf.setPattern("[0-9]*");
		tf.setPreventInvalidInput(false);

		ValidatableInput<String> input = ValidatableInput.from(tf);
		assertTrue(input.isValid());
		Assertions.assertDoesNotThrow(() -> input.validate());

		tf.setInvalid(true);
		assertFalse(input.isValid());
		Assertions.assertThrows(ValidationException.class, () -> input.validate());

		tf.setErrorMessage("Test error");
		ValidationException ve = Assertions.assertThrows(ValidationException.class, () -> input.validate());
		assertEquals("Test error", ve.getMessage());
	}

	@Test
	public void testValidationStatusHandler() {

		final ValidationEventValue<String> ve = new ValidationEventValue<>();

		final ValidatableInput<String> input = Input.string().validatable().withValidator(Validator.max(2))
				.validationStatusHandler(event -> {
					ve.event = event;
				}).build();

		assertNull(ve.event);

		input.validate();
		assertNotNull(ve.event);
		assertEquals(Status.VALID, ve.event.getStatus());

		input.setValue("aa");
		input.validate();
		assertNotNull(ve.event);
		assertEquals(Status.VALID, ve.event.getStatus());

		input.setValue("abc");
		assertFalse(input.isValid());
		assertNotNull(ve.event);
		assertEquals(Status.INVALID, ve.event.getStatus());

		input.clear();
		assertNotNull(ve.event);
		assertEquals(Status.UNRESOLVED, ve.event.getStatus());

	}

	@Test
	public void testRequired() {

		final ValidatableInput<String> input = Input.string().validatable().required().build();

		assertFalse(input.isValid());
		Assertions.assertThrows(ValidationException.class, () -> input.validate());

		input.setValue("x");
		assertTrue(input.isValid());
		Assertions.assertDoesNotThrow(() -> input.validate());

		input.clear();
		assertFalse(input.isValid());
		Assertions.assertThrows(ValidationException.class, () -> input.validate());

		final ValidatableInput<String> input2 = Input.string().validatable().required("REQUIRED").build();
		assertFalse(input2.isValid());
		ValidationException ve = Assertions.assertThrows(ValidationException.class, () -> input2.validate());
		assertEquals("REQUIRED", ve.getMessage());

	}

	@Test
	public void testLabelValidationStatusHandler() {

		final Div label = Components.label().build();

		ValidatableInput<String> input = Input.string().validatable().withValidator(Validator.max(2))
				.validationStatusHandler(ValidationStatusHandler.label(label)).build();
		assertFalse(label.isVisible());

		input.setValue("aaa");
		assertFalse(input.isValid());
		assertTrue(label.isVisible());
		assertNotNull(label.getText());
		assertTrue(label.getText().length() > 0);

		input.clear();
		assertFalse(label.isVisible());

		final Div label2 = Components.label().build();

		input = Input.string().validatable().withValidator(Validator.max(2))
				.validationStatusHandler(ValidationStatusHandler.label(label2, false)).build();
		assertTrue(label2.isVisible());

		input.setValue("aaa");
		assertFalse(input.isValid());
		assertTrue(label2.isVisible());
		assertNotNull(label2.getText());
		assertTrue(label2.getText().length() > 0);

	}

	private class ValidationEventValue<T> {

		ValidationStatusEvent<ValidatableInput<T>, T, Input<T>> event;

	}

}
