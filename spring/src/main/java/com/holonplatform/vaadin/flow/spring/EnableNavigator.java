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
package com.holonplatform.vaadin.flow.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.vaadin.flow.navigator.Navigator;
import com.holonplatform.vaadin.flow.spring.internal.UINavigatorRegistrar;
import com.vaadin.flow.spring.annotation.EnableVaadin;

/**
 * Annotation which can be used on a Spring {@link Configuration} class to made available a UI-scoped {@link Navigator}
 * instance.
 * <p>
 * The Vaadin UI scope must be enabled to use this configuration annotation, for example using the {@link EnableVaadin}
 * annotation or the Vaadin Spring Boot integration auto-configuration facilities.
 * </p>
 * 
 * @since 5.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(UINavigatorRegistrar.class)
@EnableBeanContext
public @interface EnableNavigator {

}
