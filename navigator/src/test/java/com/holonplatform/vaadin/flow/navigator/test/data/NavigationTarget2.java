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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.holonplatform.vaadin.flow.navigator.annotations.QueryParameter;
import com.vaadin.flow.router.Route;

@Route("2")
public class NavigationTarget2 {

	@QueryParameter
	private String param1;

	@QueryParameter("param2")
	private Integer p2;

	@QueryParameter(required = true)
	private Double param3;
	
	@QueryParameter(value="param4", defaultValue="dft")
	private String p4;
	
	@QueryParameter
	private List<String> param5;
	
	@QueryParameter("param6")
	private Set<LocalDate> p6;

	public Double getParam3() {
		return param3;
	}

	public void setParam3(Double param3) {
		this.param3 = param3;
	}

}
