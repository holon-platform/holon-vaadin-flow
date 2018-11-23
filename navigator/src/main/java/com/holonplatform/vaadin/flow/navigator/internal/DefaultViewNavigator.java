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
package com.holonplatform.vaadin.flow.navigator.internal;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.navigator.ViewNavigator;
import com.vaadin.flow.router.Router;

/**
 * Default {@link ViewNavigator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultViewNavigator implements ViewNavigator {

	private static final long serialVersionUID = -5365345141554389835L;
	
	private final Router router;

	/**
	 * Constructor.
	 * @param router The {@link Router} to use (not null)
	 */
	public DefaultViewNavigator(Router router) {
		super();
		ObjectUtils.argumentNotNull(router, "Router must be not null");
		this.router = router;
	}

	/**
	 * Get the {@link Router} reference.
	 * @return the router
	 */
	protected Router getRouter() {
		return router;
	}

}
