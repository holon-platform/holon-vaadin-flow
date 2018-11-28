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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasPlaceholder;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultSelectionEvent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

/**
 * Adapter to use a {@link HasValue} {@link Component} as a {@link MultiSelect}.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <C> Concrete {@link Component} type
 * 
 * @since 5.2.0
 */
public class MultiSelectInputAdapter<T, ITEM, C extends Component> implements MultiSelect<T> {

	private static final long serialVersionUID = -238233555416654435L;

	private final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	private final Input<Set<ITEM>> input;

	private final ItemConverter<T, ITEM> itemConverter;

	private final Consumer<MultiSelect<T>> refreshOperation;

	private final Supplier<Set<ITEM>> selectAllOperation;

	/**
	 * Constructor.
	 * @param input The concrete {@link Input} instance (not null)
	 * @param itemConverter Item converter (not null)
	 * @param refreshOperation The <code>refresh</code> operation (not null)
	 * @param selectAllOperation The <code>selectAll</code> operation
	 */
	public MultiSelectInputAdapter(Input<Set<ITEM>> input, ItemConverter<T, ITEM> itemConverter,
			Consumer<MultiSelect<T>> refreshOperation, Supplier<Set<ITEM>> selectAllOperation) {
		super();
		ObjectUtils.argumentNotNull(input, "Input must be not null");
		ObjectUtils.argumentNotNull(itemConverter, "Item converter must be not null");
		ObjectUtils.argumentNotNull(refreshOperation, "Refresh operation must be not null");
		this.input = input;
		this.itemConverter = itemConverter;
		this.refreshOperation = refreshOperation;
		this.selectAllOperation = selectAllOperation;
		// selection change listener
		input.addValueChangeListener(e -> fireSelectionListeners(asValues(e.getValue()), e.isUserOriginated()));
	}

	/**
	 * Get the given item type set as a value type set.
	 * @param items item type set
	 * @return value type set
	 */
	protected Set<T> asValues(Set<ITEM> items) {
		if (items == null) {
			return Collections.emptySet();
		}
		return items.stream().map(v -> itemConverter.getValue(v)).collect(Collectors.toSet());
	}

	/**
	 * Get the given value type set as a item type set.
	 * @param values values type set
	 * @return item type set
	 */
	protected Set<ITEM> asItems(Set<T> values) {
		if (values == null) {
			return Collections.emptySet();
		}
		return values.stream().map(v -> itemConverter.getItem(v).orElse(null)).filter(i -> i != null)
				.collect(Collectors.toSet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		return getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		setValue(Collections.emptySet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		refreshOperation.accept(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#select(java.lang.Iterable)
	 */
	@Override
	public void select(Iterable<T> items) {
		final Set<T> toSelect = ConversionUtils.iterableAsSet(items);
		final Set<T> values = new HashSet<>(getValue());
		toSelect.forEach(v -> values.add(v));
		setValue(values);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#deselect(java.lang.Iterable)
	 */
	@Override
	public void deselect(Iterable<T> items) {
		final Set<T> toDeselect = ConversionUtils.iterableAsSet(items);
		final Set<T> values = new HashSet<>(getValue());
		toDeselect.forEach(v -> values.remove(v));
		setValue(values);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#selectAll()
	 */
	@Override
	public void selectAll() {
		if (selectAllOperation != null) {
			final Set<ITEM> items = selectAllOperation.get();
			if (items != null) {
				setValue(asValues(items));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.Selectable#addSelectionListener(com.holonplatform.vaadin.flow.components
	 * .Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must me not null");
		selectionListeners.add(selectionListener);
		return () -> selectionListeners.remove(selectionListener);
	}

	/**
	 * Fire the registered selection listeners.
	 * @param selectedItems The selected items (may be null)
	 * @param fromClient Whether the selection event was originated from the client side
	 */
	protected void fireSelectionListeners(Set<T> selectedItems, boolean fromClient) {
		selectionListeners.forEach(
				listener -> listener.onSelectionChange(new DefaultSelectionEvent<>(selectedItems, fromClient)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		input.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return input.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return input.isRequired();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		input.setRequired(required);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#focus()
	 */
	@Override
	public void focus() {
		input.focus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Set<T> value) {
		input.setValue(asItems(value));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValue()
	 */
	@Override
	public Set<T> getValue() {
		return asValues(input.getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getEmptyValue()
	 */
	@Override
	public Set<T> getEmptyValue() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getValue().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#clear()
	 */
	@Override
	public void clear() {
		input.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.flow.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<Set<T>, ValueChangeEvent<Set<T>>> listener) {
		return input.addValueChangeListener(e -> listener.valueChange(new DefaultValueChangeEvent<>(this,
				asValues(e.getOldValue()), asValues(e.getValue()), e.isUserOriginated())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return input.getComponent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Input#hasValidation()
	 */
	@Override
	public Optional<HasValidation> hasValidation() {
		return input.hasValidation();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveLabel#hasLabel()
	 */
	@Override
	public Optional<HasLabel> hasLabel() {
		return input.hasLabel();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveTitle#hasTitle()
	 */
	@Override
	public Optional<HasTitle> hasTitle() {
		return input.hasTitle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHavePlaceholder#hasPlaceholder()
	 */
	@Override
	public Optional<HasPlaceholder> hasPlaceholder() {
		return input.hasPlaceholder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		input.setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return input.isVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasEnabled()
	 */
	@Override
	public Optional<HasEnabled> hasEnabled() {
		return input.hasEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasStyle()
	 */
	@Override
	public Optional<HasStyle> hasStyle() {
		return input.hasStyle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#hasSize()
	 */
	@Override
	public Optional<HasSize> hasSize() {
		return input.hasSize();
	}

}
