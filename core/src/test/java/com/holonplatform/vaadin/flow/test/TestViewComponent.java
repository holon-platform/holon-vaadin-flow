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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.textfield.TextField;

public class TestViewComponent {

	@Test
	public void testBuilders() {

		ViewComponentBuilder<String> builder1 = ViewComponentBuilder.create(String.class);
		assertNotNull(builder1);
		ViewComponent<String> vc = builder1.build();
		assertNotNull(vc);

		ViewComponentBuilder<Integer> builder2 = ViewComponentBuilder.create(v -> Objects.toString(v));
		assertNotNull(builder2);
		ViewComponent<Integer> vc2 = builder2.build();
		assertNotNull(vc2);

		ViewComponentBuilder<Double> builder3 = ViewComponentBuilder.<Double>create(v -> Objects.toString(v));
		assertNotNull(builder3);
		ViewComponent<Double> vc3 = builder3.build();
		assertNotNull(vc3);

		ViewComponentBuilder<String> builder4 = ViewComponentBuilder.create(StringProperty.create("test"));
		assertNotNull(builder4);
		ViewComponent<String> vc4 = builder4.build();
		assertNotNull(vc4);

		ViewComponentBuilder<String> builder5 = ViewComponent.builder(String.class);
		assertNotNull(builder5);
		ViewComponent<String> vc5 = builder1.build();
		assertNotNull(vc5);

		ViewComponentBuilder<Integer> builder6 = ViewComponent.builder(v -> Objects.toString(v));
		assertNotNull(builder6);
		ViewComponent<Integer> vc6 = builder6.build();
		assertNotNull(vc6);

		ViewComponentBuilder<Integer> builder7 = ViewComponent.builder(NumericProperty.integerType("test"));
		assertNotNull(builder7);
		ViewComponent<Integer> vc7 = builder7.build();
		assertNotNull(vc7);

		ViewComponent<Integer> vc8 = ViewComponent.create(NumericProperty.integerType("test"));
		assertNotNull(vc8);

		ViewComponentBuilder<String> builder9 = Components.view.component(String.class);
		assertNotNull(builder9);
		ViewComponent<String> vc9 = builder9.build();
		assertNotNull(vc9);

		ViewComponentBuilder<Integer> builder10 = Components.view.component(v -> Objects.toString(v));
		assertNotNull(builder10);
		ViewComponent<Integer> vc10 = builder10.build();
		assertNotNull(vc10);

		ViewComponentBuilder<Integer> builder11 = Components.view.component(NumericProperty.integerType("test"));
		assertNotNull(builder11);
		ViewComponent<Integer> vc11 = builder11.build();
		assertNotNull(vc11);

		ViewComponent<Integer> vc12 = Components.view.property(NumericProperty.integerType("test"));
		assertNotNull(vc12);
	}

