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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.Navigator.NavigationBuilder;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget1;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget2;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget3;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget9;
import com.vaadin.flow.dom.Element;
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

	@Test
	public void testNavigation() {

		Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);

		navigator.navigateTo(NavigationTarget1.class);

		Element element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getClassList().contains("nav1"));

		navigator.navigateTo("1");
		element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getClassList().contains("nav1"));

		navigator.navigateTo(NavigationTarget9.class, "test");
		element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getClassList().contains("nav9"));
		assertTrue(element.getComponent().isPresent());
		assertEquals(NavigationTarget9.class, element.getComponent().get().getClass());

		Map<String, Object> params = new HashMap<>();
		params.put("param1", "test1");
		params.put("param3", 3d);

		navigator.navigateTo("2", params);
		element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getClassList().contains("nav2"));
		assertTrue(element.getComponent().isPresent());
		assertEquals(NavigationTarget2.class, element.getComponent().get().getClass());

		NavigationTarget2 nt2 = (NavigationTarget2) element.getComponent().get();
		assertEquals("test1", nt2.getParam1Value());
		assertNull(nt2.getParam2Value());
		assertEquals(Double.valueOf(3d), nt2.getParam3());
		assertEquals("dft", nt2.getParam4Value());
		assertNotNull(nt2.getParam5Value());
		assertTrue(nt2.getParam5Value().isEmpty());
		assertNotNull(nt2.getParam6Value());
		assertTrue(nt2.getParam6Value().isEmpty());

		final LocalDate ld = LocalDate.of(2018, Month.DECEMBER, 4);

		params = new HashMap<>();
		params.put("param1", "test1");
		params.put("param2", Integer.valueOf(7));
		params.put("param3", 5d);
		params.put("param4", "test4");
		params.put("param5", Arrays.asList("s1", "s2"));
		params.put("param6", ld);

		navigator.navigateTo(NavigationTarget2.class, params);
		element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getClassList().contains("nav2"));
		assertTrue(element.getComponent().isPresent());
		assertEquals(NavigationTarget2.class, element.getComponent().get().getClass());

		nt2 = (NavigationTarget2) element.getComponent().get();
		assertEquals("test1", nt2.getParam1Value());
		assertEquals(Integer.valueOf(7), nt2.getParam2Value());
		assertEquals(Double.valueOf(5d), nt2.getParam3());
		assertEquals("test4", nt2.getParam4Value());
		assertNotNull(nt2.getParam5Value());
		assertFalse(nt2.getParam5Value().isEmpty());
		assertEquals(2, nt2.getParam5Value().size());
		assertTrue(nt2.getParam5Value().contains("s2"));
		assertTrue(nt2.getParam5Value().contains("s1"));
		assertNotNull(nt2.getParam6Value());
		assertFalse(nt2.getParam6Value().isEmpty());
		assertEquals(1, nt2.getParam6Value().size());
		assertEquals(ld, nt2.getParam6Value().iterator().next());

		navigator.navigation("3").navigate();
		element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getComponent().isPresent());
		assertEquals(NavigationTarget3.class, element.getComponent().get().getClass());

		NavigationTarget3 nt3 = (NavigationTarget3) element.getComponent().get();
		assertNull(nt3.getShowed());

		navigator.navigation(NavigationTarget3.class).withQueryParameter("param1", "testx").navigate();
		element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getComponent().isPresent());
		assertEquals(NavigationTarget3.class, element.getComponent().get().getClass());

		nt3 = (NavigationTarget3) element.getComponent().get();
		assertNotNull(nt3.getShowed());
		assertEquals("testx", nt3.getShowed());

	}

}
