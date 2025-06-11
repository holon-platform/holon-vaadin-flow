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
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.ElementConstants;

public class TestButton {

	@Test
	public void testConfigurator() {

		final Button button = new Button();

		BaseButtonConfigurator cfg = ButtonConfigurator.configure(button);
		assertNotNull(cfg);

		cfg.id("testid");
		assertTrue(button.getId().isPresent());
		assertEquals("testid", button.getId().get());

		cfg = Components.configure(button);
		assertNotNull(cfg);

		cfg.id("testid");
		assertTrue(button.getId().isPresent());
		assertEquals("testid", button.getId().get());
	}

	@Test
	public void testBuilder() {

		ButtonBuilder builder = ButtonBuilder.create();
		assertNotNull(builder);

		Button button = builder.build();
		assertNotNull(button);

		builder = Components.button();
		assertNotNull(builder);

	}

	@Test
	public void testComponent() {

		Button button = ButtonBuilder.create().id("testid").build();
		assertTrue(button.getId().isPresent());
		assertEquals("testid", button.getId().get());

		button = ButtonBuilder.create().build();
		assertTrue(button.isVisible());

		button = ButtonBuilder.create().visible(true).build();
		assertTrue(button.isVisible());

		button = ButtonBuilder.create().visible(false).build();
		assertFalse(button.isVisible());

		button = ButtonBuilder.create().hidden().build();
		assertFalse(button.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		button = ButtonBuilder.create().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(button, true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		button = ButtonBuilder.create().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(button);
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Button button = ButtonBuilder.create().styleName("test").build();
		assertNotNull(button);

		assertTrue(button.getClassNames().contains("test"));

		button = ButtonBuilder.create().styleName("test1").styleName("test2").build();
		assertTrue(button.getClassNames().contains("test1"));
		assertTrue(button.getClassNames().contains("test2"));

		button = ButtonBuilder.create().styleNames("test").build();
		assertTrue(button.getClassNames().contains("test"));

		button = ButtonBuilder.create().styleNames("test1", "test2").build();
		assertTrue(button.getClassNames().contains("test1"));
		assertTrue(button.getClassNames().contains("test2"));

	}

	@Test
	public void testSize() {

		Button button = ButtonBuilder.create().build();
		assertNull(button.getWidth());
		assertNull(button.getHeight());

		button = ButtonBuilder.create().width("50em").build();
		assertEquals("50em", button.getWidth());

		button = ButtonBuilder.create().width(50, Unit.EM).build();
		assertEquals("50em", button.getWidth());

		button = ButtonBuilder.create().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", button.getWidth());

		button = ButtonBuilder.create().height("50em").build();
		assertEquals("50em", button.getHeight());

		button = ButtonBuilder.create().height(50, Unit.EM).build();
		assertEquals("50em", button.getHeight());

		button = ButtonBuilder.create().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", button.getHeight());

		button = ButtonBuilder.create().width("50%").height("100%").build();
		assertEquals("50%", button.getWidth());
		assertEquals("100%", button.getHeight());

		button = ButtonBuilder.create().width("100px").widthUndefined().build();
		assertNull(button.getWidth());

		button = ButtonBuilder.create().height("100px").heightUndefined().build();
		assertNull(button.getHeight());

		button = ButtonBuilder.create().width("100px").height("100px").sizeUndefined().build();
		assertNull(button.getWidth());
		assertNull(button.getHeight());

		button = ButtonBuilder.create().fullWidth().build();
		assertEquals("100%", button.getWidth());
		assertNull(button.getHeight());

		button = ButtonBuilder.create().fullHeight().build();
		assertEquals("100%", button.getHeight());
		assertNull(button.getWidth());

		button = ButtonBuilder.create().fullWidth().fullHeight().build();
		assertEquals("100%", button.getWidth());
		assertEquals("100%", button.getHeight());

		button = ButtonBuilder.create().fullSize().build();
		assertEquals("100%", button.getWidth());
		assertEquals("100%", button.getHeight());

	}

	@Test
	public void testEnabled() {

		Button button = ButtonBuilder.create().build();
		assertTrue(button.isEnabled());

		button = ButtonBuilder.create().enabled(true).build();
		assertTrue(button.isEnabled());

		button = ButtonBuilder.create().enabled(false).build();
		assertFalse(button.isEnabled());

		button = ButtonBuilder.create().disabled().build();
		assertFalse(button.isEnabled());
	}

	@Test
	public void testIcon() {

		Button button = ButtonBuilder.create().icon(VaadinIcon.ASTERISK).build();
		assertNotNull(button.getIcon());
		assertTrue(button.getIcon() instanceof Icon);

		button = ButtonBuilder.create().icon(new Icon(VaadinIcon.ASTERISK)).build();
		assertNotNull(button.getIcon());
		assertTrue(button.getIcon() instanceof Icon);

		button = ButtonBuilder.create().iconConfigurator(VaadinIcon.ASTERISK).size("20px").styleName("test")
				.color("green").add().build();
		assertNotNull(button.getIcon());
		assertTrue(button.getIcon() instanceof Icon);

		Icon icon = (Icon) button.getIcon();
		assertEquals("20px", icon.getStyle().get(ElementConstants.STYLE_WIDTH));
		assertEquals("20px", icon.getStyle().get(ElementConstants.STYLE_HEIGHT));
		assertEquals("green", icon.getStyle().get("fill"));
		assertEquals("test", icon.getClassName());

		button = ButtonBuilder.create().icon(VaadinIcon.ASTERISK).build();
		assertFalse(button.isIconAfterText());

		button = ButtonBuilder.create().icon(VaadinIcon.ASTERISK).iconAfterText(false).build();
		assertFalse(button.isIconAfterText());

		button = ButtonBuilder.create().icon(VaadinIcon.ASTERISK).iconAfterText(true).build();
		assertTrue(button.isIconAfterText());

	}

	@Test
	public void testText() {

		Button button = ButtonBuilder.create().text(Localizable.builder().message("test").build()).build();
		assertEquals("test", button.getText());

		button = ButtonBuilder.create().text("test").build();
		assertEquals("test", button.getText());

		button = ButtonBuilder.create().text("test", "test.code").build();
		assertEquals("test", button.getText());

		button = ButtonBuilder.create().text("test", "test.code", "arg").build();
		assertEquals("test", button.getText());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create()
					.text(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", button2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create().text("test", "test.code").build();
			assertEquals("TestUS", button2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create().deferLocalization().text("test", "test.code").build();
			assertEquals("test", button2.getText());
			ComponentUtil.onComponentAttach(button2, true);
			assertEquals("TestUS", button2.getText());
		});

	}

	@Test
	public void testDescription() {

		Button button = ButtonBuilder.create().title(Localizable.builder().message("test").build()).build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().title("test").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().title("test", "test.code").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().title("test", "test.code", "arg").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().description(Localizable.builder().message("test").build()).build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().description("test").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().description("test", "test.code").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		button = ButtonBuilder.create().description("test", "test.code", "arg").build();
		assertEquals("test", button.getElement().getAttribute("title"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create()
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create().title("test", "test.code").build();
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create().deferLocalization().title("test", "test.code").build();
			assertEquals("test", button2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(button2, true);
			assertEquals("TestUS", button2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Button button2 = ButtonBuilder.create().withDeferredLocalization(true).text("test", "test.code")
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

		Button button = ButtonBuilder.create().tabIndex(77).build();
		assertEquals(77, button.getTabIndex());

		button = ButtonBuilder.create().autofocus(false).build();
		assertFalse(button.isAutofocus());

		button = ButtonBuilder.create().autofocus(true).build();
		assertTrue(button.isAutofocus());

		button = ButtonBuilder.create().autofocus().build();
		assertTrue(button.isAutofocus());

	}

}
