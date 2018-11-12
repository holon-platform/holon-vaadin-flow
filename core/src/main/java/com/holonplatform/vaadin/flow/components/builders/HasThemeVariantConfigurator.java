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

/**
 * Configurator for components which support theme variants.
 * 
 * @param <V> Variant type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasThemeVariantConfigurator<V extends Enum<V>, C extends HasThemeVariantConfigurator<V, C>> {

	/**
	 * Add given theme variants to the component.
	 * @param variants The theme variants to add
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	C withThemeVariants(V... variants);

}
