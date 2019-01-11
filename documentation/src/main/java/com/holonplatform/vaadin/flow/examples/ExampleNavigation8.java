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

import com.holonplatform.vaadin.flow.navigator.NavigationParameterTypeMapper;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
public class ExampleNavigation8 {

	// tag::type[]
	public class MyType {

		private final int value;

		public MyType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}
	// end::type[]

	// tag::mapper[]
	public class MyTypeParameterMapper implements NavigationParameterTypeMapper<MyType> {

		@Override
		public Class<MyType> getParameterType() { // <1>
			return MyType.class;
		}

		@Override
		public String serialize(MyType value) throws InvalidNavigationParameterException { // <2>
			if (value != null) {
				return String.valueOf(value.getValue());
			}
			return null;
		}

		@Override
		public MyType deserialize(String value) throws InvalidNavigationParameterException { // <3>
			if (value != null) {
				return new MyType(Integer.valueOf(value));
			}
			return null;
		}

	}
	// end::mapper[]

	// tag::route[]
	@Route("some/path")
	public class View extends Div {

		@QueryParameter
		private MyType parameter; // <1>

	}
	// end::route[]

}
