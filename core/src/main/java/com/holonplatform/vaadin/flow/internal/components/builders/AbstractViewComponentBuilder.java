/*
 * Copyright 2016-2019 Axioma srl.
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

import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.AbstractViewComponent;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;

/**
 * Base {@link ViewComponent} builder.
 * 
 * @param <C> Content type
 * @param <T> Value type
 * @param <V> View component type
 * @param <B> Concrete builder type
 * 
 * @since 5.2.2
 */
public abstract class AbstractViewComponentBuilder<C extends Component, T, V extends AbstractViewComponent<C, T>, B extends ViewComponentConfigurator<T, B>>
		extends AbstractLocalizableComponentConfigurator<V, B> implements ViewComponentConfigurator<T, B> {

	protected final DefaultHasLabelConfigurator<V> labelConfigurator;
	protected final DefaultHasTitleConfigurator<V> titleConfigurator;

	/**
	 * Constructor.
	 * @param component Concrete component (not null)
	 */
	public AbstractViewComponentBuilder(V component) {
		super(component);
		this.labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(),
				label -> getComponent().setLabelText(label), this);
		this.titleConfigurator = new DefaultHasTitleConfigurator<>(getComponent(), title -> {
			getComponent().getElement().setAttribute("title", (title != null) ? title : "");
		}, this);
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
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentConfigurator#labelVisible(boolean)
	 */
	@Override
	public B labelVisible(boolean visible) {
		getComponent().setLabelVisible(visible);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public B label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickListener(com.holonplatform.
	 * vaadin.flow.components.events.ClickEventListener)
	 */
	@Override
	public B withClickListener(ClickEventListener<ViewComponent<T>, ClickEvent<ViewComponent<T>>> clickEventListener) {
		getComponent()
				.addClickListener(ComponentClickListenerAdapter.forClickNotifier(getComponent(), clickEventListener));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public B withValue(T value) {
		getComponent().setValue(value, false);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ViewComponentConfigurator#withValueChangeListener(com.
	 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public B withValueChangeListener(ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		getComponent().addValueChangeListener(listener);
		return getConfigurator();
	}

}
