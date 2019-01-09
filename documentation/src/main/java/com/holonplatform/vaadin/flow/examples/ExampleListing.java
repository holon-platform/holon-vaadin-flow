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
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ExampleListing {

	// tag::propertydef[]
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	// end::propertydef[]

	private static final DataTarget<?> TARGET = DataTarget.named("subjects");

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

}
