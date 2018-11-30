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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeEvent;
import com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeListener;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.internal.VaadinSessionScope;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

/**
 * A {@link BeanPostProcessor} to set the {@link LocalizationContext} Locale in current UI when the localization
 * changes.
 *
 * @since 5.2.0
 */
public class LocalizationContextPostProcessor implements BeanPostProcessor, BeanFactoryAware {

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
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LocalizationContext) {
			final boolean sessionScoped = isSessionScope(beanFactory, beanName);
			((LocalizationContext) bean).addLocalizationChangeListener(new LocalizationChangeReflector(sessionScoped));

			LOGGER.info(sessionScoped ? "Localization context Locale reflection to Session Locale activated"
					: "Localization context Locale reflection to UI Locale activated");
		}
		return bean;
	}

	static class LocalizationChangeReflector implements LocalizationChangeListener {

		private static final long serialVersionUID = -8299171206172261063L;

		private final boolean sessionScope;

		public LocalizationChangeReflector(boolean sessionScope) {
			super();
			this.sessionScope = sessionScope;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeListener#onLocalizationChange(com.
		 * holonplatform.core.i18n.LocalizationContext.LocalizationChangeEvent)
		 */
		@Override
		public void onLocalizationChange(LocalizationChangeEvent event) {
			if (sessionScope) {
				if (VaadinSession.getCurrent() != null) {
					event.getLocale().ifPresent(locale -> {
						VaadinSession.getCurrent().setLocale(locale);
						LOGGER.debug(() -> "Setted LocalizationContext locale [" + locale + "] to current session");
					});
				}
			} else {
				if (UI.getCurrent() != null) {
					event.getLocale().ifPresent(locale -> {
						UI.getCurrent().setLocale(locale);
						LOGGER.debug(() -> "Setted LocalizationContext locale [" + locale + "] to current UI");
					});
				}
			}
		}

	}

	/**
	 * Gets whether the bean with given name is session scoped.
	 * @param beanFactory The bean factory
	 * @param beanName The bean name
	 * @return <code>true</code> if session scoped
	 */
	private static boolean isSessionScope(BeanFactory beanFactory, String beanName) {
		return getBeanScope(beanFactory, beanName).filter(scope -> scope != null).map(
				scope -> (WebApplicationContext.SCOPE_SESSION.equals(scope) || VaadinSessionScope.NAME.equals(scope)))
				.orElse(false);
	}

	/**
	 * Get the scope of given bean name, if available.
	 * @param beanFactory The bean factory
	 * @param beanName The bean name
	 * @return Optional bean scope
	 */
	private static Optional<String> getBeanScope(BeanFactory beanFactory, String beanName) {
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
