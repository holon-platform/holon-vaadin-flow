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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;

@SuppressWarnings("unused")
public class ExampleDialog {

	public void dialog1() {
		// tag::dialog1[]
		Dialog dialog = Components.dialog.message().text("Message").build(); // <1>

		Components.dialog.message().text("Default text", "message.code").open(); // <2>

		Components.dialog.showMessage("Default text", "message.code"); // <3>
		// end::dialog1[]
	}

	public void dialog2() {
		// tag::dialog2[]
		Components.dialog.confirm() // <1>
				.text("Default text", "message.code") // <2>
				.okButtonConfigurator(button -> { // <3>
					button.text("My text").icon(VaadinIcon.CHECK);
				}).open(); // <4>

		Components.dialog.showConfirm("Default text", "message.code"); // <5>
		// end::dialog2[]
	}

	public void dialog3() {
		// tag::dialog3[]
		Components.dialog.question(confirm -> { // <1>
			// handle user response (true/false)
		}).text("Default text", "message.code") // <2>
				.confirmButtonConfigurator(button -> { // <3>
					// confirm button configuration
				}).denialButtonConfigurator(button -> { // <4>
					// deny button configuration
				}).open(); // <5>

		Components.dialog.showQuestion(confirm -> {
			/* handle user response */ }, "Default text", "message.code"); // <6>
		// end::dialog3[]
	}

	public void dialog4() {
		// tag::dialog4[]
		Components.dialog.message().text("Default text", "message.code") //
				.width("200px") // <1>
				.height("200px") // <2>
				.styleName("my-style") // <3>
				.closeOnEsc(false) // <4>
				.closeOnOutsideClick(true) // <5>
				.withOpenedChangeListener(event -> { // <6>
					// open changed
				}).build();
		// end::dialog4[]
	}

	public void dialog5() {
		// tag::dialog5[]
		Components.dialog.message().text("Default text", "message.code") //
				.withComponent(Components.label().text("My label").build()) // <1>
				.withToolbarComponent(Components.button().text("My button").build()) // <2>
				.open();
		// end::dialog5[]
	}

}
