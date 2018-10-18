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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.PropertyBinding;
import com.holonplatform.vaadin.flow.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.flow.components.PropertyViewForm;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.shared.Registration;

/**
 * Default {@link PropertyViewForm} implementation.
 * 
 * @param <C> Content component type.
 *
 * @since 5.2.0
 */
public class DefaultPropertyViewForm<C extends Component> extends
		AbstractComposablePropertyForm<C, ViewComponent<?>, PropertyValueComponentSource> implements PropertyViewForm {

	private static final long serialVersionUID = -4202049108110710744L;

	/**
	 * Backing view group
	 */
	private PropertyViewGroup viewGroup;

	/**
	 * Value components source
	 */
	private PropertyValueComponentSource valueComponentSource;

	/**
	 * Constructor.
	 * @param content Form content (not null)
	 */
	public DefaultPropertyViewForm(C content) {
		super(content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractComposableForm#getComponentSource()
	 */
	@Override
	protected PropertyValueComponentSource getComponentSource() {
		return valueComponentSource;
	}

	/**
	 * Sets the backing view group.
	 * @param <G> PropertyViewGroup type
	 * @param viewGroup the view group to set
	 */
	protected <G extends PropertyViewGroup & PropertyValueComponentSource> void setViewGroup(G viewGroup) {
		ObjectUtils.argumentNotNull(viewGroup, "PropertyViewGroup must be not null");
		this.viewGroup = viewGroup;
		this.valueComponentSource = viewGroup;
	}

	/**
	 * Get the backing view group.
	 * @return the backing view group
	 */
	protected PropertyViewGroup getViewGroup() {
		return viewGroup;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		getViewGroup().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return getViewGroup().getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@Override
	public void setValue(PropertyBox propertyBox) {
		getViewGroup().setValue(propertyBox);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return getViewGroup().getProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		return getViewGroup().hasProperty(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> propertyStream() {
		return getViewGroup().propertyStream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getViewGroup().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		return getViewGroup().addValueChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#getViewComponents()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ViewComponent> getViewComponents() {
		return getViewGroup().getViewComponents();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyViewSource#getViewComponent(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<ViewComponent<T>> getViewComponent(Property<T> property) {
		return getViewGroup().getViewComponent(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#stream()
	 */
	@Override
	public <T> Stream<PropertyBinding<T, ViewComponent<T>>> stream() {
		return getViewGroup().stream();
	}

	// Builder

	/**
	 * Default {@link PropertyViewFormBuilder}.
	 * @param <C> Content type
	 */
	public static class DefaultBuilder<C extends Component>
			extends AbstractComponentConfigurator<C, PropertyViewFormBuilder<C>> implements PropertyViewFormBuilder<C> {

		private final DefaultPropertyViewForm<C> instance;

		private final DefaultPropertyViewGroup.InternalBuilder viewGroupBuilder;

		/**
		 * Constructor
		 * @param content Form composition content
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public DefaultBuilder(C content) {
			super(content);
			this.instance = new DefaultPropertyViewForm<>(content);
			this.viewGroupBuilder = new DefaultPropertyViewGroup.InternalBuilder();

			// setup default composer
			if (instance.getComposer() == null && content instanceof HasComponents) {
				instance.setComposer((Composer) Composable.componentContainerComposer());
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
		 */
		@Override
		protected PropertyViewFormBuilder<C> getConfigurator() {
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> properties(Property<?>... properties) {
			viewGroupBuilder.properties(properties);
			return this;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public <P extends Property> PropertyViewFormBuilder<C> properties(Iterable<P> properties) {
			viewGroupBuilder.properties(properties);
			return this;
		}

		@Override
		public <T> PropertyViewFormBuilder<C> hidden(Property<T> property) {
			viewGroupBuilder.hidden(property);
			return this;
		}

		@Override
		public <T, F extends T> PropertyViewFormBuilder<C> bind(Property<T> property,
				PropertyRenderer<ViewComponent<F>, T> renderer) {
			viewGroupBuilder.bind(property, renderer);
			return this;
		}

		@Override
		public <T> PropertyViewFormBuilder<C> bind(Property<T> property, ViewComponentPropertyRenderer<T> renderer) {
			viewGroupBuilder.bind(property, renderer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> withPostProcessor(BiConsumer<Property<?>, ViewComponent<?>> postProcessor) {
			viewGroupBuilder.withPostProcessor(postProcessor);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> withValueChangeListener(ValueChangeListener<PropertyBox> listener) {
			viewGroupBuilder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> ignoreMissingViewComponents(boolean ignoreMissingViewComponents) {
			viewGroupBuilder.ignoreMissingViewComponents(ignoreMissingViewComponents);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			instance.setInitializer(initializer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> composer(Composer<? super C, PropertyValueComponentSource> composer) {
			ObjectUtils.argumentNotNull(composer, "Composer must be not null");
			instance.setComposer(composer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> composeOnAttach(boolean composeOnAttach) {
			instance.setComposeOnAttach(composeOnAttach);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> componentsWidthMode(ComponentsWidthMode componentsWidthMode) {
			instance.setComponentsWidthMode(componentsWidthMode);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> propertyCaption(Property<?> property, Localizable caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(caption, "Caption must be not null");
			instance.setPropertyCaption(property, caption);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyCaption(property, Localizable.builder().message(caption).build());
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption,
				String messageCode, Object... arguments) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyCaption(property, Localizable.builder().message(defaultCaption).messageCode(messageCode)
					.messageArguments(arguments).build());
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> hidePropertyCaption(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.hidePropertyCaption(property);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> componentConfigurator(Property<?> property,
				Consumer<BaseComponentConfigurator> configurator) {
			instance.setPropertyComponentConfigurator(property, configurator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public PropertyViewForm build() {
			instance.setViewGroup(viewGroupBuilder.withPostProcessor((property, component) -> {
				instance.configurePropertyComponent(property, component);
			}).build());
			return instance;
		}

	}

}
