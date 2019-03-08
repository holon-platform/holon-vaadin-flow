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
package com.holonplatform.vaadin.flow.navigator.internal;

import java.util.function.Consumer;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasStyleConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHasTextConfigurator;
import com.holonplatform.vaadin.flow.navigator.NavigationLink;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;

/**
 * Default {@link NavigationLink} implementation.
 *
 * @since 5.2.0
 */
public class DefaultNavigationLink extends RouterLink implements NavigationLink {

	private static final long serialVersionUID = 4803506635049908107L;

	private static final PropertyDescriptor<String, String> HREF = PropertyDescriptors.attributeWithDefault("href", "",
			false);

	/**
	 * Constructor.
	 */
	public DefaultNavigationLink() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Set the HREF attribute (URL) value.
	 * @param url The URL to set
	 */
	protected void setHref(String url) {
		HREF.set(this, url);
	}

	/**
	 * Default {@link Builder} implementation.
	 */
	public static class DefaultBuilder extends AbstractNavigationURLBuilder<Builder> implements Builder {

		private static final long serialVersionUID = -5401663411127131171L;

		private final DefaultNavigationLink instance;

		private final DefaultComponentConfigurator componentConfigurator;
		private final DefaultHasTextConfigurator textConfigurator;
		private final DefaultHasStyleConfigurator styleConfigurator;

		private boolean deferredLocalizationEnabled;

		/**
		 * Constructor with navigation target.
		 * @param navigationTarget The navigation target (not null).
		 */
		public DefaultBuilder(Class<? extends Component> navigationTarget) {
			this(navigationTarget, null);
		}

		/**
		 * Constructor with navigation target.
		 * @param navigationTarget The navigation target (not null).
		 * @param router The {@link Router} to use to resolve navigation target URLs (may be null)
		 */
		public DefaultBuilder(Class<? extends Component> navigationTarget, Router router) {
			this("");
			ObjectUtils.argumentNotNull(navigationTarget, "The navigation target class must be not null");
			final Router r = (router != null) ? router : getRouter();
			final String route = RouteConfiguration.forRegistry(r.getRegistry()).getUrlBase(navigationTarget)
					.orElseThrow(() -> new IllegalArgumentException(
							"No route registered for target [" + navigationTarget.getName() + "]"));
			path.append(route);
		}

		/**
		 * Constructor with path.
		 * @param path The navigation path.
		 */
		public DefaultBuilder(String path) {
			super(path);
			this.instance = new DefaultNavigationLink();

			this.componentConfigurator = new DefaultComponentConfigurator(instance);
			this.textConfigurator = new DefaultHasTextConfigurator(instance, this);
			this.styleConfigurator = new DefaultHasStyleConfigurator(instance);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.navigator.internal.AbstractNavigationURLBuilder#getBuilder()
		 */
		@Override
		protected Builder getBuilder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization#isDeferredLocalizationEnabled()
		 */
		@Override
		public boolean isDeferredLocalizationEnabled() {
			return deferredLocalizationEnabled;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator#withDeferredLocalization
		 * (boolean)
		 */
		@Override
		public Builder withDeferredLocalization(boolean deferredLocalization) {
			this.deferredLocalizationEnabled = deferredLocalization;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
		 */
		@Override
		public Builder id(String id) {
			componentConfigurator.id(id);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
		 */
		@Override
		public Builder visible(boolean visible) {
			componentConfigurator.visible(visible);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#elementConfiguration(java.util.
		 * function.Consumer)
		 */
		@Override
		public Builder elementConfiguration(Consumer<Element> element) {
			componentConfigurator.elementConfiguration(element);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public Builder withAttachListener(ComponentEventListener<AttachEvent> listener) {
			componentConfigurator.withAttachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
		 * component.ComponentEventListener)
		 */
		@Override
		public Builder withDetachListener(ComponentEventListener<DetachEvent> listener) {
			componentConfigurator.withDetachListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
		 */
		@Override
		public Builder withThemeName(String themeName) {
			componentConfigurator.withThemeName(themeName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener)
		 */
		@Override
		public Builder withEventListener(String eventType, DomEventListener listener) {
			componentConfigurator.withEventListener(eventType, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
		 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
		 */
		@Override
		public Builder withEventListener(String eventType, DomEventListener listener, String filter) {
			componentConfigurator.withEventListener(eventType, listener, filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(com.holonplatform.core.i18n.
		 * Localizable)
		 */
		@Override
		public Builder text(Localizable text) {
			textConfigurator.text(text);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
		 */
		@Override
		public Builder styleNames(String... styleNames) {
			styleConfigurator.styleNames(styleNames);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
		 */
		@Override
		public Builder styleName(String styleName) {
			styleConfigurator.styleName(styleName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.navigator.NavigationLink.Builder#build()
		 */
		@Override
		public NavigationLink build() {
			instance.setHref(getLocation().getPathWithQueryParameters());
			return instance;
		}

		/**
		 * Get the {@link Router} to use.
		 * @return the {@link Router}
		 * @throws IllegalStateException If a {@link Router} is not available.
		 */
		private static Router getRouter() {
			Router router = VaadinService.getCurrent().getRouter();
			if (router == null && UI.getCurrent() != null) {
				return UI.getCurrent().getRouter();
			}
			if (router == null) {
				throw new IllegalStateException(
						"A Router instance is not available neither from VaadinService nor from current UI");
			}
			return router;
		}

	}

}
