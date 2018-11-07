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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingCell;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingRow;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingRow;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid.Column;

/**
 * Default footer {@link ItemListingRow} implementation.
 * 
 * @param <P> Item property type
 * 
 * @since 5.2.0
 */
public class DefaultItemListingFooterRow<P> implements EditableItemListingRow<P> {

	private final FooterRow row;
	private final Function<P, Column<?>> propertyColumnProvider;

	public DefaultItemListingFooterRow(FooterRow row, Function<P, Column<?>> propertyColumnProvider) {
		super();
		ObjectUtils.argumentNotNull(row, "Row must be not null");
		ObjectUtils.argumentNotNull(propertyColumnProvider, "Property column provider must be not null");
		this.row = row;
		this.propertyColumnProvider = propertyColumnProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing.ItemListingRow#getCells()
	 */
	@Override
	public List<ItemListingCell> getCells() {
		return row.getCells().stream().map(cell -> new DefaultItemListingFooterCell(cell)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing.ItemListingRow#getCell(java.lang.Object)
	 */
	@Override
	public Optional<ItemListingCell> getCell(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		final Column<?> column = propertyColumnProvider.apply(property);
		if (column != null) {
			try {
				return Optional.ofNullable(new DefaultItemListingFooterCell(row.getCell(column)));
			} catch (@SuppressWarnings("unused") IllegalArgumentException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.EditableItemListingRow#join(java.util.
	 * Collection)
	 */
	@Override
	public ItemListingCell join(Collection<P> properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		final List<Column<?>> columns = properties.stream().map(property -> propertyColumnProvider.apply(property))
				.filter(column -> column != null).collect(Collectors.toList());
		return new DefaultItemListingFooterCell(row.join(columns.toArray(new Column<?>[columns.size()])));
	}

}
