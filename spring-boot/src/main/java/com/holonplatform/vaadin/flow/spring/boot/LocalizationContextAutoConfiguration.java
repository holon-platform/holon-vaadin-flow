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
package com.holonplatform.vaadin.flow.spring.boot;

import java.util.Locale;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.i18n.LocalizationContextI18NProvider;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.spring.boot.internal.LocalizationContextInitializer;
import com.holonplatform.vaadin.flow.spring.boot.internal.LocalizationContextPostProcessor;
import com.vaadin.flow.i18n.I18NProvider;

/**
 * A Spring Boot auto-configuration class to setup the {@link LocalizationContext} integration with
 * the Vaadin session and UI localization support. The auto-configuration strategy implemented by
 * this class is the following:
 * <p>
 * 1. If a {@link LocalizationContext} type bean is available in context, and its scope is either
 * <code>vaadin-ui</code> or <code>session</code>/<code>"vaadin-session"</code>, a post processor is
 * configured to listener to context localization changes and reflect the changed context
 * {@link Locale} to the current UI or Vaadin Session, according to the {@link LocalizationContext}
 * bean scope. This feature can be disabled using the application configuration property
 * <code>holon.vaadin.localization-context.reflect-locale=false</code>.
 * </p>
 * <p>
 * 2. If a {@link LocalizationContext} type bean is available in context, and its scope is either
 * <code>vaadin-ui</code> or <code>session</code>/<code>"vaadin-session"</code>, an initializer is
 * bound to the Vaadin Session or UI initialization event, according to the
 * {@link LocalizationContext} bean scope, to set the initial Session/UI {@link Locale} as
 * {@link LocalizationContext} localization. This feature can be disabled using the application
 * configuration property <code>holon.vaadin.localization-context.auto-init=false</code>.
 * </p>
 * <p>
 * 3. If a {@link LocalizationContext} type bean is available in context, and {@link I18NProvider}
 * bean type is not already defined, a Vaadin {@link I18NProvider} bean type is registered, using
 * the current {@link LocalizationContext} to provide the messages localization. See
 * {@link LocalizationContextI18NProvider}. This feature can be disabled using the application
 * configuration property <code>holon.vaadin.localization-context.i18nprovider=false</code>.
 * </p>
 * 
 * @since 5.2.0
 */
@AutoConfiguration
@ConditionalOnClass(Navigator.class)
@AutoConfigureAfter(com.vaadin.flow.spring.SpringBootAutoConfiguration.class)
public class LocalizationContextAutoConfiguration {

	@Configuration
	@ConditionalOnBean(LocalizationContext.class)
	@ConditionalOnProperty(prefix = "holon.vaadin.localization-context", name = "reflect-locale", matchIfMissing = true)
	static class LocalizationContextPostProcessorConfiguration {

		@Bean
		static LocalizationContextPostProcessor localizationContextPostProcessor() {
			return new LocalizationContextPostProcessor();
		}

	}

	@Configuration
	@ConditionalOnBean(LocalizationContext.class)
	@ConditionalOnProperty(prefix = "holon.vaadin.localization-context", name = "auto-init", matchIfMissing = true)
	@Import(LocalizationContextInitializer.class)
	static class LocalizationContextInitializerConfiguration {

	}

	@Configuration
	@ConditionalOnBean(LocalizationContext.class)
	@ConditionalOnMissingBean(I18NProvider.class)
	@ConditionalOnProperty(prefix = "holon.vaadin.localization-context", name = "i18nprovider", matchIfMissing = true)
	static class I18NProviderConfiguration {

		@Bean
		public LocalizationContextI18NProvider localizationContextI18NProvider() {
			return LocalizationContextI18NProvider.create();
		}

	}

}
