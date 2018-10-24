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

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.HasValueInput;
import com.holonplatform.vaadin.flow.internal.components.InputConverterAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.converter.Converter;

/**
 * Input component representation, i.e. a UI component that has a user-editable value.
 * <p>
 * Extends {@link ValueHolder} since handles a value, supporting {@link ValueChangeListener}s registration.
 * </p>
 * <p>
 * The actual UI {@link Component} which represents the input component can be obtained through {@link #getComponent()}.
 * </p>
 * 
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public interface Input<T> extends ValueHolder<T>, ValueComponent<T>, MayHaveLabel, MayHaveTitle, MayHavePlaceholder {

	/**
	 * Sets the read-only mode of this input component. The user can't change the value when in read-only mode.
	 * @param readOnly the read-only mode of this input component
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Returns whether this input component is in read-only mode or not.
	 * @return <code>false</code> if the user can modify the value, <code>true</code> if not
	 */
	boolean isReadOnly();

	/**
	 * Gets whether the field is <em>required</em>, i.e. a <em>required indicator</em> symbol is visible.
	 * @return <code>true</code> if the field as required, <code>false</code> otherwise
	 */
	public boolean isRequired();

	/**
	 * Sets whether the <em>required indicator</em> symbol is visible.
	 * @param required <code>true</code> to set the field as required, <code>false</code> otherwise
	 */
	void setRequired(boolean required);

	/**
	 * Sets the focus for this input component, if supported by concrete component implementation.
	 */
	void focus();

	/**
	 * Checks whether the {@link Component} supports input validation, using the {@link HasValidation} interface.
	 * @return If the component supports input validation, return the {@link HasValidation} reference. An empty Optional
	 *         otherwise.
	 */
	default Optional<HasValidation> hasValidation() {
		return (getComponent() instanceof HasValidation) ? Optional.of((HasValidation) getComponent())
				: Optional.empty();
	}

	// Adapters

	/**
	 * Create a {@link Input} component type from given {@link HasValue} component.
	 * @param <T> Value type
	 * @param <F> {@link HasValue} component type
	 * @param field The field instance (not null)
	 * @return A new {@link Input} component which wraps the given <code>field</code>
	 */
	static <E extends HasValue.ValueChangeEvent<T>, F extends Component & HasValue<E, T>, T> Input<T> from(F field) {
		return new HasValueInput<>(field);
	}

	/**
	 * Create a new {@link Input} from another {@link Input} with a different value type, using given {@link Converter}
	 * to perform value conversions.
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param input Actual input (not null)
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <T, V> Input<T> from(Input<V> input, Converter<V, T> converter) {
		return new InputConverterAdapter<>(input, converter);
	}

	/**
	 * Create a new {@link Input} from given {@link HasValue} component with a different value type, using given
	 * {@link Converter} to perform value conversions.
	 * @param <F> {@link HasValue} component type
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param field The field (not null)
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <E extends HasValue.ValueChangeEvent<V>, F extends Component & HasValue<E, V>, T, V> Input<T> from(F field,
			Converter<V, T> converter) {
		return from(from(field), converter);
	}

	/**
	 * Create a new {@link Input} from another {@link Input} with a different value type, using given
	 * {@link PropertyValueConverter} to perform value conversions.
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param input Actual input (not null)
	 * @param property Property to provide to the converter
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <T, V> Input<T> from(Input<V> input, Property<T> property, PropertyValueConverter<T, V> converter) {
		ObjectUtils.argumentNotNull(converter, "PropertyValueConverter must be not null");
		return new InputConverterAdapter<>(input, Converter.from(value -> converter.fromModel(value, property),
				value -> converter.toModel(value, property), e -> e.getMessage()));
	}

	/**
	 * Create a new {@link Input} from another {@link Input} with a different value type, using given
	 * {@link PropertyValueConverter} to perform value conversions.
	 * @param <F> {@link HasValue} component type
	 * @param <T> New value type
	 * @param <V> Original value type
	 * @param field The field (not null)
	 * @param property Property to provide to the converter
	 * @param converter Value converter (not null)
	 * @return A new {@link Input} of the converted value type
	 */
	static <E extends HasValue.ValueChangeEvent<V>, F extends Component & HasValue<E, V>, T, V> Input<T> from(F field,
			Property<T> property, PropertyValueConverter<T, V> converter) {
		return from(from(field), property, converter);
	}

	// Builders

	/**
	 * Get a {@link HasValueInputBuilder} to create an {@link Input} using given {@link HasValue} {@link Component}
	 * field instance.
	 * @param <T> Value type
	 * @param <E> ValueChangeEvent type
	 * @param <H> Actual field type
	 * @param field {@link HasValue} {@link Component} field (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, E extends HasValue.ValueChangeEvent<T>, H extends Component & HasValue<E, T>> HasValueInputBuilder<T> builder(
			H field) {
		return HasValueInputBuilder.create(field);
	}

	/**
	 * et a {@link HasValueInputBuilder} to create an {@link Input} using given {@link HasValue} and {@link Component}
	 * field instances.
	 * @param <T> Value type
	 * @param <E> ValueChangeEvent type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, E extends HasValue.ValueChangeEvent<T>> HasValueInputBuilder<T> builder(HasValue<E, T> field,
			Component component) {
		return HasValueInputBuilder.create(field, component);
	}

	// Builders by type

	/**
	 * Gets a builder to a create {@link String} type {@link Input}.
	 * @return A new {@link StringInputBuilder}
	 */
	static StringInputBuilder string() {
		return StringInputBuilder.create();
	}

	/**
	 * Gets a builder to create a multi-row {@link String} type {@link Input}.
	 * @return A new {@link StringAreaInputBuilder}
	 */
	static StringAreaInputBuilder stringArea() {
		return StringAreaInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link String} type {@link Input}s which do not display user input on screen, used to
	 * enter secret text information, such as user passwords.
	 * @return A new {@link PasswordInputBuilder}
	 */
	static PasswordInputBuilder password() {
		return PasswordInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link LocalDate} type {@link Input}s.
	 * @return A {@link LocalDateInputBuilder}
	 */
	static LocalDateInputBuilder localDate() {
		return LocalDateInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link Date} type {@link Input}s.
	 * @return A {@link LocalDateInputBuilder}
	 */
	static DateInputBuilder date() {
		return DateInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link Boolean} type {@link Input}s.
	 * @return A {@link BooleanInputBuilder}
	 */
	static BooleanInputBuilder boolean_() {
		return BooleanInputBuilder.create();
	}

	/**
	 * Gets a builder to create a numeric type {@link Input}.
	 * @param <T> Number type
	 * @param numberType Number class (not null)
	 * @return A new {@link NumberInputBuilder}
	 */
	static <T extends Number> NumberInputBuilder<T> number(Class<T> numberClass) {
		return NumberInputBuilder.create(numberClass);
	}

	// Renderers

	/**
	 * A convenience interface with a fixed {@link Input} rendering type to use a {@link Input} {@link PropertyRenderer}
	 * as a functional interface.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface InputPropertyRenderer<T> extends PropertyRenderer<Input<T>, T> {

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#getRenderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		default Class<? extends Input<T>> getRenderType() {
			return (Class<? extends Input<T>>) (Class<?>) Input.class;
		}

	}

	/**
	 * A convenience interface to render a {@link Property} as a {@link Input} using a {@link HasValue} component.
	 * @param <T> Property type
	 */
	@FunctionalInterface
	public interface InputFieldPropertyRenderer<T, E extends HasValue.ValueChangeEvent<T>, F extends Component & HasValue<E, T>>
			extends InputPropertyRenderer<T> {

		/**
		 * Render given <code>property</code> as consistent value type {@link HasValue} component to handle the property
		 * value.
		 * @param property Property to render
		 * @return property {@link HasValue} component
		 */
		F renderField(Property<T> property);

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.property.PropertyRenderer#render(com.holonplatform.core.property.Property)
		 */
		@Override
		default Input<T> render(Property<T> property) {
			return Input.from(renderField(property));
		}

	}

	/**
	 * Exception used to notify Input values conversion errors.
	 */
	public static class InputValueConversionException extends RuntimeException {

		private static final long serialVersionUID = 905052787299053218L;

		public InputValueConversionException(String message) {
			super(message);
		}

		public InputValueConversionException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
