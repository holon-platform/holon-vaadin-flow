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
package com.holonplatform.vaadin.flow.examples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.holonplatform.core.Registration;
import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.PropertyInputGroup;
import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler.Status;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.DataProvider;

@SuppressWarnings("unused")
public class ExampleInput {

	public void input1() {
		// tag::input1[]
		Input<String> input = Input.string() // <1>
				.fullWidth() // <2>
				.styleName("my-style") // <3>
				.label("My label") // <4>
				.label("My label", "label.message.code") // <5>
				.description("My description") // <6>
				.blankValuesAsNull(true) // <7>
				.autocapitalize(Autocapitalize.WORDS) // <8>
				.maxLength(50) // <9>
				.placeholder("My placeholder", "placeholder.message.code")// <10>
				.autofocus(true)// <11>
				.prefixComponent(new Button("Prefix"))// <12>
				.tabIndex(99)// <13>
				.readOnly() // 14>
				.withBlurListener(event -> {// <15>
					/* handle blur event */
				}).withValueChangeListener(event -> { // <16>
					String oldValue = event.getOldValue(); // <17>
					String newValue = event.getValue(); // <18>
				}).build();

		input = Components.input.string() // <13>
				/* configuration omitted */
				.build();
		// end::input1[]
	}

	public void input2() {
		// tag::input2[]
		Input<String> input1 = Input.string().build(); // <1>
		Input<String> input2 = Input.stringArea().build(); // <2>
		Input<Boolean> input3 = Input.boolean_().build(); // <3>
		Input<Double> input4 = Input.number(Double.class).build(); // <4>
		Input<LocalDate> input5 = Input.localDate().build(); // <5>
		Input<LocalDateTime> input6 = Input.localDateTime().build(); // <6>
		Input<LocalTime> input7 = Input.localTime().build(); // <7>
		Input<Date> input8 = Input.date().build(); // <8>

		Optional<Input<String>> input = Input.create(String.class); // <9>
		// end::input2[]
	}

	public void input3() {
		VerticalLayout myLayout = new VerticalLayout();
		// tag::input3[]
		Input<String> input = Input.string().build(); // <1>

		myLayout.add(input.getComponent()); // <2>

		input.setValue("My value"); // <3>
		String value = input.getValue(); // <4>
		value = input.getValueIfPresent().orElse("Default value"); // <5>

		input.clear(); // <6>
		boolean empty = input.isEmpty(); // <7>

		input.setReadOnly(true); // <8>
		input.isReadOnly(); // <9>

		input.addValueChangeListener(event -> { // <10>
			/* handle value change */
		});
		// end::input3[]
	}

	@SuppressWarnings("unchecked")
	public void input4() {
		// tag::input4[]
		NumericProperty<Integer> MY_PROPERTY = NumericProperty.integerType("my_property"); // <1>

		Input<Integer> input = Input.create(MY_PROPERTY); // <2>

		Input<Integer> viewComponent = MY_PROPERTY.render(Input.class); // <3>
		// end::input4[]
	}

	@SuppressWarnings("serial")
	class MyInput implements Input<Integer> {

		@Override
		public void setValue(Integer value) {
		}

		@Override
		public Integer getValue() {
			return null;
		}

		@Override
		public Registration addValueChangeListener(ValueChangeListener<Integer, ValueChangeEvent<Integer>> listener) {
			return null;
		}

		@Override
		public Component getComponent() {
			return null;
		}

		@Override
		public void setReadOnly(boolean readOnly) {
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public boolean isRequired() {
			return false;
		}

		@Override
		public void setRequired(boolean required) {
		}

		@Override
		public void focus() {
		}

	}

	public void input5() {
		final NumericProperty<Integer> MY_PROPERTY = NumericProperty.integerType("my_property");
		// tag::input5[]
		PropertyRendererRegistry.getDefault() // <1>
				.forProperty(MY_PROPERTY, InputPropertyRenderer.create(property -> new MyInput())); // <2>
		// end::input5[]
	}

	public void input6() {
		// tag::input6[]
		Input<String> stringInput = Input.string().build(); // <1>
		Input<Integer> integerInput = Input.from(stringInput, new StringToIntegerConverter("Conversion failed")); // <2>
		// end::input6[]
	}

