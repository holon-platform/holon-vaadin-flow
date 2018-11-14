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
package com.holonplatform.vaadin.flow.internal.components;

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Default {@link Dialog} to be used with dialog builders.
 *
 * @since 5.2.0
 */
public class DefaultDialog extends Dialog implements HasStyle {

	private static final long serialVersionUID = 5017183187214693820L;

	private final VerticalLayout content;
	private final Div message;
	private final HorizontalLayout toolbar;

	/**
	 * Constructor.
	 */
	public DefaultDialog() {
		super();

		// default CSS style class name
		getElement().getClassList().add("h-dialog");

		this.message = new Div();
		this.message.addClassName("message");
		this.toolbar = new HorizontalLayout();
		this.toolbar.addClassName("toolbar");

		this.toolbar.setAlignItems(Alignment.END);

		this.content = new VerticalLayout();
		this.content.setPadding(false);
		this.content.setSpacing(true);
		this.content.add(message);
		this.content.add(this.toolbar);
		this.content.setAlignSelf(Alignment.CENTER, this.message);
		this.content.setAlignSelf(Alignment.END, this.toolbar);

		this.message.setVisible(false);
		this.toolbar.setVisible(false);
		
		add(content);
	}

	/**
	 * Get the dialog message text.
	 * @return Optional message text
	 */
	public Optional<String> getMessage() {
		String text = message.getText();
		if (text != null && !text.trim().equals("")) {
			return Optional.of(text);
		}
		return Optional.empty();
	}

	/**
	 * Set the dialog message text.
	 * @param text the dialog message text, if <code>null</code> or blank the dialog message is removed
	 */
	public void setMessage(String text) {
		message.setText((text != null && !text.trim().equals("")) ? text : "");
		message.setVisible(text != null && !text.trim().equals(""));
	}

	/**
	 * Add a component to dialog content.
	 * @param component The component to add (not null)
	 */
	public void addContentComponent(Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		this.content.addComponentAtIndex(this.content.indexOf(this.message) + 1, component);
		this.content.setAlignSelf(Alignment.CENTER, component);
	}

	/**
	 * Get the dialog content components, excluding the message and toolbar components.
	 * @return the dialog content components
	 */
	public Stream<Component> getContentComponents() {
		return this.content.getChildren().filter(c -> c != this.message).filter(c -> c != this.toolbar);
	}

	/**
	 * Add a component to the dialog toolbar.
	 * @param component The component to add (not null)
	 */
	public void addToolbarComponent(Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		this.toolbar.addComponentAsFirst(component);
		this.toolbar.setVisible(true);
	}

	/**
	 * Get the dialog content components, excluding the message and toolbar components.
	 * @return the dialog content components
	 */
	public Stream<Component> getToolbarComponents() {
		return this.toolbar.getChildren();
	}

}
