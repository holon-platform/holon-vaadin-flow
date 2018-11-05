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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Validator;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.ValidatableInput;

public class TestValidatableInputRenderer {

	@SuppressWarnings("unchecked")
	@Test
	public void testValidatableInputRenderer() {

		final Property<String> p1 = StringProperty.create("test");

		final ValidatableInput<String> i1 = p1.render(ValidatableInput.class);
		assertNotNull(i1);
		assertNotNull(i1.getComponent());
		i1.setValue("X");
		assertEquals("X", i1.getValue());

		i1.validate();

		assertTrue(i1.isValid());

		i1.addValidator(Validator.min(2));
		i1.setValue("A");

		assertFalse(i1.isValid());

	}

	@Test
	public void testPropertyValidators() {

		final Property<String> p1 = StringProperty.create("test").validator(Validator.min(2))
				.validator(Validator.max(3));

		ValidatableInput<String> i = ValidatableInput.create(p1);
		assertNotNull(i);

		i.setValue("A");
		assertFalse(i.isValid());

		i.setValue("ABCD");
		assertFalse(i.isValid());

		i.setValue("ABC");
		assertTrue(i.isValid());

		i.validate();
	}

}
