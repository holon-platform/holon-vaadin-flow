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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyViewFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyViewForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A {@link PropertyViewGroup} which provides an UI component to display the group elements, which can be composed using
 * a {@link Composer}.
 * <p>
 * The group elements {@link Composer} can be configured using the
 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} builder
 * method.
 * </p>
 * 
 * @since 5.2.0
 * @see PropertyViewGroup
 */
public interface PropertyViewForm extends PropertyViewGroup, ValueComponent<PropertyBox>, Composable {

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

}
