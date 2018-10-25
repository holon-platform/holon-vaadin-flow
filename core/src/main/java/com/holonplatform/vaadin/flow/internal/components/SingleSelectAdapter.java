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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.shared.Registration;

/**
 * Adapter to use a {@link HasValue} {@link Component} as a {@link SingleSelect}.
 *
 * @param <T> Value type
 * @param <V> Concrete {@link HasValue} type
 * @param <C> Concrete {@link Component} type
 * 
 * @since 5.2.0
 */
public class SingleSelectAdapter<T, V extends HasValue<?, T>, C extends Component> extends InputAdapter<T, V, C>
		implements SingleSelect<T> {

	private static final long serialVersionUID = -2059845261833011783L;

	private final List<SelectionListener<T>> selectionListeners = new LinkedList<>();

	private BiConsumer<V, C> refreshOperation;
	private BiFunction<V, C, DataProvider<T, ?>> dataProviderSupplier;

	/**
	 * Default constructor.
	 * @param field The {@link HasValue} instance (not null)
	 * @param component The {@link Component} instance (not null)
	 */
	public SingleSelectAdapter(V field, C component) {
		super(field, component);
		// selection change on value change
		getField().addValueChangeListener(e -> fireSelectionListeners(e.getValue(), e.isFromClient()));
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
	 * @see com.holonplatform.vaadin.flow.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		setValue(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.SingleSelect#getSelectedItem()
	 */
	@Override
	public Optional<T> getSelectedItem() {
		return getValueIfPresent();
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
	 * @param selectedValue The selected value (may be null)
	 * @param fromClient Whether the selection event was originated from the client side
	 */
	protected void fireSelectionListeners(T selectedValue, boolean fromClient) {
		selectionListeners.forEach(
				listener -> listener.onSelectionChange(new DefaultSelectionEvent<>(selectedValue, fromClient)));
	}

	/**
	 * Get the {@link DataProvider}, if available.
	 * @return Optional {@link DataProvider}
	 */
	protected Optional<DataProvider<T, ?>> getDataProvider() {
		return getDataProviderSupplier().map(s -> s.apply(getField(), getInputComponent()));
	}

}
