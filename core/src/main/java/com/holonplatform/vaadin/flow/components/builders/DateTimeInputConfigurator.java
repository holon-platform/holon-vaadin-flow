/*
 * Copyright 2016-2019 Axioma srl.
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
package com.holonplatform.vaadin.flow.components.builders;

import java.time.ZoneId;
import java.util.Date;

import com.holonplatform.vaadin.flow.components.Input;

/**
 * {@link Date} with time type {@link Input} components configurator.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.2
 */
@SuppressWarnings("deprecation")
public interface DateTimeInputConfigurator<C extends DateTimeInputConfigurator<C>>
		extends BaseTemporalInputConfigurator<Date, C>, HasTimeInputConfigurator<C> {

	/**
	 * Set the time zone id to use.
	 * <p>
	 * If not set, the system default time zone is used.
	 * </p>
	 * @param zone the time zone id to use
	 * @return this
	 */
	C timeZone(ZoneId zone);

}
