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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.BoundComponentGroup;
import com.holonplatform.vaadin.flow.components.HasPropertySet;
import com.holonplatform.vaadin.flow.components.ValueComponent;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultGroupValueChangeEvent;
import com.vaadin.flow.shared.Registration;

/**
 * Base {@link HasPropertySet} property components group implementation.
 * 
 * @param <C> Component type
 * @param <G> Group type
 *
 * @since 5.2.0
 */
public abstract class AbstractPropertySetGroup<C extends ValueComponent<?>, G extends BoundComponentGroup<Property<?>, C>>
		implements HasPropertySet<Property<?>>,
		ValueHolder<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, C, G>> {

	private static final long serialVersionUID = 5966779573345769968L;

	/**
	 * Current value
	 */
	private PropertyBox value;

	/**
	 * Property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Value change listeners
	 */
	private final List<ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, C, G>>> valueChangeListeners = new LinkedList<>();

	/**
	 * Optional {@link PropertyRendererRegistry} to use
	 */
	private PropertyRendererRegistry propertyRendererRegistry;

	/**
	 * Post-processors
	 */
	private final List<BiConsumer<Property<?>, C>> postProcessors = new LinkedList<>();

	/**
	 * Constructor.
	 * @param propertySet The property set (not null)
	 */
	public AbstractPropertySetGroup(PropertySet<?> propertySet) {
		super();
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
	}

	/**
	 * Get the actual component group.
	 * @return the component group
	 */
	protected abstract G getComponentGroup();

	/**
	 * Get the property set.
	 * @return the property set
	 */
	protected PropertySet<?> getPropertySet() {
		return propertySet;
	}

	/**
	 * Get the current group value, if present.
	 * @return Optional value
	 */
	protected Optional<PropertyBox> getCurrentValueIfPresent() {
		return Optional.ofNullable(getCurrentValue());
	}

	/**
	 * Get the current group value.
	 * @return the value
	 */
	protected PropertyBox getCurrentValue() {
		return value;
	}

	/**
	 * Set the current group value.
	 * @param value the value to set
	 */
	protected void setCurrentValue(PropertyBox value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasPropertySet#getProperties()
	 */
	@Override
	public Collection<Property<?>> getProperties() {
		return getPropertySet().stream().map(p -> (Property<?>) p).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		setValue(null);
	}

	/**
	 * Get the {@link ValueChangeListener}s.
	 * @return the value change listeners
	 */
	protected List<ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, C, G>>> getValueChangeListeners() {
		return valueChangeListeners;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.flow.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, C, G>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> valueChangeListeners.remove(listener);
	}

	/**
	 * Emits the value change events.
	 * @param oldValue Old value
	 */
	protected void fireValueChange(PropertyBox oldValue) {
		final GroupValueChangeEvent<PropertyBox, Property<?>, C, G> valueChangeEvent = new DefaultGroupValueChangeEvent<>(
				getComponentGroup(), this, oldValue, getCurrentValue(), false);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

	/**
	 * Get the specific {@link PropertyRendererRegistry} to use to render the components.
	 * @return Optional property renderer registry
	 */
	protected Optional<PropertyRendererRegistry> getPropertyRendererRegistry() {
		return Optional.ofNullable(propertyRendererRegistry);
	}

	/**
	 * Set the specific {@link PropertyRendererRegistry} to use to render the components.
	 * @param propertyRendererRegistry the property renderer registry to set
	 */
	protected void setPropertyRendererRegistry(PropertyRendererRegistry propertyRendererRegistry) {
		this.propertyRendererRegistry = propertyRendererRegistry;
	}

	/**
	 * Register a value component post-processor.
	 * @param postProcessor the post-processor to register (not null)
	 */
	protected void addPostProcessor(BiConsumer<Property<?>, C> postProcessor) {
		ObjectUtils.argumentNotNull(postProcessor, "Post processor must be not null");
		postProcessors.add(postProcessor);
	}

	/**
	 * Get the value component post-processors.
	 * @return the post processors
	 */
	protected List<BiConsumer<Property<?>, C>> getPostProcessors() {
		return postProcessors;
	}

}
