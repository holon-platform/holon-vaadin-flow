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

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultContextMenuBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;

/**
 * {@link ContextMenu} component builder.
 *
 * @since 5.2.0
 */
public interface ContextMenuBuilder extends
		ContextMenuConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, ContextMenu, MenuItem, SubMenu, ContextMenuBuilder> {

	/**
	 * Build the {@link ContextMenu} and bind it to the given <code>target</code> component.
	 * <p>
	 * By default, the context menu can be opened with a right click or a long touch on the target component.
	 * </p>
	 * @param target The target component for this context menu
	 * @return The {@link ContextMenu} instance
	 */
	ContextMenu build(Component target);

	/**
	 * Build the {@link ContextMenu} and bind it to the given <code>target</code> component.
	 * <p>
	 * By default, the context menu can be opened with a right click or a long touch on the target component.
	 * </p>
	 * @param target The target component for this context menu
	 * @return The {@link ContextMenu} instance
	 */
	default ContextMenu build(HasComponent target) {
		return build((target != null) ? target.getComponent() : null);
	}

	/**
	 * Build the {@link ContextMenu}. The context menu will not be bound to any target component, the
	 * {@link ContextMenu#setTarget(Component)} method should be used later to configure the target component.
	 * @return The {@link ContextMenu} instance
	 */
	default ContextMenu build() {
		return build((Component) null);
	}

	// builders

	/**
	 * Create a new {@link ContextMenuBuilder} to create and configure {@link ContextMenu} component instances.
	 * @return A new {@link ContextMenuBuilder}
	 */
	static ContextMenuBuilder create() {
		return new DefaultContextMenuBuilder();
	}

}
