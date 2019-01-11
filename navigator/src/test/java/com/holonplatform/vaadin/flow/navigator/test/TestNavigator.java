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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.Authenticator;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.exceptions.InvalidCredentialsException;
import com.holonplatform.auth.token.AccountCredentialsToken;
import com.holonplatform.core.Context;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.Navigator.NavigationBuilder;
import com.holonplatform.vaadin.flow.navigator.exceptions.ForbiddenNavigationException;
import com.holonplatform.vaadin.flow.navigator.exceptions.UnauthorizedNavigationException;
import com.holonplatform.vaadin.flow.navigator.test.data.AuthLayout;
import com.holonplatform.vaadin.flow.navigator.test.data.AuthParentLayout;
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
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget6;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget7;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget8;
import com.holonplatform.vaadin.flow.navigator.test.data.NavigationTarget9;
import com.holonplatform.vaadin.flow.navigator.test.data.NoAuthLayout;
import com.holonplatform.vaadin.flow.navigator.test.data.TestNavigationError;
import com.vaadin.flow.component.Component;
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

		final Navigator navigator = Navigator.create(ui);
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
		NavigationTarget1 nt1 = getNavigationComponent(NavigationTarget1.class);
		assertNotNull(nt1);

		navigator.navigateTo("1");
		nt1 = getNavigationComponent(NavigationTarget1.class);
		assertNotNull(nt1);

		navigator.navigateTo(NavigationTarget9.class, "test");
		NavigationTarget9 nt9 = getNavigationComponent(NavigationTarget9.class);
		assertNotNull(nt9);

		Map<String, Object> params = new HashMap<>();
		params.put("param1", "test1");
		params.put("param3", 3d);

		navigator.navigateTo("2", params);
		NavigationTarget2 nt2 = getNavigationComponent(NavigationTarget2.class);
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
		nt2 = getNavigationComponent(NavigationTarget2.class);
		assertNotNull(nt2);
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
		NavigationTarget3 nt3 = getNavigationComponent(NavigationTarget3.class);
		assertNotNull(nt3);
		assertNull(nt3.getShowed());

		navigator.navigation(NavigationTarget3.class).withQueryParameter("param1", "testx").navigate();
		nt3 = getNavigationComponent(NavigationTarget3.class);
		assertNotNull(nt3);
		assertNotNull(nt3.getShowed());
		assertEquals("testx", nt3.getShowed());

		navigator.navigateToLocation("3?param1=testx");
		nt3 = getNavigationComponent(NavigationTarget3.class);
		assertNotNull(nt3);
		assertNotNull(nt3.getShowed());
		assertEquals("testx", nt3.getShowed());

		navigator.navigateTo("4");
		NavigationTarget4 nt4 = getNavigationComponent(NavigationTarget4.class);
		assertNotNull(nt4);
		assertNotNull(nt4.getLocation());

		params = new HashMap<>();
		params.put("param1", "test1");

		navigator.navigateTo(NavigationTarget9.class, "test", params);
		nt9 = getNavigationComponent(NavigationTarget9.class);
		assertNotNull(nt9);
		assertEquals("test1", nt9.getParam1());

	}

	@Test
	public void testOptionalNavigationParameters() {

		final Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);

		navigator.navigateTo("11");
		NavigationTarget11 nt11 = getNavigationComponent(NavigationTarget11.class);
		assertNotNull(nt11);
		assertNotNull(nt11.getOptionalParam());
		assertFalse(nt11.getOptionalParam().isPresent());

		navigator.navigation("11").withQueryParameter("param", "test").navigate();
		nt11 = getNavigationComponent(NavigationTarget11.class);
		assertNotNull(nt11);
		assertNotNull(nt11.getOptionalParam());
		assertTrue(nt11.getOptionalParam().isPresent());
		assertEquals("test", nt11.getOptionalParam().orElse(null));

	}

	@Test
	public void testQueryParameterSetter() {

		final Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);

		navigator.navigateToLocation("8?param1=test");
		NavigationTarget8 nt = getNavigationComponent(NavigationTarget8.class);
		assertNotNull(nt);
		assertNotNull(nt.getParamValue());
		assertEquals("!test", nt.getParamValue());
	}

	@Test
	public void testNavigationAuth() {

		final Navigator navigator = Navigator.create(ui);
		assertNotNull(navigator);

		final Realm realm = Realm.builder().withDefaultAuthorizer()
				.withAuthenticator(Authenticator.create(AccountCredentialsToken.class, token -> {
					if ("test".equals(token.getPrincipal())) {
						return Authentication.builder("test").build();
					}
					if ("test2".equals(token.getPrincipal())) {
						return Authentication.builder("test").withPermission("X").build();
					}
					if ("test3".equals(token.getPrincipal())) {
						return Authentication.builder("test").withPermission("A").build();
					}
					throw new InvalidCredentialsException();
				})).build();

		navigator.navigateTo("6");
		TestNavigationError error = getNavigationComponent(TestNavigationError.class);
		assertNotNull(error);
		assertNotNull(error.getException());
		assertEquals(IllegalStateException.class, error.getException().getClass());

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			navigator.navigateTo("6");
			TestNavigationError error2 = getNavigationComponent(TestNavigationError.class);
			assertNotNull(error2);
			assertNotNull(error2.getException());
			assertEquals(UnauthorizedNavigationException.class, error2.getException().getClass());
		});

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			AuthContext.require().authenticate(AccountCredentialsToken.create("test", "pwd"));

			navigator.navigateTo("6");
			NavigationTarget6 nt6 = getNavigationComponent(NavigationTarget6.class);
			assertNotNull(nt6);
		});

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			navigator.navigateTo(NavigationTarget7.class);
			TestNavigationError error2 = getNavigationComponent(TestNavigationError.class);
			assertNotNull(error2);
			assertNotNull(error2.getException());
			assertEquals(UnauthorizedNavigationException.class, error2.getException().getClass());
		});

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			AuthContext.require().authenticate(AccountCredentialsToken.create("test", "pwd"));

			navigator.navigateTo(NavigationTarget7.class);
			TestNavigationError error2 = getNavigationComponent(TestNavigationError.class);
			assertNotNull(error2);
			assertNotNull(error2.getException());
			assertEquals(ForbiddenNavigationException.class, error2.getException().getClass());
		});

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			AuthContext.require().authenticate(AccountCredentialsToken.create("test2", "pwd"));

			navigator.navigateTo(NavigationTarget7.class);
			TestNavigationError error2 = getNavigationComponent(TestNavigationError.class);
			assertNotNull(error2);
			assertNotNull(error2.getException());
			assertEquals(ForbiddenNavigationException.class, error2.getException().getClass());
		});

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			AuthContext.require().authenticate(AccountCredentialsToken.create("test3", "pwd"));

			navigator.navigateTo(NavigationTarget7.class);
			NavigationTarget7 nt7 = getNavigationComponent(NavigationTarget7.class);
			assertNotNull(nt7);
		});

		// redirect
		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			navigator.navigateTo(NavigationTarget10.class);
			LoginNavigationTarget login = getNavigationComponent(LoginNavigationTarget.class);
			assertNotNull(login);
		});
		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			AuthContext.require().authenticate(AccountCredentialsToken.create("test", "pwd"));

			navigator.navigateTo(NavigationTarget10.class);
			NavigationTarget10 nt10 = getNavigationComponent(NavigationTarget10.class);
			assertNotNull(nt10);
		});

		// layouts
		navigator.navigateTo(NavigationTarget13.class);
		NoAuthLayout nt13 = getNavigationComponent(NoAuthLayout.class);
		assertNotNull(nt13);

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			navigator.navigateTo(NavigationTarget12.class);
			TestNavigationError error2 = getNavigationComponent(TestNavigationError.class);
			assertNotNull(error2);
			assertNotNull(error2.getException());
			assertEquals(UnauthorizedNavigationException.class, error2.getException().getClass());

			AuthContext.require().authenticate(AccountCredentialsToken.create("test", "pwd"));
			navigator.navigateTo(NavigationTarget12.class);
			AuthLayout t = getNavigationComponent(AuthLayout.class);
			assertNotNull(t);
		});

		Context.get().executeThreadBound(AuthContext.CONTEXT_KEY, AuthContext.create(realm), () -> {
			navigator.navigateTo(NavigationTarget14.class);
			TestNavigationError error2 = getNavigationComponent(TestNavigationError.class);
			assertNotNull(error2);
			assertNotNull(error2.getException());
			assertEquals(UnauthorizedNavigationException.class, error2.getException().getClass());

			AuthContext.require().authenticate(AccountCredentialsToken.create("test", "pwd"));
			navigator.navigateTo(NavigationTarget14.class);
			AuthParentLayout t = getNavigationComponent(AuthParentLayout.class);
			assertNotNull(t);
		});

	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T getNavigationComponent(Class<T> navigationTarget) {
		ui.getElement().getChildren().collect(Collectors.toList());
		Element element = ui.getElement().getChildren().findFirst().orElse(null);
		assertNotNull(element);
		assertTrue(element.getComponent().isPresent());
		assertEquals(navigationTarget, element.getComponent().get().getClass());
		return (T) element.getComponent().get();
	}

}
