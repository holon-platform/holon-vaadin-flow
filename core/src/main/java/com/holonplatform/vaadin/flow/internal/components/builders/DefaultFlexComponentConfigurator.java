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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.dom.DomEventListener;

/**
 * Default {@link FlexComponentConfigurator} implementation.
 * @param <C> Actual component type
 * @since 5.2.0
 */
public class DefaultFlexComponentConfigurator<C extends Component>
		implements FlexComponentConfigurator<DefaultFlexComponentConfigurator<C>> {

	private final FlexComponent component;

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;

	public DefaultFlexComponentConfigurator(FlexComponent component) {
		super();
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;

		this.sizeConfigurator = new DefaultHasSizeConfigurator(component);
		this.styleConfigurator = new DefaultHasStyleConfigurator(component);
		this.enabledConfigurator = new DefaultHasEnabledConfigurator(component);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasComponentsConfigurator#add(com.vaadin.flow.component.
	 * Component[])
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> add(Component... components) {
		component.add(components);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withThemeName(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> withThemeName(String themName) {
		component.getElement().getThemeList().add(themName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> withEventListener(String eventType, DomEventListener listener) {
		component.getElement().addEventListener(eventType, listener);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasElementConfigurator#withEventListener(java.lang.String,
	 * com.vaadin.flow.dom.DomEventListener, java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> withEventListener(String eventType, DomEventListener listener,
			String filter) {
		component.getElement().addEventListener(eventType, listener).setFilter(filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> width(String width) {
		sizeConfigurator.width(width);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> height(String height) {
		sizeConfigurator.height(height);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minWidth(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> minWidth(String minWidth) {
		sizeConfigurator.minWidth(minWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxWidth(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> maxWidth(String maxWidth) {
		sizeConfigurator.maxWidth(maxWidth);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#minHeight(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> minHeight(String minHeight) {
		sizeConfigurator.minHeight(minHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#maxHeight(java.lang.String)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> maxHeight(String maxHeight) {
		sizeConfigurator.maxHeight(maxHeight);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#alignItems(com.vaadin.flow.
	 * component. orderedlayout.FlexComponent.Alignment)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> alignItems(Alignment alignment) {
		component.setAlignItems(alignment);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#alignSelf(com.vaadin.flow.
	 * component. orderedlayout.FlexComponent.Alignment, com.vaadin.flow.component.HasElement[])
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> alignSelf(Alignment alignment, HasElement... elementContainers) {
		component.setAlignSelf(alignment, elementContainers);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#flexGrow(double,
	 * com.vaadin.flow.component.HasElement[])
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> flexGrow(double flexGrow, HasElement... elementContainers) {
		component.setFlexGrow(flexGrow, elementContainers);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#justifyContentMode(com.vaadin.
	 * flow. component.orderedlayout.FlexComponent.JustifyContentMode)
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> justifyContentMode(JustifyContentMode justifyContentMode) {
		component.setJustifyContentMode(justifyContentMode);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#expand(com.vaadin.flow.component.
	 * Component[])
	 */
	@Override
	public DefaultFlexComponentConfigurator<C> expand(Component... componentsToExpand) {
		component.expand(componentsToExpand);
		return this;
	}

}
