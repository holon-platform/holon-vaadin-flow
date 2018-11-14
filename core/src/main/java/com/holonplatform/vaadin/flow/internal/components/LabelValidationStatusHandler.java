/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;

/**
 * A {@link ValidationStatusHandler} which uses a {@link HasText} component to notify validation errors.
 *
 * @param <S> Validation source
 * @param <V> Validation value type
 * @param <C> Value component to which the validation event refers
 * @param <L> Label component type
 *
 * @since 5.2.0
 */
public class LabelValidationStatusHandler<S, V, C extends ValueComponent<V>, L extends Component & HasText>
		implements ValidationStatusHandler<S, V, C> {

	private final L label;
	private final boolean hideWhenValid;

	/**
	 * Constructor
	 * @param label Status component (not null)
	 * @param hideWhenValid <code>true</code> to hide the component when the validation status is not invalid
	 */
	public LabelValidationStatusHandler(L label, boolean hideWhenValid) {
		super();
		ObjectUtils.argumentNotNull(label, "Status label must be not null");
		this.label = label;
		this.hideWhenValid = hideWhenValid;
		// check hidden
		if (hideWhenValid) {
			label.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValidationStatusHandler#validationStatusChange(com.holonplatform.vaadin.
	 * components.ValidationStatusHandler.ValidationStatusEvent)
	 */
	@Override
	public void validationStatusChange(ValidationStatusEvent<S, V, C> statusChangeEvent) {
		label.setText(statusChangeEvent.getErrorMessage());
		if (hideWhenValid) {
			// Only show the label when validation has failed
			label.setVisible(statusChangeEvent.isInvalid());
		}
	}

}
