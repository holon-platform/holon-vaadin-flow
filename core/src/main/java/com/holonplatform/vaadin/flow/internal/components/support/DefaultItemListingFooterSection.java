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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingSection;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingRow;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingSection;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;

/**
 * Default footer {@link ItemListingSection} implementation.
 * 
 * @param <P> Item property type
 * 
 * @since 5.2.0
 */
public class DefaultItemListingFooterSection<P> implements EditableItemListingSection<P> {

	private final Grid<?> grid;
	private final Function<P, Column<?>> propertyColumnProvider;

	public DefaultItemListingFooterSection(Grid<?> grid, Function<P, Column<?>> propertyColumnProvider) {
		super();
		ObjectUtils.argumentNotNull(grid, "Grid must be not null");
		ObjectUtils.argumentNotNull(propertyColumnProvider, "Property column provider must be not null");
		this.grid = grid;
		this.propertyColumnProvider = propertyColumnProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing.ItemListingSection#getRows()
	 */
	@Override
	public List<EditableItemListingRow<P>> getRows() {
		return grid.getFooterRows().stream().map(row -> new DefaultItemListingFooterRow<>(row, propertyColumnProvider))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingSection#appendRow()
	 */
	@Override
	public EditableItemListingRow<P> appendRow() {
		return new DefaultItemListingFooterRow<>(grid.appendFooterRow(), propertyColumnProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingSection#prependRow()
	 */
	@Override
	public EditableItemListingRow<P> prependRow() {
		return new DefaultItemListingFooterRow<>(grid.prependFooterRow(), propertyColumnProvider);
	}

}
