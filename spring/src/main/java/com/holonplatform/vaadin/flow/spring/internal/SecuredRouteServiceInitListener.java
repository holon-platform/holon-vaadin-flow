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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.exceptions.ForbiddenNavigationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * A {@link VaadinServiceInitListener} to register a {@link BeforeEnterListener} to check <code>Secured</code> annotated
 * navigation targets authorization.
 *
 * @since 5.2.0
 */
public class SecuredRouteServiceInitListener implements VaadinServiceInitListener {

	private static final long serialVersionUID = 6581058594162355308L;

	private static final Logger LOGGER = VaadinLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
	 */
	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(e -> {
			e.getUI().addBeforeEnterListener(new SecuredRouteBeforeEnterListener());
			LOGGER.debug(() -> "Secured route before enter listener setted up");
		});
	}

	static class SecuredRouteBeforeEnterListener implements BeforeEnterListener {

		private static final long serialVersionUID = 1577627862173954401L;

		/*
		 * (non-Javadoc)
		 * @see com.vaadin.flow.router.internal.BeforeEnterHandler#beforeEnter(com.vaadin.flow.router.BeforeEnterEvent)
		 */
		@Override
		public void beforeEnter(BeforeEnterEvent event) {
			final Class<?> navigationTarget = event.getNavigationTarget();
			if (navigationTarget != null) {
				String[] roles = AnnotationUtils.getAnnotation(navigationTarget, Secured.class)
						.map(s -> (s.value() != null) ? s.value() : new String[0]).orElse(null);
				if (roles != null) {
					if (!isAccessGranted(roles)) {
						// redirect to error
						event.rerouteToError(ForbiddenNavigationException.class,
								LocalizationProvider.localize(ForbiddenNavigationException.DEFAULT_MESSAGE,
										ForbiddenNavigationException.DEFAULT_MESSAGE_CODE));
					}
				}
			}
		}

		/**
		 * Checks if the current user is granted any explicitly provided security attributes.
		 * @param securityConfigAttributes list of security configuration attributes (e.g. ROLE_USER, ROLE_ADMIN).
		 * @return <code>true</code> if the access is granted or the view is not secured, <code>false</code> otherwise
		 */
		private static boolean isAccessGranted(String[] securityConfigAttributes) {
			SecurityContext context = SecurityContextHolder.getContext();
			Authentication authentication = context.getAuthentication();
			if (authentication == null) {
				return false;
			}
			Set<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toSet());
			return Stream.of(securityConfigAttributes).anyMatch(authorities::contains);
		}

	}

}
