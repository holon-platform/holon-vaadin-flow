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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;

/**
 * Configurator for {@link FormLayout} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface FormLayoutConfigurator<C extends FormLayoutConfigurator<C>> extends HasComponentsConfigurator<C>,
		HasStyleConfigurator<C>, HasSizeConfigurator<C>, ComponentConfigurator<C> {

	/**
	 * Configure the responsive steps used in this layout.
	 * @see ResponsiveStep
	 * @param steps list of {@link ResponsiveStep}s to set
	 * @return this
	 */
	C responsiveSteps(List<ResponsiveStep> steps);

	/**
	 * Configure the responsive steps used in this layout.
	 * @see ResponsiveStep
	 * @param steps list of {@link ResponsiveStep}s to set
	 * @return this
	 */
	default C responsiveSteps(ResponsiveStep... steps) {
		return responsiveSteps(Arrays.asList(steps));
	}

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label component to set
	 * @param formItem Optional {@link Consumer} to configure the created {@link FormItem} instance
	 * @return this
	 */
	C withFormItem(Component field, Component label, Consumer<FormItem> formItem);

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap (not null)
	 * @param label the label component to set
	 * @param formItem Optional {@link Consumer} to configure the created {@link FormItem} instance
	 * @return this
	 */
	default C withFormItem(HasComponent field, Component label, Consumer<FormItem> formItem) {
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		return withFormItem(field.getComponent(), label, formItem);
	}

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label component to set
	 * @return this
	 */
	default C withFormItem(Component field, Component label) {
		return withFormItem(field, label, i -> {
		});
	}

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label component to set
	 * @return this
	 */
	default C withFormItem(HasComponent field, Component label) {
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		return withFormItem(field.getComponent(), label);
	}

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label text to set
	 * @param formItem Optional {@link Consumer} to configure the created {@link FormItem} instance
	 * @return this
	 */
	C withFormItem(Component field, String label, Consumer<FormItem> formItem);

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label text to set
	 * @param formItem Optional {@link Consumer} to configure the created {@link FormItem} instance
	 * @return this
	 */
	default C withFormItem(HasComponent field, String label, Consumer<FormItem> formItem) {
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		return withFormItem(field.getComponent(), label, formItem);
	}

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label text to set
	 * @return this
	 */
	default C withFormItem(Component field, String label) {
		return withFormItem(field, label, i -> {
		});
	}

	/**
	 * Create and add a new {@link FormItem} to this layout that wraps the given field with a label.
	 * @param field the field component to wrap
	 * @param label the label text to set
	 * @return this
	 */
	default C withFormItem(HasComponent field, String label) {
		ObjectUtils.argumentNotNull(field, "Field must be not null");
		return withFormItem(field.getComponent(), label);
	}

	/**
	 * Get a new {@link FormLayoutConfigurator} for given component.
	 * @param component The component to configure (not null)
	 * @return A new {@link FormLayoutConfigurator}
	 */
	static BaseFormLayoutConfigurator configure(FormLayout component) {
		return new DefaultFormLayoutConfigurator(component);
	}

	/**
	 * Base configurator.
	 */
	public interface BaseFormLayoutConfigurator extends FormLayoutConfigurator<BaseFormLayoutConfigurator> {

	}

}
