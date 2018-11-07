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
package com.holonplatform.vaadin.flow.components.events;

import java.io.Serializable;
import java.util.Optional;

import com.holonplatform.vaadin.flow.components.ItemListing;

/**
 * Event to notify an {@link ItemListing} data refresh event.
 *
 * @param <T> Item type
 * @param <P> Item property type
 *
 * @since 5.2.0
 */
public interface ItemListingRefreshEvent<T, P> extends Serializable {

	/**
	 * The {@link ItemListing} on which the event occurred.
	 * @return The {@link ItemListing} on which the event occurred
	 */
	ItemListing<T, P> getItemListing();

	/**
	 * Gets the updated item, if available.
	 * @return Optional updated item
	 */
	Optional<T> getItem();

}
