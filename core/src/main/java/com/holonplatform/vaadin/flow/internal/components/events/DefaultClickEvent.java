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
package com.holonplatform.vaadin.flow.internal.components.events;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;

/**
 * Default {@link ClickEvent} implementation.
 * 
 * @param <S> Source type
 *
 * @since 5.2.0
 */
public class DefaultClickEvent<S> implements ClickEvent<S> {

	private static final long serialVersionUID = -5356947468787075289L;

	private final S source;
	private final boolean fromClient;

	private int clientX = -1;
	private int clientY = -1;
	private int screenX = -1;
	private int screenY = -1;
	private int clickCount = 1;
	private int button = -1;
	private boolean ctrlKey;
	private boolean altKey;
	private boolean metaKey;
	private boolean shiftKey;

	/**
	 * Constructor.
	 * @param source Event source (not null)
	 * @param fromClient Whether the event was originated from client
	 */
	public DefaultClickEvent(S source, boolean fromClient) {
		super();
		ObjectUtils.argumentNotNull(source, "Event source must be not null");
		this.source = source;
		this.fromClient = fromClient;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getSource()
	 */
	@Override
	public S getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#isFromClient()
	 */
	@Override
	public boolean isFromClient() {
		return fromClient;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getClientX()
	 */
	@Override
	public int getClientX() {
		return clientX;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getClientY()
	 */
	@Override
	public int getClientY() {
		return clientY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getScreenX()
	 */
	@Override
	public int getScreenX() {
		return screenX;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getScreenY()
	 */
	@Override
	public int getScreenY() {
		return screenY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getClickCount()
	 */
	@Override
	public int getClickCount() {
		return clickCount;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#getButton()
	 */
	@Override
	public int getButton() {
		return button;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#isCtrlKey()
	 */
	@Override
	public boolean isCtrlKey() {
		return ctrlKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#isAltKey()
	 */
	@Override
	public boolean isAltKey() {
		return altKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#isMetaKey()
	 */
	@Override
	public boolean isMetaKey() {
		return metaKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.events.ClickEvent#isShiftKey()
	 */
	@Override
	public boolean isShiftKey() {
		return shiftKey;
	}

	/**
	 * Sets the x coordinate of the click event, relative to the upper left corner of the browser viewport.
	 * @param clientX the x coordinate, -1 if unknown
	 */
	public void setClientX(int clientX) {
		this.clientX = clientX;
	}

	/**
	 * Sets the y coordinate of the click event, relative to the upper left corner of the browser viewport.
	 * @param clientY the y coordinate, -1 if unknown
	 */
	public void setClientY(int clientY) {
		this.clientY = clientY;
	}

	/**
	 * Sets the x coordinate of the click event, relative to the upper left corner of the screen.
	 * @param screenX the x coordinate, -1 if unknown
	 */
	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	/**
	 * Sets the y coordinate of the click event, relative to the upper left corner of the screen.
	 * @param screenY the y coordinate, -1 if unknown
	 */
	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}

	/**
	 * Sets the number of consecutive clicks recently recorded.
	 * @param clickCount the click count to set
	 */
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	/**
	 * Sets the id of the pressed mouse button.
	 * <ul>
	 * <li>-1: No button
	 * <li>0: The primary button, typically the left mouse button
	 * <li>1: The middle button,
	 * <li>2: The secondary button, typically the right mouse button
	 * <li>3: The first additional button, typically the back button
	 * <li>4: The second additional button, typically the forward button
	 * <li>5+: More additional buttons without any typical meanings
	 * </ul>
	 * @param button the button to set
	 */
	public void setButton(int button) {
		this.button = button;
	}

	/**
	 * Sets whether the ctrl key was was down when the event was fired.
	 * @param ctrlKey <code>true</code> if was down, <code>false</code> otherwise
	 */
	public void setCtrlKey(boolean ctrlKey) {
		this.ctrlKey = ctrlKey;
	}

	/**
	 * Sets whether the alt key was was down when the event was fired.
	 * @param altKey <code>true</code> if was down, <code>false</code> otherwise
	 */
	public void setAltKey(boolean altKey) {
		this.altKey = altKey;
	}

	/**
	 * Sets whether the meta key was was down when the event was fired.
	 * @param metaKey <code>true</code> if was down, <code>false</code> otherwise
	 */
	public void setMetaKey(boolean metaKey) {
		this.metaKey = metaKey;
	}

	/**
	 * Sets whether the shift key was was down when the event was fired.
	 * @param shiftKey <code>true</code> if was down, <code>false</code> otherwise
	 */
	public void setShiftKey(boolean shiftKey) {
		this.shiftKey = shiftKey;
	}

}
