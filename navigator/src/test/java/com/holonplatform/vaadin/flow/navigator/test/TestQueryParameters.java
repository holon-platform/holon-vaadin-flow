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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
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

	@Test
	public void testNavigationParameterMapper() {

		final NavigationParameterMapper mapper = NavigationParameterMapper.get();

		List<String> serialized = mapper.serialize(null);
		assertNotNull(serialized);
		assertTrue(serialized.isEmpty());

		// String
		serialized = mapper.serialize("a");
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("a", serialized.get(0));
		
		List<String> svs = mapper.deserialize(String.class, serialized);
		assertNotNull(svs);
		assertEquals(1, svs.size());
		assertEquals("a", svs.get(0));
		
		Optional<String> sv = mapper.deserialize(String.class, "x");
		assertTrue(sv.isPresent());
		assertEquals("x", sv.get());
		
		sv = mapper.deserialize(String.class, (String)null);
		assertFalse(sv.isPresent());

		serialized = mapper.serialize(Arrays.asList("a", "b"));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals("a", serialized.get(0));
		assertEquals("b", serialized.get(1));
		
		svs = mapper.deserialize(String.class, serialized);
		assertNotNull(svs);
		assertEquals(2, svs.size());
		assertEquals("a", svs.get(0));
		assertEquals("b", svs.get(1));

		serialized = mapper.serialize(Arrays.asList("c"));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("c", serialized.get(0));

		serialized = mapper.serialize(_set("a", "b"));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertTrue(serialized.contains("a"));
		assertTrue(serialized.contains("b"));
		
		// boolean
		serialized = mapper.serialize(Boolean.TRUE);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("true", serialized.get(0));
		
		serialized = mapper.serialize(Boolean.FALSE);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("false", serialized.get(0));
		
		Optional<Boolean> bv = mapper.deserialize(Boolean.class, "true");
		assertTrue(bv.isPresent());
		assertEquals(Boolean.TRUE, bv.get());
		
		serialized = mapper.serialize(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals("false", serialized.get(0));
		assertEquals("true", serialized.get(1));
		
		// integer
		serialized = mapper.serialize(Integer.valueOf(7));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("7", serialized.get(0));
		
		serialized = mapper.serialize(Arrays.asList(1, 2));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals("1", serialized.get(0));
		assertEquals("2", serialized.get(1));
		
		

	}

	@SuppressWarnings("unchecked")
	private static <T> Set<T> _set(T... values) {
		final Set<T> set = new HashSet<>();
		for (T value : values) {
			set.add(value);
		}
		return set;
	}

}
