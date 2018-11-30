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
package com.holonplatform.vaadin.flow.spring.boot.internal;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.errors.UnauthorizedNavigationError;

/**
 * Add the default navigation errors package name to the Spring Boot auto configuration package names.
 * 
 * @since 5.2.0
 */
public class DefaultNavigatorErrorsRegistrar implements ImportBeanDefinitionRegistrar {

	private static final Logger LOGGER = VaadinLogger.create();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		AutoConfigurationPackages.register(registry, UnauthorizedNavigationError.class.getPackage().getName());
		LOGGER.info("Registered auto-configuration package [" + UnauthorizedNavigationError.class.getPackage().getName()
				+ "]");
	}

}
