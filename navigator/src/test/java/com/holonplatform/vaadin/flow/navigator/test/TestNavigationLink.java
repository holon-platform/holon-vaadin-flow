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
package com.holonplatform.vaadin.flow.navigator.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.navigator.NavigationLink;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget1;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget2;
import com.holonplatform.vaadin.flow.navigator.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;

public class TestNavigationLink extends AbstractNavigatorTest {

	@Test
	public void testBuilder() {

		NavigationLink.Builder builder = NavigationLink.builder("test");
		assertNotNull(builder);

		NavigationLink link = builder.build();
		assertNotNull(link);
		assertNotNull(link.getComponent());
		assertEquals("test", link.getHref());

	}

	@Test
	public void testNavigationURL() {

		String url = NavigationLink.builder("test").withPathParameter("a").build().getHref();
		assertNotNull(url);
		assertEquals("test/a", url);

		url = NavigationLink.builder("test").withPathParameter("a").withPathParameter("b").build().getHref();
		assertNotNull(url);
		assertEquals("test/a/b", url);

		url = NavigationLink.builder("test").withPathParameter("a").withQueryParameter("p1", "test1").build().getHref();
		assertNotNull(url);
		assertEquals("test/a?p1=test1", url);

		url = NavigationLink.builder("test").withQueryParameter("p1", "test1").withQueryParameter("p2", "test2").build()
				.getHref();
		assertNotNull(url);
		assertEquals("test?p1=test1&p2=test2", url);

	}

	@Test
	public void testURLResolution() {

		NavigationLink link = NavigationLink.builder(NavigationTarget1.class, router).build();
		assertNotNull(link);
		assertEquals("1", link.getHref());

		link = NavigationLink.builder(NavigationTarget2.class).build();
		assertNotNull(link);
		assertEquals("2", link.getHref());

	}

	@Test
	public void testComponent() {

		NavigationLink link = NavigationLink.builder("test").id("testid").build();
		assertTrue(link.getComponent().getId().isPresent());
		assertEquals("testid", link.getComponent().getId().get());

		link = NavigationLink.builder("test").build();
		assertTrue(link.isVisible());

		link = NavigationLink.builder("test").visible(true).build();
		assertTrue(link.isVisible());

		link = NavigationLink.builder("test").visible(false).build();
		assertFalse(link.isVisible());

		link = NavigationLink.builder("test").hidden().build();
		assertFalse(link.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		link = NavigationLink.builder("test").withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(link.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		link = NavigationLink.builder("test").withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(link.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		NavigationLink link = NavigationLink.builder("test").styleName("test").build();
		assertNotNull(link);

		assertTrue(link.getClassNames().contains("test"));

		link = NavigationLink.builder("test").styleName("test1").styleName("test2").build();
		assertTrue(link.getClassNames().contains("test1"));
		assertTrue(link.getClassNames().contains("test2"));

		link = NavigationLink.builder("test").styleNames("test").build();
		assertTrue(link.getClassNames().contains("test"));

		link = NavigationLink.builder("test").styleNames("test1", "test2").build();
		assertTrue(link.getClassNames().contains("test1"));
		assertTrue(link.getClassNames().contains("test2"));

	}

	@Test
	public void testText() {

		NavigationLink link = NavigationLink.builder("test").text(Localizable.builder().message("test").build())
				.build();
		assertEquals("test", link.getText());

		link = NavigationLink.builder("test").text("test").build();
		assertEquals("test", link.getText());

		link = NavigationLink.builder("test").text("test", "test.code").build();
		assertEquals("test", link.getText());

		link = NavigationLink.builder("test").text("test", "test.code", "arg").build();
		assertEquals("test", link.getText());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NavigationLink link2 = NavigationLink.builder("test")
					.text(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", link2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NavigationLink link2 = NavigationLink.builder("test").text("test", "test.code").build();
			assertEquals("TestUS", link2.getText());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			NavigationLink link2 = NavigationLink.builder("test").deferLocalization().text("test", "test.code").build();
			assertEquals("test", link2.getText());
			ComponentUtil.onComponentAttach(link2.getComponent(), true);
			assertEquals("TestUS", link2.getText());
		});

	}

}
