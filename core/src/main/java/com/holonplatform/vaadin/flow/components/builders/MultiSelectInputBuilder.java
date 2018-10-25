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

import com.holonplatform.vaadin.flow.components.MultiSelect;

/**
 * {@link MultiSelect} inputs builder.
 * 
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <CONTEXT> Item conversion context type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface MultiSelectInputBuilder<T, ITEM, CONTEXT, B extends MultiSelectInputBuilder<T, ITEM, CONTEXT, B>>
		extends InputBuilder<Set<T>, MultiSelect<T>, B>,
		SelectableDataSourceInputConfigurator<Set<T>, ITEM, CONTEXT, B> {

}
