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
package com.holonplatform.vaadin.flow.internal.components.events;

import java.util.Collections;
import java.util.List;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupElementValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;

/**
 * Default {@link GroupValidationStatusEvent} implementation.
 * 
 * @param <S> Validation source type
 * @param <P> Property type
 * @param <E> Group element type
 *
 * @since 5.2.0
 */
public class DefaultGroupValidationStatusEvent<S, P, E extends HasComponent>
		implements GroupValidationStatusEvent<S, P, E> {

	private static final long serialVersionUID = -2220090071463737481L;

	private final S source;
	private final Status groupStatus;
	private final List<Localizable> errors;
	private final List<GroupElementValidationStatusEvent<S, P, E>> inputsValidationStatus;

	public DefaultGroupValidationStatusEvent(S source, Status groupStatus, List<Localizable> errors,
			List<GroupElementValidationStatusEvent<S, P, E>> inputsValidationStatus) {
		super();
		ObjectUtils.argumentNotNull(source, "Source must be not null");
		ObjectUtils.argumentNotNull(groupStatus, "Group status must be not null");
		this.source = source;
		this.groupStatus = groupStatus;
		this.errors = (errors != null) ? errors : Collections.emptyList();
		this.inputsValidationStatus = (inputsValidationStatus != null) ? inputsValidationStatus
				: Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupValidationStatusEvent#getSource()
	 */
	@Override
	public S getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupValidationStatusEvent#getGroupStatus()
	 */
	@Override
	public Status getGroupStatus() {
		return groupStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupValidationStatusEvent#getGroupErrors()
	 */
	@Override
	public List<Localizable> getGroupErrors() {
		return errors;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupValidationStatusEvent#
	 * getInputsValidationStatus()
	 */
	@Override
	public List<GroupElementValidationStatusEvent<S, P, E>> getInputsValidationStatus() {
		return inputsValidationStatus;
	}

}
