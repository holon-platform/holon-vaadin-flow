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

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;

@SuppressWarnings({ "serial", "unused" })
public class ExampleNavigation15 {

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
		// tag::navigator1[]
		Navigator navigator = Navigator.get(); // <1>

		navigator.navigation("some/path") // <2>
				.withQueryParameter("myparam", Integer.valueOf(1)) // <3>
				.withQueryParameter("multi", "a", "b", "c") // <4>
				.navigate(); // <5>

		navigator.navigation(View.class) // <6>
				.withPathParameter("value") // <7>
				.navigate(); // <8>

		Location location = navigator.navigation("some/path") //
				.withQueryParameter("myparam", Integer.valueOf(1)) //
				.asLocation(); // <9>

		String url = navigator.navigation(View.class) //
				.withQueryParameter("myparam", Integer.valueOf(1)) //
				.asLocationURL(); // <10>
		// end::navigator1[]
	}

	public void navigator2() {
		Navigator navigator = Navigator.get();
		// tag::navigator2[]
		String url = navigator.getUrl(View.class); // <1>
		url = navigator.getUrl(View.class, "path_param_value"); // <2>
		url = navigator.getUrl(View.class, "path_param_value1", "path_param_value2"); // <3>
		// end::navigator2[]
	}

	public void navigator3() {
		// tag::navigator3[]
		Navigator navigator = Navigator.get(); // <1>

		navigator.addNavigationChangeListener(event -> { // <2>
			Location location = event.getLocation(); // <3>
			HasElement target = event.getNavigationTarget(); // <4>
		});
		// end::navigator3[]
	}

	public void navigator4() {
		// tag::navigator4[]
		Navigator.get().navigation("some/path") // <1>
				.withQueryParameter("myparam", Integer.valueOf(1)) // <2>
				.withQueryParameter("multi", "a", "b", "c") // <3>
				.encodeQueryParameters(false) // <4>
				.navigate(); // <5>
		// end::navigator4[]
	}

}
