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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.SortOrderProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * {@link ItemListing} column configuration.
 * 
 * @param <P> Item property type
 * @param <T> Item type
 * @param <V> Property value type
 *
 * @since 5.2.0
 */
public interface ItemListingColumn<P, T, V> extends Serializable {

	/**
	 * Get the item property id.
	 * @return the item property id (never null)
	 */
	P getProperty();

	/**
	 * Get the column key.
	 * @return the column key (never null)
	 */
	String getColumnKey();

	/**
	 * Get whether the column is read only.
	 * @return whether the column is read only
	 */
	boolean isReadOnly();

	/**
	 * Set the column as read-only.
	 * @param readOnly whether the column is read-only
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Gets whether the column is visible.
	 * @return whether the column is visible
	 */
	boolean isVisible();

	/**
	 * Sets whether the column is visible.
	 * @param visible whether the column is visible
	 */
	void setVisible(boolean visible);

	/**
	 * Gets whether this column is user-resizable.
	 * @return whether this column is user-resizable
	 */
	boolean isResizable();

	/**
	 * Sets whether this column is user-resizable.
	 * @param resizable whether to allow user resizing of this column
	 */
	void setResizable(boolean resizable);

	/**
	 * Gets the column frozen state.
	 * @return whether this column is frozen
	 */
	boolean isFrozen();

	/**
	 * Sets the column frozen state.
	 * @param frozen whether to freeze or unfreeze this column
	 */
	void setFrozen(boolean frozen);

	/**
	 * Get the width of this column as a CSS-string.
	 * @return Optional width of this column as a CSS-string
	 */
	Optional<String> getWidth();

	/**
	 * Sets the width of this column as a CSS-string.
	 * @param width the width to set this column to
	 */
	void setWidth(String width);

	/**
	 * Gets the flex grow value, by default 1.
	 * @return the flex grow value, by default 1
	 */
	int getFlexGrow();

	/**
	 * Sets the flex grow ratio for this column. When set to 0, column width is fixed.
	 * @param flexGrow the flex grow ratio
	 */
	void setFlexGrow(int flexGrow);

	/**
	 * Get the column alignment.
	 * @return Optional column alignment
	 */
	Optional<ColumnAlignment> getAlignment();

	/**
	 * Set the column alignment.
	 * @param alignment the column alignment to set
	 */
	void setAlignment(ColumnAlignment alignment);

	/**
	 * Gets the column header text.
	 * @return Optional column header text
	 */
	Optional<Localizable> getHeaderText();

	/**
	 * Sets the column header text.
	 * @param text the column header text
	 */
	void setHeaderText(Localizable text);

	/**
	 * Gets the column header component.
	 * @return Optional column header component
	 */
	Optional<Component> getHeaderComponent();

	/**
	 * Sets the column header component.
	 * @param component the column header component
	 */
	void setHeaderComponent(Component component);

	/**
	 * Get the renderer for this column, if available.
	 * @return Optional renderer used for this column
	 */
	Optional<Renderer<T>> getRenderer();

	/**
	 * Set the renderer for this column.
	 * @param renderer the renderer to set
	 */
	void setRenderer(Renderer<T> renderer);

	/**
	 * Get the {@link String} value provider.
	 * @return Optional presentation provider
	 */
	Optional<ValueProvider<T, String>> getValueProvider();

	/**
	 * Set the {@link String} value provider.
	 * @param valueProvider the presentation provider to set
	 */
	void setValueProvider(ValueProvider<T, String> valueProvider);

	/**
	 * Gest the column sort mode.
	 * @return the column sort mode
	 */
	SortMode getSortMode();

	/**
	 * Sets the column sort mode.
	 * @param sortMode the column sort mode
	 */
	void setSortMode(SortMode sortMode);

	/**
	 * Gets the comparator to use with in-memory sorting for this column.
	 * @return Optional comparator for this column
	 */
	Optional<Comparator<T>> getComparator();

	/**
	 * Sets a comparator to use with in-memory sorting with this column.
	 * @param comparator the comparator to use with in-memory sorting
	 */
	void setComparator(Comparator<T> comparator);

	/**
	 * Gets the sort orders when sorting this column.
	 * @return Optional function to use when generating sort orders
	 */
	Optional<SortOrderProvider> getSortOrderProvider();

	/**
	 * Sets the sort orders when sorting this column.
	 * @param provider the function to use when generating sort orders
	 */
	void setSortOrderProvider(SortOrderProvider provider);

	/**
	 * Sets strings describing back end properties to be used when sorting this column.
	 * @param properties the array of strings describing backend properties (not null)
	 */
	default void setSortProperty(String... properties) {
		ObjectUtils.argumentNotNull(properties, "Sort properties must not be null");
		setSortOrderProvider(dir -> Arrays.stream(properties).map(s -> new QuerySortOrder(s, dir)));
	}

	/**
	 * Get the properties to use to implement the sort logic for this column.
	 * @return The sort properties, empty if none
	 */
	List<P> getSortProperties();

	/**
	 * Set the properties to use to implement the sort logic for this column.
	 * @param sortProperties The sort properties to set
	 */
	void setSortProperties(List<P> sortProperties);

	/**
	 * Set the column editor.
	 * @param editor the column editor to set
	 */
	void setEditor(Input<V> editor);

	/**
	 * Get the column editor, if available.
	 * @return Optional column editor
	 */
	Optional<Input<V>> getEditor();

	/**
	 * Add a property editor validator.
	 * @param validator The validator to add (not null)
	 */
	void addValidator(Validator<V> validator);

	/**
	 * Get the property editor validators.
	 * @return The property editor validators, empty if none
	 */
	List<Validator<V>> getValidators();

	/**
	 * Column sort mode
	 */
	public enum SortMode {

		DEFAULT,

		ENABLED,

		DISABLED

	}

}
