/*
 * Copyright 2016-2019 Axioma srl.
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

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.NavigationParameters;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

public class TestNavigationParameters {

	@Test
	public void testEmptyNavigationParameters() {

		NavigationParameters np = NavigationParameters.create(Collections.emptyMap());
		assertNotNull(np);

		assertFalse(np.hasQueryParameter("x"));
		assertEquals(0, np.getQueryParameterValues("x", Integer.class).size());
		assertFalse(np.getQueryParameterValue("x", Integer.class).isPresent());

		np = NavigationParameters.create(QueryParameters.empty());
		assertNotNull(np);

		assertFalse(np.hasQueryParameter("x"));
		assertEquals(0, np.getQueryParameterValues("x", Integer.class).size());
		assertFalse(np.getQueryParameterValue("x", Integer.class).isPresent());

		np = NavigationParameters.create(new Location("host.com/test"));
		assertNotNull(np);

		assertFalse(np.hasQueryParameter("x"));
		assertEquals(0, np.getQueryParameterValues("x", Integer.class).size());
		assertFalse(np.getQueryParameterValue("x", Integer.class).isPresent());

		np = NavigationParameters.create(new Location("host.com/test?x="));
		assertNotNull(np);

		assertFalse(np.hasQueryParameter("x"));
		assertEquals(0, np.getQueryParameterValues("x", Integer.class).size());
		assertFalse(np.getQueryParameterValue("x", Integer.class).isPresent());

	}

	@Test
	public void testNavigationParameters() {

		final LocalDate ldate = LocalDate.of(2019, Month.FEBRUARY, 21);

		Map<String, List<String>> queryParameters = new HashMap<>();
		queryParameters.put("p1", Collections.singletonList("test"));
		queryParameters.put("p2", Collections.singletonList("7"));
		queryParameters.put("p3", Collections.singletonList(DateTimeFormatter.ISO_LOCAL_DATE.format(ldate)));
		queryParameters.put("p4", Arrays.asList("1.2", "2.1"));

		NavigationParameters np = NavigationParameters.create(queryParameters);
		assertNotNull(np);

		assertFalse(np.hasQueryParameter("x"));
		assertEquals(0, np.getQueryParameterValues("x", Integer.class).size());
		assertFalse(np.getQueryParameterValue("x", Integer.class).isPresent());

		assertTrue(np.hasQueryParameter("p1"));
		assertEquals(1, np.getQueryParameterValues("p1", String.class).size());
		assertEquals("test", np.getQueryParameterValues("p1", String.class).get(0));
		assertTrue(np.getQueryParameterValue("p1", String.class).isPresent());
		assertEquals("test", np.getQueryParameterValue("p1", String.class).get());
		assertEquals("test", np.getQueryParameterValue("p1", String.class, "dft"));

		assertTrue(np.hasQueryParameter("p2"));
		assertEquals(1, np.getQueryParameterValues("p2", Integer.class).size());
		assertEquals(Integer.valueOf(7), np.getQueryParameterValues("p2", Integer.class).get(0));
		assertTrue(np.getQueryParameterValue("p2", Integer.class).isPresent());
		assertEquals(Integer.valueOf(7), np.getQueryParameterValue("p2", Integer.class).get());
		assertEquals(Integer.valueOf(7), np.getQueryParameterValue("p2", Integer.class, 0));

		assertTrue(np.hasQueryParameter("p3"));
		assertEquals(1, np.getQueryParameterValues("p3", LocalDate.class).size());
		assertTrue(np.getQueryParameterValue("p3", LocalDate.class).isPresent());
		assertEquals(ldate, np.getQueryParameterValue("p3", LocalDate.class).get());

		assertTrue(np.hasQueryParameter("p4"));
		assertEquals(2, np.getQueryParameterValues("p4", Double.class).size());
		assertTrue(np.getQueryParameterValues("p4", Double.class).contains(Double.valueOf(1.2d)));
		assertTrue(np.getQueryParameterValues("p4", Double.class).contains(Double.valueOf(2.1d)));
		assertTrue(np.getQueryParameterValue("p4", Double.class).isPresent());
		assertEquals(Double.valueOf(1.2d), np.getQueryParameterValue("p4", Double.class).get());
	}

}
