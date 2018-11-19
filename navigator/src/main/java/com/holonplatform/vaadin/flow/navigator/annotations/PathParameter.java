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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Annotation which can be used on a navigation target class field to inject the URL path parameter values.
 * <p>
 * The path parameter value
 * </p>
 * <p>
 * This annotation supports a {@link #required()} attribute to require a parameter value and the {@link #defaultValue()}
 * attribute to provide a default parameter value when the value is not available from the navigation URL.
 * </p>
 * <p>
 * The supported parameter value types are:
 * <ul>
 * <li>{@link String}.</li>
 * <li>{@link Number}. The <code>.</code> (dot) character must be used as decimal separator for decimal numbers.</li>
 * <li>{@link Boolean}. The <code>true</code> and <code>false</code> String values are admitted as parameter
 * values.</li>
 * <li>{@link Enum}. The enumeration value <code>name</code> must be used as parameter value.</li>
 * <li>{@link Date}. The ISO-8601 standard format must be used as parameter value representation.</li>
 * <li>{@link LocalDate}. The ISO-8601 standard format must be used as parameter value representation.</li>
 * <li>{@link LocalTime}. The ISO-8601 standard format must be used as parameter value representation.</li>
 * <li>{@link LocalDateTime}. The ISO-8601 standard format must be used as parameter value representation.</li>
 * </ul>
 * <p>
 * The {@link Set} and {@link List} collection types are also supported as parameter value type, in order to obtain the
 * full path parameter value.
 * </p>
 * 
 * @since 5.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface PathParameter {

	/**
	 * The path segment index to use as parameter value.
	 * <p>
	 * If the segment index is <code>-1</code>, the full path value (excluding the route name) will be use as parameter
	 * value.
	 * </p>
	 * @return the parameter segment index, 0-based, or <code>-1</code> for none
	 */
	int value() default -1;

	/**
	 * Declares this parameter as required. If the parameter value is not available and no {@link #defaultValue()} is
	 * defined, an error will be thrown.
	 * <p>
	 * Default is <code>false</code>.
	 * </p>
	 * @return <code>true</code> if parameter is required, <code>false</code> (default) otherwise
	 */
	boolean required() default false;

	/**
	 * Get the default parameter value if no value is provided.
	 * <p>
	 * According to the parameter value type, a suitable {@link String} representation must be provided for the default
	 * value:
	 * <ul>
	 * <li>For numeric values, the <code>.</code> (dot) character must be used as decimal separator.</li>
	 * <li>For boolean values, only the words <code>true</code> and <code>false</code> are admitted.</li>
	 * <li>For {@link Enum} type values, the enumeration <code>name</code> must be used as parameter value.</li>
	 * <li>For temporal (date and time) type values, the ISO-8601 standard format must be used for the parameter
	 * value.</li>
	 * </ul>
	 * @return the default parameter value, or an empty String for none
	 */
	String defaultValue() default "";

}
