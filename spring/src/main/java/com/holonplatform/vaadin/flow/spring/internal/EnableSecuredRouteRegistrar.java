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

import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.holonplatform.core.internal.utils.ClassUtils;

/**
 * @author BODSI08
 *
 */
public class EnableSecuredRouteRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

	static final String SECURED_ROUTE_INITIALIZER_BEAN_NAME = SecuredRouteServiceInitListener.class.getName();

	/**
	 * Secured annotation presence in classpath for classloader
	 */
	private static final Map<ClassLoader, Boolean> SECURED_ANNOTATION_PRESENT = new WeakHashMap<>();

	private ClassLoader classLoader;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.
	 * core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		if (isSecuredAnnotationsPresent(classLoader)
				&& !registry.containsBeanDefinition(SECURED_ROUTE_INITIALIZER_BEAN_NAME)) {
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClass(SecuredRouteServiceInitListener.class);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition(SECURED_ROUTE_INITIALIZER_BEAN_NAME, beanDefinition);
		}
	}

	/**
	 * Checks whether the <code>Secured</code> annotation is available from classpath.
	 * @param classLoader ClassLoader to use
	 * @return <code>true</code> if present
	 */
	private static boolean isSecuredAnnotationsPresent(ClassLoader classLoader) {
		if (SECURED_ANNOTATION_PRESENT.containsKey(classLoader)) {
			Boolean present = SECURED_ANNOTATION_PRESENT.get(classLoader);
			return (present != null && present.booleanValue());
		}
		boolean present = ClassUtils.isPresent("org.springframework.security.access.annotation.Secured", classLoader);
		SECURED_ANNOTATION_PRESENT.put(classLoader, present);
		return present;
	}

}
