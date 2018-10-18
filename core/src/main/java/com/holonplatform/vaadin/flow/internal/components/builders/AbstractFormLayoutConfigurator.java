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
import java.util.function.Consumer;

import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator;
import com.vaadin.flow.component.Component;
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

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;

	public AbstractFormLayoutConfigurator(FormLayout component) {
		super(component);

		this.sizeConfigurator = new DefaultHasSizeConfigurator(component);
		this.styleConfigurator = new DefaultHasStyleConfigurator(component);
		this.enabledConfigurator = new DefaultHasEnabledConfigurator(component);
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public C enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public C removeStyleName(String styleName) {
		styleConfigurator.removeStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public C replaceStyleName(String styleName) {
		styleConfigurator.replaceStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public C width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public C height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

}
