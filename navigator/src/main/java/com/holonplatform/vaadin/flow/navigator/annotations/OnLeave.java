/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.navigator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.flow.router.BeforeLeaveEvent;

/**
 * Annotation which can be used on class methods when the class is a navigation (routing) target: the
 * <code>OnLeave</code> annotated methods will be invoked just before the navigation starts from the navigation target
 * class towards another navigation target class.
 * <p>
 * The <code>OnLeave</code> annotated methods must be <code>public</code> and provide either no parameters or a single
 * parameter of type {@link BeforeLeaveEvent}, to obtain the event which triggered the method invocation.
 * </p>
 * <p>
 * NOTE: The {@link BeforeLeaveEvent} has a <code>postpone</code> method, which can be used to postpone the current
 * navigational transition until a specific condition is met.
 * </p>
 * <p>
 * When more than one <code>OnLeave</code> annotated method is present in the navigation target class, no specific
 * invocation order is guaranteed.
 * </p>
 * 
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface OnLeave {

}
