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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.vaadin.flow.navigator.NavigationParameters;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

@SuppressWarnings("unused")
public class ExampleNavigationParameters {

	public void np1() {
		// tag::np1[]
		Map<String, List<String>> queryParameters = getQueryParameters();
		NavigationParameters navigationParameters = NavigationParameters.create(queryParameters); // <1>

		navigationParameters = NavigationParameters
				.create(QueryParameters.simple(Collections.singletonMap("test", "value"))); // <2>

		navigationParameters = NavigationParameters.create(new Location("host.com/?test=value")); // <3>
		// end::np1[]
	}

	public void np2() {
		// tag::np2[]
		NavigationParameters navigationParameters = NavigationParameters.create(getQueryParameters(), false); // <1>
		// end::np2[]
	}

	public void np3() {
		// tag::np3[]
		NavigationParameters navigationParameters = NavigationParameters.create(getQueryParameters()); // <1>

		boolean hasParameterAndValue = navigationParameters.hasQueryParameter("myparam"); // <2>

		List<Integer> values = navigationParameters.getQueryParameterValues("myparam", Integer.class); // <3>

		Optional<Integer> value = navigationParameters.getQueryParameterValue("myparam", Integer.class); // <4>

		Integer valueOrDefault = navigationParameters.getQueryParameterValue("myparam", Integer.class, 0); // <5>
		// end::np3[]
	}

	private static Map<String, List<String>> getQueryParameters() {
		return null;
	}

}
