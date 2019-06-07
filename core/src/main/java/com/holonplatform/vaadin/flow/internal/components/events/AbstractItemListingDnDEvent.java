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
import com.holonplatform.vaadin.flow.components.events.ItemListingDnDEvent;

/**
 * Base {@link ItemListingDnDEvent} implementation.
 *
 * @param <T> Item type
 * @param <P> Item property type
 */
public abstract class AbstractItemListingDnDEvent<T, P> implements ItemListingDnDEvent<T, P> {

	private static final long serialVersionUID = 3478617994413988358L;
	
	private final ItemListing<T, P> source;
	private final boolean fromClient;
	
	public AbstractItemListingDnDEvent(ItemListing<T, P> source, boolean fromClient) {
		super();
		this.source = source;
		this.fromClient = fromClient;
	}

	@Override
	public ItemListing<T, P> getSource() {
		return source;
	}

	@Override
	public boolean isFromClient() {
		return fromClient;
	}

}
