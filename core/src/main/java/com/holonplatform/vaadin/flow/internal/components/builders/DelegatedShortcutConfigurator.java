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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.KeyModifier;

/**
 * A {@link ShortcutConfigurator} which delegates actual configuration to another one.
 * 
 * @param <P> Parent configurator
 * 
 * @since 5.2.3
 */
public class DelegatedShortcutConfigurator<P> implements ShortcutConfigurator<P> {

	private final ShortcutConfigurator<?> delegate;
	private final P parent;

	/**
	 * Constructor.
	 * @param delegate Delegate configurator (not null)
	 * @param parent Parent configurator (not null)
	 */
	public DelegatedShortcutConfigurator(ShortcutConfigurator<?> delegate, P parent) {
		super();
		ObjectUtils.argumentNotNull(delegate, "Delegate configurator must be not null");
		ObjectUtils.argumentNotNull(parent, "Parent configurator must be not null");
		this.delegate = delegate;
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#modifiers(com.vaadin.flow.component.
	 * KeyModifier[])
	 */
	@Override
	public ShortcutConfigurator<P> modifiers(KeyModifier... keyModifiers) {
		delegate.modifiers(keyModifiers);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#withAlt()
	 */
	@Override
	public ShortcutConfigurator<P> withAlt() {
		delegate.withAlt();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#withCtrl()
	 */
	@Override
	public ShortcutConfigurator<P> withCtrl() {
		delegate.withCtrl();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#withShift()
	 */
	@Override
	public ShortcutConfigurator<P> withShift() {
		delegate.withShift();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#withMeta()
	 */
	@Override
	public ShortcutConfigurator<P> withMeta() {
		delegate.withMeta();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#allowBrowserDefault()
	 */
	@Override
	public ShortcutConfigurator<P> allowBrowserDefault() {
		delegate.allowBrowserDefault();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#allowEventPropagation()
	 */
	@Override
	public ShortcutConfigurator<P> allowEventPropagation() {
		delegate.allowEventPropagation();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#bindLifecycleTo(com.vaadin.flow.component.
	 * Component)
	 */
	@Override
	public ShortcutConfigurator<P> bindLifecycleTo(Component component) {
		delegate.bindLifecycleTo(component);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#listenOn(com.vaadin.flow.component.
	 * Component)
	 */
	@Override
	public ShortcutConfigurator<P> listenOn(Component listenOnComponent) {
		delegate.listenOn(listenOnComponent);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator#add()
	 */
	@Override
	public P add() {
		return parent;
	}

}
