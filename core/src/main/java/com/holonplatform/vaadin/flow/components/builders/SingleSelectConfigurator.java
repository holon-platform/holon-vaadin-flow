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

import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableSingleSelect;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPropertySingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSingleSelectInputBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;

/**
 * {@link SingleSelect} input builder using a {@link Select} as input component.
 *
 * @param <T> Value type
 * @param <ITEM> Item type
 * @param <B> Concrete builder type
 *
 * @since 5.2.3
 */
public interface SingleSelectConfigurator<T, ITEM, B extends SingleSelectConfigurator<T, ITEM, B>>
		extends SingleSelectableInputConfigurator<T, ITEM, B>, HasSizeConfigurator<B>, HasLabelConfigurator<B>,
		HasPlaceholderConfigurator<B>, HasAutofocusConfigurator<B>, FocusableConfigurator<Component, B>,
		HasItemEnableConfigurator<ITEM, B> {

	/**
	 * Sets the {@link ComponentRenderer} responsible to render the individual items in the list of possible choices.
	 * The renderer is applied to each item to create a component which represents the item.
	 * @param renderer the item renderer, not <code>null</code>
	 * @return this
	 */
	B renderer(ComponentRenderer<? extends Component, ITEM> renderer);

	/**
	 * Sets the item label generator. It generates the text that is shown in the input part for the item when it has
	 * been selected.
	 * @param itemLabelGenerator The item label generator to set
	 * @return this
	 */
	B itemLabelGenerator(Function<ITEM, String> itemLabelGenerator);

	/**
	 * Sets whether the user is allowed to select nothing. When set to <code>true</code>, a special empty item is shown
	 * to the user.
	 * <p>
	 * Default is <code>false</code>. The empty selection item can be customized with
	 * {@link #emptySelectionCaption(Localizable)}.
	 * </p>
	 * @param emptySelectionAllowed <code>true</code> to allow not selecting anything, <code>false</code> to require
	 *        selection
	 * @return this
	 */
	B emptySelectionAllowed(boolean emptySelectionAllowed);

	/**
	 * Sets the empty selection caption when {@link #emptySelectionAllowed(boolean)} has been enabled. The caption is
	 * shown for the empty selection item in the drop down.
	 * <p>
	 * When the empty selection item is selected, the select shows the value provided by
	 * {@link #itemLabelGenerator(Function)} for the {@code null} item, or the string set with
	 * {@link #placeholder(Localizable)} or an empty string if not placeholder is set.
	 * </p>
	 * <p>
	 * Default is an empty string "", which will show the place holder when selected.
	 * </p>
	 * @param emptySelectionCaption The empty selection caption to set (not null)
	 * @return this
	 * @see #emptySelectionAllowed(boolean)
	 * @see LocalizationProvider
	 */
	B emptySelectionCaption(Localizable emptySelectionCaption);

	/**
	 * Sets the empty selection caption when {@link #emptySelectionAllowed(boolean)} has been enabled. The caption is
	 * shown for the empty selection item in the drop down.
	 * @param emptySelectionCaption The empty selection caption to set
	 * @return this
	 * @see #emptySelectionCaption(Localizable)
	 */
	default B emptySelectionCaption(String emptySelectionCaption) {
		return emptySelectionCaption(
				(emptySelectionCaption == null) ? null : Localizable.builder().message(emptySelectionCaption).build());
	}

	/**
	 * Sets the empty selection caption when {@link #emptySelectionAllowed(boolean)} has been enabled. The caption is
	 * shown for the empty selection item in the drop down.
	 * @param defaultCaption Default empty selection caption if no translation is available for given
	 *        <code>messageCode</code>
	 * @param messageCode Empty selection caption translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationProvider
	 */
	default B emptySelectionCaption(String defaultCaption, String messageCode, Object... arguments) {
		return emptySelectionCaption(Localizable.builder().message((defaultCaption == null) ? "" : defaultCaption)
				.messageCode(messageCode).messageArguments(arguments).build());
	}

	// ------- specific configurators

	/**
	 * {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface SingleSelectInputConfigurator<T, ITEM, D extends DatastoreDataProviderConfigurator<ITEM, D>, C extends SingleSelectInputConfigurator<T, ITEM, D, C>>
			extends SingleSelectConfigurator<T, ITEM, C>, HasBeanDatastoreDataProviderConfigurator<ITEM, D, C> {

	}

	/**
	 * {@link SingleSelect} input configurator with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastoreSingleSelectInputConfigurator<T, ITEM, C extends DatastoreSingleSelectInputConfigurator<T, ITEM, C>>
			extends SingleSelectConfigurator<T, ITEM, C>, DatastoreDataProviderConfigurator<ITEM, C> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input configurator.
	 *
	 * @param <T> Value type
	 * @param <D> Datastore data source configurator type
	 * @param <C> Concrete configurator type
	 */
	public interface PropertySingleSelectInputConfigurator<T, D extends DatastoreDataProviderConfigurator<PropertyBox, D>, C extends PropertySingleSelectInputConfigurator<T, D, C>>
			extends SingleSelectConfigurator<T, PropertyBox, C>, HasPropertyBoxDatastoreDataProviderConfigurator<D, C>,
			PropertySelectInputConfigurator<T, T, C> {

		/**
		 * Set the data provider which acts as items data source, using given {@link Datastore} as backend data handler,
		 * given {@link DataTarget} as query target.
		 * <p>
		 * The query projection property set will be represented by the selection property only.
		 * </p>
		 * @param datastore The {@link Datastore} to use (not null)
		 * @param target The {@link DataTarget} to use as query target (not null)
		 * @return An extended builder which allow further data provider configuration, for example to add fixed
		 *         {@link QueryFilter} and {@link QuerySort}.
		 * @see DatastoreDataProviderConfigurator
		 */
		D dataSource(Datastore datastore, DataTarget<?> target);

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input configurator mode with
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <C> Concrete configurator type
	 */
	public interface DatastorePropertySingleSelectInputConfigurator<T, C extends DatastorePropertySingleSelectInputConfigurator<T, C>>
			extends SingleSelectConfigurator<T, PropertyBox, C>, DatastoreDataProviderConfigurator<PropertyBox, C>,
			PropertySelectInputConfigurator<T, T, C> {

	}

	// ------- actual builders

	/**
	 * {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableSingleSelectInputBuilder<T, ITEM> extends
			SingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreSingleSelectInputBuilder<T, ITEM>, ValidatableSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder with validation and {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface ValidatableDatastoreSingleSelectInputBuilder<T, ITEM> extends
			DatastoreSingleSelectInputConfigurator<T, ITEM, ValidatableDatastoreSingleSelectInputBuilder<T, ITEM>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastoreSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface SingleSelectInputBuilder<T, ITEM> extends
			SingleSelectInputConfigurator<T, ITEM, DatastoreSingleSelectInputBuilder<T, ITEM>, SingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, SingleSelectInputBuilder<T, ITEM>, ValidatableSingleSelectInputBuilder<T, ITEM>> {

	}

	/**
	 * {@link SingleSelect} input builder with {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 */
	public interface DatastoreSingleSelectInputBuilder<T, ITEM>
			extends DatastoreSingleSelectInputConfigurator<T, ITEM, DatastoreSingleSelectInputBuilder<T, ITEM>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastoreSingleSelectInputBuilder<T, ITEM>, ValidatableDatastoreSingleSelectInputBuilder<T, ITEM>> {

	}

	// ------- builders using property model

	/**
	 * {@link Property} model based {@link SingleSelect} input builder with validation support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatablePropertySingleSelectInputBuilder<T> extends
			PropertySingleSelectInputConfigurator<T, ValidatableDatastorePropertySingleSelectInputBuilder<T>, ValidatablePropertySingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatablePropertySingleSelectInputBuilder<T>> {
	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder with validation and
	 * {@link DatastoreDataProviderConfigurator} support.
	 *
	 * @param <T> Value type
	 */
	public interface ValidatableDatastorePropertySingleSelectInputBuilder<T> extends
			DatastorePropertySingleSelectInputConfigurator<T, ValidatableDatastorePropertySingleSelectInputBuilder<T>>,
			BaseValidatableInputBuilder<T, ValidatableSingleSelect<T>, ValidatableDatastorePropertySingleSelectInputBuilder<T>> {

	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder.
	 *
	 * @param <T> Value type
	 */
	public interface PropertySingleSelectInputBuilder<T> extends
			PropertySingleSelectInputConfigurator<T, DatastorePropertySingleSelectInputBuilder<T>, PropertySingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, PropertySingleSelectInputBuilder<T>, ValidatablePropertySingleSelectInputBuilder<T>> {
	}

	/**
	 * {@link Property} model based {@link SingleSelect} input builder with {@link DatastoreDataProviderConfigurator}
	 * support.
	 *
	 * @param <T> Value type
	 */
	public interface DatastorePropertySingleSelectInputBuilder<T>
			extends DatastorePropertySingleSelectInputConfigurator<T, DatastorePropertySingleSelectInputBuilder<T>>,
			InputBuilder<T, ValueChangeEvent<T>, SingleSelect<T>, ValidatableSingleSelect<T>, DatastorePropertySingleSelectInputBuilder<T>, ValidatableDatastorePropertySingleSelectInputBuilder<T>> {

	}

	// ------ builders

	/**
	 * Get a new {@link SingleSelectInputBuilder} to create a {@link SingleSelect} which uses a {@link Select} as input
	 * component.
	 * @param <T> Value type
	 * @param <ITEM> Item type
	 * @param type Selection value type (not null)
	 * @param itemType Selection items type (not null)
	 * @param itemConverter The item converter to use (not null)
	 * @return A new {@link SingleSelectInputBuilder}
	 */
	static <T, ITEM> SingleSelectInputBuilder<T, ITEM> create(Class<T> type, Class<ITEM> itemType,
			ItemConverter<T, ITEM> itemConverter) {
		return new DefaultSingleSelectInputBuilder<>(type, itemType, itemConverter);
	}

	/**
	 * Get a new {@link SingleSelectInputBuilder} to create a {@link SingleSelect} which uses a {@link Select} as input
	 * component.
	 * @param type Selection value type (not null)
	 * @param <T> Value type
	 * @return A new {@link SingleSelectInputBuilder}
	 */
	static <T> SingleSelectInputBuilder<T, T> create(Class<T> type) {
		return new DefaultSingleSelectInputBuilder<>(type, type, ItemConverter.identity());
	}

	// property

	/**
	 * Get a new {@link PropertySingleSelectInputBuilder} to create a {@link Property} model based filterable
	 * {@link SingleSelect}, which uses a {@link Select} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @return A new {@link PropertySingleSelectInputBuilder}
	 */
	static <T> PropertySingleSelectInputBuilder<T> create(final Property<T> selectionProperty) {
		return new DefaultPropertySingleSelectInputBuilder<>(selectionProperty);
	}

	/**
	 * Get a new {@link PropertySingleSelectInputBuilder} to create a {@link Property} model based filterable
	 * {@link SingleSelect}, which uses a {@link Select} as input component.
	 * @param <T> Value type
	 * @param selectionProperty The property to use to represent the selection value (not null)
	 * @param itemConverter The function to use to convert a selection value into the corresponding {@link PropertyBox}
	 *        item
	 * @return A new {@link PropertySingleSelectInputBuilder}
	 */
	static <T> PropertySingleSelectInputBuilder<T> create(final Property<T> selectionProperty,
			Function<T, Optional<PropertyBox>> itemConverter) {
		return new DefaultPropertySingleSelectInputBuilder<>(selectionProperty, itemConverter);
	}

}
