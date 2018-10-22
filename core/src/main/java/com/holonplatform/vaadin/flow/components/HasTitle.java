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

import com.holonplatform.vaadin.flow.internal.components.CallbackHasTitle;

/**
 * Represents a component which supports a <em>title</em>, i.e. a description of the UI element.
 *
 * @since 5.2.0
 */
public interface HasTitle {

	/**
	 * Get the component title.
	 * @return The title text (may be null)
	 */
	String getTitle();

	/**
	 * Set the component title.
	 * @param title The title text to set
	 */
	void setTitle(String title);

	/**
	 * Create a new {@link HasTitle} using given callback functions to get and set the title text.
	 * @param getter title text getter (not null)
	 * @param setter title text setter (not null)
	 */
	static HasTitle create(Supplier<String> getter, Consumer<String> setter) {
		return new CallbackHasTitle(getter, setter);
	}

}
