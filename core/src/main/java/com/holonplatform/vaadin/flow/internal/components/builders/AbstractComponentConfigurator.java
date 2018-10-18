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

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
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
	 * Get the actual configurator.
	 * @return the actual configurator
	 */
	protected abstract B getConfigurator();

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

}
