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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.internal.config.AbstractNavigationTargetConfigurationRegistryInitializer;
import com.holonplatform.vaadin.flow.navigator.internal.config.NavigationTargetConfigurationRegistry;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/**
 * Navigation target registry servlet context initializer for Spring Boot Application, to enable initialization also
 * when Java application is used to run Spring Boot.
 * 
 * @since 5.2.0
 */
public class NavigatorServletContextInitializer implements ServletContextInitializer {

	private static final Logger LOGGER = VaadinLogger.create();

	private class NavigationTargetConfigurationServletContextListener
			extends AbstractNavigationTargetConfigurationRegistryInitializer implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			final NavigationTargetConfigurationRegistry registry = getRegistry(sce.getServletContext());
			if (!registry.isInitialized()) {
				LOGGER.debug(
						() -> "[Spring Boot] Initializing the servlet context bound NavigationTargetConfigurationRegistry...");
				registry.initialize(findByAnnotation(getAutoScanPackages(), Route.class, RouteAlias.class)
						.filter(this::isNavigationTargetClass).collect(Collectors.toSet()));
				LOGGER.debug(
						() -> "[Spring Boot] Servlet context bound NavigationTargetConfigurationRegistry initialized.");
			}
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			// noop
		}

	}

	private final ApplicationContext applicationContext;

	public NavigatorServletContextInitializer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		NavigationTargetConfigurationRegistry registry = NavigationTargetConfigurationRegistry
				.servletContext(servletContext);
		if (!registry.isInitialized()) {
			// Postpone at the end of context initialization cycle
			servletContext.addListener(new NavigationTargetConfigurationServletContextListener());
		}
	}

	/**
	 * Find the bean classes which provides the given annotations, within the given package names set.
	 * @param packages The package names to scan
	 * @param annotations The required annotations
	 * @return The bean classes
	 */
	@SafeVarargs
	private final Stream<Class<?>> findByAnnotation(Collection<String> packages,
			Class<? extends Annotation>... annotations) {
		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.setResourceLoader(applicationContext);
		for (Class<? extends Annotation> annotation : annotations) {
			scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
		}
		return packages.stream().map(scanner::findCandidateComponents).flatMap(set -> set.stream())
				.map(this::getBeanClass);
	}

	/**
	 * Get the bean class of given bean definition.
	 * @param beanDefinition The bean definition
	 * @return The bean class
	 */
	private Class<?> getBeanClass(BeanDefinition beanDefinition) {
		AbstractBeanDefinition definition = (AbstractBeanDefinition) beanDefinition;
		Class<?> beanClass;
		if (definition.hasBeanClass()) {
			beanClass = definition.getBeanClass();
		} else {
			try {
				beanClass = definition.resolveBeanClass(applicationContext.getClassLoader());
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
		return beanClass;
	}

	/**
	 * Get the auto-scan packages.
	 * @return the auto-scan packages
	 */
	private List<String> getAutoScanPackages() {
		if (AutoConfigurationPackages.has(applicationContext)) {
			return AutoConfigurationPackages.get(applicationContext);
		}
		return Collections.emptyList();
	}

}
