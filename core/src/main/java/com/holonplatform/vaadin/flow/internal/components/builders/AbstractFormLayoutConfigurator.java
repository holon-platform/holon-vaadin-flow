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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;

/**
 * Base {@link FormLayoutConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public abstract class AbstractFormLayoutConfigurator<C extends FormLayoutConfigurator<C>>
		extends AbstractComponentConfigurator<FormLayout, C> implements FormLayoutConfigurator<C> {

	public AbstractFormLayoutConfigurator(FormLayout component) {
		super(component);
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasComponentsConfigurator#add(com.vaadin.flow.component.
	 * Component[])
	 */
	@Override
	public C add(Component... components) {
		getComponent().add(components);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator#responsiveSteps(java.util.List)
	 */
	@Override
	public C responsiveSteps(List<ResponsiveStep> steps) {
		getComponent().setResponsiveSteps(steps);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator#withFormItem(com.vaadin.flow.component.
	 * Component, com.vaadin.flow.component.Component, java.util.function.Consumer)
	 */
	@Override
	public C withFormItem(Component field, Component label, Consumer<FormItem> formItem) {
		final FormItem item = getComponent().addFormItem(field, label);
		if (formItem != null) {
			formItem.accept(item);
		}
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator#withFormItem(com.vaadin.flow.component.
	 * Component, java.lang.String, java.util.function.Consumer)
	 */
	@Override
	public C withFormItem(Component field, String label, Consumer<FormItem> formItem) {
		final FormItem item = getComponent().addFormItem(field, label);
		if (formItem != null) {
			formItem.accept(item);
		}
		return getConfigurator();
	}

}
