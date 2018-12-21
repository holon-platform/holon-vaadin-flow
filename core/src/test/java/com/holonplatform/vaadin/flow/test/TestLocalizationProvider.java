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
package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationContextI18NProvider;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletService;

public class TestLocalizationProvider {

	@Test
	public void testBuilder() {
		LocalizationContext ctx = LocalizationContext.builder().build();

		LocalizationProvider provider = LocalizationProvider.create(ctx);
		assertNotNull(provider);
		assertFalse(provider.getLocale().isPresent());
		assertFalse(provider.getMessage(Locale.US, Localizable.builder().messageCode("test.code").build()).isPresent());
		assertFalse(provider.getMessage(Locale.US, "test.code").isPresent());
		assertEquals("dft", provider.getMessage(Locale.US, "dft", "test.code"));

		provider = LocalizationProvider.create(LocalizationTestUtils.getTestLocalizationContext());
		assertNotNull(provider);
		assertTrue(provider.getLocale().isPresent());
		assertFalse(provider.getMessage(Locale.ITALIAN, Localizable.builder().messageCode("test.code").build())
				.isPresent());
		assertFalse(provider.getMessage(Locale.ITALIAN, "test.code").isPresent());
		assertTrue(provider.getMessage(Locale.US, Localizable.builder().messageCode("test.code").build()).isPresent());
		assertTrue(provider.getMessage(Locale.US, "test.code").isPresent());
		assertEquals("TestUS",
				provider.getMessage(Locale.US, Localizable.builder().messageCode("test.code").build()).orElse(null));
		assertEquals("TestUS", provider.getMessage(Locale.US, "test.code").orElse(null));
		assertEquals("TestUS", provider.getMessage(Locale.US, "dft", "test.code"));
		assertEquals("dft", provider.getMessage(Locale.US, "dft", "test.x"));
		assertEquals("dft", provider.getMessage(Locale.ITALIAN, "dft", "test.code"));

		provider = LocalizationProvider
				.create(LocalizationContextI18NProvider.create(LocalizationTestUtils.getTestLocalizationContext()));
		assertNotNull(provider);
		assertTrue(provider.getLocale().isPresent());
		assertFalse(provider.getMessage(Locale.ITALIAN, Localizable.builder().messageCode("test.code").build())
				.isPresent());
		assertFalse(provider.getMessage(Locale.ITALIAN, "test.code").isPresent());
		assertTrue(provider.getMessage(Locale.US, Localizable.builder().messageCode("test.code").build()).isPresent());
		assertTrue(provider.getMessage(Locale.US, "test.code").isPresent());
		assertEquals("TestUS",
				provider.getMessage(Locale.US, Localizable.builder().messageCode("test.code").build()).orElse(null));
		assertEquals("TestUS", provider.getMessage(Locale.US, "test.code").orElse(null));
		assertEquals("TestUS", provider.getMessage(Locale.US, "dft", "test.code"));
		assertEquals("dft", provider.getMessage(Locale.US, "dft", "test.x"));
		assertEquals("dft", provider.getMessage(Locale.ITALIAN, "dft", "test.code"));
	}

	@Test
	public void testCurrentLocale() {

		assertFalse(LocalizationProvider.getCurrentLocale().isPresent());

		UI ui = new UI();
		ui.setLocale(Locale.US);
		try {
			CurrentInstance.set(UI.class, ui);
			assertTrue(LocalizationProvider.getCurrentLocale().isPresent());
			assertEquals(Locale.US, LocalizationProvider.getCurrentLocale().orElse(null));
		} finally {
			CurrentInstance.set(UI.class, null);
		}

		assertFalse(LocalizationProvider.getCurrentLocale().isPresent());

		Locale locale = LocalizationTestUtils.withTestLocalizationContext(() -> {
			return LocalizationProvider.getCurrentLocale().orElse(null);
		});
		assertEquals(Locale.US, locale);

		VaadinService service = createVaadinService(LocalizationTestUtils.getTestLocalizationContext());
		try {
			CurrentInstance.set(VaadinService.class, service);
			assertTrue(LocalizationProvider.getCurrentLocale().isPresent());
			assertEquals(Locale.US, LocalizationProvider.getCurrentLocale().orElse(null));
		} finally {
			CurrentInstance.set(VaadinService.class, null);
		}

	}

