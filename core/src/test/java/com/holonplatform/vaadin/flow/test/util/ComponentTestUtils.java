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
package com.holonplatform.vaadin.flow.test.util;

import java.util.Collections;
import java.util.Set;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.MayHaveLabel;
import com.holonplatform.vaadin.flow.components.MayHavePlaceholder;
import com.holonplatform.vaadin.flow.components.MayHaveTitle;

public final class ComponentTestUtils {

	private ComponentTestUtils() {
	}

	public static Set<String> getClassNames(HasComponent component) {
		return component.hasStyle().map(c -> c.getClassNames()).map(v -> (Set<String>) v)
				.orElse(Collections.emptySet());
	}

	public static boolean isEnabled(HasComponent component) {
		return component.hasEnabled().map(c -> c.isEnabled()).orElse(false);
	}

	public static String getWidth(HasComponent component) {
		return component.hasSize().map(c -> c.getWidth()).orElse(null);
	}

	public static String getHeight(HasComponent component) {
		return component.hasSize().map(c -> c.getHeight()).orElse(null);
	}

	public static String getLabel(MayHaveLabel component) {
		return component.hasLabel().map(c -> c.getLabel()).orElse(null);
	}

	public static String getTitle(MayHaveTitle component) {
		return component.hasTitle().map(c -> c.getTitle()).orElse(null);
	}

	public static String getPlaceholder(MayHavePlaceholder component) {
		return component.hasPlaceholder().map(c -> c.getPlaceholder()).orElse(null);
	}

}
