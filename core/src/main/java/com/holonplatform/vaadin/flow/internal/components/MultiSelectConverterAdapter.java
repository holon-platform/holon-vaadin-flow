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
import java.util.Set;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input.InputValueConversionException;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

/**
 * Adapter for {@link MultiSelect} items using a {@link Converter}.
 * 
 * @param <T> Value type
 * @param <ITEM> Adapted item type
 * @param <C> Component type
 *
 * @since 5.2.0
 */
public class MultiSelectConverterAdapter<T, ITEM, C extends Component> implements MultiSelect<C, T> {

	private static final long serialVersionUID = -470182997423204535L;

	private final MultiSelect<C, ITEM> multiSelect;
	private final C component;
	private final Converter<T, ITEM> converter;

	/**
	 * @param multiSelect
	 * @param converter
	 */
	public MultiSelectConverterAdapter(MultiSelect<C, ITEM> multiSelect, C component, Converter<T, ITEM> converter) {
		super();
		ObjectUtils.argumentNotNull(multiSelect, "MultiSelect must be not null");
		ObjectUtils.argumentNotNull(converter, "Converter must be not null");
		this.multiSelect = multiSelect;
		this.component = component;
		this.converter = converter;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.vaadin.flow.component.HasValue#addValueChangeListener(com.vaadin.flow.component.HasValue.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<C, Set<T>>> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		return multiSelect.addValueChangeListener(e -> {
			Set<ITEM> oldValues = e.getOldValue();
			if (oldValues == null) {
				oldValues = Collections.emptySet();
			}
			listener.valueChanged(new ComponentValueChangeEvent<>(e.getSource(), this, oldValues.stream()
					.map(item -> converter.convertToPresentation(item, _valueContext())).collect(Collectors.toSet()),
					e.isFromClient()));
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.HasElement#getElement()
	 */
	@Override
	public Element getElement() {
		return multiSelect.getElement();
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.selection.MultiSelect#updateSelection(java.util.Set, java.util.Set)
	 */
	@Override
	public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
		final Set<T> added = (addedItems != null) ? addedItems : Collections.emptySet();
		final Set<T> removed = (removedItems != null) ? removedItems : Collections.emptySet();
		added.stream().map(value -> converter.convertToModel(value, _valueContext()))
				.map(r -> r.getOrThrow(error -> new InputValueConversionException(error))).collect(Collectors.toSet());
		multiSelect.updateSelection(added.stream().map(value -> converter.convertToModel(value, _valueContext()))
				.map(r -> r.getOrThrow(error -> new InputValueConversionException(error))).collect(Collectors.toSet()),
				removed.stream().map(value -> converter.convertToModel(value, _valueContext()))
						.map(r -> r.getOrThrow(error -> new InputValueConversionException(error)))
						.collect(Collectors.toSet()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.selection.MultiSelect#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		return multiSelect.getSelectedItems().stream()
				.map(item -> converter.convertToPresentation(item, _valueContext())).collect(Collectors.toSet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.selection.MultiSelect#addSelectionListener(com.vaadin.flow.data.selection.
	 * MultiSelectionListener)
	 */
	@Override
	public Registration addSelectionListener(MultiSelectionListener<C, T> listener) {
		return multiSelect.addSelectionListener(e -> {
			Set<ITEM> oldValues = e.getOldSelection();
			if (oldValues == null) {
				oldValues = Collections.emptySet();
			}
			listener.selectionChange(new MultiSelectionEvent<>(e.getSource(), this, oldValues.stream()
					.map(item -> converter.convertToPresentation(item, _valueContext())).collect(Collectors.toSet()),
					e.isFromClient()));
		});
	}

	/**
	 * Build the {@link ValueContext} to be used with the converter.
	 * @return the {@link ValueContext}
	 */
	private ValueContext _valueContext() {
		if (component != null) {
			return new ValueContext(component);
		} else {
			return new ValueContext();
		}
	}

}
