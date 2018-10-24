/*
 * Copyright 2016-2017 Axioma srl.
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * A {@link Converter}s that convert from {@link Number} types to {@link String} and back.
 * <p>
 * The String value is trimmed before conversion. Null or empty String values will be converted into <code>null</code>
 * Number values.
 * </p>
 * <p>
 * The {@link NumberFormat} to use for conversion is retrieved from {@link LocalizationContext}, if available as
 * {@link Context} resource. If a {@link LocalizationContext} is not available, default Number formats for current
 * Locale are used.
 * </p>
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public class DefaultStringToNumberConverter<T extends Number> implements StringToNumberConverter<T> {

	private static final long serialVersionUID = 2952012087662607453L;

	private static final String PATTERN_INTEGER = "[0-9]*";
	private static final String PATTERN_NEGATIVE_PREFIX = "-?";
	private static final String PATTERN_DECIMAL_PREFIX = "[0-9]+";
	private static final String PATTERN_DECIMAL_SUFFIX = "?[0-9]";
	private static final String PATTERN_GROUPS_PREFIX = "^[0-9]{1,3}(";
	private static final String PATTERN_NEGATIVE_GROUPS_PREFIX = "^-?[0-9]{1,3}(";
	private static final String PATTERN_GROUPS_SUFFIX = "?[0-9]{3})*";

	/**
	 * Number type
	 */
	private final Class<? extends T> numberType;

	/**
	 * Locale
	 */
	private final Locale locale;

	/**
	 * Number format
	 */
	private final NumberFormat numberFormat;

	/**
	 * Number format pattern
	 */
	private final String numberFormatPattern;

	/**
	 * Whether to use grouping
	 */
	private boolean useGrouping = true;

	/**
	 * Whether to allow negative numbers
	 */
	private boolean allowNegatives = true;

	/**
	 * Minimum decimals
	 */
	private int minDecimals = -1;

	/**
	 * Maximum decimals
	 */
	private int maxDecimals = -1;

	/**
	 * Default constructor. The {@link NumberFormat} to use will be obtained from the current {@link Locale}.
	 * @param numberType Number type
	 */
	public DefaultStringToNumberConverter(Class<? extends T> numberType) {
		this(numberType, null, null, null);
	}

	/**
	 * Constructor with fixed {@link Locale}.
	 * @param numberType Number type
	 * @param locale The {@link Locale} to use
	 */
	public DefaultStringToNumberConverter(Class<? extends T> numberType, Locale locale) {
		this(numberType, null, locale, null);
	}

	/**
	 * Constructor with fixed {@link NumberFormat}.
	 * @param numberType Number type
	 * @param numberFormat The {@link NumberFormat} to use
	 */
	public DefaultStringToNumberConverter(Class<? extends T> numberType, NumberFormat numberFormat) {
		this(numberType, numberFormat, null, null);
	}

	/**
	 * Constructor with fixed number format pattern.
	 * @param numberType Number type
	 * @param numberFormatPattern The number format pattern to use
	 */
	public DefaultStringToNumberConverter(Class<? extends T> numberType, String numberFormatPattern) {
		this(numberType, null, null, numberFormatPattern);
	}

	/**
	 * Constructor with fixed {@link NumberFormat} and {@link Locale}.
	 * @param numberType Number type
	 * @param numberFormat Optional {@link NumberFormat} to use
	 * @param locale Optional {@link Locale} to use
	 */
	private DefaultStringToNumberConverter(Class<? extends T> numberType, NumberFormat numberFormat, Locale locale,
			String numberFormatPattern) {
		super();
		ObjectUtils.argumentNotNull(numberType, "Number type must be not null");
		this.numberType = numberType;
		this.numberFormat = numberFormat;
		this.locale = locale;
		this.numberFormatPattern = numberFormatPattern;
	}

	/**
	 * Get the number type.
	 * @return the number type
	 */
	protected Class<? extends T> getNumberType() {
		return numberType;
	}

	/**
	 * Get the fixed {@link NumberFormat} to use for value conversions.
	 * @return Optional fixed {@link NumberFormat}
	 */
	protected Optional<NumberFormat> getNumberFormat() {
		return Optional.ofNullable(numberFormat);
	}

	/**
	 * Get the fixed number format pattern to use for value conversions.
	 * @return Optional fixed number format pattern
	 */
	protected Optional<String> getNumberFormatPattern() {
		return Optional.ofNullable(numberFormatPattern);
	}

	/**
	 * Get the fixed {@link Locale} to use.
	 * @return Optional fixed locale
	 */
	protected Optional<Locale> getLocale() {
		return Optional.ofNullable(locale);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#setUseGrouping(boolean)
	 */
	@Override
	public void setUseGrouping(boolean useGrouping) {
		this.useGrouping = useGrouping;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#isUseGrouping()
	 */
	@Override
	public boolean isUseGrouping() {
		return useGrouping;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#isAllowNegatives()
	 */
	@Override
	public boolean isAllowNegatives() {
		return allowNegatives;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#setAllowNegatives(boolean)
	 */
	@Override
	public void setAllowNegatives(boolean allowNegatives) {
		this.allowNegatives = allowNegatives;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#getMinDecimals()
	 */
	@Override
	public int getMinDecimals() {
		return minDecimals;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#setMinDecimals(int)
	 */
	@Override
	public void setMinDecimals(int minDecimals) {
		this.minDecimals = minDecimals;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#getMaxDecimals()
	 */
	@Override
	public int getMaxDecimals() {
		return maxDecimals;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#setMaxDecimals(int)
	 */
	@Override
	public void setMaxDecimals(int maxDecimals) {
		this.maxDecimals = maxDecimals;
	}

	/**
	 * Get the {@link Locale} to use for number formatting.
	 * <p>
	 * If a fixed {@link Locale} is configured, it is returned.
	 * </p>
	 * <p>
	 * Otherwise, the {@link Locale} is obtained from the given context, or using {@link #getCurrentLocale()} if not
	 * available. The default {@link Locale} is returned if a current {@link Locale} cannot be detected.
	 * </p>
	 * @param context Value context (not null)
	 * @return The {@link Locale} to use for number formatting
	 */
	protected Locale getLocale(ValueContext context) {
		if (getLocale().isPresent()) {
			return getLocale().get();
		}
		if (context == null) {
			return getCurrentLocale().orElseGet(() -> Locale.getDefault());
		}
		return context.getLocale().orElseGet(() -> getCurrentLocale().orElseGet(() -> Locale.getDefault()));
	}

	/**
	 * Get the current {@link Locale}, if available.
	 * <p>
	 * The current {@link Locale} is obtained form the current {@link LocalizationContext}, if available and localized,
	 * or from the current {@link UI}, if available and with a configured locale.
	 * </p>
	 * @return Optional current {@link Locale}
	 */
	protected Optional<Locale> getCurrentLocale() {
		// Check context
		Optional<Locale> contextLocale = LocalizationContext.getCurrent().filter(l -> l.isLocalized())
				.flatMap(l -> l.getLocale());
		if (contextLocale.isPresent()) {
			return contextLocale;
		}
		// Check UI
		if (UI.getCurrent() != null) {
			return Optional.ofNullable(UI.getCurrent().getLocale());
		}
		return Optional.empty();
	}

	/**
	 * Get the {@link NumberFormat} to use for value conversions.
	 * <p>
	 * When a fixed {@link NumberFormat} is configured, that one is always returned.
	 * </p>
	 * @param context The value context (not null)
	 * @return The {@link NumberFormat} to use for value conversions
	 */
	protected NumberFormat getNumberFormat(ValueContext context) {
		// check fixed pattern
		if (getNumberFormatPattern().isPresent()) {
			return new DecimalFormat(getNumberFormatPattern().get(), new DecimalFormatSymbols(getLocale(context)));
		}
		// check fixed NumberFormat
		if (getNumberFormat().isPresent()) {
			return getNumberFormat().get();
		}
		// get using locale
		NumberFormat numberFormat = getNumberFormat().orElseGet(
				() -> TypeUtils.isDecimalNumber(getNumberType()) ? NumberFormat.getNumberInstance(getLocale(context))
						: NumberFormat.getIntegerInstance(getLocale(context)));
		// check grouping
		if (!isUseGrouping()) {
			numberFormat.setGroupingUsed(false);
		}
		// min decimals
		if (getMinDecimals() > -1) {
			numberFormat.setMinimumFractionDigits(getMinDecimals());
		}
		// max decimals
		if (getMaxDecimals() > -1) {
			numberFormat.setMaximumFractionDigits(getMaxDecimals());
		} else {
			final int defaultMaximumFractionDigits = getDefaultMaximumFractionDigits();
			if (defaultMaximumFractionDigits > -1) {
				numberFormat.setMaximumFractionDigits(defaultMaximumFractionDigits);
			}
		}
		return numberFormat;
	}

	/**
	 * Get the default maximum fraction digits according to number type.
	 * @return Default maximum fraction digits, or <code>-1</code> if none
	 */
	private int getDefaultMaximumFractionDigits() {
		if (TypeUtils.isFloat(getNumberType())) {
			return 7;
		}
		if (TypeUtils.isDouble(getNumberType())) {
			return 16;
		}
		if (TypeUtils.isBigDecimal(getNumberType())) {
			return Integer.MAX_VALUE;
		}
		return -1;
	}

	/**
	 * Get the {@link DecimalFormatSymbols}, if available.
	 * @return Optional {@link DecimalFormatSymbols}
	 */
	protected Optional<DecimalFormatSymbols> getDecimalFormatSymbols() {
		// symbols
		Optional<DecimalFormatSymbols> symbols = getNumberFormat().filter(nf -> nf instanceof DecimalFormat)
				.map(nf -> (DecimalFormat) nf).map(df -> df.getDecimalFormatSymbols());
		if (symbols.isPresent()) {
			return symbols;
		}
		// by Locale
		symbols = getLocale().map(locale -> DecimalFormatSymbols.getInstance(locale));
		if (symbols.isPresent()) {
			return symbols;
		}
		// use current Locale
		return getCurrentLocale().map(locale -> DecimalFormatSymbols.getInstance(locale));
	}

	/**
	 * Get the grouping separator character, if available
	 * @return the grouping separator character, or empty if grouping is disabled
	 */
	@Override
	public Optional<Character> getGroupingSymbol() {
		if (!isUseGrouping()) {
			return Optional.empty();
		}
		return getDecimalFormatSymbols().map(dfs -> dfs.getGroupingSeparator());
	}

	/**
	 * Get the decimal separator character, if available
	 * @return the decimal separator character, or empty if the number type is not a decimal number
	 */
	@Override
	public Optional<Character> getDecimalSymbol() {
		if (!TypeUtils.isDecimalNumber(getNumberType())) {
			return Optional.empty();
		}
		return getDecimalFormatSymbols().map(dfs -> dfs.getDecimalSeparator());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter#getValidationPattern()
	 */
	@Override
	public String getValidationPattern() {
		final Character grouping = getGroupingSymbol().orElse(null);
		if (TypeUtils.isDecimalNumber(getNumberType())) {
			// decimals
			final Character decimals = getDecimalSymbol().orElse('.');
			// check max decimals
			final String decimalSuffix = (getMaxDecimals() > -1)
					? (PATTERN_DECIMAL_SUFFIX + "{0," + getMaxDecimals() + "}")
					: (PATTERN_DECIMAL_SUFFIX + "+");
			if (isUseGrouping() && grouping != null) {
				final String decimalPattern = escape(decimals) + decimalSuffix;

				return isAllowNegatives()
						? PATTERN_NEGATIVE_GROUPS_PREFIX + escape(grouping) + PATTERN_GROUPS_SUFFIX + decimalPattern
						: PATTERN_GROUPS_PREFIX + escape(grouping) + PATTERN_GROUPS_SUFFIX + decimalPattern;
			} else {
				final String decimalPattern = PATTERN_DECIMAL_PREFIX + escape(decimals) + decimalSuffix;
				return isAllowNegatives() ? PATTERN_NEGATIVE_PREFIX + decimalPattern : decimalPattern;
			}
		} else {
			// integers
			if (isUseGrouping() && grouping != null) {
				return isAllowNegatives() ? PATTERN_NEGATIVE_GROUPS_PREFIX + escape(grouping) + PATTERN_GROUPS_SUFFIX
						: PATTERN_GROUPS_PREFIX + escape(grouping) + PATTERN_GROUPS_SUFFIX;
			} else {
				return isAllowNegatives() ? PATTERN_NEGATIVE_PREFIX + PATTERN_INTEGER : PATTERN_INTEGER;
			}
		}
	}

	private static final List<Character> REGEX_RESERVED = Arrays.asList('.', '<', '(', '[', '{', '\\', '^', '-', '=',
			'$', '!', '|', ']', '}', ')', '?', '*', '+', '>');

	/**
	 * Escape given character for regex if required.
	 * @param c Character to escape
	 * @return Escaped character
	 */
	private static String escape(Character c) {
		if (c == null) {
			return null;
		}
		if (REGEX_RESERVED.contains(c)) {
			return "\\" + c;
		}
		return String.valueOf(c);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToModel(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public Result<T> convertToModel(String value, ValueContext context) {
		if (value != null && !value.trim().equals("")) {
			try {
				final T number = ConversionUtils
						.convertNumberToTargetClass(getNumberFormat(context).parse(value.trim()), getNumberType());
				if (number != null && !isAllowNegatives() && Math.signum(number.doubleValue()) < 0) {
					return Result.error("Negative numbers not allowed [" + value + "]");
				}
				return Result.ok(number);
			} catch (@SuppressWarnings("unused") Exception e) {
				return Result.error("Could not convert String value [" + value + "] to numeric type ["
						+ getNumberType().getName() + "]");
			}
		}
		// null value if String value was null or blank
		return Result.ok(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Converter#convertToPresentation(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public String convertToPresentation(T value, ValueContext context) {
		return (value != null) ? getNumberFormat(context).format(value) : null;
	}

	/**
	 * Default {@link Builder} implementation.
	 *
	 * @param <T> Number type
	 */
	public static class DefaultBuilder<T extends Number> implements Builder<T> {

		private final DefaultStringToNumberConverter<T> instance;

		/**
		 * Default constructor. The {@link NumberFormat} to use will be obtained from the current {@link Locale}.
		 * @param numberType Number type
		 */
		public DefaultBuilder(Class<? extends T> numberType) {
			super();
			this.instance = new DefaultStringToNumberConverter<>(numberType);
		}

		/**
		 * Constructor with fixed {@link Locale}.
		 * @param numberType Number type
		 * @param locale The {@link Locale} to use
		 */
		public DefaultBuilder(Class<? extends T> numberType, Locale locale) {
			super();
			this.instance = new DefaultStringToNumberConverter<>(numberType, locale);
		}

		/**
		 * Constructor with fixed {@link NumberFormat}.
		 * @param numberType Number type
		 * @param numberFormat The {@link NumberFormat} to use
		 */
		public DefaultBuilder(Class<? extends T> numberType, NumberFormat numberFormat) {
			super();
			this.instance = new DefaultStringToNumberConverter<>(numberType, numberFormat);
		}

		/**
		 * Constructor with fixed number format pattern.
		 * @param numberType Number type
		 * @param numberFormatPattern The number format pattern to use
		 */
		public DefaultBuilder(Class<? extends T> numberType, String numberFormatPattern) {
			super();
			this.instance = new DefaultStringToNumberConverter<>(numberType, numberFormatPattern);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter.Builder#grouping(boolean)
		 */
		@Override
		public Builder<T> grouping(boolean grouping) {
			this.instance.setUseGrouping(grouping);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter.Builder#negatives(boolean)
		 */
		@Override
		public Builder<T> negatives(boolean negatives) {
			this.instance.setAllowNegatives(negatives);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter.Builder#minDecimals(int)
		 */
		@Override
		public Builder<T> minDecimals(int minDecimals) {
			this.instance.setMinDecimals(minDecimals);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter.Builder#maxDecimals(int)
		 */
		@Override
		public Builder<T> maxDecimals(int maxDecimals) {
			this.instance.setMaxDecimals(maxDecimals);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter.Builder#build()
		 */
		@Override
		public StringToNumberConverter<T> build() {
			return this.instance;
		}

	}

}
