/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyBinding;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.PropertyValueComponentSource;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.shared.Registration;

/**
 * Default {@link PropertyInputForm} implementation.
 * 
 * @param <C> Content component type.
 *
 * @since 5.2.0
 */
public class DefaultPropertyInputForm<C extends Component>
		extends AbstractComposablePropertyForm<C, Input<?>, PropertyValueComponentSource> implements PropertyInputForm {

	private static final long serialVersionUID = -4202049108110710744L;

	/**
	 * Backing input group
	 */
	private PropertyInputGroup inputGroup;

	/**
	 * Value components source
	 */
	private PropertyValueComponentSource valueComponentSource;

	/**
	 * Constructor.
	 * @param content Form content (not null)
	 */
	public DefaultPropertyInputForm(C content) {
		super(content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractComposableForm#getComponentSource()
	 */
	@Override
	protected PropertyValueComponentSource getComponentSource() {
		return valueComponentSource;
	}

	/**
	 * Sets the backing input group.
	 * @param <G> PropertyInputGroup type
	 * @param inputGroup the input group to set
	 */
	protected <G extends PropertyInputGroup & PropertyValueComponentSource> void setInputGroup(G inputGroup) {
		ObjectUtils.argumentNotNull(inputGroup, "PropertyViewGroup must be not null");
		this.inputGroup = inputGroup;
		this.valueComponentSource = inputGroup;
	}

	/**
	 * Get the backing input group.
	 * @return the backing input group
	 */
	protected PropertyInputGroup getInputGroup() {
		return inputGroup;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return getContent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#clear()
	 */
	@Override
	public void clear() {
		getInputGroup().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return getInputGroup().getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewGroup#setValue(com.holonplatform.core.property.PropertyBox)
	 */
	@Override
	public void setValue(PropertyBox propertyBox) {
		getInputGroup().setValue(propertyBox);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputBinder#refresh()
	 */
	@Override
	public void refresh() {
		getInputGroup().refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.PropertyInputBinder#refresh(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> boolean refresh(Property<T> property) {
		return getInputGroup().refresh(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#getValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		return getInputGroup().getValue(validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#getValueIfValid()
	 */
	@Override
	public Optional<PropertyBox> getValueIfValid() {
		return getInputGroup().getValueIfValid();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.PropertyInputGroup#setValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@Override
	public void setValue(PropertyBox propertyBox, boolean validate) {
		getInputGroup().setValue(propertyBox, validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		getInputGroup().setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getInputGroup().setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		getInputGroup().validate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#getProperties()
	 */
	@Override
	public Iterable<Property<?>> getProperties() {
		return getInputGroup().getProperties();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#hasProperty(com.holonplatform.core.property.Property)
	 */
	@Override
	public boolean hasProperty(Property<?> property) {
		return getInputGroup().hasProperty(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertySetBound#propertyStream()
	 */
	@Override
	public Stream<Property<?>> propertyStream() {
		return getInputGroup().propertyStream();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getInputGroup().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.components.
	 * ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<PropertyBox> listener) {
		return getInputGroup().addValueChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#getViewComponents()
	 */
	@Override
	public Iterable<Input<?>> getInputs() {
		return getInputGroup().getInputs();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyViewSource#getViewComponent(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		return getInputGroup().getInput(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.PropertyViewSource#stream()
	 */
	@Override
	public <T> Stream<PropertyBinding<T, Input<T>>> stream() {
		return getInputGroup().stream();
	}

	// Builder

	/**
	 * Default {@link PropertyInputFormBuilder}.
	 * @param <C> Content type
	 */
	public static class DefaultBuilder<C extends Component> extends
			AbstractComponentConfigurator<C, PropertyInputFormBuilder<C>> implements PropertyInputFormBuilder<C> {

		private final DefaultPropertyInputForm<C> instance;

		private final DefaultPropertyInputGroup.InternalBuilder inputGroupBuilder;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public <P extends Property<?>> DefaultBuilder(C content, Iterable<P> properties) {
			super(content);
			this.instance = new DefaultPropertyInputForm<>(content);
			this.inputGroupBuilder = new DefaultPropertyInputGroup.InternalBuilder(properties);

			// setup default composer
			if (instance.getComposer() == null && content instanceof HasComponents) {
				instance.setComposer((Composer) Composable.componentContainerComposer());
			}
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
		 */
		@Override
		protected PropertyInputFormBuilder<C> getConfigurator() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			inputGroupBuilder.bind(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withPostProcessor(java.util.function.
		 * BiConsumer)
		 */
		@Override
		public PropertyInputFormBuilder<C> withPostProcessor(BiConsumer<Property<?>, Input<?>> postProcessor) {
			inputGroupBuilder.withPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withValueChangeListener(com.holonplatform
		 * .vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public PropertyInputFormBuilder<C> withValueChangeListener(ValueChangeListener<PropertyBox> listener) {
			inputGroupBuilder.withValueChangeListener(listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#readOnly(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> readOnly(Property<T> property) {
			inputGroupBuilder.readOnly(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> required(Property<T> property) {
			inputGroupBuilder.required(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> required(Property<T> property, Localizable message) {
			inputGroupBuilder.required(property, message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#defaultValue(com.holonplatform.core.
		 * property.Property, com.holonplatform.vaadin.flow.components.PropertyInputGroup.DefaultValueProvider)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> defaultValue(Property<T> property,
				DefaultValueProvider<T> defaultValueProvider) {
			inputGroupBuilder.defaultValue(property, defaultValueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValidator(Property<T> property, Validator<T> validator) {
			inputGroupBuilder.withValidator(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public PropertyInputFormBuilder<C> withValidator(Validator<PropertyBox> validator) {
			inputGroupBuilder.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform
		 * .core.property.Property, com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> validationStatusHandler(Property<T> property,
				ValidationStatusHandler<T> validationStatusHandler) {
			inputGroupBuilder.validationStatusHandler(property, validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#propertiesValidationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> propertiesValidationStatusHandler(
				ValidationStatusHandler<?> validationStatusHandler) {
			inputGroupBuilder.propertiesValidationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#validationStatusHandler(com.holonplatform
		 * .vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> validationStatusHandler(
				ValidationStatusHandler<PropertyBox> validationStatusHandler) {
			inputGroupBuilder.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#validateOnValueChange(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> validateOnValueChange(boolean validateOnValueChange) {
			inputGroupBuilder.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> stopValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			inputGroupBuilder.stopValidationAtFirstFailure(stopValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#stopOverallValidationAtFirstFailure(
		 * boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> stopOverallValidationAtFirstFailure(
				boolean stopOverallValidationAtFirstFailure) {
			inputGroupBuilder.stopOverallValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withValueChangeListener(com.holonplatform
		 * .core.property.Property, com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValueChangeListener(Property<T> property,
				ValueChangeListener<T> listener) {
			inputGroupBuilder.withValueChangeListener(property, listener);
			return this;
		}

		@Override
		public <T> PropertyInputFormBuilder<C> hidden(Property<T> property) {
			inputGroupBuilder.hidden(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#usePropertyRendererRegistry(com.
		 * holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public PropertyInputFormBuilder<C> usePropertyRendererRegistry(
				PropertyRendererRegistry propertyRendererRegistry) {
			inputGroupBuilder.usePropertyRendererRegistry(propertyRendererRegistry);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			instance.setInitializer(initializer);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> composer(Composer<? super C, PropertyValueComponentSource> composer) {
			ObjectUtils.argumentNotNull(composer, "Composer must be not null");
			instance.setComposer(composer);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> composeOnAttach(boolean composeOnAttach) {
			instance.setComposeOnAttach(composeOnAttach);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, Localizable caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(caption, "Caption must be not null");
			instance.setPropertyCaption(property, caption);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyCaption(property, Localizable.builder().message(caption).build());
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption,
				String messageCode, Object... arguments) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyCaption(property, Localizable.builder().message(defaultCaption).messageCode(messageCode)
					.messageArguments(arguments).build());
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> hidePropertyCaption(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.hidePropertyCaption(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public PropertyInputForm build() {
			instance.setInputGroup(inputGroupBuilder.withPostProcessor((property, component) -> {
				instance.configurePropertyComponent(property, component);
			}).build());
			return instance;
		}

	}

}
