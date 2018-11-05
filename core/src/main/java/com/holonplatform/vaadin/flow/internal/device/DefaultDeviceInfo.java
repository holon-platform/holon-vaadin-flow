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
package com.holonplatform.vaadin.flow.internal.device;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.device.DeviceInfo;
import com.holonplatform.vaadin.flow.device.UserAgentInspector;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.BrowserWindowResizeEvent;
import com.vaadin.flow.component.page.BrowserWindowResizeListener;
import com.vaadin.flow.component.page.Page;

/**
 * Default {@link DeviceInfo} implementation using Vaadin {@link Page} to obtain client viewport dimensions.
 *
 * @since 5.0.0
 */
public class DefaultDeviceInfo implements DeviceInfo, BrowserWindowResizeListener {

	private static final long serialVersionUID = -8874983833266879186L;

	private static final Logger LOGGER = VaadinLogger.create();

	private final UserAgentInspector userAgentInspector;

	private int windowWidth = -1;
	private int windowHeight = -1;

	/**
	 * Constructor
	 * @param userAgentInspector The User-Agent inspector to use (not null)
	 * @param ui Optional UI to which this instance refers
	 */
	public DefaultDeviceInfo(UserAgentInspector userAgentInspector, UI ui) {
		super();
		ObjectUtils.argumentNotNull(userAgentInspector, "UserAgentInspector must be not null");
		this.userAgentInspector = userAgentInspector;
		if (ui != null) {
			ui.getPage().addBrowserWindowResizeListener(this);
		} else {
			LOGGER.warn(
					"The UI reference is not available, the browser window width and height won't be detected and updated");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.flow.component.page.BrowserWindowResizeListener#browserWindowResized(com.vaadin.flow.component.page.
	 * BrowserWindowResizeEvent)
	 */
	@Override
	public void browserWindowResized(BrowserWindowResizeEvent event) {
		this.windowWidth = event.getWidth();
		this.windowHeight = event.getHeight();
	}

	/**
	 * User-Agent inspector
	 * @return the User-Agent inspector
	 */
	protected UserAgentInspector getUserAgentInspector() {
		return userAgentInspector;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#getUserAgentHeader()
	 */
	@Override
	public String getUserAgentHeader() {
		return getUserAgentInspector().getUserAgentHeader();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#getAcceptHeader()
	 */
	@Override
	public String getAcceptHeader() {
		return getUserAgentInspector().getAcceptHeader();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isWebKit()
	 */
	@Override
	public boolean isWebKit() {
		return getUserAgentInspector().isWebKit();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isIPhone()
	 */
	@Override
	public boolean isIPhone() {
		return getUserAgentInspector().isIPhone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isIPad()
	 */
	@Override
	public boolean isIPad() {
		return getUserAgentInspector().isIPad();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isIPod()
	 */
	@Override
	public boolean isIPod() {
		return getUserAgentInspector().isIPod();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isIOs()
	 */
	@Override
	public boolean isIOs() {
		return getUserAgentInspector().isIOs();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isAndroid()
	 */
	@Override
	public boolean isAndroid() {
		return getUserAgentInspector().isAndroid();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isAndroidPhone()
	 */
	@Override
	public boolean isAndroidPhone() {
		return getUserAgentInspector().isAndroidPhone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isAndroidTablet()
	 */
	@Override
	public boolean isAndroidTablet() {
		return getUserAgentInspector().isAndroidTablet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isWindowsPhone()
	 */
	@Override
	public boolean isWindowsPhone() {
		return getUserAgentInspector().isWindowsPhone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isKindle()
	 */
	@Override
	public boolean isKindle() {
		return getUserAgentInspector().isKindle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isPlaystation()
	 */
	@Override
	public boolean isPlaystation() {
		return getUserAgentInspector().isPlaystation();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isNintendo()
	 */
	@Override
	public boolean isNintendo() {
		return getUserAgentInspector().isNintendo();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isXbox()
	 */
	@Override
	public boolean isXbox() {
		return getUserAgentInspector().isXbox();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isGameConsole()
	 */
	@Override
	public boolean isGameConsole() {
		return getUserAgentInspector().isGameConsole();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isSmartphone()
	 */
	@Override
	public boolean isSmartphone() {
		return getUserAgentInspector().isSmartphone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isTablet()
	 */
	@Override
	public boolean isTablet() {
		return getUserAgentInspector().isTablet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.core.device.UserAgentInspector#isMobile()
	 */
	@Override
	public boolean isMobile() {
		return getUserAgentInspector().isMobile();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.device.DeviceInfo#getWindowWidth()
	 */
	@Override
	public int getWindowWidth() {
		return windowWidth;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.device.DeviceInfo#getWindowHeight()
	 */
	@Override
	public int getWindowHeight() {
		return windowHeight;
	}

}
