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

import java.util.Optional;

import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;

/**
 * Adapter to convert an {@link SingleSelect} instance into a {@link ValidatableSingleSelect} one.
 * 
 * @param <T> Value type
 *
 * @since 5.2.2
 */
public class ValidatableSingleSelectAdapter<T> extends AbstractValidatableInputAdapter<T, SingleSelect<T>>
		implements ValidatableSingleSelect<T> {

	private static final long serialVersionUID = -2291397152828158839L;

	/**
	 * Constructor.
	 * @param input Wrapped select (not null)
	 */
	public ValidatableSingleSelectAdapter(SingleSelect<T> input) {
		super(input);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.SingleSelect#getSelectedItem()
	 */
	@Override
	public Optional<T> getSelectedItem() {
		return getInput().getSelectedItem();
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

}
