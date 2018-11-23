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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.internal.utils.LocationUtils;

public class TestQueryParameters {

	@Test
	public void testQueryParametersFromLocation() {

		String query = null;
		Map<String, List<String>> parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		query = "";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		query = " ";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		query = "test";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		query = "test=";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		query = "test=val";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(1, parameters.size());
		assertTrue(parameters.containsKey("test"));
		List<String> values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("val", values.get(0));

		query = "test=val&";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(1, parameters.size());
		assertTrue(parameters.containsKey("test"));
		values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("val", values.get(0));

		query = "test=val&test2";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(1, parameters.size());
		assertTrue(parameters.containsKey("test"));
		values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("val", values.get(0));

		query = "test=val&test2=";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(1, parameters.size());
		assertTrue(parameters.containsKey("test"));
		values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("val", values.get(0));

		query = "test=val&test2=val2";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(2, parameters.size());
		assertTrue(parameters.containsKey("test"));
		values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("val", values.get(0));
		assertTrue(parameters.containsKey("test2"));
		values = parameters.get("test2");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("val2", values.get(0));

		query = "test=val1&test=val2";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(1, parameters.size());
		assertTrue(parameters.containsKey("test"));
		values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(2, values.size());
		assertTrue(values.contains("val1"));
		assertTrue(values.contains("val2"));

		query = "test=val1&test2=valx&test=val2";
		parameters = LocationUtils.getQueryParameters(query);
		assertNotNull(parameters);
		assertEquals(2, parameters.size());
		assertTrue(parameters.containsKey("test"));
		values = parameters.get("test");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(2, values.size());
		assertTrue(values.contains("val1"));
		assertTrue(values.contains("val2"));
		assertTrue(parameters.containsKey("test2"));
		values = parameters.get("test2");
		assertNotNull(values);
		assertFalse(values.isEmpty());
		assertEquals(1, values.size());
		assertEquals("valx", values.get(0));

	}

}
