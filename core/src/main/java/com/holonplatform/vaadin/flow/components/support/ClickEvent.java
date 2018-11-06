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
package com.holonplatform.vaadin.flow.components.support;

import java.io.Serializable;

/**
 * Event fired when a component is clicked.
 *
 * @since 5.2.0
 */
public interface ClickEvent extends Serializable {

	/**
	 * Gets the x coordinate of the click event, relative to the upper left corner of the browser viewport.
	 * @return the x coordinate, -1 if unknown
	 */
	int getClientX();

	/**
	 * Gets the y coordinate of the click event, relative to the upper left corner of the browser viewport.
	 * @return the y coordinate, -1 if unknown
	 */
	int getClientY();

	/**
	 * Gets the x coordinate of the click event, relative to the upper left corner of the screen.
	 * @return the x coordinate, -1 if unknown
	 */
	int getScreenX();

	/**
	 * Gets the y coordinate of the click event, relative to the upper left corner of the screen.
	 * @return the y coordinate, -1 if unknown
	 */
	int getScreenY();

	/**
	 * Gets the number of consecutive clicks recently recorded.
	 * @return the click count
	 */
	int getClickCount();

	/**
	 * Gets the id of the pressed mouse button.
	 * <ul>
	 * <li>-1: No button
	 * <li>0: The primary button, typically the left mouse button
	 * <li>1: The middle button,
	 * <li>2: The secondary button, typically the right mouse button
	 * <li>3: The first additional button, typically the back button
	 * <li>4: The second additional button, typically the forward button
	 * <li>5+: More additional buttons without any typical meanings
	 * </ul>
	 * @return the button id, or -1 if no button was pressed
	 */
	int getButton();

	/**
	 * Checks whether the ctrl key was was down when the event was fired.
	 * @return <code>true</code> if the ctrl key was down when the event was fired, <code>false</code> otherwise
	 */
	boolean isCtrlKey();

	/**
	 * Checks whether the alt key was was down when the event was fired.
	 * @return <code>true</code> if the alt key was down when the event was fired, <code>false</code> otherwise
	 */
	boolean isAltKey();

	/**
	 * Checks whether the meta key was was down when the event was fired.
	 * @return <code>true</code> if the meta key was down when the event was fired, <code>false</code> otherwise
	 */
	boolean isMetaKey();

	/**
	 * Checks whether the shift key was was down when the event was fired.
	 * @return <code>true</code> if the shift key was down when the event was fired, <code>false</code> otherwise
	 */
	boolean isShiftKey();

}
