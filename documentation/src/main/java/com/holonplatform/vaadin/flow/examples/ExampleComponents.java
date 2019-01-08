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

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("unused")
public class ExampleComponents {

	public void builders1() {
		// tag::builders1[]
		Div label = Components.label() // <1>
				.text("Label text") // <2>
				.width("200px") // <3>
				.build();

		Button button = Components.button() // <4>
				.text("Button text") // <5>
				.width("200px") // <6>
				.build();
		// end::builders1[]
	}

	public void button() {
		// tag::button[]
		Button button = Components.button() // <1>
				.text("Button text") // <2>
				.text("Default text", "message.code") // <3>
				.description("The description") // <4>
				.fullWidth() // <5>
				.styleName("my-style") // <6>
				.withThemeVariants(ButtonVariant.LUMO_PRIMARY) // <7>
				.icon(VaadinIcon.CHECK) // <8>
				.disableOnClick() // <9>
				.onClick(event -> { // <10>
					// do something
				}).build();

		button = Components.button("Button text", e -> {
			/* do something */ }); // <11>
		// end::button[]
	}

	public void label1() {
		// tag::label1[]
		Div label = Components.label() // <1>
				.text("Label text") // <2>
				.text("Default text", "message.code") // <3>
				.description("The description") // <4>
				.fullWidth() // <5>
				.styleName("my-style") // <6>
				.build();
		// end::label1[]
	}

	public void label2() {
		// tag::label2[]
		Paragraph p = Components.paragraph().text("text").build(); // <1>
		Span span = Components.span().text("text").build(); // <2>
		H1 h1 = Components.h1().text("text").build(); // <3>
		H2 h2 = Components.h2().text("text").build(); // <4>
		H3 h3 = Components.h3().text("text").build(); // <5>
		H4 h4 = Components.h4().text("text").build(); // <6>
		H5 h5 = Components.h5().text("text").build(); // <7>
		H6 h6 = Components.h6().text("text").build(); // <8>
		// end::label2[]
	}

	public void layouts() {
		final Div myComponent1 = Components.label().build();
		final Div myComponent2 = Components.label().build();
		// tag::layouts[]
		VerticalLayout verticalLayout = Components.vl() // <1>
				.spacing() // <2>
				.margin() // <3>
				.alignItems(Alignment.CENTER) // <4>
				.justifyContentMode(JustifyContentMode.END) // <5>
				.fullSize() // <6>
				.styleName("my-style") // <7>
				.add(Components.button().text("Button text").build()) // <8>
				.add(Components.label().text("Label1").build(), Components.label().text("Label2").build()) // <9>
				.addAndAlign(myComponent1, Alignment.START) // <10>
				.build();

		HorizontalLayout horizontalLayout = Components.hl() // <11>
				.add(myComponent1) // <12>
				.add(myComponent2) // <13>
				.flexGrow(1, myComponent1) // <14>
				.build();

		FormLayout formLayout = Components.formLayout() // <15>
				.add(myComponent1) // <16>
				.withFormItem(myComponent1, "Label 1") // <17>
				.responsiveSteps(new ResponsiveStep("100px", 2)) // <18>
				.build();
		// end::layouts[]
	}

	public void ctxmenu() {
		final Div myComponent = Components.label().build();
		final Div itemComponent = Components.label().build();
		// tag::ctxmenu[]
		Components.contextMenu() // <1>
				.withItem("Item 1").onClick(e -> {
					// do something
				}).add() // <2>
				.withItem("Item 2", "message.code").disabled().add() // <3>
				.withItem(itemComponent).add() // <4>
				.withOpenedChangeListener(e -> { // <5>
					e.isOpened();
				}).build(myComponent); // <6>
		// end::ctxmenu[]
	}

	public void configurators() {
		final Div myComponent = Components.label().build();
		// tag::configurators[]
		Button btn = new Button();

		Components.configure(btn).text("Default text", "message.code"); // <1>

		VerticalLayout vl = new VerticalLayout();

		Components.configure(vl).withoutSpacing().add(myComponent); // <2>
		// end::configurators[]
	}

}
