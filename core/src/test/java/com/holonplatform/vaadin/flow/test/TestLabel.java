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
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator.BaseLabelConfigurator;
import com.holonplatform.vaadin.flow.components.support.ContentMode;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;

public class TestLabel {

	@Test
	public void testConfigurator() {

		final Div div = new Div();

		BaseLabelConfigurator<Div> cfg = LabelConfigurator.configure(div);
		assertNotNull(cfg);

		cfg.id("testid");
		assertTrue(div.getId().isPresent());
		assertEquals("testid", div.getId().get());

		cfg = Components.configure(div);
		assertNotNull(cfg);

		cfg.id("testid");
		assertTrue(div.getId().isPresent());
		assertEquals("testid", div.getId().get());
	}

	@Test
	public void testBuilder() {

		LabelBuilder<?> builder = LabelBuilder.span();
		assertNotNull(builder);
		HtmlComponent label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof Span);

		builder = LabelBuilder.div();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof Div);

		builder = LabelBuilder.paragraph();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof Paragraph);

		builder = LabelBuilder.h1();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof H1);

		builder = LabelBuilder.h2();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof H2);

		builder = LabelBuilder.h3();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof H3);

		builder = LabelBuilder.h4();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof H4);

		builder = LabelBuilder.h5();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof H5);

		builder = LabelBuilder.h6();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);
		assertTrue(label instanceof H6);

		builder = Components.label();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.div();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.paragraph();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.h1();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.h2();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.h3();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.h4();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.h5();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

		builder = Components.h6();
		assertNotNull(builder);
		label = builder.build();
		assertNotNull(label);

	}

	@Test
	public void testComponent() {

		Div label = LabelBuilder.div().id("testid").build();
		assertTrue(label.getId().isPresent());
		assertEquals("testid", label.getId().get());

		label = LabelBuilder.div().build();
		assertTrue(label.isVisible());

		label = LabelBuilder.div().visible(true).build();
		assertTrue(label.isVisible());

		label = LabelBuilder.div().visible(false).build();
		assertFalse(label.isVisible());

		label = LabelBuilder.div().hidden().build();
		assertFalse(label.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		label = LabelBuilder.div().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(label, true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		label = LabelBuilder.div().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(label);
		assertTrue(detached.get());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testStyles() {

		Div label = LabelBuilder.div().styleName("test").build();
		assertNotNull(label);

		assertTrue(label.getClassNames().contains("test"));

		label = LabelBuilder.div().styleName("test1").styleName("test2").build();
		assertTrue(label.getClassNames().contains("test1"));
		assertTrue(label.getClassNames().contains("test2"));

		label = LabelBuilder.div().styleNames("test").build();
		assertTrue(label.getClassNames().contains("test"));

		label = LabelBuilder.div().styleNames("test1", "test2").build();
		assertTrue(label.getClassNames().contains("test1"));
		assertTrue(label.getClassNames().contains("test2"));

		label = LabelBuilder.div().styleNames("test1", "test2").removeStyleName("test2").replaceStyleName("test3")
				.build();
		assertFalse(label.getClassNames().contains("test1"));
		assertFalse(label.getClassNames().contains("test2"));
		assertTrue(label.getClassNames().contains("test3"));

		label = LabelBuilder.div().primaryStyleName("test").build();
		assertTrue(label.getClassNames().contains("test"));

	}

	@Test
	public void testSize() {

		Div label = LabelBuilder.div().build();
		assertNull(label.getWidth());
		assertNull(label.getHeight());

		label = LabelBuilder.div().width("50em").build();
		assertEquals("50em", label.getWidth());

		label = LabelBuilder.div().width(50, Unit.EM).build();
		assertEquals("50em", label.getWidth());

		label = LabelBuilder.div().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", label.getWidth());

		label = LabelBuilder.div().height("50em").build();
		assertEquals("50em", label.getHeight());

		label = LabelBuilder.div().height(50, Unit.EM).build();
		assertEquals("50em", label.getHeight());

		label = LabelBuilder.div().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", label.getHeight());

		label = LabelBuilder.div().width("50%").height("100%").build();
		assertEquals("50%", label.getWidth());
		assertEquals("100%", label.getHeight());

		label = LabelBuilder.div().width("100px").widthUndefined().build();
		assertNull(label.getWidth());

		label = LabelBuilder.div().height("100px").heightUndefined().build();
		assertNull(label.getHeight());

		label = LabelBuilder.div().width("100px").height("100px").sizeUndefined().build();
		assertNull(label.getWidth());
		assertNull(label.getHeight());

		label = LabelBuilder.div().fullWidth().build();
		assertEquals("100%", label.getWidth());
		assertNull(label.getHeight());

		label = LabelBuilder.div().fullHeight().build();
		assertEquals("100%", label.getHeight());
		assertNull(label.getWidth());

		label = LabelBuilder.div().fullWidth().fullHeight().build();
		assertEquals("100%", label.getWidth());
		assertEquals("100%", label.getHeight());

		label = LabelBuilder.div().fullSize().build();
		assertEquals("100%", label.getWidth());
		assertEquals("100%", label.getHeight());

	}

	@Test
	public void testEnabled() {

		Div label = LabelBuilder.div().build();
		assertTrue(label.isEnabled());

		label = LabelBuilder.div().enabled(true).build();
		assertTrue(label.isEnabled());

		label = LabelBuilder.div().enabled(false).build();
		assertFalse(label.isEnabled());

		label = LabelBuilder.div().disabled().build();
		assertFalse(label.isEnabled());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testText() {

		Div label = LabelBuilder.div().text(Localizable.builder().message("test").build()).build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().text("test").build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().text("test", "test.code").build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().text("test", "test.code", "arg").build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().content(Localizable.builder().message("test").build()).build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().content("test").build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().content("test", "test.code").build();
		assertEquals("test", label.getText());

		label = LabelBuilder.div().content("test", "test.code", "arg").build();
		assertEquals("test", label.getText());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().text(Localizable.builder().message("test").messageCode("test.code").build())
					.build();
			assertEquals("TestUS", label2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().text("test", "test.code").build();
			assertEquals("TestUS", label2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().deferLocalization().text("test", "test.code").build();
			assertEquals("test", label2.getText());
			ComponentUtil.onComponentAttach(label2, true);
			assertEquals("TestUS", label2.getText());
		});

	}

	@Test
	public void testDescription() {

		Div label = LabelBuilder.div().title(Localizable.builder().message("test").build()).build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().title("test").build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().title("test", "test.code").build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().title("test", "test.code", "arg").build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().description(Localizable.builder().message("test").build()).build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().description("test").build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().description("test", "test.code").build();
		assertEquals("test", label.getElement().getAttribute("title"));

		label = LabelBuilder.div().description("test", "test.code", "arg").build();
		assertEquals("test", label.getElement().getAttribute("title"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div()
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", label2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().title("test", "test.code").build();
			assertEquals("TestUS", label2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().deferLocalization().title("test", "test.code").build();
			assertEquals("test", label2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(label2, true);
			assertEquals("TestUS", label2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().withDeferredLocalization(true).text("test", "test.code")
					.title("test", "test.code").build();
			assertEquals("test", label2.getText());
			assertEquals("test", label2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(label2, true);
			assertEquals("TestUS", label2.getText());
			assertEquals("TestUS", label2.getElement().getAttribute("title"));
		});

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testHtmlText() {

		Div label = LabelBuilder.div().htmlText(Localizable.builder().message("test").build()).build();
		assertEquals("test", label.getElement().getProperty("innerHTML"));

		label = LabelBuilder.div().htmlText("test").build();
		assertEquals("test", label.getElement().getProperty("innerHTML"));

		label = LabelBuilder.div().htmlText("test", "test.code").build();
		assertEquals("test", label.getElement().getProperty("innerHTML"));

		label = LabelBuilder.div().htmlText("test", "test.code", "arg").build();
		assertEquals("test", label.getElement().getProperty("innerHTML"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div()
					.htmlText(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", label2.getElement().getProperty("innerHTML"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().htmlText("test", "test.code").build();
			assertEquals("TestUS", label2.getElement().getProperty("innerHTML"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Div label2 = LabelBuilder.div().deferLocalization().htmlText("test", "test.code").build();
			assertEquals("test", label2.getElement().getProperty("innerHTML"));
			ComponentUtil.onComponentAttach(label2, true);
			assertEquals("TestUS", label2.getElement().getProperty("innerHTML"));
		});

		label = LabelBuilder.div().text("test").contentMode(ContentMode.HTML).build();
		assertEquals("test", label.getElement().getProperty("innerHTML"));

		label = LabelBuilder.div().text("test").html().build();
		assertEquals("test", label.getElement().getProperty("innerHTML"));

	}

}
