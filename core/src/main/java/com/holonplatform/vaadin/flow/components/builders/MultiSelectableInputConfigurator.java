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

import java.util.Set;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.vaadin.flow.data.provider.ListDataProvider;

/**
 * {@link MultiSelect} type {@link Input} configurator.
 * 
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface MultiSelectableInputConfigurator<T, ITEM, C extends MultiSelectableInputConfigurator<T, ITEM, C>> extends
		ItemSetConfigurator<C>, SelectableInputConfigurator<Set<T>, T, C>, HasItemCaptionConfigurator<ITEM, C> {

	/**
	 * Set the items data provider using a {@link ListDataProvider}.
	 * @param dataProvider The data provider to set
	 * @return this
	 */
	C dataSource(ListDataProvider<ITEM> dataProvider);

}
