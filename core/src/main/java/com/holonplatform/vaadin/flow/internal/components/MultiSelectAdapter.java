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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;

/**
 * Adapter to use a {@link HasValue} {@link Component} as a {@link MultiSelect}.
 *
 * @param <T> Value type
 * @param <V> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 * 
 * @since 5.2.0
 */
public class MultiSelectAdapter<T, V extends HasValue<?, Set<T>>, C extends Component>
		extends InputAdapter<Set<T>, V, C> implements MultiSelect<T> {

	private static final long serialVersionUID = -238233555416654435L;

	private final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	private BiConsumer<V, C> refreshOperation;
	private BiFunction<V, C, DataProvider<T, ?>> dataProviderSupplier;

	private com.vaadin.flow.data.selection.MultiSelect<C, T> multiSelect;

	/**
	 * Default constructor.
	 * @param field The {@link HasValue} instance (not null)
	 * @param component The {@link Component} instance (not null)
	 */
	public MultiSelectAdapter(V field, C component) {
		super(field, component);
		// selection change
		field.addValueChangeListener(e -> fireSelectionListeners(e.getValue(), e.isFromClient()));
	}

	/**
	 * Constructor using a {@link com.vaadin.flow.data.selection.MultiSelect}.
	 * @param <M> MultiSelect type
	 * @param field The {@link com.vaadin.flow.data.selection.MultiSelect} instance (not null)
	 * @param component The {@link Component} instance (not null)
	 */
	@SuppressWarnings("unchecked")
	public <M extends com.vaadin.flow.data.selection.MultiSelect<C, T>> MultiSelectAdapter(M field, C component) {
		super((V) field, component);
		this.multiSelect = field;
		// selection change
		field.addSelectionListener(e -> fireSelectionListeners(e.getNewSelection(), e.isFromClient()));
	}

	/**
	 * Gwt the {@link com.vaadin.flow.data.selection.MultiSelect} field instance, if available.
	 * @return Optional MultiSelect
	 */
	protected Optional<com.vaadin.flow.data.selection.MultiSelect<C, T>> getMultiSelect() {
		return Optional.ofNullable(multiSelect);
	}

	/**
	 * Get the <code>refresh</code> operation, if available.
	 * @return Optional <code>refresh</code> operation
	 */
	public Optional<BiConsumer<V, C>> getRefreshOperation() {
		return Optional.ofNullable(refreshOperation);
	}

	/**
	 * Set the <code>refresh</code> operation.
	 * @param refreshOperation the operation to set
	 */
	public void setRefreshOperation(BiConsumer<V, C> refreshOperation) {
		this.refreshOperation = refreshOperation;
	}

	/**
	 * Get the {@link DataProvider} supplier, if available.
	 * @return Optional {@link DataProvider} supplier
	 */
	public Optional<BiFunction<V, C, DataProvider<T, ?>>> getDataProviderSupplier() {
		return Optional.ofNullable(dataProviderSupplier);
	}

	/**
	 * Set the {@link DataProvider} supplier.
	 * @param dataProviderSupplier the supplier to set
	 */
	public void setDataProviderSupplier(BiFunction<V, C, DataProvider<T, ?>> dataProviderSupplier) {
		this.dataProviderSupplier = dataProviderSupplier;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		return getMultiSelect().map(s -> s.getSelectedItems()).orElseGet(() -> getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		if (getMultiSelect().isPresent()) {
			getMultiSelect().get().deselectAll();
		} else {
			clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		if (getRefreshOperation().isPresent()) {
			getRefreshOperation().get().accept(getField(), getInputComponent());
		} else {
			getDataProvider().ifPresent(d -> d.refreshAll());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#select(java.lang.Iterable)
	 */
	@Override
	public void select(Iterable<T> items) {
		if (getMultiSelect().isPresent()) {
			getMultiSelect().get().updateSelection(ConversionUtils.iterableAsSet(items), Collections.emptySet());
		} else {
			setValue(ConversionUtils.iterableAsSet(items));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#deselect(java.lang.Iterable)
	 */
	@Override
	public void deselect(Iterable<T> items) {
		if (getMultiSelect().isPresent()) {
			getMultiSelect().get().updateSelection(Collections.emptySet(), ConversionUtils.iterableAsSet(items));
		} else {
			final Set<T> deselect = ConversionUtils.iterableAsSet(items);
			Set<T> selected = getValue();
			if (deselect != null && !deselect.isEmpty() && selected != null && !selected.isEmpty()) {
				Set<T> value = new HashSet<>(selected);
				value.removeIf(item -> deselect.contains(item));
				setValue(value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#selectAll()
	 */
	@Override
	public void selectAll() {
		getDataProvider().ifPresent(d -> {
			select(d.fetch(new Query<>()).collect(Collectors.toSet()));
		});
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
		final Set<T> values = (selectedItems != null) ? selectedItems : Collections.emptySet();
		selectionListeners
				.forEach(listener -> listener.onSelectionChange(new DefaultSelectionEvent<>(values, fromClient)));
	}

	/**
	 * Get the {@link DataProvider}, if available.
	 * @return Optional {@link DataProvider}
	 */
	protected Optional<DataProvider<T, ?>> getDataProvider() {
		return getDataProviderSupplier().map(s -> s.apply(getField(), getInputComponent()));
	}

}
