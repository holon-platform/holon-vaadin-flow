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
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.spring.EnableNavigator;
import com.holonplatform.vaadin.flow.spring.boot.internal.DefaultNavigatorErrorsRegistrar;

/**
 * TODO
 */
@Configuration
@ConditionalOnClass(Navigator.class)
@AutoConfigureBefore(com.vaadin.flow.spring.SpringBootAutoConfiguration.class)
@AutoConfigureAfter(com.vaadin.flow.spring.VaadinScopesConfig.class)
public class NavigatorAutoConfiguration {

	@Configuration
	@ConditionalOnMissingBean(Navigator.class)
	@EnableNavigator
	static class UINavigatorAutoConfiguration {

	}

	@Configuration
	@Import(DefaultNavigatorErrorsRegistrar.class)
	static class DefaultNavigatorErrorsAutoConfiguration {

	}

}
