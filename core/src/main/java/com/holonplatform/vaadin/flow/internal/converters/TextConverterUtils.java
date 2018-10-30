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
package com.holonplatform.vaadin.flow.internal.converters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for text converters.
 *
 * @since 5.2.0
 */
public final class TextConverterUtils implements Serializable {

	private static final long serialVersionUID = 2744272418301534811L;

	public static final List<Character> REGEX_RESERVED = Arrays.asList('.', '<', '(', '[', '{', '\\', '^', '-', '=',
			'$', '!', '|', ']', '}', ')', '?', '*', '+', '>');

	private TextConverterUtils() {
	}

	/**
	 * Escape given character for regex if required.
	 * @param c Character to escape
	 * @return Escaped character
	 */
	public static String escape(Character c) {
		if (c == null) {
			return null;
		}
		if (REGEX_RESERVED.contains(c)) {
			return "\\" + c;
		}
		return String.valueOf(c);
	}

}
