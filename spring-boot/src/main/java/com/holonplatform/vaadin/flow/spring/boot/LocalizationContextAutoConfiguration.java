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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.spring.boot.internal.LocalizationContextPostProcessor;

/**
 * A Spring Boot auto-configuration class to setup the {@link LocalizationContext} integration with the Vaadin session
 * and UI localization support.
 * 
 * @since 5.2.0
 */
@Configuration
@ConditionalOnClass(Navigator.class)
@AutoConfigureAfter(com.vaadin.flow.spring.SpringBootAutoConfiguration.class)
public class LocalizationContextAutoConfiguration {

	@Configuration
	@ConditionalOnBean(LocalizationContext.class)
	@ConditionalOnProperty(prefix = "holon.vaadin.localization", name = "reflect-in-ui", matchIfMissing = true)
	static class UINavigatorAutoConfiguration {

		@Bean
		static LocalizationContextPostProcessor localizationContextPostProcessor() {
			return new LocalizationContextPostProcessor();
		}

	}

}
