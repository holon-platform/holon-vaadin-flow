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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.operation.TriConsumer;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.HasValueInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalDateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.ItemOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.PropertyOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.ItemSelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.PropertySelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.flow.components.events.InvalidChangeEventNotifier;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.DefaultInputPropertyRenderer;
import com.holonplatform.vaadin.flow.internal.components.EnumItemCaptionGenerator;
import com.holonplatform.vaadin.flow.internal.components.HasValueInputAdapter;
import com.holonplatform.vaadin.flow.internal.components.InputAdapter;
import com.holonplatform.vaadin.flow.internal.components.InputConverterAdapter;
import com.holonplatform.vaadin.flow.internal.components.support.CallbackPropertyHandler;
import com.vaadin.flow.component.Component;
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
public interface Input<T> extends ValueHolder<T>, ValueComponent<T>, MayHaveLabel {

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
	 * Checks whether this component supports a title, which text can be handled using the {@link HasTitle} interface.
	 * @return If this component supports a title, return the {@link HasTitle} reference. An empty Optional is returned
	 *         otherwise.
	 */
	default Optional<HasTitle> hasTitle() {
		return Optional.empty();
	}

	/**
	 * Checks whether this component supports a placeholder, which text can be handled using the {@link HasPlaceholder}
	 * interface.
	 * @return If this component supports a placeholder, return the {@link HasPlaceholder} reference. An empty Optional
	 *         is returned otherwise.
	 */
	default Optional<HasPlaceholder> hasPlaceholder() {
		return Optional.empty();
	}

	/**
	 * Checks whether this input supports invalid change events notification, using a
	 * {@link InvalidChangeEventNotifier}.
	 * @return If this input supports invalid change events notification, return the {@link InvalidChangeEventNotifier}
	 *         reference. An empty Optional is returned otherwise.
	 */
	default Optional<InvalidChangeEventNotifier> hasInvalidChangeEventNotifier() {
		return Optional.empty();
	}

	// Adapters

	/**
	 * Get this {@link Input} as an {@link HasValue} component.
	 * @return The {@link HasValue} component
	 */
	default HasValue<?, T> asHasValue() {
		return new HasValueInputAdapter<>(this);
	}

