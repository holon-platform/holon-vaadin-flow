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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
public class ExampleNavigation13 {

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

		navigator.navigateTo(View.class, "value"); // <2>
		navigator.navigateTo(View.class, "value", Collections.singletonMap("myparam", Integer.valueOf(1))); // <3>
		// end::navigator1[]
	}

}
