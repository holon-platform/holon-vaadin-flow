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
package com.holonplatform.vaadin.flow.spring.internal;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigator;
import com.holonplatform.vaadin.flow.spring.EnableNavigator;
import com.vaadin.flow.spring.scopes.VaadinUIScope;

/**
 * A bean definition registrar for the {@link EnableNavigator} annotation.
 * 
 * @since 5.2.0
 */
public class UINavigatorRegistrar implements ImportBeanDefinitionRegistrar {

	private static final Logger LOGGER = VaadinLogger.create();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

		if (!annotationMetadata.isAnnotated(EnableNavigator.class.getName())) {
			// ignore call from sub classes
			return;
		}

		// register UI-scoped Navigator bean definition
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(DefaultNavigator.class);
		beanDefinition.setScope(VaadinUIScope.VAADIN_UI_SCOPE_NAME);
		beanDefinition.setAutowireCandidate(true);
		registry.registerBeanDefinition(Navigator.CONTEXT_KEY, beanDefinition);

		LOGGER.info("UI scoped Navigator registered");
	}

}
