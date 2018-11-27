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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.PropertyViewForm;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.PropertyViewFormBuilder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * Default {@link PropertyViewForm} implementation.
 * 
 * @param <C> Content component type.
 *
 * @since 5.2.0
 */
public class DefaultPropertyViewForm<C extends Component>
		extends AbstractComposablePropertyForm<C, ViewComponent<?>, PropertyViewGroup> implements PropertyViewForm {

	private static final long serialVersionUID = -4202049108110710744L;

	/**
	 * Constructor.
	 * @param content Form content (not null)
	 */
	public DefaultPropertyViewForm(C content) {
		super(content);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyViewSource#getViewComponent(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<ViewComponent<T>> getViewComponent(Property<T> property) {
		return getComponentGroup().getViewComponent(property);
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

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public <P extends Property<?>> DefaultBuilder(C content, Iterable<P> properties) {
			super(content);
			this.instance = new DefaultPropertyViewForm<>(content);
			this.viewGroupBuilder = new DefaultPropertyViewGroup.InternalBuilder(properties);
			// setup default composer
			if (content instanceof HasComponents) {
				instance.setComposer((Composer) Composable.componentContainerComposer());
			}
		}

		@Override
		protected PropertyViewFormBuilder<C> getConfigurator() {
			return this;
		}

		@Override
		public <T> PropertyViewFormBuilder<C> hidden(Property<T> property) {
			viewGroupBuilder.hidden(property);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> usePropertyRendererRegistry(
				PropertyRendererRegistry propertyRendererRegistry) {
			viewGroupBuilder.usePropertyRendererRegistry(propertyRendererRegistry);
			return this;
		}

		@Override
		public <T> PropertyViewFormBuilder<C> bind(Property<T> property,
				PropertyRenderer<ViewComponent<T>, T> renderer) {
			viewGroupBuilder.bind(property, renderer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> withPostProcessor(BiConsumer<Property<?>, ViewComponent<?>> postProcessor) {
			viewGroupBuilder.withPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.PropertyGroupConfigurator#withValueChangeListener(com.
		 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public PropertyViewFormBuilder<C> withValueChangeListener(
				ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, ViewComponent<?>, PropertyViewGroup>> listener) {
			viewGroupBuilder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			instance.setInitializer(initializer);
			return this;
		}

		@Override
		public PropertyViewFormBuilder<C> composer(Composer<? super C, ViewComponent<?>, PropertyViewGroup> composer) {
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
		public PropertyViewForm build() {
			instance.setComponentGroup(viewGroupBuilder.withPostProcessor((property, component) -> {
				instance.configurePropertyComponent(property, component);
			}).build());
			return instance;
		}

	}

}