	@Test
	public void testComponent() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).id("testid").build();
		assertNotNull(vc.getComponent());
		assertTrue(vc.getComponent().getId().isPresent());
		assertEquals("testid", vc.getComponent().getId().get());

		vc = ViewComponent.builder(String.class).visible(true).build();
		assertTrue(vc.isVisible());

		vc = ViewComponent.builder(String.class).visible(false).build();
		assertFalse(vc.isVisible());

		vc = ViewComponent.builder(String.class).hidden().build();
		assertFalse(vc.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		vc = ViewComponent.builder(String.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(vc.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		vc = ViewComponent.builder(String.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(vc.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).styleName("test").build();
		assertNotNull(vc);

		assertTrue(ComponentTestUtils.getClassNames(vc).contains("test"));

		vc = ViewComponent.builder(String.class).styleName("test1").styleName("test2").build();
		assertTrue(ComponentTestUtils.getClassNames(vc).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(vc).contains("test2"));

		vc = ViewComponent.builder(String.class).styleNames("test").build();
		assertTrue(ComponentTestUtils.getClassNames(vc).contains("test"));

		vc = ViewComponent.builder(String.class).styleNames("test1", "test2").build();
		assertTrue(ComponentTestUtils.getClassNames(vc).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(vc).contains("test2"));

	}

	@Test
	public void testSize() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).build();
		assertNull(ComponentTestUtils.getWidth(vc));
		assertNull(ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(vc));

		vc = ViewComponent.builder(String.class).width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(vc));

		vc = ViewComponent.builder(String.class).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(vc));

		vc = ViewComponent.builder(String.class).height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(vc));
		assertEquals("100%", ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).width("100px").widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(vc));

		vc = ViewComponent.builder(String.class).height("100px").heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).width("100px").height("100px").sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(vc));
		assertNull(ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(vc));
		assertNull(ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(vc));
		assertNull(ComponentTestUtils.getWidth(vc));

		vc = ViewComponent.builder(String.class).fullWidth().fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getWidth(vc));
		assertEquals("100%", ComponentTestUtils.getHeight(vc));

		vc = ViewComponent.builder(String.class).fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(vc));
		assertEquals("100%", ComponentTestUtils.getHeight(vc));

	}

	@Test
	public void testEnabled() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).build();
		assertTrue(ComponentTestUtils.isEnabled(vc));

		vc = ViewComponent.builder(String.class).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(vc));

		vc = ViewComponent.builder(String.class).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(vc));

		vc = ViewComponent.builder(String.class).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(vc));
	}

	@Test
	public void testLabel() {

		ViewComponent<String> vc = ViewComponent.builder(String.class)
				.label(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getLabel(vc));

		vc = ViewComponent.builder(String.class).label("test").build();
		assertEquals("test", ComponentTestUtils.getLabel(vc));

		vc = ViewComponent.builder(String.class).label("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getLabel(vc));

		vc = ViewComponent.builder(String.class).label("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getLabel(vc));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class)
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(vc2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).label("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getLabel(vc2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).deferLocalization()
					.label("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(vc2));
			ComponentUtil.onComponentAttach(vc2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(vc2));
		});

	}

	@Test
	public void testDescription() {

		ViewComponent<String> vc = ViewComponent.builder(String.class)
				.title(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).title("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).title("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).title("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).description(Localizable.builder().message("test").build()).build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).description("test").build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).description("test", "test.code").build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		vc = ViewComponent.builder(String.class).description("test", "test.code", "arg").build();
		assertEquals("test", ComponentTestUtils.getTitle(vc));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class)
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(vc2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).title("test", "test.code").build();
			assertEquals("TestUS", ComponentTestUtils.getTitle(vc2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).deferLocalization()
					.title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getTitle(vc2));
			ComponentUtil.onComponentAttach(vc2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getTitle(vc2));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).withDeferredLocalization(true)
					.label("test", "test.code").title("test", "test.code").build();
			assertEquals("test", ComponentTestUtils.getLabel(vc2));
			assertEquals("test", ComponentTestUtils.getTitle(vc2));
			ComponentUtil.onComponentAttach(vc2.getComponent(), true);
			assertEquals("TestUS", ComponentTestUtils.getLabel(vc2));
			assertEquals("TestUS", ComponentTestUtils.getTitle(vc2));
		});

	}

	@Test
	public void testPropertyConfig() {
		ViewComponent<Integer> vc = ViewComponent.create(NumericProperty.integerType("test").message("Test"));
		assertEquals("Test", ComponentTestUtils.getLabel(vc));

	}

	@Test
	public void testValueHolder() {

		ViewComponent<String> vc = ViewComponent.create(String.class);

		assertNull(vc.getValue());
		assertTrue(vc.isEmpty());

		vc = ViewComponent.builder(String.class).withValue("test").build();
		assertFalse(vc.isEmpty());
		assertEquals("test", vc.getValue());

		vc = ViewComponent.create(String.class);

		vc.setValue("test2");
		assertFalse(vc.isEmpty());
		assertEquals("test2", vc.getValue());
		assertTrue(vc.getValueIfPresent().isPresent());
		assertEquals("test2", vc.getValueIfPresent().orElse(null));

		vc.clear();
		assertNull(vc.getValue());
		assertTrue(vc.isEmpty());

		ViewComponent<Integer> vci = ViewComponent.create(Integer.class);
		assertNull(vc.getValue());
		assertTrue(vc.isEmpty());

		vci.setValue(7);
		assertFalse(vci.isEmpty());
		assertEquals(Integer.valueOf(7), vci.getValue());
		assertTrue(vci.getValueIfPresent().isPresent());
		assertEquals(Integer.valueOf(7), vci.getValueIfPresent().orElse(null));

		vci.clear();
		assertNull(vci.getValue());
		assertTrue(vci.isEmpty());

		final AtomicInteger fired = new AtomicInteger(0);

		final AtomicInteger ov = new AtomicInteger(-1);
		final AtomicInteger nv = new AtomicInteger(-1);

		vci.addValueChangeListener(e -> {
			fired.incrementAndGet();
			ov.set((e.getOldValue() == null) ? -1 : e.getOldValue());
			nv.set((e.getValue() == null) ? -1 : e.getValue());
		});

		assertEquals(0, fired.get());
		assertEquals(-1, ov.get());
		assertEquals(-1, nv.get());

		vci.setValue(7);

		assertEquals(1, fired.get());
		assertEquals(-1, ov.get());
		assertEquals(7, nv.get());

		vci.clear();

		assertEquals(2, fired.get());
		assertEquals(7, ov.get());
		assertEquals(-1, nv.get());

		final AtomicInteger fired2 = new AtomicInteger(0);

		final StringValue osv = new StringValue();
		final StringValue nsv = new StringValue();

		vc = ViewComponent.builder(String.class).withValueChangeListener(e -> {
			fired2.incrementAndGet();
			osv.value = e.getOldValue();
			nsv.value = e.getValue();
		}).build();

		assertEquals(0, fired2.get());
		assertNull(osv.value);
		assertNull(nsv.value);

		vc.setValue("test");

		assertEquals(1, fired2.get());
		assertNull(osv.value);
		assertEquals("test", nsv.value);

		fired2.set(0);

		vc = ViewComponent.builder(String.class).withValue("test2").withValueChangeListener(e -> {
			fired2.incrementAndGet();
		}).build();

		assertEquals(0, fired2.get());

		vc = ViewComponent.builder(String.class).withValueChangeListener(e -> {
			fired2.incrementAndGet();
		}).withValue("test2").build();

		assertEquals(0, fired2.get());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPropertyRenderer() {
		final StringProperty p1 = StringProperty.create("test1");

		ViewComponent<String> v1 = p1.render(ViewComponent.class);
		assertNotNull(v1);

		v1.setValue("a");
		assertEquals("a", v1.getValue());

		final BooleanProperty p2 = BooleanProperty.create("test2").message("caption");

		ViewComponent<?> v2 = p2.render(ViewComponent.class);
		assertNotNull(v2);

		assertEquals("caption", ComponentTestUtils.getLabel(v2));

	}

	@Test
	public void testAdapter() {

		final TextField tf = new TextField();

		ViewComponent<String> vc = ViewComponent.adapt(String.class, tf, (field, value) -> {
			field.setValue((value != null) ? value : "");
		}).build();

		assertNull(vc.getValue());
		assertTrue(vc.isEmpty());
		assertTrue(tf.isEmpty());

		vc.setValue("test");
		assertFalse(vc.isEmpty());
		assertFalse(tf.isEmpty());
		assertEquals("test", vc.getValue());
		assertEquals("test", tf.getValue());

		vc.clear();
		assertNull(vc.getValue());
		assertTrue(vc.isEmpty());
		assertTrue(tf.isEmpty());

		final TextFieldValue tfv = new TextFieldValue();

		vc = ViewComponent.adapt(String.class, value -> {
			if (value != null) {
				TextField t = new TextField();
				t.setValue(value);
				tfv.value = t;
				return t;
			}
			return null;
		}).build();

		assertNull(vc.getValue());
		assertTrue(vc.isEmpty());
		assertNull(tfv.value);

		vc.setValue("test");
		assertFalse(vc.isEmpty());
		assertEquals("test", vc.getValue());
		assertNotNull(tfv.value);
		assertEquals("test", tfv.value.getValue());
	}

	@Test
	public void testAdapterBuilder() {

		ViewComponent<String> vc = ViewComponent.adapt(String.class, new TextField(), (field, value) -> {
			field.setValue((value != null) ? value : "");
		}).width("50em").label("test").enabled(false).build();

		assertEquals("50em", ComponentTestUtils.getWidth(vc));
		assertEquals("test", ComponentTestUtils.getLabel(vc));
		assertFalse(ComponentTestUtils.isEnabled(vc));
	}

	private class StringValue {

		String value;

	}

	private class TextFieldValue {

		TextField value;

	}

}
