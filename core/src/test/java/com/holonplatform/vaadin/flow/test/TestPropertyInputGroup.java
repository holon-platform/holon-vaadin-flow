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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup.PropertyInputGroupBuilder;

public class TestPropertyInputGroup {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> "[" + pb.getValue(NAME) + "]");

	private static final PropertySet<?> SET = PropertySet.of(ID, NAME, VIRTUAL);

	@Test
	public void testBuilder() {

		PropertyInputGroupBuilder builder = PropertyInputGroup.builder(SET);
		assertNotNull(builder);

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertNotNull(group);

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		final AtomicInteger size = new AtomicInteger(0);
		group.getProperties().iterator().forEachRemaining(p -> size.incrementAndGet());
		assertEquals(3, size.get());

		group = PropertyInputGroup.builder(ID, NAME, VIRTUAL).build();
		assertNotNull(group);

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		final Input<String> input = Input.create(String.class).orElseThrow(() -> new RuntimeException("Failed to create Input"));

		final InputPropertyRenderer<String> renderer1 = InputPropertyRenderer.create(p -> input);

		group = PropertyInputGroup.builder(ID, NAME).bind(NAME, input).build();
		assertEquals(input, group.getInput(NAME).orElse(null));

		group = PropertyInputGroup.builder(ID, NAME).bind(NAME, renderer1).build();
		assertEquals(input, group.getInput(NAME).orElse(null));

		// TODO
		//group = PropertyInputGroup.builder(ID, NAME).bind(NAME, p -> input).build();
		//assertEquals(input, group.getInput(NAME).orElse(null));

		group = PropertyInputGroup.builder(ID, NAME).hidden(ID).build();
		assertTrue(group.hasProperty(ID));
		assertFalse(group.getInput(ID).isPresent());

		group = PropertyInputGroup.builder(ID, NAME).withPostProcessor((property, component) -> {
			if (ID.equals(property)) {
				component.hasEnabled().ifPresent(e -> e.setEnabled(false));
			}
		}).build();

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(ID).isPresent());
		assertFalse(group.getInput(ID).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));
		assertTrue(group.getInput(NAME).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));
	}

	@Test
	public void testGroup() {

		PropertyInputGroup group = PropertyInputGroup.builder(SET).build();
		assertNotNull(group);

		assertEquals(3, group.propertyStream().count());
		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		assertEquals(3, group.stream().filter(b -> b.getComponent() != null).count());

		assertTrue(group.getInput(ID).isPresent());
		assertTrue(group.getInput(NAME).isPresent());
		assertTrue(group.getInput(VIRTUAL).isPresent());

		assertTrue(group.isEmpty());
		assertNull(group.getValueIfPresent().orElse(null));

		group.setValue(PropertyBox.create(SET));
		assertNotNull(group.getValue());
		assertFalse(group.isEmpty());

		group.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test").build());
		assertNotNull(group.getValue());
		assertFalse(group.isEmpty());

		assertNotNull(group.getInput(ID).map(c -> c.getValue()).orElse(null));
		assertNotNull(group.getInput(NAME).map(c -> c.getValue()).orElse(null));
		assertNotNull(group.getInput(VIRTUAL).map(c -> c.getValue()).orElse(null));

		group.clear();
		assertTrue(group.isEmpty());

		final AtomicInteger fired = new AtomicInteger(0);

		group = PropertyInputGroup.builder(SET).withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		group.setValue(PropertyBox.builder(SET).set(ID, 2L).build());
		assertEquals(1, fired.get());

		group.clear();
		assertEquals(2, fired.get());

	}

}
