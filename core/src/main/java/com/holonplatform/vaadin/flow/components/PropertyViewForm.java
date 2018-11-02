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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyViewForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A {@link PropertyViewGroup} component to display the property {@link ViewComponent}s on a layout, using the
 * {@link Composable} composition strategy.
 * <p>
 * A {@link Composer} can be configured to control how and where each {@link ViewComponent} is displayed in UI.
 * </p>
 * 
 * @since 5.2.0
 */
public interface PropertyViewForm extends Composable, ValueComponent<PropertyBox>, PropertyViewGroup {

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set.
	 * @param <C> Form content element type
	 * @param <P> Property type
	 * @param content The form content, where the {@link ViewComponent}s will be composed using the configured
	 *        {@link Composer} (not null)
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyViewFormBuilder}
	 */
	@SuppressWarnings("rawtypes")
	static <C extends Component, P extends Property> PropertyViewFormBuilder<C> builder(C content,
			Iterable<P> properties) {
		return new DefaultPropertyViewForm.DefaultBuilder<>(content, properties);
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set.
	 * @param <C> Form content element type
	 * @param content The form content, where the {@link ViewComponent}s will be composed using the configured
	 *        {@link Composer} (not null)
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyViewFormBuilder}
	 */
	static <C extends Component> PropertyViewFormBuilder<C> builder(C content, Property<?>... properties) {
		return new DefaultPropertyViewForm.DefaultBuilder<>(content, PropertySet.of(properties));
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link FormLayout} as content
	 * layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to provide
	 * a custom components composer.
	 * </p>
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A {@link PropertyViewForm} builder
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyViewFormBuilder<FormLayout> formLayout(Iterable<P> properties) {
		return builder(FormLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link FormLayout} as content
	 * layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to provide
	 * a custom components composer.
	 * </p>
	 * @param properties The property set (not null)
	 * @return A {@link PropertyViewForm} builder
	 */
	static PropertyViewFormBuilder<FormLayout> formLayout(Property<?>... properties) {
		return builder(FormLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link VerticalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to provide
	 * a custom components composer.
	 * </p>
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A {@link PropertyViewForm} builder
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyViewFormBuilder<VerticalLayout> verticalLayout(Iterable<P> properties) {
		return builder(VerticalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link VerticalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to provide
	 * a custom components composer.
	 * </p>
	 * @param properties The property set (not null)
	 * @return A {@link PropertyViewForm} builder
	 */
	static PropertyViewFormBuilder<VerticalLayout> verticalLayout(Property<?>... properties) {
		return builder(VerticalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link HorizontalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to provide
	 * a custom components composer.
	 * </p>
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A {@link PropertyViewForm} builder
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyViewFormBuilder<HorizontalLayout> horizontalLayout(Iterable<P> properties) {
		return builder(HorizontalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link HorizontalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to provide
	 * a custom components composer.
	 * </p>
	 * @param properties The property set (not null)
	 * @return A {@link PropertyViewForm} builder
	 */
	static PropertyViewFormBuilder<HorizontalLayout> horizontalLayout(Property<?>... properties) {
		return builder(HorizontalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * {@link PropertyViewForm} builder.
	 * @param <C> Form content element type
	 */
	public interface PropertyViewFormBuilder<C extends Component>
			extends Composable.Builder<PropertyValueComponentSource, C, PropertyViewFormBuilder<C>>,
			PropertyViewGroup.Builder<PropertyViewForm, PropertyViewFormBuilder<C>>,
			ComponentConfigurator<PropertyViewFormBuilder<C>> {

		/**
		 * Set the caption for the view component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the view component caption (not null)
		 * @param caption Localizable view component caption
		 * @return this
		 */
		PropertyViewFormBuilder<C> propertyCaption(Property<?> property, Localizable caption);

		/**
		 * Set the caption for the view component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the view component caption (not null)
		 * @param caption View component caption
		 * @return this
		 */
		PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String caption);

		/**
		 * Set the caption for the view component bound to given property. By default, the caption is obtained from
		 * {@link Property} itself (which is {@link Localizable}).
		 * @param property Property for which to set the view component caption (not null)
		 * @param defaultCaption Default caption if no translation is available for given <code>messageCode</code> for
		 *        current Locale, or no {@link LocalizationContext} is available at all
		 * @param messageCode Caption translation message key
		 * @param arguments Optional translation arguments
		 * @return this
		 */
		PropertyViewFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption, String messageCode,
				Object... arguments);

		/**
		 * Set the caption for the view component bound to given property as hidden.
		 * @param property Property for which to hide the view component caption (not null)
		 * @return this
		 */
		PropertyViewFormBuilder<C> hidePropertyCaption(Property<?> property);

	}

}
