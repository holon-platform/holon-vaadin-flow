/*
 * Copyright 2016-2019 Axioma srl.
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasLabel;
import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultValueChangeEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;

/**
 * Base {@link ViewComponent} implementation.
 *
 * @param <C> Content type
 * @param <T> Value type
 *
 * @since 5.2.2
 */
public abstract class AbstractViewComponent<C extends Component, T> extends Composite<Div>
		implements ViewComponent<T>, HasSize, HasStyle, HasEnabled, ClickNotifier<Component> {

	private static final long serialVersionUID = -1079320883460226029L;

	private final Label label;

	private C content;

	private T value;

	private final List<ValueChangeListener<T, ValueChangeEvent<T>>> valueChangeListeners = new LinkedList<>();

	/**
	 * Constructor.
	 * @param content Optional initial content component
	 */
	public AbstractViewComponent(C content) {
		super();
		this.content = content;
		this.label = new Label();
		this.label.addClassName("caption");

		getContent().addClassName("h-viewcomponent");
		getContent().add(label);

		if (content != null) {
			getContent().add(content);
		}
	}

	/**
	 * Update the value of the component,
	 * @param value The value to update
	 * @return The content component to use to replace the current content, if any
	 */
	protected abstract Optional<C> updateValue(T value);

	/**
	 * Get the component {@link Label} element.
	 * @return the label element
	 */
	protected Label getLabel() {
		return label;
	}

	/**
	 * Get the content component.
	 * @return Optional content component
	 */
	protected Optional<C> getInternalContent() {
		return Optional.ofNullable(content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ViewComponent#getContentComponent()
	 */
	@Override
	public Optional<? extends Component> getContentComponent() {
		return getInternalContent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.HasComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Sets the component value.
	 * @param value the value to set
	 * @param fireEvent Whether to fire a value change event
	 */
	public void setValue(T value, boolean fireEvent) {
		T oldValue = this.value;
		this.value = value;
		// update internal component
		final Optional<C> updated = updateValue(value);
		if (updated.isPresent()) {
			final C newContent = updated.get();
			if (content == null || content != newContent) {
				if (content != null) {
					getContent().remove(content);
				}
				content = newContent;
				getContent().add(content);
			}
		} else {
			if (content != null) {
				getContent().remove(content);
			}
		}
		if (fireEvent) {
			// fire value change
			fireValueChange(oldValue, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		setValue(value, true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.ValueHolder#addValueChangeListener(com.holonplatform.vaadin.flow.
	 * components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public Registration addValueChangeListener(ValueChangeListener<T, ValueChangeEvent<T>> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		valueChangeListeners.add(listener);
		return () -> valueChangeListeners.remove(listener);
	}

	/**
	 * Emits the value change event
	 * @param oldValue the old value
	 * @param value the changed value
	 */
	protected void fireValueChange(T oldValue, T value) {
		final ValueChangeEvent<T> valueChangeEvent = new DefaultValueChangeEvent<>(this, oldValue, value, false);
		valueChangeListeners.forEach(l -> l.valueChange(valueChangeEvent));
	}

	/**
	 * Set the component label text.
	 * @param label The label text to set
	 */
	public void setLabelText(String label) {
		getLabel().setText(label);
	}

	/**
	 * Set whether the label is visible.
	 * @param visible whether the label is visible
	 */
	public void setLabelVisible(boolean visible) {
		getLabel().setVisible(visible);
	}

	/**
	 * Get whether the label is visible.
	 * @return whether the label is visible
	 */
	public boolean isLabelVisible() {
		return getLabel().isVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveLabel#hasLabel()
	 */
	@Override
	public Optional<HasLabel> hasLabel() {
		return Optional.of(HasLabel.create(() -> getLabel().getText(), label -> getLabel().setText(label)));
	}

	/**
	 * Set the component title text.
	 * @param title The title text to set
	 */
	public void setTitle(String title) {
		getElement().setAttribute("title", (title != null) ? title : "");
	}

	/**
	 * Get the component title text.
	 * @return the component title
	 */
	public String getTitle() {
		return getElement().getAttribute("title");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.MayHaveTitle#hasTitle()
	 */
	@Override
	public Optional<HasTitle> hasTitle() {
		return Optional.of(HasTitle.create(() -> getTitle(), title -> setTitle(title)));
	}

}
