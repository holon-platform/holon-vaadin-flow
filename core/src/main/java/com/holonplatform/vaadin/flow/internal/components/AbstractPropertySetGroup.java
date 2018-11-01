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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.PropertySetBound;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.vaadin.flow.shared.Registration;

/**
 * Base {@link PropertySetBound} property components group implementation.
 *
 * @since 5.2.0
 */
public abstract class AbstractPropertySetGroup implements PropertySetBound, ValueHolder<PropertyBox> {

	private static final long serialVersionUID = 5966779573345769968L;

	/**
	 * Current value
	 */
	protected PropertyBox value;

	/**
	 * Property set
	 */
	@SuppressWarnings("rawtypes")
	protected final List<Property> properties = new LinkedList<>();

	/**
	 * Value change listeners
	 */
	protected final List<ValueChangeListener<PropertyBox>> valueChangeListeners = new LinkedList<>();

	/**
	 * Add a property to the property set
	 * @param property Property to add
	 */
	@SuppressWarnings("rawtypes")
	protected void addProperty(Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#getProperties()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<Property> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		return properties.contains(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Stream<Property> propertyStream() {
		return properties.stream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		final PropertyBox value = getValue();
		return (value == null) || (!value.propertyValues().filter(v -> v.hasValue()).findAny().isPresent());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		setValue(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.flow.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> valueChangeListeners.remove(listener);
	}

	/**
	 * Emits the value change event
	 * @param oldValue Old value
	 * @param value the changed value
	 */
	protected void fireValueChange(PropertyBox oldValue, PropertyBox value) {
		final ValueChangeEvent<PropertyBox> valueChangeEvent = new DefaultValueChangeEvent<>(this, oldValue, value,
				false);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

}
