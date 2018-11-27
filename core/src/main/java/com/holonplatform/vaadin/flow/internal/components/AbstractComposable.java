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
import com.holonplatform.vaadin.flow.components.ComponentGroup;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.vaadin.flow.component.Component;

/**
 * Base {@link Composable} form component.
 * 
 * @param <C> Content component type
 * @param <E> Elements type
 * @param <G> Elements group type
 * 
 * @since 5.2.0
 */
public abstract class AbstractComposable<C extends Component, E extends HasComponent, G extends ComponentGroup<E>>
		implements Composable {

	/**
	 * Form content initializer
	 */
	private Consumer<C> initializer;

	/**
	 * Composer
	 */
	private Composer<? super C, E, G> composer;

	/**
	 * Compose on attach behaviour
	 */
	private boolean composeOnAttach = true;

	/**
	 * Whether content component was initialized
	 */
	private boolean contentInitialized = false;

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
			// check compose on attach if not already composed
			if (!isComposed() && isComposeOnAttach()) {
				compose();
			}
		});
	}

	/**
	 * Get the bound component group.
	 * @return the bound component group
	 */
	protected abstract G getComponentGroup();

	/**
	 * Get the form content component.
	 * @return the form content component
	 */
	protected C getContent() {
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
	 * Get the composer, if available.
	 * @return Optional composer
	 */
	public Optional<Composer<? super C, E, G>> getComposer() {
		return Optional.ofNullable(composer);
	}

	/**
	 * Set the composer
	 * @param composer the composer to set
	 */
	public void setComposer(Composer<? super C, E, G> composer) {
		this.composer = composer;
	}

	/**
	 * Gets whether the form must be composed on content component <code>attach</code>, if not already composed invoking
	 * {@link #compose()}.
	 * @return <code>true</code> if the form must be composed on content component <code>attach</code>
	 */
	public boolean isComposeOnAttach() {
		return composeOnAttach;
	}

	/**
	 * Sets whether the form must be composed on content component <code>attach</code>, if not already composed invoking
	 * {@link #compose()}.
	 * @param composeOnAttach <code>true</code> to compose the form on content component <code>attach</code>
	 */
	public void setComposeOnAttach(boolean composeOnAttach) {
		this.composeOnAttach = composeOnAttach;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ComposableComponent#compose()
	 */
	@Override
	public void compose() {

		// content initializer
		getInitializer().ifPresent(i -> {
			if (!contentInitialized) {
				i.accept(getContent());
				contentInitialized = true;
			}
		});

		// compose
		getComposer().orElseThrow(() -> new IllegalStateException("No composer available")).compose(getContent(),
				getComponentGroup());

		this.composed = true;
	}

}
