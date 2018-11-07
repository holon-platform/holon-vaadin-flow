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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ContextMenuBuilder;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Span;

public class TestContextMenu {

	@Test
	public void testBuilder() {

		ContextMenuBuilder builder = ContextMenuBuilder.create();
		assertNotNull(builder);

		ContextMenu contextMenu = builder.build();
		assertNotNull(contextMenu);

		builder = Components.contextMenu();
		assertNotNull(builder);

	}

	@Test
	public void testComponent() {

		ContextMenu menu = ContextMenuBuilder.create().id("testid").build();
		assertTrue(menu.getId().isPresent());
		assertEquals("testid", menu.getId().get());

		menu = ContextMenuBuilder.create().build();
		assertTrue(menu.isVisible());

		menu = ContextMenuBuilder.create().visible(true).build();
		assertTrue(menu.isVisible());

		menu = ContextMenuBuilder.create().visible(false).build();
		assertFalse(menu.isVisible());

		menu = ContextMenuBuilder.create().hidden().build();
		assertFalse(menu.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		menu = ContextMenuBuilder.create().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(menu, true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		menu = ContextMenuBuilder.create().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(menu);
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		ContextMenu menu = ContextMenuBuilder.create().styleName("test").build();
		assertNotNull(menu);

		assertTrue(menu.getClassNames().contains("test"));

		menu = ContextMenuBuilder.create().styleName("test1").styleName("test2").build();
		assertTrue(menu.getClassNames().contains("test1"));
		assertTrue(menu.getClassNames().contains("test2"));

		menu = ContextMenuBuilder.create().styleNames("test").build();
		assertTrue(menu.getClassNames().contains("test"));

		menu = ContextMenuBuilder.create().styleNames("test1", "test2").build();
		assertTrue(menu.getClassNames().contains("test1"));
		assertTrue(menu.getClassNames().contains("test2"));

		menu = ContextMenuBuilder.create().styleNames("test1", "test2").removeStyleName("test2")
				.replaceStyleName("test3").build();
		assertFalse(menu.getClassNames().contains("test1"));
		assertFalse(menu.getClassNames().contains("test2"));
		assertTrue(menu.getClassNames().contains("test3"));

	}

	@Test
	public void testConfiguration() {

		ContextMenu menu = ContextMenuBuilder.create().openOnClick(true).build();
		assertTrue(menu.isOpenOnClick());

		menu = ContextMenuBuilder.create().openOnClick(false).build();
		assertFalse(menu.isOpenOnClick());

	}

	@Test
	public void testMenuItems() {

		ContextMenu menu = ContextMenuBuilder.create().withItem("test1").add().withItem("test2").add().build();
		assertEquals(2L, menu.getItems().stream().count());

		menu = ContextMenuBuilder.create().withItem("test1").disabled().add().build();
		assertFalse(menu.getItems().stream().findFirst().map(i -> i.isEnabled()).orElse(true));

		menu = ContextMenuBuilder.create().withItem("test1").text("test2").add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test2".equals(i.getText())).findFirst().orElse(null));

	}

	@Test
	public void testTextMenuItems() {

		ContextMenu menu = ContextMenuBuilder.create().withItem("test").add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

		menu = ContextMenuBuilder.create().withItem(Localizable.builder().message("test").build()).add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

		menu = ContextMenuBuilder.create().withItem("test", "test.code").add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ContextMenu menu2 = ContextMenuBuilder.create()
					.withItem(Localizable.builder().message("test").messageCode("test.code").build()).add().build();
			assertNotNull(menu2.getItems().stream().filter(i -> "TestUS".equals(i.getText())).findFirst().orElse(null));
		});

		menu = ContextMenuBuilder.create().withItem("test", e -> {
		}).build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

	}

	@Test
	public void testComponentMenuItems() {

		final Span cmp = new Span("test");

		ContextMenu menu = ContextMenuBuilder.create().withItem(cmp).add().build();
		assertNotNull(menu.getItems().stream().filter(i -> cmp.equals(i.getChildren().findFirst().orElse(null)))
				.findFirst().orElse(null));

		menu = ContextMenuBuilder.create().withItem(cmp, e -> {
		}).build();
		assertNotNull(menu.getItems().stream().filter(i -> cmp.equals(i.getChildren().findFirst().orElse(null)))
				.findFirst().orElse(null));

	}

}
