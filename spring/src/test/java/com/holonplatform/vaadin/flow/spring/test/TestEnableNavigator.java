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
package com.holonplatform.vaadin.flow.spring.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.spring.EnableNavigator;
import com.vaadin.flow.spring.annotation.EnableVaadin;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestEnableNavigator.Config.class)
public class TestEnableNavigator extends AbstractVaadinSpringTest {

	@Configuration
	@EnableVaadin
	@EnableNavigator
	static class Config {

	}

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void testUIScopedNavigator() {
		Navigator navigator = applicationContext.getBean(Navigator.class);
		assertNotNull(navigator);
	}

	@Test
	public void testNavigatorFromContext() {
		Navigator navigator = Navigator.get();
		assertNotNull(navigator);
	}

}
