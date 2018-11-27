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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;

/**
 * Interface to configure an {@link Input} with value configuration support.
 * 
 * @param <T> Value type
 * @param <E> Value change event type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface InputValueConfigurator<T, E extends ValueChangeEvent<T>, C extends InputValueConfigurator<T, E, C>>
		extends InputConfigurator<T, E, C> {

	/**
	 * Sets the initial value.
	 * @param value The value to set
	 * @return this
	 */
	C withValue(T value);

}
