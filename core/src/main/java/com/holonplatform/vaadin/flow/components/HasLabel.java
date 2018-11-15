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

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.holonplatform.vaadin.flow.internal.components.CallbackHasLabel;

/**
 * Represents a component which supports a label.
 *
 * @since 5.2.0
 */
public interface HasLabel {

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

	/**
	 * Create a new {@link HasLabel} using given callback functions to get and set the label text.
	 * @param getter label text getter (not null)
	 * @param setter label text setter (not null)
	 * @return A new {@link HasLabel} using given callback functions
	 */
	static HasLabel create(Supplier<String> getter, Consumer<String> setter) {
		return new CallbackHasLabel(getter, setter);
	}

}
