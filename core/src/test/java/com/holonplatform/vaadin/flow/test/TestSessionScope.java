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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Context;
import com.holonplatform.vaadin.flow.VaadinSessionScope;
import com.vaadin.flow.server.VaadinSession;

public class TestSessionScope extends AbstractSessionTest {

	@Test
	public void testScopeConfig() {

		assertTrue(Context.get().scope(VaadinSessionScope.NAME).isPresent());

		assertTrue(VaadinSessionScope.get().isPresent());

		assertNotNull(VaadinSessionScope.require());

	}

	@Test
	public void testScopeResources() {

		assertNotNull(VaadinSession.getCurrent());

		assertTrue(VaadinSessionScope.require().get("mykey", String.class).isPresent());
		assertEquals("test-attribute", VaadinSessionScope.require().get("mykey", String.class).orElse(null));

		assertEquals("test-attribute", Context.get().resource("mykey", String.class).orElse(null));

	}

}
