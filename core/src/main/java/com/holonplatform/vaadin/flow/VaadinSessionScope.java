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
package com.holonplatform.vaadin.flow;

import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.ContextScope;
import com.vaadin.flow.server.VaadinSession;

/**
 * A {@link ContextScope} bound to current {@link VaadinSession}, looking up context resources using Vaadin Session
 * attributes.
 * <p>
 * This scope uses {@link VaadinSession#getCurrent()} to obtain the current Vaadin session.
 * </p>
 * 
 * @since 5.2.0
 */
public interface VaadinSessionScope extends ContextScope {

	/**
	 * Scope name
	 */
	public final static String NAME = "vaadin-session";

	/**
	 * Get the Vaadin Session scope from current context.
	 * @return The Vaadin Session scope, if available.
	 */
	static Optional<ContextScope> get() {
		return Context.get().scope(NAME);
	}

	/**
	 * Get the Vaadin Session scope from current context.
	 * @return The Vaadin Session scope
	 * @throws IllegalStateException If the Vaadin Session scope is not available
	 */
	static ContextScope require() {
		return get().orElseThrow(() -> new IllegalStateException("The Vaadin Session scope is not available"));
	}

}
