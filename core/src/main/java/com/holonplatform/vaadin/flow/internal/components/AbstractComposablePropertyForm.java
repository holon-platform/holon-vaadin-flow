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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.Registration;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.BoundComponentGroup;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValueComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;

/**
 * Base {@link Composable} property form component.
 * 
 * @param <C> Content component type
 * @param <E> Elements type
 * @param <G> Elements group type
 * 
 * @since 5.2.0
 */
public abstract class AbstractComposablePropertyForm<C extends Component, E extends HasComponent, G extends BoundComponentGroup<Property<?>, E> & ValueHolder<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, E, G>>>
		extends AbstractComposable<C, E, G> implements BoundComponentGroup<Property<?>, E>,
		ValueHolder<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, E, G>>, ValueComponent<PropertyBox> {

	private static final long serialVersionUID = -2331872101418117289L;

	/**
	 * Backing group
	 */
	private G componentGroup;

	/**
	 * Custom property captions
	 */
	private final transient Map<Property<?>, Localizable> propertyCaptions = new HashMap<>(8);

	/**
	 * Hidden property captions
	 */
	private final Collection<Property<?>> hiddenPropertyCaptions = new HashSet<>(8);

	/**
	 * Constructor.
	 * @param content Form composition content (not null)
	 */
	public AbstractComposablePropertyForm(C content) {
		super(content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return getContent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.AbstractComposable#getComponentGroup()
	 */
	@Override
	protected G getComponentGroup() {
		return componentGroup;
	}

	/**
	 * Set the backing group.
	 * @param componentGroup the component group to set (not null)
	 */
	protected void setComponentGroup(G componentGroup) {
		ObjectUtils.argumentNotNull(componentGroup, "Component group must be not null");
		this.componentGroup = componentGroup;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ComponentGroup#getElements()
	 */
	@Override
	public Stream<E> getElements() {
		return getComponentGroup().getElements();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup#getBindings()
	 */
	@Override
	public Stream<Binding<Property<?>, E>> getBindings() {
		return getComponentGroup().getBindings();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.BoundComponentGroup#getElement(java.lang.Object)
	 */
	@Override
	public Optional<E> getElement(Property<?> property) {
		return getComponentGroup().getElement(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasPropertySet#getProperties()
	 */
	@Override
	public Collection<Property<?>> getProperties() {
		return getComponentGroup().getProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(PropertyBox value) {
		getComponentGroup().setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return getComponentGroup().getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValueIfPresent()
	 */
	@Override
	public Optional<PropertyBox> getValueIfPresent() {
		return getComponentGroup().getValueIfPresent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public PropertyBox getEmptyValue() {
		return getComponentGroup().getEmptyValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getComponentGroup().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		getComponentGroup().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.flow.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, E, G>> listener) {
		return getComponentGroup().addValueChangeListener(listener);
	}

	/**
	 * Set the caption for the component bound to given property
	 * @param property Property
	 * @param caption Localizable caption
	 */
	protected void setPropertyCaption(Property<?> property, Localizable caption) {
		if (property != null && caption != null) {
			propertyCaptions.put(property, caption);
		}
	}

	/**
	 * Set the caption for the component bound to given property as hidden
	 * @param property Property
	 */
	protected void hidePropertyCaption(Property<?> property) {
		if (property != null) {
			hiddenPropertyCaptions.add(property);
		}
	}

	/**
	 * Configure given component using given property.
	 * @param property Property to which the component refers
	 * @param component Component to configure
	 */
	protected void configurePropertyComponent(Property<?> property, E component) {
		if (component != null) {
			component.hasLabel().ifPresent(hasLabel -> {
				if (hiddenPropertyCaptions.contains(property)) {
					hasLabel.setLabel(null);
				} else {
					if (propertyCaptions.containsKey(property)) {
						LocalizationProvider.localize(propertyCaptions.get(property))
								.ifPresent(message -> hasLabel.setLabel(message));
					} else {
						if (hasLabel.getLabel() == null || hasLabel.getLabel().trim().equals("")) {
							// default behaviour
							hasLabel.setLabel(
									LocalizationProvider.localize(property).orElseGet(() -> property.getName()));
						}
					}
				}
			});
		}
	}

}
