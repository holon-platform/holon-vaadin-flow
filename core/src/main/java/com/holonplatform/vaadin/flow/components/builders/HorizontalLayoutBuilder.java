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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHorizontalLayoutBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Builder to create {@link HorizontalLayout} components.
 * 
 * @since 5.2.0
 */
public interface HorizontalLayoutBuilder extends ThemableFlexComponentConfigurator<HorizontalLayoutBuilder>,
		ComponentBuilder<HorizontalLayout, HorizontalLayoutBuilder> {

	/**
	 * Create a new {@link HorizontalLayoutBuilder} to build a {@link HorizontalLayout} component.
	 * @return A new {@link HorizontalLayoutBuilder}
	 */
	static HorizontalLayoutBuilder create() {
		return new DefaultHorizontalLayoutBuilder();
	}

}
