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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyViewForm;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TestPropertyViewForm {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id").message("Id").messageCode("id.code");
	private static final StringProperty NAME = StringProperty.create("name").message("Name").messageCode("test.code");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> "[" + pb.getValue(NAME) + "]");

	private static final PropertySet<?> SET = PropertySet.of(ID, NAME, VIRTUAL);

	@Test
	public void testBuilder() {

		final VerticalLayout layout = new VerticalLayout();

		PropertyViewForm form = PropertyViewForm.builder(layout, SET).build();
		assertNotNull(form);

		assertEquals(layout, form.getComponent());

		form = Components.view.form(layout, SET).build();
		assertNotNull(form);

		final VerticalLayout layout2 = new VerticalLayout();
		layout2.add(form.getComponent());

		assertEquals(layout, layout2.getComponentAt(0));

		form = PropertyViewForm.formLayout(SET).build();
		assertNotNull(form);
		assertNotNull(form.getComponent());

		form = PropertyViewForm.verticalLayout(SET).build();
		assertNotNull(form);
		assertNotNull(form.getComponent());

		form = PropertyViewForm.horizontalLayout(SET).build();
		assertNotNull(form);
		assertNotNull(form.getComponent());
	}

	@Test
	public void testComponents() {

		PropertyViewForm form = PropertyViewForm.formLayout(SET).build();
		assertNotNull(form);

		assertTrue(form.hasProperty(ID));
		assertTrue(form.hasProperty(NAME));
		assertTrue(form.hasProperty(VIRTUAL));

		assertEquals(3, StreamSupport.stream(form.getProperties().spliterator(), false).count());

		assertTrue(form.getViewComponent(ID).isPresent());
		assertTrue(form.getViewComponent(NAME).isPresent());
		assertTrue(form.getViewComponent(VIRTUAL).isPresent());

		assertEquals(3, form.getBindings().count());

		final ViewComponent<String> vc1 = ViewComponent.create(NAME);
		final ViewComponent<Long> vc2 = ViewComponent.create(ID);

		form = PropertyViewForm.formLayout(SET).bind(NAME, vc1).build();
		assertNotNull(form);

		assertTrue(form.getViewComponent(NAME).isPresent());
		assertEquals(vc1, form.getViewComponent(NAME).get());

		form = PropertyViewForm.formLayout(SET).bind(NAME, vc1).bind(ID, p -> vc2).build();
		assertNotNull(form);

		assertTrue(form.getViewComponent(NAME).isPresent());
		assertEquals(vc1, form.getViewComponent(NAME).get());
		assertTrue(form.getViewComponent(ID).isPresent());
		assertEquals(vc2, form.getViewComponent(ID).get());

		form = PropertyViewForm.formLayout(SET).withPostProcessor((property, component) -> {
			if (ID.equals(property)) {
				component.hasStyle().ifPresent(hs -> hs.addClassName("post-processed"));
			}
		}).build();
		assertNotNull(form);

		assertTrue(form.getViewComponent(ID).isPresent());
		assertTrue(form.getViewComponent(NAME).isPresent());
		assertTrue(ComponentTestUtils.getClassNames(form.getViewComponent(ID).get()).contains("post-processed"));
		assertFalse(ComponentTestUtils.getClassNames(form.getViewComponent(NAME).get()).contains("post-processed"));

		assertThrows(NoSuitableRendererAvailableException.class, () -> PropertyViewForm.formLayout(SET)
				.usePropertyRendererRegistry(PropertyRendererRegistry.create(false)).build());

	}

	@Test
	public void testHiddenComponents() {

		PropertyViewForm form = PropertyViewForm.formLayout(SET).hidden(ID).build();
		assertNotNull(form);

		assertTrue(form.hasProperty(ID));
		assertFalse(form.getViewComponent(ID).isPresent());

		form.setValue(PropertyBox.builder(SET).set(ID, 7L).set(NAME, "test").build());

		PropertyBox value = form.getValue();
		assertTrue(value.contains(ID));
		assertEquals(Long.valueOf(7), value.getValue(ID));

	}

	@Test
	public void testValueHolder() {

		PropertyViewForm form = PropertyViewForm.formLayout(SET).build();
		assertNotNull(form);

		assertTrue(form.isEmpty());
		assertNull(form.getValue());
		assertFalse(form.getValueIfPresent().isPresent());

		assertTrue(form.hasProperty(ID));
		assertTrue(form.hasProperty(NAME));
		assertTrue(form.hasProperty(VIRTUAL));

		form.setValue(PropertyBox.builder(SET).set(ID, 7L).set(NAME, "test").build());

		assertFalse(form.isEmpty());
		assertNotNull(form.getValue());
		assertTrue(form.getValueIfPresent().isPresent());

		PropertyBox value = form.getValue();
		assertEquals(Long.valueOf(7), value.getValue(ID));
		assertEquals("test", value.getValue(NAME));
		assertEquals("[test]", value.getValue(VIRTUAL));

		assertTrue(form.getViewComponent(ID).isPresent());
		assertTrue(form.getViewComponent(NAME).isPresent());
		assertTrue(form.getViewComponent(VIRTUAL).isPresent());

		assertEquals(Long.valueOf(7), form.getViewComponent(ID).get().getValue());
		assertEquals("test", form.getViewComponent(NAME).get().getValue());
		assertEquals("[test]", form.getViewComponent(VIRTUAL).get().getValue());

		form.clear();

		assertTrue(form.isEmpty());
		assertNull(form.getValue());
		assertFalse(form.getValueIfPresent().isPresent());

		final AtomicInteger fired = new AtomicInteger(0);

		form.addValueChangeListener(e -> fired.incrementAndGet());
		assertEquals(0, fired.get());

		form.setValue(PropertyBox.builder(SET).set(ID, 7L).set(NAME, "test").build());
		assertEquals(1, fired.get());

		form.clear();
		assertEquals(2, fired.get());

		fired.set(0);

		form = PropertyViewForm.formLayout(SET).withValueChangeListener(e -> fired.incrementAndGet()).build();
		assertEquals(0, fired.get());

		form.setValue(PropertyBox.builder(SET).set(ID, 7L).set(NAME, "test").build());
		assertEquals(1, fired.get());

		form.clear();
		assertEquals(2, fired.get());

	}

	@Test
	public void testComposer() {

		VerticalLayout layout = new VerticalLayout();

		PropertyViewForm form = PropertyViewForm.builder(layout, SET).build();
		assertNotNull(form);
		assertEquals(0, layout.getComponentCount());

		form.compose();
		assertEquals(3, layout.getComponentCount());

		layout = new VerticalLayout();

		form = PropertyViewForm.builder(layout, SET).build();
		assertNotNull(form);
		assertEquals(0, layout.getComponentCount());

		ComponentUtil.onComponentAttach(form.getComponent(), true);
		assertEquals(3, layout.getComponentCount());

		layout = new VerticalLayout();

		form = PropertyViewForm.builder(layout, SET).composeOnAttach(false).build();
		assertNotNull(form);
		assertEquals(0, layout.getComponentCount());

		ComponentUtil.onComponentAttach(form.getComponent(), true);
		assertEquals(0, layout.getComponentCount());

		form.compose();
		assertEquals(3, layout.getComponentCount());

		layout = new VerticalLayout();

		form = PropertyViewForm.builder(layout, SET).composer((content, source) -> {
			source.getComponents().forEach(c -> content.add(c));
		}).initializer(content -> {
			content.setAlignItems(Alignment.STRETCH);
		}).build();

		form.compose();
		assertEquals(3, layout.getComponentCount());
		assertEquals(Alignment.STRETCH, layout.getAlignItems());

		layout = new VerticalLayout();

		form = PropertyViewForm.builder(layout, SET).composer((content, source) -> {
			source.getElement(ID).ifPresent(c -> content.add(c.getComponent()));
			source.getElement(NAME).ifPresent(c -> content.add(c.getComponent()));
		}).build();

		form.compose();
		assertEquals(2, layout.getComponentCount());

		layout = new VerticalLayout();

		final ViewComponent<Long> vc1 = ViewComponent.create(ID);

		form = PropertyViewForm.builder(layout, SET).bind(ID, vc1).composer((content, source) -> {
			source.getElement(ID).ifPresent(c -> content.add(c.getComponent()));
		}).build();

		form.compose();
		assertEquals(1, layout.getComponentCount());

		assertEquals(vc1.getComponent(), layout.getComponentAt(0));

		form.compose();
		assertEquals(1, layout.getComponentCount());

	}

	@Test
	public void testPropertyCaptions() {

		PropertyViewForm form = PropertyViewForm.formLayout(SET).build();
		assertNotNull(form);

		assertTrue(form.getViewComponent(ID).isPresent());
		assertTrue(form.getViewComponent(NAME).isPresent());
		assertTrue(form.getViewComponent(VIRTUAL).isPresent());

		assertEquals("Id", ComponentTestUtils.getLabel(form.getViewComponent(ID).get()));
		assertEquals("Name", ComponentTestUtils.getLabel(form.getViewComponent(NAME).get()));

		form = PropertyViewForm.formLayout(SET).propertyCaption(NAME, "TheName").hidePropertyCaption(ID).build();

		assertEquals("", Optional.ofNullable(ComponentTestUtils.getLabel(form.getViewComponent(ID).get())).orElse(""));
		assertEquals("TheName", ComponentTestUtils.getLabel(form.getViewComponent(NAME).get()));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			PropertyViewForm form2 = PropertyViewForm.formLayout(SET).build();
			assertEquals("Id", ComponentTestUtils.getLabel(form2.getViewComponent(ID).get()));
			assertEquals("TestUS", ComponentTestUtils.getLabel(form2.getViewComponent(NAME).get()));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			PropertyViewForm form2 = PropertyViewForm.formLayout(SET).propertyCaption(NAME, "MyName")
					.propertyCaption(ID, "test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(form2.getViewComponent(ID).get()));
			assertEquals("MyName", ComponentTestUtils.getLabel(form2.getViewComponent(NAME).get()));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			PropertyViewForm form2 = PropertyViewForm.formLayout(SET)
					.propertyCaption(ID, Localizable.builder().message("test").messageCode("test.code").build())
					.build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(form2.getViewComponent(ID).get()));
			assertEquals("TestUS", ComponentTestUtils.getLabel(form2.getViewComponent(NAME).get()));
		});

	}

	@Test
	public void testComponent() {

		PropertyViewForm form = PropertyViewForm.formLayout(SET).id("testid").build();
		assertTrue(form.getComponent().getId().isPresent());
		assertEquals("testid", form.getComponent().getId().get());

		form = PropertyViewForm.formLayout(SET).build();
		assertTrue(form.getComponent().isVisible());

		form = PropertyViewForm.formLayout(SET).visible(true).build();
		assertTrue(form.getComponent().isVisible());

		form = PropertyViewForm.formLayout(SET).visible(false).build();
		assertFalse(form.getComponent().isVisible());

		form = PropertyViewForm.formLayout(SET).hidden().build();
		assertFalse(form.getComponent().isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		form = PropertyViewForm.formLayout(SET).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(form.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		form = PropertyViewForm.formLayout(SET).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(form.getComponent());
		assertTrue(detached.get());

	}

}
