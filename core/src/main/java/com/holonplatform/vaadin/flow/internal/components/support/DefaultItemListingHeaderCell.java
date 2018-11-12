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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ItemListing.ItemListingCell;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;

/**
 * Default header {@link ItemListingCell} implementation.
 *
 * @since 5.2.0
 */
public class DefaultItemListingHeaderCell implements ItemListingCell {

	private final HeaderCell cell;

	public DefaultItemListingHeaderCell(HeaderCell cell) {
		super();
		ObjectUtils.argumentNotNull(cell, "Cell must be not null");
		this.cell = cell;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing.ItemListingCell#setText(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public void setText(Localizable text) {
		cell.setText((text == null) ? null : LocalizationContext.translate(text, true));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ItemListing.ItemListingCell#setComponent(com.vaadin.flow.component.
	 * Component)
	 */
	@Override
	public void setComponent(Component component) {
		cell.setComponent(component);
	}

}