	public void input7() {
		// tag::input7[]
		BooleanProperty PROPERTY = BooleanProperty.create("test");

		Input<Integer> integerInput = Input.number(Integer.class).build(); // <1>
		Input<Boolean> booleanInput = Input.from(integerInput, PROPERTY,
				PropertyValueConverter.numericBoolean(Integer.class)); // <2>
		// end::input7[]
	}

	public void input8() {
		// tag::input8[]
		Input<String> stringInput = Input.builder(new TextField()).build(); // <1>

		Input<Integer> input = Input.from(new TextField(), new StringToIntegerConverter("Conversion failed")); // <2>
		// end::input8[]
	}

	public void input9() {
		// tag::input9[]
		SingleSelect<String> singleSelect = Input.singleSelect(String.class).build(); // <1>
		singleSelect = Input.singleOptionSelect(String.class).build(); // <2>

		MultiSelect<String> multiSelect = Input.multiOptionSelect(String.class).build(); // <3>
		// end::input9[]
	}

	public void input10() {
		// tag::input10[]
		SingleSelect<String> singleSelect = Input.singleSelect(String.class).build();

		singleSelect.setValue("A value"); // <1>
		String value = singleSelect.getValue(); // <2>
		singleSelect.clear(); // <3>
		singleSelect.isEmpty(); // <4>

		MultiSelect<String> multiSelect = Input.multiOptionSelect(String.class).build();

		multiSelect.setValue(Arrays.asList("Value 1", "Value 2").stream().collect(Collectors.toSet())); // <5>
		Set<String> values = multiSelect.getValue(); // <6>
		// end::input10[]
	}

	public void input11() {
		// tag::input11[]
		SingleSelect<String> singleSelect = Input.singleSelect(String.class).build();

		singleSelect.select("A value"); // <1>
		String value = singleSelect.getSelectedItem().orElse(null); // <2>
		singleSelect.deselectAll(); // <3>
		// end::input11[]
	}

	public void input12() {
		// tag::input12[]
		SingleSelect<String> singleSelect = Input.singleSelect(String.class) //
				.addItem("Value 1") // <1>
				.addItem("Value 2") // <2>
				.items("Value 1", "Value 2") // <3>
				.build();
		// end::input12[]
	}

	public void input13() {
		// tag::input13[]
		SingleSelect<String> singleSelect = Input.singleSelect(String.class) //
				.dataSource(DataProvider.ofItems("Value 1", "Value 2")) // <1>
				.build();
		// end::input13[]
	}

	private class MyBean {

		private String name;

		public MyBean() {
			super();
		}

		public MyBean(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public void input14() {
		// tag::input14[]
		SingleSelect<String> singleSelect = Input.singleSelect(String.class, MyBean.class, ItemConverter.create( // <1>
				item -> item.getName(), // <2>
				value -> Optional.of(new MyBean(value)) // <3>
		)).addItem(new MyBean("One")).addItem(new MyBean("Two")).build();
		// end::input14[]
	}

	// tag::propertydef[]
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	// end::propertydef[]

	// tag::target[]
	static final DataTarget<?> TARGET = DataTarget.named("subjects");
	// end::target[]

	public void input15() {
		// tag::input15[]
		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build()) // <2>
				.build();
		// end::input15[]
	}

	public void input16() {
		// tag::input16[]
		Datastore datastore = getDatastore();

		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.dataSource(datastore, TARGET) // <2>
				.build();

		singleSelect = Input.singleSelect(ID) //
				.dataSource(datastore, TARGET, SUBJECT) // <3>
				.build();
		// end::input16[]
	}

	public void input17() {
		final QueryConfigurationProvider myQueryConfigurationProvider = null;
		// tag::input17[]
		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.dataSource(getDatastore(), TARGET, SUBJECT) // <2>
				.withQueryFilter(NAME.isNotNull()) // <3>
				.withQuerySort(ID.asc()) // <4>
				.withQueryConfigurationProvider(myQueryConfigurationProvider) // <5>
				.build();
		// end::input17[]
	}

	public void input18() {
		// tag::input18[]
		SingleSelect<String> input = Input.singleSelect(String.class) // <1>
				.dataSource(DataProvider.ofCollection(Arrays.asList("One", "Two", "Three")) // <2>
						.filteringByPrefix(v -> v)) // <3>
				.build();
		// end::input18[]
	}

	public void input19() {
		// tag::input19[]
		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.dataSource(getDatastore(), TARGET, // <2>
						text -> NAME.contains(text), // <3>
						SUBJECT // <4>
				).build();
		// end::input19[]
	}

