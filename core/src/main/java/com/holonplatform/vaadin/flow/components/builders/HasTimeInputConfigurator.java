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

import java.time.Duration;

/**
 * Configurator for input component which supports a date with time.
 *
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
@Deprecated
public interface HasTimeInputConfigurator<C extends HasTimeInputConfigurator<C>> {

	/**
	 * Set whether to display a space between the <code>date</code>, <code>time</code> input fields.
	 * @param spacing whether to enable spacing
	 * @return this
	 * @deprecated Since 5.5.0 Vaadin DateTimePicker is used. Use CSS to configure spacing.
	 */
	@Deprecated
	C spacing(boolean spacing);

	/**
	 * Set the time input width.
	 * @param timeInputWidth the time input width to set
	 * @return this
	 * @deprecated Since 5.5.0 Vaadin DateTimePicker is used. Use CSS to configure time input width.
	 */
	@Deprecated
	C timeInputWidth(String timeInputWidth);

	/**
	 * Sets the time <em>step</em> using duration. It specifies the intervals for the displayed items in the time input
	 * dropdown and also the displayed time format.
	 * <p>
	 * The set step needs to evenly divide a day or an hour and has to be larger than 0 milliseconds. By default, the
	 * format is <code>hh:mm</code> (same as <code>Duration.ofHours(1)</code>).
	 * </p>
	 * <p>
	 * If the step is less than 60 seconds, the format will be changed to <code>hh:mm:ss</code> and it can be in
	 * <code>hh:mm:ss.fff</code> format, when the step is less than 1 second.
	 * </p>
	 * <p>
	 * <em>NOTE:</em> If the step is less than 900 seconds, the dropdown is hidden.
	 * </p>
	 * @param step the step to set, not <code>null</code> and should divide a day or an hour evenly
	 * @return this
	 * @since 5.2.3
	 * @deprecated Since 5.5.0 Vaadin DateTimePicker is used. Time step setting not supported.
	 */
	@Deprecated
	C timeStep(Duration step);

}
