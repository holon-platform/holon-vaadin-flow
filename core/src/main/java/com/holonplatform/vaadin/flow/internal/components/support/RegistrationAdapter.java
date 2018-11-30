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
package com.holonplatform.vaadin.flow.internal.components.support;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Vaadin to Holon {@link Registration} adapter.
 * 
 * @since 5.2.0
 */
public class RegistrationAdapter implements Registration {

	private static final long serialVersionUID = 910460619183674008L;

	private final com.vaadin.flow.shared.Registration registration;

	public RegistrationAdapter(com.vaadin.flow.shared.Registration registration) {
		super();
		ObjectUtils.argumentNotNull(registration, "Registration must be not null");
		this.registration = registration;
	}

	@Override
	public void remove() {
		registration.remove();
	}

	/**
	 * Adapt given registration to a {@link Registration}.
	 * @param registration The registration to adapt (not null)
	 * @return The adapted registration
	 */
	public static Registration adapt(com.vaadin.flow.shared.Registration registration) {
		return new RegistrationAdapter(registration);
	}

}
