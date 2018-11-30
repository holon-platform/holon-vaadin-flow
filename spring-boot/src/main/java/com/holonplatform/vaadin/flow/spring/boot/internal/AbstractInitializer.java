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

import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

import com.holonplatform.vaadin.flow.internal.VaadinSessionScope;
import com.vaadin.flow.spring.scopes.VaadinUIScope;

/**
 * Abstract beans initializer.
 *
 * @since 5.2.0
 */
public abstract class AbstractInitializer {

	/**
	 * Gets whether the bean with given name is session scoped.
	 * @param beanFactory The bean factory
	 * @param beanName The bean name
	 * @return <code>true</code> if session scoped
	 */
	protected static boolean isSessionScope(BeanFactory beanFactory, String beanName) {
		return getBeanScope(beanFactory, beanName).filter(scope -> scope != null).map(
				scope -> (WebApplicationContext.SCOPE_SESSION.equals(scope) || VaadinSessionScope.NAME.equals(scope)))
				.orElse(false);
	}

	/**
	 * Gets whether the bean with given name is UI scoped.
	 * @param beanFactory The bean factory
	 * @param beanName The bean name
	 * @return <code>true</code> if UI scoped
	 */
	protected static boolean isUIScope(BeanFactory beanFactory, String beanName) {
		return getBeanScope(beanFactory, beanName).filter(scope -> scope != null)
				.map(scope -> VaadinUIScope.VAADIN_UI_SCOPE_NAME.equals(scope)).orElse(false);
	}

	/**
	 * Get the scope of given bean name, if available.
	 * @param beanFactory The bean factory
	 * @param beanName The bean name
	 * @return Optional bean scope
	 */
	protected static Optional<String> getBeanScope(BeanFactory beanFactory, String beanName) {
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			try {
				return Optional.ofNullable(
						((ConfigurableListableBeanFactory) beanFactory).getBeanDefinition(beanName).getScope());
			} catch (@SuppressWarnings("unused") NoSuchBeanDefinitionException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

}
