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
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.exceptions.ForbiddenNavigationException;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.theme.NoTheme;

/**
 * Default navigator error handler for the {@link ForbiddenNavigationException} error.
 * <p>
 * This error handler will show a localizable header message composed by the {@link #DEFAULT_NAVIGATION_FAILED_MESSAGE}
 * followed by the failed navigation path, using the {@link #DEFAULT_NAVIGATION_FAILED_MESSAGE_CODE} as localization
 * message code.
 * </p>
 * <p>
 * The header is followed by the localizable message {@link ForbiddenNavigationException#DEFAULT_MESSAGE}, using the
 * {@link ForbiddenNavigationException#DEFAULT_MESSAGE_CODE} as localization message code.
 * </p>
 *
 * @since 5.2.0
 */
@NoTheme
public class ForbiddenNavigationError extends Div implements HasErrorParameter<ForbiddenNavigationException> {

	private static final long serialVersionUID = 3345073026233740221L;

	public ForbiddenNavigationError() {
		super();
		getElement().getClassList().add("forbidden-navigation-error");
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<ForbiddenNavigationException> parameter) {
		add(new H2(LocalizationContext
				.translate(Localizable.of(Navigator.DEFAULT_NAVIGATION_FAILED_MESSAGE,
						Navigator.DEFAULT_NAVIGATION_FAILED_MESSAGE_CODE), true)
				+ ": " + event.getLocation().getPath()));
		add(new H3(LocalizationContext.translate(Localizable.of(ForbiddenNavigationException.DEFAULT_MESSAGE,
				ForbiddenNavigationException.DEFAULT_MESSAGE_CODE), true)));
		return HttpStatus.FORBIDDEN.getCode();
	}

}
