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
package com.holonplatform.vaadin.flow.components;

import java.util.Optional;

/**
 * Represents a component which may support a label through the {@link HasLabel} interface.
 *
 * @since 5.2.0
 */
public interface MayHaveLabel {

	/**
	 * Checks whether this component supports a label, which text can be handled using the {@link HasLabel} interface.
	 * @return If this component supports a label, return the {@link HasLabel} reference. An empty Optional is returned
	 *         otherwise.
	 */
	default Optional<HasLabel> hasLabel() {
		return Optional.empty();
	}

}
