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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.Optional;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.dom.DomEventListener;

/**
 * Base {@link ComponentConfigurator}.
 * 
 * @param <C> Concrete component type
 * @param <B> Concrete configurator type
 */
public abstract class AbstractComponentConfigurator<C extends Component, B extends ComponentConfigurator<B>>
		implements ComponentConfigurator<B> {

	protected static final Logger LOGGER = VaadinLogger.create();

	private final C component;

	private final DefaultComponentConfigurator componentConfigurator;

	private HasSizeConfigurator<?> sizeConfigurator;
	private HasStyleConfigurator<?> styleConfigurator;
	private HasEnabledConfigurator<?> enabledConfigurator;

	/**
	 * Constructor.
	 * @param component The component instance (not null)
	 */
	public AbstractComponentConfigurator(C component) {
		super();
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		this.component = component;
		this.componentConfigurator = new DefaultComponentConfigurator(component);
	}

	/**
	 * If the component supports {@link HasSize}, return the component as {@link HasSize}.
	 * @return Optional component as {@link HasSize}, if supported
	 */
	protected abstract Optional<HasSize> hasSize();

	/**
	 * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
	 * @return Optional component as {@link HasStyle}, if supported
	 */
	protected abstract Optional<HasStyle> hasStyle();

	/**
	 * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
	 * @return Optional component as {@link HasEnabled}, if supported
	 */
	protected abstract Optional<HasEnabled> hasEnabled();

	/**
	 * Get the actual configurator.
	 * @return the actual configurator
	 */
	protected abstract B getConfigurator();

	/**
	 * Get the {@link HasSize} configurator, if available.
	 * @return Optional {@link HasSize} configurator
	 */
	protected Optional<HasSizeConfigurator<?>> getSizeConfigurator() {
		if (this.sizeConfigurator == null) {
			hasSize().ifPresent(h -> {
				this.sizeConfigurator = new DefaultHasSizeConfigurator(h);
			});
		}
		return Optional.ofNullable(this.sizeConfigurator);
	}

	/**
	 * Get the {@link HasStyle} configurator, if available.
	 * @return Optional {@link HasStyle} configurator
	 */
	protected Optional<HasStyleConfigurator<?>> getStyleConfigurator() {
		if (this.styleConfigurator == null) {
			hasStyle().ifPresent(h -> {
				this.styleConfigurator = new DefaultHasStyleConfigurator(h);
			});
		}
		return Optional.ofNullable(this.styleConfigurator);
	}

	/**
	 * Get the {@link HasEnabled} configurator, if available.
	 * @return Optional {@link HasEnabled} configurator
	 */
	protected Optional<HasEnabledConfigurator<?>> getEnabledConfigurator() {
		if (this.enabledConfigurator == null) {
			hasEnabled().ifPresent(h -> {
				this.enabledConfigurator = new DefaultHasEnabledConfigurator(h);
			});
		}
		return Optional.ofNullable(this.enabledConfigurator);
	}

	/**
	 * Get the component instance.
	 * @return the component instance
	 */
	protected C getComponent() {
		return component;
	}

	/**
	 * Get the component configurator.
	 * @return the component configurator
	 */
	protected DefaultComponentConfigurator getComponentConfigurator() {
		return componentConfigurator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#id(java.lang.String)
	 */
	@Override
	public B id(String id) {
		getComponentConfigurator().id(id);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#visible(boolean)
	 */
	@Override
	public B visible(boolean visible) {
		getComponentConfigurator().visible(visible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withAttachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public B withAttachListener(ComponentEventListener<AttachEvent> listener) {
		getComponentConfigurator().withAttachListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator#withDetachListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public B withDetachListener(ComponentEventListener<DetachEvent> listener) {
		getComponentConfigurator().withDetachListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public B withEventListener(String eventType, DomEventListener listener) {
		getComponentConfigurator().withEventListener(eventType, listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public B withEventListener(String eventType, DomEventListener listener, String filter) {
		getComponentConfigurator().withEventListener(eventType, listener, filter);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public B withThemeName(String themName) {
		getComponentConfigurator().withThemeName(themName);
		return getConfigurator();
	}

	// ------ size

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	public B width(String width) {
		getSizeConfigurator().ifPresent(c -> c.width(width));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	public B height(String height) {
		getSizeConfigurator().ifPresent(c -> c.height(height));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
	 */
	public B minWidth(String minWidth) {
		getSizeConfigurator().ifPresent(c -> c.minWidth(minWidth));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
	 */
	public B maxWidth(String maxWidth) {
		getSizeConfigurator().ifPresent(c -> c.maxWidth(maxWidth));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
	 */
	public B minHeight(String minHeight) {
		getSizeConfigurator().ifPresent(c -> c.minHeight(minHeight));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
	 */
	public B maxHeight(String maxHeight) {
		getSizeConfigurator().ifPresent(c -> c.maxHeight(maxHeight));
		return getConfigurator();
	}

	// ------- style

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	public B styleNames(String... styleNames) {
		getStyleConfigurator().ifPresent(c -> c.styleNames(styleNames));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	public B styleName(String styleName) {
		getStyleConfigurator().ifPresent(c -> c.styleName(styleName));
		return getConfigurator();
	}

	// ------- enabled

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	public B enabled(boolean enabled) {
		getEnabledConfigurator().ifPresent(c -> c.enabled(enabled));
		return getConfigurator();
	}

}
