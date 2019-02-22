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

import java.util.function.Function;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyValueProvider;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.ItemListing.EditorComponentGroup;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator.PropertySetInputGroupConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.converter.Converter;

/**
 * {@link PropertyListing} configurator.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface PropertyListingConfigurator<C extends PropertyListingConfigurator<C>>
		extends ItemListingConfigurator<PropertyBox, Property<?>, PropertyListing, C>,
		PropertySetInputGroupConfigurator<EditorComponentGroup<Property<?>, PropertyBox>, C> {

	/**
	 * Add a column which contents will be rendered using given {@link VirtualProperty}.
	 * <p>
	 * The virtual property {@link PropertyValueProvider} will be invoked for each listing item to obtain the value to
	 * display.
	 * </p>
	 * @param <X> Property type
	 * @param property The virtual property which represent the column.
	 * @return An {@link ItemListingColumnBuilder} which allow further column configuration and provides the
	 *         {@link ItemListingColumnBuilder#add()} method to add the column to the listing
	 */
	<X> ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, C> withColumn(VirtualProperty<X> property);

	/**
	 * Add a column which contents will be rendered as a {@link Component} using given {@link VirtualProperty}.
	 * <p>
	 * The virtual property {@link PropertyValueProvider} will be invoked for each listing item to obtain the Component
	 * to display.
	 * </p>
	 * @param property The virtual property which represent the column.
	 * @return An {@link ItemListingColumnBuilder} which allow further column configuration and provides the
	 *         {@link ItemListingColumnBuilder#add()} method to add the column to the listing
	 */
	ItemListingColumnBuilder<PropertyBox, Property<?>, PropertyListing, C> withComponentColumn(
			VirtualProperty<? extends Component> property);

	/**
	 * Set the {@link PropertyRenderer} to provide the {@link Input} to use as given <code>property</code> column
	 * editor, when a listing row is in editable mode.
	 * @param <T> Property type
	 * @param property The property for which to set the editor provider (not null)
	 * @param renderer The property {@link Input} provider (not null)
	 * @return this
	 */
	<T> C editor(Property<T> property, PropertyRenderer<Input<T>, T> renderer);

	/**
	 * Set the function to provide the {@link Input} to use as given <code>property</code> column editor, when a listing
	 * row is in editable mode.
	 * @param <T> Property type
	 * @param property The property for which to set the editor provider (not null)
	 * @param function The function to use to provider the property {@link Input} editor (not null)
	 * @return this
	 */
	default <T> C editor(Property<T> property, Function<Property<? extends T>, Input<T>> function) {
		return editor(property, InputPropertyRenderer.create(function));
	}

	/**
	 * Set the {@link Input} to use as given <code>property</code> column editor, when a listing row is in editable
	 * mode.
	 * @param <T> Property type
	 * @param property The property for which to set the editor {@link Input} (not null)
	 * @param input The {@link Input} to use as property editor (not null)
	 * @return this
	 */
	default <T> C editor(Property<T> property, Input<T> input) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		return editor(property, InputPropertyRenderer.create(p -> input));
	}

	/**
	 * Set the {@link Input} to use as given <code>property</code> column editor, when a listing row is in editable
	 * mode, ad the {@link Converter} to use to convert the {@link Input} value type to the required property value
	 * type.
	 * @param <T> Property type
	 * @param <V> Input value type
	 * @param property The property for which to set the editor {@link Input} (not null)
	 * @param input The {@link Input} to use as property editor (not null)
	 * @param converter The value converter (not null)
	 * @return this
	 */
	default <T, V> C editor(Property<T> property, Input<V> input, Converter<V, T> converter) {
		return editor(property, Input.from(input, converter));
	}

	/**
	 * Set the {@link HasValue} component to use as given <code>property</code> column editor, when a listing row is in
	 * editable mode.
	 * @param <T> Property type
	 * @param <F> HasValue type
	 * @param property The property for which to set the editor (not null)
	 * @param field The {@link HasValue} component to use as property editor (not null)
	 * @return this
	 */
	default <T, F extends Component & HasValue<?, T>> C editorField(Property<T> property, F field) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		return editor(property, Input.from(field));
	}

	/**
	 * Set the {@link HasValue} component to use as given <code>property</code> column editor, when a listing row is in
	 * editable mode, ad the {@link Converter} to use to convert the {@link HasValue} value type to the required
	 * property value type.
	 * @param <T> Property type
	 * @param <V> HasValue value type
	 * @param <F> HasValue type
	 * @param property The property for which to set the editor (not null)
	 * @param field The {@link HasValue} component to use as property editor (not null)
	 * @param converter The value converter (not null)
	 * @return this
	 */
	default <T, V, F extends Component & HasValue<?, V>> C editorField(Property<T> property, F field,
			Converter<V, T> converter) {
		return editor(property, Input.from(field, converter));
	}

	/**
	 * Render the column associated to given <code>property</code> using a {@link ViewComponent}.
	 * <p>
	 * The current {@link PropertyRendererRegistry} is used, if available, to render the property as a
	 * {@link ViewComponent}.
	 * </p>
	 * <p>
	 * By default, the {@link ViewComponent#getContentComponent()} method is used to obtain the Component with which to
	 * render the cell contents, in order to exclude any {@link ViewComponent} label.
	 * </p>
	 * <p>
	 * If a {@link ViewComponent} is not available or it ha not content Component, and empty {@link Div} is returned as
	 * cell content.
	 * </p>
	 * @param <T> Property type
	 * @param property The property to render as a {@link ViewComponent} (not null)
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default <T> C renderAsViewComponent(Property<T> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return componentRenderer(property, item -> {
			final ViewComponent<T> viewComponent = property.renderIfAvailable(ViewComponent.class)
					.orElseGet(() -> ViewComponent.builder(property).build());
			if (item.contains(property)) {
				viewComponent.setValue(item.getValue(property));
			}
			return viewComponent.getContentComponent().orElseGet(() -> new Div());
		});
	}

}
