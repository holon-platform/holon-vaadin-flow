/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.device;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.device.DefaultDeviceInfo;
import com.holonplatform.vaadin.flow.internal.device.DeviceInfoRegistry;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

/**
 * Provides informations about the client device in which application is running.
 * 
 * @since 5.2.0
 * 
 * @deprecated Use Vaadin {@link WebBrowser} instead
 * @see VaadinSession#getBrowser()
 */
@Deprecated
public interface DeviceInfo extends UserAgentInspector {

	/**
	 * Name of the session attribute to cache DeviceInfo instance
	 */
	public static final String SESSION_ATTRIBUTE_NAME = DeviceInfo.class.getName();

	/**
	 * Get the browser window width.
	 * @return the browser window width in pixels, or <code>-1</code> if not available at method call time
	 */
	int getWindowWidth();

	/**
	 * Get the browser window height.
	 * @return the browser window height in pixels, or <code>-1</code> if not available at method call time
	 */
	int getWindowHeight();

	// Providers

	/**
	 * Get the {@link DeviceInfo} for given UI, if available.
	 * @param ui The UI for which to obtain the {@link DeviceInfo} (not null)
	 * @return Optional {@link DeviceInfo} for given UI
	 */
	static Optional<DeviceInfo> get(UI ui) {
		return DeviceInfoRegistry.getSessionRegistry().flatMap(registry -> registry.getDeviceInfo(ui));
	}

	/**
	 * Get the {@link DeviceInfo} for the current UI, if available.
	 * @return Optional {@link DeviceInfo} for the current UI
	 */
	static Optional<DeviceInfo> get() {
		final UI ui = UI.getCurrent();
		if (ui != null) {
			return get(ui);
		}
		return Optional.empty();
	}

	/**
	 * Get the {@link DeviceInfo} for the current UI, throwing a {@link IllegalStateException} if not available.
	 * @return The {@link DeviceInfo} for the current UI
	 * @throws IllegalStateException If a DeviceInfo for current UI is not available
	 */
	static DeviceInfo require() {
		return get().orElseThrow(() -> new IllegalStateException("A DeviceInfo for current UI is not available"));
	}

	// Builders

	/**
	 * Create a new {@link DeviceInfo} for given UI, using the provided {@link UserAgentInspector}.
	 * @param ui The UI
	 * @param userAgentInspector The {@link UserAgentInspector} to use (not null)
	 * @return A new {@link DeviceInfo}
	 */
	static DeviceInfo create(UI ui, UserAgentInspector userAgentInspector) {
		return new DefaultDeviceInfo(userAgentInspector, ui);
	}

	/**
	 * Create a new {@link DeviceInfo} for given UI, using the provided {@link VaadinRequest} to configure a default
	 * {@link UserAgentInspector}.
	 * @param ui The UI
	 * @param request The request (not null)
	 * @return A new {@link DeviceInfo}
	 */
	static DeviceInfo create(UI ui, VaadinRequest request) {
		ObjectUtils.argumentNotNull(request, "Request must be not null");
		return new DefaultDeviceInfo(UserAgentInspector.create(request), ui);
	}

}