	public void input19b() {
		// tag::input19b[]
		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.dataSource(getDatastore(), TARGET, SUBJECT) // <2>
				.filterConverter(text -> NAME.contains(text)) // <3>
				.build();
		// end::input19b[]
	}

	// tag::enum1[]
	enum MyEnum {

		FIRST, SECOND, THIRD;

	}
	// end::enum1[]

	public void input20() {
		// tag::input20[]
		SingleSelect<MyEnum> singleSelect = Input.enumSelect(MyEnum.class).build(); // <1>
		singleSelect = Input.enumOptionSelect(MyEnum.class).build(); // <2>

		MultiSelect<MyEnum> multiSelect = Input.enumMultiSelect(MyEnum.class).build(); // <3>
		// end::input20[]
	}

	public void input21() {
		// tag::input21[]
		SingleSelect<Integer> singleSelect = Input.singleSelect(Integer.class) // <1>
				.items(1, 2) //
				.itemCaption(1, "One") // <2>
				.itemCaption(2, "One", "two.message.code") // <3>
				.build();
		// end::input21[]
	}

	public void input22() {
		// tag::input22[]
		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.dataSource(getDatastore(), TARGET, SUBJECT) // <2>
				.itemCaptionGenerator(item -> item.getValue(NAME)) // <3>
				.build();
		// end::input22[]
	}

	public void input22b() {
		// tag::input22b[]
		SingleSelect<Long> singleSelect = Input.singleSelect(ID) // <1>
				.dataSource(getDatastore(), TARGET, SUBJECT) // <2>
				.itemCaptionProperty(NAME) // <3>
				.build();
		// end::input22b[]
	}

	public void input23() {
		// tag::input23[]
		ValidatableInput<String> validatableInput = Input.string() //
				.validatable() // <1>
				.withValidator(Validator.max(10)) // <2>
				.validateOnValueChange(true) // <3>
				.build();
		// end::input23[]
	}

	public void input24() {
		// tag::input24[]
		Input<String> input = Input.string().build();

		ValidatableInput<String> validatableInput = ValidatableInput.builder(input) // <1>
				.withValidator(Validator.max(10)) // <2>
				.build();

		validatableInput = ValidatableInput.from(input); // <3>
		validatableInput.addValidator(Validator.max(10)); // <4>
		// end::input24[]
	}

	public void input25() {
		// tag::input25[]
		ValidatableInput<String> validatableInput = Input.string().validatable().build(); // <1>

		validatableInput.isValid(); // <2>

		try {
			validatableInput.validate(); // <3>
		} catch (ValidationException e) {
			/* value is not valid */
		}

		Optional<String> value = validatableInput.getValueIfValid(); // <4>
		// end::input25[]
	}

	public void input26() {
		// tag::input26[]
		ValidatableInput<String> validatableInput = Input.string().validatable() //
				.validationStatusHandler(event -> { // <1>
					Status validationStatus = event.getStatus();
					String error = event.getErrorMessage();
					/* notify the validation errors */
				}).build();
		// end::input26[]
	}

	public void input27() {
		// tag::input27[]
		PropertyInputGroup group = PropertyInputGroup.builder(SUBJECT).build(); // <1>
		group = Components.input.propertyGroup(SUBJECT).build(); // <2>

		Collection<Property<?>> properties = group.getProperties(); // <3>
		Stream<Input<?>> components = group.getElements(); // <4>
		group.getBindings().forEach(binding -> { // <5>
			Property<?> property = binding.getProperty();
			Input<?> component = binding.getElement();
		});

		Optional<Input<?>> element = group.getElement(NAME); // <6>
		Input<?> component = group.requireElement(NAME); // <7>

		PropertyBox value = PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build();

		group.setValue(value); // <8>
		value = group.getValue(); // <9>

		group.clear(); // <10>
		group.isEmpty(); // <11>
		// end::input27[]
	}

	public void input28() {
		// tag::input28[]
		PropertyInputGroup group = PropertyInputGroup.builder(SUBJECT) // <1>
				.bind(NAME, Input.string().build()) // <2>
				.bind(NAME, property -> Input.stringArea().build()) // <3>
				.build();
		// end::input28[]
	}

