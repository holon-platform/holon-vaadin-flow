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
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.ViewComponent.ViewComponentPropertyRenderer;

public class TestPropertyViewGroup {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> "[" + pb.getValue(NAME) + "]");

	private static final PropertySet<?> SET = PropertySet.of(ID, NAME, VIRTUAL);

	@Test
	public void testBuilder() {

		PropertyViewGroupBuilder builder = PropertyViewGroup.builder(SET);
		assertNotNull(builder);
		
		builder = Components.view.propertyGroup(SET);
		assertNotNull(builder);

		PropertyViewGroup group = PropertyViewGroup.builder(SET).build();
		assertNotNull(group);

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		final AtomicInteger size = new AtomicInteger(0);
		group.getProperties().iterator().forEachRemaining(p -> size.incrementAndGet());
		assertEquals(3, size.get());

		group = PropertyViewGroup.builder(ID, NAME, VIRTUAL).build();
		assertNotNull(group);

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		final ViewComponent<String> vc = ViewComponent.create(String.class);

		final ViewComponentPropertyRenderer<String> renderer1 = ViewComponentPropertyRenderer.create(p -> vc);

		group = PropertyViewGroup.builder(ID, NAME).bind(NAME, vc).build();
		assertEquals(vc, group.getViewComponent(NAME).orElse(null));

		group = PropertyViewGroup.builder(ID, NAME).bind(NAME, renderer1).build();
		assertEquals(vc, group.getViewComponent(NAME).orElse(null));

		group = PropertyViewGroup.builder(ID, NAME).bind(NAME, p -> vc).build();
		assertEquals(vc, group.getViewComponent(NAME).orElse(null));

		group = PropertyViewGroup.builder(ID, NAME).hidden(ID).build();
		assertTrue(group.hasProperty(ID));
		assertFalse(group.getViewComponent(ID).isPresent());

		group = PropertyViewGroup.builder(ID, NAME).withPostProcessor((property, component) -> {
			if (ID.equals(property)) {
				component.hasEnabled().ifPresent(e -> e.setEnabled(false));
			}
		}).build();

		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.getViewComponent(ID).isPresent());
		assertTrue(group.getViewComponent(ID).isPresent());
		assertFalse(group.getViewComponent(ID).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));
		assertTrue(group.getViewComponent(NAME).get().hasEnabled().map(e -> e.isEnabled()).orElse(false));

		final ViewComponent<Number> nvc = ViewComponent.create(Number.class);
		final NumericProperty<Double> np = NumericProperty.doubleType("test");

		ViewComponentPropertyRenderer<Number> rnd2 = ViewComponentPropertyRenderer.create(p -> nvc);
		assertEquals(nvc, rnd2.render(np));
	}

	@Test
	public void testGroup() {

		PropertyViewGroup group = PropertyViewGroup.builder(SET).build();
		assertNotNull(group);

		assertEquals(3, group.propertyStream().count());
		assertTrue(group.hasProperty(ID));
		assertTrue(group.hasProperty(NAME));
		assertTrue(group.hasProperty(VIRTUAL));

		assertEquals(3, group.stream().filter(b -> b.getComponent() != null).count());

		assertTrue(group.getViewComponent(ID).isPresent());
		assertTrue(group.getViewComponent(NAME).isPresent());
		assertTrue(group.getViewComponent(VIRTUAL).isPresent());

		assertTrue(group.isEmpty());
		assertNull(group.getValue());
		assertNull(group.getValueIfPresent().orElse(null));

		group.setValue(PropertyBox.create(SET));
		assertNotNull(group.getValue());
		assertFalse(group.isEmpty());

		group.setValue(PropertyBox.builder(SET).set(ID, 1L).set(NAME, "test").build());
		assertNotNull(group.getValue());
		assertFalse(group.isEmpty());

		assertNotNull(group.getViewComponent(ID).map(c -> c.getValue()).orElse(null));
		assertNotNull(group.getViewComponent(NAME).map(c -> c.getValue()).orElse(null));
		assertNotNull(group.getViewComponent(VIRTUAL).map(c -> c.getValue()).orElse(null));

		group.clear();
		assertTrue(group.isEmpty());
		assertNull(group.getValue());

		final AtomicInteger fired = new AtomicInteger(0);

		group = PropertyViewGroup.builder(SET).withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		group.setValue(PropertyBox.builder(SET).set(ID, 2L).build());
		assertEquals(1, fired.get());

		group.clear();
		assertEquals(2, fired.get());

	}

}