	/**
	 * Create a {@link Input} component type from given {@link HasValue} component.
	 * @param <F> {@link HasValue} component type
	 * @param <T> Value type
	 * @param field The field instance (not null)
	 * @return A new {@link Input} component which wraps the given <code>field</code>
	 */
	static <T, F extends Component & HasValue<?, T>> Input<T> from(F field) {
		return new InputAdapter<>(field, field);
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
	static <F extends Component & HasValue<?, V>, T, V> Input<T> from(F field, Converter<V, T> converter) {
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
	static <F extends Component & HasValue<?, V>, T, V> Input<T> from(F field, Property<T> property,
			PropertyValueConverter<T, V> converter) {
		return from(from(field), property, converter);
	}

	// Builders

	/**
	 * Get a {@link HasValueInputBuilder} to create an {@link Input} using given {@link HasValue} {@link Component}
	 * field instance.
	 * @param <T> Value type
	 * @param <H> Field type
	 * @param field {@link HasValue} {@link Component} field (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, H extends Component & HasValue<?, T>> HasValueInputBuilder<T, H, H> builder(H field) {
		return HasValueInputBuilder.create(field);
	}

	/**
	 * et a {@link HasValueInputBuilder} to create an {@link Input} using given {@link HasValue} and {@link Component}
	 * field instances.
	 * @param <T> Value type
	 * @param <H> {@link HasValue} type
	 * @param <C> {@link Component} type
	 * @param field {@link HasValue} field (not null)
	 * @param component Field {@link Component} (not null)
	 * @return A new {@link HasValueInputBuilder}
	 */
	static <T, H extends HasValue<?, T>, C extends Component> HasValueInputBuilder<T, H, C> builder(H field,
			C component) {
		return HasValueInputBuilder.create(field, component);
	}

	// Builders using property

	/**
	 * Create an {@link Input} component using given {@link Property} to detect the Input value type and the Input
	 * configuration.
	 * <p>
	 * Any available {@link Input} type {@link PropertyRenderer} from the current {@link PropertyRendererRegistry} will
	 * be used to provide the {@link Input} instance.
	 * </p>
	 * @param <T> Property and Input type
	 * @param property The property to use (not null)
	 * @return A new {@link Input} component with the same type of given {@link Property}
	 * @throws NoSuitableRendererAvailableException if a suitable {@link Input} type {@link PropertyRenderer} is not
	 *         available
	 * @see PropertyRendererRegistry
	 */
	@SuppressWarnings("unchecked")
	static <T> Input<T> create(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return property.render(Input.class);
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
	 * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
	 * @return A {@link LocalDateTimeInputBuilder}
	 */
	static LocalDateTimeInputBuilder localDateTime() {
		return LocalDateTimeInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link LocalTime} type {@link Input}s.
	 * @return A {@link LocalTimeInputBuilder}
	 */
	static LocalTimeInputBuilder localTime() {
		return LocalTimeInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link Date} type {@link Input}s.
	 * <p>
	 * This Input use the {@link Date} type only for simple date representations (day, month, year), i.e. without the
	 * time part.
	 * </p>
	 * @return A {@link DateInputBuilder}
	 */
	static DateInputBuilder date() {
		return DateInputBuilder.create();
	}

	/**
	 * Gets a builder to create {@link Date} type {@link Input}s with time (hours and minutes) support.
	 * <p>
	 * Only the hours and minutes time parts are supported.
	 * </p>
	 * @return A {@link DateTimeInputBuilder}
	 */
	static DateTimeInputBuilder dateTime() {
		return DateTimeInputBuilder.create();
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

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are consistent. Use
	 * {@link #singleSelect(ItemConverter)} if not.
	 * <p>
	 * @param <T> Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T> ItemSelectModeSingleSelectInputBuilder<T, T> singleSelect(Class<T> type) {
		return SingleSelect.create(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are not consistent (i.e. of
	 * different type). When the the selection item and the selection value types are consistent, the
	 * {@link #singleSelect()} method can be used.
	 * <p>
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item into a selection (Input) value and
	 *        back (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemSelectModeSingleSelectInputBuilder<T, ITEM> singleSelect(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return SingleSelect.create(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property}.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> singleSelect(final Property<T> selectionProperty) {
		return SingleSelect.create(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property} and converter.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
	 */
	static <T> PropertySelectModeSingleSelectInputBuilder<T> singleSelect(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return SingleSelect.create(selectionProperty, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input using the <em>options</em> rendering mode, i.e. a
	 * radio button group.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are consistent. Use
	 * {@link #singleSelect(ItemConverter)} if not.
	 * <p>
	 * @param <T> Value type
	 * @param type Selection value type (not null)
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <T> ItemOptionsModeSingleSelectInputBuilder<T, T> singleOptionSelect(Class<T> type) {
		return SingleSelect.options(type);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input using the <em>options</em> rendering mode, i.e. a
	 * radio button group.
	 * <p>
	 * This builder can be used when the selection items type and the selection value type are not consistent (i.e. of
	 * different type). When the the selection item and the selection value types are consistent, the
	 * {@link #singleOptionSelect()} method can be used.
	 * <p>
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use to convert a selection item into a selection (Input) value and
	 *        back (not null)
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <T, ITEM> ItemOptionsModeSingleSelectInputBuilder<T, ITEM> singleOptionSelect(Class<T> type,
			Class<ITEM> itemType, ItemConverter<T, ITEM> itemConverter) {
		return SingleSelect.options(type, itemType, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property} and the
	 * <em>options</em> rendering mode, i.e. a radio button group.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeSingleSelectInputBuilder<T> singleOptionSelect(final Property<T> selectionProperty) {
		return SingleSelect.options(selectionProperty);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property}, a converter
	 * and the <em>options</em> rendering mode, i.e. a radio button group.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
	 */
	static <T> PropertyOptionsModeSingleSelectInputBuilder<T> singleOptionSelect(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return SingleSelect.options(selectionProperty, itemConverter);
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input for given <code>enum</code> type.
	 * <p>
	 * All the enum constants declared for the given enum type will be available as selection items.
	 * </p>
	 * @param <E> Enum type
	 * @param enumType Enum type (not null)
	 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
	 */
	static <E extends Enum<E>> ItemSelectModeSingleSelectInputBuilder<E, E> enumSelect(Class<E> enumType) {
		return singleSelect(enumType).items(enumType.getEnumConstants())
				.itemCaptionGenerator(new EnumItemCaptionGenerator<>());
	}

	/**
	 * Gets a builder to create a {@link SingleSelect} type Input for given <code>enum</code> type with the
	 * <em>options</em> rendering mode, i.e. using a radio button group.
	 * <p>
	 * All the enum constants declared for the given enum type will be available as selection items.
	 * </p>
	 * @param <E> Enum type
	 * @param enumType Enum type (not null)
	 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
	 */
	static <E extends Enum<E>> ItemOptionsModeSingleSelectInputBuilder<E, E> enumOptionSelect(Class<E> enumType) {
		return singleOptionSelect(enumType).items(enumType.getEnumConstants())
				.itemCaptionGenerator(new EnumItemCaptionGenerator<>());
	}

	// Create by type

	/**
	 * Create an {@link Input} to handle given value <code>type</code>, if available.
	 * @param <T> Value type
	 * @param type The value type (not null)
	 * @return Optional {@link Input} component
	 */
	static <T> Optional<Input<T>> create(Class<T> type) {
		return DefaultInputPropertyRenderer.createByType(type);
	}

	// Renderers

	/**
	 * A convenience interface with a fixed {@link Input} rendering type to use a {@link Input} {@link PropertyRenderer}
	 * as a functional interface.
	 * 
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

		/**
		 * Create a new {@link InputPropertyRenderer} using given function.
		 * @param <T> Property type
		 * @param function Function to use to provide the {@link Input} component
		 * @return A new {@link InputPropertyRenderer}
		 */
		static <T> InputPropertyRenderer<T> create(Function<Property<? extends T>, Input<T>> function) {
			return property -> function.apply(property);
		}

	}

	/**
	 * Input field/component property handler.
	 * 
	 * @param <P> Property value type
	 * @param <T> Input value type
	 * @param <V> {@link HasValue} type
	 * @param <C> {@link Component} type
	 * 
	 * @see HasValueInputBuilder
	 */
	public interface PropertyHandler<P, T, V extends HasValue<?, T>, C extends Component>
			extends BiFunction<V, C, P>, TriConsumer<V, C, P> {

		/**
		 * Create a new {@link PropertyHandler} using given <code>getter</code> to get the property value and given
		 * <code>setter</code> to set the property value
		 * @param <P> Property value type
		 * @param <T> Input value type
		 * @param <V> {@link HasValue} type
		 * @param <C> {@link Component} type
		 * @param getter A {@link BiFunction} to get the property value (not null)
		 * @param setter A {@link TriConsumer} to set the property value (not null)
		 * @return A new {@link PropertyHandler}
		 */
		static <P, T, V extends HasValue<?, T>, C extends Component> PropertyHandler<P, T, V, C> create(
				BiFunction<V, C, P> getter, TriConsumer<V, C, P> setter) {
			return new CallbackPropertyHandler<>(getter, setter);
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
