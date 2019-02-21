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
package com.holonplatform.vaadin.flow.navigator.internal;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.holonplatform.vaadin.flow.navigator.NavigationParameterMapper;
import com.holonplatform.vaadin.flow.navigator.exceptions.InvalidNavigationParameterException;

/**
 * Navigation parameters serialization utility class.
 *
 * @since 5.2.0
 */
public final class NavigationParameterUtils implements Serializable {

	private static final long serialVersionUID = -4902803574414314795L;

	private static final Pattern QUERY_PARAMETER_SEPARATOR_PATTERN = Pattern.compile("&");

	private static final String ENCODE_DECODE_CHARSET = StandardCharsets.UTF_8.toString();

	private NavigationParameterUtils() {
	}

	/**
	 * Encode given parameter name or value to obtain a URL-safe representation, using the
	 * {@link #ENCODE_DECODE_CHARSET} charset.
	 * @param nameOrValue The parameter name or value to encode
	 * @return Encoded parameter name or value
	 * @throws InvalidNavigationParameterException If an encoding error occurs
	 */
	public static String encodeParameter(String nameOrValue) {
		if (nameOrValue != null) {
			try {
				return URLEncoder.encode(nameOrValue, ENCODE_DECODE_CHARSET);
			} catch (UnsupportedEncodingException e) {
				throw new InvalidNavigationParameterException(
						"Failed to encode parameter name or value [" + nameOrValue + "]", e);
			}
		}
		return nameOrValue;
	}

	/**
	 * Encode given parameter values to obtain a URL-safe representation, using the {@link #ENCODE_DECODE_CHARSET}
	 * charset.
	 * @param values The parameter values to encode
	 * @return Encoded parameter values
	 * @throws InvalidNavigationParameterException If an encoding error occurs
	 */
	public static List<String> encodeParameterValues(List<String> values) {
		if (values != null) {
			return values.stream().map(v -> encodeParameter(v)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Encode given parameter names and values map to obtain a URL-safe representation, using the
	 * {@link #ENCODE_DECODE_CHARSET} charset.
	 * @param parameters The parameter names and values map to encode
	 * @return Encoded parameters
	 * @throws InvalidNavigationParameterException If an encoding error occurs
	 */
	public static Map<String, List<String>> encodeParameters(Map<String, List<String>> parameters) {
		if (parameters != null) {
			return parameters.entrySet().stream().filter(entry -> entry.getKey() != null).collect(Collectors
					.toMap(entry -> encodeParameter(entry.getKey()), entry -> encodeParameterValues(entry.getValue())));
		}
		return Collections.emptyMap();
	}

	/**
	 * Decode given parameter name or value from a URL representation, using the {@link #ENCODE_DECODE_CHARSET} charset.
	 * @param nameOrValue The parameter name or value to decode
	 * @return Decoded parameter name or value
	 * @throws InvalidNavigationParameterException If a decoding error occurs
	 */
	public static String decodeParameter(String nameOrValue) {
		if (nameOrValue != null) {
			try {
				return URLDecoder.decode(nameOrValue, ENCODE_DECODE_CHARSET);
			} catch (UnsupportedEncodingException e) {
				throw new InvalidNavigationParameterException(
						"Failed to decode parameter name or value [" + nameOrValue + "]", e);
			}
		}
		return nameOrValue;
	}

	/**
	 * Decode given parameter values from a URL representation, using the {@link #ENCODE_DECODE_CHARSET} charset.
	 * @param values The parameter values to decode
	 * @return Decoded parameter values
	 * @throws InvalidNavigationParameterException If a decoding error occurs
	 */
	public static List<String> decodeParameterValues(List<String> values) {
		if (values != null) {
			return values.stream().map(v -> decodeParameter(v)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Decode given parameter names and values map from a URL representation, using the {@link #ENCODE_DECODE_CHARSET}
	 * charset.
	 * @param parameters The parameter names and values map to decode
	 * @return Decoded parameters
	 * @throws InvalidNavigationParameterException If a decoding error occurs
	 */
	public static Map<String, List<String>> decodeParameters(Map<String, List<String>> parameters) {
		if (parameters != null) {
			return parameters.entrySet().stream().filter(entry -> entry.getKey() != null).collect(Collectors
					.toMap(entry -> decodeParameter(entry.getKey()), entry -> decodeParameterValues(entry.getValue())));
		}
		return Collections.emptyMap();
	}

	/**
	 * Serialize given query parameters.
	 * @param parameters The query parameters
	 * @return The serialized query parameters
	 */
	public static Map<String, List<String>> serializeQueryParameters(Map<String, Object> parameters) {
		if (parameters != null && !parameters.isEmpty()) {
			final NavigationParameterMapper mapper = NavigationParameterMapper.get();
			final Map<String, List<String>> serialized = new HashMap<>(parameters.size());
			for (Entry<String, Object> e : parameters.entrySet()) {
				serialized.put(e.getKey(), mapper.serialize(e.getValue()));
			}
			return serialized;
		}
		return Collections.emptyMap();
	}

	/**
	 * Serialize given path parameters.
	 * @param <T> Parameter value type
	 * @param parameters The path parameters values
	 * @return The serialized path parameters values
	 */
	public static <T> List<String> serializePathParameters(List<T> parameters) {
		if (parameters != null && !parameters.isEmpty()) {
			return parameters.stream().map(v -> NavigationParameterMapper.get().serialize(Collections.singletonList(v)))
					.flatMap(List::stream).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Get the given URI query part as a map of query parameter name and values.
	 * @param queryPart The query part (excluding the <code>?</code> character)
	 * @return A map of query parameter name and values, empty if none
	 */
	public static Map<String, List<String>> getQueryParameters(String queryPart) {
		if (queryPart != null) {
			return QUERY_PARAMETER_SEPARATOR_PATTERN.splitAsStream(queryPart.trim())
					.map(s -> Arrays.copyOf(s.split("="), 2)).filter(pair -> {
						if (pair.length < 2) {
							return false;
						}
						if (pair[0] == null || pair[0].trim().equals("")) {
							return false;
						}
						if (pair[1] == null || pair[1].trim().equals("")) {
							return false;
						}
						return true;
					}).collect(Collectors.groupingBy(s -> s[0], Collectors.mapping(s -> s[1], Collectors.toList())));
		}
		return Collections.emptyMap();
	}

}
