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

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.BoundComponentGroup;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;

/**
 * Input group configurator.
 *
 * @param <P> Property type
 * @param <T> Group item type
 * @param <G> Group type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface InputGroupConfigurator<P, T, G extends BoundComponentGroup<P, Input<?>>, C extends InputGroupConfigurator<P, T, G, C>>
		extends ComponentGroupConfigurator<P, T, Input<?>, G, C> {

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param property The property to set as required (not null)
	 * @return this
	 */
	C required(P property);

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param property The property to set as required (not null)
	 * @param message The required validation error message to use
	 * @return this
	 */
	C required(P property, Localizable message);

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param property The property to set as required (not null)
	 * @param message The required validation error message to use
	 * @return this
	 */
	default C required(P property, String message) {
		return required(property, Localizable.builder().message(message).build());
	}

	/**
	 * Set the given property as required. If a property is required, the {@link Input} bound to the property will be
	 * setted as required, and its validation will fail when empty.
	 * @param property The property to set as required (not null)
	 * @param defaultMessage Default required validation error message
	 * @param messageCode Required validation error message translation key
	 * @param arguments Optional translation arguments
	 * @return this
	 */
	default C required(P property, String defaultMessage, String messageCode, Object... arguments) {
		return required(property, Localizable.builder().message((defaultMessage == null) ? "" : defaultMessage)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

	/**
	 * Add a {@link BiConsumer} to allow further {@link Input} configuration before the input is actually bound to a
	 * property as editor.
	 * @param postProcessor the post processor to add (not null)
	 * @return this
	 */
	C withPostProcessor(BiConsumer<P, Input<?>> postProcessor);

	/**
	 * Adds a group value {@link Validator}.
	 * @param validator The group validator to add (not null)
	 * @return this
	 */
	C withValidator(Validator<T> validator);

	/**
	 * Set the {@link ValidationStatusHandler} to use to track the group validation status changes.
	 * @param validationStatusHandler the group {@link ValidationStatusHandler} to set (not null)
	 * @return this
	 */
	C validationStatusHandler(ValidationStatusHandler<G, T, ValueComponent<T>> validationStatusHandler);

	/**
	 * Use given label as status label to track group validation status changes.
	 * @param <L> Label type
	 * @param statusLabel the status label to set (not null)
	 * @return this
	 */
	default <L extends Component & HasText> C validationStatusLabel(L statusLabel) {
		return validationStatusHandler(ValidationStatusHandler.label(statusLabel));
	}

	/**
	 * Set whether to enable {@link VirtualProperty} input values refresh when any group input value changes.
	 * <p>
	 * Default is <code>false</code>.
	 * </p>
	 * @param enableRefreshOnValueChange Whether to enable {@link VirtualProperty} input values refresh when any group
	 *        input value changes
	 * @return this
	 */
	C enableRefreshOnValueChange(boolean enableRefreshOnValueChange);

	/**
	 * An {@link InputGroupConfigurator} bound to a {@link Property} set.
	 * 
	 * @param <G> Group type
	 * @param <C> Concrete configurator type
	 * 
	 * @since 5.2.0
	 */
	public interface PropertySetInputGroupConfigurator<G extends BoundComponentGroup<Property<?>, Input<?>>, C extends PropertySetInputGroupConfigurator<G, C>>
			extends InputGroupConfigurator<Property<?>, PropertyBox, G, C> {

		/**
		 * Set the default value provider for given <code>property</code>.
		 * @param <V> Property type
		 * @param property Property (not null)
		 * @param defaultValueProvider The function to provide the property default value (not null)
		 * @return this
		 */
		<V> C defaultValue(Property<V> property, Function<Property<V>, V> defaultValueProvider);

		/**
		 * Add a {@link ValueChangeListener} to the {@link Input} bound to given <code>property</code>.
		 * @param <V> Property type
		 * @param property The property (not null)
		 * @param listener The ValueChangeListener to add (not null)
		 * @return this
		 */
		<V> C withValueChangeListener(Property<V> property,
				ValueChangeListener<V, GroupValueChangeEvent<V, Property<?>, Input<?>, G>> listener);

		/**
		 * Adds a {@link Validator} to the {@link Input} bound to given <code>property</code>.
		 * @param <V> Property type
		 * @param property The property (not null)
		 * @param validator The validator to add (not null)
		 * @return this
		 */
		<V> C withValidator(Property<V> property, Validator<V> validator);

		/**
		 * Set the {@link ValidationStatusHandler} to use to track given <code>property</code> validation status
		 * changes.
		 * @param <V> Property type
		 * @param property The property for which to set the validation status handler (not null)
		 * @param validationStatusHandler the {@link ValidationStatusHandler} to associate to given
		 *        <code>property</code> (not null)
		 * @return this
		 */
		<V> C validationStatusHandler(Property<V> property,
				ValidationStatusHandler<G, V, Input<V>> validationStatusHandler);

	}

	/**
	 * An {@link InputGroupConfigurator} bound to a bean {@link String} type property set.
	 * 
	 * @param <T> Bean type
	 * @param <G> Group type
	 * @param <C> Concrete configurator type
	 * 
	 * @since 5.2.0
	 */
	public interface BeanInputGroupConfigurator<T, G extends BoundComponentGroup<String, Input<?>>, C extends BeanInputGroupConfigurator<T, G, C>>
			extends InputGroupConfigurator<String, T, G, C> {

		/**
		 * Set the default value provider for given <code>property</code>.
		 * @param property Property (not null)
		 * @param defaultValueProvider The function to provide the property default value (not null)
		 * @return this
		 */
		C defaultValue(String property, Function<String, Object> defaultValueProvider);

		/**
		 * Add a {@link ValueChangeListener} to the {@link Input} bound to given <code>property</code>.
		 * @param property Property (not null)
		 * @param listener The ValueChangeListener to add (not null)
		 * @return this
		 */
		C withValueChangeListener(String property,
				ValueChangeListener<?, GroupValueChangeEvent<?, String, Input<?>, G>> listener);

		/**
		 * Adds a {@link Validator} to the {@link Input} bound to given <code>property</code>.
		 * @param property The property (not null)
		 * @param validator The validator to add (not null)
		 * @return this
		 */
		C withValidator(String property, Validator<?> validator);

		/**
		 * Set the {@link ValidationStatusHandler} to use to track given <code>property</code> validation status
		 * changes.
		 * @param property The property for which to set the validation status handler (not null)
		 * @param validationStatusHandler the {@link ValidationStatusHandler} to associate to given
		 *        <code>property</code> (not null)
		 * @return this
		 */
		C validationStatusHandler(String property, ValidationStatusHandler<G, ?, Input<?>> validationStatusHandler);

	}

}
