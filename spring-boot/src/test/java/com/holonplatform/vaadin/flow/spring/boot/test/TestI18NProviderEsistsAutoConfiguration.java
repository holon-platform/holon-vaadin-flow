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
package com.holonplatform.vaadin.flow.spring.boot.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.i18n.LocalizationContextI18NProvider;
import com.vaadin.flow.i18n.I18NProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestI18NProviderEsistsAutoConfiguration.Config.class)
public class TestI18NProviderEsistsAutoConfiguration extends AbstractVaadinSpringBootTest {

	@Configuration
	@EnableAutoConfiguration
	static class Config {

		@Bean
		public LocalizationContext localizationContext() {
			return LocalizationContext.builder().build();
		}

		@SuppressWarnings("serial")
		@Bean
		public I18NProvider i18NProvider() {
			return new I18NProvider() {

				@Override
				public String getTranslation(String key, Locale locale, Object... params) {
					return null;
				}

				@Override
				public List<Locale> getProvidedLocales() {
					return null;
				}
			};
		}

	}

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void testI18NProvider() {
		I18NProvider i18NProvider = applicationContext.getBean(I18NProvider.class);
		assertNotNull(i18NProvider);
		assertFalse(i18NProvider instanceof LocalizationContextI18NProvider);
	}

}
