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
package com.holonplatform.vaadin.flow.examples;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

@SuppressWarnings("unused")
public class ExampleDatastore {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name");
	private static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	private static final DataTarget<?> TARGET = DataTarget.named("subjects");
	private static final Datastore datastore = null;

	public void dataprovider1() {
		// tag::dataprovider1[]
		final NumericProperty<Long> ID = NumericProperty.longType("id");
		final StringProperty NAME = StringProperty.create("name");

		final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME); // <1>

		final DataTarget<?> TARGET = DataTarget.named("subjects"); // <2>

		final Datastore datastore = obtainDatastore(); // <3>

		DataProvider<PropertyBox, QueryFilter> dataProvider = DatastoreDataProvider.create(datastore, TARGET, SUBJECT); // <4>

		dataProvider = DatastoreDataProvider.create(datastore, TARGET, ID, NAME); // <5>
		// end::dataprovider1[]
	}

	public void dataprovider2() {
		// tag::dataprovider2[]
		final DataTarget<?> TARGET = DataTarget.named("subjects"); // <1>

		final Datastore datastore = obtainDatastore(); // <2>

		DataProvider<MyBean, QueryFilter> dataProvider = DatastoreDataProvider.create(datastore, TARGET, MyBean.class); // <3>
		// end::dataprovider2[]
	}

	public void dataprovider3() {
		// tag::dataprovider3[]
		DataProvider<PropertyBox, String> dataProvider = DatastoreDataProvider.create(datastore, TARGET, SUBJECT,
				stringValue -> NAME.startsWith(stringValue));
		// end::dataprovider3[]
	}

	public void dataprovider4() {
		// tag::dataprovider4[]
		DataProvider<MyBean, String> dataProvider = DatastoreDataProvider.create(datastore, TARGET, MyBean.class,
				stringValue -> NAME.startsWith(stringValue));
		// end::dataprovider4[]
	}

	public void dataprovider5() {
		// tag::dataprovider5[]
		DataProvider<MyItem, String> dataProvider = DatastoreDataProvider.create(datastore, TARGET, //
				SUBJECT, // <1>
				propertyBox -> new MyItem(propertyBox.getValue(ID), propertyBox.getValue(NAME)), // <2>
				stringValue -> NAME.startsWith(stringValue)); // <3>
		// end::dataprovider5[]
	}

	public void dataprovider6() {
		// tag::dataprovider6[]
		DataProvider<?, ?> dataProvider = DatastoreDataProvider.builder(datastore, TARGET, SUBJECT) // <1>
				.withQueryFilter(NAME.isNotNull()) // <2>
				.withQuerySort(NAME.asc()) // <3>
				.withDefaultQuerySort(ID.desc()) // <4>
				.build();
		// end::dataprovider6[]
	}

	public void dataprovider7() {
		// tag::dataprovider7[]
		DataProvider<?, ?> dataProvider = DatastoreDataProvider.builder(datastore, TARGET, SUBJECT) // <1>
				.withQueryConfigurationProvider(new QueryConfigurationProvider() { // <2>

					@Override
					public QueryFilter getQueryFilter() { // <3>
						return null;
					}

					@Override
					public QuerySort getQuerySort() { // <4>
						return null;
					}

				}).build();
		// end::dataprovider7[]
	}

	public void dataprovider8() {
		// tag::dataprovider8[]
		DataProvider<?, ?> dataProvider = DatastoreDataProvider.builder(datastore, TARGET, SUBJECT) // <1>
				.querySortOrderConverter(querySortOrder -> { // <2>
					// QuerySortOrder to QuerySort conversion logic omitted
					return null;
				}).build();
		// end::dataprovider8[]
	}

	public void dataprovider9() {
		// tag::dataprovider9[]
		DataProvider<?, ?> dataProvider = DatastoreDataProvider.builder(datastore, TARGET, SUBJECT)
				.itemIdentifierProvider(propertyBox -> propertyBox.getValue(ID)) // <1>
				.build();
		// end::dataprovider9[]
	}

	public void dataprovider10() {
		// tag::dataprovider10[]
		DataProvider<?, ?> dataProvider = DatastoreDataProvider.builder(datastore, TARGET, MyBean.class)
				.itemIdentifierProvider(bean -> bean.getId()) // <1>
				.build();
		// end::dataprovider10[]
	}

	public void builder() {
		// tag::builder[]
		SingleSelect<Long> select = Components.input.singleSelect(ID).dataSource(datastore, TARGET).build();
		// end::builder[]
	}

	private class MyBean {

		public Long getId() {
			return null;
		}

	}

	private class MyItem {

		public MyItem(Long id, String name) {

		}

	}

	private static Datastore obtainDatastore() {
		return null;
	}

}
