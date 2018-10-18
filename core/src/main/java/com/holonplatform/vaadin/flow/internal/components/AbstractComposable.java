/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Optional;
import java.util.function.Consumer;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.PropertyComponentSource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;

/**
 * Base {@link Composable} form component.
 * 
 * @param <C> Content component type
 * @param <S> Components source type
 * 
 * @since 5.2.0
 */
public abstract class AbstractComposable<C extends Component, S extends PropertyComponentSource> extends Composite<C>
		implements Composable {

	private static final long serialVersionUID = 6196476129131362753L;

	/**
	 * Form content initializer
	 */
	private Consumer<C> initializer;

	/**
	 * Composer
	 */
	private Composer<? super C, S> composer;

	/**
	 * Compose on attach behaviour
	 */
	private boolean composeOnAttach = true;

	/**
	 * Components width mode
	 */
	private ComponentsWidthMode componentsWidthMode = ComponentsWidthMode.AUTO;

	/**
	 * Composition state
	 */
	private boolean composed = false;

	/**
	 * Content component
	 */
	private final C content;

	/**
	 * Constructor.
	 * @param content Form composition content (not null)
	 */
	public AbstractComposable(C content) {
		super();
		ObjectUtils.argumentNotNull(content, "Content component must be not null");
		this.content = content;
		// compose on attach
		content.addAttachListener(e -> {
			// check compose on attach
			if (!isComposed() && isComposeOnAttach()) {
				compose();
			}
		});
	}

	/**
	 * Get the actual {@link PropertyComponentSource} to use.
	 * @return the form {@link PropertyComponentSource}
	 */
	protected abstract S getComponentSource();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.Composite#initContent()
	 */
	@Override
	protected C initContent() {
		return content;
	}

	/**
	 * Gets whether the form was already composed.
	 * @return whether the form was already composed
	 */
	protected boolean isComposed() {
		return composed;
	}

	/**
	 * Get the form content initializer
	 * @return the form content initializer
	 */
	public Optional<Consumer<C>> getInitializer() {
		return Optional.ofNullable(initializer);
	}

	/**
	 * Set the form content initializer
	 * @param initializer the initializer to set
	 */
	public void setInitializer(Consumer<C> initializer) {
		this.initializer = initializer;
	}

	/**
	 * Get the composer
	 * @return the composer
	 */
	public Composer<? super C, S> getComposer() {
		return composer;
	}

	/**
	 * Set the composer
	 * @param composer the composer to set
	 */
	public void setComposer(Composer<? super C, S> composer) {
		this.composer = composer;
	}

	/**
	 * Gets whether the form must be composed on {@link #attach()}, if not already composed invoking {@link #compose()}.
	 * @return <code>true</code> if the form must be composed on {@link #attach()}
	 */
	public boolean isComposeOnAttach() {
		return composeOnAttach;
	}

	/**
	 * Sets whether the form must be composed on {@link #attach()}, if not already composed invoking {@link #compose()}.
	 * @param composeOnAttach <code>true</code> to compose the form on {@link #attach()}
	 */
	public void setComposeOnAttach(boolean composeOnAttach) {
		this.composeOnAttach = composeOnAttach;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ComposableComponent#getComponentsWidthMode()
	 */
	@Override
	public ComponentsWidthMode getComponentsWidthMode() {
		return componentsWidthMode;
	}

	/**
	 * Set the composed Components width setup mode
	 * @param componentsWidthMode the ComponentsWidthMode to set (not null)
	 */
	public void setComponentsWidthMode(ComponentsWidthMode componentsWidthMode) {
		ObjectUtils.argumentNotNull(componentsWidthMode, "ComponentsWidthMode must be not null");
		this.componentsWidthMode = componentsWidthMode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ComposableComponent#compose()
	 */
	@Override
	public void compose() {
		if (getComposer() == null) {
			throw new IllegalStateException("Missing form composer");
		}

		final C content = getContent();
		if (content == null) {
			throw new IllegalStateException("Missing form content");
		}

		// init form content
		getInitializer().ifPresent(i -> i.accept(content));

		// setup components
		final String contentWidth = (content instanceof HasSize) ? ((HasSize) content).getWidth() : null;
		final boolean fullWidth = (getComponentsWidthMode() == ComponentsWidthMode.FULL)
				|| ((getComponentsWidthMode() == ComponentsWidthMode.AUTO) && "100%".equals(contentWidth));

		getComponentSource().streamOfComponents().forEach(binding -> {
			setupPropertyComponent(binding.getProperty(), binding.getComponent(), fullWidth);
		});

		// compose
		getComposer().compose(content, getComponentSource());

		this.composed = true;
	}

	/**
	 * Setup the {@link Component} associated with given {@link Property}.
	 * @param property Property
	 * @param component Component
	 * @param fullWidth whether to set the Component width to 100% width according to {@link #getComponentsWidthMode()},
	 *        if the component provides {@link HasSize} support
	 */
	protected void setupPropertyComponent(Property<?> property, Component component, boolean fullWidth) {
		if (fullWidth && component instanceof HasSize) {
			((HasSize) component).setWidth("100%");
		}
	}

}
