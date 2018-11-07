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
package com.holonplatform.vaadin.flow.internal.components.support;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultClickEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * {@link ClickEventListener} adapter for {@link Component} type click events.
 * 
 * @param <C> Component type
 *
 * @since 5.2.0
 */
public class ComponentClickListenerAdapter<C extends Component> implements ComponentEventListener<ClickEvent<C>> {

	private static final long serialVersionUID = 2832892710052433615L;

	private final ClickEventListener<C, com.holonplatform.vaadin.flow.components.events.ClickEvent<C>> clickListener;

	/**
	 * Constructor.
	 * @param clickListener Wrapped click listener (not null)
	 */
	public ComponentClickListenerAdapter(
			ClickEventListener<C, com.holonplatform.vaadin.flow.components.events.ClickEvent<C>> clickListener) {
		super();
		ObjectUtils.argumentNotNull(clickListener, "ClickEventListener must be not null");
		this.clickListener = clickListener;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.ComponentEventListener#onComponentEvent(com.vaadin.flow.component.ComponentEvent)
	 */
	@Override
	public void onComponentEvent(ClickEvent<C> event) {
		this.clickListener.onClickEvent(convert(event));
	}

	/**
	 * Convert a component click event into a ClickEvent.
	 * @param <C> Event source component type
	 * @param componentEvent Event to convert
	 * @return Converter event
	 */
	private static <C extends Component> com.holonplatform.vaadin.flow.components.events.ClickEvent<C> convert(
			ClickEvent<C> componentEvent) {
		final DefaultClickEvent<C> event = new DefaultClickEvent<>(componentEvent.getSource(),
				componentEvent.isFromClient());
		return configure(event, componentEvent);
	}

	/**
	 * Configure given {@link DefaultClickEvent} using given <code>componentEvent</code> attributes.
	 * @param <S> Event source type
	 * @param event Event to configure
	 * @param componentEvent Component event
	 * @return Configured event
	 */
	public static <S> DefaultClickEvent<S> configure(DefaultClickEvent<S> event, ClickEvent<?> componentEvent) {
		event.setClientX(componentEvent.getClientX());
		event.setClientY(componentEvent.getClientY());
		event.setScreenX(componentEvent.getScreenX());
		event.setScreenY(componentEvent.getScreenY());
		event.setClickCount(componentEvent.getClickCount());
		event.setButton(componentEvent.getButton());
		event.setAltKey(componentEvent.isAltKey());
		event.setCtrlKey(componentEvent.isCtrlKey());
		event.setShiftKey(componentEvent.isShiftKey());
		event.setMetaKey(componentEvent.isMetaKey());
		return event;
	}

	/**
	 * Get a click event {@link ComponentEventListener} which wraps given <code>clickListener</code>.
	 * @param <S> Click event source type
	 * @param <C> Event source component type
	 * @param source Click event source (not null)
	 * @param clickListener Click event listener (not null)
	 * @return A {@link ComponentEventListener} which wraps given click event listener
	 */
	@SuppressWarnings("serial")
	public static <S, C extends Component> ComponentEventListener<ClickEvent<C>> forClickNotifier(S source,
			final ClickEventListener<S, com.holonplatform.vaadin.flow.components.events.ClickEvent<S>> clickListener) {
		ObjectUtils.argumentNotNull(clickListener, "ClickEventListener must be not null");
		return new ComponentEventListener<ClickEvent<C>>() {

			@Override
			public void onComponentEvent(ClickEvent<C> event) {
				clickListener.onClickEvent(configure(new DefaultClickEvent<>(source, event.isFromClient()), event));
			}
		};
	}

}
