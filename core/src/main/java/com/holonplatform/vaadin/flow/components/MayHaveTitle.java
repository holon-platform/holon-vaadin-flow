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
 * Represents a component which may support a title through the {@link HasTitle} interface.
 *
 * @since 5.2.0
 */
public interface MayHaveTitle {

	/**
	 * Checks whether this component supports a title, which text can be handled using the {@link HasTitle} interface.
	 * @return If this component supports a title, return the {@link HasTitle} reference. An empty Optional is returned
	 *         otherwise.
	 */
	default Optional<HasTitle> hasTitle() {
		return Optional.empty();
	}

}
