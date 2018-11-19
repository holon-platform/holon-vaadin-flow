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

import java.lang.reflect.Method;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.exceptions.NavigationTargetConfigurationException;

/**
 * Base class for navigation event listeners.
 * 
 * @since 5.2.0
 */
public abstract class AbstractNavigationTargetListener {

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = VaadinLogger.create();

	/**
	 * Invoke given <code>method</code> on given <code>instance</code>, checking if the method signature has a single
	 * parameter to provide given <code>event</code> value.
	 * @param instance The navigation target instance
	 * @param method The method to invoke
	 * @param event The optional event parameter
	 */
	protected void invokeMethod(Object instance, Method method, Object event) {
		try {
			if (method.getParameterCount() == 0) {
				method.invoke(instance, new Object[0]);
			} else {
				method.invoke(instance, new Object[] { event });
			}
		} catch (Exception e) {
			LOGGER.error("Failed to invoke method [" + method.getName() + "] on navigation target class ["
					+ instance.getClass().getName() + "]", e);
			// TODO check side effects
			throw new NavigationTargetConfigurationException("Failed to invoke method [" + method.getName()
					+ "] on navigation target class [" + instance.getClass().getName() + "]", e);
		}
	}

}
