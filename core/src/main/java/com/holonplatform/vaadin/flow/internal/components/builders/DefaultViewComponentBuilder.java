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

import java.util.function.Function;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.presentation.StringValuePresenter;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultViewComponent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Default {@link ViewComponentBuilder} implementation.
 *
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public class DefaultViewComponentBuilder<T>
		extends AbstractLocalizableComponentConfigurator<DefaultViewComponent<T>, ViewComponentBuilder<T>>
		implements ViewComponentBuilder<T> {

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasLabelConfigurator labelConfigurator;
	protected final DefaultHasTitleConfigurator<DefaultViewComponent<T>> titleConfigurator;

	/**
	 * Constructor which uses a {@link StringValuePresenter} as value converter.
	 * @param type Value type (not null)
	 */
	public DefaultViewComponentBuilder(Class<? extends T> type) {
		this(v -> StringValuePresenter.getDefault().present(type, v, null));
	}

	/**
	 * Constructor which uses given <code>property</code> for value presentation and configuration parameters source.
	 * The {@link Property#present(Object)} method will be called when value presentation is required.
	 * @param property The property to use (not null)
	 */
	public DefaultViewComponentBuilder(Property<T> property) {
		this(v -> property.present(v));
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		label(property);
	}

	/**
	 * Constructor.
	 * @param stringValueConverter Value converter (not null)
	 */
	public DefaultViewComponentBuilder(Function<T, String> stringValueConverter) {
		super(new DefaultViewComponent<>(stringValueConverter));
		this.sizeConfigurator = new DefaultHasSizeConfigurator(getComponent());
		this.styleConfigurator = new DefaultHasStyleConfigurator(getComponent());
		this.enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
		this.labelConfigurator = new DefaultHasLabelConfigurator(getComponent());
		this.titleConfigurator = new DefaultHasTitleConfigurator<>(getComponent(), title -> {
			getComponent().getElement().setAttribute("title", (title != null) ? title : "");
		}, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ViewComponentBuilder<T> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public ViewComponentBuilder<T> width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public ViewComponentBuilder<T> height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public ViewComponentBuilder<T> styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public ViewComponentBuilder<T> styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public ViewComponentBuilder<T> removeStyleName(String styleName) {
		styleConfigurator.removeStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public ViewComponentBuilder<T> replaceStyleName(String styleName) {
		styleConfigurator.replaceStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public ViewComponentBuilder<T> title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public ViewComponentBuilder<T> enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public ViewComponentBuilder<T> label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public ViewComponentBuilder<T> withClickListener(ComponentEventListener<ClickEvent<Component>> listener) {
		getComponent().addClickListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder#withValue(java.lang.Object)
	 */
	@Override
	public ViewComponentBuilder<T> withValue(T value) {
		getComponent().setValue(value);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public ViewComponentBuilder<T> withValueChangeListener(ValueChangeListener<T> listener) {
		getComponent().addValueChangeListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder#build()
	 */
	@Override
	public ViewComponent<T> build() {
		return getComponent();
	}

}