	@Test
	public void testLocalize() {

		assertFalse(LocalizationProvider
				.getLocalization(Locale.US, Localizable.builder().messageCode("test.code").build()).isPresent());
		assertFalse(LocalizationProvider.getLocalization(Locale.US, "test.code").isPresent());
		assertEquals("dft", LocalizationProvider.getLocalization(Locale.US, "dft", "test.code"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			assertFalse(LocalizationProvider
					.getLocalization(Locale.ITALIAN, Localizable.builder().messageCode("test.code").build())
					.isPresent());
			assertFalse(LocalizationProvider.getLocalization(Locale.ITALIAN, "test.code").isPresent());
			assertTrue(LocalizationProvider
					.getLocalization(Locale.US, Localizable.builder().messageCode("test.code").build()).isPresent());
			assertTrue(LocalizationProvider.getLocalization(Locale.US, "test.code").isPresent());
			assertEquals("TestUS", LocalizationProvider
					.getLocalization(Locale.US, Localizable.builder().messageCode("test.code").build()).orElse(null));
			assertEquals("TestUS", LocalizationProvider.getLocalization(Locale.US, "test.code").orElse(null));
			assertEquals("TestUS", LocalizationProvider.getLocalization(Locale.US, "dft", "test.code"));
			assertEquals("dft", LocalizationProvider.getLocalization(Locale.US, "dft", "test.x"));
			assertEquals("dft", LocalizationProvider.getLocalization(Locale.ITALIAN, "dft", "test.code"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			assertTrue(
					LocalizationProvider.localize(Localizable.builder().messageCode("test.code").build()).isPresent());
			assertTrue(LocalizationProvider.localize("test.code").isPresent());
			assertEquals("TestUS",
					LocalizationProvider.localize(Localizable.builder().messageCode("test.code").build()).orElse(null));
			assertEquals("TestUS", LocalizationProvider.localize("test.code").orElse(null));
			assertEquals("TestUS", LocalizationProvider.localize("dft", "test.code"));
			assertEquals("dft", LocalizationProvider.localize("dft", "test.x"));
		});

		VaadinService service = createVaadinService(LocalizationTestUtils.getTestLocalizationContext());
		try {
			CurrentInstance.set(VaadinService.class, service);
			assertTrue(
					LocalizationProvider.localize(Localizable.builder().messageCode("test.code").build()).isPresent());
			assertTrue(LocalizationProvider.localize("test.code").isPresent());
			assertEquals("TestUS",
					LocalizationProvider.localize(Localizable.builder().messageCode("test.code").build()).orElse(null));
			assertEquals("TestUS", LocalizationProvider.localize("test.code").orElse(null));
			assertEquals("TestUS", LocalizationProvider.localize("dft", "test.code"));
			assertEquals("dft", LocalizationProvider.localize("dft", "test.x"));
		} finally {
			CurrentInstance.set(VaadinService.class, null);
		}

	}

	@Test
	public void testComponents() {

		assertFalse(Components.getCurrentLocale().isPresent());

		UI ui = new UI();
		ui.setLocale(Locale.US);
		try {
			CurrentInstance.set(UI.class, ui);
			assertTrue(Components.getCurrentLocale().isPresent());
			assertEquals(Locale.US, Components.getCurrentLocale().orElse(null));
		} finally {
			CurrentInstance.set(UI.class, null);
		}

		assertFalse(Components.getCurrentLocale().isPresent());

		Locale locale = LocalizationTestUtils.withTestLocalizationContext(() -> {
			return Components.getCurrentLocale().orElse(null);
		});
		assertEquals(Locale.US, locale);

		VaadinService service = createVaadinService(LocalizationTestUtils.getTestLocalizationContext());
		try {
			CurrentInstance.set(VaadinService.class, service);
			assertTrue(Components.getCurrentLocale().isPresent());
			assertEquals(Locale.US, Components.getCurrentLocale().orElse(null));
		} finally {
			CurrentInstance.set(VaadinService.class, null);
		}

		assertFalse(Components.getLocalization(Locale.US, Localizable.builder().messageCode("test.code").build())
				.isPresent());
		assertFalse(Components.getLocalization(Locale.US, "test.code").isPresent());
		assertEquals("dft", Components.getLocalization(Locale.US, "dft", "test.code"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			assertFalse(
					Components.getLocalization(Locale.ITALIAN, Localizable.builder().messageCode("test.code").build())
							.isPresent());
			assertFalse(Components.getLocalization(Locale.ITALIAN, "test.code").isPresent());
			assertTrue(Components.getLocalization(Locale.US, Localizable.builder().messageCode("test.code").build())
					.isPresent());
			assertTrue(Components.getLocalization(Locale.US, "test.code").isPresent());
			assertEquals("TestUS", Components
					.getLocalization(Locale.US, Localizable.builder().messageCode("test.code").build()).orElse(null));
			assertEquals("TestUS", Components.getLocalization(Locale.US, "test.code").orElse(null));
			assertEquals("TestUS", Components.getLocalization(Locale.US, "dft", "test.code"));
			assertEquals("dft", Components.getLocalization(Locale.US, "dft", "test.x"));
			assertEquals("dft", Components.getLocalization(Locale.ITALIAN, "dft", "test.code"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			assertTrue(Components.localize(Localizable.builder().messageCode("test.code").build()).isPresent());
			assertTrue(Components.localize("test.code").isPresent());
			assertEquals("TestUS",
					Components.localize(Localizable.builder().messageCode("test.code").build()).orElse(null));
			assertEquals("TestUS", Components.localize("test.code").orElse(null));
			assertEquals("TestUS", Components.localize("dft", "test.code"));
			assertEquals("dft", Components.localize("dft", "test.x"));
		});

		service = createVaadinService(LocalizationTestUtils.getTestLocalizationContext());
		try {
			CurrentInstance.set(VaadinService.class, service);
			assertTrue(Components.localize(Localizable.builder().messageCode("test.code").build()).isPresent());
			assertTrue(Components.localize("test.code").isPresent());
			assertEquals("TestUS",
					Components.localize(Localizable.builder().messageCode("test.code").build()).orElse(null));
			assertEquals("TestUS", Components.localize("test.code").orElse(null));
			assertEquals("TestUS", Components.localize("dft", "test.code"));
			assertEquals("dft", Components.localize("dft", "test.x"));
		} finally {
			CurrentInstance.set(VaadinService.class, null);
		}

	}

	private static VaadinService createVaadinService(LocalizationContext localizationContext) {
		Instantiator instantiator = createInstantiator(localizationContext);
		VaadinServletService vaadinService = mock(VaadinServletService.class);
		when(vaadinService.getDeploymentConfiguration())
				.thenReturn(new DefaultDeploymentConfiguration(VaadinServletService.class, getDeploymentProperties()));
		when(vaadinService.getInstantiator()).thenReturn(instantiator);
		return vaadinService;
	}

	private static Instantiator createInstantiator(LocalizationContext localizationContext) {
		Instantiator instantiator = mock(Instantiator.class);
		when(instantiator.getI18NProvider()).thenReturn(
				(localizationContext == null) ? null : LocalizationContextI18NProvider.create(localizationContext));
		return instantiator;
	}

	private static Properties getDeploymentProperties() {
		Properties properties = new Properties();
		properties.put(Constants.SERVLET_PARAMETER_PRODUCTION_MODE, "true");
		return properties;
	}

}
