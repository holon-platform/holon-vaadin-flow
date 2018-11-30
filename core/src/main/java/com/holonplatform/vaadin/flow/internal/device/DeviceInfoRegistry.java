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
package com.holonplatform.vaadin.flow.internal.device;

import java.io.Serializable;
import java.util.Optional;

import com.holonplatform.vaadin.flow.device.DeviceInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

/**
 * A {@link DeviceInfo} registry to track the {@link DeviceInfo} instances associated to each {@link UI} instance.
 *
 * @since 5.2.0
 */
public interface DeviceInfoRegistry extends Serializable {

	/**
	 * Checks if a {@link DeviceInfo} for given UI is available.
	 * @param ui The UI (not null)
	 * @return <code>true</code> if a {@link DeviceInfo} for given UI is available, <code>false</code> otherwise
	 */
	boolean hasDeviceInfo(UI ui);

	/**
	 * Get the {@link DeviceInfo} for given UI, if available.
	 * @param ui The UI (not null)
	 * @return Optional {@link DeviceInfo} for given UI
	 */
	Optional<DeviceInfo> getDeviceInfo(UI ui);

	/**
	 * Set the {@link DeviceInfo} associated to given UI.
	 * @param ui The UI (not null)
	 * @param deviceInfo The {@link DeviceInfo} for given UI
	 */
	void setDeviceInfo(UI ui, DeviceInfo deviceInfo);

	/**
	 * Get the {@link DeviceInfoRegistry} associated with current session, if available.
	 * @return Optional session registry
	 */
	static Optional<DeviceInfoRegistry> getSessionRegistry() {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			return Optional.ofNullable(session.getAttribute(DeviceInfoRegistry.class));
		}
		return Optional.empty();
	}

}
