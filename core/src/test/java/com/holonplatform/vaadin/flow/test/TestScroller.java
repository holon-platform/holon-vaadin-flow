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

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ScrollerBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TestScroller {

	@Test
	public void testBuilder() {

		ScrollerBuilder builder = ScrollerBuilder.create();
		assertNotNull(builder);

		Scroller scroller = builder.build();
		assertNotNull(scroller);

		builder = Components.scroller();
		assertNotNull(builder);

		builder = Components.scroller(new VerticalLayout());
		assertNotNull(builder);

		builder = Components.scroller(new VerticalLayout(), ScrollDirection.VERTICAL);
		assertNotNull(builder);

	}

	@Test
	public void testContent() {

		final VerticalLayout vl = new VerticalLayout();

		Scroller scroller = ScrollerBuilder.create().content(vl).build();
		Component c = scroller.getContent();
		assertNotNull(c);
		assertEquals(vl, c);

		scroller = Components.scroller(vl).build();
		c = scroller.getContent();
		assertNotNull(c);
		assertEquals(vl, c);

		scroller = Components.scroller(vl, ScrollDirection.BOTH).build();
		c = scroller.getContent();
		assertNotNull(c);
		assertEquals(vl, c);

	}

	@Test
	public void testDirection() {

		final VerticalLayout vl = new VerticalLayout();

		Scroller scroller = ScrollerBuilder.create().content(vl).scrollDirection(ScrollDirection.VERTICAL).build();
		assertNotNull(scroller);
		assertEquals(ScrollDirection.VERTICAL, scroller.getScrollDirection());

		scroller = ScrollerBuilder.create().content(vl).scrollDirection(ScrollDirection.HORIZONTAL).build();
		assertNotNull(scroller);
		assertEquals(ScrollDirection.HORIZONTAL, scroller.getScrollDirection());

		scroller = ScrollerBuilder.create().content(vl).scrollDirection(ScrollDirection.BOTH).build();
		assertNotNull(scroller);
		assertEquals(ScrollDirection.BOTH, scroller.getScrollDirection());

		scroller = ScrollerBuilder.create().content(vl).scrollDirection(ScrollDirection.NONE).build();
		assertNotNull(scroller);
		assertEquals(ScrollDirection.NONE, scroller.getScrollDirection());
	}

	@Test
	public void testComponent() {

		Scroller scroller = ScrollerBuilder.create().id("testid").build();
		assertTrue(scroller.getId().isPresent());
		assertEquals("testid", scroller.getId().get());

		scroller = ScrollerBuilder.create().build();
		assertTrue(scroller.isVisible());

		scroller = ScrollerBuilder.create().visible(true).build();
		assertTrue(scroller.isVisible());

		scroller = ScrollerBuilder.create().visible(false).build();
		assertFalse(scroller.isVisible());

		scroller = ScrollerBuilder.create().hidden().build();
		assertFalse(scroller.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		scroller = ScrollerBuilder.create().withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(scroller, true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		scroller = ScrollerBuilder.create().withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(scroller);
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		Scroller scroller = ScrollerBuilder.create().styleName("test").build();
		assertNotNull(scroller);

		assertTrue(scroller.getClassNames().contains("test"));

		scroller = ScrollerBuilder.create().styleName("test1").styleName("test2").build();
		assertTrue(scroller.getClassNames().contains("test1"));
		assertTrue(scroller.getClassNames().contains("test2"));

		scroller = ScrollerBuilder.create().styleNames("test").build();
		assertTrue(scroller.getClassNames().contains("test"));

		scroller = ScrollerBuilder.create().styleNames("test1", "test2").build();
		assertTrue(scroller.getClassNames().contains("test1"));
		assertTrue(scroller.getClassNames().contains("test2"));

	}

	@Test
	public void testSize() {

		Scroller scroller = ScrollerBuilder.create().build();
		assertNull(scroller.getWidth());
		assertNull(scroller.getHeight());

		scroller = ScrollerBuilder.create().width("50em").build();
		assertEquals("50em", scroller.getWidth());

		scroller = ScrollerBuilder.create().width(50, Unit.EM).build();
		assertEquals("50em", scroller.getWidth());

		scroller = ScrollerBuilder.create().width(50.7f, Unit.EM).build();
		assertEquals("50.7em", scroller.getWidth());

		scroller = ScrollerBuilder.create().height("50em").build();
		assertEquals("50em", scroller.getHeight());

		scroller = ScrollerBuilder.create().height(50, Unit.EM).build();
		assertEquals("50em", scroller.getHeight());

		scroller = ScrollerBuilder.create().height(50.7f, Unit.EM).build();
		assertEquals("50.7em", scroller.getHeight());

		scroller = ScrollerBuilder.create().width("50%").height("100%").build();
		assertEquals("50%", scroller.getWidth());
		assertEquals("100%", scroller.getHeight());

		scroller = ScrollerBuilder.create().width("100px").widthUndefined().build();
		assertNull(scroller.getWidth());

		scroller = ScrollerBuilder.create().height("100px").heightUndefined().build();
		assertNull(scroller.getHeight());

		scroller = ScrollerBuilder.create().width("100px").height("100px").sizeUndefined().build();
		assertNull(scroller.getWidth());
		assertNull(scroller.getHeight());

		scroller = ScrollerBuilder.create().fullWidth().build();
		assertEquals("100%", scroller.getWidth());
		assertNull(scroller.getHeight());

		scroller = ScrollerBuilder.create().fullHeight().build();
		assertEquals("100%", scroller.getHeight());
		assertNull(scroller.getWidth());

		scroller = ScrollerBuilder.create().fullWidth().fullHeight().build();
		assertEquals("100%", scroller.getWidth());
		assertEquals("100%", scroller.getHeight());

		scroller = ScrollerBuilder.create().fullSize().build();
		assertEquals("100%", scroller.getWidth());
		assertEquals("100%", scroller.getHeight());

	}

}
