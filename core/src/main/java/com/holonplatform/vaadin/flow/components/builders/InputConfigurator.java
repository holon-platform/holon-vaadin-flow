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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;

/**
 * Interface to configure an {@link Input}.
 * 
 * @param <T> Value type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface InputConfigurator<T, C extends InputConfigurator<T, C>> extends ComponentConfigurator<C> {

	/**
	 * Set whether the input component is read-only.
	 * <p>
	 * When the input component is read-only, the user can not change the value of the input.
	 * </p>
	 * @return this
	 */
	C readOnly(boolean readOnly);

	/**
	 * Set the input component as read-only. The user can not change the value of the input.
	 * @return this
	 */
	default C readOnly() {
		return readOnly(true);
	}

	/**
	 * Add a {@link ValueChangeListener} to be notified when the input value changes.
	 * @param listener The {@link ValueChangeListener} to add (not null)
	 * @return this
	 */
	C withValueChangeListener(ValueChangeListener<T> listener);

	/**
	 * Sets the <em>required indicator</em> visible or not.
	 * @param requiredIndicatorVisible Whether to make the required indicator visible
	 * @return this
	 * @deprecated Use {@link #required(boolean)}
	 */
	@Deprecated
	default C requiredIndicatorVisible(boolean requiredIndicatorVisible) {
		return required(requiredIndicatorVisible);
	}

	/**
	 * Set the input as <em>required</em> or not. When the input is required the user must fill in a value.
	 * <p>
	 * Depending on the actual input component implementation, setting the input as required may involve:
	 * <ul>
	 * <li>Showing a <em>required indicator</em> symbol, if supported. Normally a label must be configured to show the
	 * required indicator symbol.</li>
	 * <li>Setting a <code>required</code> attribute in the <code>input</code> element at client side, if
	 * supported.</li>
	 * </ul>
	 * @param required Whether the input is required
	 * @return this
	 */
	C required(boolean required);

	/**
	 * Set the input as <em>required</em>, i.e. the user must fill in a value.
	 * <p>
	 * Depending on the actual input component implementation, setting the input as required may involve:
	 * <ul>
	 * <li>Showing a <em>required indicator</em> symbol, if supported. Normally a label must be configured to show the
	 * required indicator symbol.</li>
	 * <li>Setting a <code>required</code> attribute in the <code>input</code> element at client side, if
	 * supported.</li>
	 * </ul>
	 * @return this
	 */
	default C required() {
		return required(true);
	}

	/**
	 * Base {@link InputConfigurator}.
	 * 
	 * @param <T> Value type
	 */
	public interface BaseInputConfigurator<T> extends InputConfigurator<T, BaseInputConfigurator<T>> {

	}

}
