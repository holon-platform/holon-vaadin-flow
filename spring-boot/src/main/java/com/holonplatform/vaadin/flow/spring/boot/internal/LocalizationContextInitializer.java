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

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * A bean definition registrar to provide {@link LocalizationContext} beans localization initialization using the
 * session or UI {@link Locale}, according to the {@link LocalizationContext} bean scope.
 * 
 * @since 5.2.0
 */
public class LocalizationContextInitializer extends AbstractInitializer
		implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

	private static final Logger LOGGER = VaadinLogger.create();

	private BeanFactory beanFactory;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.
	 * core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		if (beanFactory instanceof ListableBeanFactory) {
			String[] names = ((ListableBeanFactory) beanFactory).getBeanNamesForType(LocalizationContext.class);
			if (names != null && names.length > 0) {
				final String name = names[0];
				if (isSessionScope(beanFactory, name)) {
					if (!registry.containsBeanDefinition(
							SessionLocalizationContextInitializerServiceInitListener.class.getName())) {
						GenericBeanDefinition definition = new GenericBeanDefinition();
						definition.setBeanClass(SessionLocalizationContextInitializerServiceInitListener.class);
						definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
						registry.registerBeanDefinition(
								SessionLocalizationContextInitializerServiceInitListener.class.getName(), definition);

						LOGGER.info("LocalizationContext localization initializer setted up: using Session Locale");
					}
				} else if (isUIScope(beanFactory, name)) {
					if (!registry.containsBeanDefinition(
							UILocalizationContextInitializerServiceInitListener.class.getName())) {
						GenericBeanDefinition definition = new GenericBeanDefinition();
						definition.setBeanClass(UILocalizationContextInitializerServiceInitListener.class);
						definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
						registry.registerBeanDefinition(
								UILocalizationContextInitializerServiceInitListener.class.getName(), definition);

						LOGGER.info("LocalizationContext localization initializer setted up: using UI Locale");
					}
				}
			}
		}
	}

	static class SessionLocalizationContextInitializerServiceInitListener implements VaadinServiceInitListener {

		private static final long serialVersionUID = 5101338098301799675L;

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
		 */
		@Override
		public void serviceInit(ServiceInitEvent event) {
			event.getSource().addSessionInitListener(e -> {
				LocalizationContext.getCurrent().ifPresent(lc -> lc.localize(e.getSession().getLocale(), false));
			});
		}

	}

	static class UILocalizationContextInitializerServiceInitListener implements VaadinServiceInitListener {

		private static final long serialVersionUID = 5101338098301799675L;

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
		 */
		@Override
		public void serviceInit(ServiceInitEvent event) {
			event.getSource().addUIInitListener(e -> {
				LocalizationContext.getCurrent().ifPresent(lc -> lc.localize(e.getUI().getLocale(), false));
			});
		}

	}

}
