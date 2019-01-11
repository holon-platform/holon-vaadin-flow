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

import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
public class ExampleNavigation3 {

	// tag::route1[]
	@Route("some/path")
	public class View extends Div {

		@QueryParameter
		private Integer parameter;

		public Integer getParameter() { // <1>
			return parameter;
		}

		public void setParameter(Integer parameter) { // <2>
			this.parameter = parameter;
		}

	}
	// end::route1[]

}
