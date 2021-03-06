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
package com.holonplatform.vaadin.flow.navigator.test.data;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Caption(value = "test", messageCode = "test.code")
@Route("8")
public class NavigationTarget8 extends Div implements AfterNavigationObserver {

	@QueryParameter
	private String param1;

	private String afterNavigation;

	public void setParam1(String param1) {
		this.param1 = (param1 == null) ? param1 : ("!" + param1);

	}

	public String getParamValue() {
		return this.param1;
	}

	public String getAfterNavigation() {
		return this.afterNavigation;
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		this.afterNavigation = getParamValue();
	}

}
