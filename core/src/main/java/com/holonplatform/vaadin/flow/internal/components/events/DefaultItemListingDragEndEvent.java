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

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.events.ItemListingDragEndEvent;
import com.vaadin.flow.component.grid.dnd.GridDragEndEvent;

/**
 * Default {@link ItemListingDragEndEvent} implementation.
 *
 * @param <T> Item type
 * @param <P> Item property type
 */
public class DefaultItemListingDragEndEvent<T, P> extends AbstractItemListingDnDEvent<T, P>
		implements ItemListingDragEndEvent<T, P> {

	private static final long serialVersionUID = -1808064117712861924L;

	public DefaultItemListingDragEndEvent(ItemListing<T, P> source, GridDragEndEvent<T> event) {
		super(source, event.isFromClient());
	}

}
