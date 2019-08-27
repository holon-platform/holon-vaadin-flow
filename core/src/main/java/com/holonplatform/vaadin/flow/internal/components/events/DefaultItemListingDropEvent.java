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
import java.util.Optional;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.events.ItemListingDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;

/**
 * Default {@link ItemListingDropEvent} implementation.
 *
 * @param <T> Item type
 * @param <P> Item property type
 */
public class DefaultItemListingDropEvent<T, P> extends AbstractItemListingDnDEvent<T, P>
		implements ItemListingDropEvent<T, P> {

	private static final long serialVersionUID = 4771156316048469614L;

	private final GridDropEvent<T> event;

	public DefaultItemListingDropEvent(ItemListing<T, P> source, GridDropEvent<T> event) {
		super(source, event.isFromClient());
		this.event = event;
	}

	@Override
	public Optional<T> getDropTargetItem() {
		return event.getDropTargetItem();
	}

	@Override
	public GridDropLocation getDropLocation() {
		return event.getDropLocation();
	}

	@Override
	public Optional<String> getDataTransferData(String type) {
		return event.getDataTransferData(type);
	}

	@Override
	public String getDataTransferText() {
		return event.getDataTransferText();
	}

	@Override
	public Map<String, String> getDataTransferData() {
		return event.getDataTransferData();
	}

}
