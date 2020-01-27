package com.holonplatform.vaadin.flow.data;

import java.io.Serializable;
import java.util.List;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PropertySet;

/**
 * Additional items provider. The additional items will be added to the item set
 * returned by the actual backend query.
 *
 * @param <T> Item type
 * 
 * @see DatastoreDataProvider
 */
public interface AdditionalItemsProvider<T> extends Serializable {

	/**
	 * Get the additional items count.
	 * @param datastore   Datastore instance
	 * @param target      Data target of the backend query
	 * @param propertySet Projection of the backend query
	 * @return The additional items count, <code>0</code> if none
	 */
	int getAdditionalItemsCount(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet);

	/**
	 * Get the additional items.
	 * @param datastore   Datastore instance
	 * @param target      Data target of the backend query
	 * @param propertySet Projection of the backend query
	 * @return The additional items list. List size must match the
	 *         {@link #getAdditionalItemsCount(Datastore, DataTarget, PropertySet)}
	 *         value
	 */
	List<T> getAdditionalItems(Datastore datastore, DataTarget<?> target, PropertySet<?> propertySet);

}
