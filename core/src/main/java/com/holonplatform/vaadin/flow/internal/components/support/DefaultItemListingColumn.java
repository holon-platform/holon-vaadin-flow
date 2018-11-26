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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.SortOrderProvider;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * Default {@link ItemListingColumn} implementation.
 * 
 * @param <T> Item type
 * @param <P> Item property type
 * @param <V> Property value type
 *
 * @since 5.2.0
 */
public class DefaultItemListingColumn<P, T, V> implements ItemListingColumn<P, T, V> {

	private static final long serialVersionUID = 8922982578042556430L;

	private final P property;
	private final String columnKey;

	private boolean readOnly = false;
	private boolean visible = true;
	private boolean resizable = false;
	private boolean frozen = false;
	private SortMode sortMode = SortMode.DEFAULT;
	private int flexGrow = 1;
	private ColumnAlignment alignment;
	private String width = null;
	private Localizable headerText;
	private Component headerComponent;
	private Localizable footerText;
	private Component footerComponent;
	private Renderer<T> renderer;
	private ValueProvider<T, String> valueProvider;
	private Comparator<T> comparator;
	private SortOrderProvider sortOrderProvider;
	private List<P> sortProperties;
	private List<Validator<V>> validators;
	private Input<V> editorInput;
	private Function<T, ? extends Component> editorComponent;
	private ValidationStatusHandler<ItemListing<T, P>, V, Input<V>> validationStatusHandler;

