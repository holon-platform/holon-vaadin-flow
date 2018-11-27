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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyRendererRegistry.NoSuitableRendererAvailableException;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.ViewComponent.ViewComponentPropertyRenderer;
import com.holonplatform.vaadin.flow.components.builders.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyViewGroupConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyConfiguration;
import com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyConfigurationRegistry;
import com.holonplatform.vaadin.flow.internal.components.support.ViewComponentPropertyRegistry;

/**
 * Default {@link PropertyViewGroup} implementation.
 *
 * @since 5.2.0
 */
public class DefaultPropertyViewGroup extends AbstractPropertySetGroup<ViewComponent<?>> implements PropertyViewGroup {

	private static final long serialVersionUID = -2110591918893531742L;

	/**
	 * Property configurations
	 */
	private final ViewComponentPropertyConfigurationRegistry configuration = ViewComponentPropertyConfigurationRegistry
			.create();

	/**
	 * Property components
	 */
	private final ViewComponentPropertyRegistry components = ViewComponentPropertyRegistry.create();

	/**
	 * Constructor.
	 * @param propertySet The property set (not null)
	 */
	public DefaultPropertyViewGroup(PropertySet<?> propertySet) {
		super(propertySet);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup#getBindings()
	 */
	@Override
	public Stream<Binding<Property<?>, ViewComponent<?>>> getBindings() {
		return components.stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup#getElement(java.lang.Object)
	 */
	@Override
	public Optional<ViewComponent<?>> getElement(Property<?> property) {
		return components.get(property).map(i -> i);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ComponentGroup#getElements()
	 */
	@Override
	public Stream<ViewComponent<?>> getElements() {
		return components.stream().map(b -> b.getElement());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getViewComponent(com.holonplatform.core.property.
	 * Property)
	 */
	@Override
	public <T> Optional<ViewComponent<T>> getViewComponent(Property<T> property) {
		return components.get(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyViewGroup#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return getCurrentValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@Override
	public void setValue(PropertyBox value) {
		final PropertyBox oldValue = getCurrentValue();
		setCurrentValue(value);
		if (value == null) {
			// reset all values
			components.bindings().map(b -> b.getElement()).forEach(c -> c.clear());
		} else {
			components.bindings().forEach(b -> b.getElement().setValue(getPropertyValue(value, b.getProperty())));
		}
		// fire value change
		fireValueChange(oldValue);
	}

	/**
	 * Get the value of given <code>property</code> using given <code>propertyBox</code>.
	 * @param <T> Property type
	 * @param propertyBox PropertyBox
	 * @param property Property
	 * @return Property value
	 */
	protected <T> T getPropertyValue(PropertyBox propertyBox, Property<T> property) {
		if (VirtualProperty.class.isAssignableFrom(property.getClass())) {
			if (((VirtualProperty<T>) property).getValueProvider() != null) {
				return ((VirtualProperty<T>) property).getValueProvider().getPropertyValue(propertyBox);
			}
			return null;
		}
		if (propertyBox.contains(property)) {
			return propertyBox.getValue(property);
		}
		return null;
	}

	/**
	 * Set property as hidden.
	 * @param property Property to set as hidden
	 */
	public void setPropertyHidden(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		configuration.get(property).setHidden(true);
	}

	/**
	 * Get whether given property is hidden.
	 * @param property Property to check
	 * @return whether given property is hidden
	 */
	protected boolean isPropertyHidden(Property<?> property) {
		return property != null && configuration.get(property).isHidden();
	}

	/**
	 * Set the {@link ViewComponentPropertyRenderer} to use with given property
	 * @param <T> Property type
	 * @param property Property
	 * @param renderer Renderer
	 */
	protected <T> void setPropertyRenderer(Property<T> property, PropertyRenderer<ViewComponent<T>, T> renderer) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		configuration.get(property).setRenderer(renderer);
	}

	/**
	 * Build and bind {@link ViewComponent}s to the properties of the property set.
	 */
	@SuppressWarnings("unchecked")
	protected void build() {
		components.clear();
		// render and bind components
		getPropertySet().stream().filter(property -> !isPropertyHidden(property))
				.forEach(property -> renderAndBind(property));
	}

	/**
	 * Render given property as a {@link ViewComponent} and register the binding.
	 * @param <T> Property type
	 * @param property The property to render and bind
	 */
	protected <T> void renderAndBind(final Property<T> property) {
		if (property != null) {
			final ViewComponentPropertyConfiguration<T> propertyConfiguration = configuration.get(property);
			// render
			final ViewComponent<T> component = render(propertyConfiguration)
					.orElseThrow(() -> new NoSuitableRendererAvailableException(
							"No renderer available to render the property [" + property + "] as a ViewComponent"));
			// configure
			getPostProcessors().forEach(postProcessor -> postProcessor.accept(property, component));
			// register
			components.set(property, component);
		}
	}

	/**
	 * Render given property configuration as a {@link ViewComponent}.
	 * @param <T> Property type
	 * @param propertyConfiguration Property configuration
	 * @return Optional rendered component
	 */
	@SuppressWarnings("unchecked")
	protected <T> Optional<ViewComponent<T>> render(ViewComponentPropertyConfiguration<T> propertyConfiguration) {
		// check custom renderer
		Optional<ViewComponent<T>> component = propertyConfiguration.getRenderer()
				.map(r -> r.render(propertyConfiguration.getProperty()));
		if (component.isPresent()) {
			return component;
		}
		// check specific registry
		if (getPropertyRendererRegistry().isPresent()) {
			return getPropertyRendererRegistry().get()
					.getRenderer(ViewComponent.class, propertyConfiguration.getProperty())
					.map(r -> r.render(propertyConfiguration.getProperty()));
		} else {
			// use default
			return propertyConfiguration.getProperty().renderIfAvailable(ViewComponent.class)
					.map(c -> (ViewComponent<T>) c);
		}
	}

	// Builders

	static class InternalBuilder extends AbstractBuilder<DefaultPropertyViewGroup, InternalBuilder> {

		public <P extends Property<?>> InternalBuilder(Iterable<P> properties) {
			super(properties);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.DefaultPropertyViewGroup.AbstractBuilder#builder()
		 */
		@Override
		protected InternalBuilder builder() {
			return this;
		}

		public DefaultPropertyViewGroup build() {
			instance.build();
			return instance;
		}

	}

	public static class DefaultBuilder extends AbstractBuilder<PropertyViewGroup, PropertyViewGroupBuilder>
			implements PropertyViewGroupBuilder {

		public <P extends Property<?>> DefaultBuilder(Iterable<P> properties) {
			super(properties);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.internal.components.DefaultPropertyViewGroup.AbstractBuilder#builder()
		 */
		@Override
		protected PropertyViewGroupBuilder builder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public PropertyViewGroup build() {
			instance.build();
			return instance;
		}

	}

	public static abstract class AbstractBuilder<G extends PropertyViewGroup, B extends PropertyViewGroupConfigurator<G, B>>
			implements PropertyViewGroupConfigurator<G, B> {

		protected final DefaultPropertyViewGroup instance;

		public <P extends Property<?>> AbstractBuilder(Iterable<P> properties) {
			super();
			ObjectUtils.argumentNotNull(properties, "Properties must be not null");
			this.instance = new DefaultPropertyViewGroup(
					(properties instanceof PropertySet<?>) ? (PropertySet<?>) properties : PropertySet.of(properties));
		}

		/**
		 * Actual builder
		 * @return Builder
		 */
		protected abstract B builder();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.components.PropertyViewGroup.Builder#hidden(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> B hidden(Property<T> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyHidden(property);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyViewGroup.Builder#usePropertyRendererRegistry(com.
		 * holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public B usePropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry) {
			instance.setPropertyRendererRegistry(propertyRendererRegistry);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyViewGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> B bind(Property<T> property, PropertyRenderer<ViewComponent<T>, T> renderer) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(renderer, "PropertyRenderer must be not null");
			instance.setPropertyRenderer(property, renderer);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.components.PropertyViewGroup.Builder#withPostProcessor(com.holonplatform.vaadin.
		 * components.PropertyBinding.PostProcessor)
		 */
		@Override
		public B withPostProcessor(BiConsumer<Property<?>, ViewComponent<?>> postProcessor) {
			ObjectUtils.argumentNotNull(postProcessor, "PostProcessor must be not null");
			instance.addPostProcessor(postProcessor);
			return builder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyViewGroup.Builder#withValueChangeListener(com.holonplatform.
		 * vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public B withValueChangeListener(ValueChangeListener<PropertyBox> listener) {
			ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
			instance.addValueChangeListener(listener);
			return builder();
		}

	}

}
