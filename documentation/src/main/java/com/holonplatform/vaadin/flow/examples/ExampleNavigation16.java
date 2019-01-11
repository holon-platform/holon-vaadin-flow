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

import com.holonplatform.vaadin.flow.navigator.NavigationLink;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
public class ExampleNavigation16 {

	// tag::target[]
	@Route("some/path")
	public class View extends Div implements HasUrlParameter<String> {

		@QueryParameter("myparam")
		private Integer parameter;

		@Override
		public void setParameter(BeforeEvent event, String parameter) {
			/* handle the path parameter value */
		}

	}
	// end::target[]

	public void navigator1() {
		VerticalLayout myLayout = new VerticalLayout();
		// tag::navigator1[]
		NavigationLink link = NavigationLink.builder("some/path") // <1>
				.withQueryParameter("myparam", 123) // <2>
				.text("Link text") // <3>
				.build();

		link = NavigationLink.builder(View.class) // <4>
				.withQueryParameter("myparam", 123) // <5>
				.withPathParameter("value") // <6>
				.text("Link text") // <7>
				.build();

		myLayout.add(link.getComponent()); // <8>
		// end::navigator1[]
	}

}
