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

import java.util.Collections;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@SuppressWarnings({ "unused", "serial" })
public class ExampleNavigation12 {

	// tag::target[]
	@Route("some/path")
	public class View extends Div {

		@QueryParameter("myparam")
		private Integer parameter; // <1>

	}
	// end::target[]

	public void navigator1() {
		// tag::navigator1[]
		Navigator navigator = Navigator.get(); // <1>
		// end::navigator1[]
	}

	public void navigator2() {
		UI myUI = null;
		// tag::navigator2[]
		Navigator navigator = Navigator.create(myUI); // <1>
		// end::navigator2[]
	}

	public void navigator3() {
		// tag::navigator3[]
		Navigator navigator = Navigator.get(); // <1>

		navigator.navigateTo("some/path"); // <2>
		navigator.navigateTo("some/path", Collections.singletonMap("myparam", Integer.valueOf(1))); // <3>

		navigator.navigateTo(View.class); // <4>
		navigator.navigateTo(View.class, Collections.singletonMap("myparam", Integer.valueOf(1))); // <5>

		navigator.navigateToLocation("some/path?myparam=1"); // <6>
		// end::navigator3[]
	}

}
