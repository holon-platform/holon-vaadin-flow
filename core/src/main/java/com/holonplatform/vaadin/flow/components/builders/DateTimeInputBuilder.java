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
package com.holonplatform.vaadin.flow.components.builders;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultDateTimeInputBuilder;

/**
 * A {@link BaseDateInputBuilder} to create {@link Date} type {@link Input} components.
 *
 * @since 5.2.0
 */
public interface DateTimeInputBuilder
		extends BaseDateInputBuilder<Date, DateTimeInputBuilder>, HasTimeInputConfigurator<DateTimeInputBuilder> {

	/**
	 * Set the time zone id to use.
	 * <p>
	 * If not set, the system default time zone is used.
	 * </p>
	 * @param zone the time zone id to use
	 * @return this
	 */
	DateTimeInputBuilder timeZone(ZoneId zone);

	/**
	 * Set the time zone to use
	 * @param timeZone the time zone to use
	 * @return this
	 * @deprecated Use {@link #timeZone(ZoneId)}
	 */
	@Deprecated
	default DateTimeInputBuilder timeZone(TimeZone timeZone) {
		return timeZone((timeZone != null) ? timeZone.toZoneId() : null);
	}

	/**
	 * Create a new {@link DateTimeInputBuilder}.
	 * @return A new {@link DateTimeInputBuilder}
	 */
	static DateTimeInputBuilder create() {
		return new DefaultDateTimeInputBuilder();
	}

}
