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
package com.holonplatform.vaadin.flow.internal.converters;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;

/**
 * {@link Date} to {@link LocalTime} converter.
 *
 * @since 5.2.0
 */
public class DateToLocalTimeConverter extends AbstractLocaleSupportConverter<LocalTime, Date> {

	private static final long serialVersionUID = 8774267222144386868L;

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.converter.Converter#convertToModel(java.lang.Object,
	 * com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public Result<Date> convertToModel(LocalTime value, ValueContext context) {
		if (value != null) {
			final Calendar calendar = Calendar.getInstance(getLocale(context));
			calendar.set(Calendar.YEAR, 0);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.HOUR_OF_DAY, value.getHour());
			calendar.set(Calendar.MINUTE, value.getMinute());
			calendar.set(Calendar.SECOND, value.getSecond());
			return Result.ok(calendar.getTime());
		}
		return Result.ok(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.data.converter.Converter#convertToPresentation(java.lang.Object,
	 * com.vaadin.flow.data.binder.ValueContext)
	 */
	@Override
	public LocalTime convertToPresentation(Date value, ValueContext context) {
		if (value != null) {
			final Calendar calendar = Calendar.getInstance(getLocale(context));
			calendar.setTime(value);
			return LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND));
		}
		return null;
	}

}
