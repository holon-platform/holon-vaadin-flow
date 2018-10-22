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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;

/**
 * {@link HasPlaceholder} implementation using callback functions to get and set the placeholder text.
 *
 * @since 5.2.0
 */
public class CallbackHasPlaceholder implements HasPlaceholder {

	private final Supplier<String> getter;
	private final Consumer<String> setter;

	/**
	 * Constructor.
	 * @param getter placeholder text supplier (not null)
	 * @param setter placeholder text consumer (not null)
	 */
	public CallbackHasPlaceholder(Supplier<String> getter, Consumer<String> setter) {
		super();
		ObjectUtils.argumentNotNull(getter, "The label text supplier must be not null");
		ObjectUtils.argumentNotNull(setter, "The label text consumer must be not null");
		this.getter = getter;
		this.setter = setter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasPlaceholder#getPlaceholder()
	 */
	@Override
	public String getPlaceholder() {
		return getter.get();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasPlaceholder#setPlaceholder(java.lang.String)
	 */
	@Override
	public void setPlaceholder(String placeholder) {
		this.setter.accept(placeholder);
	}

}
