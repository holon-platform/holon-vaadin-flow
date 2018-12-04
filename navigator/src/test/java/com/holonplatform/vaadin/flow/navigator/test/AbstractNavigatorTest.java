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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget1;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget2;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget3;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget4;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget5;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget6;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget7;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget8;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget9;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.startup.RouteRegistry;
import com.vaadin.flow.spring.SpringVaadinSession;

public abstract class AbstractNavigatorTest {

	protected static final String TEST_SESSION_ID = "TestSessionID";
	protected static final int TEST_UIID = 1;

	protected static RouteRegistry routeRegistry;

	protected Router router;

	protected VaadinSession vaadinSession;

	protected UI ui;

	@BeforeAll
	public static void _beforeAll() throws Exception {
		routeRegistry = new TestRouteRegistry(Arrays.asList(NavigationTarget1.class, NavigationTarget2.class,
				NavigationTarget3.class, NavigationTarget4.class, NavigationTarget5.class, NavigationTarget6.class,
				NavigationTarget7.class, NavigationTarget8.class, NavigationTarget9.class));
	}

	@AfterAll
	public static void _afterAll() {
		routeRegistry = null;
	}

	@BeforeEach
	public void _beforeEach() throws Exception {
		router = new Router(routeRegistry);

		vaadinSession = createVaadinSession(Locale.US);
		VaadinRequest request = buildVaadinRequest();
		CurrentInstance.set(VaadinSession.class, vaadinSession);
		CurrentInstance.set(VaadinRequest.class, request);

		ui = new UI();
		ui.getInternals().setSession(vaadinSession);
		ui.doInit(request, TEST_UIID);

		CurrentInstance.setCurrent(ui);
	}

	@AfterEach
	public void _afterEach() {
		router = null;
	}

	protected VaadinSession createVaadinSession(Locale locale) throws Exception {
		WrappedSession wrappedSession = mock(WrappedSession.class);
		VaadinServletService vaadinService = mock(VaadinServletService.class);
		when(vaadinService.getDeploymentConfiguration())
				.thenReturn(new DefaultDeploymentConfiguration(VaadinServletService.class, getDeploymentProperties()));
		when(vaadinService.getMainDivId(any(VaadinSession.class), any(VaadinRequest.class)))
				.thenReturn("test-main-div-id");
		when(vaadinService.getRouter()).thenReturn(router);
		SpringVaadinSession session = mock(SpringVaadinSession.class);
		when(session.getState()).thenReturn(VaadinSessionState.OPEN);
		when(session.getSession()).thenReturn(wrappedSession);
		when(session.getService()).thenReturn(vaadinService);
		when(session.getSession().getId()).thenReturn(TEST_SESSION_ID);
		when(session.hasLock()).thenReturn(true);
		when(session.getLocale()).thenReturn(locale != null ? locale : Locale.US);
		return session;
	}

	/**
	 * Build VaadinServletRequest
	 * @return VaadinServletRequest
	 */
	protected VaadinServletRequest buildVaadinRequest() {
		return new VaadinServletRequest(buildHttpServletRequest(), (VaadinServletService) vaadinSession.getService());
	}

	/**
	 * Mocks a HttpServletRequest
	 * @return a HttpServletRequest
	 */
	protected HttpServletRequest buildHttpServletRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getProtocol()).thenReturn("http");
		when(request.getScheme()).thenReturn("http");
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getRemoteHost()).thenReturn("localhost");
		when(request.getRemotePort()).thenReturn(80);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http").append("://").append("localhost"));
		when(request.getRequestURI()).thenReturn("/");
		when(request.getLocalName()).thenReturn("localhost");
		when(request.getLocalPort()).thenReturn(80);
		when(request.isSecure()).thenReturn(false);
		when(request.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
		when(request.getCharacterEncoding()).thenReturn("utf-8");
		when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
		return request;
	}

	protected Properties getDeploymentProperties() {
		Properties properties = new Properties();
		properties.put(Constants.SERVLET_PARAMETER_PRODUCTION_MODE, "true");
		return properties;
	}

	@SuppressWarnings("serial")
	static class TestRouteRegistry extends RouteRegistry {

		public TestRouteRegistry(List<Class<? extends Component>> navigationTargets)
				throws InvalidRouteConfigurationException {
			super();
			setNavigationTargets(navigationTargets.stream().collect(Collectors.toSet()));
		}

	}

}
