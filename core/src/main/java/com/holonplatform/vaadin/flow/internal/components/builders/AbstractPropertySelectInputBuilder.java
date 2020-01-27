package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.Collections;
import java.util.Optional;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.holonplatform.vaadin.flow.internal.data.PropertyItemConverter;

public class AbstractPropertySelectInputBuilder<T> {

	protected PropertyItemConverter<T> propertyItemConverter;

	protected final Property<T> selectionProperty;

	public AbstractPropertySelectInputBuilder(Property<T> selectionProperty) {
		super();
		ObjectUtils.argumentNotNull(selectionProperty, "Selection property must be not null");
		this.selectionProperty = selectionProperty;
	}

	/**
	 * Setup a item converter function if the current item converter is a
	 * {@link PropertyItemConverter}, using the selection property to retrieve an
	 * item.
	 * @param dataProvider Data provider
	 * @param datastore    The datastore
	 * @param target       The query target
	 * @param propertySet  The query projection
	 */
	protected void setupDatastoreItemConverter(DatastoreDataProvider<PropertyBox, ?> dataProvider, Datastore datastore,
			DataTarget<?> target, PropertySet<?> propertySet) {
		if (propertyItemConverter != null && propertyItemConverter.getToItemConverter() == null) {
			propertyItemConverter.setToItemConverter(value -> {
				if (value == null) {
					return Optional.empty();
				}
				final Query query = datastore.query(target).filter(QueryFilter.eq(selectionProperty, value));
				if (dataProvider != null) {
					dataProvider.getQueryFilter().ifPresent(f -> query.filter(f));
				}
				Optional<PropertyBox> item = query.findOne(propertySet);
				if (item.isPresent()) {
					return item;
				}
				// check additional
				if (dataProvider != null) {
					return dataProvider.getAdditionalItemsProvider()
							.map(p -> p.getAdditionalItems(datastore, target, propertySet))
							.orElse(Collections.emptyList()).stream().filter(i -> i.getValueIfPresent(selectionProperty)
									.map(v -> value.equals(v)).orElse(false))
							.findFirst();
				}
				return Optional.empty();
			});
		}
	}

}
