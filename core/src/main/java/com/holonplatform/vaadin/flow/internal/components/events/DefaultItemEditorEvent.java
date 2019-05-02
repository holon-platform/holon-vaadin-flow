/*
 * Copyright 2016-2019 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.components.events;

import java.util.Map;
import java.util.function.Supplier;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemEditorEvent;
import com.vaadin.flow.component.grid.editor.Editor;

/**
 * Default {@link ItemEditorEvent} implementation.
 *
 * @param <T> Item type
 * @param <P> Property type
 * 
 * @since 5.2.8
 */
public class DefaultItemEditorEvent<T, P> implements ItemEditorEvent<T, P> {

	private static final long serialVersionUID = 5792406429806357770L;

	private final ItemListing<T, P> listing;
	private final Editor<T> editor;
	private final T item;
	private final Supplier<Map<P, Input<?>>> bindingsSupplier;

	public DefaultItemEditorEvent(ItemListing<T, P> listing, Editor<T> editor, T item,
			Supplier<Map<P, Input<?>>> bindingsSupplier) {
		super();
		this.listing = listing;
		this.editor = editor;
		this.item = item;
		this.bindingsSupplier = bindingsSupplier;
	}

	@Override
	public ItemListing<T, P> getListing() {
		return listing;
	}

	@Override
	public Editor<T> getEditor() {
		return editor;
	}

	@Override
	public T getItem() {
		return item;
	}

	@Override
	public Map<P, Input<?>> getBindings() {
		return bindingsSupplier.get();
	}

}
