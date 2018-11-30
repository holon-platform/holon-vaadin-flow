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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeEvent;
import com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeListener;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

/**
 * A {@link BeanPostProcessor} to set the {@link LocalizationContext} Locale in current UI when the localization
 * changes.
 *
 * @since 5.2.0
 */
public class LocalizationContextPostProcessor extends AbstractInitializer
		implements BeanPostProcessor, BeanFactoryAware {

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
			if (isSessionScope(beanFactory, beanName)) {
				((LocalizationContext) bean).addLocalizationChangeListener(new SessionLocalizationChangeReflector());
				LOGGER.info("Localization context Locale reflection to Session Locale activated");
			} else if (isUIScope(beanFactory, beanName)) {
				((LocalizationContext) bean).addLocalizationChangeListener(new UILocalizationChangeReflector());
				LOGGER.info("Localization context Locale reflection to UI Locale activated");
			}
		}
		return bean;
	}

	static class SessionLocalizationChangeReflector implements LocalizationChangeListener {

		private static final long serialVersionUID = 7336953791954450518L;

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeListener#onLocalizationChange(com.
		 * holonplatform.core.i18n.LocalizationContext.LocalizationChangeEvent)
		 */
		@Override
		public void onLocalizationChange(LocalizationChangeEvent event) {
			if (VaadinSession.getCurrent() != null) {
				event.getLocale().ifPresent(locale -> {
					VaadinSession.getCurrent().setLocale(locale);
					LOGGER.debug(() -> "Setted LocalizationContext locale [" + locale + "] to current session");
				});
			}
		}

	}

	static class UILocalizationChangeReflector implements LocalizationChangeListener {

		private static final long serialVersionUID = 6723782844437508012L;

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.i18n.LocalizationContext.LocalizationChangeListener#onLocalizationChange(com.
		 * holonplatform.core.i18n.LocalizationContext.LocalizationChangeEvent)
		 */
		@Override
		public void onLocalizationChange(LocalizationChangeEvent event) {
			if (UI.getCurrent() != null) {
				event.getLocale().ifPresent(locale -> {
					UI.getCurrent().setLocale(locale);
					LOGGER.debug(() -> "Setted LocalizationContext locale [" + locale + "] to current UI");
				});
			}
		}

	}

}
