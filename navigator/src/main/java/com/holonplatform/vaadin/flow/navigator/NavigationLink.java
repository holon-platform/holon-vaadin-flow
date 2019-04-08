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
package com.holonplatform.vaadin.flow.navigator;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator;
import com.holonplatform.vaadin.flow.navigator.internal.DefaultNavigationLink;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.router.Router;

/**
 * A link (using the default <code>A</code> tag) that handles navigation internally, without loading a new page in the
 * browser.
 * <p>
 * The {@link #getComponent()} method can be used to obtain the component to be added in the UI.
 * </p>
 * 
 * @since 5.2.0
 */
public interface NavigationLink extends HasComponent, HasText, HasStyle, HasEnabled, HasSize {

	/**
	 * Gets the href (the URL) of this link.
	 * @return the href value
	 */
	String getHref();

	// ------- builders

	/**
	 * Get a builder to create a {@link NavigationLink}.
	 * @param path The path to navigate to
	 * @return A new {@link NavigationLink} builder
	 */
	static Builder builder(String path) {
		return new DefaultNavigationLink.DefaultBuilder(path);
	}

	/**
	 * Get a builder to create a {@link NavigationLink}.
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @return A new {@link NavigationLink} builder
	 */
	static Builder builder(Class<? extends Component> navigationTarget) {
		return new DefaultNavigationLink.DefaultBuilder(navigationTarget);
	}

	/**
	 * Get a builder to create a {@link NavigationLink}.
	 * @param navigationTarget The navigation target to navigate to (not null)
	 * @param router The {@link Router} to use to resolve navigation target URLs
	 * @return A new {@link NavigationLink} builder
	 */
	static Builder builder(Class<? extends Component> navigationTarget, Router router) {
		return new DefaultNavigationLink.DefaultBuilder(navigationTarget, router);
	}

	/**
	 * {@link NavigationLink} builder.
	 */
	public interface Builder extends ComponentConfigurator<Builder>, HasTextConfigurator<Builder>,
			HasStyleConfigurator<Builder>, HasSizeConfigurator<Builder>, HasEnabledConfigurator<Builder>,
			NavigationURLBuilder<Builder>, DeferrableLocalizationConfigurator<Builder> {

		/**
		 * Build the {@link NavigationLink}.
		 * @return A new {@link NavigationLink} instance
		 */
		NavigationLink build();

	}

}
