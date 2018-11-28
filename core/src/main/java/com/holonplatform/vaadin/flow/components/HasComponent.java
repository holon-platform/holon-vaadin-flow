/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components;

import java.util.Optional;

import com.holonplatform.vaadin.flow.internal.components.HasComponentAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.dom.Element;

/**
 * Represents and object which can be represented by a UI {@link Component}, which can be obtained using the
 * {@link #getComponent()} method.
 *
 * @since 5.2.0
 */
@FunctionalInterface
public interface HasComponent extends HasElement {

	/**
	 * Get the UI {@link Component} which represents this object.
	 * @return the UI component (not null)
	 */
	Component getComponent();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasElement#getElement()
	 */
	@Override
	default Element getElement() {
		return getComponent().getElement();
	}

	/**
	 * Sets the component visibility value.
	 * <p>
	 * When a component is set as invisible, all the updates of the component from the server to the client are blocked
	 * until the component is set as visible again.
	 * <p>
	 * Invisible components don't receive any updates from the client-side. Unlike the server-side updates, client-side
	 * updates, if any, are discarded while the component is invisible, and are not transmitted to the server when the
	 * component is made visible.
	 * @param visible the component visibility value
	 */
	default void setVisible(boolean visible) {
		getComponent().setVisible(visible);
	}

	/**
	 * Gets the component visibility value.
	 * @return <code>true</code> if the component is visible, <code>false</code> otherwise
	 */
	default boolean isVisible() {
		return getComponent().isVisible();
	}

	/**
	 * Checks whether the {@link Component} may be enabled or disabled, using the {@link HasEnabled} interface.
	 * @return If the component may be enabled or disabled, return the {@link HasEnabled} reference. An empty Optional
	 *         otherwise.
	 */
	default Optional<HasEnabled> hasEnabled() {
		return (getComponent() instanceof HasEnabled) ? Optional.of((HasEnabled) getComponent()) : Optional.empty();
	}

	/**
	 * Checks whether the {@link Component} supports css style classes, using the {@link HasStyle} interface.
	 * @return If the component supports css style classes, return the {@link HasStyle} reference. An empty Optional
	 *         otherwise.
	 */
	default Optional<HasStyle> hasStyle() {
		return (getComponent() instanceof HasStyle) ? Optional.of((HasStyle) getComponent()) : Optional.empty();
	}

	/**
	 * Checks whether the {@link Component} supports size setting, using the {@link HasSize} interface.
	 * @return If the component supports size setting, return the {@link HasSize} reference. An empty Optional
	 *         otherwise.
	 */
	default Optional<HasSize> hasSize() {
		return (getComponent() instanceof HasSize) ? Optional.of((HasSize) getComponent()) : Optional.empty();
	}

	/**
	 * Checks whether this component supports a label, which text can be handled using the {@link HasLabel} interface.
	 * @return If this component supports a label, return the {@link HasLabel} reference. An empty Optional is returned
	 *         otherwise.
	 */
	default Optional<HasLabel> hasLabel() {
		return Optional.empty();
	}

	/**
	 * Checks whether the {@link Component} supports input validation, using the {@link HasValidation} interface.
	 * @return If the component supports input validation, return the {@link HasValidation} reference. An empty Optional
	 *         otherwise.
	 */
	default Optional<HasValidation> hasValidation() {
		return (getComponent() instanceof HasValidation) ? Optional.of((HasValidation) getComponent())
				: Optional.empty();
	}

	// ------- builders

	/**
	 * Create a {@link HasComponent} using given <code>component</code>.
	 * @param component The component to provide from the {@link HasComponent#getComponent()} method (not null)
	 * @return A new {@link HasComponent} bound to given <code>component</code>
	 */
	static HasComponent create(Component component) {
		return new HasComponentAdapter(component);
	}

}
