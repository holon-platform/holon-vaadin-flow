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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
import com.holonplatform.vaadin.flow.navigator.internal.utils.LocationUtils;
import com.holonplatform.vaadin.flow.navigator.test.data.TestParameterType;

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

		sv = mapper.deserialize(String.class, (String) null);
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

		// long
		serialized = mapper.serialize(Long.valueOf(777));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("777", serialized.get(0));

		List<Long> lvs = mapper.deserialize(Long.class, serialized);
		assertNotNull(lvs);
		assertEquals(1, lvs.size());
		assertEquals(Long.valueOf(777), lvs.get(0));

		serialized = mapper.serialize(Arrays.asList(3L, 2L, 1L));
		assertNotNull(serialized);
		assertEquals(3, serialized.size());
		assertEquals("3", serialized.get(0));
		assertEquals("2", serialized.get(1));
		assertEquals("1", serialized.get(2));

		lvs = mapper.deserialize(Long.class, serialized);
		assertNotNull(lvs);
		assertEquals(3, lvs.size());
		assertEquals(Long.valueOf(3), lvs.get(0));
		assertEquals(Long.valueOf(2), lvs.get(1));
		assertEquals(Long.valueOf(1), lvs.get(2));

		// integer
		serialized = mapper.serialize(Integer.valueOf(7));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("7", serialized.get(0));

		List<Integer> ivs = mapper.deserialize(Integer.class, serialized);
		assertNotNull(ivs);
		assertEquals(1, ivs.size());
		assertEquals(Integer.valueOf(7), ivs.get(0));

		serialized = mapper.serialize(Arrays.asList(1, 2));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals("1", serialized.get(0));
		assertEquals("2", serialized.get(1));

		ivs = mapper.deserialize(Integer.class, serialized);
		assertNotNull(ivs);
		assertEquals(2, ivs.size());
		assertEquals(Integer.valueOf(1), ivs.get(0));
		assertEquals(Integer.valueOf(2), ivs.get(1));

		// short
		serialized = mapper.serialize(Short.valueOf("3"));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("3", serialized.get(0));

		List<Short> stvs = mapper.deserialize(Short.class, serialized);
		assertNotNull(stvs);
		assertEquals(1, stvs.size());
		assertEquals(Short.valueOf("3"), stvs.get(0));

		// byte
		serialized = mapper.serialize(Byte.valueOf("1"));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("1", serialized.get(0));

		List<Byte> btvs = mapper.deserialize(Byte.class, serialized);
		assertNotNull(btvs);
		assertEquals(1, btvs.size());
		assertEquals(Byte.valueOf("1"), btvs.get(0));

		// float
		serialized = mapper.serialize(23.5f);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("23.5", serialized.get(0));

		List<Float> fvs = mapper.deserialize(Float.class, serialized);
		assertNotNull(fvs);
		assertEquals(1, fvs.size());
		assertEquals(Float.valueOf(23.5f), fvs.get(0));

		// double
		serialized = mapper.serialize(21.543d);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("21.543", serialized.get(0));

		List<Double> dvs = mapper.deserialize(Double.class, serialized);
		assertNotNull(dvs);
		assertEquals(1, dvs.size());
		assertEquals(Double.valueOf(21.543d), dvs.get(0));

		// BigDecimal
		serialized = mapper.serialize(BigDecimal.valueOf(123.4567d));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("123.4567", serialized.get(0));

		List<BigDecimal> bdvs = mapper.deserialize(BigDecimal.class, serialized);
		assertNotNull(bdvs);
		assertEquals(1, bdvs.size());
		assertEquals(BigDecimal.valueOf(123.4567d), bdvs.get(0));

		// enum
		serialized = mapper.serialize(TestEnum.ONE);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals(TestEnum.ONE.name(), serialized.get(0));

		List<TestEnum> ebvs = mapper.deserialize(TestEnum.class, serialized);
		assertNotNull(ebvs);
		assertEquals(1, ebvs.size());
		assertEquals(TestEnum.ONE, ebvs.get(0));

		serialized = mapper.serialize(Arrays.asList(TestEnum.TWO, TestEnum.ONE));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals(TestEnum.TWO.name(), serialized.get(0));
		assertEquals(TestEnum.ONE.name(), serialized.get(1));

		ebvs = mapper.deserialize(TestEnum.class, serialized);
		assertNotNull(ebvs);
		assertEquals(2, ebvs.size());
		assertEquals(TestEnum.TWO, ebvs.get(0));
		assertEquals(TestEnum.ONE, ebvs.get(1));

		// LocalDate
		final LocalDate ldate = LocalDate.of(2018, Month.DECEMBER, 3);
		final LocalDate ldate2 = LocalDate.of(2019, Month.JANUARY, 12);

		serialized = mapper.serialize(ldate);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals(DateTimeFormatter.ISO_LOCAL_DATE.format(ldate), serialized.get(0));

		List<LocalDate> ldvs = mapper.deserialize(LocalDate.class, serialized);
		assertNotNull(ldvs);
		assertEquals(1, ldvs.size());
		assertEquals(ldate, ldvs.get(0));

		serialized = mapper.serialize(Arrays.asList(ldate, ldate2));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals(DateTimeFormatter.ISO_LOCAL_DATE.format(ldate), serialized.get(0));
		assertEquals(DateTimeFormatter.ISO_LOCAL_DATE.format(ldate2), serialized.get(1));

		ldvs = mapper.deserialize(LocalDate.class, serialized);
		assertNotNull(ldvs);
		assertEquals(2, ldvs.size());
		assertEquals(ldate, ldvs.get(0));
		assertEquals(ldate2, ldvs.get(1));

		// LocalTime
		final LocalTime ltime = LocalTime.of(14, 21, 15);
		final LocalTime ltime2 = LocalTime.of(18, 30);

		serialized = mapper.serialize(ltime);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(ltime), serialized.get(0));

		List<LocalTime> ltvs = mapper.deserialize(LocalTime.class, serialized);
		assertNotNull(ltvs);
		assertEquals(1, ltvs.size());
		assertEquals(ltime, ltvs.get(0));

		serialized = mapper.serialize(Arrays.asList(ltime, ltime2));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(ltime), serialized.get(0));
		assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(ltime2), serialized.get(1));

		ltvs = mapper.deserialize(LocalTime.class, serialized);
		assertNotNull(ltvs);
		assertEquals(2, ltvs.size());
		assertEquals(ltime, ltvs.get(0));
		assertEquals(ltime2, ltvs.get(1));

		// LocalDateTime
		final LocalDateTime ldtime = LocalDateTime.of(2018, Month.DECEMBER, 3, 14, 21, 15);
		final LocalDateTime ldtime2 = LocalDateTime.of(2019, Month.JANUARY, 12, 18, 30);

		serialized = mapper.serialize(ldtime);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldtime), serialized.get(0));

		List<LocalDateTime> ldtvs = mapper.deserialize(LocalDateTime.class, serialized);
		assertNotNull(ldtvs);
		assertEquals(1, ldtvs.size());
		assertEquals(ldtime, ldtvs.get(0));

		serialized = mapper.serialize(Arrays.asList(ldtime, ldtime2));
		assertNotNull(serialized);
		assertEquals(2, serialized.size());
		assertEquals(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldtime), serialized.get(0));
		assertEquals(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldtime2), serialized.get(1));

		ldtvs = mapper.deserialize(LocalDateTime.class, serialized);
		assertNotNull(ldtvs);
		assertEquals(2, ldtvs.size());
		assertEquals(ldtime, ldtvs.get(0));
		assertEquals(ldtime2, ldtvs.get(1));

		// Date
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 3);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 26);
		c.set(Calendar.SECOND, 30);
		c.set(Calendar.MILLISECOND, 0);

		final Date ud = c.getTime();

		serialized = mapper.serialize(ud);
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals(
				DateTimeFormatter.ISO_LOCAL_DATE_TIME
						.format(Instant.ofEpochMilli(ud.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime()),
				serialized.get(0));

		List<Date> udvs = mapper.deserialize(Date.class, serialized);
		assertNotNull(udvs);
		assertEquals(1, udvs.size());
		assertEquals(ud, udvs.get(0));

		// custom mapper
		serialized = mapper.serialize(new TestParameterType(7));
		assertNotNull(serialized);
		assertEquals(1, serialized.size());
		assertEquals("7", serialized.get(0));

		List<TestParameterType> tvs = mapper.deserialize(TestParameterType.class, serialized);
		assertNotNull(tvs);
		assertEquals(1, tvs.size());
		assertEquals(new TestParameterType(7), tvs.get(0));
	}

	private enum TestEnum {

		ONE, TWO

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
