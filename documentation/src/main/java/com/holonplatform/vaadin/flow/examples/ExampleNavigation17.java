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

import com.holonplatform.auth.annotations.Authenticate;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@SuppressWarnings("serial")
public class ExampleNavigation17 {

	// tag::auth1[]
	@Authenticate // <1>
	@Route("some/path")
	public class View1 extends Div {

		/* content omitted */

	}

	@Authenticate(redirectURI = "login") // <2>
	@Route("some/path")
	public class View2 extends Div {

		/* content omitted */

	}
	// end::auth1[]

	// tag::auth2[]
	@Authenticate // <1>
	public class MainLayout extends Div implements RouterLayout {

	}

	@Route(value = "some/path", layout = MainLayout.class) // <2>
	public class View extends Div {

		/* content omitted */

	}
	// end::auth2[]

}
