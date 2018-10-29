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

import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;

/**
 * Configurator for {@link Selectable} input components.
 * 
 * @param <T> Value type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface SelectableInputConfigurator<T, C extends SelectableInputConfigurator<T, C>>
		extends InputConfigurator<T, C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C>,
		DeferrableLocalizationConfigurator<C> {

	/**
	 * Adds a {@link SelectionListener} to listen to selection changes.
	 * @param selectionListener The listener to add (not null)
	 * @return this
	 */
	C withSelectionListener(SelectionListener<T> selectionListener);

}
