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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyInputForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A {@link PropertyInputGroup} component to display the property {@link Input}s on a layout, using the
 * {@link Composable} composition strategy.
 * <p>
 * A {@link Composer} can be configured to control how and where each {@link Input} is displayed in UI.
 * </p>
 * 
 * @since 5.2.0
 */
public interface PropertyInputForm extends Composable, ValueComponent<PropertyBox>, PropertyInputGroup {

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set.
	 * @param <C> Form content element type
	 * @param <P> Property type
	 * @param content The form content, where the {@link Input}s will be composed using the configured {@link Composer}
	 *        (not null)
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyInputFormBuilder}
	 */
	@SuppressWarnings("rawtypes")
	static <C extends Component, P extends Property> PropertyInputFormBuilder<C> builder(C content,
			Iterable<P> properties) {
		return new DefaultPropertyInputForm.DefaultBuilder<>(content, properties);
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set.
	 * @param <C> Form content element type
	 * @param content The form content, where the {@link Input}s will be composed using the configured {@link Composer}
	 *        (not null)
	 * @param properties The property set (not null)
	 * @return A new {@link PropertyInputFormBuilder}
	 */
	static <C extends Component> PropertyInputFormBuilder<C> builder(C content, Property<?>... properties) {
		return new DefaultPropertyInputForm.DefaultBuilder<>(content, PropertySet.of(properties));
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link FormLayout} as content
	 * layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
	 * provide a custom components composer.
	 * </p>
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A {@link PropertyInputForm} builder
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyInputFormBuilder<FormLayout> formLayout(Iterable<P> properties) {
		return builder(FormLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link FormLayout} as content
	 * layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
	 * provide a custom components composer.
	 * </p>
	 * @param properties The property set (not null)
	 * @return A {@link PropertyInputForm} builder
	 */
	static PropertyInputFormBuilder<FormLayout> formLayout(Property<?>... properties) {
		return builder(FormLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link VerticalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
	 * provide a custom components composer.
	 * </p>
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A {@link PropertyInputForm} builder
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyInputFormBuilder<VerticalLayout> verticalLayout(Iterable<P> properties) {
		return builder(VerticalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link VerticalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
	 * provide a custom components composer.
	 * </p>
	 * @param properties The property set (not null)
	 * @return A {@link PropertyInputForm} builder
	 */
	static PropertyInputFormBuilder<VerticalLayout> verticalLayout(Property<?>... properties) {
		return builder(VerticalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link HorizontalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
	 * provide a custom components composer.
	 * </p>
	 * @param <P> Property type
	 * @param properties The property set (not null)
	 * @return A {@link PropertyInputForm} builder
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> PropertyInputFormBuilder<HorizontalLayout> horizontalLayout(Iterable<P> properties) {
		return builder(HorizontalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

	/**
	 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link HorizontalLayout} as
	 * content layout.
	 * <p>
	 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
	 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
	 * provide a custom components composer.
	 * </p>
	 * @param properties The property set (not null)
	 * @return A {@link PropertyInputForm} builder
	 */
	static PropertyInputFormBuilder<HorizontalLayout> horizontalLayout(Property<?>... properties) {
		return builder(HorizontalLayoutBuilder.create().build(), properties)
				.composer(Composable.componentContainerComposer());
	}

}
