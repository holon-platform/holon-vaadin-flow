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
package com.holonplatform.vaadin.flow.test;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.PropertyViewForm;

public class TestPropertyViewForm {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> "[" + pb.getValue(NAME) + "]");

	private static final PropertySet<?> SET = PropertySet.of(ID, NAME, VIRTUAL);

	@Test
	public void testBuilder() {
		
		//PropertyViewForm.builder(content)
		
	}
	
}
