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
package com.holonplatform.vaadin.flow.navigator.errors;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.exceptions.UnauthorizedNavigationException;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.theme.NoTheme;

/**
 * Default navigator error handler for the {@link UnauthorizedNavigationError} error.
 * <p>
 * This error handler will show a localizable header message composed by the
 * {@link Navigator#DEFAULT_NAVIGATION_FAILED_MESSAGE} followed by the failed navigation path, using the
 * {@link Navigator#DEFAULT_NAVIGATION_FAILED_MESSAGE_CODE} as localization message code.
 * </p>
 * <p>
 * The header is followed by the localizable message {@link UnauthorizedNavigationException#DEFAULT_MESSAGE}, using the
 * {@link UnauthorizedNavigationException#DEFAULT_MESSAGE_CODE} as localization message code.
 * </p>
 *
 * @since 5.2.0
 */
@NoTheme
public class UnauthorizedNavigationError extends Div implements HasErrorParameter<UnauthorizedNavigationException> {

	private static final long serialVersionUID = -7685178616285431879L;

	public UnauthorizedNavigationError() {
		super();
		getElement().getClassList().add("unauthorized-navigation-error");
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<UnauthorizedNavigationException> parameter) {
		add(new H2(LocalizationProvider
				.localize(Localizable.of(Navigator.DEFAULT_NAVIGATION_FAILED_MESSAGE,
						Navigator.DEFAULT_NAVIGATION_FAILED_MESSAGE_CODE))
				.orElse(Navigator.DEFAULT_NAVIGATION_FAILED_MESSAGE) + ": " + event.getLocation().getPath()));
		add(new H3(LocalizationProvider
				.localize(Localizable.of(UnauthorizedNavigationException.DEFAULT_MESSAGE,
						UnauthorizedNavigationException.DEFAULT_MESSAGE_CODE))
				.orElse(UnauthorizedNavigationException.DEFAULT_MESSAGE)));
		return HttpStatus.UNAUTHORIZED.getCode();
	}

}
