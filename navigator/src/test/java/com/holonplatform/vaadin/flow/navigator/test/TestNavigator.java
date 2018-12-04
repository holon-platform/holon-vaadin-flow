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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.Navigator.NavigationBuilder;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget1;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget9;
import com.vaadin.flow.router.Location;

public class TestNavigator extends AbstractNavigatorTest {

	@Test
	public void testNavigationBuilder() {

		Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);

		NavigationBuilder builder = navigator.navigation("test");
		assertNotNull(builder);

		String url = navigator.navigation("").asLocationURL();
		assertNotNull(url);
		assertEquals("", url);

		Location l = builder.asLocation();
		assertNotNull(l);
		assertEquals("test", l.getPath());

		url = builder.asLocationURL();
		assertNotNull(url);
		assertEquals("test", url);

		l = navigator.navigation("test").withQueryParameter("p1", "test1").asLocation();
		assertNotNull(l);
		assertEquals("test", l.getPath());
		assertNotNull(l.getQueryParameters());
		assertEquals(1, l.getQueryParameters().getParameters().size());
		assertNotNull(l.getQueryParameters().getParameters().get("p1"));
		assertEquals("test1", l.getQueryParameters().getParameters().get("p1").get(0));

		l = navigator.navigation("test").withQueryParameter("p1", "test1").withQueryParameter("p2", "test2")
				.withQueryParameter("p1", "testx").asLocation();
		assertNotNull(l);
		assertEquals("test", l.getPath());
		assertNotNull(l.getQueryParameters());
		assertEquals(2, l.getQueryParameters().getParameters().size());
		assertNotNull(l.getQueryParameters().getParameters().get("p1"));
		assertEquals(2, l.getQueryParameters().getParameters().get("p1").size());
		assertTrue(l.getQueryParameters().getParameters().get("p1").contains("test1"));
		assertTrue(l.getQueryParameters().getParameters().get("p1").contains("testx"));
		assertNotNull(l.getQueryParameters().getParameters().get("p2"));
		assertEquals("test2", l.getQueryParameters().getParameters().get("p2").get(0));

		l = navigator.navigation("test").withQueryParameter("p1", "test1", "testx").asLocation();
		assertNotNull(l);
		assertEquals("test", l.getPath());
		assertNotNull(l.getQueryParameters());
		assertEquals(1, l.getQueryParameters().getParameters().size());
		assertNotNull(l.getQueryParameters().getParameters().get("p1"));
		assertEquals(2, l.getQueryParameters().getParameters().get("p1").size());
		assertTrue(l.getQueryParameters().getParameters().get("p1").contains("test1"));
		assertTrue(l.getQueryParameters().getParameters().get("p1").contains("testx"));

	}

	@Test
	public void testNavigationBuilderPathParameters() {

		Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);

		String url = navigator.navigation("test").withPathParameter("a").asLocationURL();
		assertNotNull(url);
		assertEquals("test/a", url);

		url = navigator.navigation("test").withPathParameter("a").withPathParameter("b").asLocationURL();
		assertNotNull(url);
		assertEquals("test/a/b", url);

		url = navigator.navigation("test").withPathParameter("a").withQueryParameter("p1", "test1").asLocationURL();
		assertNotNull(url);
		assertEquals("test/a?p1=test1", url);

	}
	
	@Test
	public void testGetURL() {

		Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);
		
		String url = navigator.getUrl(NavigationTarget1.class);
		assertNotNull(url);
		assertEquals("1", url);
		
		url = navigator.getUrl(NavigationTarget9.class, "test");
		assertNotNull(url);
		assertEquals("9/test", url);
		
	}
		

}
