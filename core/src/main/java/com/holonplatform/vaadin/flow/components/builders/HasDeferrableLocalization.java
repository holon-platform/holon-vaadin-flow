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
 * Represents components or builders with deferrable localization support.
 * 
 * @since 5.2.0
 */
public interface HasDeferrableLocalization {

	/**
	 * Gets whether the localizable elements of the component (for the example the text or the title) should be
	 * localized in <em>deferred</em> mode.
	 * <p>
	 * When the deferred localization mode is enabled, the actual messages localization is triggered when the component
	 * is displayed in UI, typically when it is attached to a parent component. Otherwise, the messages localization is
	 * performed immediately at component build/configuration time.
	 * </p>
	 * @return whether the deferred localization mode is enabled
	 */
	boolean isDeferredLocalizationEnabled();

}
