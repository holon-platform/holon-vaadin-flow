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
package com.holonplatform.vaadin.flow.components;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValueChangeEvent;

/**
 * Represents an object which holds a value and provide methods to handle such value.
 * 
 * @param <V> Value type
 * @param <E> Value change event type
 * 
 * @since 5.2.0
 */
public interface ValueHolder<V, E extends ValueChangeEvent<V>> extends Serializable {

	/**
	 * Sets the <code>value</code> of this value holder.
	 * @param value the value to set
	 * @throws IllegalArgumentException if the value is not valid
	 */
	void setValue(V value);

	/**
	 * Gets the current value of this value holder.
	 * @return the current value
	 */
	V getValue();

	/**
	 * Gets the value as an {@link Optional}, which will be empty if the value holder is considered to be empty or the
	 * value is <code>null</code>.
	 * @return Optional value
	 */
	default Optional<V> getValueIfPresent() {
		return isEmpty() ? Optional.empty() : Optional.ofNullable(getValue());
	}

	/**
	 * Returns the value that represents an empty value.
	 * @return the value that represents an empty value (<code>null</code> by default)
	 */
	default V getEmptyValue() {
		return null;
	}

	/**
	 * Returns whether this value holder is considered to be empty, according to its current value.
	 * <p>
	 * By default this is an equality check between current value and empty value.
	 * </p>
	 * @return <code>true</code> if considered empty, <code>false</code> if not
	 */
	default boolean isEmpty() {
		return Objects.equals(getValue(), getEmptyValue());
	}

	/**
	 * Clears this value holder.
	 * <p>
	 * By default, resets the value to the empty one.
	 * </p>
	 */
	default void clear() {
		setValue(getEmptyValue());
	}

	/**
	 * Adds a value change listener, called when the value changes.
	 * @param listener the value change listener to add (not null)
	 * @return a registration for the listener, which provides the <em>remove</em> operation
	 */
	public Registration addValueChangeListener(ValueChangeListener<V, E> listener);

	// ------- value change handling

	/**
	 * A listener for {@link ValueHolder} value change events.
	 * 
	 * @param <V> Value type
	 * @param <E> Value change event type
	 */
	@FunctionalInterface
	public interface ValueChangeListener<V, E extends ValueChangeEvent<V>> extends Serializable {

		/**
		 * Invoked when a {@link ValueChangeEvent} is triggered.
		 * @param event the value change event
		 */
		void valueChange(E event);

	}

	/**
	 * A {@link ValueChangeListener} event.
	 * 
	 * @param <V> Value type
	 */
	public interface ValueChangeEvent<V> extends Serializable {

		/**
		 * Returns whether this event was triggered by user interaction, on the client side, or programmatically, on the
		 * server side.
		 * @return <code>true</code> if this event originates from the client, <code>false</code> otherwise.
		 */
		boolean isUserOriginated();

		/**
		 * Get the source of this value change event.
		 * @return the {@link ValueHolder} type source
		 */
		ValueHolder<V, ?> getSource();

		/**
		 * Returns the value of the source before this value change event occurred.
		 * @return the old value
		 */
		V getOldValue();

		/**
		 * Returns the new value that triggered this value change event.
		 * @return the new value
		 */
		V getValue();

		/**
		 * Create a new {@link ValueChangeEvent}.
		 * @param <T> Value type
		 * @param source Source (not null)
		 * @param oldValue Old value
		 * @param value New value
		 * @param userOriginated Whether is a client-side (user originated) change event or server side
		 * @return A new {@link ValueChangeEvent} instance
		 */
		static <T> ValueChangeEvent<T> create(ValueHolder<T, ?> source, T oldValue, T value, boolean userOriginated) {
			return new DefaultValueChangeEvent<>(source, oldValue, value, userOriginated);
		}

	}

}
