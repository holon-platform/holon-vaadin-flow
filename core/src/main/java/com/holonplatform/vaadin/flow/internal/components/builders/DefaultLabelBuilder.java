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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.HtmlContainer;

/**
 * Default {@link LabelBuilder} implementation.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public class DefaultLabelBuilder<L extends HtmlContainer & ClickNotifier>
		extends AbstractLabelConfigurator<L, LabelBuilder<L>> implements LabelBuilder<L> {

	public DefaultLabelBuilder(L component) {
		super(component);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected LabelBuilder<L> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ComponentBuilder#build()
	 */
	@Override
	public L build() {
		return getComponent();
	}

}
