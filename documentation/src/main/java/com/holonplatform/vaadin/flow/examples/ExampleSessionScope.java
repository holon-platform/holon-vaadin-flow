/*
 * Copyright 2016-2019 Axioma srl.
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

import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.ContextScope;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.vaadin.flow.VaadinSessionScope;
import com.vaadin.flow.server.VaadinSession;

@SuppressWarnings("unused")
public class ExampleSessionScope {

	public void scope1() {
		// tag::scope1[]
		Optional<ContextScope> scope = Context.get().scope(VaadinSessionScope.NAME); // <1>
		scope = VaadinSessionScope.get(); // <2>
		ContextScope sessionScope = VaadinSessionScope.require(); // <3>
		// end::scope1[]
	}

	public void scope2() {
		// tag::scope2[]
		VaadinSession.getCurrent().setAttribute(LocalizationContext.class, // <1>
				LocalizationContext.builder().withInitialLocale(Locale.US).build());

		Optional<LocalizationContext> localizationContext = Context.get().resource(LocalizationContext.class); // <2>

		localizationContext = LocalizationContext.getCurrent(); // <3>
		// end::scope2[]
	}

}
