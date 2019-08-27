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
package com.holonplatform.vaadin.flow.components.events;

import java.util.Map;
import java.util.Optional;

import com.holonplatform.vaadin.flow.components.ItemListing;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;

/**
 * Drop event that occurs on the {@link ItemListing} or its rows.
 * 
 * @param <T> Item type
 * @param <P> Item property type
 */
public interface ItemListingDropEvent<T, P> extends ItemListingDnDEvent<T, P> {

	/**
	 * Get the row the drop happened on.
	 * <p>
	 * If the drop was not on top of a row (see {@link #getDropLocation()}) or {@link GridDropMode#ON_GRID} is used,
	 * then returns an empty optional.
	 * </p>
	 * @return The item of the row the drop happened on, or an empty Optional if drop was not on a row
	 */
	Optional<T> getDropTargetItem();

	/**
	 * Get the location of the drop within the row.
	 * <p>
	 * NOTE: the location will be {@link GridDropLocation#EMPTY} if:
	 * <ul>
	 * <li>dropped on an empty grid</li>
	 * <li>dropping on rows was not possible because of {@link GridDropMode#ON_GRID } was used</li>
	 * <li>{@link GridDropMode#ON_TOP} is used and the drop happened on empty space after last row or on top of the
	 * header / footer</li>
	 * </ul>
	 * @return The location of the drop in relative to the {@link #getDropTargetItem()} or
	 *         {@link GridDropLocation#EMPTY} if no target row present
	 */
	GridDropLocation getDropLocation();

	/**
	 * Get data from the <code>DataTransfer</code> object.
	 * @param type Data format, e.g. <code>text/plain</code> or <code>text/uri-list</code>.
	 * @return Optional data for the given format if exists in the <code>DataTransfer</code>, otherwise an empty
	 *         Optional
	 */
	Optional<String> getDataTransferData(String type);

	/**
	 * Get data of any of the types <code>text</code>, <code>Text</code> or <code>text/plain</code>.
	 * @return First existing data of types in order <code>text</code>, <code>Text</code> or <code>text/plain</code>, or
	 *         <code>null</code> if none of them exist.
	 */
	String getDataTransferText();

	/**
	 * Get all of the transfer data from the <code>DataTransfer</code> object. The data can be iterated to find the most
	 * relevant data as it preserves the order in which the data was set to the drag source element.
	 * @return Map of type/data pairs, containing all the data from the <code>DataTransfer</code> object.
	 */
	Map<String, String> getDataTransferData();

}
