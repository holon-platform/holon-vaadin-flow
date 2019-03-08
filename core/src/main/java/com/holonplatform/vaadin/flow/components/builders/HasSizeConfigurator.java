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

import com.holonplatform.vaadin.flow.components.support.Unit;
import com.vaadin.flow.component.HasSize;

/**
 * Configurator for {@link HasSize} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasSizeConfigurator<C extends HasSizeConfigurator<C>> {

	public static final String FULL_SIZE = "100%";

	/**
	 * Sets the width of the component.
	 * <p>
	 * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
	 * </p>
	 * <p>
	 * If the provided <code>width</code> value is <code>null</code> then width is removed.
	 * </p>
	 * @param width the width to set, may be <code>null</code>
	 * @return this
	 */
	C width(String width);

	/**
	 * Sets the height of the component.
	 * <p>
	 * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
	 * </p>
	 * <p>
	 * If the provided <code>height</code> value is <code>null</code> then height is removed.
	 * </p>
	 * @param height the height to set, may be <code>null</code>
	 * @return this
	 */
	C height(String height);

	/**
	 * Sets the width of the component, using a value and a size {@link Unit}.
	 * <p>
	 * Negative number implies unspecified size.
	 * </p>
	 * @param width The width of the component, negative values implies unspecified width
	 * @param unit The size unit used for the width
	 * @return this
	 */
	default C width(float width, Unit unit) {
		return (width < 0) ? width(null) : width(Unit.sizeToString(width) + Unit.orDefault(unit).getSymbol());
	}

	/**
	 * Sets the height of the component, using a value and a size {@link Unit}.
	 * <p>
	 * Negative number implies unspecified size.
	 * </p>
	 * @param height The height of the component, negative values implies unspecified height
	 * @param unit The size unit used for the height
	 * @return this
	 */
	default C height(float height, Unit unit) {
		return (height < 0) ? height(null) : height(Unit.sizeToString(height) + Unit.orDefault(unit).getSymbol());
	}

	/**
	 * Set the component width to <code>100%</code>.
	 * @return this
	 */
	default C fullWidth() {
		return width(FULL_SIZE);
	}

	/**
	 * Set the component heigth to <code>100%</code>.
	 * @return this
	 */
	default C fullHeight() {
		return height(FULL_SIZE);
	}

	/**
	 * Set the component width and heigth to <code>100%</code>.
	 * @return this
	 */
	default C fullSize() {
		width(FULL_SIZE);
		return height(FULL_SIZE);
	}

	/**
	 * Clears any defined width.
	 * @return this
	 */
	default C widthUndefined() {
		return width(null);
	}

	/**
	 * Clears any defined height.
	 * @return this
	 */
	default C heightUndefined() {
		return height(null);
	}

	/**
	 * Clears any size settings.
	 * @return this
	 */
	default C sizeUndefined() {
		width(null);
		return height(null);
	}

	/**
	 * Set the min-width of the component.
	 * <p>
	 * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
	 * <p>
	 * @param minWidth the min width to set, may be <code>null</code>
	 * @return this
	 */
	C minWidth(String minWidth);

	/**
	 * Set the max-width of the component.
	 * <p>
	 * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
	 * <p>
	 * @param maxWidth the max width to set, may be <code>null</code>
	 * @return this
	 */
	C maxWidth(String maxWidth);

	/**
	 * Set the min-height of the component.
	 * <p>
	 * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
	 * <p>
	 * @param minHeight the min height to set, may be <code>null</code>
	 * @return this
	 */
	C minHeight(String minHeight);

	/**
	 * Set the max-height of the component.
	 * <p>
	 * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
	 * <p>
	 * @param maxHeight the max height to set, may be <code>null</code>
	 * @return this
	 */
	C maxHeight(String maxHeight);

}
