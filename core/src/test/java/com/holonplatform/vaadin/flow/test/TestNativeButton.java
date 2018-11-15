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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.NativeButtonBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.NativeButton;

public class TestNativeButton {

	@Test
	public void testBuilder() {

		NativeButtonBuilder builder = NativeButtonBuilder.create();
		assertNotNull(builder);

		NativeButton button = builder.build();
		assertNotNull(button);

		builder = Components.nativeButton();
		assertNotNull(builder);

	}

	@Test
	public void testComponent() {

		NativeButton button = NativeButtonBuilder.create().id("testid").build();
		assertTrue(button.getId().isPresent());
		assertEquals("testid", button.getId().get());

		button = NativeButtonBuilder.create().build();
		assertTrue(button.isVisible());

		button = NativeButtonBuilder.create().visible(true).build();
		assertTrue(button.isVisible());

		button = NativeButtonBuilder.create().visible(false).build();
		assertFalse(button.isVisible());

		button = NativeButtonBuilder.create().hidden().build();
		assertFalse(button.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		button = NativeButtonBuilder.create().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(button, true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		button = NativeButtonBuilder.create().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(button);
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		NativeButton button = NativeButtonBuilder.create().styleName("test").build();
		assertNotNull(button);

		assertTrue(button.getClassNames().contains("test"));

		button = NativeButtonBuilder.create().styleName("test1").styleName("test2").build();
		assertTrue(button.getClassNames().contains("test1"));
		assertTrue(button.getClassNames().contains("test2"));

		button = NativeButtonBuilder.create().styleNames("test").build();
		assertTrue(button.getClassNames().contains("test"));

		button = NativeButtonBuilder.create().styleNames("test1", "test2").build();
		assertTrue(button.getClassNames().contains("test1"));
		assertTrue(button.getClassNames().contains("test2"));

	}

	@Test
	public void testSize() {

		NativeButton button = NativeButtonBuilder.create().build();
		assertNull(button.getWidth());
		assertNull(button.getHeight());

		button = NativeButtonBuilder.create().width("50em").build();
		assertEquals("50em", button.getWidth());

		button = NativeButtonBuilder.create().width(50, Unit.EM).build();
		assertEquals("50em", button.getWidth());

		button = NativeButtonBuilder.create().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", button.getWidth());

		button = NativeButtonBuilder.create().height("50em").build();
		assertEquals("50em", button.getHeight());

		button = NativeButtonBuilder.create().height(50, Unit.EM).build();
		assertEquals("50em", button.getHeight());

		button = NativeButtonBuilder.create().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", button.getHeight());

		button = NativeButtonBuilder.create().width("50%").height("100%").build();
		assertEquals("50%", button.getWidth());
		assertEquals("100%", button.getHeight());

		button = NativeButtonBuilder.create().width("100px").widthUndefined().build();
		assertNull(button.getWidth());

		button = NativeButtonBuilder.create().height("100px").heightUndefined().build();
		assertNull(button.getHeight());

		button = NativeButtonBuilder.create().width("100px").height("100px").sizeUndefined().build();
		assertNull(button.getWidth());
		assertNull(button.getHeight());

		button = NativeButtonBuilder.create().fullWidth().build();
		assertEquals("100%", button.getWidth());
		assertNull(button.getHeight());

		button = NativeButtonBuilder.create().fullHeight().build();
		assertEquals("100%", button.getHeight());
		assertNull(button.getWidth());

		button = NativeButtonBuilder.create().fullWidth().fullHeight().build();
		assertEquals("100%", button.getWidth());
		assertEquals("100%", button.getHeight());

		button = NativeButtonBuilder.create().fullSize().build();
		assertEquals("100%", button.getWidth());
		assertEquals("100%", button.getHeight());

	}

	@Test
	public void testEnabled() {

		NativeButton button = NativeButtonBuilder.create().build();
		assertTrue(button.isEnabled());

		button = NativeButtonBuilder.create().enabled(true).build();
		assertTrue(button.isEnabled());

		button = NativeButtonBuilder.create().enabled(false).build();
		assertFalse(button.isEnabled());

		button = NativeButtonBuilder.create().disabled().build();
		assertFalse(button.isEnabled());
	}

	@Test
	public void testText() {

		NativeButton button = NativeButtonBuilder.create().text(Localizable.builder().message("test").build()).build();
		assertEquals("test", button.getText());

		button = NativeButtonBuilder.create().text("test").build();
		assertEquals("test", button.getText());

		button = NativeButtonBuilder.create().text("test", "test.code").build();
		assertEquals("test", button.getText());

		button = NativeButtonBuilder.create().text("test", "test.code", "arg").build();
		assertEquals("test", button.getText());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create()
					.text(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", button2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create().text("test", "test.code").build();
			assertEquals("TestUS", button2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create().deferLocalization().text("test", "test.code").build();
			assertEquals("test", button2.getText());
			ComponentUtil.onComponentAttach(button2, true);
			assertEquals("TestUS", button2.getText());
		});

	}

	@Test
	public void testDescription() {

		NativeButton button = NativeButtonBuilder.create().title(Localizable.builder().message("test").build()).build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().title("test").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().title("test", "test.code").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().title("test", "test.code", "arg").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().description(Localizable.builder().message("test").build()).build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().description("test").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().description("test", "test.code").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = NativeButtonBuilder.create().description("test", "test.code", "arg").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create()
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create().title("test", "test.code").build();
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create().deferLocalization().title("test", "test.code").build();
			assertEquals("test", button2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(button2, true);
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NativeButton button2 = NativeButtonBuilder.create().withDeferredLocalization(true).text("test", "test.code")
					.title("test", "test.code").build();
			assertEquals("test", button2.getText());
			assertEquals("test", button2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(button2, true);
			assertEquals("TestUS", button2.getText());
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

	}

	@Test
	public void testFocus() {

		NativeButton button = NativeButtonBuilder.create().tabIndex(77).build();
		assertEquals(77, button.getTabIndex());

	}

}
