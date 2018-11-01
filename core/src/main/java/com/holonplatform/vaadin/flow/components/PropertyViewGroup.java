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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.flow.components.ViewComponent.ViewComponentPropertyRenderer;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyViewGroup;

/**
 * Provides functionalities to build and manage a group of {@link ViewComponent}s bound to a {@link Property} set.
 * 
 * @since 5.2.0
 */
public interface PropertyViewGroup extends PropertySetBound, ValueHolder<PropertyBox> {

	/**
	 * Gets all the {@link ViewComponent}s that have been bound to a property.
	 * @return An {@link Iterable} on all bound ViewComponents
	 */
	@SuppressWarnings("rawtypes")
	Iterable<ViewComponent> getViewComponents();

	/**
	 * Get the {@link ViewComponent} bound to given <code>property</code>, if any.
	 * @param <T> Property type
	 * @param property Property (not null)
	 * @return Optional {@link ViewComponent} bound to given <code>property</code>
	 */
	<T> Optional<ViewComponent<T>> getViewComponent(Property<T> property);

	/**
	 * Return a {@link Stream} of the properties and their bound {@link ViewComponent}s of this view group.
	 * @param <T> Property type
	 * @return Property-ViewComponent {@link PropertyBinding} stream
	 */
	<T> Stream<PropertyBinding<T, ViewComponent<T>>> stream();

	/**
	 * Clears (reset the value) all the {@link ViewComponent}s.
	 */
	@Override
	void clear();

	/**
	 * Get the current property values collected into a {@link PropertyBox}, using the group configured properties as
	 * property set.
	 * @return A {@link PropertyBox} containing the property values (never null)
	 */
	@Override
	PropertyBox getValue();

	/**
	 * Set the current property values using a {@link PropertyBox}, loading the values to the available property bound
	 * {@link ViewComponent}s through the {@link ViewComponent#setValue(Object)} method.
	 * <p>
	 * Only the properties which belong to the group's property set are taken into account.
	 * </p>
	 * @param propertyBox the {@link PropertyBox} which contains the property values to load. If <code>null</code>, all
	 *        the {@link ViewComponent} components are cleared.
	 */
	@Override
	void setValue(PropertyBox propertyBox);

	// ------- Builders

	/**
	 * Get a {@link Builder} to create and setup a {@link PropertyViewGroup}.
	 * @return {@link PropertyViewGroupBuilder} builder
	 */
	static PropertyViewGroupBuilder builder() {
		return PropertyViewGroupBuilder.create();
	}

	/**
	 * {@link PropertyViewGroup} builder.
	 */
	public interface PropertyViewGroupBuilder extends Builder<PropertyViewGroup, PropertyViewGroupBuilder> {

		/**
		 * Create a new {@link PropertyViewGroupBuilder} to create and setup a {@link PropertyViewGroup}.
		 * @return A new {@link PropertyViewGroupBuilder} builder
		 */
		static PropertyViewGroupBuilder create() {
			return new DefaultPropertyViewGroup.DefaultBuilder();
		}

	}

	/**
	 * Base {@link PropertyViewGroup} builder.
	 * @param <G> Actual {@link PropertyViewGroup} type
	 * @param <B> Concrete builder type
	 */
	public interface Builder<G extends PropertyViewGroup, B extends Builder<G, B>> {

		/**
		 * Add given properties to the {@link PropertyViewGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add
		 * @return this
		 */
		B properties(Property<?>... properties);

		/**
		 * Add given properties to the {@link PropertyViewGroup} property set.
		 * @param <P> Property type
		 * @param properties Properties to add (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		<P extends Property> B properties(Iterable<P> properties);

		/**
		 * Set the given property as hidden. If a property is hidden, the {@link ViewComponent} bound to the property
		 * will never be generated, but its value will be written to a {@link PropertyBox} using
		 * {@link PropertyViewGroup#getValue()}.
		 * @param <T> Property type
		 * @param property Property to set as hidden (not null)
		 * @return this
		 */
		<T> B hidden(Property<T> property);

		/**
		 * Set to use the provided {@link PropertyRendererRegistry} to render the group components.
		 * <p>
		 * By default, the {@link PropertyRendererRegistry#get()} method is used to obtain the
		 * {@link PropertyRendererRegistry} to use.
		 * </p>
		 * @param propertyRendererRegistry The {@link PropertyRendererRegistry} to use to render the group components
		 * @return this
		 */
		B usePropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry);

		/**
		 * Set the {@link PropertyRenderer} to use to render the {@link ViewComponent} bound to given
		 * <code>property</code>.
		 * @param <T> Property type
		 * @param property The property to render (not null)
		 * @param renderer The renderer to use to render the property {@link ViewComponent} (not null)
		 * @return this
		 */
		<T> B bind(Property<T> property, PropertyRenderer<ViewComponent<T>, T> renderer);

		/**
		 * Set the function to use to render the {@link ViewComponent} bound to given <code>property</code>.
		 * @param <T> Property type
		 * @param property The property to render (not null)
		 * @param function The function to use to render the property {@link ViewComponent} (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, Function<Property<? extends T>, ViewComponent<T>> function) {
			return bind(property, ViewComponentPropertyRenderer.create(function));
		}

		/**
		 * Bind the given <code>property</code> to given <code>viewComponent</code> instance.
		 * @param <T> Property type
		 * @param property The property to render (not null)
		 * @param viewComponent The {@link ViewComponent} to use to render the property (not null)
		 * @return this
		 */
		default <T> B bind(Property<T> property, ViewComponent<T> viewComponent) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(viewComponent, "ViewComponent must be not null");
			return bind(property, ViewComponentPropertyRenderer.create(p -> viewComponent));
		}

		/**
		 * Add a {@link BiConsumer} to allow further {@link ViewComponent} configuration after generation and before the
		 * {@link ViewComponent} is actually bound to a property.
		 * @param postProcessor the post processor to add (not null)
		 * @return this
		 */
		B withPostProcessor(BiConsumer<Property<?>, ViewComponent<?>> postProcessor);

		/**
		 * Add a {@link ValueChangeListener} to the group.
		 * @param listener The ValueChangeListener to add
		 * @return this
		 */
		B withValueChangeListener(ValueChangeListener<PropertyBox> listener);

		/**
		 * Build the {@link PropertyViewGroup}
		 * @return PropertyViewGroup instance
		 */
		G build();

	}

}
