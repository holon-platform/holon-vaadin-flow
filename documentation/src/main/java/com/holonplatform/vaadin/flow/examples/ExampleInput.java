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
import java.util.Date;
import java.util.Optional;

import com.holonplatform.core.Registration;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

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

}
