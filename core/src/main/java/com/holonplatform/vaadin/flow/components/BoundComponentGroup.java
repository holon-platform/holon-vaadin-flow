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
package com.holonplatform.vaadin.flow.components;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.vaadin.flow.internal.components.DefaultBinding;
import com.vaadin.flow.component.Component;

/**
 * A {@link ComponentGroup} for which each component is bound to a <em>property</em>, which identifies the component
 * itself within the group.
 * 
 * @param <P> Property type
 * @param <C> Group element type
 *
 * @since 5.2.0
 */
public interface BoundComponentGroup<P, C extends HasComponent> extends ComponentGroup<C>, HasPropertySet<P> {

	/**
	 * Get the available group element bindings. A {@link Binding} represents the group element and the property to
	 * which is bound.
	 * @return A stream of the group bindings
	 */
	Stream<Binding<P, C>> getBindings();

	/**
	 * Get the group element bound to given <code>property</code>, if available.
	 * @param property The property which identifies the element within the group (not null)
	 * @return Optional group element bound to given <code>property</code>
	 */
	Optional<C> getElement(P property);

	/**
	 * Get the group element bound to given <code>property</code>, throwing a {@link IllegalArgumentException} if not
	 * available.
	 * @param property The property which identifies the element within the group (not null)
	 * @return The roup element bound to given <code>property</code>
	 * @throws IllegalArgumentException If no element of the group is bound to given <code>property</code>
	 */
	default C requireElement(P property) {
		return getElement(property)
				.orElseThrow(() -> new IllegalArgumentException("No element bound to the property [" + property + "]"));
	}

	/**
	 * A binding between a property and a group element.
	 *
	 * @param <P> The property type
	 * @param <C> The element type
	 * 
	 * @since 5.2.0
	 */
	public interface Binding<P, C extends HasComponent> extends Serializable {

		/**
		 * Get the property which identifies the component within the group.
		 * @return The property
		 */
		P getProperty();

		/**
		 * Get the element of the group bound to the property.
		 * @return The bound element
		 * @see #getProperty()
		 */
		C getElement();

		/**
		 * Get the bound element {@link Component}.
		 * @return the bound element {@link Component}
		 */
		default Component getComponent() {
			return getElement().getComponent();
		}

		/**
		 * Create a new {@link Binding}.
		 * @param <P> The property type
		 * @param <C> The element type
		 * @param property The binding property (not null)
		 * @param element The bound element (not null)
		 * @return A new {@link Binding} instance
		 */
		static <P, C extends HasComponent> Binding<P, C> create(P property, C element) {
			return new DefaultBinding<>(property, element);
		}

	}

}
