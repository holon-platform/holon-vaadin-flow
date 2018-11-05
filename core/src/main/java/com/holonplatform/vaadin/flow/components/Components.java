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
package com.holonplatform.vaadin.flow.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.PropertyInputForm.PropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup.PropertyInputGroupBuilder;
import com.holonplatform.vaadin.flow.components.PropertyViewForm.PropertyViewFormBuilder;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator.BaseFormLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator.BaseLabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalDateTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.LocalTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.NativeButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.ItemOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsModeSingleSelectInputBuilder.PropertyOptionsModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.ItemSelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SelectModeSingleSelectInputBuilder.PropertySelectModeSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator.HorizontalLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator.VerticalLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;

/**
 * Main provider of UI components builders and configurators.
 * <p>
 * Provides static methods to obtain builder for common UI components type, allowing fluent and implementation-agnostic
 * components creation and configuration.
 * </p>
 * 
 * @since 5.2.0
 */
public interface Components {

	// Configurators

	/**
	 * Get a {@link LabelConfigurator} to configure given <em>label</em> type component.
	 * <p>
	 * The component must be a {@link HtmlContainer} and a {@link ClickNotifier}, such as {@link Span} or {@link Div}.
	 * </p>
	 * @param <L> Label element type
	 * @param label The component to configure (not null)
	 * @return A {@link LabelConfigurator}
	 */
	@SuppressWarnings("rawtypes")
	static <L extends HtmlContainer & ClickNotifier> BaseLabelConfigurator<L> configure(L label) {
		return LabelConfigurator.configure(label);
	}

	/**
	 * Get a {@link ButtonConfigurator} to configure given {@link Button} instance.
	 * @param button Button to configure (not null)
	 * @return A {@link ButtonConfigurator}
	 */
	static BaseButtonConfigurator configure(Button button) {
		return ButtonConfigurator.configure(button);
	}

	/**
	 * Get a {@link VerticalLayoutConfigurator} to configure given {@link VerticalLayout}.
	 * @param layout Layout to configure
	 * @return A new {@link VerticalLayoutConfigurator}
	 */
	static VerticalLayoutConfigurator configure(VerticalLayout layout) {
		return ThemableFlexComponentConfigurator.configure(layout);
	}

	/**
	 * Get a {@link HorizontalLayoutConfigurator} to configure given {@link HorizontalLayout}.
	 * @param layout Layout to configure
	 * @return A new {@link HorizontalLayoutConfigurator}
	 */
	static HorizontalLayoutConfigurator configure(HorizontalLayout layout) {
		return ThemableFlexComponentConfigurator.configure(layout);
	}

	/**
	 * Get a {@link BaseFormLayoutConfigurator} to configure given {@link FormLayout}.
	 * @param layout Layout to configure
	 * @return A new {@link BaseFormLayoutConfigurator}
	 */
	static BaseFormLayoutConfigurator configure(FormLayout layout) {
		return FormLayoutConfigurator.configure(layout);
	}

