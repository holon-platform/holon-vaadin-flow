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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.holonplatform.vaadin.flow.navigator.internal.listeners.DefaultNavigationTargetAfterNavigationListener;
import com.holonplatform.vaadin.flow.navigator.internal.listeners.DefaultNavigationTargetBeforeEnterListener;
import com.holonplatform.vaadin.flow.navigator.test.data.LoginNavigationTarget;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget1;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget10;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget11;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget12;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget13;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget14;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget2;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget3;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget4;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget5;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget6;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget7;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget8;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget9;
import com.holonplatform.vaadin.flow.navigator.test.data.TestNavigationError;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.startup.ApplicationConfiguration;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractNavigatorTest {

	protected static final String TEST_SESSION_ID = "TestSessionID";
	protected static final int TEST_UIID = 1;

	protected RouteRegistry routeRegistry;

	protected Router router;

	protected VaadinService vaadinService;

	protected VaadinSession vaadinSession;

	protected UI ui;

	protected static List<Class<? extends Component>> navigationTargets;
	protected static List<Class<? extends Component>> errors;

	@BeforeAll
	public static void _beforeAll() throws Exception {

		navigationTargets = Arrays.asList(NavigationTarget1.class, NavigationTarget2.class, NavigationTarget3.class,
				NavigationTarget4.class, NavigationTarget5.class, NavigationTarget6.class, NavigationTarget7.class,
				NavigationTarget8.class, NavigationTarget9.class, NavigationTarget10.class, NavigationTarget11.class,
				NavigationTarget12.class, NavigationTarget13.class, NavigationTarget14.class,
				LoginNavigationTarget.class);
		errors = Arrays.asList(TestNavigationError.class);

//				routeRegistry = new TestRouteRegistry(
//						Arrays.asList(NavigationTarget1.class, NavigationTarget2.class, NavigationTarget3.class,
//								NavigationTarget4.class, NavigationTarget5.class, NavigationTarget6.class,
//								NavigationTarget7.class, NavigationTarget8.class, NavigationTarget9.class,
//								NavigationTarget10.class, NavigationTarget11.class, NavigationTarget12.class,
//								NavigationTarget13.class, NavigationTarget14.class, LoginNavigationTarget.class),
//						Arrays.asList(TestNavigationError.class));
	}

//	@AfterAll
//	public static void _afterAll() {
//		routeRegistry = null;
//	}

	@BeforeEach
	public void _beforeEach() throws Exception {

		vaadinService = createVaadinService();
		vaadinSession = createVaadinSession(vaadinService, Locale.US);
		VaadinRequest request = buildVaadinRequest();
		CurrentInstance.set(VaadinSession.class, vaadinSession);
		CurrentInstance.set(VaadinRequest.class, request);

		routeRegistry = new TestRouteRegistry(vaadinService.getContext(), navigationTargets, errors);
		router = new Router(routeRegistry);

		ui = new UI();
		ui.getInternals().setSession(vaadinSession);
		// ui.getInternals().setAppId(TEST_UIID); ui.doInit(request, TEST_UIID);

		ui.addBeforeEnterListener(new DefaultNavigationTargetBeforeEnterListener());
		ui.addAfterNavigationListener(new DefaultNavigationTargetAfterNavigationListener());

		CurrentInstance.setCurrent(ui);
	}

	@AfterEach
	public void _afterEach() {
		router = null;
	}

	@SuppressWarnings("serial")
	private class TestVaadinService extends VaadinServletService {

		public RouteRegistry getServiceRouteRegistry() {
			return routeRegistry;
		}

		@Override
		protected RouteRegistry getRouteRegistry() {
			return getServiceRouteRegistry();
		}

	}

	protected VaadinService createVaadinService() throws Exception {
		TestVaadinService vaadinService = mock(TestVaadinService.class);
//		when(vaadinService.getDeploymentConfiguration())
//				.thenReturn(new DefaultDeploymentConfiguration(ApplicationConfiguration.get(vaadinService.getContext()),
//						VaadinServletService.class, getDeploymentProperties()));
		when(vaadinService.getMainDivId(any(VaadinSession.class), any(VaadinRequest.class)))
				.thenReturn("test-main-div-id");
		when(vaadinService.getRouter()).thenReturn(router);
		when(vaadinService.getInstantiator()).thenReturn(new DefaultInstantiator(vaadinService));
		when(vaadinService.getServiceRouteRegistry()).thenCallRealMethod();
		when(vaadinService.getRouteRegistry()).thenReturn(routeRegistry);
		return vaadinService;
	}

	protected VaadinSession createVaadinSession(VaadinService service, Locale locale) throws Exception {
		WrappedSession wrappedSession = mock(WrappedSession.class);
		VaadinSession session = mock(VaadinSession.class);
		when(session.getState()).thenReturn(VaadinSessionState.OPEN);
		when(session.getSession()).thenReturn(wrappedSession);
		when(session.getService()).thenReturn(service);
		when(session.getSession().getId()).thenReturn(TEST_SESSION_ID);
		when(session.hasLock()).thenReturn(true);
		when(session.getLocale()).thenReturn(locale != null ? locale : Locale.US);
		Properties p = new Properties();
//		p.setProperty(InitParameters.SERVLET_PARAMETER_COMPATIBILITY_MODE, "true");
		when(session.getConfiguration()).thenReturn(new DefaultDeploymentConfiguration(
				ApplicationConfiguration.get((vaadinService.getContext())), getClass(), p));
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
//		properties.put(InitParameters.SERVLET_PARAMETER_COMPATIBILITY_MODE, "true");
		return properties;
	}

	@SuppressWarnings("serial")
	class TestRouteRegistry extends ApplicationRouteRegistry {

		public TestRouteRegistry(VaadinContext context, List<Class<? extends Component>> navigationTargets,
				List<Class<? extends Component>> errors) throws InvalidRouteConfigurationException {
			super(context);
			navigationTargets.forEach(navigationTarget -> {
				String route = RouteUtil.getRoutePath(context, navigationTarget);
				setRoute(route, navigationTarget, RouteUtil.getParentLayouts(context, navigationTarget, route));
			});
			setErrorNavigationTargets(errors.stream().collect(Collectors.toSet()));
		}

	}

}
