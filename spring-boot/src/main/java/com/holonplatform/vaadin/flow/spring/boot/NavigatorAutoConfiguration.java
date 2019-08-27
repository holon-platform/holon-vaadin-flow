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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.exceptions.ForbiddenNavigationException;
import com.holonplatform.vaadin.flow.navigator.exceptions.UnauthorizedNavigationException;
import com.holonplatform.vaadin.flow.spring.EnableNavigator;
import com.holonplatform.vaadin.flow.spring.boot.internal.NavigatorServletContextInitializer;

/**
 * A Spring Boot auto-configuration class to setup a UI-scoped {@link Navigator} bean and register the default error
 * components for the {@link UnauthorizedNavigationException} and {@link ForbiddenNavigationException} navigation
 * exceptions.
 * <p>
 * The UI-scoped {@link Navigator} bean is registered only if there is not another {@link Navigator} type bean
 * registered in context.
 * </p>
 * <p>
 * The <code>holon.vaadin.navigator.errors.enabled</code> boolean configuration property can be used to disable the
 * default error components registration, setting its value to <code>false</code>.
 * </p>
 * <p>
 * Furthermore, this class sets up a servlet context initializer to pre-load the navigation target classes
 * configurations.
 * </p>
 * 
 * @since 5.2.0
 */
@Configuration
@ConditionalOnClass(Navigator.class)
@AutoConfigureBefore(com.vaadin.flow.spring.SpringBootAutoConfiguration.class)
@AutoConfigureAfter(com.vaadin.flow.spring.VaadinScopesConfig.class)
public class NavigatorAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public ServletContextInitializer navigatorNavigationTargetConfigurationServletContextInitializer() {
		return new NavigatorServletContextInitializer(applicationContext);
	}

	@Configuration
	@ConditionalOnMissingBean(Navigator.class)
	@EnableNavigator
	static class UINavigatorAutoConfiguration {

	}

}