	/**
	 * Constructor.
	 * @param property Item property id (not null)
	 * @param columnKey Column key (not null)
	 * @param readOnly Whether the column is read-only by default
	 */
	public DefaultItemListingColumn(P property, String columnKey, boolean readOnly) {
		super();
		ObjectUtils.argumentNotNull(property, "Item property id must be not null");
		ObjectUtils.argumentNotNull(columnKey, "Column keymust be not null");
		this.property = property;
		this.columnKey = columnKey;
		this.readOnly = readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getProperty()
	 */
	@Override
	public P getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getColumnKey()
	 */
	@Override
	public String getColumnKey() {
		return columnKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#isResizable()
	 */
	@Override
	public boolean isResizable() {
		return resizable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setResizable(boolean)
	 */
	@Override
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#isFrozen()
	 */
	@Override
	public boolean isFrozen() {
		return frozen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setFrozen(boolean)
	 */
	@Override
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getWidth()
	 */
	@Override
	public Optional<String> getWidth() {
		return Optional.ofNullable(width);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setWidth(java.lang.String)
	 */
	@Override
	public void setWidth(String width) {
		this.width = width;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getFlexGrow()
	 */
	@Override
	public int getFlexGrow() {
		return flexGrow;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setFlexGrow(int)
	 */
	@Override
	public void setFlexGrow(int flexGrow) {
		this.flexGrow = flexGrow;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getAlignment()
	 */
	@Override
	public Optional<ColumnAlignment> getAlignment() {
		return Optional.ofNullable(alignment);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setAlignment(com.holonplatform.vaadin
	 * .flow.components.builders.ItemListingConfigurator.ColumnAlignment)
	 */
	@Override
	public void setAlignment(ColumnAlignment alignment) {
		this.alignment = alignment;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getHeaderText()
	 */
	@Override
	public Optional<Localizable> getHeaderText() {
		return Optional.ofNullable(headerText);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setHeaderText(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public void setHeaderText(Localizable text) {
		this.headerText = text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getHeaderComponent()
	 */
	@Override
	public Optional<Component> getHeaderComponent() {
		return Optional.ofNullable(headerComponent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setHeaderComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public void setHeaderComponent(Component component) {
		this.headerComponent = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getFooterComponent()
	 */
	@Override
	public Optional<Component> getFooterComponent() {
		return Optional.ofNullable(footerComponent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getFooterText()
	 */
	@Override
	public Optional<Localizable> getFooterText() {
		return Optional.ofNullable(footerText);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setFooterText(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public void setFooterText(Localizable text) {
		this.footerText = text;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setFooterComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public void setFooterComponent(Component component) {
		this.footerComponent = component;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getRenderer()
	 */
	@Override
	public Optional<Renderer<T>> getRenderer() {
		return Optional.ofNullable(renderer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setRenderer(com.vaadin.flow.data.
	 * renderer.Renderer)
	 */
	@Override
	public void setRenderer(Renderer<T> renderer) {
		this.renderer = renderer;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getValueProvider()
	 */
	@Override
	public Optional<ValueProvider<T, String>> getValueProvider() {
		return Optional.ofNullable(valueProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setValueProvider(com.vaadin.flow.
	 * function.ValueProvider)
	 */
	@Override
	public void setValueProvider(ValueProvider<T, String> valueProvider) {
		this.valueProvider = valueProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getSortMode()
	 */
	@Override
	public SortMode getSortMode() {
		return sortMode;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setSortMode(com.holonplatform.vaadin.
	 * flow.internal.components.support.ItemListingColumn.SortMode)
	 */
	@Override
	public void setSortMode(SortMode sortMode) {
		this.sortMode = (sortMode != null) ? sortMode : SortMode.DEFAULT;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getComparator()
	 */
	@Override
	public Optional<Comparator<T>> getComparator() {
		return Optional.ofNullable(comparator);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setComparator(java.util.Comparator)
	 */
	@Override
	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
		if (comparator != null) {
			setSortMode(SortMode.ENABLED);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getSortOrderProvider()
	 */
	@Override
	public Optional<SortOrderProvider> getSortOrderProvider() {
		return Optional.ofNullable(sortOrderProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setSortOrderProvider(com.vaadin.flow.
	 * component.grid.SortOrderProvider)
	 */
	@Override
	public void setSortOrderProvider(SortOrderProvider provider) {
		this.sortOrderProvider = provider;
		if (provider != null) {
			setSortMode(SortMode.ENABLED);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getSortProperties()
	 */
	@Override
	public List<P> getSortProperties() {
		return (sortProperties != null) ? sortProperties : Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setSortProperties(java.util.List)
	 */
	@Override
	public void setSortProperties(List<P> sortProperties) {
		this.sortProperties = sortProperties;
		if (sortProperties != null && !sortProperties.isEmpty()) {
			setSortMode(SortMode.ENABLED);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setEditorInput(com.holonplatform.
	 * vaadin. flow.components.Input)
	 */
	@Override
	public void setEditorInput(Input<V> editor) {
		this.editorInput = editor;
		if (editor != null && isReadOnly()) {
			setReadOnly(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getEditorInput()
	 */
	@Override
	public Optional<Input<V>> getEditorInput() {
		return Optional.ofNullable(editorInput);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setEditorComponent(java.util.function
	 * .Function)
	 */
	@Override
	public void setEditorComponent(Function<T, ? extends Component> editorComponent) {
		this.editorComponent = editorComponent;
		if (editorComponent != null && isReadOnly()) {
			setReadOnly(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getEditorComponent()
	 */
	@Override
	public Optional<Function<T, ? extends Component>> getEditorComponent() {
		return Optional.ofNullable(editorComponent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#addValidator(com.holonplatform.core.
	 * Validator)
	 */
	@Override
	public void addValidator(Validator<V> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		if (validators == null) {
			validators = new LinkedList<>();
		}
		validators.add(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getValidators()
	 */
	@Override
	public List<Validator<V>> getValidators() {
		return (validators != null) ? validators : Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#setValidationStatusHandler(com.
	 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
	 */
	@Override
	public void setValidationStatusHandler(
			ValidationStatusHandler<ItemListing<T, P>, V, Input<V>> validationStatusHandler) {
		this.validationStatusHandler = validationStatusHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.support.ItemListingColumn#getValidationStatusHandler()
	 */
	@Override
	public Optional<ValidationStatusHandler<ItemListing<T, P>, V, Input<V>>> getValidationStatusHandler() {
		return Optional.ofNullable(validationStatusHandler);
	}

}
