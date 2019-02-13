/*
 * Copyright 2000-2017 Holon TDCN.
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

import java.util.Set;

import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.ValidatableMultiSelect;

/**
 * Adapter to convert an {@link MultiSelect} instance into a {@link ValidatableMultiSelect} one.
 * 
 * @param <T> Value type
 *
 * @since 5.2.2
 */
public class ValidatableMultiSelectAdapter<T> extends AbstractValidatableInputAdapter<Set<T>, MultiSelect<T>>
		implements ValidatableMultiSelect<T> {

	private static final long serialVersionUID = -2291397152828158839L;

	/**
	 * Constructor.
	 * @param input Wrapped select (not null)
	 */
	public ValidatableMultiSelectAdapter(MultiSelect<T> input) {
		super(input);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		getInput().select(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		getInput().deselect(item);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.Selectable#addSelectionListener(com.holonplatform.vaadin.flow.components
	 * .Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(SelectionListener<T> selectionListener) {
		return getInput().addSelectionListener(selectionListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		getInput().refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#select(java.lang.Iterable)
	 */
	@Override
	public void select(Iterable<T> items) {
		getInput().select(items);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#deselect(java.lang.Iterable)
	 */
	@Override
	public void deselect(Iterable<T> items) {
		getInput().deselect(items);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MultiSelect#selectAll()
	 */
	@Override
	public void selectAll() {
		getInput().selectAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		return getInput().getSelectedItems();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		getInput().deselectAll();
	}

}
