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

import java.util.Map;
import java.util.WeakHashMap;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Enum values {@link Localizable} captions registry.
 *
 * @since 5.2.0
 */
public class DefaultEnumCaptionRegistry {

	private static final Map<Enum<?>, Localizable> enumCaptions = new WeakHashMap<>();

	/**
	 * Get the localizable caption for given enum value.
	 * @param value The enum value (not null)
	 * @return the enum value localizable caption
	 */
	public static Localizable getEnumCaption(Enum<?> value) {
		ObjectUtils.argumentNotNull(value, "Enum value must be not null");
		return enumCaptions.computeIfAbsent(value, enm -> getEnumCaptionLocalizable(enm));
	}

	/**
	 * Get given enum localizable caption value, using the {@link Caption} annotation if available.
	 * @param value The enum value
	 * @return The enum localizable caption, using the {@link Caption} annotation if available or the enum value name if
	 *         not
	 */
	private static Localizable getEnumCaptionLocalizable(Enum<?> value) {
		try {
			final java.lang.reflect.Field field = value.getClass().getField(value.name());
			if (field.isAnnotationPresent(Caption.class)) {
				String captionMessage = AnnotationUtils.getStringValue(field.getAnnotation(Caption.class).value());
				return Localizable.builder().message((captionMessage != null) ? captionMessage : value.name())
						.messageCode(AnnotationUtils.getStringValue(field.getAnnotation(Caption.class).messageCode()))
						.build();
			}
		} catch (@SuppressWarnings("unused") NoSuchFieldException | SecurityException e) {
			return Localizable.of(value.name());
		}
		return Localizable.of(value.name());
	}

}
