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
package com.holonplatform.vaadin.flow.navigator.test.it;

import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SuppressWarnings("serial")
@Theme(Lumo.class)
@Route("")
@PageTitle("Integration Test")
public class IntegrationTestView extends VerticalLayout implements AfterNavigationObserver {

	static final String VIEW = "it-view";
	static final String LABEL1 = "it-lbl1";
	static final String BUTTON1 = "it-btn1";

	private Div label1;

	public IntegrationTestView() {
		super();

		setId(VIEW);

		add(label1 = Components.label().id(LABEL1).build());

		add(Components.button().id(BUTTON1).text("TEST").withClickListener(e -> {
			label1.setText(e.getSource().getId().orElse(null));
		}).build());
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		// noop
	}

}
