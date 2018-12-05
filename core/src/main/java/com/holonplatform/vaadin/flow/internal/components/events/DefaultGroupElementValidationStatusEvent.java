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

import java.util.List;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupElementValidationStatusEvent;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;

/**
 * Default {@link GroupElementValidationStatusEvent} implementation.
 * 
 * @param <S> Validation source
 * @param <P> Property type
 * @param <E> Group element type
 *
 * @since 5.2.0
 */
public class DefaultGroupElementValidationStatusEvent<S, P, E extends HasComponent>
		extends DefaultValidationStatusEvent<S> implements GroupElementValidationStatusEvent<S, P, E> {

	private static final long serialVersionUID = -6525450825356074007L;

	private final P property;
	private final E element;

	public DefaultGroupElementValidationStatusEvent(S source, P property, E element, Status status,
			List<Localizable> errors) {
		super(source, status, errors);
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(element, "Group element must be not null");
		this.property = property;
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupElementValidationStatusEvent#
	 * getProperty()
	 */
	@Override
	public P getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.GroupValidationStatusHandler.GroupElementValidationStatusEvent#
	 * getElement()
	 */
	@Override
	public E getElement() {
		return element;
	}

}
