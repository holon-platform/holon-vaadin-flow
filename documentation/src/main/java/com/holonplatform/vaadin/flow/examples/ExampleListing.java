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
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;

@SuppressWarnings("unused")
public class ExampleListing {

	// tag::propertydef[]
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	// end::propertydef[]

	// tag::target[]
	static final DataTarget<?> TARGET = DataTarget.named("subjects");
	// end::target[]

	public void listing1() {
		// tag::listing1[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).build(); // <1>

		listing = Components.listing.properties(ID, NAME).build(); // <2>

		new VerticalLayout().add(listing.getComponent()); // <3>
		// end::listing1[]
	}

	// tag::beandef[]
	class MyBean {

		private Long id;
		private String name;

		public MyBean() {
			super();
		}

		public MyBean(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
	// end::beandef[]

	public void listing2() {
		// tag::listing2[]
		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class).build(); // <1>

		listing = Components.listing.items(MyBean.class).build(); // <2>

		new VerticalLayout().add(listing.getComponent()); // <3>
		// end::listing2[]
	}

	public void listing3() {
		// tag::listing3[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.items(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build()).build();

		listing = PropertyListing.builder(SUBJECT) // <2>
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build())
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 2L).set(NAME, "Two").build()).build();
		// end::listing3[]
	}

	public void listing4() {
		// tag::listing4[]
		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class) // <1>
				.items(new MyBean(1L, "One"), new MyBean(2L, "Two")).build();

		listing = BeanListing.builder(MyBean.class) // <2>
				.addItem(new MyBean(1L, "One")) //
				.addItem(new MyBean(2L, "Two")).build();
		// end::listing4[]
	}

	public void listing5() {
		// tag::listing5[]
		DataProvider<PropertyBox, ?> dataProvider = getPropertyBoxDataProvider(); // <1>

		PropertyListing listing = PropertyListing.builder(SUBJECT).dataSource(dataProvider) // <2>
				.build();
		// end::listing5[]
	}

	public void listing6() {
		// tag::listing6[]
		DataProvider<MyBean, ?> dataProvider = getBeanDataProvider(); // <1>

		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class).dataSource(dataProvider) // <2>
				.build();
		// end::listing6[]
	}

	public void listing7() {
		PropertyBox itemToRefresh = null;
		// tag::listing7[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).dataSource(getDataProvider()).build(); // <1>

		listing.refresh(); // <2>
		listing.refreshItem(itemToRefresh); // <3>
		// end::listing7[]
	}

	public void listing8() {
		// tag::listing8[]
		Datastore datastore = getDatastore();

		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.dataSource(DatastoreDataProvider.create(datastore, TARGET, SUBJECT)) // <2>
				.build();
		// end::listing8[]
	}

	public void listing9() {
		// tag::listing9[]
		Datastore datastore = getDatastore();

		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.dataSource(datastore, TARGET) // <2>
				.build();
		// end::listing9[]
	}

	public void listing10() {
		// tag::listing10[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.dataSource(getDatastore(), TARGET) // <1>
				.withQueryFilter(NAME.isNotNull()) // <2>
				.withQuerySort(ID.asc()) // <3>
				.build();
		// end::listing10[]
	}

	private static DataProvider<PropertyBox, ?> getDataProvider() {
		return null;
	}

	private static DataProvider<PropertyBox, ?> getPropertyBoxDataProvider() {
		return null;
	}

	private static DataProvider<MyBean, ?> getBeanDataProvider() {
		return null;
	}

	private static Datastore getDatastore() {
		return null;
	}

}
