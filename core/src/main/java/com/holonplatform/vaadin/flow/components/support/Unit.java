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
package com.holonplatform.vaadin.flow.components.support;

import java.util.Optional;

/**
 * Component size units enumeration.
 * <p>
 * Mainly intended as a replacement for the Vaadin 7/8 Sizeable.Unit class to ensure API backward compatibility.
 * </p>
 *
 * @since 5.2.0
 */
public enum Unit {

	/**
	 * Unit code representing pixels.
	 */
	PIXELS("px"),
	/**
	 * Unit code representing points (1/72nd of an inch).
	 */
	POINTS("pt"),
	/**
	 * Unit code representing picas (12 points).
	 */
	PICAS("pc"),
	/**
	 * Unit code representing the font-size of the relevant font.
	 */
	EM("em"),
	/**
	 * Unit code representing the font-size of the root font.
	 */
	REM("rem"),
	/**
	 * Unit code representing the x-height of the relevant font.
	 */
	EX("ex"),
	/**
	 * Unit code representing millimeters.
	 */
	MM("mm"),
	/**
	 * Unit code representing centimeters.
	 */
	CM("cm"),
	/**
	 * Unit code representing inches.
	 */
	INCH("in"),
	/**
	 * Unit code representing in percentage of the containing element defined by terminal.
	 */
	PERCENTAGE("%");

	/*
	 * Symbol
	 */
	private String symbol;

	/**
	 * Constructor
	 * @param symbol Unit symbol
	 */
	private Unit(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Get the unit symbol.
	 * @return The unit symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	/**
	 * Get the given unit or the default one (Unit{@link #PIXELS}) if <code>null</code>.
	 * @param unit The unit
	 * @return The given unit or the default one (Unit{@link #PIXELS}) if <code>null</code>
	 */
	public static Unit orDefault(Unit unit) {
		return (unit != null) ? unit : Unit.PIXELS;
	}

	/**
	 * Get the {@link Unit} which corresponds to given symbol, if available.
	 * @param symbol Unit symbol (may be null)
	 * @return Optional {@link Unit} which corresponds to given symbol
	 */
	public static Optional<Unit> getUnitFromSymbol(String symbol) {
		if (symbol != null) {
			for (Unit unit : Unit.values()) {
				if (symbol.equals(unit.getSymbol())) {
					return Optional.of(unit);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Get the {@link String} representation of given size.
	 * @param size Size
	 * @return String representation
	 */
	public static String sizeToString(float size) {
		if (size % 1 == 0) {
			return String.valueOf(((Float) size).intValue());
		}
		return String.valueOf(size);
	}

}
