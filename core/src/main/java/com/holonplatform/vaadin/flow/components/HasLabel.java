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
package com.holonplatform.vaadin.flow.components;

import com.vaadin.flow.component.HasElement;

/**
 * Represents a component which supports a label.
 *
 * @since 5.2.0
 */
public interface HasLabel extends HasElement {

	/**
	 * Get the component label.
	 * @return The label text (may be null)
	 */
	String getLabel();

	/**
	 * Set the component label.
	 * @param label The label text to set
	 */
	void setLabel(String label);

}
