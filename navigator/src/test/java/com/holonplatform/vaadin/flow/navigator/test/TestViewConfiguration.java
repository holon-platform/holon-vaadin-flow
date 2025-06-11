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

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration.ParameterContainerType;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfiguration.QueryParameterDefinition;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget1;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget2;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget3;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget4;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget5;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget6;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget7;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget8;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

public class TestViewConfiguration {

	private int count;
	private MockUI ui;

	@BeforeEach
	public void init() {
		ui = new MockUI();
		UI.setCurrent(ui);
	}

	@Test
	public void testDefault() {

		final NavigationTargetConfiguration cfg = NavigationTargetConfiguration.create(NavigationTarget1.class,
				ui.getSession().getService().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget1.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("1", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertTrue(cfg.getOnShowMethods().isEmpty());
		assertTrue(cfg.getQueryParameters().isEmpty());

	}

	@Test
	public void testQueryParameters() {

		final NavigationTargetConfiguration cfg = NavigationTargetConfiguration.create(NavigationTarget2.class,
				VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget2.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("2", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertTrue(cfg.getOnShowMethods().isEmpty());

		assertFalse(cfg.getQueryParameters().isEmpty());
		assertEquals(6, cfg.getQueryParameters().size());
		assertTrue(cfg.getQueryParameters().containsKey("param1"));
		assertTrue(cfg.getQueryParameters().containsKey("param2"));
		assertTrue(cfg.getQueryParameters().containsKey("param3"));
		assertTrue(cfg.getQueryParameters().containsKey("param4"));
		assertTrue(cfg.getQueryParameters().containsKey("param5"));
		assertTrue(cfg.getQueryParameters().containsKey("param6"));

		QueryParameterDefinition d = cfg.getQueryParameters().get("param1");
		assertNotNull(d);
		assertEquals("param1", d.getName());
		assertEquals(String.class, d.getType());
		assertEquals(ParameterContainerType.NONE, d.getParameterContainerType());
		assertFalse(d.isRequired());
		assertFalse(d.getDefaultValue().isPresent());
		assertNotNull(d.getField());
		assertFalse(d.getWriteMethod().isPresent());

		d = cfg.getQueryParameters().get("param2");
		assertNotNull(d);
		assertEquals("param2", d.getName());
		assertEquals(Integer.class, d.getType());
		assertEquals(ParameterContainerType.NONE, d.getParameterContainerType());
		assertFalse(d.isRequired());
		assertFalse(d.getDefaultValue().isPresent());
		assertNotNull(d.getField());
		assertFalse(d.getWriteMethod().isPresent());

		d = cfg.getQueryParameters().get("param3");
		assertNotNull(d);
		assertEquals("param3", d.getName());
		assertEquals(Double.class, d.getType());
		assertEquals(ParameterContainerType.NONE, d.getParameterContainerType());
		assertTrue(d.isRequired());
		assertFalse(d.getDefaultValue().isPresent());
		assertNotNull(d.getField());
		assertTrue(d.getWriteMethod().isPresent());
		assertEquals("setParam3", d.getWriteMethod().get().getName());

		d = cfg.getQueryParameters().get("param4");
		assertNotNull(d);
		assertEquals("param4", d.getName());
		assertEquals(String.class, d.getType());
		assertEquals(ParameterContainerType.NONE, d.getParameterContainerType());
		assertFalse(d.isRequired());
		assertTrue(d.getDefaultValue().isPresent());
		assertEquals("dft", d.getDefaultValue().get());
		assertNotNull(d.getField());
		assertFalse(d.getWriteMethod().isPresent());

		d = cfg.getQueryParameters().get("param5");
		assertNotNull(d);
		assertEquals("param5", d.getName());
		assertEquals(String.class, d.getType());
		assertEquals(ParameterContainerType.LIST, d.getParameterContainerType());
		assertFalse(d.isRequired());
		assertFalse(d.getDefaultValue().isPresent());
		assertNotNull(d.getField());
		assertFalse(d.getWriteMethod().isPresent());

		d = cfg.getQueryParameters().get("param6");
		assertNotNull(d);
		assertEquals("param6", d.getName());
		assertEquals(LocalDate.class, d.getType());
		assertEquals(ParameterContainerType.SET, d.getParameterContainerType());
		assertFalse(d.isRequired());
		assertFalse(d.getDefaultValue().isPresent());
		assertNotNull(d.getField());
		assertFalse(d.getWriteMethod().isPresent());

	}

	@Test
	public void testOnShow() {

		NavigationTargetConfiguration cfg = NavigationTargetConfiguration.create(NavigationTarget3.class,
				VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget3.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("3", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertFalse(cfg.getQueryParameters().isEmpty());
		assertEquals(1, cfg.getQueryParameters().size());
		assertTrue(cfg.getQueryParameters().containsKey("param1"));

		assertFalse(cfg.getOnShowMethods().isEmpty());
		assertEquals(1, cfg.getOnShowMethods().size());

		Method m = cfg.getOnShowMethods().get(0);
		assertEquals("onShow1", m.getName());
		assertEquals(0, m.getParameterTypes().length);

		cfg = NavigationTargetConfiguration.create(NavigationTarget4.class, VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget4.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("4", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertTrue(cfg.getQueryParameters().isEmpty());

		assertFalse(cfg.getOnShowMethods().isEmpty());
		assertEquals(1, cfg.getOnShowMethods().size());

		m = cfg.getOnShowMethods().get(0);
		assertEquals("onShow2", m.getName());
		assertEquals(1, m.getParameterTypes().length);

		cfg = NavigationTargetConfiguration.create(NavigationTarget5.class, VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget5.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("5", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertTrue(cfg.getQueryParameters().isEmpty());

		assertFalse(cfg.getOnShowMethods().isEmpty());
		assertEquals(2, cfg.getOnShowMethods().size());

		List<String> names = cfg.getOnShowMethods().stream().map(mt -> mt.getName()).collect(Collectors.toList());
		assertTrue(names.contains("onShow3"));
		assertTrue(names.contains("parentShow"));

	}

	@Test
	public void testAuth() {

		NavigationTargetConfiguration cfg = NavigationTargetConfiguration.create(NavigationTarget6.class,
				VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget6.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("6", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertTrue(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertTrue(cfg.getQueryParameters().isEmpty());
		assertTrue(cfg.getOnShowMethods().isEmpty());

		cfg = NavigationTargetConfiguration.create(NavigationTarget7.class, VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget7.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("7", cfg.getRoutePath().get());
		assertFalse(cfg.getCaption().isPresent());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getQueryParameters().isEmpty());
		assertTrue(cfg.getOnShowMethods().isEmpty());
		assertFalse(cfg.getAuthorization().isEmpty());
		assertEquals(2, cfg.getAuthorization().size());
		assertTrue(cfg.getAuthorization().contains("A"));
		assertTrue(cfg.getAuthorization().contains("B"));

	}

	@Test
	public void testCaption() {

		NavigationTargetConfiguration cfg = NavigationTargetConfiguration.create(NavigationTarget8.class,
				VaadinService.getCurrent().getContext());
		assertNotNull(cfg);
		assertEquals(NavigationTarget8.class, cfg.getNavigationTarget());
		assertTrue(cfg.getRoutePath().isPresent());
		assertEquals("8", cfg.getRoutePath().get());
		assertFalse(cfg.getAuthentication().isPresent());
		assertTrue(cfg.getAuthorization().isEmpty());
		assertFalse(cfg.getQueryParameters().isEmpty());
		assertEquals(1, cfg.getQueryParameters().size());
		assertTrue(cfg.getQueryParameters().containsKey("param1"));
		assertTrue(cfg.getOnShowMethods().isEmpty());
		assertTrue(cfg.getCaption().isPresent());
		assertEquals("test", cfg.getCaption().get().getMessage());
		assertEquals("test.code", cfg.getCaption().get().getMessageCode());

	}

	public static class MockUI extends UI {

		public MockUI() {
			this(findOrcreateSession());
		}

		public MockUI(VaadinSession session) {
			getInternals().setSession(session);
			setCurrent(this);
		}

		@Override
		protected void init(VaadinRequest request) {
			// Do nothing
		}

		private static VaadinSession findOrcreateSession() {
			VaadinSession session = VaadinSession.getCurrent();
			if (session == null) {
				session = new AlwaysLockedVaadinSession(null);
				VaadinSession.setCurrent(session);
			}
			return session;
		}

		public static class AlwaysLockedVaadinSession extends MockVaadinSession {

			public AlwaysLockedVaadinSession(VaadinService service) {
				super(service);
				lock();
			}
		}

		public static class MockVaadinSession extends VaadinSession {
			/*
			 * Used to make sure there's at least one reference to the mock session while it's locked. This is
			 * used to prevent the session from being eaten by GC in tests where @Before creates a session and
			 * sets it as the current instance without keeping any direct reference to it. This pattern has a
			 * chance of leaking memory if the session is not unlocked in the right way, but it should be
			 * acceptable for testing use.
			 */
			private static final ThreadLocal<MockVaadinSession> referenceKeeper = new ThreadLocal<>();

			public MockVaadinSession(VaadinService service) {
				super(service);
			}

			@Override
			public void close() {
				super.close();
				closeCount++;
			}

			public int getCloseCount() {
				return closeCount;
			}

			@Override
			public Lock getLockInstance() {
				return lock;
			}

			@Override
			public void lock() {
				super.lock();
				referenceKeeper.set(this);
			}

			@Override
			public void unlock() {
				super.unlock();
				referenceKeeper.remove();
			}

			private int closeCount;

			private ReentrantLock lock = new ReentrantLock();
		}
	}

}
