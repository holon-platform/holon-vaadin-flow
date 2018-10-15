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
package com.holonplatform.vaadin.flow.test.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.DivElement;

public class ButtonIT extends AbstractIntegrationTest {

	@Test
	public void testButton() {

		assertEquals("TEST", $(ButtonElement.class).id(IntegrationTestView.BUTTON1).getText());

		$(ButtonElement.class).id(IntegrationTestView.BUTTON1).click();

		assertEquals(IntegrationTestView.BUTTON1, $(DivElement.class).id(IntegrationTestView.LABEL1).getText());

	}

}
