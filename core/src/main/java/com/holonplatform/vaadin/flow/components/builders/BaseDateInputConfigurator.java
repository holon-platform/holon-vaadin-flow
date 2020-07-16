package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.Input;

/**
 * Base date type {@link Input} components configurator.
 * 
 * @param <D> Actual date value type
 * @param <C> Concrete configurator type
 * 
 * @since 5.5.0
 */
public interface BaseDateInputConfigurator<D, C extends BaseDateInputConfigurator<D, C>>
		extends BaseTemporalInputConfigurator<D, C> {

	/**
	 * Set the date which should be visible when there is no value selected.
	 * @param initialPosition the initial position to set
	 * @return this
	 */
	C initialPosition(D initialPosition);

	/**
	 * Set whether to show a <em>clear</em> button which can be used to clear the
	 * input value.
	 * @param clearButtonVisible <code>true</code> to show the clear button,
	 *                           <code>false</code> to hide it
	 * @return this
	 * @since 5.3.0
	 */
	C clearButtonVisible(boolean clearButtonVisible);

}