	public void input29() {
		// tag::input29[]
		PropertyInputGroup group = PropertyInputGroup.builder(SUBJECT) // <1>
				.hidden(ID) // <2>
				.defaultValue(NAME, () -> "(no name)") // <3>
				.withPostProcessor((property, component) -> { // <4>
					/* Inputs configuration */
					component.hasStyle().ifPresent(s -> s.addClassName("my-style"));
				}) //
				.withValueChangeListener(event -> { // <5>
					/* handle value change */
					PropertyBox value = event.getValue();
				}).build();
		// end::input29[]
	}

	public void input30() {
		VerticalLayout myLayout = new VerticalLayout();
		// tag::input30[]
		PropertyInputForm form = PropertyInputForm.builder(new VerticalLayout(), SUBJECT) // <1>
				.composer(Composable.componentContainerComposer()) // <2>
				.build();

		myLayout.add(form.getComponent()); // <3>

		form.setValue(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build()); // <4>
		// end::input30[]
	}

	public void input31() {
		// tag::input31[]
		PropertyInputForm form = PropertyInputForm.verticalLayout(SUBJECT).build(); // <1>
		form = PropertyInputForm.horizontalLayout(SUBJECT).build(); // <2>
		form = PropertyInputForm.formLayout(SUBJECT).build(); // <3>

		// Using the Components API:
		form = Components.input.formVertical(SUBJECT).build();
		form = Components.input.formHorizontal(SUBJECT).build();
		form = Components.input.form(SUBJECT).build();
		// end::input31[]
	}

	public void input32() {
		// tag::input32[]
		PropertyInputForm form = PropertyInputForm.verticalLayout(SUBJECT) //
				.composer((content, group) -> { // <1>
					group.getBindings().forEach(binding -> {
						content.add(binding.getElement().getComponent());
					});
				}).build();
		// end::input32[]
	}

	public void input33() {
		// tag::input33[]
		PropertyInputForm.formLayout(SUBJECT) // <1>
				.hidden(ID) // <2>
				.initializer(content -> { // <3>
					/* content configuration */
					FormLayout fl = content;
				}).withPostProcessor((property, component) -> { // <4>
					/* Inputs configuration */
					component.hasStyle().ifPresent(s -> s.addClassName("my-style"));
				}) //
				.withValueChangeListener(event -> { // <5>
					/* handle value change */
					PropertyBox value = event.getValue();
				}) //
				.hidePropertyCaption(ID) // <6>
				.propertyCaption(NAME, "My name", "name.message.code") // <7>
				.build();
		// end::input33[]
	}

	public void input34() {
		// tag::input34[]
		PropertyInputForm.formLayout(SUBJECT) // <1>
				.withValidator(NAME, Validator.max(10)) // <2>
				.withValidator(propertyBox -> { // <3>
					/* group value validation */
				}).validateOnValueChange(true) // <4>
				.build();
		// end::input34[]
	}

	public void input35() {
		// tag::input35[]
		PropertyInputForm form = PropertyInputForm.formLayout(SUBJECT).build();

		form.isValid(); // <1>
		try {
			form.validate(); // <2>
		} catch (ValidationException e) {
			/* value is not valid */
		}

		Optional<PropertyBox> valueIfValid = form.getValueIfValid(); // <3>
		PropertyBox value = form.getValue(); // <4>
		value = form.getValue(false); // <5>
		// end::input35[]
	}

	public void input36() {
		// tag::input36[]
		PropertyInputForm form = PropertyInputForm.formLayout(SUBJECT) //
				.validationStatusHandler(event -> { // <1>
					if (event.isInvalid()) {
						Notification.show("Validation falied: " + event.getErrorMessage());
					}
				}).validationStatusHandler(NAME, event -> { // <2>
					/* omitted */
				}).build();
		// end::input36[]
	}

	public void input37() {
		// tag::input37[]
		PropertyInputForm form = PropertyInputForm.formLayout(SUBJECT) //
				.groupValidationStatusHandler(event -> { // <1>
					event.getGroupStatus(); // <2>
					event.getInputsValidationStatus(); // <3>
					event.getGroupErrorMessages(); // <4>
					event.getInputsValidationStatus().forEach(s -> s.getErrorMessages()); // <5>
				}).build();
		// end::input37[]
	}

	public void input38() {
		// tag::input38[]
		Input<String> input = Input.string().withAdapter(MyExtension.class, i -> new MyExtension()) // <1>
				.build();

		Optional<MyExtension> extension = input.as(MyExtension.class); // <2>
		// end::input38[]
	}

	private class MyExtension {

	}

	private static Datastore getDatastore() {
		return null;
	}

}
