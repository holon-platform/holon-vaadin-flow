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

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValuePresenterRegistry;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Composable;
import com.holonplatform.vaadin.flow.components.PropertyViewForm;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("unused")
public class ExampleViewComponent {

	public void vc1() {
		// tag::vc1[]
		ViewComponent<String> viewComponent = ViewComponent.builder(String.class) // <1>
				.fullWidth() // <2>
				.styleName("my-style") // <3>
				.label("My label") // <4>
				.label("My label", "label.message.code") // <5>
				.description("My description") // <6>
				.onClick(event -> { // <7>
					/* handle the click event */
				}).withValue("Initial value") // <8>
				.withValueChangeListener(event -> { // <9>
					String oldValue = event.getOldValue(); // <10>
					String newValue = event.getValue(); // <11>
				}).build();

		viewComponent = ViewComponent.create(String.class); // <12>

		viewComponent = Components.view.component(String.class) // <13>
				/* configuration omitted */
				.build();
		// end::vc1[]
	}

	public void vc2() {
		// tag::vc2[]
		ViewComponent<String> viewComponent = ViewComponent.create(String.class); // <1>

		new VerticalLayout().add(viewComponent.getComponent()); // <2>

		viewComponent.setValue("My value"); // <3>
		String value = viewComponent.getValue(); // <4>
		value = viewComponent.getValueIfPresent().orElse("Default value"); // <5>

		viewComponent.clear(); // <6>
		boolean empty = viewComponent.isEmpty(); // <7>
		// end::vc2[]
	}

	public void vc3() {
		// tag::vc3[]
		ViewComponent<Integer> viewComponent = ViewComponent.<Integer>builder(value -> String.valueOf(value)).build(); // <1>
		// end::vc3[]
	}

	public void vc4() {
		// tag::vc4[]
		NumericProperty<Integer> MY_PROPERTY = NumericProperty.integerType("my_property"); // <1>

		ViewComponent<Integer> viewComponent = ViewComponent.create(MY_PROPERTY); // <2>
		// end::vc4[]
	}

	public void vc5() {
		final NumericProperty<Integer> MY_PROPERTY = NumericProperty.integerType("my_property");
		// tag::vc5a[]
		PropertyValuePresenterRegistry.getDefault() // <1>
				.forProperty(MY_PROPERTY, (property, value) -> "#" + value); // <2>
		// end::vc5a[]

		// tag::vc5b[]
		ViewComponent<Integer> viewComponent = ViewComponent.create(MY_PROPERTY); // <1>

		String value = MY_PROPERTY.present(1); // <2>
		// end::vc5b[]
	}

	@SuppressWarnings("unchecked")
	public void vc6() {
		// tag::vc6[]
		BooleanProperty PROPERTY = BooleanProperty.create("test") // <1>
				.message("caption").messageCode("caption.message.code"); // <2>

		ViewComponent<Boolean> viewComponent = PROPERTY.render(ViewComponent.class); // <3>
		// end::vc6[]
	}

	// tag::propertydef[]
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	// end::propertydef[]

	public void vc7() {
		// tag::vc7[]
		PropertyViewGroup group = PropertyViewGroup.builder(SUBJECT).build(); // <1>
		group = Components.view.propertyGroup(SUBJECT).build(); // <2>

		Collection<Property<?>> properties = group.getProperties(); // <3>
		Stream<ViewComponent<?>> components = group.getElements(); // <4>
		group.getBindings().forEach(binding -> { // <5>
			Property<?> property = binding.getProperty();
			ViewComponent<?> component = binding.getElement();
		});

		Optional<ViewComponent<?>> element = group.getElement(NAME); // <6>
		ViewComponent<?> component = group.requireElement(NAME); // <7>

		PropertyBox value = PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build();

		group.setValue(value); // <8>
		value = group.getValue(); // <9>

		group.clear(); // <10>
		group.isEmpty(); // <11>
		// end::vc7[]
	}

	public void vc8() {
		// tag::vc8[]
		PropertyViewGroup group = PropertyViewGroup.builder(SUBJECT) // <1>
				.bind(NAME, ViewComponent.create(String.class)) // <2>
				.bind(NAME, property -> ViewComponent.create(String.class)) // <3>
				.build();
		// end::vc8[]
	}

	public void vc9() {
		// tag::vc9[]
		PropertyViewGroup group = PropertyViewGroup.builder(SUBJECT) // <1>
				.hidden(ID) // <2>
				.withPostProcessor((property, component) -> { // <3>
					/* ViewComponent configuration */
					component.hasStyle().ifPresent(s -> s.addClassName("my-style"));
				}) //
				.withValueChangeListener(event -> { // <4>
					/* handle value change */
					PropertyBox value = event.getValue();
				}).build();
		// end::vc9[]
	}

	public void vc10() {
		VerticalLayout myLayout = new VerticalLayout();
		// tag::vc10[]
		PropertyViewForm form = PropertyViewForm.builder(new VerticalLayout(), SUBJECT) // <1>
				.composer(Composable.componentContainerComposer()) // <2>
				.build();

		myLayout.add(form.getComponent()); // <3>

		form.setValue(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build()); // <4>
		// end::vc10[]
	}

	public void vc11() {
		// tag::vc11[]
		PropertyViewForm form = PropertyViewForm.verticalLayout(SUBJECT).build(); // <1>
		form = PropertyViewForm.horizontalLayout(SUBJECT).build(); // <2>
		form = PropertyViewForm.formLayout(SUBJECT).build(); // <3>

		// Using the Components API:
		form = Components.view.formVertical(SUBJECT).build();
		form = Components.view.formHorizontal(SUBJECT).build();
		form = Components.view.form(SUBJECT).build();
		// end::vc11[]
	}

	public void vc12() {
		// tag::vc12[]
		PropertyViewForm form = PropertyViewForm.verticalLayout(SUBJECT) //
				.composer((content, group) -> { // <1>
					group.getBindings().forEach(binding -> {
						content.add(binding.getElement().getComponent());
					});
				}).build();
		// end::vc12[]
	}

	public void vc13() {
		// tag::vc13[]
		PropertyViewForm.formLayout(SUBJECT) // <1>
				.hidden(ID) // <2>
				.initializer(content -> { // <3>
					/* content configuration */
					FormLayout fl = content;
				}).withPostProcessor((property, component) -> { // <4>
					/* ViewComponent configuration */
					component.hasStyle().ifPresent(s -> s.addClassName("my-style"));
				}) //
				.withValueChangeListener(event -> { // <5>
					/* handle value change */
					PropertyBox value = event.getValue();
				}) //
				.hidePropertyCaption(ID) // <6>
				.propertyCaption(NAME, "My name", "name.message.code") // <7>
				.build();
		// end::vc13[]
	}

}
