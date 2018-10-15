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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultLabelBuilder;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;

/**
 * Builder to create <em>label</em> components, i.e. components to display a text.
 * 
 * @param <L> Concrete label component type
 * 
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public interface LabelBuilder<L extends HtmlContainer & ClickNotifier>
		extends LabelConfigurator<L, LabelBuilder<L>>, ComponentBuilder<L, LabelBuilder<L>> {

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Span} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Span> span() {
		return new DefaultLabelBuilder<>(new Span());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Div} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Div> div() {
		return new DefaultLabelBuilder<>(new Div());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Paragraph} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Paragraph> paragraph() {
		return new DefaultLabelBuilder<>(new Paragraph());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H1} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H1> h1() {
		return new DefaultLabelBuilder<>(new H1());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H2} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H2> h2() {
		return new DefaultLabelBuilder<>(new H2());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H3} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H3> h3() {
		return new DefaultLabelBuilder<>(new H3());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H4} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H4> h4() {
		return new DefaultLabelBuilder<>(new H4());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H5} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H5> h5() {
		return new DefaultLabelBuilder<>(new H5());
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H6} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H6> h6() {
		return new DefaultLabelBuilder<>(new H6());
	}

}
