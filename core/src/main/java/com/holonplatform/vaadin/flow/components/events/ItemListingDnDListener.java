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

import java.io.Serializable;
import java.util.EventListener;

import com.holonplatform.vaadin.flow.components.ItemListing;

/**
 * {@link ItemListing} drag'n'drop event listener.
 *
 * @param <T> Item type
 * @param <P> Item property type
 * @param <E> Drag'n'drop event type
 */
@FunctionalInterface
public interface ItemListingDnDListener<T, P, E extends ItemListingDnDEvent<T, P>> extends EventListener, Serializable {

	/**
	 * Invoked when a drag'n'drop event has been fired.
	 * @param event The drag'n'drop event
	 */
	void onDndEvent(E event);

}