	// Builders

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Div} tag.
	 * <p>
	 * This is an alias for the {@link #div()} method.
	 * </p>
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Div> label() {
		return div();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Span} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Span> span() {
		return LabelBuilder.span();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Div} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Div> div() {
		return LabelBuilder.div();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Paragraph} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Paragraph> paragraph() {
		return LabelBuilder.paragraph();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H1} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H1> h1() {
		return LabelBuilder.h1();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H2} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H2> h2() {
		return LabelBuilder.h2();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H3} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H3> h3() {
		return LabelBuilder.h3();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H4} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H4> h4() {
		return LabelBuilder.h4();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H5} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H5> h5() {
		return LabelBuilder.h5();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H6} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H6> h6() {
		return LabelBuilder.h6();
	}

	/**
	 * Gets a builder to create {@link Button}s.
	 * @return A new {@link ButtonBuilder}
	 */
	static ButtonBuilder button() {
		return ButtonBuilder.create();
	}

	/**
	 * Gets a builder to create {@link NativeButton}s.
	 * @return A new {@link NativeButtonBuilder}
	 */
	static NativeButtonBuilder nativeButton() {
		return NativeButtonBuilder.create();
	}

	/**
	 * Gets a builder to create {@link VerticalLayout}s.
	 * @return A new {@link VerticalLayoutBuilder}
	 */
	static VerticalLayoutBuilder vl() {
		return VerticalLayoutBuilder.create();
	}

	/**
	 * Gets a builder to create {@link HorizontalLayout}s.
	 * @return A new {@link HorizontalLayoutBuilder}
	 */
	static HorizontalLayoutBuilder hl() {
		return HorizontalLayoutBuilder.create();
	}

	/**
	 * Gets a builder to create {@link FormLayout}s.
	 * @return A new {@link FormLayoutBuilder}
	 */
	static FormLayoutBuilder formLayout() {
		return FormLayoutBuilder.create();
	}

	// TODO tabs, dialogs

	// /**
	// * Gets a builder to create a {@link TabSheet}.
	// * @return TabSheet builder
	// */
	// static TabsBuilder<TabSheet> tabSheet() {
	// return new TabSheetBuilder();
	// }
	//
	// /**
	// * Gets a builder to create an {@link Accordion}.
	// * @return Accordion builder
	// */
	// static TabsBuilder<Accordion> accordion() {
	// return new AccordionBuilder();
	// }
	//
	// /**
	// * Gets a builder to create and open a {@link Dialog} window. The dialog will present by default a single
	// * <em>ok</em> button.
	// * @return DialogBuilder
	// */
	// static DialogBuilder dialog() {
	// return Dialog.builder();
	// }
	//
	// /**
	// * Gets a builder to create and open a question {@link Dialog} window. The dialog will present by default a
	// * <em>yes</em> and a <em>no</em> button. Use
	// * {@link QuestionDialogBuilder#callback(com.holonplatform.vaadin.components.Dialog.QuestionCallback)} to handle
	// the
	// * user selected answer.
	// * @return QuestionDialogBuilder
	// */
	// static QuestionDialogBuilder questionDialog() {
	// return Dialog.question();
	// }
	//

	// View components

	/**
	 * {@link ViewComponent} and {@link PropertyViewGroup} builders provider.
	 */
	static interface view {

		/**
		 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given value type.
		 * @param <T> Value type
		 * @param valueType Value type (not null)
		 * @return A {@link ViewComponentBuilder}
		 */
		static <T> ViewComponentBuilder<T> component(Class<? extends T> valueType) {
			return ViewComponent.builder(valueType);
		}

		/**
		 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given {@link Property} for label
		 * and value presentation through the {@link Property#present(Object)} method.
		 * @param <T> Value type
		 * @param property The property to use (not null)
		 * @return A {@link ViewComponentBuilder}
		 */
		static <T> ViewComponentBuilder<T> component(Property<T> property) {
			return ViewComponent.builder(property);
		}

		/**
		 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given function to convert the
		 * value to a {@link String} type representation.
		 * @param <T> Value type
		 * @param stringValueConverter Value converter function (not null)
		 * @return A {@link ViewComponentBuilder}
		 */
		static <T> ViewComponentBuilder<T> component(Function<T, String> stringValueConverter) {
			return ViewComponent.builder(stringValueConverter);
		}

		/**
		 * Create a {@link ViewComponent} using given value type.
		 * @param <T> Value type
		 * @param valueType Value type (not null)
		 * @return A {@link ViewComponent} instance
		 */
		static <T> ViewComponent<T> type(Class<T> valueType) {
			return ViewComponent.builder(valueType).build();
		}

		/**
		 * Create a {@link ViewComponent} using given {@link Property} for label and value presentation through the
		 * {@link Property#present(Object)} method.
		 * @param <T> Value type
		 * @param property The property to use (not null)
		 * @return A {@link ViewComponent} instance
		 */
		static <T> ViewComponent<T> property(Property<T> property) {
			return ViewComponent.create(property);
		}

		/**
		 * Get a {@link PropertyViewGroupBuilder} to create and setup a {@link PropertyViewGroup}.
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyViewGroupBuilder}
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyViewGroupBuilder propertyGroup(Iterable<P> properties) {
			return PropertyViewGroup.builder(properties);
		}

		/**
		 * Get a {@link PropertyViewGroupBuilder} to create and setup a {@link PropertyViewGroup}.
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyViewGroupBuilder}
		 */
		static PropertyViewGroupBuilder propertyGroup(Property<?>... properties) {
			return PropertyViewGroup.builder(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set.
		 * @param <C> Form content element type
		 * @param <P> Property type
		 * @param content The form content, where the {@link ViewComponent}s will be composed using the configured
		 *        {@link Composer} (not null)
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyViewFormBuilder}
		 */
		@SuppressWarnings("rawtypes")
		static <C extends Component, P extends Property> PropertyViewFormBuilder<C> form(C content,
				Iterable<P> properties) {
			return PropertyViewForm.builder(content, properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set.
		 * @param <C> Form content element type
		 * @param content The form content, where the {@link ViewComponent}s will be composed using the configured
		 *        {@link Composer} (not null)
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyViewFormBuilder}
		 */
		static <C extends Component> PropertyViewFormBuilder<C> form(C content, Property<?>... properties) {
			return PropertyViewForm.builder(content, properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link FormLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A {@link PropertyViewForm} builder
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyViewFormBuilder<FormLayout> form(Iterable<P> properties) {
			return PropertyViewForm.formLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link FormLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param properties The property set (not null)
		 * @return A {@link PropertyViewForm} builder
		 */
		static PropertyViewFormBuilder<FormLayout> form(Property<?>... properties) {
			return PropertyViewForm.formLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link VerticalLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A {@link PropertyViewForm} builder
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyViewFormBuilder<VerticalLayout> formVertical(Iterable<P> properties) {
			return PropertyViewForm.verticalLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link VerticalLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param properties The property set (not null)
		 * @return A {@link PropertyViewForm} builder
		 */
		static PropertyViewFormBuilder<VerticalLayout> formVertical(Property<?>... properties) {
			return PropertyViewForm.verticalLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link HorizontalLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A {@link PropertyViewForm} builder
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyViewFormBuilder<HorizontalLayout> formHorizontal(Iterable<P> properties) {
			return PropertyViewForm.horizontalLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyViewForm} using given property set and a {@link HorizontalLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param properties The property set (not null)
		 * @return A {@link PropertyViewForm} builder
		 */
		static PropertyViewFormBuilder<HorizontalLayout> formHorizontal(Property<?>... properties) {
			return PropertyViewForm.horizontalLayout(properties);
		}

	}

	// Inputs

	/**
	 * {@link Input}, {@link PropertyInputGroup} and {@link PropertyInputForm} builders provider.
	 */
	static interface input {

		/**
		 * Gets a builder to create {@link String} type {@link Input}s.
		 * @return A {@link StringInputBuilder}
		 */
		static StringInputBuilder string() {
			return Input.string();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s rendered as a <em>text area</em>.
		 * @return A {@link StringAreaInputBuilder}
		 */
		static StringAreaInputBuilder stringArea() {
			return Input.stringArea();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s which not display user input on screen, used to
		 * enter secret text information like passwords.
		 * <p>
		 * Alias for {@link #password()}.
		 * </p>
		 * @return A {@link PasswordInputBuilder}
		 */
		static PasswordInputBuilder secretString() {
			return password();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s which not display user input on screen, used to
		 * enter secret text information like passwords.
		 * @return A {@link PasswordInputBuilder}
		 */
		static PasswordInputBuilder password() {
			return Input.password();
		}

		/**
		 * Gets a builder to create {@link LocalDate} type {@link Input}s.
		 * @return A {@link LocalDateInputBuilder}
		 */
		static LocalDateInputBuilder localDate() {
			return Input.localDate();
		}

		/**
		 * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
		 * @return A {@link LocalDateTimeInputBuilder}
		 */
		static LocalDateTimeInputBuilder localDateTime() {
			return Input.localDateTime();
		}

		/**
		 * Gets a builder to create {@link LocalTime} type {@link Input}s.
		 * @return A {@link LocalTimeInputBuilder}
		 */
		static LocalTimeInputBuilder localTime() {
			return LocalTimeInputBuilder.create();
		}

		/**
		 * Gets a builder to create {@link Date} type {@link Input}s.
		 * <p>
		 * This Input use the {@link Date} type only for simple date representations (day, month, year), i.e. without
		 * the time part.
		 * </p>
		 * @return A {@link DateInputBuilder}
		 */
		static DateInputBuilder date() {
			return Input.date();
		}

		/**
		 * Gets a builder to create {@link Date} type {@link Input}s with time (hours and minutes) support.
		 * <p>
		 * Only the hours and minutes time parts are supported.
		 * </p>
		 * @return A {@link DateTimeInputBuilder}
		 */
		static DateTimeInputBuilder dateTime() {
			return Input.dateTime();
		}

		/**
		 * Gets a builder to create {@link Boolean} type {@link Input}s.
		 * @return A {@link BooleanInputBuilder}
		 */
		static BooleanInputBuilder boolean_() {
			return Input.boolean_();
		}

		/**
		 * Gets a builder to create a numeric type {@link Input}.
		 * @param <T> Number type
		 * @param numberType Number class (not null)
		 * @return A new {@link NumberInputBuilder}
		 */
		static <T extends Number> NumberInputBuilder<T> number(Class<T> numberClass) {
			return Input.number(numberClass);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input.
		 * <p>
		 * This builder can be used when the selection items type and the selection value type are consistent. Use
		 * {@link #singleSelect(ItemConverter)} if not.
		 * <p>
		 * @param <T> Value type
		 * @param type Selection value type (not null)
		 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
		 */
		static <T> ItemSelectModeSingleSelectInputBuilder<T, T> singleSelect(Class<T> type) {
			return Input.singleSelect(type);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input.
		 * <p>
		 * This builder can be used when the selection items type and the selection value type are not consistent (i.e.
		 * of different type). When the the selection item and the selection value types are consistent, the
		 * {@link #singleSelect()} method can be used.
		 * <p>
		 * @param <T> Value type
		 * @param <ITEM> Item type
		 * @param type Selection value type (not null)
		 * @param itemType Selection items type (not null)
		 * @param itemConverter The item converter to use to convert a selection item into a selection (Input) value and
		 *        back (not null)
		 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
		 */
		static <T, ITEM> ItemSelectModeSingleSelectInputBuilder<T, ITEM> singleSelect(Class<T> type,
				Class<ITEM> itemType, ItemConverter<T, ITEM, DataProvider<ITEM, ?>> itemConverter) {
			return Input.singleSelect(type, itemType, itemConverter);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property}.
		 * @param <T> Value type
		 * @param selectionProperty The property to use to represent the selection value (not null)
		 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
		 */
		static <T> PropertySelectModeSingleSelectInputBuilder<T> singleSelect(final Property<T> selectionProperty) {
			return Input.singleSelect(selectionProperty);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property} and
		 * converter.
		 * @param <T> Value type
		 * @param selectionProperty The property to use to represent the selection value (not null)
		 * @param itemConverter The function to use to convert a selection value into the corresponding
		 *        {@link PropertyBox} item
		 * @return A new {@link PropertySelectModeSingleSelectInputBuilder}
		 */
		static <T> PropertySelectModeSingleSelectInputBuilder<T> singleSelect(final Property<T> selectionProperty,
				BiFunction<DataProvider<PropertyBox, ?>, T, PropertyBox> itemConverter) {
			return Input.singleSelect(selectionProperty, itemConverter);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input using the <em>options</em> rendering mode, i.e. a
		 * radio button group.
		 * <p>
		 * This builder can be used when the selection items type and the selection value type are consistent. Use
		 * {@link #singleSelect(ItemConverter)} if not.
		 * <p>
		 * @param <T> Value type
		 * @param type Selection value type (not null)
		 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
		 */
		static <T> ItemOptionsModeSingleSelectInputBuilder<T, T> singleOptionSelect(Class<T> type) {
			return Input.singleOptionSelect(type);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input using the <em>options</em> rendering mode, i.e. a
		 * radio button group.
		 * <p>
		 * This builder can be used when the selection items type and the selection value type are not consistent (i.e.
		 * of different type). When the the selection item and the selection value types are consistent, the
		 * {@link #singleOptionSelect()} method can be used.
		 * <p>
		 * @param <T> Value type
		 * @param <ITEM> Item type
		 * @param type Selection value type (not null)
		 * @param itemType Selection items type (not null)
		 * @param itemConverter The item converter to use to convert a selection item into a selection (Input) value and
		 *        back (not null)
		 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
		 */
		static <T, ITEM> ItemOptionsModeSingleSelectInputBuilder<T, ITEM> singleOptionSelect(Class<T> type,
				Class<ITEM> itemType, ItemConverter<T, ITEM, DataProvider<ITEM, ?>> itemConverter) {
			return Input.singleOptionSelect(type, itemType, itemConverter);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property} and the
		 * <em>options</em> rendering mode, i.e. a radio button group.
		 * @param <T> Value type
		 * @param selectionProperty The property to use to represent the selection value (not null)
		 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
		 */
		static <T> PropertyOptionsModeSingleSelectInputBuilder<T> singleOptionSelect(
				final Property<T> selectionProperty) {
			return Input.singleOptionSelect(selectionProperty);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input, using given selection {@link Property}, a
		 * converter and the <em>options</em> rendering mode, i.e. a radio button group.
		 * @param <T> Value type
		 * @param selectionProperty The property to use to represent the selection value (not null)
		 * @param itemConverter The function to use to convert a selection value into the corresponding
		 *        {@link PropertyBox} item
		 * @return A new {@link PropertyOptionsModeSingleSelectInputBuilder}
		 */
		static <T> PropertyOptionsModeSingleSelectInputBuilder<T> singleOptionSelect(
				final Property<T> selectionProperty,
				BiFunction<DataProvider<PropertyBox, ?>, T, PropertyBox> itemConverter) {
			return Input.singleOptionSelect(selectionProperty, itemConverter);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input for given <code>enum</code> type.
		 * <p>
		 * All the enum constants declared for the given enum type will be available as selection items.
		 * </p>
		 * @param <E> Enum type
		 * @param enumType Enum type (not null)
		 * @return A new {@link ItemSelectModeSingleSelectInputBuilder}
		 */
		static <E extends Enum<E>> ItemSelectModeSingleSelectInputBuilder<E, E> enumSelect(Class<E> enumType) {
			return Input.enumSelect(enumType);
		}

		/**
		 * Gets a builder to create a {@link SingleSelect} type Input for given <code>enum</code> type with the
		 * <em>options</em> rendering mode, i.e. using a radio button group.
		 * <p>
		 * All the enum constants declared for the given enum type will be available as selection items.
		 * </p>
		 * @param <E> Enum type
		 * @param enumType Enum type (not null)
		 * @return A new {@link ItemOptionsModeSingleSelectInputBuilder}
		 */
		static <E extends Enum<E>> ItemOptionsModeSingleSelectInputBuilder<E, E> enumOptionSelect(Class<E> enumType) {
			return Input.enumOptionSelect(enumType);
		}

		/**
		 * Get a {@link PropertyInputGroupBuilder} to create and setup a {@link PropertyInputGroup}.
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyInputGroupBuilder}
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyInputGroupBuilder propertyGroup(Iterable<P> properties) {
			return PropertyInputGroup.builder(properties);
		}

		/**
		 * Get a {@link PropertyInputGroupBuilder} to create and setup a {@link PropertyInputGroup}.
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyInputGroupBuilder}
		 */
		static PropertyInputGroupBuilder propertyGroup(Property<?>... properties) {
			return PropertyInputGroup.builder(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set.
		 * @param <C> Form content element type
		 * @param <P> Property type
		 * @param content The form content, where the {@link Input}s will be composed using the configured
		 *        {@link Composer} (not null)
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyInputFormBuilder}
		 */
		@SuppressWarnings("rawtypes")
		static <C extends Component, P extends Property> PropertyInputFormBuilder<C> form(C content,
				Iterable<P> properties) {
			return PropertyInputForm.builder(content, properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set.
		 * @param <C> Form content element type
		 * @param content The form content, where the {@link Input}s will be composed using the configured
		 *        {@link Composer} (not null)
		 * @param properties The property set (not null)
		 * @return A new {@link PropertyInputFormBuilder}
		 */
		static <C extends Component> PropertyInputFormBuilder<C> form(C content, Property<?>... properties) {
			return PropertyInputForm.builder(content, PropertySet.of(properties));
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link FormLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A {@link PropertyInputForm} builder
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyInputFormBuilder<FormLayout> form(Iterable<P> properties) {
			return PropertyInputForm.formLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link FormLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param properties The property set (not null)
		 * @return A {@link PropertyInputForm} builder
		 */
		static PropertyInputFormBuilder<FormLayout> form(Property<?>... properties) {
			return PropertyInputForm.formLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link VerticalLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A {@link PropertyInputForm} builder
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyInputFormBuilder<VerticalLayout> formVertical(Iterable<P> properties) {
			return PropertyInputForm.verticalLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link VerticalLayout} as
		 * content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param properties The property set (not null)
		 * @return A {@link PropertyInputForm} builder
		 */
		static PropertyInputFormBuilder<VerticalLayout> formVertical(Property<?>... properties) {
			return PropertyInputForm.verticalLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link HorizontalLayout}
		 * as content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param <P> Property type
		 * @param properties The property set (not null)
		 * @return A {@link PropertyInputForm} builder
		 */
		@SuppressWarnings("rawtypes")
		static <P extends Property> PropertyInputFormBuilder<HorizontalLayout> formHorizontal(Iterable<P> properties) {
			return PropertyInputForm.horizontalLayout(properties);
		}

		/**
		 * Get a builder to create a {@link PropertyInputForm} using given property set and a {@link HorizontalLayout}
		 * as content layout.
		 * <p>
		 * A default composer is configured using {@link Composable#componentContainerComposer()}. Use
		 * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)} to
		 * provide a custom components composer.
		 * </p>
		 * @param properties The property set (not null)
		 * @return A {@link PropertyInputForm} builder
		 */
		static PropertyInputFormBuilder<HorizontalLayout> formHorizontal(Property<?>... properties) {
			return PropertyInputForm.horizontalLayout(properties);
		}

	}

	// Item listings TODO

	/**
	 * {@link ItemListing} builders provider.
	 */
	static interface listing {

		// /**
		// * Builder to create an {@link ItemListing} instance using a {@link Grid} as backing component.
		// * @param <T> Item data type
		// * @param itemType Item bean type
		// * @return Grid {@link ItemListing} builder
		// */
		// static <T> BeanListingBuilder<T> items(Class<T> itemType) {
		// return BeanListing.builder(itemType);
		// }
		//
		// /**
		// * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
		// * @param <P> Actual property type
		// * @param properties The property set to use for the listing
		// * @return Grid {@link PropertyListing} builder
		// */
		// @SafeVarargs
		// static <P extends Property<?>> GridPropertyListingBuilder properties(P... properties) {
		// return properties(PropertySet.of(properties));
		// }
		//
		// /**
		// * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
		// * @param <P> Actual property type
		// * @param properties The property set to use for the listing
		// * @return Grid {@link PropertyListing} builder
		// */
		// static <P extends Property<?>> GridPropertyListingBuilder properties(Iterable<P> properties) {
		// return PropertyListing.builder(properties);
		// }
		//
		// /**
		// * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
		// * @param <P> Actual property type
		// * @param properties The property set to use for the listing (not null)
		// * @param additionalProperties Additional properties to declare
		// * @return Grid {@link PropertyListing} builder
		// */
		// @SafeVarargs
		// @SuppressWarnings("rawtypes")
		// static <P extends Property> GridPropertyListingBuilder properties(PropertySet<P> properties,
		// P... additionalProperties) {
		// ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		// if (additionalProperties != null && additionalProperties.length > 0) {
		// PropertySet.Builder<Property<?>> builder = PropertySet.builder().add(properties);
		// for (P property : additionalProperties) {
		// builder.add(property);
		// }
		// return PropertyListing.builder(builder.build());
		// }
		// return PropertyListing.builder(properties);
		// }

	}

}
