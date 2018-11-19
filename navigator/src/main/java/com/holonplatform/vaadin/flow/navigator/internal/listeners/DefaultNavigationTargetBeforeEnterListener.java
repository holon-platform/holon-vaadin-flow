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
package com.holonplatform.vaadin.flow.navigator.internal.listeners;

import java.util.Optional;
import java.util.Set;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.vaadin.flow.VaadinHttpRequest;
import com.holonplatform.vaadin.flow.navigator.exceptions.ForbiddenNavigationException;
import com.holonplatform.vaadin.flow.navigator.exceptions.UnauthorizedNavigationException;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration;
import com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfigurationRegistry;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

/**
 * A {@link BeforeEnterListener} to check navigation target authentication and authorization.
 * 
 * @since 5.2.0
 */
public class DefaultNavigationTargetBeforeEnterListener extends AbstractNavigationTargetListener
		implements BeforeEnterListener {

	private static final long serialVersionUID = 4407342989579425922L;

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.router.internal.BeforeEnterHandler#beforeEnter(com.vaadin.flow.router.BeforeEnterEvent)
	 */
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		final Class<?> navigationTarget = event.getNavigationTarget();
		if (navigationTarget != null) {
			final NavigationTargetConfiguration configuration = NavigationTargetConfigurationRegistry
					.getNavigationTargetConfiguration(navigationTarget);
			// check authentication required
			if (configuration.isAuthenticationRequired()) {
				// get AuthContext
				final AuthContext authContext;
				try {
					authContext = AuthContext.require();
				} catch (IllegalStateException e) {
					event.rerouteToError(e, "Authentication/authorization check failed: no "
							+ AuthContext.class.getName() + " available as a context resource");
					return;
				}
				// check authentication
				final Authentication authentication = getAuthentication(configuration, authContext);
				if (authentication == null) {
					final String redirect = configuration.getAuthentication()
							.map(a -> AnnotationUtils.getStringValue(a.redirectURI())).orElse(null);
					if (redirect != null) {
						// redirect to path
						event.rerouteTo(redirect);
					} else {
						// redirect to error
						event.rerouteToError(UnauthorizedNavigationException.class,
								LocalizationContext
										.translate(Localizable.of(UnauthorizedNavigationException.DEFAULT_MESSAGE,
												UnauthorizedNavigationException.DEFAULT_MESSAGE_CODE), true));
					}
				} else {
					// check authorization
					final Set<String> roles = configuration.getAuthorization();
					if (!roles.isEmpty() && !authContext.isPermittedAny(roles.toArray(new String[roles.size()]))) {
						// redirect to error
						event.rerouteToError(ForbiddenNavigationException.class,
								LocalizationContext
										.translate(Localizable.of(ForbiddenNavigationException.DEFAULT_MESSAGE,
												ForbiddenNavigationException.DEFAULT_MESSAGE_CODE), true));
					}
				}
			}
		}
	}

	/**
	 * Get the current {@link Authentication} from given {@link AuthContext}, if available. The {@link Authenticate}
	 * annotation, if available, is used to obtain the authentication schemes allowed and perform authentication using
	 * the current request, if available.
	 * @param configuration Navigation target configuration
	 * @param authContext AuthContext
	 * @return The current {@link Authentication}, or <code>null</code> if none
	 */
	private static Authentication getAuthentication(NavigationTargetConfiguration configuration,
			AuthContext authContext) {
		// check authentication
		Optional<Authentication> authentication = authContext.getAuthentication();
		if (authentication.isPresent()) {
			return authentication.get();
		}
		// try to authenticate using current request
		final VaadinRequest request = VaadinService.getCurrentRequest();
		if (request != null) {
			try {
				LOGGER.debug(() -> "Try to perform authentication using current request");
				return authContext.authenticate(VaadinHttpRequest.create(request),
						configuration.getAuthentication().map(a -> a.schemes()).orElse(new String[0]));
			} catch (AuthenticationException e) {
				// failed, ignore
				LOGGER.debug(() -> "Authentication using current request failed", e);
				return null;
			}
		}
		return null;
	}

}
