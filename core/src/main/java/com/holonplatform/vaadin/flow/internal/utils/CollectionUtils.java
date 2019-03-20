/*
 * Copyright 2016-2019 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.utils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Collections related utility class.
 *
 * @since 5.2.4
 */
public final class CollectionUtils implements Serializable {

	private static final long serialVersionUID = -4296571955394598665L;

	private CollectionUtils() {
	}

	/**
	 * Convert given {@link Iterable} into a {@link Set}.
	 * @param iterable The iterable to convert (maybe null)
	 * @return A linked {@link Set} with all the elements provided by the iterable, if any
	 */
	public static <T> Set<T> iterableAsSet(Iterable<T> iterable) {
		final Set<T> set = new LinkedHashSet<>();
		if (iterable != null) {
			for (T element : iterable) {
				set.add(element);
			}
		}
		return set;
	}

}
