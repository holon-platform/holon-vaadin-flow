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

import com.holonplatform.vaadin.flow.navigator.NavigationParameterTypeMapper;
import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

public class TestParameterTypeMapper implements NavigationParameterTypeMapper<TestParameterType> {

	@Override
	public Class<TestParameterType> getParameterType() {
		return TestParameterType.class;
	}

	@Override
	public String serialize(TestParameterType value) throws InvalidNavigationParameterException {
		if (value != null) {
			return String.valueOf(value.getValue());
		}
		return null;
	}

	@Override
	public TestParameterType deserialize(String value) throws InvalidNavigationParameterException {
		if (value != null) {
			return new TestParameterType(Integer.valueOf(value));
		}
		return null;
	}

}
