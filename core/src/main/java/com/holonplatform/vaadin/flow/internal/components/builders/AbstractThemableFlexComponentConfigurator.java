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

import java.util.Optional;

import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;

/**
 * Default {@link ThemableFlexComponentConfigurator} implementation.
 *
 * @since 5.2.0
 */
public abstract class AbstractThemableFlexComponentConfigurator<L extends Component & FlexComponent<L> & ThemableLayout, C extends ThemableFlexComponentConfigurator<C>>
		extends AbstractComponentConfigurator<L, C> implements ThemableFlexComponentConfigurator<C> {

	private final DefaultFlexComponentConfigurator<L> flexComponentConfigurator;
	private final DefaultThemableLayoutConfigurator themableLayoutConfigurator;

	public AbstractThemableFlexComponentConfigurator(L component) {
		super(component);
		flexComponentConfigurator = new DefaultFlexComponentConfigurator<>(component);
		themableLayoutConfigurator = new DefaultThemableLayoutConfigurator(component);
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
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#margin(boolean)
	 */
	@Override
	public C margin(boolean margin) {
		themableLayoutConfigurator.margin(margin);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#padding(boolean)
	 */
	@Override
	public C padding(boolean padding) {
		themableLayoutConfigurator.padding(padding);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#spacing(boolean)
	 */
	@Override
	public C spacing(boolean spacing) {
		themableLayoutConfigurator.spacing(spacing);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ThemableLayoutConfigurator#boxSizing(com.vaadin.flow.component.
	 * orderedlayout.BoxSizing)
	 */
	@Override
	public C boxSizing(BoxSizing boxSizing) {
		themableLayoutConfigurator.boxSizing(boxSizing);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#alignItems(com.vaadin.flow.component.
	 * orderedlayout.FlexComponent.Alignment)
	 */
	@Override
	public C alignItems(Alignment alignment) {
		flexComponentConfigurator.alignItems(alignment);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#alignSelf(com.vaadin.flow.component.
	 * orderedlayout.FlexComponent.Alignment, com.vaadin.flow.component.HasElement[])
	 */
	@Override
	public C alignSelf(Alignment alignment, HasElement... elementContainers) {
		flexComponentConfigurator.alignSelf(alignment, elementContainers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#flexGrow(double,
	 * com.vaadin.flow.component.HasElement[])
	 */
	@Override
	public C flexGrow(double flexGrow, HasElement... elementContainers) {
		flexComponentConfigurator.flexGrow(flexGrow, elementContainers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#justifyContentMode(com.vaadin.flow.
	 * component.orderedlayout.FlexComponent.JustifyContentMode)
	 */
	@Override
	public C justifyContentMode(JustifyContentMode justifyContentMode) {
		flexComponentConfigurator.justifyContentMode(justifyContentMode);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.FlexComponentConfigurator#expand(com.vaadin.flow.component.
	 * Component[])
	 */
	@Override
	public C expand(Component... componentsToExpand) {
		flexComponentConfigurator.expand(componentsToExpand);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		flexComponentConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		flexComponentConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public C width(String width) {
		flexComponentConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public C height(String height) {
		flexComponentConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasComponentsConfigurator#add(com.vaadin.flow.component.
	 * Component[])
	 */
	@Override
	public C add(Component... components) {
		flexComponentConfigurator.add(components);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public C enabled(boolean enabled) {
		flexComponentConfigurator.enabled(enabled);
		return getConfigurator();
	}

}
