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

import java.util.ServiceLoader;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.vaadin.flow.device.DeviceInfo;
import com.holonplatform.vaadin.flow.device.DeviceInfoRegistry;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * A {@link VaadinServiceInitListener} to configure the session {@link DeviceInfo} object.
 * <p>
 * The listener is automatically registered using {@link ServiceLoader}.
 * </p>
 *
 * @since 5.2.0
 */
public class DeviceInfoServiceInitListener implements VaadinServiceInitListener {

	private static final long serialVersionUID = -5461929742733298279L;

	private static final Logger LOGGER = VaadinLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
	 */
	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.addBootstrapListener(e -> {
			// check DeviceInfo registry is available
			if (e.getSession().getAttribute(DeviceInfoRegistry.class) == null) {
				e.getSession().setAttribute(DeviceInfoRegistry.class, new DefaultDeviceInfoRegistry());
			}
			// configure DeviceInfo
			final DeviceInfoRegistry registry = e.getSession().getAttribute(DeviceInfoRegistry.class);
			if (!registry.hasDeviceInfo(e.getUI())) {
				registry.setDeviceInfo(e.getUI(), DeviceInfo.create(e.getUI(), e.getRequest()));
				// log
				LOGGER.info("A DeviceInfo was configured for UI with id [" + e.getUI().getUIId() + "]");
			}
		});
	}

}
