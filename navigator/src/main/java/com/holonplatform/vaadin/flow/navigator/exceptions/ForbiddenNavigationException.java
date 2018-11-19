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
package com.holonplatform.vaadin.flow.navigator.exceptions;

/**
 * Exception used to notify a forbidden navigation target error, when the user does not have the required authorizations
 * to access a navigation target.
 *
 * @since 5.2.0
 */
public class ForbiddenNavigationException extends RuntimeException {

	private static final long serialVersionUID = 7543955538077396665L;

	/**
	 * Default forbidden navigation error message
	 */
	public static final String DEFAULT_MESSAGE = "Forbidden navigation: the user does not have the required authorizations";

	/**
	 * Default forbidden navigation error message localization code
	 */
	public static final String DEFAULT_MESSAGE_CODE = "com.holonplatform.vaadin.flow.navigator.error.forbidden";

	public ForbiddenNavigationException() {
		super();
	}

}
