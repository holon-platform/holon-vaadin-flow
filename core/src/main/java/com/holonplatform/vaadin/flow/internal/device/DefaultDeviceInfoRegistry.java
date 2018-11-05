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

import java.util.Optional;
import java.util.WeakHashMap;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.device.DeviceInfo;
import com.holonplatform.vaadin.flow.device.DeviceInfoRegistry;
import com.vaadin.flow.component.UI;

/**
 * Default {@link DeviceInfoRegistry} implementation.
 * 
 * @since 5.2.0
 */
public class DefaultDeviceInfoRegistry implements DeviceInfoRegistry {

	private static final long serialVersionUID = -3886097061085664626L;

	private final WeakHashMap<UI, DeviceInfo> deviceInfos = new WeakHashMap<>(4);

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.device.DeviceInfoRegistry#hasDeviceInfo(com.vaadin.flow.component.UI)
	 */
	@Override
	public boolean hasDeviceInfo(UI ui) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		return deviceInfos.containsKey(ui);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.device.DeviceInfoRegistry#getDeviceInfo(com.vaadin.flow.component.UI)
	 */
	@Override
	public Optional<DeviceInfo> getDeviceInfo(UI ui) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		return Optional.ofNullable(deviceInfos.get(ui));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.device.DeviceInfoRegistry#setDeviceInfo(com.vaadin.flow.component.UI,
	 * com.holonplatform.vaadin.flow.device.DeviceInfo)
	 */
	@Override
	public void setDeviceInfo(UI ui, DeviceInfo deviceInfo) {
		ObjectUtils.argumentNotNull(ui, "UI must be not null");
		deviceInfos.put(ui, deviceInfo);
	}

}
