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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHorizontalLayoutConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultVerticalLayoutConfigurator;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Configurator for {@link FlexComponent} and {@link ThemableLayout} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface ThemableFlexComponentConfigurator<C extends ThemableFlexComponentConfigurator<C>>
		extends FlexComponentConfigurator<C>, ThemableLayoutConfigurator<C>, ComponentConfigurator<C> {

	/**
	 * Get a new {@link VerticalLayoutConfigurator} for given component.
	 * @param component The component to configure (not null)
	 * @return A new {@link VerticalLayoutConfigurator}
	 */
	static VerticalLayoutConfigurator configure(VerticalLayout component) {
		return new DefaultVerticalLayoutConfigurator(component);
	}

	/**
	 * Get a new {@link HorizontalLayoutConfigurator} for given component.
	 * @param component The component to configure (not null)
	 * @return A new {@link HorizontalLayoutConfigurator}
	 */
	static HorizontalLayoutConfigurator configure(HorizontalLayout component) {
		return new DefaultHorizontalLayoutConfigurator(component);
	}

	public interface VerticalLayoutConfigurator extends ThemableFlexComponentConfigurator<VerticalLayoutConfigurator> {

	}

	public interface HorizontalLayoutConfigurator
			extends ThemableFlexComponentConfigurator<HorizontalLayoutConfigurator> {

	}

}
